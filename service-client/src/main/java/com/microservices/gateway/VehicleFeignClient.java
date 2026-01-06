package com.microservices.gateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-vehicle")
public interface VehicleFeignClient {
    @GetMapping("/api/vehicles/byUser/{userId}")
    Object getVehicle(@PathVariable("userId") Long userId);
}

