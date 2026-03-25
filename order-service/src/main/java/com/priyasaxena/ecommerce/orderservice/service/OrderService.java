package com.priyasaxena.ecommerce.orderservice.service;

import com.priyasaxena.ecommerce.orderservice.dto.OrderItemRequest;
import com.priyasaxena.ecommerce.orderservice.dto.OrderItemResponse;
import com.priyasaxena.ecommerce.orderservice.dto.OrderRequest;
import com.priyasaxena.ecommerce.orderservice.dto.OrderResponse;
import com.priyasaxena.ecommerce.orderservice.exception.OrderNotFoundException;
import com.priyasaxena.ecommerce.orderservice.exception.ProductServiceException;
import com.priyasaxena.ecommerce.orderservice.model.Order;
import com.priyasaxena.ecommerce.orderservice.model.OrderItem;
import com.priyasaxena.ecommerce.orderservice.model.OrderStatus;
import com.priyasaxena.ecommerce.orderservice.repository.OrderRepository;
import com.priyasaxena.ecommerce.orderservice.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        return mapToResponse(order);
    }
    public OrderResponse createOrder(OrderRequest request) {
        Order order = mapToOrder(request);

        // For each OrderItemRequest, fetch product details from product-service
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(item -> mapToOrderItem(item, order))
                .toList();

        order.setOrderItems(orderItems);
        return mapToResponse(orderRepository.save(order));
    }
    private OrderItem mapToOrderItem(OrderItemRequest item, Order order) {
        ProductResponse product = getProductDetails(item.getProductId());

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getProductId());
        orderItem.setProductName(product.getProductName());
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setOrder(order);
        return orderItem;
    }

    // calling product-service to fetch product details by ID
    private ProductResponse getProductDetails(Long productId) {
        try {
            return restTemplate.getForObject(
                    productServiceUrl + "/" + productId,
                    ProductResponse.class
            );

        }catch(HttpClientErrorException.NotFound e){
            throw new ProductServiceException("Product " + productId + " not found!");
        }catch (HttpClientErrorException e) {
            throw new ProductServiceException("Error while fetching product " + productId + ": " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ProductServiceException("Product service is unavailable. Please try again later.");
        } catch (ResourceAccessException e) {
            throw new ProductServiceException("Cannot connect to product service. Please try again later.");
        }
    }

    public List<OrderResponse> getOrderByCustomer(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        existingOrder.setOrderNumber(request.getOrderNumber());
        existingOrder.setOrderStatus(request.getOrderStatus());
        existingOrder.setCustomerId(request.getCustomerId());
        return mapToResponse(orderRepository.save(existingOrder));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        existingOrder.setOrderStatus(orderStatus);
        return mapToResponse(orderRepository.save(existingOrder));
    }

    public void cancelOrder(Long orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found!"));
        orderRepository.delete(existingOrder);
    }
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        Double totalAmount = orderItemResponses.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                orderItemResponses,
                totalAmount
        );
    }
    private Order mapToOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setCustomerId(request.getCustomerId());
        order.setOrderStatus(request.getOrderStatus());
        return order;
    }
}
