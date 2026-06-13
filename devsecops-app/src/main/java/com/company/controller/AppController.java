package com.company.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AppController {

    @GetMapping("/")
    public Map<String, Object> home() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("application", "Enterprise Ecommerce Platform");
        response.put("status", "UP");
        response.put("environment", "Production");
        response.put("version", "v2.1.0");
        response.put("deployment", "AWS EKS Kubernetes Cluster");
        response.put("ci_cd_pipeline", "Jenkins + Docker + SonarQube + EKS");
        response.put("domain", "Ecommerce");
        response.put("timestamp", LocalDateTime.now());

        Map<String, Object> services = new LinkedHashMap<>();

        services.put("user-service", "Running");
        services.put("product-service", "Running");
        services.put("payment-service", "Running");
        services.put("order-service", "Running");
        services.put("inventory-service", "Running");
        services.put("notification-service", "Running");

        response.put("microservices", services);

        Map<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("activeUsers", 1285);
        metrics.put("ordersToday", 542);
        metrics.put("paymentsProcessed", 531);
        metrics.put("podsRunning", 4);

        response.put("platformMetrics", metrics);

        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {

        Map<String, String> health = new LinkedHashMap<>();

        health.put("status", "Healthy");
        health.put("kubernetes", "Connected");
        health.put("database", "Connected");
        health.put("message", "Application running successfully on EKS");

        return health;
    }

    @GetMapping("/orders")
    public Map<String, Object> orders() {

        Map<String, Object> orders = new LinkedHashMap<>();

        orders.put("totalOrders", 24561);
        orders.put("pendingOrders", 125);
        orders.put("deliveredOrders", 23890);
        orders.put("returnedOrders", 546);

        return orders;
    }

    @GetMapping("/products")
    public Map<String, Object> products() {

        Map<String, Object> products = new LinkedHashMap<>();

        products.put("category", "Electronics");
        products.put("topSellingProduct", "Apple iPhone 16 Pro");
        products.put("availableStock", 785);
        products.put("warehouse", "Mumbai DC");

        return products;
    }
}
