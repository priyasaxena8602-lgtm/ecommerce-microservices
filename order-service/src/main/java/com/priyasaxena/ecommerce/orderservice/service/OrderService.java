package com.priyasaxena.ecommerce.orderservice.service;

import com.priyasaxena.ecommerce.orderservice.exception.OrderNotFoundException;
import com.priyasaxena.ecommerce.orderservice.model.Order;
import com.priyasaxena.ecommerce.orderservice.model.OrderStatus;
import com.priyasaxena.ecommerce.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
    }

    public Order createOrder(Order order) {
        order.setOrderId(null) ;
        return orderRepository.save(order);
    }

    public List<Order> getOrderByCustomer(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }

    @Transactional
    public Order updateOrder(Long orderId, Order order) {
        Order existingOrder =  orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        existingOrder.setOrderNumber(order.getOrderNumber());
        existingOrder.setOrderStatus(order.getOrderStatus());
        existingOrder.setCustomerId(order.getCustomerId());

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order existingOrder =  orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        existingOrder.setOrderStatus(orderStatus);
        return orderRepository.save(existingOrder);

    }

    public void cancelOrder(Long orderId) {
        Order existingOrder =  orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        orderRepository.delete(existingOrder);
    }
}
