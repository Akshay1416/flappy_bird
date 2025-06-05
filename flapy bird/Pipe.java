import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Pipe {
    private double x;
    private double y; // Represents the y-coordinate of the top of the gap
    private double width;
    private double gap;
    private double speed;
    private Rectangle2D boundsTop;
    private Rectangle2D boundsBottom;
    public static final double PIPE_WIDTH = 70;
    private static final double PIPE_GAP = 200;
    private static final double BASE_SPEED = 2.0;
    private boolean passed;
    private double topPipeHeight; // Store the height of the top pipe

    public Pipe(double x, double gapY, double width, double gap, double speed, double gameHeight, double groundHeight) {
        this.x = x;
        this.y = gapY; // y is the top of the gap
        this.width = width;
        this.gap = gap;
        this.speed = speed;
        this.passed = false;

        // Calculate top pipe height based on gap position
        this.topPipeHeight = gapY; // The top of the gap is also the bottom of the top pipe

        this.boundsTop = new Rectangle2D.Double(x, 0, width, topPipeHeight);
        this.boundsBottom = new Rectangle2D.Double(x, y + gap, width, gameHeight - (y + gap) - groundHeight);
    }

    public void update() {
        x -= speed;
        // Update bounds based on new x position
        boundsTop.setRect(x, 0, width, topPipeHeight);
        boundsBottom.setRect(x, y + gap, width, boundsBottom.getHeight()); // Height remains constant after creation
    }

    // Reset method to reuse pipes, takes game dimensions and ground height
    public void reset(int gameWidth, int gameHeight, double gap, double speed, double groundHeight) {
        this.x = gameWidth; // Start off screen to the right
        this.gap = gap;
        this.speed = speed;
        this.passed = false;

        // Calculate a new random position for the gap (and thus the pipes)
        double minY = 50; // Minimum height for the top pipe
        double maxY = gameHeight - groundHeight - gap - 50; // Maximum height for the top pipe
        this.y = minY + Math.random() * (maxY - minY); // y is the top of the gap
        this.topPipeHeight = this.y; // The top of the gap is the bottom of the top pipe

        // Update bounds with new position and calculated heights
        boundsTop.setRect(x, 0, width, topPipeHeight);
        boundsBottom.setRect(x, this.y + gap, width, gameHeight - (this.y + gap) - groundHeight);
    }

    public void draw(Graphics2D g2d) {
        // Set stroke for borders
        g2d.setStroke(new BasicStroke(2));

        // Define colors with shades for gradient
        Color pipeBaseColor = new Color(176, 252, 56); // #B0FC38
        Color pipeShadeColor = new Color(140, 200, 45); // A darker shade for gradient
        Color capBaseColor = new Color(60, 176, 67); // #3CB043
        Color capShadeColor = new Color(40, 140, 50); // A darker shade for gradient

        // Draw top pipe with gradient
        GradientPaint topPipeGradient = new GradientPaint(
                (float) x, (float) 0,
                pipeBaseColor,
                (float) x + (float) width, (float) topPipeHeight,
                pipeShadeColor);
        g2d.setPaint(topPipeGradient);
        g2d.fill(boundsTop);
        g2d.setColor(Color.BLACK);
        g2d.draw(boundsTop);

        // Draw top pipe cap with gradient
        GradientPaint topCapGradient = new GradientPaint(
                (float) x - 10, (float) topPipeHeight - 30,
                capBaseColor,
                (float) x + (float) width + 10, (float) topPipeHeight,
                capShadeColor);
        g2d.setPaint(topCapGradient);
        Rectangle2D topCapBounds = new Rectangle2D.Double(x - 10, topPipeHeight - 30, width + 20, 30);
        g2d.fill(topCapBounds);
        g2d.setColor(Color.BLACK);
        g2d.draw(topCapBounds);

        // Draw bottom pipe with gradient
        GradientPaint bottomPipeGradient = new GradientPaint(
                (float) x, (float) y + (float) gap,
                pipeBaseColor,
                (float) x + (float) width, (float) y + (float) gap + (float) boundsBottom.getHeight(),
                pipeShadeColor);
        g2d.setPaint(bottomPipeGradient);
        g2d.fill(boundsBottom);
        g2d.setColor(Color.BLACK);
        g2d.draw(boundsBottom);

        // Draw bottom pipe cap with gradient
        GradientPaint bottomCapGradient = new GradientPaint(
                (float) x - 10, (float) y + (float) gap,
                capBaseColor,
                (float) x + (float) width + 10, (float) y + (float) gap + 30,
                capShadeColor);
        g2d.setPaint(bottomCapGradient);
        Rectangle2D bottomCapBounds = new Rectangle2D.Double(x - 10, y + gap, width + 20, 30);
        g2d.fill(bottomCapBounds);
        g2d.setColor(Color.BLACK);
        g2d.draw(bottomCapBounds);
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }

    public Rectangle2D getBoundsTop() {
        return boundsTop;
    }

    public Rectangle2D getBoundsBottom() {
        return boundsBottom;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    // Add methods to get vertical position and height components for scoring
    public double getGapY() {
        return y; // y is the top of the gap
    }

    public double getGapHeight() {
        return gap;
    }
}