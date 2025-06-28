import cofre.*;
import mapa.Mapa;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.DatosJson;
import utils.FileReader;
import utils.Grafo;

import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static final String ROBOT_EMOJI = "🤖";
    public static final String COFRE = "\uD83D\uDCE6";
    public static final String ROBOPUERTO = "➕";

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
                robot -> new Robot(robot.getId(),robot.getPosicionX(),robot.getPosicionY())
        ).toList();

        EstacionRobot estacion = new EstacionRobot(new Mapa(data.getMapa().getCasilleros()),robopuertos, robots);

        data.getCofres().forEach(
                cofre -> {
                    switch (cofre.getTipo()){
                        case ACTIVO -> {
                            estacion.addCofreActivo(new CofreProvisionActiva(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(),cofre.getItemsOfrecidos()));
                        }
                        case PASIVO -> {
                            estacion.addCofrePasivo(new CofreProvisionPasiva(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(),cofre.getItemsOfrecidos()));
                        }
                        case BUFER -> {
                            estacion.addRequestChest(new CofreBuffer(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(),cofre.getItemsOfrecidos(),cofre.getSolicitudes()));
                        }
                        case ALMACENAMIENTO -> {
                            estacion.addCofreAlmacenamiento(new CofreAlmacenamiento(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId()));
                        }
                        case SOLICITUD -> {
                            estacion.addRequestChest(new CofreSolicitud(cofre.getPosicionX(), cofre.getPosicionY(), cofre.getId(),cofre.getSolicitudes()));
                        }
                        default -> throw new InputMismatchException("Tipo de cofre no reconocido");
                    }
                }
        );

        estacion.setup();

        estacion.getMapa().mostrarMapaConContorno(estacion.getRobopuertos(),estacion.getCofres());
        estacion.getGrafo().mostrarMatriz();
        estacion.getGrafo().mostrarNodos();
        estacion.atenderPedidos();
        estacion.chequearExcedentes();


    }
}