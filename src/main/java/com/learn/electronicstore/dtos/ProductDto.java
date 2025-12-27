package com.learn.electronicstore.dtos;


import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDto {

    private String productId;
    @NotBlank(message = "Product title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 1000, message = "Descripion between 5 and 1000 characters")
    private String description;

    @Min(value = 1, message = "Price must be at least 1")
    private int price;

    @Min(value = 0, message = "Discount price must be positive")
    private int discountPrice;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;


    @PastOrPresent(message = "Added date must be in the past or present")
    private Date addedDate;

    private boolean live;

    private boolean stock;

    private String productImageName;
    private CategoryDto categoryDto;

}




