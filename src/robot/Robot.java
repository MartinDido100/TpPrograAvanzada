package robot;

import Item.Item;

import java.util.ArrayList;
import java.util.List;

public class Robot {
    private int id;
    private int posicionX;
    private int posicionY;
    private static final double BATERIA_TOTAL = 10; //cantidad de celulas
    private double bateriaActual;
    private static final double FACTOR_CONSUMO = 1.5; //consumo de celulas
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
    public double getBateriaActual() {
        return bateriaActual;
    }
    public List<Item> getItems() {
        return items;
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

    public void addItem(Item item){
        items.add(item);
    }

    public static double getFactorConsumo(){
        return FACTOR_CONSUMO;
    }

    public static double getBateriaTotal(){
        return BATERIA_TOTAL;
    }


}
