package Item;

public class Item {
    private int id;
    private String nombre;
    private TipoItem tipo;
    private int cantidad;

    public Item(int id, String nombre, TipoItem tipo, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    public int getCantidad() { return cantidad; }

    public String getNombre() { return nombre; }
}
