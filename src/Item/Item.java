package Item;

public class Item {
    private int id;
    private String nombre;
    private TipoItem tipo;
    private int cantidad;

    public Item(int id, String nombre, String tipo, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;

        switch (tipo) {
            case "MATERIAL":
                this.tipo = TipoItem.MATERIAL;
                break;
            case "ROBOTECH":
                this.tipo = TipoItem.ROBOTECH;
                break;
            case "COFRE":
                this.tipo = TipoItem.COFRE;
                break;
                default:
                    this.tipo = TipoItem.MATERIAL;
        }
    }
    public Item(String nombre, int cantidad){
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.tipo = null;
        this.id = -1;
    }

    public int getCantidad() { return cantidad; }

    public String getNombre() { return nombre; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }


    @Override
    public String toString() {
        return nombre + " con cantidad " + cantidad;
    }

    public int getId() {
        return id;
    }


    public String getTipo() {
        return tipo.toString();
    }
}
