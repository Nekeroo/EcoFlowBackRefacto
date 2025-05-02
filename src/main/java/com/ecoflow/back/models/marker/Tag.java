package com.ecoflow.back.models.marker;

import lombok.Builder;

@Builder
public record Tag(
        String amenity,
        String name,
        String recycling_type
) {
}
