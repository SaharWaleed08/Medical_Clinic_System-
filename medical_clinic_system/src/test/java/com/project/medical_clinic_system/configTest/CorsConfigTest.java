package com.project.medical_clinic_system.configTest;

import com.project.medical_clinic_system.config.CorsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    /** CorsRegistry.getCorsConfigurations() is protected, so expose it through a tiny subclass. */
    private static class InspectableCorsRegistry extends CorsRegistry {
        Map<String, CorsConfiguration> configurations() {
            return getCorsConfigurations();
        }
    }

    private CorsConfiguration apiConfiguration() {
        InspectableCorsRegistry registry = new InspectableCorsRegistry();
        new CorsConfig().addCorsMappings(registry);
        return registry.configurations().get("/api/**");
    }

    @Test
    void addCorsMappings_registersOnlyTheApiPath() {
        InspectableCorsRegistry registry = new InspectableCorsRegistry();

        new CorsConfig().addCorsMappings(registry);

        assertEquals(1, registry.configurations().size());
        assertTrue(registry.configurations().containsKey("/api/**"));
    }

    @Test
    void addCorsMappings_allowsOnlyTheAngularDevOrigin() {
        CorsConfiguration config = apiConfiguration();

        assertNotNull(config);
        assertEquals(java.util.List.of("http://localhost:4200"), config.getAllowedOrigins());
    }

    @Test
    void addCorsMappings_allowsExpectedHttpMethods() {
        CorsConfiguration config = apiConfiguration();

        assertNotNull(config);
        assertTrue(config.getAllowedMethods().containsAll(
                java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE")));
    }

    @Test
    void addCorsMappings_doesNotAllowAnyOrigin() {
        CorsConfiguration config = apiConfiguration();

        assertNotNull(config);
        assertFalse(config.getAllowedOrigins().contains("*"));
    }
}
