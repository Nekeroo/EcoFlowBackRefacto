package com.ecoflow.back.dto.input;

import lombok.Builder;

import java.util.List;

@Builder
public record MarkerInputDTO (
        float lat,
        float lon,

        List<String> amenities,

        List<String> recyclingFilters

){
}
