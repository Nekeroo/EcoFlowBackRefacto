package com.ecoflow.back.dto.output;

import com.ecoflow.back.models.marker.Tag;
import lombok.Builder;

@Builder
public record MarkerOutputDTO(
        float lat,
        float lon,
        String name,
        Tag tags,
        String address
) {
}
