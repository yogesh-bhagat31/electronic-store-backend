package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.AddItemToCartRequest;
import com.learn.electronicstore.dtos.CartDto;
import org.apache.coyote.BadRequestException;

public interface CartService {

    //Add items to cart
    CartDto addItemToCart(String userId, AddItemToCartRequest request) throws BadRequestException;

    //Remove item from cart
    void removeItemFromCart(String userId, Integer cartItemId);

    //clear cart
    void clearCart(String userId);

    //get cart for a user
    CartDto getCartByUser(String userId);


}
