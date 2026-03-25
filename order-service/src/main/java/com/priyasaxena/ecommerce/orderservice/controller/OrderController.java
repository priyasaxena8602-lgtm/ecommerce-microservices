package com.priyasaxena.ecommerce.orderservice.controller;

import com.priyasaxena.ecommerce.orderservice.model.Order;
import com.priyasaxena.ecommerce.orderservice.model.OrderStatus;
import com.priyasaxena.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order){
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrderByCustomer(@PathVariable Long customerId){
        return ResponseEntity.ok(orderService.getOrderByCustomer(customerId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @Valid @RequestBody Order order){
        return ResponseEntity.ok(orderService.updateOrder(orderId, order));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus orderStatus){
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, orderStatus));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Object> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
