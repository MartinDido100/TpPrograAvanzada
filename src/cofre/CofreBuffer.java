package cofre;

public class CofreBuffer extends Cofre implements CofreProveedor,CofreSolicitador{
    public CofreBuffer(int posicionX, int posicionY, int id) {
        super(posicionX, posicionY, id);
    }
}
