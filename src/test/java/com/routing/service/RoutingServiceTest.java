package com.routing.service;

import com.routing.exception.CountryNotFoundException;
import com.routing.exception.NoRouteFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoutingServiceTest {

    // Shared graph that covers the standard Central-European routing scenarios
    private static final CountryGraph EUROPE = graph(Map.ofEntries(
            Map.entry("CZE", Set.of("AUT", "DEU", "POL", "SVK")),
            Map.entry("AUT", Set.of("CZE", "DEU", "HUN", "ITA", "LIE", "SVK", "SVN", "CHE")),
            Map.entry("ITA", Set.of("AUT", "FRA", "SMR", "SVN", "VAT", "CHE")),
            Map.entry("DEU", Set.of("AUT", "BEL", "CZE", "DNK", "FRA", "LUX", "NLD", "POL", "CHE")),
            Map.entry("POL", Set.of("BLR", "CZE", "DEU", "LTU", "RUS", "SVK", "UKR")),
            Map.entry("SVK", Set.of("AUT", "CZE", "HUN", "POL", "UKR")),
            Map.entry("FRA", Set.of("AND", "BEL", "DEU", "ITA", "LUX", "MCO", "ESP", "CHE")),
            Map.entry("ESP", Set.of("AND", "FRA", "GIB", "PRT", "MAR")),
            Map.entry("PRT", Set.of("ESP")),
            Map.entry("JPN", Set.of()),
            Map.entry("AUS", Set.of())
    ));

    @Test
    void findRoute_directNeighbours_returnsTwoHopRoute() {
        List<String> route = new RoutingService(EUROPE).findRoute("CZE", "AUT");

        assertThat(route).containsExactly("CZE", "AUT");
    }

    @Test
    void findRoute_czechToItaly_returnsShortestPath() {
        List<String> route = new RoutingService(EUROPE).findRoute("CZE", "ITA");

        // Only AUT is a shared neighbour at depth-1; BFS guarantees the 3-hop path.
        assertThat(route).containsExactly("CZE", "AUT", "ITA");
    }

    @Test
    void findRoute_sameCountry_returnsSingleElement() {
        List<String> route = new RoutingService(EUROPE).findRoute("CZE", "CZE");

        assertThat(route).containsExactly("CZE");
    }

    @Test
    void findRoute_longRoute_pathIsValidAndShortest() {
        // Linear chain — single possible shortest path, deterministic result
        CountryGraph chain = graph(Map.of(
                "A", Set.of("B"),
                "B", Set.of("A", "C"),
                "C", Set.of("B", "D"),
                "D", Set.of("C", "E"),
                "E", Set.of("D")
        ));
        List<String> route = new RoutingService(chain).findRoute("A", "E");

        assertThat(route).containsExactly("A", "B", "C", "D", "E");
    }

    @Test
    void findRoute_islandOriginToIslandDestination_throwsNoRouteFoundException() {
        assertThatThrownBy(() -> new RoutingService(EUROPE).findRoute("JPN", "AUS"))
                .isInstanceOf(NoRouteFoundException.class)
                .hasMessageContaining("JPN")
                .hasMessageContaining("AUS");
    }

    @Test
    void findRoute_continentalToIsland_throwsNoRouteFoundException() {
        assertThatThrownBy(() -> new RoutingService(EUROPE).findRoute("CZE", "JPN"))
                .isInstanceOf(NoRouteFoundException.class);
    }

    @Test
    void findRoute_unknownOrigin_throwsCountryNotFoundException() {
        assertThatThrownBy(() -> new RoutingService(EUROPE).findRoute("XYZ", "ITA"))
                .isInstanceOf(CountryNotFoundException.class)
                .hasMessageContaining("XYZ");
    }

    @Test
    void findRoute_unknownDestination_throwsCountryNotFoundException() {
        assertThatThrownBy(() -> new RoutingService(EUROPE).findRoute("CZE", "XYZ"))
                .isInstanceOf(CountryNotFoundException.class)
                .hasMessageContaining("XYZ");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static CountryGraph graph(Map<String, Set<String>> data) {
        return new CountryGraph() {
            @Override
            public boolean containsCountry(String cca3) {
                return data.containsKey(cca3);
            }

            @Override
            public Set<String> getNeighbors(String cca3) {
                return data.getOrDefault(cca3, Set.of());
            }
        };
    }
}
