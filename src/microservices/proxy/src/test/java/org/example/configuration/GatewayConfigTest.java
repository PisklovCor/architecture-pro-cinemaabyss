package org.example.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "GRADUAL_MIGRATION=false"
})
class GatewayConfigTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldConfigureAllRoutes() {
        List<Route> routes = routeLocator.getRoutes()
                .collectList()
                .block();

        assertThat(routes)
                .isNotNull()
                .hasSize(3)
                .anySatisfy(route -> {
                    assertThat(route.getId()).isEqualTo("movies-service-route");
                    assertThat(route.getUri().toString()).contains("movies-service:8081");
                })
                .anySatisfy(route -> {
                    assertThat(route.getId()).isEqualTo("events-service-route");
                    assertThat(route.getUri().toString()).contains("events-service:8082");
                })
                .anySatisfy(route -> {
                    assertThat(route.getId()).isEqualTo("monolith-fallback");
                    assertThat(route.getUri().toString()).contains("monolith:8080");
                });
    }
}
