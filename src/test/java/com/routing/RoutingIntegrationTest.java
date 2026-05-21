package com.routing;

import com.routing.model.ErrorResponse;
import com.routing.model.RoutingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoutingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void routing_directNeighbours_returns200() {
        ResponseEntity<RoutingResponse> response = restTemplate.getForEntity(
                "/routing/CZE/AUT", RoutingResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().route()).containsExactly("CZE", "AUT");
    }

    @Test
    void routing_czechToItaly_returnsShortestPath() {
        ResponseEntity<RoutingResponse> response = restTemplate.getForEntity(
                "/routing/CZE/ITA", RoutingResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().route()).containsExactly("CZE", "AUT", "ITA");
    }

    @Test
    void routing_sameCountry_returnsSingleElement() {
        ResponseEntity<RoutingResponse> response = restTemplate.getForEntity(
                "/routing/CZE/CZE", RoutingResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().route()).containsExactly("CZE");
    }

    @Test
    void routing_longRoute_returnsValidPath() {
        ResponseEntity<RoutingResponse> response = restTemplate.getForEntity(
                "/routing/CZE/PRT", RoutingResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().route()).first().isEqualTo("CZE");
        assertThat(response.getBody().route()).last().isEqualTo("PRT");
        assertThat(response.getBody().route()).hasSizeGreaterThan(2);
    }

    @Test
    void routing_islandNations_returns400() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/routing/JPN/AUS", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).contains("JPN").contains("AUS");
    }

    @Test
    void routing_unknownOrigin_returns400WithMessage() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/routing/XYZ/ITA", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Country not found: XYZ");
    }

    @Test
    void routing_unknownDestination_returns400WithMessage() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/routing/CZE/XYZ", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Country not found: XYZ");
    }

    @Test
    void healthCheck_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
