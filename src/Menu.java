import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class Menu extends JPanel {
    private final JFrame parentFrame;
    private final JLabel backgroundLabel;

    private final ImageIcon backgroundImage;


    public Menu(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Load the background image
        backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("images/bg_image.jpg"));

        // Create a label with the background image
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new GridBagLayout()); // for centering components

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false); // Make the panel transparent to show the background

        // Create buttons with a consistent size
        JButton playButton = createMenuButton("Play Game");
        JButton highScoreButton = createMenuButton("High Scores");
        JButton exitButton = createMenuButton("Exit");

        // Add action listeners for the buttons
        playButton.addActionListener(e -> startGame());
        highScoreButton.addActionListener(e -> showHighScores());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to the menu panel
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(playButton);
        menuPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        menuPanel.add(highScoreButton);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(exitButton);
        menuPanel.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        //places menuPanel from the top
        gbc.gridy = GridBagConstraints.RELATIVE;

        //anchored to the start of the page
        gbc.anchor = GridBagConstraints.PAGE_START;
        backgroundLabel.add(menuPanel, gbc);

        // Add the backgroundLabel to the frame
        add(backgroundLabel, BorderLayout.CENTER);

        // Add resize listener to parent frame
        parentFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeBackground();
            }
        });

        // Initial resize to fit current frame size
        resizeBackground();

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void showHighScores() {
        HighScoreManager highScoreManager = new HighScoreManager();
        List<HighScore> highScores = highScoreManager.getHighScores();

        //making highScore object a string and create new array of string with an appropriate  size
        String[] highScoreArray = highScores.stream().map(HighScore::toString).toArray(String[]::new);

        // Styled JList for high scores
        JList<String> highScoreList = new JList<>(highScoreArray);
        highScoreList.setFont(new Font("Arial", Font.BOLD, 16));
        highScoreList.setForeground(Color.WHITE);
        highScoreList.setBackground(Color.BLACK);

        // Add a title label for the high scores
        JLabel titleLabel = new JLabel("High Scores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create a panel to hold the title and the high score list
        JPanel highScorePanel = new JPanel(new BorderLayout());
        highScorePanel.setBackground(Color.BLACK);
        highScorePanel.add(titleLabel, BorderLayout.NORTH);
        highScorePanel.add(new JScrollPane(highScoreList), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, highScorePanel, "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startGame() {
        // Create a panel to hold the size selection buttons
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select Board Size:", SwingConstants.CENTER));

        String[] colorOptions = {"Small", "Medium", "Large", "Extra-Large", "Huge"};
        Color[] buttonColors = {Color.YELLOW, Color.GREEN, Color.ORANGE, Color.PINK, Color.CYAN};

        for (int i = 0; i < colorOptions.length; i++) {
            String option = colorOptions[i];
            JButton button = new JButton(option);
            button.setBackground(buttonColors[i]);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            button.addActionListener(e -> {
                parentFrame.getContentPane().removeAll();
                GamePanel gamePanel = new GamePanel(option, parentFrame);
                parentFrame.add(gamePanel, BorderLayout.CENTER);
                parentFrame.revalidate();
                parentFrame.repaint();
                // Close the dialog
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window != null) {
                    window.dispose();
                }
            });
            panel.add(button);
        }

        // Show the dialog
        JOptionPane.showOptionDialog(this, panel, "Board Size", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
    }
    private void resizeBackground() {
        int width = parentFrame.getWidth();
        int height = parentFrame.getHeight();
        Image scaledImage = backgroundImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
    }

}