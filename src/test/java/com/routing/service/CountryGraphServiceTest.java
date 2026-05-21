package com.routing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routing.model.Country;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryGraphServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void loadCountryData_buildsGraphFromClasspath() {
        CountryGraphService service = new CountryGraphService(MAPPER);
        service.loadCountryData();

        // Test fixture (src/test/resources/countries.json) has CZE, AUT, ITA, JPN, AUS, etc.
        assertThat(service.containsCountry("CZE")).isTrue();
        assertThat(service.containsCountry("JPN")).isTrue();
        assertThat(service.containsCountry("XYZ")).isFalse();
        assertThat(service.getNeighbors("CZE")).containsExactlyInAnyOrder("AUT", "DEU", "POL", "SVK");
        assertThat(service.getNeighbors("JPN")).isEmpty();
        assertThat(service.getNeighbors("UNKNOWN")).isEmpty();
    }

    @Test
    void country_nullBorders_defaultsToEmpty() {
        Country country = new Country("TST", null);

        assertThat(country.borders()).isEmpty();
    }

    @Test
    void country_borders_areImmutableCopy() {
        List<String> mutableBorders = new ArrayList<>(List.of("AUT", "DEU"));
        Country country = new Country("CZE", mutableBorders);

        mutableBorders.add("POL");

        assertThat(country.borders()).hasSize(2);
    }
}
