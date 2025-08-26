package com.betul.productmanagement.service;

import com.betul.productmanagement.client.PaymentServiceClient;
import com.betul.productmanagement.dto.OrderDTO;
import com.betul.productmanagement.dto.PaymentRequestDTO;
import com.betul.productmanagement.dto.PaymentResponseDTO;
import com.betul.productmanagement.dto.RefundRequestDTO;
import com.betul.productmanagement.dto.RefundResponseDTO;
import com.betul.productmanagement.entity.*;
import com.betul.productmanagement.enums.OrderStatus;
import com.betul.productmanagement.enums.PaymentStatus;
import com.betul.productmanagement.enums.PaymentType;
import com.betul.productmanagement.Mapper.OrderMapper;
import com.betul.productmanagement.repository.CartItemRepository;
import com.betul.productmanagement.repository.OrderRepository;
import com.betul.productmanagement.repository.PaymentTransactionRepository;
import com.betul.productmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public OrderDTO checkout(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        order.setOrderStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getStock()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            try {
                productRepository.save(product);
            } catch (OptimisticLockingFailureException e) {
                throw new RuntimeException("Stock update failed, please try again");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderDTO(savedOrder);
    }

    @Transactional
    public OrderDTO completeOrder(Long orderId, User user) {
        return processOrderPayment(orderId, user, PaymentStatus.SUCCESS);
    }

    @Transactional
    public OrderDTO failOrder(Long orderId, User user) {
        return processOrderPayment(orderId, user, PaymentStatus.FAILED);
    }

    private OrderDTO processOrderPayment(Long orderId, User user, PaymentStatus intendedStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User is not authorized for this order");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be processed.");
        }

        PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .paymentType(PaymentType.PURCHASE)
                .paymentStatus(intendedStatus)
                .build();

        PaymentTransaction transaction = paymentTransactionService.initiatePayment(paymentRequest);

        try {
            PaymentResponseDTO paymentResponse = paymentServiceClient.createPayment(paymentRequest);
            transaction.setStatus(paymentResponse.getStatus());
            paymentTransactionRepository.save(transaction);

            if (paymentResponse.getStatus() == PaymentStatus.SUCCESS) {
                order.setOrderStatus(OrderStatus.COMPLETED);
                cartItemRepository.deleteByUser(user);
            } else {
                order.setOrderStatus(OrderStatus.FAILED);
                revertStock(order);
            }
        } catch (Exception e) {
            transaction.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(transaction);
            order.setOrderStatus(OrderStatus.FAILED);
            revertStock(order);
            return OrderMapper.toOrderDTO(order);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderDTO(savedOrder);
    }

    private void revertStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            });
        }
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu siparişi iptal etme yetkiniz yok");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.FAILED) {
            throw new RuntimeException("Bu sipariş iptal edilemez");
        }

        // Refund isteği oluştur
        RefundRequestDTO refundReq = new RefundRequestDTO(order.getId(), order.getTotalAmount());
        RefundResponseDTO refundRes;

        try {
            refundRes = paymentServiceClient.requestRefund(refundReq);
        } catch (Exception e) {
            // Refund servisine bağlanamazsa FAILED_CLAIM kaydı (ayrı transaction)
            saveTransactionInNewTx(order.getId(), order.getTotalAmount(), PaymentStatus.FAILED_CLAIM);
            throw new RuntimeException("Refund servisine bağlanırken hata oluştu: " + e.getMessage());
        }

        // Transaction tablosuna refund sonucunu kaydet (ayrı transaction)
        PaymentStatus txStatus = "SUCCESS_CLAIM".equals(refundRes.getClaimStatus()) ?
                PaymentStatus.SUCCESS_CLAIM : PaymentStatus.FAILED_CLAIM;
        saveTransactionInNewTx(order.getId(), order.getTotalAmount(), txStatus);

        // Refund başarısızsa exception fırlat
        if (txStatus == PaymentStatus.FAILED_CLAIM) {
            throw new RuntimeException("İptal işlemi gerçekleştirilemedi: " + refundRes.getMessage());
        }

        // Refund başarılıysa order iptal et ve stokları geri ekle
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            });
        }

        return OrderMapper.toOrderDTO(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTransactionInNewTx(Long orderId, BigDecimal amount, PaymentStatus status) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderId(orderId);
        tx.setAmount(amount);
        tx.setStatus(status);
        tx.setCreatedAt(Instant.now());
        paymentTransactionRepository.save(tx);
    }

    public Page<OrderDTO> listUserOrders(User user, int page, int size) {
        if (user == null) {
            logger.warn("User object is null in listUserOrders. Cannot fetch orders.");
            return Page.empty();
        }
        logger.info("Fetching orders for User ID: {}", user.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByUser(user, pageable);
        logger.info("Found {} orders for User ID: {}", orderPage.getTotalElements(), user.getId());
        List<OrderDTO> dtoList = orderPage.getContent().stream()
                .map(OrderMapper::toOrderDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, orderPage.getTotalElements());
    }

    public Page<OrderDTO> listAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDTO> dtoList = orderPage.getContent().stream()
                .map(OrderMapper::toOrderDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, orderPage.getTotalElements());
    }

    public OrderDTO getOrder(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toOrderDTO)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
