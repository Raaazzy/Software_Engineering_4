package com.idz4.ordersservice.controller;

import com.idz4.ordersservice.entity.Dish;
import com.idz4.ordersservice.entity.Orders;
import com.idz4.ordersservice.entity.Order_dish;
import com.idz4.ordersservice.service.OrdersService;
import com.idz4.ordersservice.types.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getmenu")
    public ResponseEntity<List<Dish>> getMenu() {
        return ResponseEntity.status(HttpStatus.OK).body(ordersService.getMenu());
    }

    @PostMapping("/createdish")
    public ResponseEntity<String> createDish(@RequestBody Dish dish, @RequestParam String token) {
        String role = restTemplate.getForObject("http://USER-SERVICE/users/getrole?token=" + token, String.class);
        if (!Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user does not have required role");
        }
        ordersService.saveDish(dish);
        if (dish.getName().isBlank() || dish.getDescription().isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Wrong parameters");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Created dish");
    }

    @GetMapping("/readdish")
    public ResponseEntity<String> readDish(@RequestParam Long id, @RequestParam String token) {
        String role = restTemplate.getForObject("http://USER-SERVICE/users/getrole?token=" + token, String.class);
        if (!Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user does not have required role");
        }
        Optional<Dish> dish = ordersService.readDish(id);
        if (dish.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No dish with such id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(dish.toString());
    }

    @PutMapping("/putdish")
    public ResponseEntity<String> updateDish(@RequestBody Dish dish, @RequestParam String token) {
        String role = restTemplate.getForObject("http://USER-SERVICE/users/getrole?token=" + token, String.class);
        if (!Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user does not have required role");
        }
        Optional<Dish> optionalDish = ordersService.readDish(dish.getId());
        if (optionalDish.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No dish with such id");
        }
        ordersService.saveDish(dish);
        return ResponseEntity.status(HttpStatus.OK).body("Dish was successfully updated");
    }

    @DeleteMapping("/deletedish")
    public ResponseEntity<String> deleteDish(@RequestParam Long id, @RequestParam String token) {
        String role = restTemplate.getForObject("http://USER-SERVICE/users/getrole?token=" + token, String.class);
        if (!Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user does not have required role");
        }
        Optional<Dish> optionalDish = ordersService.readDish(id);
        if (optionalDish.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No dish with such id");
        }
        ordersService.deleteDish(id);
        return ResponseEntity.status(HttpStatus.OK).body("Dish deleted successfully");
    }

    @GetMapping("/getorderinfo")
    public ResponseEntity<String> getOrderInfo(@RequestParam Long id, @RequestParam String token) {
        String role = restTemplate.getForObject("http://USER-SERVICE/users/getrole?token=" + token, String.class);
        if (!Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user does not have required role");
        }
        Optional<Order_dish> optionalOrderDish = ordersService.getOrderDish(id);
        if (optionalOrderDish.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No order with such id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalOrderDish.get().toString());
    }

    @PostMapping("/createorder")
    public ResponseEntity<String> createOrder(@RequestBody OrderType orderType){
        String role = restTemplate.getForObject("http://USER-SERVICE/users/getrole?token=" + orderType.getToken(), String.class);
        if (!Objects.equals(role, "manager")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user does not have required role");
        }
        Orders orders = new Orders();
        orders.setStatus("being done");
        orders.setSpecial_requests(orderType.getSpecialRequests());
        orders.setCreated_at(new Date());
        orders.setUser_id(orders.getUser_id());
        ordersService.saveOrder(orders);
        List<Order_dish> dishes = new ArrayList<>();
        for (OrderType.DishesType dish : orderType.getDishes()) {
            ResponseEntity<String> dishTemp = readDish(dish.getId(), orderType.getToken());
            if (dishTemp.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such dish");
            }
            Order_dish orderDish = new Order_dish();
            orderDish.setOrder_id(orders.getId());
            orderDish.setDish_id(dish.getId());
            orderDish.setPrice(dish.getPrice());
            orderDish.setQuantity(dish.getQuantity());
            if (!ordersService.decreaseQuantity(dish.getId(), dish.getQuantity())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not enough dishes in stock");
            }
            dishes.add(orderDish);
        }
        for (Order_dish orderDish : dishes){
            ordersService.saveOrderDish(orderDish);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Successfully created order");
    }
}
