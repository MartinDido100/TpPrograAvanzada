import cofre.*;
import mapa.Mapa;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.Grafo;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class EstacionRobot {
    Mapa mapa;
    List<Robopuerto> robopuertos;
    List<Robot> robots;
    List<CofreProvisionActiva> cofresActivos;
    List<CofreProvisionPasiva> cofresPasivos;
    List<CofreAlmacenamiento> cofresAlmacenamiento;
    List<Cofre> pedidos;
    List<Cofre>cofres;
    Grafo grafo;

    public EstacionRobot(Mapa mapa, List<Robopuerto> robopuertos, List<Robot> robots) {
        this.mapa = mapa;
        this.robopuertos = robopuertos;
        this.robots = robots;
        this.cargarMapa();
    }

    public void mostrarVecinos() { //DEBUG
        for (Robopuerto robopuerto : this.robopuertos) {
            System.out.println("Robopuerto " + robopuerto.getId() + " tiene los siguientes robopuertos vecinos:");
            for (Robopuerto vecino : robopuerto.getRobopuertosVecinos()) {
                System.out.println("Robopuerto " + vecino.getId());
            }
        }
    }

    public void setup(){
        List<Cofre> cofres = new ArrayList<>();
        cofres.addAll(cofresPasivos);
        cofres.addAll(cofresActivos);
        cofres.addAll(cofresAlmacenamiento);
        cofres.addAll(pedidos);
        this.cofres = cofres;

        this.calcularRobopuertosVecinos();
        this.grafo = new Grafo(new ArrayList<>(robopuertos),new ArrayList<>(cofres));
    }

    public void addCofrePasivo(CofreProvisionPasiva cofre) {
        if (cofre != null) {
            this.cofresPasivos.add(cofre);
            mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
        }
    }

    public void addCofreActivo(CofreProvisionActiva cofre) {
        if (cofre != null) {
            this.cofresActivos.add(cofre);
            mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
        }
    }

    public void addCofreAlmacenamiento(CofreAlmacenamiento cofre) {
        if (cofre != null) {
            this.cofresAlmacenamiento.add(cofre);
            mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
        }
    }

    public void addRequestChest(Cofre requestChest) {
        if (requestChest != null) {
            this.pedidos.add(requestChest);
            mapa.setValue(requestChest.getPosicionX(), requestChest.getPosicionY(), Main.COFRE);
        }
    }

    public void atenderPedidos(){
        for (Cofre cofre : this.pedidos) {
            this.grafo.obtenerRobotMasCercanoYCaminoACofre(cofre, new ArrayList<>(this.robots));
        }
    }

    public void calcularRobopuertosVecinos() {
        for (Robopuerto robopuerto : this.robopuertos) {
            for (Robopuerto otroRobopuerto : this.robopuertos) {
                double distancia = Math.sqrt(Math.pow(otroRobopuerto.getPosicionX() - robopuerto.getPosicionX(), 2) + Math.pow(otroRobopuerto.getPosicionY() - robopuerto.getPosicionY(), 2));
                if (robopuerto != otroRobopuerto && (robopuerto.getAlcance()*2) >= distancia) {
                    robopuerto.getRobopuertosVecinos().add(otroRobopuerto);
                }
            }
        }

        for (Robopuerto robopuerto : this.robopuertos) {
            List<Robopuerto> vecinosOriginales = new ArrayList<>(robopuerto.getRobopuertosVecinos());

            for (Robopuerto vecino : vecinosOriginales) {
                for (Robopuerto vecinoDelVecino : vecino.getRobopuertosVecinos()) {
                    if (!robopuerto.getRobopuertosVecinos().contains(vecinoDelVecino)
                            && !vecinoDelVecino.equals(robopuerto)) {
                        robopuerto.getRobopuertosVecinos().add(vecinoDelVecino);
                    }
                }
            }
        }

        for (Robopuerto robopuerto : this.robopuertos) {
            for (Cofre cofre : this.cofres) {
                double distancia = Math.sqrt(Math.pow(cofre.getPosicionX() - robopuerto.getPosicionX(), 2) + Math.pow(cofre.getPosicionY() - robopuerto.getPosicionY(), 2));
                if (robopuerto.getAlcance() >= distancia) {
                    robopuerto.getCofresIncluidos().add(cofre);
                }
            }
        }
    }

    public void cargarMapa() {

        for (Robopuerto robopuerto : this.robopuertos) {
            if (robopuerto.getPosicionX() == 0 && robopuerto.getPosicionY() == 0) {
                throw new InputMismatchException("Error en el archivo de entrada: posicion del robopuerto fuera de rango");
            }else{
                mapa.setValue(robopuerto.getPosicionX(), robopuerto.getPosicionY(), Main.ROBOPUERTO);
            }
        }

        for (Cofre cofre : cofres) {
            if (mapa.getValue(cofre.getPosicionX(), cofre.getPosicionY()).equals(Main.ROBOPUERTO)) {
                throw new InputMismatchException("Error en el archivo de entrada: no se puede colocar un cofre en la misma posicion que un robopuerto");
            } else if (cofre.getPosicionX() > mapa.getCasilleros() || cofre.getPosicionY() > mapa.getCasilleros()) {
                throw new InputMismatchException("Error en el archivo de entrada: posicion del cofre fuera de rango");
            }else{
                mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
            }
        }

        for (Robot robot : robots) {
            if (mapa.getValue(robot.getPosicionX(), robot.getPosicionY()).equals(Main.COFRE) || mapa.getValue(robot.getPosicionX(), robot.getPosicionY()).equals(Main.ROBOPUERTO)) {
                throw new InputMismatchException("Error en el archivo de entrada: no se puede colocar un robot en la misma posicion que un cofre o un robopuerto");
            } else if (robot.getPosicionX() > mapa.getCasilleros() || robot.getPosicionY() > mapa.getCasilleros()) {
                throw new InputMismatchException("Error en el archivo de entrada: posicion del robot fuera de rango");
            } else{
                mapa.setValue(robot.getPosicionX(), robot.getPosicionY(), Main.ROBOT_EMOJI);
            }
        }
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public List<Robopuerto> getRobopuertos() {
        return robopuertos;
    }

    public List<Cofre> getCofres() {
        return cofres;
    }
}
