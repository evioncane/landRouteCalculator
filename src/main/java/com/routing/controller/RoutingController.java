package com.routing.controller;

import com.routing.model.RoutingResponse;
import com.routing.service.RoutingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/{origin}/{destination}")
    public RoutingResponse getRoute(
            @PathVariable String origin,
            @PathVariable String destination) {
        return new RoutingResponse(routingService.findRoute(origin, destination));
    }
}
