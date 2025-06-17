package cofre;

import Item.Item;

import java.util.HashMap;
import java.util.Map;

public class CofreProvisionActiva extends Cofre implements CofreProveedor {
        protected Map<String, Integer> itemsOfrecidos;

    public CofreProvisionActiva(int posicionX, int posicionY, int id) {

        super(posicionX, posicionY, id);
        this.itemsOfrecidos = new HashMap<String, Integer>();
    }

    @Override
    public void ofrecer(String item, int cantidad) {
        int cantidadActual = itemsOfrecidos.get(item);
        if(cantidadActual - cantidad  == 0) {
            this.itemsOfrecidos.remove(item);
        }
        else {

            this.itemsOfrecidos.put(item,cantidadActual-cantidad);
        }

    }

    @Override
    public Map<String, Integer> getOfrecimientos() {
        return this.itemsOfrecidos;
    }
}
