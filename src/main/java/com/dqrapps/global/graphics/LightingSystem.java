package com.dqrapps.global.graphics;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Handles all lighting calculations and rendering for the globe visualization
 */
public class LightingSystem {
    private boolean showLightSource = true;
    private boolean showTerminator = true;
    private double lightAngle = Math.PI / 4; // 45 degrees default
    
    /**
     * Calculate lighting intensity for a given position on the sphere
     */
    public double calculateLightingIntensity(double longitude, double latitude) {
        // Light source position in 3D space (fixed position from the right side)
        double lightX = Math.cos(lightAngle);
        double lightY = Math.sin(lightAngle);
        double lightZ = 0.5; // Light slightly forward for better illumination
        
        // Calculate 3D surface normal at the given longitude/latitude
        double cosLat = Math.cos(latitude);
        double sinLat = Math.sin(latitude);
        double cosLon = Math.cos(longitude);
        double sinLon = Math.sin(longitude);
        
        // Surface normal in 3D space (pointing outward from sphere)
        double normalX = cosLat * cosLon;
        double normalY = -sinLat;  // Negative for proper Earth orientation
        double normalZ = cosLat * sinLon;
        
        // Normalize light vector
        double lightLength = Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
        lightX /= lightLength;
        lightY /= lightLength;
        lightZ /= lightLength;
        
        // Calculate dot product for lighting intensity (3D)
        double intensity = normalX * lightX + normalY * lightY + normalZ * lightZ;
        
        // Apply smooth lighting with good ambient component
        intensity = Math.max(0.0, intensity); // Remove negative values
        return Math.max(0.25, intensity * 0.75 + 0.25); // 25% ambient, 75% directional
    }
    
    /**
     * Apply lighting to a color value
     */
    public int applyLighting(int originalColor, double lightingIntensity) {
        int red = (originalColor >> 16) & 0xFF;
        int green = (originalColor >> 8) & 0xFF;
        int blue = originalColor & 0xFF;
        int alpha = (originalColor >> 24) & 0xFF;
        
        // Apply lighting multiplier
        red = (int) Math.min(255, red * lightingIntensity);
        green = (int) Math.min(255, green * lightingIntensity);
        blue = (int) Math.min(255, blue * lightingIntensity);
        
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    /**
     * Draw the light source visualization
     */
    public void drawLightSource(Graphics2D g2d, int centerX, int centerY, int radius, double animationTime) {
        if (!showLightSource) return;
        
        // Calculate light position
        int lightDistance = radius + 80;
        int lightX = (int) (centerX + Math.cos(lightAngle) * lightDistance);
        int lightY = (int) (centerY + Math.sin(lightAngle) * lightDistance);
        
        // Draw light source with gradient
        RadialGradientPaint lightGradient = new RadialGradientPaint(
            lightX, lightY, 15,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{new Color(255, 255, 200, 200), new Color(255, 255, 100, 100), new Color(255, 255, 0, 0)}
        );
        g2d.setPaint(lightGradient);
        g2d.fillOval(lightX - 15, lightY - 15, 30, 30);
        
        // Draw animated light rays
        g2d.setColor(new Color(255, 255, 150, 80));
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < 8; i++) {
            double rayAngle = (animationTime + i * Math.PI / 4) % (2 * Math.PI);
            int rayLength = 25 + (int)(5 * Math.sin(animationTime * 3 + i));
            int rayX1 = (int) (lightX + Math.cos(rayAngle) * 20);
            int rayY1 = (int) (lightY + Math.sin(rayAngle) * 20);
            int rayX2 = (int) (lightX + Math.cos(rayAngle) * (20 + rayLength));
            int rayY2 = (int) (lightY + Math.sin(rayAngle) * (20 + rayLength));
            g2d.drawLine(rayX1, rayY1, rayX2, rayY2);
        }
    }
    
    /**
     * Draw the day/night terminator line
     */
    public void drawTerminator(Graphics2D g2d, int centerX, int centerY, int radius) {
        if (!showTerminator) return;
        
        // Light direction vector (normalized)
        double lightX = Math.cos(lightAngle);
        double lightY = Math.sin(lightAngle);
        double lightZ = 0.5;
        double lightLength = Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
        lightX /= lightLength;
        lightY /= lightLength;
        lightZ /= lightLength;
        
        // Calculate terminator line based on 3D lighting
        Path2D terminatorPath = new Path2D.Double();
        boolean first = true;
        
        for (double angle = 0; angle < 2 * Math.PI; angle += 0.05) {
            // Point on sphere edge in 2D view
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            
            // Calculate corresponding 3D surface normal (assuming viewing from front)
            double normalX = x;
            double normalY = -y;  // Negative for proper Earth orientation
            double normalZ = 0;   // On the visible edge, Z component is 0
            
            // Check if this point is near the terminator (where dot product ≈ 0)
            double lightDot = normalX * lightX + normalY * lightY + normalZ * lightZ;
            if (Math.abs(lightDot) < 0.1) { // Near the terminator
                int screenX = (int) (centerX + x * radius);
                int screenY = (int) (centerY + y * radius);
                
                if (first) {
                    terminatorPath.moveTo(screenX, screenY);
                    first = false;
                } else {
                    terminatorPath.lineTo(screenX, screenY);
                }
            }
        }
        
        // Draw terminator line
        g2d.setColor(new Color(255, 255, 0, 150));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(terminatorPath);
    }
    
    // Getters and setters
    public boolean isShowLightSource() {
        return showLightSource;
    }
    
    public void setShowLightSource(boolean showLightSource) {
        this.showLightSource = showLightSource;
    }
    
    public boolean isShowTerminator() {
        return showTerminator;
    }
    
    public void setShowTerminator(boolean showTerminator) {
        this.showTerminator = showTerminator;
    }
    
    public double getLightAngle() {
        return lightAngle;
    }
    
    public void setLightAngle(double lightAngle) {
        this.lightAngle = lightAngle;
    }
}