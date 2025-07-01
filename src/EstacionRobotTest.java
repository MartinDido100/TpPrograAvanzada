import static org.junit.Assert.*;

import Item.Item;
import Item.TipoItem;
import cofre.*;
import mapa.Mapa;
import org.junit.Before;
import org.junit.Test;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.DatosJson;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class EstacionRobotTest {

    Mapa mapa;
    DatosJson.Item itemJson;


    @Before
    public void setup() {
        this.mapa = new Mapa(20);
        itemJson = new DatosJson.Item();
        itemJson.setId(1);
        itemJson.setNombre("item1");
        itemJson.setTipo("MATERIAL");
        itemJson.setCantidad(10);
    }

    @Test
    public void cofresFueraDelAreaCobertura() {
        List<Robopuerto> robopuertos = List.of(
                new Robopuerto(5, 6, 1),
                new Robopuerto(9, 10, 2)
        );

        List <Robot> robots = List.of(
                new Robot(1, 5, 6, 100, 1.5)
        );

        CofreSolicitud cofreS = new CofreSolicitud(15, 19, 1, List.of(itemJson));

        EstacionRobot estacion = new EstacionRobot(mapa, robopuertos, robots);
        estacion.addRequestChest(cofreS);
        estacion.setup();

        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        PrintStream salidaOriginal = System.out;
        System.setOut(new PrintStream(salidaCapturada));

        estacion.atenderPedidos();

        System.setOut(salidaOriginal);

        String salida = salidaCapturada.toString();
        assertTrue("Debe imprimirse que el cofre está fuera de cobertura",
                salida.contains("El cofre no tiene a ningun robopuerto en cobertura"));

        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void ningunCofreOfreceTalItem() {
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 100, 1.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(itemJson)); // pide item1

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);

        estacion.setup();
        estacion.atenderPedidos();

        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void pedidoRequiereMultiplesViajesPorCapacidad() {
        Robopuerto r = new Robopuerto(3, 3, 1);
        Robot robot = new Robot(1, 3, 3, 100, 1.0);

        Item itemGrande = new DatosJson.Item();
        itemGrande.setId(2);
        itemGrande.setNombre("itemGrande");
        itemGrande.setTipo("MATERIAL");
        itemGrande.setCantidad(10);

        CofreSolicitud cofreS = new CofreSolicitud(5, 5, 1, List.of(itemGrande));
        CofreActivo cofreActivo = new CofreActivo(4, 4, 2);
        cofreActivo.setItemsOfrecidos(Map.of("itemGrande", 10));

        mapa.agregarCofres(List.of(cofreS, cofreActivo));
        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.setup();
        estacion.atenderPedidos();

        assertTrue(estacion.pedidosNoCumplidos.isEmpty());
    }

}