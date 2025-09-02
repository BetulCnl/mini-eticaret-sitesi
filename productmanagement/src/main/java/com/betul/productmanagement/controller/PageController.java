package com.betul.productmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class PageController {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // templates/login.html dosyasını döndürür
    }
    @GetMapping("/products")
    public String productsPage() {
        return "products"; // templates/products.html
    }
    @GetMapping("/cart")
    public String cartPage() {
        return "cart";
    }
    @GetMapping("/purchase")
    public String purchasePage(@RequestParam(name = "orderId", required = false) Long orderId, Model model) {
        if (orderId != null) {
            model.addAttribute("orderId", orderId);
        }
        model.addAttribute("apiBaseUrl", apiBaseUrl);
        return "purchase";
    }
    @GetMapping("/myOrders")
    public String myOrdersPage() {
        return "myOrders";
    }
}
