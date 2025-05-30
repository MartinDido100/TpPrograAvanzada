package robopuerto;

import java.util.ArrayList;

public class Robopuerto {
    private int posicionX;
    private int posicionY;
    private static final int alcance = 3;
    private int id;
    private ArrayList<Robopuerto> robopuertosVecinos;

    public Robopuerto(int posicionX, int posicionY, int id) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.id = id;
        this.robopuertosVecinos = new ArrayList<>();
    }

    public int getPosicionX() {
        return posicionX;
    }

    public int getPosicionY() {
        return posicionY;
    }

    public int getAlcance() {
        return alcance;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Robopuerto> getRobopuertosVecinos() {
        return robopuertosVecinos;
    }
}
