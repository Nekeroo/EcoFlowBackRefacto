package com.ecoflow.back.models.marker;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Marker {
    private long id;

    private double lat;

    private double lon;

    private String name;

    private Tag tags;

    private String address;

}