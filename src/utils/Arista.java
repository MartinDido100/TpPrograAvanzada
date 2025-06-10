package utils;

public class Arista implements Comparable<Arista> {
    public int origen;
    public int destino;
    public double distancia;
    public Arista(int origen, int destino, double distancia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;

    }

    @Override
    public int compareTo(Arista o) {
        return (int) (this.distancia - o.distancia);
    }
}
