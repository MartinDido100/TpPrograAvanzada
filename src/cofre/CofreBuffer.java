package cofre;

import Item.Item;
import utils.DatosJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CofreBuffer extends Cofre implements CofreProveedor,CofreSolicitador{
    protected Map<String,Integer> itemsOfrecidos;
    protected List<Item> solicitudes;
    public CofreBuffer(int posicionX, int posicionY, int id, Map<String,Integer> itemsOfrecidos, List<DatosJson.Item> solicitudes) {

        super(posicionX, posicionY, id);
        this.itemsOfrecidos = itemsOfrecidos;
        this.solicitudes = new ArrayList<>();
        this.tipo = TipoCofre.BUFER;

        for(DatosJson.Item item : solicitudes){
            this.solicitudes.add(new Item(item.getId(),item.getNombre(), item.getTipo(),item.getCantidad()));
        }
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
        System.out.println("Se cumplio la solicitud del item " + item);
    }
}
