package com.routing.controller;

import com.routing.model.ErrorResponse;
import com.routing.model.RoutingResponse;
import com.routing.service.RoutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Routing", description = "Land route calculation between countries")
@Validated
@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @Operation(
            summary = "Find shortest land route",
            description = "Returns the ordered list of country codes representing the shortest path "
                    + "of border crossings from origin to destination. "
                    + "Both codes must be ISO 3166-1 alpha-3 (cca3) format."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Route found",
                content = @Content(schema = @Schema(implementation = RoutingResponse.class),
                        examples = @ExampleObject(value = "{\"route\":[\"CZE\",\"AUT\",\"ITA\"]}"))),
        @ApiResponse(responseCode = "400", description = "No land route exists or invalid country code",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                value = "{\"message\":\"No land route found from JPN to AUS\"}"))),
        @ApiResponse(responseCode = "404", description = "Endpoint not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{origin}/{destination}")
    public RoutingResponse getRoute(
            @Parameter(description = "Origin country code (ISO 3166-1 alpha-3)", example = "CZE")
            @PathVariable
            @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter ISO 3166-1 alpha-3 code")
            String origin,
            @Parameter(description = "Destination country code (ISO 3166-1 alpha-3)", example = "ITA")
            @PathVariable
            @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter ISO 3166-1 alpha-3 code")
            String destination) {
        return new RoutingResponse(routingService.findRoute(origin, destination));
    }
}
