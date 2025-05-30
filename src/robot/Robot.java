package robot;

import Item.Item;

import java.util.ArrayList;
import java.util.List;

public class Robot {
    private int id;
    private int posicionX;
    private int posicionY;
    private static final int BATERIA_TOTAL = 100; //cantidad de celulas
    private int bateriaActual;
    private static final int FACTOR_CONSUMO = 1; //consumo de celulas
    List<Item> items;

    public Robot(int id, int posicionX, int posicionY) {
        this.items = new ArrayList<Item>();
        this.id = id;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
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
    public int getBateriaActual() {
        return bateriaActual;
    }
    public List<Item> getItems() {
        return items;
    }

    public void recargar(){
        bateriaActual = BATERIA_TOTAL;
    }
    public void consumirBateria(double distancia){
        bateriaActual-= (int) distancia*FACTOR_CONSUMO;

    }
}
