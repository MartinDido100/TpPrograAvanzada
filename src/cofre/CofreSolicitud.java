package cofre;

import Item.Item;
import utils.DatosJson;

import java.util.ArrayList;
import java.util.List;

public class CofreSolicitud extends Cofre implements CofreSolicitador{
    protected List<Item> solicitudes;



    public CofreSolicitud(int posicionX, int posicionY, int id, List<Item> solicitudes) {
        super(posicionX, posicionY, id);
        this.solicitudes = new ArrayList<Item>();

        this.tipo = TipoCofre.SOLICITUD;


        for(Item item : solicitudes){
            this.solicitudes.add(new Item(item.getId(),item.getNombre(), item.getTipo(),item.getCantidad()));
        }

    }

    @Override
    public void cumplirSolicitud(Item item) {
        System.out.println("Se cumplio la solicitud del " + this);
    }

    public List<Item> getSolicitudes(){
        return this.solicitudes;
    }


}
