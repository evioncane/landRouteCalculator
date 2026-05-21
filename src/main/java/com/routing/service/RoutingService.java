package com.routing.service;

import com.routing.exception.CountryNotFoundException;
import com.routing.exception.NoRouteFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Finds the shortest land route between two countries using BFS.
 * Time complexity: O(V + E), where V = countries, E = border pairs.
 * Space complexity: O(V) for the visited set and parent map.
 */
@Service
public class RoutingService {

    private final CountryGraph countryGraph;

    public RoutingService(CountryGraph countryGraph) {
        this.countryGraph = countryGraph;
    }

    public List<String> findRoute(String origin, String destination) {
        if (!countryGraph.containsCountry(origin)) {
            throw new CountryNotFoundException(origin);
        }
        if (!countryGraph.containsCountry(destination)) {
            throw new CountryNotFoundException(destination);
        }

        if (origin.equals(destination)) {
            return List.of(origin);
        }

        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        queue.add(origin);
        visited.add(origin);
        parent.put(origin, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (String neighbor : countryGraph.getNeighbors(current)) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                parent.put(neighbor, current);
                if (neighbor.equals(destination)) {
                    return reconstructPath(parent, destination);
                }
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }

        throw new NoRouteFoundException(origin, destination);
    }

    private List<String> reconstructPath(Map<String, String> parent, String destination) {
        List<String> path = new ArrayList<>();
        String current = destination;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}
