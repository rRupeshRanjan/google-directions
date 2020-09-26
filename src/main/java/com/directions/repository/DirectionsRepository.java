package com.directions.repository;

import com.directions.configuration.AppConfiguration;
import com.directions.domain.GoogleApiResponse;
import com.directions.domain.LatLng;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
public class DirectionsRepository {

    private final String url;
    private final String key;
    private final WebClient webClient;
    private static final int MAX_RETRIES = 5;

    public DirectionsRepository(AppConfiguration configuration) {
        this.url = configuration.getGoogleDirectionsUrl();
        this.key = configuration.getGetGoogleDirectionsKey();
        this.webClient = WebClient.create(url);
    }

    public Mono<GoogleApiResponse> getDirections(LatLng origin, LatLng destination) {
        String start = origin.getLat() + "," + origin.getLng();
        String dest = destination.getLat() + "," + destination.getLng();

        return webClient.get()
                .uri(url, start, dest, key)
                .exchange()
                .flatMap(this::clientResponseValidator)
                .retry(MAX_RETRIES, throwable -> throwable instanceof ServerErrorException);
    }

    private Mono<GoogleApiResponse> clientResponseValidator(ClientResponse clientResponse) {
        HttpStatus httpStatus = clientResponse.statusCode();

        if (!httpStatus.equals(HttpStatus.OK)) {
            log.error("Error while getting coordinates with status code:" + httpStatus.value());
            return Mono.empty();
        } else {
            return clientResponse.bodyToMono(GoogleApiResponse.class);
        }
    }
}
