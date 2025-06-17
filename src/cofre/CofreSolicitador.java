package cofre;

import Item.Item;

import java.util.List;
import java.util.Map;

public interface CofreSolicitador {
    public List<Item> getSolicitudes();
    public void cumplirSolicitud(Item item);
}
