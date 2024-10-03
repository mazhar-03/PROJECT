import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Ghost {
    private int x, y;
    private Board board;
    private ImageIcon ghostImage;

    public Ghost(int x, int y, Board board, int imageIndex) {
        this.x = x;
        this.y = y;
        this.board = board;
        loadGhostImage(imageIndex);
    }

    private void loadGhostImage(int index) {
        String[] ghostImagePaths = {
                "images/ghost0.png",
                "images/ghost1.png",
                "images/ghost2.png",
                "images/ghost3.png",
                "images/ghost4.png"
        };
        URL imageUrl = getClass().getClassLoader().getResource(ghostImagePaths[index]);
        if (imageUrl == null) {
            System.out.println("Could not find image: " + ghostImagePaths[index]);
            return;
        }
        // scaling the image to the size of the game board's cell
        ghostImage = new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(board.getCellSize(), board.getCellSize(), Image.SCALE_SMOOTH));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageIcon getGhostImage() {
        return ghostImage;
    }

    public void chasePacman(int pacmanX, int pacmanY) {
        int dx = pacmanX - x;
        int dy = pacmanY - y;

        int newX = x;
        int newY = y;

        //if the difference is bigger on x-axis we come closer to the pacman's x-axis
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) newX++;
            else newX--;
        } else {
            if (dy > 0) newY++;
            else newY--;
        }

        if (!board.isWall(newX, newY)) {
            x = newX;
            y = newY;
        }
    }
}
