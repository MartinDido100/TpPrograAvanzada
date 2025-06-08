package utils;

import robot.Robot;

import java.util.List;

public class ResultadoDijkstra {
    public Robot robot;
    public List<Object> camino;

    public ResultadoDijkstra(Robot robot, List<Object> camino) {
        this.robot = robot;
        this.camino = camino;
    }

}
