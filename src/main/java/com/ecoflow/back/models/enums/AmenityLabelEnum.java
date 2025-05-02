package com.ecoflow.back.models.enums;

public enum AmenityLabelEnum {
    RECYCLING("recycling", "Point de recyclage"),
    WASTE_BASKET("waste_basket", "Poubelle publique"),
    WASTE_DISPOSAL("waste_disposal", "Centre de tri des déchets");

    private final String code;
    private final String label;

    AmenityLabelEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Retourne le label associé au code, ou null si non trouvé.
     *
     * @param code le code de l'amenity
     * @return le label correspondant ou null
     */
    public static String getLabelByCode(String code) {
        for (AmenityLabelEnum amenity : AmenityLabelEnum.values()) {
            if (amenity.getCode().equals(code)) {
                return amenity.getLabel();
            }
        }
        return null;
    }
}
