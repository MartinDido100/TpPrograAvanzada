package cofre;

import Item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CofreAlmacenamiento extends Cofre{
    private Map<String,Integer> almacenamiento;
    public CofreAlmacenamiento(int posicionX, int posicionY, int id) {

        super(posicionX, posicionY, id);

        this.tipo = TipoCofre.ALMACENAMIENTO;

        almacenamiento = new HashMap<>();
    }

    public Map<String,Integer> getAlmacenamiento() {
        return almacenamiento;
    }

    public void almacenar(Item item) {
        if(almacenamiento.containsKey(item.getNombre())){
            int cantidad = almacenamiento.get(item.getNombre());
            cantidad += item.getCantidad();
            almacenamiento.put(item.getNombre(),cantidad);
        }
        else{
            almacenamiento.put(item.getNombre(),item.getCantidad());
        }

    }
}
