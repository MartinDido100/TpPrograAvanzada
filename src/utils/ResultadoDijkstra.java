package utils;

import robot.Robot;

import java.util.List;

public class ResultadoDijkstra {
    public double[] distancias;
    public int[] sucesores;

    public ResultadoDijkstra(double[] distancias, int[] sucesores) {
        this.distancias = distancias;
        this.sucesores = sucesores;

    }

}
