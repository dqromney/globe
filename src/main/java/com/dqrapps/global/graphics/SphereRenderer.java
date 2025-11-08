package com.dqrapps.global.graphics;

import com.dqrapps.global.model.Planet;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles sphere mapping and rendering of planet textures onto a 2D sphere
 */
public class SphereRenderer {
    private final TextureManager textureManager;
    private final LightingSystem lightingSystem;
    
    public SphereRenderer(TextureManager textureManager, LightingSystem lightingSystem) {
        this.textureManager = textureManager;
        this.lightingSystem = lightingSystem;
    }
    
    /**
     * Draw a planet with sphere-mapped texture and lighting
     */
    public void drawPlanet(Graphics2D g2d, int centerX, int centerY, int radius, 
                          Planet planet, double earthRotation) {
        BufferedImage texture = textureManager.getPlanetTexture(planet);
        if (texture == null) {
            // Add debug info
            System.out.println("Texture not found for " + planet.getDisplayName() + ", using fallback");
            drawFallbackPlanet(g2d, centerX, centerY, radius, planet);
            return;
        }
        
        drawSphereMappedTexture(g2d, centerX, centerY, radius, texture, earthRotation, planet);
        drawPlanetOutline(g2d, centerX, centerY, radius);
    }
    
    /**
     * Draw sphere-mapped texture with proper spherical projection
     */
    private void drawSphereMappedTexture(Graphics2D g2d, int centerX, int centerY, int radius, 
                                       BufferedImage texture, double earthRotation, Planet planet) {
        // Use time-based caching for smooth performance
        if (textureManager.canUseCachedTexture(planet)) {
            BufferedImage cached = textureManager.getCachedSphereTexture();
            if (cached != null) {
                g2d.drawImage(cached, centerX - radius, centerY - radius, null);
                return;
            }
        }
        
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        int sphereSize = radius * 2;
        
        // Create sphere image with proper alpha
        BufferedImage sphereImage = new BufferedImage(sphereSize, sphereSize, BufferedImage.TYPE_INT_ARGB);
        
        // Render each pixel of the sphere
        for (int y = 0; y < sphereSize; y++) {
            for (int x = 0; x < sphereSize; x++) {
                // Convert screen coordinates to sphere coordinates
                double sx = (x - radius) / (double) radius;
                double sy = (y - radius) / (double) radius;
                double distance = sx * sx + sy * sy;
                
                if (distance <= 1.0) { // Inside the sphere
                    // Calculate 3D coordinates on sphere (proper Earth orientation)
                    double sz = Math.sqrt(1.0 - distance);
                    
                    // Convert to spherical coordinates (Earth side view)
                    // For proper Earth orientation: longitude from left-right, latitude from top-bottom
                    double longitude = Math.atan2(sz, sx); // Use depth and x for longitude
                    double latitude = Math.asin(-sy);       // Use -y for latitude (flip Y axis)
                    
                    // Apply rotation
                    longitude += earthRotation;
                    
                    // Convert to texture coordinates
                    double u = (longitude + Math.PI) / (2 * Math.PI);
                    double v = (Math.PI / 2 - latitude) / Math.PI;
                    
                    // Wrap texture coordinates
                    u = u - Math.floor(u);
                    v = Math.max(0, Math.min(1, v));
                    
                    // Get texture pixel with bilinear interpolation
                    int color = getInterpolatedPixel(texture, u * textureWidth, v * textureHeight);
                    
                    // Enhance texture contrast and brightness before lighting
                    color = enhanceTextureColor(color);
                    
                    // Apply lighting
                    double lightingIntensity = lightingSystem.calculateLightingIntensity(longitude, latitude);
                    color = lightingSystem.applyLighting(color, lightingIntensity);
                    
                    // Add subtle fade towards edges for sphere effect (reduced transparency)
                    double edgeFade = 1.0 - Math.pow(distance, 1.5); // Less aggressive fade
                    int alpha = (int) (255 * Math.max(0.7, edgeFade)); // Minimum 70% opacity
                    color = (alpha << 24) | (color & 0xFFFFFF);
                    
                    sphereImage.setRGB(x, y, color);
                }
            }
        }
        
        // Cache the result
        textureManager.setCachedSphereTexture(sphereImage, planet);
        
        // Draw the sphere-mapped texture
        g2d.drawImage(sphereImage, centerX - radius, centerY - radius, null);
    }
    
    /**
     * Enhance texture color for better visibility
     */
    private int enhanceTextureColor(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int alpha = (color >> 24) & 0xFF;
        
        // Enhance contrast and brightness
        red = Math.min(255, (int)(red * 1.2 + 20));    // Increase brightness
        green = Math.min(255, (int)(green * 1.2 + 20));
        blue = Math.min(255, (int)(blue * 1.2 + 20));
        
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    /**
     * Get interpolated pixel value using bilinear interpolation
     */
    private int getInterpolatedPixel(BufferedImage texture, double x, double y) {
        int width = texture.getWidth();
        int height = texture.getHeight();
        
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        int x2 = (x1 + 1) % width;
        int y2 = Math.min(y1 + 1, height - 1);
        
        double fx = x - x1;
        double fy = y - y1;
        
        // Ensure coordinates are within bounds
        x1 = Math.max(0, Math.min(x1, width - 1));
        y1 = Math.max(0, Math.min(y1, height - 1));
        
        int c1 = texture.getRGB(x1, y1);
        int c2 = texture.getRGB(x2, y1);
        int c3 = texture.getRGB(x1, y2);
        int c4 = texture.getRGB(x2, y2);
        
        // Interpolate each color component
        int red = (int) (
            ((c1 >> 16) & 0xFF) * (1 - fx) * (1 - fy) +
            ((c2 >> 16) & 0xFF) * fx * (1 - fy) +
            ((c3 >> 16) & 0xFF) * (1 - fx) * fy +
            ((c4 >> 16) & 0xFF) * fx * fy
        );
        
        int green = (int) (
            ((c1 >> 8) & 0xFF) * (1 - fx) * (1 - fy) +
            ((c2 >> 8) & 0xFF) * fx * (1 - fy) +
            ((c3 >> 8) & 0xFF) * (1 - fx) * fy +
            ((c4 >> 8) & 0xFF) * fx * fy
        );
        
        int blue = (int) (
            (c1 & 0xFF) * (1 - fx) * (1 - fy) +
            (c2 & 0xFF) * fx * (1 - fy) +
            (c3 & 0xFF) * (1 - fx) * fy +
            (c4 & 0xFF) * fx * fy
        );
        
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }
    
    /**
     * Draw planet outline
     */
    private void drawPlanetOutline(Graphics2D g2d, int centerX, int centerY, int radius) {
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(100, 100, 100, 150));
        g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }
    
    /**
     * Draw fallback planet when texture is not available
     */
    private void drawFallbackPlanet(Graphics2D g2d, int centerX, int centerY, int radius, Planet planet) {
        // Create bright, visible gradient based on planet colors
        Color planetColor = new Color(
            Math.min(255, (int)(planet.getRedComponent() * 255 * 1.5)),   // Brighter colors
            Math.min(255, (int)(planet.getGreenComponent() * 255 * 1.5)),
            Math.min(255, (int)(planet.getBlueComponent() * 255 * 1.5))
        );
        
        // Much more visible gradient with better contrast
        RadialGradientPaint gradient = new RadialGradientPaint(
            centerX - radius/3, centerY - radius/3, radius * 1.2f,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 200),  // Bright center
                planetColor, 
                planetColor.darker().darker()   // Darker edge
            }
        );
        
        g2d.setPaint(gradient);
        g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        drawPlanetOutline(g2d, centerX, centerY, radius);
    }
    
    /**
     * Draw stars background
     */
    public void drawStars(Graphics2D g2d, int width, int height) {
        BufferedImage starsTexture = textureManager.getStarsTexture();
        if (starsTexture != null) {
            // Tile the stars texture to fill the background
            int texWidth = starsTexture.getWidth();
            int texHeight = starsTexture.getHeight();
            
            for (int x = 0; x < width; x += texWidth) {
                for (int y = 0; y < height; y += texHeight) {
                    g2d.drawImage(starsTexture, x, y, null);
                }
            }
        }
    }
}