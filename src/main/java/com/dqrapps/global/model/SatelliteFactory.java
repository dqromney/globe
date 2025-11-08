package com.dqrapps.global.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory class for creating satellites
 */
public class SatelliteFactory {
    private static final Random random = new Random();
    private static final Color[] SATELLITE_COLORS = {
        Color.WHITE, Color.CYAN, Color.YELLOW, Color.ORANGE, 
        Color.PINK, Color.LIGHT_GRAY, Color.GREEN, Color.MAGENTA
    };
    
    /**
     * Create a list of satellites with random properties
     */
    public static List<Satellite> createSatellites(int count, int minRadius, int maxRadius) {
        List<Satellite> satellites = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = minRadius + random.nextDouble() * (maxRadius - minRadius);
            double speed = 0.01 + random.nextDouble() * 0.02; // Varying speeds
            Color color = SATELLITE_COLORS[random.nextInt(SATELLITE_COLORS.length)];
            int size = 3 + random.nextInt(4); // Size between 3-6
            
            satellites.add(new Satellite(angle, radius, speed, color, size));
        }
        
        return satellites;
    }
    
    /**
     * Create satellites with specific configuration
     */
    public static List<Satellite> createSatellites(int count, int baseRadius) {
        return createSatellites(count, baseRadius + 20, baseRadius + 80);
    }
}