package com.priyasaxena.ecommerce.orderservice.dto;

import com.priyasaxena.ecommerce.orderservice.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequest {
    @NotBlank
    private String orderNumber;
    @NotNull
    private Long customerId;
    @NotNull
    private OrderStatus orderStatus;
    @NotEmpty
    @Valid
    private List<OrderItemRequest> orderItems;

    public OrderRequest(){
        super();
    }

    public OrderRequest(String orderNumber, Long customerId, OrderStatus orderStatus, List<OrderItemRequest> orderItems) {
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }
}
