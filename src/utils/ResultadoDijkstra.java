package utils;

import robot.Robot;

import java.util.List;

public class ResultadoDijkstra {
    public Robot robot;
    public List<Object> camino;
    public double distancia;

    public ResultadoDijkstra(Robot robot, List<Object> camino, double distancia) {
        this.robot = robot;
        this.camino = camino;
        this.distancia = distancia;
    }

}
