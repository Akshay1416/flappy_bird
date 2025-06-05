import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private static final double SCALE_FACTOR = 0.8; // Scale factor for the game window
    private int width;
    private int height;
    private static final int DELAY = 20;
    private static final double GRAVITY = 1.0;
    private static final double JUMP_SPEED = -10.0;
    private static final double INITIAL_PIPE_SPEED = 3.0;
    private static final double PIPE_GAP_RATIO = 0.25; // 25% of screen height
    private static final double INITIAL_PIPE_SPACING_RATIO = 0.4; // 40% of screen width
    private static final double GROUND_HEIGHT_RATIO = 0.15; // 15% of screen height
    private static final int LEVEL_UP_SCORE = 5;
    private static final double SPEED_INCREASE = 0.5;

    private Bird bird;
    private ArrayList<Pipe> pipes;
    private ArrayList<Cloud> clouds;
    private ArrayList<ScorePopup> scorePopups;
    private Timer timer;
    private boolean gameOver;
    private int score;
    private int level;
    private double pipeSpeed;
    private double pipeSpacing;
    private Random random;
    private double groundOffset;
    private double parallaxOffset;
    private int levelUpTimer;
    private double scale;

    public FlappyBird() {
        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) (screenSize.width * SCALE_FACTOR);
        height = (int) (screenSize.height * SCALE_FACTOR);

        // Calculate scale factor based on reference dimensions (800x600)
        scale = Math.min(width / 800.0, height / 600.0);

        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);

        bird = new Bird(width / 4, height / 2, scale);
        pipes = new ArrayList<>();
        clouds = new ArrayList<>();
        scorePopups = new ArrayList<>();
        random = new Random();
        gameOver = false;
        score = 0;
        level = 1;
        pipeSpeed = INITIAL_PIPE_SPEED * scale;
        pipeSpacing = width * INITIAL_PIPE_SPACING_RATIO;
        groundOffset = 0;
        parallaxOffset = 0;
        levelUpTimer = 0;

        // Add initial clouds
        for (int i = 0; i < 5; i++) {
            clouds.add(new Cloud(random.nextInt(width), random.nextInt(height / 3), scale));
        }

        timer = new Timer(DELAY, this);
        timer.start();

        // Add initial pipes
        addPipe();
    }

    private void addPipe() {
        int pipeGap = (int) (height * PIPE_GAP_RATIO);
        int groundHeight = (int) (this.height * GROUND_HEIGHT_RATIO);

        // Calculate a random position for the top of the gap (gapY)
        double minY = 50 * scale; // Minimum y for the top of the gap
        double maxY = this.height - groundHeight - pipeGap - (50 * scale); // Maximum y for the top of the gap
        double gapY = minY + random.nextDouble() * (maxY - minY);

        // Add a single Pipe object representing the top and bottom pipe with a gap
        pipes.add(new Pipe(width, gapY, Pipe.PIPE_WIDTH * scale, pipeGap, pipeSpeed, this.height, groundHeight));
    }

    private void levelUp() {
        level++;
        pipeSpeed += SPEED_INCREASE * scale;
        pipeSpacing = Math.max(width * 0.2, width * INITIAL_PIPE_SPACING_RATIO - (level - 1) * 20 * scale);
        levelUpTimer = 60;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw clouds with parallax
        for (Cloud cloud : clouds) {
            cloud.draw(g2d);
        }

        // Draw pipes
        for (Pipe pipe : pipes) {
            pipe.draw(g2d);
        }

        // Draw ground
        drawGround(g2d);

        // Draw bird
        bird.draw(g2d);

        // Draw score popups
        for (ScorePopup popup : scorePopups) {
            popup.draw(g2d);
        }

        // Draw score and level
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, (int) (20 * scale)));
        g2d.drawString("Score: " + score, (int) (20 * scale), (int) (30 * scale));
        g2d.drawString("Level: " + level, (int) (20 * scale), (int) (60 * scale));

        // Draw level up message
        if (levelUpTimer > 0) {
            g2d.setColor(new Color(255, 255, 255, levelUpTimer * 4));
            g2d.setFont(new Font("Arial", Font.BOLD, (int) (40 * scale)));
            String levelUpText = "Level " + level + "!";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(levelUpText);
            g2d.drawString(levelUpText, width / 2 - textWidth / 2, height / 2);
            levelUpTimer--;
        }

        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, (int) (40 * scale)));
            String gameOverText = "Game Over!";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(gameOverText);
            g2d.drawString(gameOverText, width / 2 - textWidth / 2, height / 2);

            g2d.setFont(new Font("Arial", Font.BOLD, (int) (20 * scale)));
            String restartText = "Press SPACE to restart";
            textWidth = g2d.getFontMetrics().stringWidth(restartText);
            g2d.drawString(restartText, width / 2 - textWidth / 2, height / 2 + (int) (40 * scale));

            String levelText = "Final Level: " + level;
            textWidth = g2d.getFontMetrics().stringWidth(levelText);
            g2d.drawString(levelText, width / 2 - textWidth / 2, height / 2 + (int) (80 * scale));
        }
    }

    private void drawGround(Graphics2D g) {
        int groundHeight = (int) (height * GROUND_HEIGHT_RATIO);

        // Draw ground base with gradient
        GradientPaint groundGradient = new GradientPaint(
                0, height - groundHeight,
                new Color(139, 69, 19),
                0, height,
                new Color(101, 67, 33));
        g.setPaint(groundGradient);
        g.fillRect(0, height - groundHeight, width, groundHeight);

        // Draw grass with smooth wave animation
        g.setColor(new Color(34, 139, 34));
        for (int i = 0; i < width; i += 2) {
            double wave = Math.sin((i + groundOffset) * 0.05) * (8 * scale);
            g.fillRect(i, height - groundHeight, 2, (int) (25 * scale) + (int) wave);
        }

        // Draw ground shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillRect(0, height - groundHeight, width, (int) (8 * scale));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            bird.update();

            // Update clouds with parallax
            parallaxOffset += 0.3 * scale;
            for (Cloud cloud : clouds) {
                cloud.update();
                if (cloud.getX() > width) {
                    cloud.reset(width);
                }
            }

            // Update ground offset
            groundOffset += 0.5 * scale;

            // Update pipes
            for (int i = pipes.size() - 1; i >= 0; i--) {
                Pipe pipe = pipes.get(i);
                pipe.update();

                // Remove pipes that are off screen
                if (pipe.getX() + pipe.getWidth() < 0) {
                    pipes.remove(i);
                }

                // Check collision with both top and bottom pipe sections
                if (bird.getBounds().intersects(pipe.getBoundsTop())
                        || bird.getBounds().intersects(pipe.getBoundsBottom())) {
                    gameOver = true;
                }

                // Score point when passing pipe
                if (!pipe.isPassed() && pipe.getX() + pipe.getWidth() < bird.getX()) {
                    pipe.setPassed(true);
                    score++;

                    // Check for level up
                    if (score > 0 && score % LEVEL_UP_SCORE == 0) {
                        levelUp();
                    }

                    // Add score popup at the center of the gap
                    scorePopups.add(new ScorePopup(pipe.getX() + pipe.getWidth() / 2,
                            pipe.getGapY() + pipe.getGapHeight() / 2, scale));
                }
            }

            // Update score popups
            for (int i = scorePopups.size() - 1; i >= 0; i--) {
                ScorePopup popup = scorePopups.get(i);
                popup.update();
                if (popup.isDead()) {
                    scorePopups.remove(i);
                }
            }

            // Add new pipes
            if (pipes.size() == 0 || pipes.get(pipes.size() - 1).getX() < width - pipeSpacing) {
                addPipe();
            }

            // Check if bird hits the ground or ceiling
            if (bird.getY() <= 0 || bird.getY() >= height - (height * GROUND_HEIGHT_RATIO)) {
                gameOver = true;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                // Reset game
                bird = new Bird(width / 4, height / 2, scale);
                pipes.clear();
                scorePopups.clear();
                gameOver = false;
                score = 0;
                level = 1;
                pipeSpeed = INITIAL_PIPE_SPEED * scale;
                pipeSpacing = width * INITIAL_PIPE_SPACING_RATIO;
                addPipe();
            } else {
                bird.jump();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new FlappyBird());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class ScorePopup {
    private double x, y;
    private double vy;
    private int life;
    private static final int MAX_LIFE = 30;
    private double scale;

    public ScorePopup(double x, double y, double scale) {
        this.x = x;
        this.y = y;
        this.vy = -2;
        this.life = MAX_LIFE;
        this.scale = scale;
    }

    public void update() {
        y += vy;
        vy += 0.1;
        life--;
    }

    public void draw(Graphics2D g) {
        float alpha = (float) life / MAX_LIFE;
        g.setColor(new Color(255, 255, 255, (int) (alpha * 255)));
        g.setFont(new Font("Arial", Font.BOLD, (int) (20 * scale)));
        String text = "+1";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (int) x - textWidth / 2, (int) y);
    }

    public boolean isDead() {
        return life <= 0;
    }
}