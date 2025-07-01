import Item.Item;
import cofre.*;
import mapa.Mapa;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.DatosJson;
import utils.FileReader;

import java.util.*;


public class Main {
    public static final String ROBOT_EMOJI = "🤖";
    public static final String COFRE = "\uD83D\uDCE6";
    public static final String ROBOPUERTO = "➕";
    public static final String RUTA_EMOJI = "\uD83D\uDEE3\uFE0F";

    public static void main(String[] args) {
        DatosJson data = FileReader.leerArchivo("./entrada/entrada.json");

        if (data == null) {
            System.out.println("Error al leer el archivo JSON");
            return;
        }

        List<Robopuerto> robopuertos = data.getMapa().getRobopuertos().stream().map(
                robopuerto -> new Robopuerto(robopuerto.getPosicionX(),robopuerto.getPosicionY(),robopuerto.getId())
        ).toList();

        List<Robot> robots = data.getRobots().stream().map(
                robot -> new Robot(robot.getId(),robot.getPosicionX(),robot.getPosicionY(),data.getBateriaRobots(),data.getFactorConsumo())
        ).toList();

        EstacionRobot estacion = new EstacionRobot(new Mapa(data.getMapa().getCasilleros()),robopuertos, robots);

        for (DatosJson.Cofre cofre : data.getCofres()) {
            switch (cofre.getTipo()) {
                case ACTIVO -> estacion.addCofreActivo(new CofreProvisionActiva(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(), cofre.getItemsOfrecidos()));
                case PASIVO -> estacion.addCofrePasivo(new CofreProvisionPasiva(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(), cofre.getItemsOfrecidos()));
                case BUFER -> estacion.addRequestChest(new CofreBuffer(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(), cofre.getItemsOfrecidos(), cofre.getSolicitudes()));
                case ALMACENAMIENTO -> estacion.addCofreAlmacenamiento(new CofreAlmacenamiento(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId()));
                case SOLICITUD -> {
                    List<Item> solicitudesConvertidas = new ArrayList<>();
                    for (DatosJson.Item item : cofre.getSolicitudes()) {
                        solicitudesConvertidas.add(new Item(
                                item.getId(),
                                item.getNombre(),
                                item.getTipo(),
                                item.getCantidad()
                        ));
                    }
                    estacion.addRequestChest(new CofreSolicitud(
                            cofre.getPosicionX(),
                            cofre.getPosicionY(),
                            cofre.getId(),
                            solicitudesConvertidas
                    ));
                }
                default -> throw new InputMismatchException("Tipo de cofre no reconocido");
            }
        }

        estacion.setup();
        estacion.getMapa().mostrarMapaConContorno(estacion.getRobopuertos(),estacion.getCofres());
        estacion.atenderPedidos();

        System.out.println("\n\nChequeando excedentes de los cofres...\n\n");
        estacion.chequearExcedentes();

        if(!estacion.pedidosNoCumplidos.isEmpty()){

            String mensaje = "EL SISTEMA NO ALCANZO UN ESTADO DE FINALIZACION ESTABLE";
            int ancho = mensaje.length() + 4;

            System.out.println("\n\t" + "┌" + "─".repeat(ancho) + "┐");
            System.out.println("\t" + "│  " + mensaje + "  │");
            System.out.println("\t" + "└" + "─".repeat(ancho) + "┘\n\n");

            System.out.println("Los pedidos que no se cumplieron fueron: ");

            Iterator<Map.Entry<CofreSolicitador, Item>> it = estacion.pedidosNoCumplidos.entrySet().iterator();

            while(it.hasNext()) {
                Map.Entry<CofreSolicitador, Item> entry = it.next();
                String key = entry.getKey().toString();
                String capitalizedKey = key.substring(0, 1).toUpperCase() + key.substring(1);
                System.out.println(capitalizedKey + ": " + entry.getValue());
            }
        }else{
            String mensaje = "EL SISTEMA ALCANZO UN ESTADO DE FINALIZACION ESTABLE";
            int ancho = mensaje.length() + 4;

            System.out.println("\n\t" + "┌" + "─".repeat(ancho) + "┐");
            System.out.println("\t" + "│  " + mensaje + "  │");
            System.out.println("\t" + "└" + "─".repeat(ancho) + "┘");
        }

        System.out.println("\n\nLos cofres de almacenamiento quedaron de la siguiente manera:...\n\n");
        estacion.mostrarAlmacenamiento();
    }
}