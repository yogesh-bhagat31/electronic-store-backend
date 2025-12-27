package com.learn.electronicstore.repositories;

import com.learn.electronicstore.entities.Order;
import com.learn.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUser(User user);
}
