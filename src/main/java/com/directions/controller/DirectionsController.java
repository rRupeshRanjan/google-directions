package com.directions.controller;

import com.directions.domain.LatLng;
import com.directions.service.DirectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DirectionsController {
    private DirectionService directionService;

    public DirectionsController(DirectionService directionService) {
        this.directionService = directionService;
    }

    /*
    * usgae: http://localhost:8080/getCoordinates?origin=12.9783,77.6408&destination=12.9351,77.6244
    * */
    @GetMapping("/getCoordinates")
    public Flux<LatLng> getCoordinates(
            @RequestParam(value = "origin") String origin,
            @RequestParam(value = "destination") String destination) throws Exception {

        return directionService.getDirections(new LatLng(origin), new LatLng(destination));
    }
}
