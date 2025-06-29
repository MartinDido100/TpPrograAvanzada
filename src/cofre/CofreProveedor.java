package cofre;

import java.util.Map;

public interface CofreProveedor {
    void ofrecer(String item, int cantidad);
    Map<String, Integer> getOfrecimientos();
}

