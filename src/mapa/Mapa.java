package mapa;

import cofre.Cofre;
import robopuerto.Robopuerto;

import java.util.ArrayList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Mapa {
    private int casilleros;
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

        System.out.print("  ");
        for (int j = 0; j < casilleros; j++) {
            System.out.print("---");
        }
        System.out.println();
    }

    public void mostrarMapaConContorno(List<Robopuerto> robopuertos, List<Cofre> cofres) {
        boolean[][] contorno = new boolean[casilleros][casilleros];

        // Marcar contorno (solo borde exterior alcance=3)
        for (Robopuerto r : robopuertos) {
            int x = r.getPosicionX();
            int y = r.getPosicionY();
            int alcance = r.getAlcance();

            for (int i = x - alcance; i <= x + alcance; i++) {
                for (int j = y - alcance; j <= y + alcance; j++) {
                    if (i >= 0 && i < casilleros && j >= 0 && j < casilleros) {
                        // Distancia manhattan o max abs para anillo exterior:
                        int dx = Math.abs(i - x);
                        int dy = Math.abs(j - y);

                        // Solo los que están en el borde exterior (max(dx, dy) == alcance)
                        if (Math.max(dx, dy) == alcance) {
                            contorno[i][j] = true;
                        }
                    }
                }
            }
        }

        System.out.print("  ");
        for (int j = 0; j < casilleros; j++) {
            System.out.print("---");
        }
        System.out.println();

        for (int i = 0; i < casilleros; i++) {
            System.out.print("|");
            for (int j = 0; j < casilleros; j++) {
                String contenido = mapa[i][j];
                if (contenido == null) contenido = " ";

                // Verificar si hay cofre en (i, j)
                int finalI = i;
                int finalJ = j;
                boolean hayCofre = cofres.stream().anyMatch(c -> c.getPosicionX() == finalI && c.getPosicionY() == finalJ);

                if (contenido.equals("➕")) {
                    // Robopuerto
                    System.out.print(String.format(" %-2s", contenido));
                } else if (contorno[i][j] && !hayCofre) {
                    // Pintar contorno solo si NO hay cofre
                    System.out.print(" # ");
                } else {
                    // Normal (incluye cofres y otras cosas)
                    System.out.print(String.format(" %-2s", contenido));
                }
            }
            System.out.println("|");
        }

        System.out.print("  ");
        for (int j = 0; j < casilleros; j++) {
            System.out.print("---");
        }
        System.out.println();
    }

}
