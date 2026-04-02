package com.priyasaxena.ecommerce.orderservice.service;

import com.priyasaxena.ecommerce.orderservice.dto.OrderItemRequest;
import com.priyasaxena.ecommerce.orderservice.dto.OrderRequest;
import com.priyasaxena.ecommerce.orderservice.dto.OrderResponse;
import com.priyasaxena.ecommerce.orderservice.dto.ProductResponse;
import com.priyasaxena.ecommerce.orderservice.model.Order;
import com.priyasaxena.ecommerce.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.priyasaxena.ecommerce.orderservice.exception.OrderNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.priyasaxena.ecommerce.orderservice.model.OrderStatus.CONFIRMED;
import static com.priyasaxena.ecommerce.orderservice.model.OrderStatus.SHIPPED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldThrowExceptionWhenOrderNotFound(){
        //Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        //Act and Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void shouldReturnOrderWhenOrderExists(){
        //Arrange
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderNumber("ORD-001");
        order.setCustomerId(101L);
        order.setOrderStatus(SHIPPED);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //Act
        OrderResponse response = orderService.getOrderById(1L);

        //Assert
        assertNotNull(response);
        assertEquals("ORD-001", response.getOrderNumber());
        assertEquals(101L, response.getCustomerId());
    }

    @Test
    void shouldReturnAllOrders() {
        //Arrange
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setOrderNumber("ORD-001");
        order1.setCustomerId(101L);
        order1.setOrderStatus(SHIPPED);
        order1.setOrderItems(new ArrayList<>());

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setOrderNumber("ORD-002");
        order2.setCustomerId(102L);
        order2.setOrderStatus(SHIPPED);
        order2.setOrderItems(new ArrayList<>());

        when(orderRepository.findAll()).thenReturn(List.of(order1,order2));

        //Act
        List<OrderResponse> orders = orderService.getAllOrders();

        //Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals("ORD-001", orders.get(0).getOrderNumber());
        assertEquals("ORD-002", orders.get(1).getOrderNumber());

    }

    @Test
    void shouldCancelOrder(){
        //Arrange
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderNumber("ORD-001");
        order.setCustomerId(101L);
        order.setOrderStatus(SHIPPED);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //Act
        orderService.cancelOrder(1L);

        //Assert
        verify(orderRepository, times(1)).delete(order);


    }

    @Test
    void shouldUpdateOrderStatus(){
        //Arrange
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderNumber("ORD-001");
        order.setCustomerId(101L);
        order.setOrderStatus(SHIPPED);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        //Act
        OrderResponse response = orderService.updateOrderStatus(1L, SHIPPED);

        //Assert
        assertNotNull(response);
        assertEquals(SHIPPED, response.getOrderStatus());
    }

    @Test
    void shouldReturnOrderByCustomerId(){
        //Arrange
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setOrderNumber("ORD-001");
        order1.setCustomerId(101L);
        order1.setOrderStatus(SHIPPED);
        order1.setOrderItems(new ArrayList<>());

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setOrderNumber("ORD-002");
        order2.setCustomerId(101L);
        order2.setOrderStatus(SHIPPED);
        order2.setOrderItems(new ArrayList<>());

        when(orderRepository.findAllByCustomerId(101L)).thenReturn(List.of(order1, order2));

        //Act
        List<OrderResponse> orders = orderService.getOrderByCustomer(101L);

        //Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    void shouldUpdateOrder(){
        //Arrange
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderNumber("ORD-001");
        order.setCustomerId(101L);
        order.setOrderStatus(SHIPPED);
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        //Act
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(102L);
        orderRequest.setOrderStatus(CONFIRMED);
        orderRequest.setOrderNumber("ORD-111");
        orderRequest.setOrderItems(new ArrayList<>());

        OrderResponse response = orderService.updateOrder(1L, orderRequest);

        //Assert
        assertNotNull(response);
        assertEquals(102L, response.getCustomerId());
        assertEquals("ORD-111", response.getOrderNumber());
        assertEquals(CONFIRMED, response.getOrderStatus());
    }

    @Test
    void shouldCreateOrder(){
        //Arrange
        Order savedOrder = new Order();
        savedOrder.setOrderNumber("ORD-111");
        savedOrder.setCustomerId(101L);
        savedOrder.setOrderStatus(CONFIRMED);
        savedOrder.setOrderItems(new ArrayList<>());

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(101L);
        orderRequest.setOrderStatus(CONFIRMED);
        orderRequest.setOrderNumber("ORD-111");

        List<OrderItemRequest> itemList = new ArrayList<>();
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(22222L);
        orderItemRequest.setQuantity(2);
        itemList.add(orderItemRequest);

        orderRequest.setOrderItems(itemList);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setPrice(337666D);
        productResponse.setProductName("Phone");
        productResponse.setProductId(33333L);

        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class))).thenReturn(productResponse);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals("ORD-111", response.getOrderNumber());

    }

}
