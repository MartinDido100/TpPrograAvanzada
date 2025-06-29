package cofre;

import Item.Item;

import java.util.List;

public interface CofreSolicitador {
    List<Item> getSolicitudes();
    void cumplirSolicitud(Item item);
}
