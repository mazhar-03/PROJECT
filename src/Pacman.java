import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

public class Pacman implements KeyListener, Runnable {
    private int x, y;
    private int score;
    private final Board board;
    private int speed;
    private boolean hasSpeedUpgrade;
    private boolean hasExtraLifeUpgrade;
    private boolean hasSlowDownGhostsUpgrade;
    private boolean hasInvincibilityUpgrade;
    private boolean hasDoubleScoreUpgrade;
    private final ImageIcon[][] pacmanImages; // 0: up, 1: down, 2: left, 3: right
    private int currentDirection;
    private int requestedDirection;
    private boolean mouthOpen;
    private static final int BASE_SPEED = 1;
    private static final int UPGRADED_SPEED = 2;
    private final Thread pacmanThread;

    public Pacman(Board board) {
        this.board = board;
        resetPosition();
        this.score = 0;
        this.speed = BASE_SPEED;
        this.hasSpeedUpgrade = false;
        this.hasExtraLifeUpgrade = false;
        this.hasSlowDownGhostsUpgrade = false;
        this.hasInvincibilityUpgrade = false;
        this.hasDoubleScoreUpgrade = false;

        // Load Pacman images
        pacmanImages = new ImageIcon[4][2];
        pacmanImages[2][0] = loadImage("pacman_up.png");      // Left
        pacmanImages[2][1] = loadImage("pacman_up_closed.png");
        pacmanImages[3][0] = loadImage("pacman_down.png");     // Right
        pacmanImages[3][1] = loadImage("pacman_down_closed.png");
        pacmanImages[0][0] = loadImage("pacman_left.png");        // Up
        pacmanImages[0][1] = loadImage("pacman_left_closed.png");
        pacmanImages[1][0] = loadImage("pacman_right.png");      // Down
        pacmanImages[1][1] = loadImage("pacman_right_closed.png");

        currentDirection = 1; // Initially facing right
        requestedDirection = 1;
        mouthOpen = true;

        board.addKeyListener(this);
        board.setFocusable(true);
        board.requestFocusInWindow();

        // Start the pacman thread
        pacmanThread = new Thread(this);
        pacmanThread.start();
    }

    private ImageIcon loadImage(String path) {
        URL imageUrl = getClass().getClassLoader().getResource("images/" + path);
        if (imageUrl == null) {
            System.out.println("Could not find image: " + path);
            return null;
        }
        return scaleImage(new ImageIcon(imageUrl));
    }

    private ImageIcon scaleImage(ImageIcon icon) {
        int cellSize = board.getCellSize();
        return new ImageIcon(icon.getImage().getScaledInstance(cellSize, cellSize, java.awt.Image.SCALE_SMOOTH));
    }

    private void move() {
        // First, try to move in the requested direction
        int dx = 0, dy = 0;
        boolean canMove = false;

        switch (requestedDirection) {
            case 0 -> dy = -1; // Up
            case 1 -> dy = 1;  // Down
            case 2 -> dx = -1; // Left
            case 3 -> dx = 1;  // Right
        }

        int newX = x + dx;
        int newY = y + dy;

        if (!board.isWall(newX, newY)) {
            currentDirection = requestedDirection;
            canMove = true;
        }

        if (!canMove) {
            // If we can't move in the requested direction, move in the current direction
            dx = 0;
            dy = 0;
            switch (currentDirection) {
                case 0 -> dy = -1; // Up
                case 1 -> dy = 1;  // Down
                case 2 -> dx = -1; // Left
                case 3 -> dx = 1;  // Right
            }

            newX = x + dx;
            newY = y + dy;

            if (!board.isWall(newX, newY)) {
                x = newX;
                y = newY;
            } else {
                // If Pacman can't move, it should still face the requested direction with an open mouth
                currentDirection = requestedDirection;
                mouthOpen = true;
            }
        } else {
            x = newX;
            y = newY;
        }

        // Handle food and upgrades
        if (board.isFood(x, y)) {
            eatFood();
            board.removeFood(x, y);
            if (!board.hasFood()) {
                board.gameOver();
            }
        }
        if (board.isUpgrade(x, y)) {
            board.applyUpgrade(this);
            board.removeUpgrade(x, y);
        }
    }

    public void applyUpgrade(String upgradeType) {
        switch (upgradeType) {
            case "speed":
                hasSpeedUpgrade = true;
                speed = UPGRADED_SPEED;
                break;
            case "extraLife":
                hasExtraLifeUpgrade = true;
                board.incrementLives();
                break;
            case "slowDownGhosts":
                hasSlowDownGhostsUpgrade = true;
                break;
            case "invincibility":
                hasInvincibilityUpgrade = true;
                break;
            case "doubleScore":
                hasDoubleScoreUpgrade = true;
                break;
            default:
                break;
        }
    }

    public void resetUpgrades() {
        hasSpeedUpgrade = false;
        hasExtraLifeUpgrade = false;
        hasSlowDownGhostsUpgrade = false;
        hasInvincibilityUpgrade = false;
        hasDoubleScoreUpgrade = false;
        speed = BASE_SPEED;
    }

    public boolean hasInvincibilityUpgrade() {
        return hasInvincibilityUpgrade;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScore() {
        return score;
    }

    public void eatFood() {
        score += hasDoubleScoreUpgrade ? 20 : 10;
    }

    public void resetPosition() {
        x = board.getBoardSize() / 2;
        y = board.getBoardSize() / 2;
        currentDirection = 3; // Reset facing right
        requestedDirection = 3;
        board.requestFocusInWindow(); // Ensure the board regains focus to receive key events
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT -> requestedDirection = 0; // Left
            case KeyEvent.VK_RIGHT -> requestedDirection = 1; // Right
            case KeyEvent.VK_UP -> requestedDirection = 2; // Up
            case KeyEvent.VK_DOWN -> requestedDirection = 3; // Down
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void run() {
        while (!pacmanThread.isInterrupted()) {
            mouthOpen = !mouthOpen; // Toggle mouth state for animation
            move();
            board.updateBoard();
            try {
                Thread.sleep(500 / speed);
            } catch (InterruptedException e) {
                pacmanThread.interrupt();
            }
        }
    }

    public ImageIcon getCurrentImage() {
        return pacmanImages[currentDirection][mouthOpen ? 0 : 1];
    }
}
