package com.dqrapps.global.graphics;

import com.dqrapps.global.model.Planet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading, caching, and providing access to planet textures
 */
public class TextureManager {
    private static final Logger logger = LoggerFactory.getLogger(TextureManager.class);
    
    private final Map<Planet, BufferedImage> planetTextures = new HashMap<>();
    private BufferedImage starsTexture;
    private BufferedImage cachedSphereTexture;
    private Planet cachedPlanet;
    private long lastCacheTime = 0;
    
    /**
     * Load all planet textures
     */
    public void loadAllTextures() {
        // Load planet textures
        for (Planet planet : Planet.values()) {
            loadPlanetTexture(planet);
        }
        
        // Load stars background
        loadStarsTexture();
        
        logTextureStatus();
    }
    
    /**
     * Load texture for a specific planet
     */
    private void loadPlanetTexture(Planet planet) {
        try {
            InputStream stream = getClass().getClassLoader()
                    .getResourceAsStream(planet.getTextureFileName());
            if (stream != null) {
                BufferedImage texture = ImageIO.read(stream);
                planetTextures.put(planet, texture);
                logger.debug("Loaded texture for {}: {}x{}", 
                    planet.getDisplayName(), texture.getWidth(), texture.getHeight());
            } else {
                logger.warn("Could not find texture file: {}", planet.getTextureFileName());
            }
        } catch (IOException e) {
            logger.error("Failed to load texture for {}: {}", planet.getDisplayName(), e.getMessage());
        }
    }
    
    /**
     * Load stars background texture
     */
    private void loadStarsTexture() {
        try {
            InputStream stream = getClass().getClassLoader()
                    .getResourceAsStream("stars.jpg");
            if (stream != null) {
                starsTexture = ImageIO.read(stream);
                logger.debug("Loaded stars texture: {}x{}", 
                    starsTexture.getWidth(), starsTexture.getHeight());
            } else {
                logger.warn("Could not find stars texture file");
            }
        } catch (IOException e) {
            logger.error("Failed to load stars texture: {}", e.getMessage());
        }
    }
    
    /**
     * Get texture for a specific planet
     */
    public BufferedImage getPlanetTexture(Planet planet) {
        return planetTextures.get(planet);
    }
    
    /**
     * Get stars background texture
     */
    public BufferedImage getStarsTexture() {
        return starsTexture;
    }
    
    /**
     * Check if all textures loaded successfully
     */
    public boolean allTexturesLoaded() {
        if (starsTexture == null) return false;
        
        for (Planet planet : Planet.values()) {
            if (planetTextures.get(planet) == null) return false;
        }
        return true;
    }
    
    /**
     * Cache management for sphere textures
     */
    public BufferedImage getCachedSphereTexture() {
        return cachedSphereTexture;
    }
    
    public void setCachedSphereTexture(BufferedImage texture, Planet planet) {
        this.cachedSphereTexture = texture;
        this.cachedPlanet = planet;
        this.lastCacheTime = System.currentTimeMillis();
    }
    
    public boolean canUseCachedTexture(Planet planet) {
        long currentTime = System.currentTimeMillis();
        return cachedSphereTexture != null && 
               planet.equals(cachedPlanet) && 
               (currentTime - lastCacheTime) < 50;
    }
    
    /**
     * Clear all cached data
     */
    public void clearCache() {
        cachedSphereTexture = null;
        cachedPlanet = null;
        lastCacheTime = 0;
    }
    
    /**
     * Log the status of loaded textures
     */
    private void logTextureStatus() {
        StringBuilder status = new StringBuilder("Planet textures loaded: ");
        for (Planet planet : Planet.values()) {
            status.append(planet.getDisplayName()).append(": ")
                  .append(planetTextures.get(planet) != null).append(", ");
        }
        status.append("Stars: ").append(starsTexture != null);
        
        logger.info(status.toString());
    }
}