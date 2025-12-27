package com.learn.electronicstore.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {

    private int itemId;
    private int quantity;
    private int totalPrice;
    private ProductDto productDto;
}

