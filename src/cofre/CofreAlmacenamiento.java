package cofre;

import Item.Item;

import java.util.ArrayList;
import java.util.List;

public class CofreAlmacenamiento extends Cofre{
    private List<Item> almacenamiento;
    public CofreAlmacenamiento(int posicionX, int posicionY, int id) {

        super(posicionX, posicionY, id);

        this.tipo = TipoCofre.ALMACENAMIENTO;

        almacenamiento = new ArrayList<>();
    }

    public List<Item> getAlmacenamiento() {
        return almacenamiento;
    }

    public void almacenar(Item item) {
        almacenamiento.add(item);
    }
}
