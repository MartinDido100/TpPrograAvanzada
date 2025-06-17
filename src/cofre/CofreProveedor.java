package cofre;

import Item.Item;

import java.util.Map;

public interface CofreProveedor {

    public void ofrecer(String item, int cantidad);
    public Map<String, Integer> getOfrecimientos();
}

