package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/user/**")
						.filters(f -> f.rewritePath("/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString())
								)
						.uri("lb://user-service")) //ln load balancer + application_name
				.route(p -> p
						.path("/accounts/**")
						.filters(f -> f.rewritePath("/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString())
								)
						.uri("lb://account-service")).build();
	}
}
