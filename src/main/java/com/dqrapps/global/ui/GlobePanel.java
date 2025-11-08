package com.dqrapps.global.ui;

import com.dqrapps.global.graphics.LightingSystem;
import com.dqrapps.global.graphics.SphereRenderer;
import com.dqrapps.global.graphics.TextureManager;
import com.dqrapps.global.model.Planet;
import com.dqrapps.global.model.Satellite;
import com.dqrapps.global.model.SatelliteFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

/**
 * Main panel for displaying the globe visualization
 * Uses composition pattern with specialized classes for different concerns
 */
public class GlobePanel extends JPanel {
    // Core systems
    private final TextureManager textureManager;
    private final LightingSystem lightingSystem;
    private final SphereRenderer sphereRenderer;
    
    // Animation and state
    private double animationTime = 0;
    private double earthRotation = 0;
    private double animationSpeed = 1.0;
    private Planet currentPlanet = Planet.EARTH;
    
    // Satellites
    private List<Satellite> satellites;
    private boolean showOrbits = true;
    private int satelliteCount = 8;
    
    public GlobePanel() {
        setBackground(Color.BLACK);
        
        // Initialize systems
        textureManager = new TextureManager();
        lightingSystem = new LightingSystem();
        sphereRenderer = new SphereRenderer(textureManager, lightingSystem);
        
        // Load textures
        textureManager.loadAllTextures();
        
        // Create satellites
        satellites = SatelliteFactory.createSatellites(satelliteCount, 150);
        
        setupMouseInteraction();
        
        // Enable anti-aliasing
        setDoubleBuffered(true);
    }
    
    /**
     * Setup mouse interaction for rotation control
     */
    private void setupMouseInteraction() {
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Mouse position affects rotation speed
                int mouseX = e.getX() - getWidth() / 2;
                earthRotation += mouseX * 0.01;
                textureManager.clearCache(); // Clear cache when manually rotating
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // No action needed
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 4;
        
        // Draw background stars
        sphereRenderer.drawStars(g2d, width, height);
        
        // Draw satellites and orbits
        if (showOrbits) {
            drawSatelliteOrbits(g2d, centerX, centerY);
        }
        drawSatellites(g2d, centerX, centerY);
        
        // Draw main planet
        sphereRenderer.drawPlanet(g2d, centerX, centerY, radius, currentPlanet, earthRotation);
        
        // Draw lighting effects
        lightingSystem.drawLightSource(g2d, centerX, centerY, radius, animationTime);
        lightingSystem.drawTerminator(g2d, centerX, centerY, radius);
        
        // Draw UI info
        drawInfo(g2d);
        
        // Update animation
        updateAnimation();
        
        g2d.dispose();
    }
    
    /**
     * Draw satellites
     */
    private void drawSatellites(Graphics2D g2d, int centerX, int centerY) {
        for (Satellite satellite : satellites) {
            satellite.update(0.02 * animationSpeed);
            satellite.draw(g2d, centerX, centerY);
        }
    }
    
    /**
     * Draw satellite orbits
     */
    private void drawSatelliteOrbits(Graphics2D g2d, int centerX, int centerY) {
        for (Satellite satellite : satellites) {
            satellite.drawOrbit(g2d, centerX, centerY);
        }
    }
    
    /**
     * Update animation parameters
     */
    private void updateAnimation() {
        animationTime += 0.02 * animationSpeed;
        // Smoother rotation with higher precision (60 FPS friendly)
        earthRotation += 0.003 * animationSpeed;
    }
    
    /**
     * Draw UI information
     */
    private void drawInfo(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Planet: " + currentPlanet.getDisplayName(), 10, 30);
        g2d.drawString("Satellites: " + satellites.size(), 10, 50);
        g2d.drawString("Speed: " + String.format("%.1fx", animationSpeed), 10, 70);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Move mouse to control rotation", 10, getHeight() - 20);
    }
    
    // Public control methods
    public void setAnimationSpeed(double speed) {
        this.animationSpeed = speed;
    }
    
    public void setPlanet(Planet planet) {
        if (!planet.equals(this.currentPlanet)) {
            this.currentPlanet = planet;
            textureManager.clearCache(); // Clear cache when changing planets
        }
    }
    
    public void setPlanet(String planetName) {
        setPlanet(Planet.fromDisplayName(planetName));
    }
    
    public void setSatelliteCount(int count) {
        this.satelliteCount = Math.max(0, Math.min(count, 20));
        this.satellites = SatelliteFactory.createSatellites(this.satelliteCount, 150);
    }
    
    public void setShowOrbits(boolean showOrbits) {
        this.showOrbits = showOrbits;
    }
    
    public void setShowLightSource(boolean show) {
        lightingSystem.setShowLightSource(show);
        textureManager.clearCache(); // Clear cache when lighting changes
    }
    
    public void setShowTerminator(boolean show) {
        lightingSystem.setShowTerminator(show);
    }
    
    public void setLightAngle(double angle) {
        lightingSystem.setLightAngle(angle);
        textureManager.clearCache(); // Clear cache when light angle changes
    }
    
    public void reset() {
        animationTime = 0;
        earthRotation = 0;
        animationSpeed = 1.0;
        currentPlanet = Planet.EARTH;
        showOrbits = true;
        satelliteCount = 8;
        satellites = SatelliteFactory.createSatellites(satelliteCount, 150);
        
        // Reset lighting system
        lightingSystem.setShowLightSource(true);
        lightingSystem.setShowTerminator(true);
        lightingSystem.setLightAngle(Math.PI / 4);
        
        // Clear cache on reset
        textureManager.clearCache();
    }
    
    // Getters
    public boolean areTexturesLoaded() {
        return textureManager.allTexturesLoaded();
    }
    
    public Planet getCurrentPlanet() {
        return currentPlanet;
    }
    
    public double getAnimationSpeed() {
        return animationSpeed;
    }
    
    public int getSatelliteCount() {
        return satelliteCount;
    }
    
    public boolean isShowOrbits() {
        return showOrbits;
    }
    
    public TextureManager getTextureManager() {
        return textureManager;
    }
}