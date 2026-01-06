package com.microservices.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehicleController {

    @GetMapping("/api/vehicles/byUser/{userId}")
    public Vehicle getVehicle(@PathVariable Long userId) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Vehicle(10L, "Toyota", "Yaris", userId);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Vehicle {
        private Long id;
        private String brand;
        private String model;
        private Long userId;
    }
}

