package com.idz4.ordersservice.repository;

import com.idz4.ordersservice.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByIsAvailable(Boolean filedValue);

}
