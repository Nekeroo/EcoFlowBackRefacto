package com.ecoflow.back.mappers;

import com.ecoflow.back.dto.input.MarkerInputDTO;
import com.ecoflow.back.dto.output.MarkerOutputDTO;
import com.ecoflow.back.models.enums.AmenityLabelEnum;
import com.ecoflow.back.models.marker.Marker;
import com.ecoflow.back.models.marker.Tag;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MarkerMapper {

    public Marker mapDataAsMarker(JsonNode node, ObjectMapper mapper) {
        // Récupération des tags
        JsonNode tagsNode = node.path("tags");
        Map<String, String> tags = mapper.convertValue(tagsNode, Map.class);

        // Récupération du type d'amenity ou chaîne vide par défaut
        String amenityType = tags.getOrDefault("amenity", "");

        // Filtrage optionnel des types de recyclage (similaire à recyclingData en TS)
        List<String> recyclingData = tags.entrySet().stream()
                .filter(e -> e.getKey().startsWith("recycling:") && "yes".equals(e.getValue()))
                .map(e -> e.getKey().replace("recycling:", ""))
                .toList();
        // Vous pouvez utiliser recyclingData si besoin

        // Détermination du nom à afficher
        String name = tags.get("name");
        String amenityLabel = AmenityLabelEnum.getLabelByCode(amenityType);
        if (name == null || name.isEmpty()) {
            name = (amenityLabel != null ? amenityLabel : "Lieu de recyclage");
        }

        // Création d'un objet OSMNode
        Marker osmNode = new Marker();
        osmNode.setId(node.path("id").asLong());
        osmNode.setLat(node.path("lat").asDouble());
        osmNode.setLon(node.path("lon").asDouble());
        osmNode.setName(name);

        // Ajout des tags (ici, on crée une map simplifiée)
        Tag tag = new Tag(amenityType, tags.get("name") != null ? tags.get("name") : amenityLabel, tags.get("recycling_type"));
        osmNode.setTags(tag);

        return new Marker(node.path("id").asInt(), node.path("lat").asDouble(), node.path("lon").asDouble(), name, tag,"" );
    }

}
