import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board extends JPanel implements Serializable, Runnable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int size;
    private boolean[][] walls;
    private boolean[][] food;
    private List<Upgrade> upgrades;
    private List<Ghost> ghosts;
    private final Pacman pacman;
    private final HighScoreManager highScoreManager;
    private int lifeCounter;
    private int timeCounter;
    private final JPanel[][] cells;
    private final int cellSize;
    private final Random random;

    private final JLabel scoreLabel;
    private final JLabel livesLabel;
    private final JLabel timeLabel;

    private final JFrame parentFrame; // Reference to the parent frame
    private ImageIcon upgradeIcon;

    private final Thread boardThread;

    public Board(String sizeType, JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setSize(sizeType);
        setLayout(new BorderLayout());

        cellSize = Math.min(800 / size, 800 / size);
        JPanel gamePanel = new JPanel(new GridLayout(size, size));
        gamePanel.setPreferredSize(new Dimension(cellSize * size, cellSize * size));
        add(gamePanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(1, 4));
        scoreLabel = new JLabel("Score: 0");
        livesLabel = new JLabel("Lives: 3");
        timeLabel = new JLabel("Time: 0");
        infoPanel.add(scoreLabel);
        infoPanel.add(livesLabel);
        infoPanel.add(timeLabel);

        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> exitGame());
        infoPanel.add(backButton);

        add(infoPanel, BorderLayout.NORTH);

        cells = new JPanel[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new JPanel(new BorderLayout());
                cells[i][j].setBackground(Color.BLACK);
                gamePanel.add(cells[i][j]);
            }
        }

        random = new Random();
        initializeBoard();
        pacman = new Pacman(this); // Create the Pacman instance and pass the Board to it
        highScoreManager = new HighScoreManager(); // Initialize the high score manager
        updateBoard();

        // Load the upgrade image icon
        upgradeIcon = new ImageIcon(getClass().getClassLoader().getResource("images/cherry.png"));
        upgradeIcon = new ImageIcon(upgradeIcon.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));

        // Start the game thread
        boardThread = new Thread(this);
        boardThread.start();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(200, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }

    private void setSize(String sizeType) {
        switch (sizeType.toLowerCase()) {
            case "small":
                this.size = 15;
                break;
            case "medium":
                this.size = 18;
                break;
            case "large":
                this.size = 21;
                break;
            case "extra-large":
                this.size = 23;
                break;
            case "huge":
                this.size = 26;
                break;
            default:
                throw new IllegalArgumentException("Invalid board size type");
        }
    }

    private void initializeBoard() {
        walls = new boolean[size][size];
        food = new boolean[size][size];
        upgrades = new ArrayList<>();
        ghosts = new ArrayList<>();
        lifeCounter = 3;
        timeCounter = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //simple maze structure with an easy nested for loops
                if (i == 0 || i == size - 1 || j == 0 || j == size - 1) {
                    walls[i][j] = true; // Outer boundary
                } else if (size > 4 && (i % 2 == 0 && j % 2 == 0)) {
                    walls[i][j] = true;
                } else {
                    food[i][j] = true;
                }
            }
        }

        upgrades.add(new Upgrade(size / 2, size / 2, "speed"));

        for (int i = 0; i < 5; i++) { // Create 5 ghosts with different images
            int x, y;
            do {
                x = random.nextInt(size);
                y = random.nextInt(size);
            } while (walls[x][y]); // ensuring ghosts are not placed on walls

            Ghost ghost = new Ghost(x, y, this, i);
            ghosts.add(ghost);
        }
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || y < 0 || x >= size || y >= size) {
            return true; // treatin outoufbounds
        }
        return walls[x][y];
    }

    public boolean isFood(int x, int y) {
        return food[x][y];
    }

    public void removeFood(int x, int y) {
        food[x][y] = false;
    }

    public boolean isUpgrade(int x, int y) {
        for (Upgrade upgrade : upgrades) {
            if (upgrade.getX() == x && upgrade.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void removeUpgrade(int x, int y) {
        upgrades.removeIf(upgrade -> upgrade.getX() == x && upgrade.getY() == y);
    }

    public void applyUpgrade(Pacman pacman) {
        for (Upgrade upgrade : upgrades) {
            if (upgrade.getX() == pacman.getX() && upgrade.getY() == pacman.getY()) {
                upgrade.applyTo(pacman);
                removeUpgrade(upgrade.getX(), upgrade.getY());
                break;
            }
        }
    }

    public boolean isGhost(int x, int y) {
        for (Ghost ghost : ghosts) {
            if (ghost.getX() == x && ghost.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void moveGhosts() {
        for (Ghost ghost : ghosts) {
            ghost.chasePacman(pacman.getX(), pacman.getY());
        }
    }

    public void incrementLives() {
        lifeCounter++;
        livesLabel.setText("Lives: " + lifeCounter);
    }

    private void generateUpgrade() {
        if (random.nextFloat() < 0.25) { // 25% chance to get a random upgrade out of 5 upgrade
            int x, y;
            do {
                x = random.nextInt(size);
                y = random.nextInt(size);
            } while (isWall(x, y) || isFood(x, y) || isUpgrade(x, y) || isGhost(x, y) || (x == pacman.getX() && y == pacman.getY()));

            String[] upgradeTypes = {"speed", "extraLife", "slowDownGhosts", "invincibility", "doubleScore"};
            String type = upgradeTypes[random.nextInt(upgradeTypes.length)];
            upgrades.add(new Upgrade(x, y, type));
        }
    }

    public void gameOver() {
        boardThread.interrupt();
        int score = pacman != null ? pacman.getScore() : 0;
        String name = JOptionPane.showInputDialog(this, "Game Over! Your score: " + score + "\nEnter your name:");
        if (name != null && !name.isEmpty()) {
            highScoreManager.addHighScore(name, score);
        }
        displayHighScores();
        System.exit(0);
    }

    private void displayHighScores() {
        List<HighScore> highScores = highScoreManager.getHighScores();
        String[] highScoreArray = highScores.stream().map(HighScore::toString).toArray(String[]::new);

        // JList for high scores
        JList<String> highScoreList = new JList<>(highScoreArray);
        highScoreList.setFont(new Font("Arial", Font.BOLD, 16));
        highScoreList.setForeground(Color.WHITE);
        highScoreList.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("High Scores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel highScorePanel = new JPanel(new BorderLayout());
        highScorePanel.setBackground(Color.BLACK);
        highScorePanel.add(titleLabel, BorderLayout.NORTH);
        highScorePanel.add(new JScrollPane(highScoreList), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, highScorePanel, "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }
    public int getBoardSize() {
        return size;
    }

    public int getCellSize() {
        return cellSize;
    }

    public boolean hasFood() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (food[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void updateBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j].removeAll();
                cells[i][j].revalidate();
                if (walls[i][j]) {
                    cells[i][j].setBackground(Color.BLUE);
                } else if (food[i][j]) {
                    cells[i][j].setBackground(Color.BLACK); // Set cell color to black and draw food
                    JLabel foodLabel = new JLabel(".", SwingConstants.CENTER);
                    foodLabel.setForeground(Color.WHITE);
                    cells[i][j].add(foodLabel, BorderLayout.CENTER);
                } else {
                    cells[i][j].setBackground(Color.BLACK);
                }
            }
        }

        for (Upgrade upgrade : upgrades) {
            cells[upgrade.getX()][upgrade.getY()].removeAll();
            JLabel upgradeLabel = new JLabel(upgradeIcon, SwingConstants.CENTER);
            upgradeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            upgradeLabel.setVerticalAlignment(SwingConstants.CENTER);
            cells[upgrade.getX()][upgrade.getY()].add(upgradeLabel, BorderLayout.CENTER);
        }

        if (pacman != null) {
            cells[pacman.getX()][pacman.getY()].setBackground(Color.BLACK); // Set background to black
            JLabel pacmanLabel = new JLabel(pacman.getCurrentImage(), SwingConstants.CENTER);
            pacmanLabel.setHorizontalAlignment(SwingConstants.CENTER);
            pacmanLabel.setVerticalAlignment(SwingConstants.CENTER);
            cells[pacman.getX()][pacman.getY()].add(pacmanLabel, BorderLayout.CENTER);
        }

        // Ensure multiple ghosts can occupy and be rendered in the same cell
        for (Ghost ghost : ghosts) {
            int ghostX = ghost.getX();
            int ghostY = ghost.getY();
            JLabel ghostLabel = new JLabel(ghost.getGhostImage(), SwingConstants.CENTER);
            ghostLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ghostLabel.setVerticalAlignment(SwingConstants.CENTER);
            cells[ghostX][ghostY].add(ghostLabel, BorderLayout.CENTER);

            if (pacman != null && pacman.getX() == ghostX && pacman.getY() == ghostY) {
                if (!pacman.hasInvincibilityUpgrade()) {
                    pacman.resetUpgrades(); // Clear all upgrades
                    lifeCounter--;
                    if (lifeCounter <= 0) {
                        gameOver();
                    } else {
                        pacman.resetPosition();
                    }
                }
            }
        }

        // updating the upper info bar
        if (pacman != null) {
            scoreLabel.setText("Score: " + pacman.getScore());
            livesLabel.setText("Lives: " + lifeCounter);
            timeLabel.setText("Time: " + timeCounter);
        }

        revalidate();
        repaint();
    }

    private void exitGame() {
        boardThread.interrupt();
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new Menu(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    @Override
    public void run() {
        while (!boardThread.isInterrupted()) {
            try {
                Thread.sleep(200);
                timeCounter++;
                if (timeCounter % 5 == 0) {
                    generateUpgrade();
                }
                moveGhosts();
                updateBoard();
                if (!hasFood()) {
                    gameOver();
                }
            } catch (InterruptedException e) {
                boardThread.interrupt();
            }
        }
    }
}
