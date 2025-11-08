package com.dqrapps.global;

import com.dqrapps.global.model.Planet;
import com.dqrapps.global.ui.GlobePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SpringBootApplication
public class GlobalApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(GlobalApplication.class);

    public static void main(String[] args) {
        // Set system properties for headless mode compatibility
        System.setProperty("java.awt.headless", "false");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SpringApplication.run(GlobalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Globe 2D Visualization Application...");
        
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        try {
            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel: {}", e.getMessage());
        }

        // Create main frame
        JFrame frame = new JFrame("Enhanced Globe 3D Visualization - Refactored");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        // Create globe panel
        GlobePanel globePanel = new GlobePanel();
        
        // Create control panel
        JPanel controlPanel = createControlPanel(globePanel);
        
        // Layout
        frame.setLayout(new BorderLayout());
        frame.add(globePanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.EAST);

        // Start animation timer (16ms = 60 FPS for ultra-smooth rotation)
        Timer timer = new Timer(16, e -> globePanel.repaint());
        timer.start();

        // Show frame
        frame.setVisible(true);
        frame.setAlwaysOnTop(true); // Bring to front initially
        frame.setAlwaysOnTop(false); // Remove always on top after showing
        frame.toFront(); // Bring to front
        frame.requestFocus(); // Request focus

        logger.info("Enhanced Globe 3D Visualization window created successfully");
        logger.info("Look for a window titled 'Enhanced Globe 3D Visualization - Refactored' with rotating Earth and interactive controls");
        logger.info("If you don't see it, check your taskbar or use Alt+Tab to cycle through windows");
    }

    private JPanel createControlPanel(GlobePanel globePanel) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.DARK_GRAY);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setPreferredSize(new Dimension(200, 0));

        // Animation speed control
        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setForeground(Color.WHITE);
        JSlider speedSlider = new JSlider(1, 50, 10);
        speedSlider.setBackground(Color.DARK_GRAY);
        speedSlider.setForeground(Color.WHITE);
        speedSlider.addChangeListener(e -> globePanel.setAnimationSpeed(speedSlider.getValue() / 10.0));

        // Satellite count control
        JLabel satLabel = new JLabel("Satellites:");
        satLabel.setForeground(Color.WHITE);
        JSlider satSlider = new JSlider(0, 20, 8);
        satSlider.setBackground(Color.DARK_GRAY);
        satSlider.setForeground(Color.WHITE);
        satSlider.addChangeListener(e -> globePanel.setSatelliteCount(satSlider.getValue()));

        // Planet selection
        JLabel planetLabel = new JLabel("Planet:");
        planetLabel.setForeground(Color.WHITE);
        String[] planetNames = Planet.getAllDisplayNames();
        System.out.println("Planet names loaded: " + java.util.Arrays.toString(planetNames)); // Debug
        JComboBox<String> planetCombo = new JComboBox<>(planetNames);
        planetCombo.setMaximumRowCount(8);  // Show all planets at once
        planetCombo.setPreferredSize(new Dimension(185, 42)); // Exact size to match texture
        planetCombo.setMinimumSize(new Dimension(185, 42));   // Force exact size
        planetCombo.setMaximumSize(new Dimension(185, 42));   // Force exact size
        
        // Remove all default styling to let our renderer take full control
        planetCombo.setOpaque(false);
        planetCombo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No border at all
        planetCombo.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        
        // Custom renderer to show texture previews
        DefaultListCellRenderer textureRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                
                if (value != null) {
                    String planetName = value.toString();
                    Planet planet = Planet.fromDisplayName(planetName);
                    if (planet != null) {
                        BufferedImage texture = globePanel.getTextureManager().getPlanetTexture(planet);
                        if (texture != null) {
                            // Create a preview that exactly fills the entire combo box area
                            int previewWidth = 185; // Match combo box width exactly
                            int previewHeight = 42;  // Match combo box height exactly - fill completely
                            BufferedImage preview = new BufferedImage(previewWidth, previewHeight, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = preview.createGraphics();
                            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            
                            // Fill entire background with texture
                            g2d.drawImage(texture, 0, 0, previewWidth, previewHeight, null);
                            
                            // Add semi-transparent overlay for text readability
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                            if (isSelected || index == -1) { // index == -1 means this is the selected value display
                                g2d.setColor(new Color(0, 100, 200, 180));
                            } else {
                                g2d.setColor(new Color(0, 0, 0, 120));
                            }
                            g2d.fillRect(0, 0, previewWidth, previewHeight);
                            
                            // Add planet name text overlay - stretched to fill width
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                            g2d.setColor(Color.WHITE);
                            
                            // Calculate optimal font size to stretch text across width
                            Font baseFont = new Font("Arial", Font.BOLD, 12);
                            FontMetrics baseFm = g2d.getFontMetrics(baseFont);
                            int baseTextWidth = baseFm.stringWidth(planetName);
                            
                            // Scale font to fit most of the width (with some padding)
                            int targetWidth = (int)(previewWidth * 0.8); // Use 80% of available width
                            float scaleFactor = (float)targetWidth / baseTextWidth;
                            int newFontSize = Math.max(8, Math.min(24, (int)(12 * scaleFactor))); // Keep reasonable bounds
                            
                            Font stretchedFont = new Font("Arial", Font.BOLD, newFontSize);
                            g2d.setFont(stretchedFont);
                            FontMetrics fm = g2d.getFontMetrics();
                            
                            // Center the stretched text
                            int textX = (previewWidth - fm.stringWidth(planetName)) / 2;
                            int textY = (previewHeight + fm.getAscent()) / 2;
                            
                            // Add text shadow for better visibility
                            g2d.setColor(Color.BLACK);
                            g2d.drawString(planetName, textX + 1, textY + 1);
                            g2d.setColor(Color.WHITE);
                            g2d.drawString(planetName, textX, textY);
                            
                            g2d.dispose();
                            
                            // Set the icon as our rendered texture
                            setIcon(new ImageIcon(preview));
                            setText(""); // No text - everything is in the icon
                            
                            // Customize the component appearance - fill entire space
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setVerticalAlignment(SwingConstants.CENTER);
                            setOpaque(true);
                            setBackground(new Color(0, 0, 0, 0)); // Transparent
                            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No border/margin at all
                            
                            // Force the component to fill the entire combo box space
                            setPreferredSize(new Dimension(previewWidth, previewHeight));
                            setMinimumSize(new Dimension(previewWidth, previewHeight));
                            setMaximumSize(new Dimension(previewWidth, previewHeight));
                            setSize(new Dimension(previewWidth, previewHeight));
                            
                            // Additional settings to ensure full coverage
                            setAlignmentX(Component.CENTER_ALIGNMENT);
                            setAlignmentY(Component.CENTER_ALIGNMENT);
                            
                            return this;
                        }
                    }
                }
                
                // Fallback for null values
                setText(value != null ? value.toString() : "");
                setIcon(null);
                setOpaque(true);
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                return this;
            }
        };
        
        planetCombo.setRenderer(textureRenderer);
        
        // Force the combo box to use our renderer for the selected value display
        planetCombo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        
        planetCombo.addActionListener(e -> {
            String selected = (String) planetCombo.getSelectedItem();
            System.out.println("Planet selected: " + selected); // Debug
            globePanel.setPlanet(selected);
        });

        // Show orbits toggle
        JCheckBox orbitCheckbox = new JCheckBox("Show Orbits", true);
        orbitCheckbox.setBackground(Color.DARK_GRAY);
        orbitCheckbox.setForeground(Color.WHITE);
        orbitCheckbox.addActionListener(e -> globePanel.setShowOrbits(orbitCheckbox.isSelected()));

        // Show light source toggle
        JCheckBox lightCheckbox = new JCheckBox("Show Light", true);
        lightCheckbox.setBackground(Color.DARK_GRAY);
        lightCheckbox.setForeground(Color.WHITE);
        lightCheckbox.addActionListener(e -> globePanel.setShowLightSource(lightCheckbox.isSelected()));

        // Show terminator toggle
        JCheckBox terminatorCheckbox = new JCheckBox("Day/Night Line", true);
        terminatorCheckbox.setBackground(Color.DARK_GRAY);
        terminatorCheckbox.setForeground(Color.WHITE);
        terminatorCheckbox.addActionListener(e -> globePanel.setShowTerminator(terminatorCheckbox.isSelected()));

        // Light angle control
        JLabel lightLabel = new JLabel("Light Angle:");
        lightLabel.setForeground(Color.WHITE);
        JSlider lightSlider = new JSlider(0, 360, 45);
        lightSlider.setBackground(Color.DARK_GRAY);
        lightSlider.setForeground(Color.WHITE);
        lightSlider.addChangeListener(e -> globePanel.setLightAngle(Math.toRadians(lightSlider.getValue())));

        // Reset button
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(Color.GRAY);
        resetButton.setForeground(Color.BLACK);
        resetButton.addActionListener(e -> {
            speedSlider.setValue(10);
            satSlider.setValue(8);
            planetCombo.setSelectedItem("Earth");
            orbitCheckbox.setSelected(true);
            lightCheckbox.setSelected(true);
            terminatorCheckbox.setSelected(true);
            lightSlider.setValue(45);
            globePanel.reset();
        });

        // Add components to control panel
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        controlPanel.add(Box.createVerticalStrut(10));
        
        controlPanel.add(satLabel);
        controlPanel.add(satSlider);
        controlPanel.add(Box.createVerticalStrut(10));
        
        controlPanel.add(planetLabel);
        controlPanel.add(planetCombo);
        controlPanel.add(Box.createVerticalStrut(10));
        
        controlPanel.add(orbitCheckbox);
        controlPanel.add(Box.createVerticalStrut(5));
        
        controlPanel.add(lightCheckbox);
        controlPanel.add(Box.createVerticalStrut(5));
        
        controlPanel.add(terminatorCheckbox);
        controlPanel.add(Box.createVerticalStrut(10));
        
        controlPanel.add(lightLabel);
        controlPanel.add(lightSlider);
        controlPanel.add(Box.createVerticalStrut(10));
        
        controlPanel.add(resetButton);
        controlPanel.add(Box.createVerticalGlue());

        return controlPanel;
    }
}