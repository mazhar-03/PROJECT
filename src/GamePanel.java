import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Board board;

    public GamePanel(String sizeType, JFrame parentFrame) {
        setLayout(new BorderLayout());
        board = new Board(sizeType, parentFrame);
        add(board, BorderLayout.CENTER);
    }

//    public void startGame() {
//        Thread gameThread = new Thread(board);
//        gameThread.start();
//    }
}
