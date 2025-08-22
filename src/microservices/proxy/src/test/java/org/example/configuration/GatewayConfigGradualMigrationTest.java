package org.example.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.test.StepVerifier;

@SpringBootTest
@TestPropertySource(properties = {
    "GRADUAL_MIGRATION=true",
    "MOVIES_MIGRATION_PERCENT=30"
})
class GatewayConfigGradualMigrationTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldSetupGradualMigrationRoutes() {
        StepVerifier.create(routeLocator.getRoutes())
                .expectNextMatches(route -> route.getId().equals("movies-service-migration")
                        && route.getUri().toString().contains("movies-service:8081"))
                .expectNextMatches(route -> route.getId().equals("movies-monolith-fallback")
                        && route.getUri().toString().contains("monolith:8080"))
                .expectNextCount(2)
                .verifyComplete();
    }
}
