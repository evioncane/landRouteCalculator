package com.routing.controller;

import com.routing.exception.CountryNotFoundException;
import com.routing.exception.NoRouteFoundException;
import com.routing.service.RoutingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoutingController.class)
class RoutingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoutingService routingService;

    @Test
    void getRoute_validLandRoute_returns200WithRoute() throws Exception {
        when(routingService.findRoute("CZE", "ITA"))
                .thenReturn(List.of("CZE", "AUT", "ITA"));

        mockMvc.perform(get("/routing/CZE/ITA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route[0]").value("CZE"))
                .andExpect(jsonPath("$.route[1]").value("AUT"))
                .andExpect(jsonPath("$.route[2]").value("ITA"))
                .andExpect(jsonPath("$.route.length()").value(3));
    }

    @Test
    void getRoute_sameOriginAndDestination_returns200WithSingleElement() throws Exception {
        when(routingService.findRoute("CZE", "CZE"))
                .thenReturn(List.of("CZE"));

        mockMvc.perform(get("/routing/CZE/CZE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route[0]").value("CZE"))
                .andExpect(jsonPath("$.route.length()").value(1));
    }

    @Test
    void getRoute_noLandRoute_returns400() throws Exception {
        when(routingService.findRoute("JPN", "AUS"))
                .thenThrow(new NoRouteFoundException("JPN", "AUS"));

        mockMvc.perform(get("/routing/JPN/AUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getRoute_unknownOrigin_returns400() throws Exception {
        when(routingService.findRoute("XYZ", "ITA"))
                .thenThrow(new CountryNotFoundException("XYZ"));

        mockMvc.perform(get("/routing/XYZ/ITA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Country not found: XYZ"));
    }

    @Test
    void getRoute_unknownDestination_returns400() throws Exception {
        when(routingService.findRoute("CZE", "XYZ"))
                .thenThrow(new CountryNotFoundException("XYZ"));

        mockMvc.perform(get("/routing/CZE/XYZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Country not found: XYZ"));
    }

    @Test
    void getRoute_tooLongOrigin_returns400() throws Exception {
        mockMvc.perform(get("/routing/INVALID/ITA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getRoute_lowercaseOrigin_returns400() throws Exception {
        mockMvc.perform(get("/routing/cze/ITA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void unknownPath_returns404WithMessage() throws Exception {
        mockMvc.perform(get("/unknown/path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void postToRoutingEndpoint_returns405WithMessage() throws Exception {
        mockMvc.perform(post("/routing/CZE/ITA"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteToRoutingEndpoint_returns405WithMessage() throws Exception {
        mockMvc.perform(delete("/routing/CZE/ITA"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").exists());
    }
}
