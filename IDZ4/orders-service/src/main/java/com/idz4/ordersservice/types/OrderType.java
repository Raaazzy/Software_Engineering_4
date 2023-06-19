package com.idz4.ordersservice.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderType {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DishesType {
        private Long id;
        private Integer quantity;
        private Double price;
    }
    private String token;
    private List<DishesType> dishes;
    private String specialRequests;
    private String status;
}
