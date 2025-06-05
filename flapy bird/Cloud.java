import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Cloud {
    private double x;
    private double y;
    private double speed;
    private double scale;
    private static final double BASE_SPEED = 0.5;
    private static final int BASE_SIZE = 60;
    private double wobbleOffset;

    public Cloud(int x, int y, double scale) {
        this.x = x;
        this.y = y;
        this.speed = BASE_SPEED * (0.5 + Math.random() * 0.5);
        this.scale = scale;
        this.wobbleOffset = 0;
    }

    public void update() {
        x += speed * scale;
        wobbleOffset = Math.sin(x * 0.02) * 3 * scale;
    }

    public void reset(int width) {
        x = -BASE_SIZE * scale;
        y = Math.random() * 200 * scale;
    }

    public void draw(Graphics2D g2d) {
        int size = (int) (BASE_SIZE * scale);

        // Create a combined shape for the cloud
        Area cloudShape = new Area();

        // Add main cloud body
        cloudShape.add(new Area(new Ellipse2D.Double(x, y, size, size)));

        // Add top cloud puff
        cloudShape.add(new Area(new Ellipse2D.Double(x + size / 3, y - size / 4, size, size)));

        // Add bottom cloud puff
        cloudShape.add(new Area(new Ellipse2D.Double(x + size / 2, y + size / 4, size, size)));

        // Draw cloud with gradient
        GradientPaint cloudGradient = new GradientPaint(
                (int) x, (int) y,
                new Color(255, 255, 255, 200), // White with opacity
                (int) x + size, (int) y + size,
                new Color(240, 240, 240, 150) // Light grey with opacity
        );
        g2d.setPaint(cloudGradient);
        g2d.translate(wobbleOffset, 0);
        g2d.fill(cloudShape);

        // Reset translation
        g2d.translate(-wobbleOffset, 0);
    }

    public double getX() {
        return x;
    }
}