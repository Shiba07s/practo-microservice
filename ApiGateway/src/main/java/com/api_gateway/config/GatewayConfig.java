package com.api_gateway.config;


 
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//            // Route for patient service
//            .route("Patient-Service", r -> r
//                .path("/api/v1/patient-profile/**")
//                .uri("lb://Patient-Service"))
//                
//            // Route for doctor service
//            .route("Doctor-Service", r -> r
//                .path("/api/v1/doctor-profile/**")
//                .uri("lb://Doctor-Service"))
//                
//            .build();
//    }
}