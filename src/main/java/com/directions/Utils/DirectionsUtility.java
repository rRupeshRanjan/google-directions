package com.directions.utils;

import com.directions.domain.LatLng;
import com.google.maps.internal.PolylineEncoding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectionsUtility {
    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

    public double getDistance(LatLng start, LatLng end) {
        double dLat  = Math.toRadians(end.getLat() - start.getLat());
        double dLong = Math.toRadians(end.getLng() - start.getLng());
        double a = haversin(dLat) +
                Math.cos(Math.toRadians(start.getLat())) *
                        Math.cos(Math.toRadians(end.getLat())) *
                        haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * 1000; // in metres
    }

    private static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public List<LatLng> convertPolyLineToCoordinates(String polyline) {
        return PolylineEncoding.decode(polyline)
                .stream()
                .map(c -> new LatLng(c.lat, c.lng))
                .collect(Collectors.toList());
    }

    public List<LatLng> getInterpolatedCoordinates(LatLng start, LatLng end, double distThreshold) {
        List<LatLng> results = new ArrayList<>();

        double x1 = start.getLat(), y1 = start.getLng();
        double x2 = end.getLat(), y2 = end.getLng();
        double distance = getDistance(start, end);

        double latInc = (x2-x1) * distThreshold / distance;
        double lngInc = (y2-y1) * distThreshold / distance;

        if(x2>x1 && y2>y1) {
            while (x1 + latInc <= end.getLat() && y1 + lngInc <= end.getLng()) {
                results.add(new LatLng(x1, y1));
                x1 += latInc;
                y1 += lngInc;
            }
        } else if(x2<x1 && y2>y1) {
            while (x1 - latInc >= end.getLat() && y1 + lngInc <= end.getLng()) {
                results.add(new LatLng(x1, y1));
                x1 += latInc;
                y1 += lngInc;
            }
        } else if(x2>x1 && y2<y1) {
            while (x1 + latInc <= end.getLat() && y1 - lngInc >= end.getLng()) {
                results.add(new LatLng(x1, y1));
                x1 += latInc;
                y1 += lngInc;
            }
        } else if(x2<x1 && y2<y1) {
            while (x1 - latInc >= end.getLat() && y1 - lngInc >= end.getLng()) {
                results.add(new LatLng(x1, y1));
                x1 += latInc;
                y1 += lngInc;
            }
        }

        results.add(end);
        return results;
    }
}
