package cofre;

import Item.Item;
import Item.TipoItem;
import utils.DatosJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CofreSolicitud extends Cofre implements CofreSolicitador{
    protected List<Item> solicitudes;



    public CofreSolicitud(int posicionX, int posicionY, int id, List<DatosJson.Item> solicitudes) {
        super(posicionX, posicionY, id);
        this.solicitudes = new ArrayList<Item>();

        for(DatosJson.Item item : solicitudes){
            this.solicitudes.add(new Item(item.getId(),item.getNombre(), item.getTipo(),item.getCantidad()));
        }


    }


    @Override
    public void cumplirSolicitud(Item item) {
        this.solicitudes.remove(item);

    }

    public List<Item> getSolicitudes(){
        return this.solicitudes;
    }


}
