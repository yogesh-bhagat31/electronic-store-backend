package com.learn.electronicstore.dtos;


import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {

    private String cartId;
    private Date createdAt;
    private UserDto userDto;
    private List<CartItemDto> cartItemDtos = new ArrayList<>();
}
