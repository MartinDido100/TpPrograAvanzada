package utils;

import robot.Robot;

import java.util.List;

public class ResultadoDijkstra {
    public Object nodo;
    public List<Object> camino;
    public double distancia;

    public ResultadoDijkstra(Object nodo, List<Object> camino, double distancia) {
        this.nodo = nodo;
        this.camino = camino;
        this.distancia = distancia;
    }

}
