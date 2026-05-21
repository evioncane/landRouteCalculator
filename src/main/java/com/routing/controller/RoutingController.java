package com.routing.controller;

import com.routing.model.RoutingResponse;
import com.routing.service.RoutingService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/{origin}/{destination}")
    public RoutingResponse getRoute(
            @PathVariable @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter ISO 3166-1 alpha-3 code") String origin,
            @PathVariable @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter ISO 3166-1 alpha-3 code") String destination) {
        return new RoutingResponse(routingService.findRoute(origin, destination));
    }
}
