import static org.junit.Assert.*;

import Item.Item;
import cofre.*;
import mapa.Mapa;
import org.junit.Before;
import org.junit.Test;
import robopuerto.Robopuerto;
import robot.Robot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class EstacionRobotTest {

    Mapa mapa;
    Item item;


    @Before
    public void setup() {
        this.mapa = new Mapa(20);
        this.item = new Item(1, "item1", "MATERIAL", 10);
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

        CofreSolicitud cofreS = new CofreSolicitud(15, 19, 1, List.of(item));

        EstacionRobot estacion = new EstacionRobot(mapa, robopuertos, robots);
        estacion.addRequestChest(cofreS);
        estacion.setup();

        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salidaCapturada));

        estacion.atenderPedidos();

        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void ningunCofreOfreceTalItem() {
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 100, 1.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);

        estacion.setup();
        estacion.atenderPedidos();

        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void solicitudAccesibleProveedorNo() {
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 100, 1.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));
        CofreProvisionActiva cofrePA = new CofreProvisionActiva(18, 18, 1,Map.of(item.getNombre(),item.getCantidad()));

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);

        estacion.setup();
        estacion.atenderPedidos();

        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

}