package org.example.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${MONOLITH_URL:http://monolith:8080}")
    private String monolithUrl;

    @Value("${MOVIES_SERVICE_URL:http://movies-service:8081}")
    private String moviesServiceUrl;

    @Value("${EVENTS_SERVICE_URL:http://events-service:8082}")
    private String eventsServiceUrl;

    @Value("${GRADUAL_MIGRATION:false}")
    private boolean gradualMigration;

    @Value("${MOVIES_MIGRATION_PERCENT:0}")
    private int moviesMigrationPercent;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        if (gradualMigration && moviesMigrationPercent > 0) {
            // Маршрут для процентной миграции movies
            routes.route("movies-service-migration", r -> r
                    .path("/api/movies")
                    .and()
                    .weight("movies-group", moviesMigrationPercent)
                    .uri(moviesServiceUrl));

            // Остальной трафик movies идет на монолит
            routes.route("movies-monolith-fallback", r -> r
                    .path("/api/movies")
                    .and()
                    .weight("movies-group", 100 - moviesMigrationPercent)
                    .uri(monolithUrl));
        } else {
            // Обычный маршрут movies (без миграции)
            routes.route("movies-service-route", r -> r
                    .path("/api/movies")
                    .uri(moviesServiceUrl));
        }

        // Маршруты для events сервиса
        routes.route("events-service-route", r -> r
                .path("/api/events/**")
                .uri(eventsServiceUrl));

        // Все остальные запросы идут на монолит
        routes.route("monolith-fallback", r -> r
                .path("/**")
                .uri(monolithUrl));

        return routes.build();
    }
}
