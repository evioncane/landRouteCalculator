package com.routing.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routing.model.Country;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CountryGraphService implements CountryGraph {

    private static final Logger log = LoggerFactory.getLogger(CountryGraphService.class);
    private static final String COUNTRIES_FILE = "countries.json";

    private final ObjectMapper objectMapper;
    private Map<String, Set<String>> adjacencyMap;

    public CountryGraphService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void loadCountryData() {
        log.info("Loading country graph from classpath:{}", COUNTRIES_FILE);
        try {
            ClassPathResource resource = new ClassPathResource(COUNTRIES_FILE);
            List<Country> countries = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<>() {});

            Map<String, Set<String>> graph = HashMap.newHashMap(countries.size() * 2);
            for (Country country : countries) {
                if (country.cca3() != null) {
                    graph.put(country.cca3(), new HashSet<>(country.borders()));
                }
            }

            this.adjacencyMap = Collections.unmodifiableMap(graph);
            log.info("Country graph built: {} countries loaded", adjacencyMap.size());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load country graph from classpath:" + COUNTRIES_FILE, e);
        }
    }

    @Override
    public boolean containsCountry(String cca3) {
        return adjacencyMap.containsKey(cca3);
    }

    @Override
    public Set<String> getNeighbors(String cca3) {
        return adjacencyMap.getOrDefault(cca3, Set.of());
    }
}
