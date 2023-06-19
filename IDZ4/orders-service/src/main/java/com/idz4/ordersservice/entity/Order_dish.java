package com.idz4.ordersservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order_dish {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long order_id;
    private Long dish_id;
    private Integer quantity;
    private Double price;
}
