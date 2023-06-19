package com.idz4.ordersservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.idz4.ordersservice.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

}
