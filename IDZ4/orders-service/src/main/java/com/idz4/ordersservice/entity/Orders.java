package com.idz4.ordersservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long user_id;
    private String status;
    private String special_requests;
    private Date created_at = new Date();
    private Date updated_at = new Date();

    @PreUpdate
    public void preUpdate() {
        this.updated_at = new Date();
    }
}
