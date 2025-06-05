import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Ground {
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed;
    private static final double BASE_SPEED = 2.0;
    private static final int GROUND_HEIGHT = 150;

    public Ground(int x, int y, double speed, double scale) {
        this.x = x;
        this.y = y;
        this.width = 1000 * scale; // Assuming a sufficient width
        this.height = GROUND_HEIGHT * scale;
        this.speed = speed * scale;
    }

    public void update() {
        x -= speed;
        // If the ground piece moves off-screen to the left, reset its position
        if (x + width < 0) {
            x = x + width * 2; // Place it back to the right, creating a continuous effect
        }
    }

    public void draw(Graphics2D g2d) {
        // Draw the dirt/stone layer
        g2d.setColor(new Color(139, 69, 19)); // Saddle Brown
        g2d.fill(new Rectangle2D.Double(x, y, width, height));

        // Draw the grass layer
        g2d.setColor(new Color(124, 252, 0)); // Lawn Green
        g2d.fill(new Rectangle2D.Double(x, y, width, height * 0.3)); // Grass on top 30%

        // Add some simple texture elements (optional, can be expanded)
        g2d.setColor(new Color(107, 142, 35)); // Olive Drab for texture
        for (int i = 0; i < width; i += 20) {
            g2d.fillRect((int) (x + i), (int) (y + height * 0.3 - 5), 5, 5);
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }
}