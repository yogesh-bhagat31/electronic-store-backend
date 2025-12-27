package com.learn.electronicstore.repositories;

import com.learn.electronicstore.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.itemId = :cartItemId")
    void deleteCartItem(@Param("cartItemId") int cartItemId);
}
