# Enhanced Globe 3D Visualization Application

![Screenshot 2025-11-08 010937.png](src/main/resources/Screenshot%202025-11-08%20010937.png)

A sophisticated Spring Boot application that displays an interactive 3D visualization of planets with orbiting satellites, featuring real textures, multiple planet types, and interactive controls.

## 🌟 Features

### Visual Features
- **Rotating Earth with Real Texture**: Uses SilverGlobe2.jpg for realistic Earth appearance
- **Multiple Planet Types**: Earth, Mars, Jupiter, Venus, and Mercury with unique characteristics
- **Animated Satellites**: Colorful satellites orbiting at different speeds and distances
- **Star-filled Background**: Dynamic starfield with varying brightness
- **Smooth Animations**: Real-time rotation and orbital mechanics
- **Glow Effects**: Enhanced satellite rendering with glow effects

### Interactive Controls
- **Speed Control**: Adjust animation speed from 0.1x to 2.0x
- **Satellite Count**: Control number of orbiting satellites (0-20)
- **Planet Selection**: Switch between different planets in real-time
- **Orbit Visibility**: Toggle satellite orbit paths on/off
- **Mouse Interaction**: Move mouse to influence Earth's rotation speed
- **Reset Function**: Restore all settings to defaults

### Planet-Specific Features
- **Earth**: Textured surface with realistic continents (when texture available)
- **Mars**: Red surface with white polar ice caps
- **Jupiter**: Orange surface with distinctive brown bands
- **Venus**: Golden surface representing thick atmosphere
- **Mercury**: Gray rocky surface

## 📋 Requirements

- Java 11 or higher
- Maven 3.6 or higher
- Display capable of showing GUI applications
- Recommended: 1000x700 screen resolution or higher

## 🚀 How to Run

### Using Maven

```bash
# Clean and compile the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

### Using Maven Wrapper (if available)

```bash
# On Windows
.\mvnw.cmd spring-boot:run

# On Unix/Linux/Mac
./mvnw spring-boot:run
```

## 🎮 Controls and Interaction

### Control Panel (Bottom of Window)
- **Speed Slider**: Adjust animation speed
- **Satellites Slider**: Change number of orbiting objects
- **Planet Dropdown**: Select different planets to visualize
- **Show Orbits Checkbox**: Toggle orbit path visibility
- **Reset Button**: Restore all settings to defaults

### Mouse Controls
- **Move Mouse**: Influences Earth's rotation speed based on horizontal position
- **Mouse Position**: Hover over different areas to see dynamic rotation effects

## 🖼️ What You'll See

When the application starts:

1. **Main Window**: "Enhanced Globe 3D Visualization" (1000x700 pixels)
2. **Central Display**: 
   - Rotating planet with realistic textures/colors
   - Dynamic satellite orbits with colored objects
   - Starfield background with twinkling stars
3. **Information Display**:
   - Current planet name
   - Active satellite count
   - Animation speed
   - Interactive instructions
4. **Control Panel**: Bottom toolbar with interactive controls

## 🏗️ Application Structure

- `GlobalApplication.java` - Main Spring Boot application class
- `EnhancedGlobePanel` - Advanced JPanel for rendering 3D visualization
- `Satellite` - Class representing orbiting objects with physics
- Control panel integration with real-time updates

## 🎨 Customization Options

### Easy Modifications
- **Planet Colors**: Modify `getPlanetColor()` method
- **Satellite Colors**: Update `generateSatelliteColor()` method
- **Animation Speed**: Adjust timer intervals and speed multipliers
- **Orbit Patterns**: Modify satellite creation and update logic
- **Visual Effects**: Enhance glow, trails, or particle effects

### Advanced Customizations
- **Add New Planets**: Extend planet types in dropdown and color/feature methods
- **Custom Textures**: Add new image resources and texture loading
- **Enhanced Physics**: Implement elliptical orbits, gravitational effects
- **3D Perspective**: Add depth simulation and perspective transformations

## 🛠️ Technical Features

- **Texture Loading**: Automatic fallback if primary texture unavailable
- **Performance Optimized**: Efficient rendering with Graphics2D optimizations
- **Memory Efficient**: Proper object management and cleanup
- **Thread Safe**: UI updates on Event Dispatch Thread
- **Responsive Design**: Dynamic scaling and positioning

## 🔧 Troubleshooting

### Common Issues

1. **Texture Not Loading**:
   - Check that files <planets>.jpg exists in `src/main/resources/`
   - Application falls back to earth.jpg automatically
   - Will use solid colors if no textures available

2. **Performance Issues**:
   - Reduce satellite count using the slider
   - Decrease animation speed
   - Close other resource-intensive applications

3. **Window Not Visible**:
   - Check taskbar for application icon
   - Use Alt+Tab to cycle through windows
   - Ensure display resolution supports 1000x700 window

4. **Controls Not Responding**:
   - Click directly on control elements
   - Ensure window has focus
   - Try the Reset button to restore defaults

## 🚫 Closing the Application

- Click the X button on the window
- Or press Ctrl+C in the terminal
- Application logs "Application closing..." on graceful shutdown

## 💻 Technology Stack

- **Spring Boot 2.7.18** - Application framework
- **Java Swing** - GUI framework with Graphics2D rendering
- **BufferedImage** - Texture and image processing
- **Maven** - Dependency management and build system
- **SLF4J** - Logging framework

## 🎯 Performance Notes

- Optimized for 60 FPS rendering
- Supports up to 20 satellites without performance degradation
- Texture caching for efficient memory usage
- Smooth animations on modern hardware

Enjoy exploring the cosmos with your interactive planet visualization! 🌌
