import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pacman Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setLayout(new BorderLayout());

            Menu menu = new Menu(frame);
            frame.add(menu);

            frame.setLocationRelativeTo(null); // Center the frame
            frame.setVisible(true);
        });
    }
}

