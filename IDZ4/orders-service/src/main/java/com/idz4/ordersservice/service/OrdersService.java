package com.idz4.ordersservice.service;

import com.idz4.ordersservice.entity.Dish;
import com.idz4.ordersservice.entity.Orders;
import com.idz4.ordersservice.entity.Order_dish;
import com.idz4.ordersservice.repository.DishRepository;
import com.idz4.ordersservice.repository.OrderDishRepository;
import com.idz4.ordersservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private OrderDishRepository orderDishRepository;

    public List<Dish> getMenu() {
        return dishRepository.findByIsAvailable(true);
    }

    public void saveDish(Dish dish) {
        dishRepository.save(dish);
    }

    public Optional<Dish> readDish(Long id) {
        return dishRepository.findById(id);
    }

    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }

    public Optional<Order_dish> getOrderDish(Long id) {
        return orderDishRepository.findById(id);
    }

    public void saveOrderDish(Order_dish orderDish) {
        orderDishRepository.save(orderDish);
    }

    public Boolean decreaseQuantity(Long id, Integer quantity){
        Optional<Dish> dish = dishRepository.findById(id);
        if (dish.isEmpty()) {
            return false;
        }
        if (dish.get().getQuantity() - quantity >= 0) {
            dish.get().setQuantity(dish.get().getQuantity() - quantity);
            return true;
        } else {
            return false;
        }
    }

    public void saveOrder(Orders orders) {
        orderRepository.save(orders);
    }
}
