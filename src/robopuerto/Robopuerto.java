package robopuerto;

import cofre.Cofre;
import robot.Robot;

import java.util.ArrayList;

public class Robopuerto {
    private final int posicionX;
    private final int posicionY;
    private static final int alcance = 3;
    private final int id;
    private final ArrayList<Robopuerto> robopuertosVecinos;
    private final ArrayList<Cofre> cofresIncluidos;
    private final ArrayList<Robot> robotsActuales;

    public Robopuerto(int posicionX, int posicionY, int id) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.id = id;
        this.robopuertosVecinos = new ArrayList<>();
        this.cofresIncluidos = new ArrayList<>();
        this.robotsActuales = new ArrayList<>();
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

    public ArrayList<Cofre> getCofresIncluidos() {
        return cofresIncluidos;
    }

    public ArrayList<Robot> getRobotsActuales() { return robotsActuales; }

    public void addVecino(Robopuerto vecino) {
        this.robopuertosVecinos.add(vecino);
    }

    public void addCofreIncluido(Cofre cofre) {
        this.cofresIncluidos.add(cofre);
    }
}
