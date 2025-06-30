package utils;

import java.util.List;

public class ResultadoRutas {
    public Object nodo;
    public List<Object> camino;
    public double distancia;

    public ResultadoRutas(Object nodo, List<Object> camino,double distancia) {
        this.nodo = nodo;
        this.camino = camino;
        this.distancia = distancia;
    }
}
