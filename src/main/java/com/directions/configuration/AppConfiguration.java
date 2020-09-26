package com.directions.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppConfiguration {

    @Value("${google.directions.url}")
    private String googleDirectionsUrl;

    @Value("${google.directions.key}")
    private String getGoogleDirectionsKey;
}
