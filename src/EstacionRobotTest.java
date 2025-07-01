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
import java.util.HashMap;
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
        estacion.addCofreActivo(cofrePA);

        estacion.setup();
        estacion.atenderPedidos();

        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

   @Test
    public void almacenamientoNoAccesible() {
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 100, 1.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));
        CofreAlmacenamiento cofreA = new CofreAlmacenamiento(18,18,3);
       HashMap<String,Integer> items = new HashMap<>();
       items.put(item.getNombre(), 20);
        CofreProvisionActiva cofrePA = new CofreProvisionActiva(4, 3, 1,items);



        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);
        estacion.addCofreActivo(cofrePA);
        estacion.addCofreAlmacenamiento(cofreA);

        estacion.setup();
        estacion.atenderPedidos();
        estacion.chequearExcedentes();
        assertTrue(cofreA.getAlmacenamiento().isEmpty());
    }

    @Test
    public void EntregaEnDosPartes() {
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 100, 1.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));
        CofreAlmacenamiento cofreA = new CofreAlmacenamiento(18,18,3);
        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 5);
        HashMap<String,Integer> items2 = new HashMap<>();
        items2.put(item.getNombre(), 5);
        CofreProvisionActiva cofrePA = new CofreProvisionActiva(4, 3, 1,items);
        CofreProvisionActiva cofrePA2 = new CofreProvisionActiva(5, 8, 1,items2);



        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);
        estacion.addCofreActivo(cofrePA);
        estacion.addCofreActivo(cofrePA2);

        estacion.setup();
        estacion.atenderPedidos();
        assertTrue(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void robotConMuchoConsumo(){
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 100, 1000.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));
        CofreAlmacenamiento cofreA = new CofreAlmacenamiento(18,18,3);
        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 5);
        HashMap<String,Integer> items2 = new HashMap<>();
        items2.put(item.getNombre(), 5);
        CofreProvisionActiva cofrePA = new CofreProvisionActiva(4, 3, 1,items);
        CofreProvisionActiva cofrePA2 = new CofreProvisionActiva(5, 8, 1,items2);



        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);
        estacion.addCofreActivo(cofrePA);
        estacion.addCofreActivo(cofrePA2);

        estacion.setup();
        estacion.atenderPedidos();
        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }
    @Test
    public void robotConPocaBateria(){
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 1, 1.0);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));
        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 5);
        HashMap<String,Integer> items2 = new HashMap<>();
        items2.put(item.getNombre(), 5);
        CofreProvisionActiva cofrePA = new CofreProvisionActiva(4, 3, 1,items);
        CofreProvisionActiva cofrePA2 = new CofreProvisionActiva(5, 8, 1,items2);



        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);
        estacion.addCofreActivo(cofrePA);
        estacion.addCofreActivo(cofrePA2);

        estacion.setup();
        estacion.atenderPedidos();
        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void excedeCapacidadDeCarga(){ // deberia hacer 2 viajes y completarlo
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 10, 1.5);

        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item));
        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 10);

        CofreProvisionActiva cofrePA = new CofreProvisionActiva(4, 3, 1,items);

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);
        estacion.addCofreActivo(cofrePA);

        estacion.setup();
        estacion.atenderPedidos();
        assertTrue(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void dosPedidosYPuedeTraerSoloUno(){
        Robopuerto r = new Robopuerto(5, 5, 1);
        Robot robot = new Robot(1, 5, 5, 10, 1.5);
        Item item2 = new Item(2, "item2", "MATERIAL", 10);
        CofreSolicitud cofreS = new CofreSolicitud(6, 6, 1, List.of(item,item2));
        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 10);

        CofreProvisionActiva cofrePA = new CofreProvisionActiva(4, 3, 1,items);

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r), List.of(robot));
        estacion.addRequestChest(cofreS);
        estacion.addCofreActivo(cofrePA);

        estacion.setup();
        estacion.atenderPedidos();
        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void zonasDisjuntas(){
        Robopuerto r1 = new Robopuerto(3, 3, 1);
        Robopuerto r2 = new Robopuerto(15,15,2);

        Robot robot1 = new Robot(1, 3, 3, 10, 1.5);
        Robot robot2 = new Robot(2,15,15,10,1.5);
        Item item2 = new Item(2, "item2", "MATERIAL", 10);
        CofreSolicitud cofreS1 = new CofreSolicitud(3, 4, 1, List.of(item));
        CofreSolicitud cofreS2 = new CofreSolicitud(15,16,2,List.of(item2));
        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 10);
        HashMap<String,Integer> items2 = new HashMap<>();
        items2.put(item2.getNombre(), 10);

        CofreProvisionActiva cofrePA1 = new CofreProvisionActiva(4, 3, 1,items);
        CofreProvisionActiva cofrePA2 = new CofreProvisionActiva(15, 17, 1,items2);

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r1,r2), List.of(robot1,robot2));
        estacion.addRequestChest(cofreS1);
        estacion.addRequestChest(cofreS2);
        estacion.addCofreActivo(cofrePA1);
        estacion.addCofreActivo(cofrePA2);

        estacion.setup();
        estacion.atenderPedidos();
        assertTrue(estacion.pedidosNoCumplidos.isEmpty());
    }

    @Test
    public void zonasDisjuntasCofreSolicitudSinCobertura(){
        Robopuerto r1 = new Robopuerto(3, 3, 1);
        Robopuerto r2 = new Robopuerto(15,15,2);

        Robot robot1 = new Robot(1, 3, 3, 10, 1.5);
        Robot robot2 = new Robot(2,15,15,10,1.5);
        CofreSolicitud cofreS1 = new CofreSolicitud(9, 9, 1, List.of(item));

        HashMap<String,Integer> items = new HashMap<>();
        items.put(item.getNombre(), 10);


        CofreProvisionActiva cofrePA1 = new CofreProvisionActiva(4, 3, 1,items);

        EstacionRobot estacion = new EstacionRobot(mapa, List.of(r1,r2), List.of(robot1,robot2));
        estacion.addRequestChest(cofreS1);

        estacion.addCofreActivo(cofrePA1);


        estacion.setup();
        estacion.atenderPedidos();
        assertFalse(estacion.pedidosNoCumplidos.isEmpty());
    }

}