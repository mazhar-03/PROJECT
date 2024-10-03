import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<HighScore> highScores;

    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }

    public void addHighScore(String name, int score) {
        highScores.add(new HighScore(name, score));
        Collections.sort(highScores, Comparator.comparingInt(HighScore::getScore).reversed());
        if (highScores.size() > 10) {
            highScores.remove(highScores.size() - 1);
        }
        saveHighScores();
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("highscores.txt"))) {
            //reads the serialized highScore's object
            highScores = (List<HighScore>) ois.readObject();
        } catch (FileNotFoundException e) {
            // it might be on the first time, so ignoring
        } catch (IOException | ClassNotFoundException e) {
            // If there's an error reading the file, reset the high scores
            System.out.println("Error reading high score file. Resetting high scores.");
            //creating empty array
            highScores = new ArrayList<>();
        }
    }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("highscores.txt"))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
