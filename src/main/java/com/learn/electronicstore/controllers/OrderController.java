package com.learn.electronicstore.controllers;

import com.learn.electronicstore.dtos.*;
import com.learn.electronicstore.enums.OrderStatus;
import com.learn.electronicstore.exceptions.ResourceNotFoundException;
import com.learn.electronicstore.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {

    OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasAnyRole('NORMAL','ADMIN')")
    @PostMapping()
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        OrderDto order = orderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId) {
        orderService.removeOrder(orderId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Order removed successfully")
                .success(true)
                .httpStatus(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('NORMAL','ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUser(@PathVariable String userId) {
        List<OrderDto> ordersOfUser = orderService.getOrdersOfUser(userId);
        return new ResponseEntity<>(ordersOfUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getOrders(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "orderedDate", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageableResponse<OrderDto> orders = orderService.getOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> confirmOrder(@PathVariable String orderId) {
        ApiResponseMessage message = null;
        if (orderService.confirmOrder(orderId).getOrderStatus() == OrderStatus.CONFIRMED) {
            message = ApiResponseMessage.builder()
                    .message("Order has confirmed")
                    .success(true)
                    .httpStatus(HttpStatus.OK)
                    .build();
        } else {
            throw new ResourceNotFoundException("Order has not comfirmed");
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/updates/{orderId}")
    public ResponseEntity<ApiResponseMessage> updateOrderStatus(@PathVariable String orderId, @RequestBody UpdateOrderStatusRequest newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Order status changed successfully to " + newStatus.getNewStatus())
                .success(true)
                .httpStatus(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
