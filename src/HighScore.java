import java.io.Serializable;

public class HighScore implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int score;

    public HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }
}
