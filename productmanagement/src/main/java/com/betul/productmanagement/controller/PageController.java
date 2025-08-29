package com.betul.productmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

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
    public String purchasePage() {
        return "purchase";
    }
    @GetMapping("/myOrders")
    public String myOrdersPage() {
        return "myOrders";
    }
}
