package com.learn.electronicstore.dtos;

import com.learn.electronicstore.enums.OrderStatus;
import com.learn.electronicstore.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {
    @NotBlank(message = "User ID cannot be blank")
    String userId;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    @NotBlank(message = "Billing address cannot be blank")
    @Size(max = 255, message = "Billing address cannot exceed 255 characters")
    private String billingAddress;

    @NotBlank(message = "Billing phone cannot be blank")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String billingPhone;

    @NotBlank(message = "Billing name cannot be blank")
    @Size(min = 2, max = 50, message = "Billing name must be between 2 and 50 characters")
    private String billingName;
}

