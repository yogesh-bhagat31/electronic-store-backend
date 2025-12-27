package com.learn.electronicstore.services.servicesimpl;

import com.learn.electronicstore.dtos.AddItemToCartRequest;
import com.learn.electronicstore.dtos.CartDto;
import com.learn.electronicstore.entities.Cart;
import com.learn.electronicstore.entities.CartItem;
import com.learn.electronicstore.entities.Product;
import com.learn.electronicstore.entities.User;
import com.learn.electronicstore.exceptions.BadApiRequestException;
import com.learn.electronicstore.exceptions.ResourceNotFoundException;
import com.learn.electronicstore.repositories.CartItemRepository;
import com.learn.electronicstore.repositories.CartRepository;
import com.learn.electronicstore.repositories.ProductRepository;
import com.learn.electronicstore.repositories.UserRepository;
import com.learn.electronicstore.services.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private ProductRepository productRepository;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private ModelMapper cartModelMapper;
    private CartItemRepository cartItemRepository;

    public CartServiceImpl(ProductRepository productRepository, UserRepository userRepository, CartRepository cartRepository, @Qualifier("cartModelMapper") ModelMapper cartModelMapper, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartModelMapper = cartModelMapper;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Adds an item to the user's shopping cart.
     *
     * @param userId  The ID of the user adding the item.
     * @param request The request containing product ID and quantity.
     * @return The updated cart details as a DTO.
     * @throws ResourceNotFoundException If the product or user is not found.
     */
    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request) throws BadRequestException {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0)
            throw new BadApiRequestException("Quantity must be greater than 0");

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
        } catch (NoSuchElementException e) {
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());
            cart.setUser(user);
        }


        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItem> cartItems = cart.getCartItems();

        List<CartItem> updatedItems = cartItems.stream().map(item -> {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setTotalPrice(item.getQuantity() * product.getDiscountPrice());
                updated.set(true);

            }
            return item;
        }).collect(Collectors.toList());


        if (!updated.get()) {
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountPrice())
                    .cart(cart)
                    .product(product)
                    .build();
            cart.getCartItems().add(cartItem);

        }

        Cart updatedCart = cartRepository.save(cart);

        return cartModelMapper.map(updatedCart, CartDto.class);
    }


    @Override
    @Transactional
    public void removeItemFromCart(String userId, Integer itemId) {
        boolean exists = cartItemRepository.existsById(itemId);
        System.out.println("Cart item exists: " + exists);
        cartItemRepository.deleteCartItem(itemId);
    }

    @Override
    public void clearCart(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return cartModelMapper.map(cart, CartDto.class);
    }
}

