package cofre;

public abstract class Cofre {
    protected int posicionX;
    protected int posicionY;
    protected int id;

    public Cofre(int posicionX, int posicionY, int id) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.id = id;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public int getPosicionY() {
        return posicionY;
    }

    public int getId() {
        return id;
    }
}
