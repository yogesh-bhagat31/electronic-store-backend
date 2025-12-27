package com.learn.electronicstore.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    private String productId;

    @Column(nullable = false)
    private String title;

    @Column(length = 10000)
    private String description;

    @Column(nullable = false)
    private int price;

    private int discountPrice;

    @Column(nullable = false)
    private int quantity;

    private Date addedDate;

    @Column(name = "is_live")
    private boolean live;

    private boolean stock;

    private String productImageName;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;


}

