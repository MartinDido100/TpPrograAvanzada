package cofre;

import java.util.Map;

public class CofreProvisionPasiva extends Cofre implements CofreProveedor{
    public CofreProvisionPasiva(int posicionX, int posicionY, int id) {
        super(posicionX, posicionY, id);
    }

    @Override
    public void ofrecer(String item, int cantidad) {

    }

    @Override
    public Map<String, Integer> getOfrecimientos() {
        return Map.of();
    }
}
