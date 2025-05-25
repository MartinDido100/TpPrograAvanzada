import cofre.Cofre;
import mapa.Mapa;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.DatosJson;
import utils.FileReader;

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
                robot -> new Robot(robot.getPosicionX(),robot.getPosicionY(),robot.getId())
        ).toList();

        List<Cofre> cofres = data.getCofres().stream().map(
                cofre -> new Cofre(cofre.getPosicionX(),cofre.getPosicionY(),cofre.getId(),cofre.getTipo())
        ).toList();


        EstacionRobot estacion = new EstacionRobot(new Mapa(data.getMapa().getCasilleros()),robopuertos, robots, cofres);

        estacion.mostrarVecinos();

    }
}