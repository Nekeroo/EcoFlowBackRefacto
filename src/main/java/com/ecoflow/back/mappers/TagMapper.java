package com.ecoflow.back.mappers;

public class TagMapper {

    public static String mapToTag(String amenities, String recyclingFilters) {
        return amenities + "," + recyclingFilters;
    }

    public static String mapToTag(String[] amenities, String[] recyclingFilters) {
        return String.join(",", amenities) + "," + String.join(",", recyclingFilters);
    }
}
