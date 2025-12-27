package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.*;

import java.util.List;

public interface OrderService {

    //create order
    OrderDto createOrder(CreateOrderRequest orderDto);

    //remove order
    void removeOrder(String orderId);

    //get orders of user
    List<OrderDto> getOrdersOfUser(String userId);

    //get orders
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

    // confirmOrder
    OrderDto confirmOrder(String orderId);

    // update orderStatus
    OrderDto updateOrderStatus(String orderId, UpdateOrderStatusRequest orderStatus);
}
