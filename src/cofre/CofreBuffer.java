package cofre;

import Item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CofreBuffer extends Cofre implements CofreProveedor,CofreSolicitador{
    protected Map<String,Integer> itemsOfrecidos;
    protected List<Item> solicitudes;
    public CofreBuffer(int posicionX, int posicionY, int id) {

        super(posicionX, posicionY, id);
        itemsOfrecidos = new HashMap<String,Integer>();
        solicitudes = new ArrayList<Item>();
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
        return  itemsOfrecidos;
    }

    @Override
    public List<Item> getSolicitudes() {
        return solicitudes;
    }

    @Override
    public void cumplirSolicitud(Item item) {
        this.solicitudes.remove(item);
    }
}
