import java.io.Serializable;

public class Upgrade implements Serializable {
    private final int x, y;
    private final String type;

    public Upgrade(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void applyTo(Pacman pacman) {
        switch (type) {
            case "speed":
                pacman.applyUpgrade("speed");
                break;
            case "extraLife":
                pacman.applyUpgrade("extraLife");
                break;
            case "slowDownGhosts":
                pacman.applyUpgrade("slowDownGhosts");
                break;
            case "invincibility":
                pacman.applyUpgrade("invincibility");
                break;
            case "doubleScore":
                pacman.applyUpgrade("doubleScore");
                break;
            default:
                break;
        }
    }
}
