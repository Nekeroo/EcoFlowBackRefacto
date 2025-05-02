package com.ecoflow.back.services;

import com.ecoflow.back.mappers.MarkerMapper;
import com.ecoflow.back.models.marker.Marker;
import com.ecoflow.back.utils.MarkerUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MarkerService {

    @Autowired
    private MarkerMapper markerMapper;

    public List<Marker> fetchMarkersFromCoord(float lat, float lon, List<String> amenities, List<String> recyclingFilters) {
        if (amenities == null || amenities.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String overpassQuery = buildOverpassQuery(amenities, recyclingFilters, lat, lon);
            String responseJson = executeOverpassRequest(overpassQuery);
            return parseMarkersFromJson(responseJson);
        } catch (Exception e) {
            // Prefer logging instead of printing to stdout
            System.err.println("Error fetching markers: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private String buildOverpassQuery(List<String> amenities, List<String> recyclingFilters, float lat, float lon) {
        return this.castUriFromFitlers(amenities, recyclingFilters, lat, lon);
    }

    private String executeOverpassRequest(String query) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String uri = "https://overpass-api.de/api/interpreter?data=" + encodedQuery;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private List<Marker> parseMarkersFromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode elements = root.path("elements");

        List<Marker> markers = new ArrayList<>();
        if (elements.isArray()) {
            for (JsonNode node : elements) {
                Marker marker = markerMapper.mapDataAsMarker(node, mapper);
                markers.add(marker);
            }
        }
        return markers;
    }

    public String castUriFromFitlers(List<String> amenities, List<String> recyclingFilters, double lat, double lon) {
        List<String> amenitiesQueries = amenities.stream().map(
                        amenity -> String.format("node[\"amenity\"=\"%s\"](around:%s,%s,%s);", amenity, 1000, lat, lon))
                .toList();

        List<String> recyclingQueries =
                !recyclingFilters.isEmpty() ? recyclingFilters.stream().map(recyclingType -> String.format("node[\"recycling:%s\"=\"yes\"](around:%s,%s,%s);", recyclingType, 1000, lat, lon))
                        .toList() : new ArrayList<>();

        List<String> combinedQueries = Stream.concat(amenitiesQueries.stream(), recyclingQueries.stream())
                .collect(Collectors.toList());

        // Joindre les requêtes avec un saut de ligne
        String joinedQueries = String.join("\n", combinedQueries);

        // Composer la requête finale
        String overpassQuery = String.format(
                "[out:json];\n(\n%s\n);\nout body;",
                joinedQueries
        );

        return overpassQuery;
    }

    public List<Map<String, Object>> orderMarkerByProximity(List<Marker> markers, float latUser, float lonUser) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Marker marker : markers) {
            double distance = MarkerUtil.distance(latUser, lonUser, marker.getLat(), marker.getLon());
            // Arrondi à deux chiffres après la virgule
            double roundedDistance = Math.round(distance * 100.0) / 100.0;
            Map<String, Object> entry = new HashMap<>();
            entry.put("distance", roundedDistance);
            entry.put("marker", marker);
            result.add(entry);
        }

        // Tri de la liste par distance croissante
        result.sort(Comparator.comparingDouble(map -> (Double) map.get("distance")));
        return result;
    }


}
