import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Bird {
    private double x;
    private double y;
    private double velocity;
    private double rotation;
    private int frame;
    private double scale;
    private static final double GRAVITY = 1.0;
    private static final double JUMP_SPEED = -10.0;
    private static final double ROTATION_SPEED = 5.0;
    private static final int FRAME_DELAY = 5;
    private static final int BIRD_SIZE = 40;
    private Rectangle bounds;
    private double wingAngle;
    private ArrayList<Particle> particles;
    private int frameCount;
    private double glowIntensity;
    private double wingSpeed = 0.6;
    private double wingRange = 70;

    public Bird(int x, int y, double scale) {
        this.x = x;
        this.y = y;
        this.velocity = 0;
        this.rotation = 0;
        this.frame = 0;
        this.scale = scale;
        this.bounds = new Rectangle((int) (x - BIRD_SIZE * scale / 2), (int) (y - BIRD_SIZE * scale / 2),
                (int) (BIRD_SIZE * scale), (int) (BIRD_SIZE * scale));
        this.wingAngle = 0;
        this.particles = new ArrayList<>();
        this.frameCount = 0;
        this.glowIntensity = 0;
    }

    public void update() {
        frameCount++;
        velocity += GRAVITY * scale;
        y += velocity * scale;
        bounds.setLocation((int) (x - BIRD_SIZE * scale / 2), (int) (y - BIRD_SIZE * scale / 2));

        // Rotation based on velocity
        double targetRotation = velocity * ROTATION_SPEED;
        rotation += (targetRotation - rotation) * 0.15;

        // Update animation frame
        frame = (frame + 1) % (3 * FRAME_DELAY);

        // Wing animation
        wingAngle = Math.sin(frameCount * wingSpeed) * wingRange * scale;

        // Update particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (p.isDead()) {
                particles.remove(i);
            }
        }
    }

    public void jump() {
        velocity = JUMP_SPEED;
        // Add particles
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(
                    (int) (x - BIRD_SIZE * scale / 2),
                    (int) (y - BIRD_SIZE * scale / 2 + BIRD_SIZE * scale / 2),
                    -5 + Math.random() * 10,
                    4 + Math.random() * 5));
        }
    }

    public void draw(Graphics2D g2d) {
        // Draw particles first
        for (Particle p : particles) {
            p.draw(g2d);
        }

        AffineTransform oldTransform = g2d.getTransform();

        // Calculate scaled size
        int scaledSize = (int) (BIRD_SIZE * scale);

        // Apply translation and rotation
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(rotation));

        // Draw bird body with border (Yellow)
        g2d.setColor(new Color(255, 255, 0)); // Yellow
        g2d.fillOval(-scaledSize / 2, -scaledSize / 2, scaledSize, scaledSize);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2 * (float) scale));
        g2d.drawOval(-scaledSize / 2, -scaledSize / 2, scaledSize, scaledSize);

        // Draw wing with border (Orange)
        g2d.setColor(new Color(255, 165, 0)); // Orange
        g2d.rotate(Math.toRadians(wingAngle));
        g2d.fillOval(-scaledSize / 2, -scaledSize / 4, scaledSize / 2, scaledSize / 2);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(-scaledSize / 2, -scaledSize / 4, scaledSize / 2, scaledSize / 2);
        g2d.rotate(Math.toRadians(-wingAngle));

        // Draw eye with border
        g2d.setColor(Color.WHITE);
        g2d.fillOval(scaledSize / 4, -scaledSize / 4, scaledSize / 4, scaledSize / 4);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(scaledSize / 4, -scaledSize / 4, scaledSize / 4, scaledSize / 4);
        g2d.fillOval(scaledSize / 3, -scaledSize / 4, scaledSize / 8, scaledSize / 8);

        // Draw beak with border (Orange)
        g2d.setColor(new Color(255, 165, 0)); // Orange
        int[] xPoints = { scaledSize / 2, scaledSize / 2 + scaledSize / 4, scaledSize / 2 };
        int[] yPoints = { -scaledSize / 8, 0, scaledSize / 8 };
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, 3);

        // Restore the transform
        g2d.setTransform(oldTransform);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

class Particle {
    private double x, y;
    private double vx, vy;
    private int life;
    private static final int MAX_LIFE = 25;
    private Color color;
    private double scale;
    private double rotation;

    public Particle(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = MAX_LIFE;
        this.color = new Color(255, 255, 255, 200);
        this.scale = 1.0;
        this.rotation = Math.random() * Math.PI * 2;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.15;
        life--;
        float alpha = (float) life / MAX_LIFE;
        color = new Color(255, 255, 255, (int) (alpha * 200));
        scale = 1.0 + (1.0 - alpha) * 0.5;
        rotation += 0.1;
    }

    public void draw(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();
        g.translate(x, y);
        g.rotate(rotation);

        g.setColor(color);
        int size = (int) (4 * scale);
        g.fillOval(-size / 2, -size / 2, size, size);
        // Keeping particle border as it was already there
        g.setColor(new Color(0, 0, 0, (int) (color.getAlpha() * 0.5)));
        g.drawOval(-size / 2, -size / 2, size, size);

        g.setTransform(oldTransform);
    }

    public boolean isDead() {
        return life <= 0;
    }
}