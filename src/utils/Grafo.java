package utils;

import cofre.Cofre;
import robopuerto.Robopuerto;


import java.util.ArrayList;

public class Grafo {
    private double[][] matrizAdyacencia;
    private ArrayList<Object> nodos;

    public Grafo(ArrayList<Robopuerto> robopuertos, ArrayList<Cofre> cofres) {
        this.nodos = new ArrayList<>();
        nodos.addAll(robopuertos);
        nodos.addAll(cofres);

        int n = nodos.size();
        matrizAdyacencia = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrizAdyacencia[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
            }
        }

        construirMatriz(robopuertos, cofres);
    }

    private void construirMatriz(ArrayList<Robopuerto> robopuertos, ArrayList<Cofre> cofres) {
        for (int i = 0; i < robopuertos.size(); i++) {
            Robopuerto r1 = robopuertos.get(i);

            for (int j = 0; j < robopuertos.size(); j++) {
                if (i == j) continue;

                Robopuerto r2 = robopuertos.get(j);
                double distancia = calcularDistancia(r1.getPosicionX(), r1.getPosicionY(), r2.getPosicionX(), r2.getPosicionY());

                if (distancia <= r1.getAlcance()*2) {
                    matrizAdyacencia[i][j] = distancia;
                }else{
                    matrizAdyacencia[i][j] = Double.POSITIVE_INFINITY;
                }
            }

            for (int k = 0; k < cofres.size(); k++) {
                Cofre c = cofres.get(k);
                double distancia = calcularDistancia(r1.getPosicionX(), r1.getPosicionY(), c.getPosicionX(), c.getPosicionY());

                if (distancia <= r1.getAlcance()) {
                    int j = robopuertos.size() + k;
                    matrizAdyacencia[i][j] = distancia;
                }
            }
        }
    }

    private double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public void mostrarMatriz() {
        System.out.println("Matriz de Adyacencia:");
        for (double[] doubles : matrizAdyacencia) {
            for (double aDouble : doubles) {
                if (aDouble == Double.POSITIVE_INFINITY) {
                    System.out.print("INF ");
                } else {
                    System.out.printf("%.1f ", aDouble);
                }
            }
            System.out.println();
        }
    }

    public void mostrarNodos() {
        System.out.println("Nodos:");
        for (int i = 0; i < nodos.size(); i++) {
            System.out.println(i + ": " + nodos.get(i));
        }
    }
}
