package com.dqrapps.global.model;

/**
 * Represents different planets with their properties and characteristics
 */
public enum Planet {
    EARTH("Earth", "earth_daymap.jpg", 1.0, 0.8, 0.6, 0.4),
    MARS("Mars", "mars.jpg", 0.8, 0.4, 0.2, 0.0),
    JUPITER("Jupiter", "jupiter.jpg", 1.2, 1.0, 0.8, 0.6),
    VENUS("Venus", "venus.jpg", 0.9, 0.7, 0.5, 0.3),
    MERCURY("Mercury", "mercury.jpg", 0.7, 0.5, 0.3, 0.1),
    SATURN("Saturn", "saturn.jpg", 1.1, 0.9, 0.7, 0.5),
    NEPTUNE("Neptune", "neptune.jpg", 1.0, 0.6, 0.8, 0.9);

    private final String displayName;
    private final String textureFileName;
    private final double baseReflectivity;
    private final double redComponent;
    private final double greenComponent;
    private final double blueComponent;

    Planet(String displayName, String textureFileName, double baseReflectivity, 
           double red, double green, double blue) {
        this.displayName = displayName;
        this.textureFileName = textureFileName;
        this.baseReflectivity = baseReflectivity;
        this.redComponent = red;
        this.greenComponent = green;
        this.blueComponent = blue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTextureFileName() {
        return textureFileName;
    }

    public double getBaseReflectivity() {
        return baseReflectivity;
    }

    public double getRedComponent() {
        return redComponent;
    }

    public double getGreenComponent() {
        return greenComponent;
    }

    public double getBlueComponent() {
        return blueComponent;
    }

    /**
     * Get planet by display name
     */
    public static Planet fromDisplayName(String name) {
        for (Planet planet : values()) {
            if (planet.displayName.equals(name)) {
                return planet;
            }
        }
        return EARTH; // Default fallback
    }

    /**
     * Get all planet display names for UI components
     */
    public static String[] getAllDisplayNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].displayName;
        }
        return names;
    }
}