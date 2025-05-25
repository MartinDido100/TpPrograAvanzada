package robot;

public class Robot {
    private int id;
    private int posicionX;
    private int posicionY;

    public Robot(int id, int posicionX, int posicionY) {
        this.id = id;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    public int getId() {
        return id;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public int getPosicionY() {
        return posicionY;
    }
}
