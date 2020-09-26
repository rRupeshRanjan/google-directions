package com.directions.service;

import com.directions.utils.DirectionsUtility;
import com.directions.domain.GoogleApiResponse;
import com.directions.domain.LatLng;
import com.directions.domain.Route;
import com.directions.repository.DirectionsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class DirectionService {
    private static final double interval = 50;
    private DirectionsRepository directionsRepository;
    private final DirectionsUtility directionsUtility;

    public DirectionService(DirectionsRepository directionsRepository) {
        this.directionsRepository = directionsRepository;
        this.directionsUtility = new DirectionsUtility();
    }

    public Flux<LatLng> getDirections(LatLng start, LatLng end) {
        return directionsRepository.getDirections(start, end)
                .flatMapMany(directions -> {
                    Route optimalRoute = getOptimalRoute(directions);
                    List<LatLng> coordinates = directionsUtility
                            .convertPolyLineToCoordinates(optimalRoute.getPolyline().getPoints());

                    if(!coordinates.isEmpty()) {
                        return Flux.fromIterable(
                                directionsUtility.getInterpolatedCoordinates(
                                        coordinates.get(0), coordinates.get(coordinates.size()-1), interval)
                        );
                    } else {
                        return Flux.just(start, end);
                    }
                }).switchIfEmpty(Flux.just(start, end));
    }

    /*
    * Find optimal route based on distances of steps involved
    * */
    private Route getOptimalRoute(GoogleApiResponse directions) {
        int minRouteLength = Integer.MAX_VALUE;
        Route optimalRoute = null;
        List<Route> routes = directions.getRoutes();
        for(int i=0; i<routes.size(); i++) {
            int currRouteLength = routes.get(i)
                    .getLegs()
                    .stream()
                    .mapToInt(leg -> leg.getDistance().getValue())
                    .sum();

            if(currRouteLength < minRouteLength) {
                optimalRoute = directions.getRoutes().get(i);
                minRouteLength = currRouteLength;
            }
        }

        return optimalRoute;
    }
}
