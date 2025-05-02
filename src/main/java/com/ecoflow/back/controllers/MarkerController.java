package com.ecoflow.back.controllers;

import com.ecoflow.back.dto.input.MarkerInputDTO;
import com.ecoflow.back.dto.output.MarkerOutputDTO;
import com.ecoflow.back.models.marker.Marker;
import com.ecoflow.back.models.marker.Tag;
import com.ecoflow.back.services.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController("/api/markers")
@RequiredArgsConstructor
public class MarkerController {

    @Autowired
    private final MarkerService markerService;

    @GetMapping("/retrieve")
    @ResponseBody
    public ResponseEntity<List<MarkerOutputDTO>> retrieveMarkersOrderedByCoordinates(MarkerInputDTO input) {

        List<MarkerOutputDTO> markerOutputDTOList = new ArrayList<>();

        if (input == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        List<Marker> markers = markerService.fetchMarkersFromCoord(input.lat(), input.lon(), input.amenities(), input.recyclingFilters());

        if (markers.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        List<Map<String, Object>> sortedMarkers = markerService.orderMarkerByProximity(
                markers,
                input.lat(),
                input.lon());

        for (Map<String, Object> marker : sortedMarkers) {
            MarkerOutputDTO markerOutputDTO = new MarkerOutputDTO(
                    (long) marker.get("lat"),
                    (long) marker.get("lon"),
                    (String) marker.get("name"),
                    (Tag) marker.get("tags"),
                    (String) marker.get("address")
            );
            markerOutputDTOList.add(markerOutputDTO);
        }

        return new ResponseEntity<>(markerOutputDTOList, HttpStatus.OK);
    }

}
