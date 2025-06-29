package utils;
import cofre.TipoCofre;

import java.util.List;
import java.util.Map;

public class DatosJson {
    private Mapa mapa;
    private List<Robot> robots;
    private List<Cofre> cofres;
    private List<Item> items;
    private double bateriaRobots;
    private double factorConsumo;

    public Mapa getMapa() {
        return mapa;
    }

    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public void setRobots(List<Robot> robots) {
        this.robots = robots;
    }

    public List<Cofre> getCofres() {
        return cofres;
    }

    public void setCofres(List<Cofre> cofres) {
        this.cofres = cofres;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public double getBateriaRobots() {
        return bateriaRobots;
    }

    public void setBateriaRobots(double bateriaRobots) {
        this.bateriaRobots = bateriaRobots;
    }

    public double getFactorConsumo() {
        return factorConsumo;
    }

    public void setFactorConsumo(double factorConsumo) {
        this.factorConsumo = factorConsumo;
    }

    public static class Mapa {
        private List<Robopuerto> robopuertos;
        private int casilleros;

        public List<Robopuerto> getRobopuertos() {
            return robopuertos;
        }

        public void setRobopuertos(List<Robopuerto> robopuertos) {
            this.robopuertos = robopuertos;
        }

        public int getCasilleros() {
            return casilleros;
        }

        public void setCasilleros(int casilleros) {
            this.casilleros = casilleros;
        }
    }

    public static class Robopuerto {
        private int posicionX;
        private int posicionY;
        private int id;

        public int getPosicionX() {
            return posicionX;
        }

        public void setPosicionX(int posicionX) {
            this.posicionX = posicionX;
        }

        public int getPosicionY() {
            return posicionY;
        }

        public void setPosicionY(int posicionY) {
            this.posicionY = posicionY;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Robot {
        private int id;
        private int posicionX;
        private int posicionY;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPosicionX() {
            return posicionX;
        }

        public void setPosicionX(int posicionX) {
            this.posicionX = posicionX;
        }

        public int getPosicionY() {
            return posicionY;
        }

        public void setPosicionY(int posicionY) {
            this.posicionY = posicionY;
        }
    }

    public static class Cofre {
        private int posicionX;
        private int posicionY;
        private int id;
        private TipoCofre tipo;
        private Map<String, Integer> itemsOfrecidos;
        private List<Item> solicitudes;

        public int getPosicionX() {
            return posicionX;
        }

        public void setPosicionX(int posicionX) {
            this.posicionX = posicionX;
        }

        public int getPosicionY() {
            return posicionY;
        }

        public void setPosicionY(int posicionY) {
            this.posicionY = posicionY;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public TipoCofre getTipo() {
            return tipo;
        }

        public void setTipo(TipoCofre tipo) {
            this.tipo = tipo;
        }

        public Map<String, Integer> getItemsOfrecidos(){ return itemsOfrecidos; }

        public void setItemsOfrecidos(Map<String, Integer> itemsOfrecidos){ this.itemsOfrecidos = itemsOfrecidos; }

        public List<Item> getSolicitudes(){ return solicitudes; }

        public void setSolicitudes(List<Item> solicitudes){ this.solicitudes = solicitudes; }
    }

    public static class Item {
        private int id;
        private String nombre;
        private String tipo;
        private int cantidad;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public int getCantidad() {return cantidad;}

        public void setCantidad(int cantidad) { this.cantidad = cantidad;}
    }
}



