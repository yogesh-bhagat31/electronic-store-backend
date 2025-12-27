package com.learn.electronicstore.repositories;

import com.learn.electronicstore.entities.Category;
import com.learn.electronicstore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByTitleContaining(Pageable pageable, String title);

    Page<Product> findByLiveTrue(Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);


}
