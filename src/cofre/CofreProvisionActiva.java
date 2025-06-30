package cofre;

import java.util.Map;

public class CofreProvisionActiva extends Cofre implements CofreProveedor {
        protected Map<String, Integer> itemsOfrecidos;

    public CofreProvisionActiva(int posicionX, int posicionY, int id, Map<String, Integer> itemsOfrecidos) {

        super(posicionX, posicionY, id);
        this.itemsOfrecidos = itemsOfrecidos;

        this.tipo = TipoCofre.ACTIVO;
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
