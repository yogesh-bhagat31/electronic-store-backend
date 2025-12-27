package com.learn.electronicstore.dtos;


import com.learn.electronicstore.enums.OrderStatus;
import com.learn.electronicstore.enums.PaymentStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private String orderId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private int orderAmount;
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderedDate;
    private Date deliveredDate;
    private List<OrderItemDto> orderItems = new ArrayList<>();

}

