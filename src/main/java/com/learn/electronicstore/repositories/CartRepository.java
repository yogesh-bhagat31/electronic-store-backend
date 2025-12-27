package com.learn.electronicstore.repositories;

import com.learn.electronicstore.entities.Cart;
import com.learn.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {


    Optional<Cart> findByUser(User user);
}
