package com.routing.model;

import java.util.List;

public record RoutingResponse(List<String> route) {
    public RoutingResponse {
        route = List.copyOf(route);
    }
}
