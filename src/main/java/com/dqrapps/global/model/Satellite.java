package com.dqrapps.global.model;

import java.awt.*;

/**
 * Represents a satellite orbiting around the planet
 */
public class Satellite {
    private double angle;
    private double orbitRadius;
    private double speed;
    private Color color;
    private int size;
    
    public Satellite(double initialAngle, double orbitRadius, double speed, Color color, int size) {
        this.angle = initialAngle;
        this.orbitRadius = orbitRadius;
        this.speed = speed;
        this.color = color;
        this.size = size;
    }
    
    /**
     * Update satellite position
     */
    public void update(double deltaTime) {
        angle += speed * deltaTime;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
    }
    
    /**
     * Get satellite position relative to center
     */
    public Point getPosition(int centerX, int centerY) {
        int x = (int) (centerX + Math.cos(angle) * orbitRadius);
        int y = (int) (centerY + Math.sin(angle) * orbitRadius);
        return new Point(x, y);
    }
    
    /**
     * Draw the satellite
     */
    public void draw(Graphics2D g2d, int centerX, int centerY) {
        Point pos = getPosition(centerX, centerY);
        
        // Draw satellite
        g2d.setColor(color);
        g2d.fillOval(pos.x - size/2, pos.y - size/2, size, size);
        
        // Draw satellite glow
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        g2d.fillOval(pos.x - size, pos.y - size, size * 2, size * 2);
    }
    
    /**
     * Draw orbit path
     */
    public void drawOrbit(Graphics2D g2d, int centerX, int centerY) {
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                     0, new float[]{2, 4}, 0));
        g2d.setColor(new Color(100, 100, 100, 100));
        int diameter = (int)(orbitRadius * 2);
        g2d.drawOval(centerX - (int)orbitRadius, centerY - (int)orbitRadius, diameter, diameter);
    }
    
    // Getters
    public double getAngle() { return angle; }
    public double getOrbitRadius() { return orbitRadius; }
    public double getSpeed() { return speed; }
    public Color getColor() { return color; }
    public int getSize() { return size; }
    
    // Setters
    public void setAngle(double angle) { this.angle = angle; }
    public void setOrbitRadius(double orbitRadius) { this.orbitRadius = orbitRadius; }
    public void setSpeed(double speed) { this.speed = speed; }
    public void setColor(Color color) { this.color = color; }
    public void setSize(int size) { this.size = size; }
}