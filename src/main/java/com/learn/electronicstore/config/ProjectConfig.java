package com.learn.electronicstore.config;

import com.learn.electronicstore.dtos.CartDto;
import com.learn.electronicstore.dtos.CartItemDto;
import com.learn.electronicstore.dtos.JwtRequest;
import com.learn.electronicstore.dtos.ProductDto;
import com.learn.electronicstore.entities.Cart;
import com.learn.electronicstore.entities.CartItem;
import com.learn.electronicstore.entities.Product;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;

@Configuration
public class ProjectConfig {


    @Bean
    public ModelMapper modelMapper() {

        return new ModelMapper();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public ModelMapper cartModelMapper() {
        ModelMapper modelMapper = new ModelMapper();


        modelMapper.typeMap(Cart.class, CartDto.class).addMappings(mapper -> {
            mapper.map(Cart::getUser, CartDto::setUserDto);
        });


        modelMapper.typeMap(Cart.class, CartDto.class).addMappings(mapper -> {
            mapper.map(Cart::getCartItems, CartDto::setCartItemDtos);
        });


        modelMapper.typeMap(CartItem.class, CartItemDto.class).addMappings(mapper -> {
            mapper.map(CartItem::getProduct, CartItemDto::setProductDto);
        });


        modelMapper.typeMap(Product.class, ProductDto.class).addMappings(mapper -> {
            mapper.map(Product::getCategory, ProductDto::setCategoryDto);
        });


        return modelMapper;

    }

}
