package mapa;

import robopuerto.Robopuerto;

import java.util.ArrayList;

public class Mapa {
    private int casilleros;
    //Cofres:■, Robopuertos:+, Robots: 🤖
    private String[][] mapa;

    public Mapa(int casilleros) {
        this.casilleros = casilleros;
        this.mapa = new String[casilleros][casilleros];
        for (int i = 0; i < casilleros; i++) {
            for (int j = 0; j < casilleros; j++) {
                mapa[i][j] = " ";
            }
        }
    }

    public String getValue(int x, int y) {
        if (x >= 0 && x < casilleros && y >= 0 && y < casilleros) {
            return mapa[x][y];
        } else {
            System.out.println("Posicion fuera de rango");
            return null;
        }
    }

    public void setValue(int x, int y, String value) {
        if (x >= 0 && x < casilleros && y >= 0 && y < casilleros) {
            mapa[x][y] = value;
        } else {
            System.out.println("Posicion fuera de rango");
        }
    }

    public int getCasilleros() {
        return casilleros;
    }

    public void mostrarMapa() {
        System.out.print("  ");
        for (int j = 0; j < casilleros; j++) {
            System.out.print("---");
        }
        System.out.println();

        for (int i = 0; i < casilleros; i++) {
            System.out.print("|");
            for (int j = 0; j < casilleros; j++) {
                String contenido = mapa[i][j];
                System.out.print(String.format(" %-2s", contenido));
            }
            System.out.println("|");
        }

        // Imprimir borde inferior
        System.out.print("  ");
        for (int j = 0; j < casilleros; j++) {
            System.out.print("---");
        }
        System.out.println();
    }



}
