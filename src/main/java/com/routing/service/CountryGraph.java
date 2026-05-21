package com.routing.service;

import java.util.Set;

public interface CountryGraph {
    boolean containsCountry(String cca3);
    Set<String> getNeighbors(String cca3);
}
