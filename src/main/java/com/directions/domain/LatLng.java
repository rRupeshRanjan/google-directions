package com.directions.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatLng {
    private double lat;
    private double lng;

    public LatLng(String location) throws Exception {
        String[] split = location.split(",");
        if(split.length==2) {
            this.lat = Double.parseDouble(split[0]);
            this.lng = Double.parseDouble(split[1]);
        } else {
            throw new Exception("Incorrect coordinates passed");
        }
    }
}
