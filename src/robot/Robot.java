package robot;

import Item.Item;

import java.util.ArrayList;
import java.util.List;

public class Robot {
    private final int id;
    private final int posicionX;
    private final int posicionY;
    private static double BATERIA_TOTAL = 10; //cantidad de celulas
    private double bateriaActual;
    private static double FACTOR_CONSUMO = 1.5; //consumo de celulas
    private static int capacidadCarga = 5;
    List<Item> items;

    public Robot(int id, int posicionX, int posicionY, double bateriaTotal, double factorConsumo) {
        this.items = new ArrayList<Item>();
        this.id = id;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        if (bateriaTotal > 0) {
            BATERIA_TOTAL = bateriaTotal;
        }
        if (factorConsumo > 0) {
            FACTOR_CONSUMO = factorConsumo;
        }
        bateriaActual = BATERIA_TOTAL;
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
    public double getBateriaActual() {
        return bateriaActual;
    }

    public void recargar(){
        bateriaActual = BATERIA_TOTAL;
    }
    public void consumirBateria(double distancia){
        bateriaActual-=  distancia*FACTOR_CONSUMO;

    }

    public boolean alcanzaBateria(double distancia){
        return bateriaActual >= distancia*FACTOR_CONSUMO;
    }


    public static double getFactorConsumo(){
        return FACTOR_CONSUMO;
    }


    @Override
    public String toString() {
        return "Robot con id " + id;
    }

    public static double getBateriaTotal(){
        return BATERIA_TOTAL;
    }


    public static int getCapacidadCarga() {
        return capacidadCarga;
    }

}
