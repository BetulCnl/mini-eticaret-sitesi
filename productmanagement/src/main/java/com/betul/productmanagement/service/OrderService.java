package com.betul.productmanagement.service;

import com.betul.productmanagement.client.PaymentServiceClient;
import com.betul.productmanagement.dto.OrderDTO;
import com.betul.productmanagement.dto.PaymentRequestDTO;
import com.betul.productmanagement.dto.PaymentResponseDTO;
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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

        // 1. Create PENDING transaction

        // 2. Prepare request for payment service
        PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .paymentType(PaymentType.PURCHASE)
                .paymentStatus(intendedStatus) // Pass intended status
                .build();

        PaymentTransaction transaction = paymentTransactionService.initiatePayment(paymentRequest);

        try {
            PaymentResponseDTO paymentResponse = paymentServiceClient.createPayment(paymentRequest);

            // 3. Update our transaction with the final status from payment service
            transaction.setStatus(paymentResponse.getStatus());
            paymentTransactionRepository.save(transaction);

            // 4. Update order based on final status
            if (paymentResponse.getStatus() == PaymentStatus.SUCCESS) {
                order.setOrderStatus(OrderStatus.COMPLETED);
                cartItemRepository.deleteByUser(user); // Clear cart on success
            } else {
                order.setOrderStatus(OrderStatus.FAILED);
                revertStock(order);
            }
        } catch (Exception e) {
            // If payment service call fails, mark everything as FAILED
            transaction.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(transaction);
            order.setOrderStatus(OrderStatus.FAILED);
            revertStock(order);
            return OrderMapper.toOrderDTO(order);
            //throw new RuntimeException("Payment service is currently unavailable. Please try again later.", e);
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

    public Page<OrderDTO> listUserOrders(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByUser(user, pageable);
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
