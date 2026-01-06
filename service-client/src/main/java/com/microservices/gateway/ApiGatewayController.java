package com.microservices.gateway;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@AllArgsConstructor
public class ApiGatewayController {

    private final RestTemplate restTemplate;
    private final VehicleFeignClient vehicleFeignClient;
    private final WebClient.Builder webClientBuilder;

    @GetMapping("/api/gateway/{userId}/vehicle/rest")
    public Object getVehicleRest(@PathVariable Long userId) {
        return restTemplate.getForObject("http://service-vehicle/api/vehicles/byUser/" + userId, Object.class);
    }

    @GetMapping("/api/gateway/{userId}/vehicle/feign")
    public Object getVehicleFeign(@PathVariable Long userId) {
        return vehicleFeignClient.getVehicle(userId);
    }

    @GetMapping("/api/gateway/{userId}/vehicle/webclient")
    public Object getVehicleWebClient(@PathVariable Long userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://service-vehicle/api/vehicles/byUser/" + userId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}

