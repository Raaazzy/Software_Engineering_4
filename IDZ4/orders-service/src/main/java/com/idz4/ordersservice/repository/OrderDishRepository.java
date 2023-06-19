package com.idz4.ordersservice.repository;

import com.idz4.ordersservice.entity.Order_dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDishRepository extends JpaRepository<Order_dish, Long> {

}
