package cofre;

public class Cofre {
    private int posicionX;
    private int posicionY;
    private int id;
    private String tipo;

    public Cofre(int posicionX, int posicionY, int id, String tipo) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.id = id;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }

}
