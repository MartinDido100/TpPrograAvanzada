import Item.Item;
import cofre.*;
import mapa.Mapa;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.Grafo;
import utils.ResultadoDijkstra;

import java.util.*;

public class EstacionRobot {
    Mapa mapa;
    List<Robopuerto> robopuertos;
    List<Robot> robots;
    List<CofreProvisionActiva> cofresActivos;
    List<CofreProvisionPasiva> cofresPasivos;
    List<CofreAlmacenamiento> cofresAlmacenamiento;
    List<CofreSolicitador> pedidos;
    List<Cofre>cofres;
    List<CofreProveedor> cofresProveedores;
    Grafo grafo;

    public EstacionRobot(Mapa mapa, List<Robopuerto> robopuertos, List<Robot> robots) {
        this.mapa = mapa;
        this.robopuertos = robopuertos;
        this.robots = robots;
        this.pedidos = new ArrayList<>();
        this.cofresActivos = new ArrayList<>();
        this.cofresPasivos = new ArrayList<>();
        this.cofresAlmacenamiento = new ArrayList<>();
        this.cofres = new ArrayList<>();
        this.cofresProveedores = new ArrayList<>();
    }

    public void mostrarVecinos() { //DEBUG
        for (Robopuerto robopuerto : this.robopuertos) {
            System.out.println("Robopuerto " + robopuerto.getId() + " tiene los siguientes robopuertos vecinos:");
            for (Robopuerto vecino : robopuerto.getRobopuertosVecinos()) {
                System.out.println("Robopuerto " + vecino.getId());
            }
        }
    }

    public void setup(){
        List<Cofre> cofres = new ArrayList<>();
        cofres.addAll(cofresPasivos);
        cofres.addAll(cofresActivos);
        cofres.addAll(cofresAlmacenamiento);
        cofresProveedores.addAll(cofresActivos);
        cofresProveedores.addAll(cofresPasivos);

        for(CofreSolicitador c : pedidos){ // no se puede castear la lista entera
            cofres.add((Cofre)c);
            if(c instanceof CofreBuffer){
                cofresProveedores.add((CofreBuffer)c);
            }
        }
        this.cofres = cofres;
        this.cargarMapa();

        for(Robot rob : this.robots){
            for (Robopuerto robopuerto : this.robopuertos) {
                if (rob.getPosicionX() == robopuerto.getPosicionX() && rob.getPosicionY() == robopuerto.getPosicionY()) {
                    robopuerto.getRobotsActuales().add(rob);
                }
            }
        }

        this.calcularRobopuertosVecinos();
        for (Robopuerto robopuerto : this.robopuertos) {
            System.out.println(robopuerto.getRobopuertosVecinos());
        }
        this.grafo = new Grafo(new ArrayList<>(robopuertos),new ArrayList<>(cofres));
    }

    public void addCofrePasivo(CofreProvisionPasiva cofre) {
        if (cofre != null) {
            this.cofresPasivos.add(cofre);
            mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
        }
    }

    public void addCofreActivo(CofreProvisionActiva cofre) {
        if (cofre != null) {
            this.cofresActivos.add(cofre);
            mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
        }
    }

    public void addCofreAlmacenamiento(CofreAlmacenamiento cofre) {
        if (cofre != null) {
            this.cofresAlmacenamiento.add(cofre);
            mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
        }
    }

    public void addRequestChest(Cofre requestChest) {
        if (requestChest != null) {
            this.pedidos.add((CofreSolicitador) requestChest);
            mapa.setValue(requestChest.getPosicionX(), requestChest.getPosicionY(), Main.COFRE);
        }
    }

    public void atenderPedidos(){
        for (CofreSolicitador cofre : this.pedidos) {
            while(!cofre.getSolicitudes().isEmpty()){ // mientras quede pedidos
                Iterator<Item> it = cofre.getSolicitudes().iterator();

                while (it.hasNext()) {
                    Item itemSolicitado = it.next();

                    CofreProveedor proveedor = null;
                    int cantidadTotalUniverso = 0;

                    ResultadoDijkstra robopuertoMasCercanoCamino = grafo.obtenerRobopuertoMasCercano(cofre);

                    Robopuerto robopuertoMasCercano = (Robopuerto) robopuertoMasCercanoCamino.nodo;
                    for (Cofre p : robopuertoMasCercano.getCofresIncluidos()) { // solo los que pueden llegar a llegar
                        if(p instanceof CofreProveedor){
                            CofreProveedor proveedores = (CofreProveedor) p;
                            if (proveedores.getOfrecimientos().containsKey(itemSolicitado.getNombre())) {
                                cantidadTotalUniverso += proveedores.getOfrecimientos().get(itemSolicitado.getNombre());
                            }
                        }

                    }
                    if (itemSolicitado.getCantidad() > cantidadTotalUniverso) {
                        System.out.println("Se solicita " + itemSolicitado.getCantidad() + " y solo hay la siguiente cantidad en todo el universo: " + cantidadTotalUniverso);
                        it.remove();
                        continue;
                    }
                    for (CofreProvisionActiva prov : this.cofresActivos) { // prioridad
                        if (prov.getOfrecimientos().containsKey(itemSolicitado.getNombre())) {
                            proveedor = prov;
                        }
                        if (proveedor != null)
                            break;
                    }

                    boolean completado = realizarEntrega((Cofre) cofre, itemSolicitado, proveedor);

                    if (completado) {
                        it.remove(); // lo saco de solicitudes porque ya se entrego toda la cantidad
                    }
                }
            }



        }
    }

    public void calcularRobopuertosVecinos() {
        for (Robopuerto robopuerto : this.robopuertos) {
            for (Robopuerto otroRobopuerto : this.robopuertos) {
                double distancia = Math.sqrt(Math.pow(otroRobopuerto.getPosicionX() - robopuerto.getPosicionX(), 2) + Math.pow(otroRobopuerto.getPosicionY() - robopuerto.getPosicionY(), 2));
                if (robopuerto != otroRobopuerto && (robopuerto.getAlcance()*2) >= distancia) {
                    robopuerto.addVecino(otroRobopuerto);
                }
            }
        }

        for (Robopuerto robopuerto : this.robopuertos) {
            List<Robopuerto> vecinosOriginales = new ArrayList<>(robopuerto.getRobopuertosVecinos());

            for (Robopuerto vecino : vecinosOriginales) {
                for (Robopuerto vecinoDelVecino : vecino.getRobopuertosVecinos()) {
                    if (!robopuerto.getRobopuertosVecinos().contains(vecinoDelVecino)
                            && !vecinoDelVecino.equals(robopuerto)) {
                        robopuerto.addVecino(vecinoDelVecino);
                    }
                }
            }
        }

        for (Robopuerto robopuerto : this.robopuertos) {
            for (Cofre cofre : this.cofres) {
                double distancia = Math.sqrt(Math.pow(cofre.getPosicionX() - robopuerto.getPosicionX(), 2) + Math.pow(cofre.getPosicionY() - robopuerto.getPosicionY(), 2));
                if (robopuerto.getAlcance() >= distancia) {
                    robopuerto.addCofreIncluido(cofre);
                }
            }
        }

        for (Robopuerto robopuerto : this.robopuertos) {
            List<Robopuerto> vecinos = robopuerto.getRobopuertosVecinos();
            for (Robopuerto vecino : vecinos) {
                for (Cofre cofre : vecino.getCofresIncluidos()) {
                    if (!robopuerto.getCofresIncluidos().contains(cofre)) {
                        robopuerto.addCofreIncluido(cofre);
                    }
                }
            }
        }
    }

    public void cargarMapa() {

        for (Robopuerto robopuerto : this.robopuertos) {
            if (robopuerto.getPosicionX() == 0 && robopuerto.getPosicionY() == 0) {
                throw new InputMismatchException("Error en el archivo de entrada: posicion del robopuerto fuera de rango");
            }else{
                mapa.setValue(robopuerto.getPosicionX(), robopuerto.getPosicionY(), Main.ROBOPUERTO);
            }
        }

        for (Cofre cofre : cofres) {
            if (mapa.getValue(cofre.getPosicionX(), cofre.getPosicionY()).equals(Main.ROBOPUERTO)) {
                throw new InputMismatchException("Error en el archivo de entrada: no se puede colocar un cofre en la misma posicion que un robopuerto");
            } else if (cofre.getPosicionX() > mapa.getCasilleros() || cofre.getPosicionY() > mapa.getCasilleros()) {
                throw new InputMismatchException("Error en el archivo de entrada: posicion del cofre fuera de rango");
            }else{
                mapa.setValue(cofre.getPosicionX(), cofre.getPosicionY(), Main.COFRE);
            }
        }

        for (Robot robot : robots) {
            if (robot.getPosicionX() > mapa.getCasilleros() || robot.getPosicionY() > mapa.getCasilleros()) {
                throw new InputMismatchException("Error en el archivo de entrada: posicion del robot fuera de rango");
            } else{
                mapa.setValue(robot.getPosicionX(), robot.getPosicionY(), Main.ROBOT_EMOJI);
            }
        }
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public List<Robopuerto> getRobopuertos() {
        return robopuertos;
    }

    public List<Cofre> getCofres() {
        return cofres;
    }

    public boolean realizarEntrega(Cofre cofre, Item itemSolicitado, CofreProveedor proveedor){
        ResultadoDijkstra rutaCofreARobot = this.grafo.obtenerRobopuertoConRobotMasCercano(cofre); // me devuelve el robopuerto con robot disponible mas cercano
        boolean completado = false; // si no complete totalmente el pedido, no lo saco de la lista
        if(rutaCofreARobot != null){ // si no hay robot disponible, no puedo cumplir con el pedido
            Robopuerto robopuertoConRobotMasCercano = (Robopuerto)rutaCofreARobot.nodo;
            Robot robot = robopuertoConRobotMasCercano.getRobotsActuales().removeFirst();

            System.out.println(rutaCofreARobot.distancia);
            System.out.println(rutaCofreARobot.camino);
            System.out.println(robot.getId());
            System.out.println(robot.getPosicionX());
            System.out.println(robot.getPosicionY());



            if(proveedor == null){ // si no hubo proveedor activo, busco uno pasivo
                ResultadoDijkstra rutaACofreProveedor = this.grafo.obtenerCofreConObjetoMasCercano(robopuertoConRobotMasCercano,itemSolicitado); // robopuerto a proveecdor
                proveedor = (CofreProveedor) rutaACofreProveedor.nodo;
            }

            ResultadoDijkstra rutaARobopuertoMasCercano = this.grafo.obtenerRobopuertoMasCercano(cofre); // solicitante a robopuerto
            Robopuerto robopuertoMasCercano = (Robopuerto)rutaARobopuertoMasCercano.nodo;

            // Paso 1: del robopuerto del robot al cofre proveedor
            int indiceRobopuertoOrigen = grafo.nodos.indexOf(robopuertoConRobotMasCercano);
            int indiceCofreProveedor = grafo.nodos.indexOf(proveedor);
            int indiceCofreSolicitador = grafo.nodos.indexOf(cofre);
            int indiceRobopuertoMasCercano = grafo.nodos.indexOf(robopuertoMasCercano);
            ResultadoDijkstra tramo1 = grafo.planificarRutaConRecargas(
                    indiceRobopuertoOrigen,
                    indiceCofreProveedor,
                    robot
            ); // puede ir directo o parar a recargar, este algoritmo sirve para ambos
            // no recarga la bateria real del robot, toma como una bateria virtual, para eso el metodo de abajo

            if (tramo1 == null) {
                System.out.println("No se puede llegar al cofre proveedor.");
                return false;
            }

            grafo.aplicarRuta(tramo1,robot); // aplicar ruta lo que hace es consumir la bateria real del robot, y decir que ruta tomo hasta el objetivo
            int cantidadParaOfrecer = proveedor.getOfrecimientos().get(itemSolicitado.getNombre());






            // Paso 2: del cofre proveedor al cofre solicitador (con batería llena)
            ResultadoDijkstra tramo2 = grafo.planificarRutaConRecargas(
                    indiceCofreProveedor,
                    indiceCofreSolicitador,
                    robot
            );

            if (tramo2 == null) {
                System.out.println("No se puede entregar desde proveedor al solicitador.");
                return false;
            }

            grafo.aplicarRuta(tramo2,robot);
            if(cofre instanceof CofreSolicitador){
                ((CofreSolicitador)cofre).cumplirSolicitud(itemSolicitado);
                if(cantidadParaOfrecer >= itemSolicitado.getCantidad()){
                    proveedor.ofrecer(itemSolicitado.getNombre(), itemSolicitado.getCantidad());
                    itemSolicitado.setCantidad(0);
                    completado = true; // si devuelvo true, saca el pedido de la lista
                }
                else
                {
                    proveedor.ofrecer(itemSolicitado.getNombre(), cantidadParaOfrecer); // doy todooo
                    itemSolicitado.setCantidad(itemSolicitado.getCantidad()-cantidadParaOfrecer); // no se cumplio todo
                }
            }
            else if(cofre instanceof CofreAlmacenamiento){
                ((CofreAlmacenamiento)cofre).almacenar(itemSolicitado);

            }


            // Paso 3: del cofre solicitador al robopuerto más cercano (para que el robot recargue)
            ResultadoDijkstra tramo3 = grafo.planificarRutaConRecargas(
                    indiceCofreSolicitador,
                    indiceRobopuertoMasCercano,
                    robot
            );


            if (tramo3 == null) {
                System.out.println("No se puede regresar a ningún robopuerto después de entregar.");
                return false;
            }

            grafo.aplicarRuta(tramo3,robot);

            // ✅ Si llegaste acá, tenés todos los tramos viables:
            System.out.println("Ruta total:");
            System.out.println("Tramo 1 (al proveedor):");
            tramo1.camino.forEach(System.out::println);

            System.out.println("Tramo 2 (al solicitador):");
            tramo2.camino.forEach(System.out::println);

            System.out.println("Tramo 3 (a recarga final):");
            tramo3.camino.forEach(System.out::println);

            Robopuerto robopuertoFinal = (Robopuerto) tramo3.nodo;
            robopuertoFinal.getRobotsActuales().add(robot);
        }
        return completado;
    }

    public void chequearExcedentes(){
        for (CofreProveedor prov : cofresProveedores) {
            if (prov.getOfrecimientos().isEmpty())
                continue;

            Iterator<Map.Entry<String, Integer>> it = prov.getOfrecimientos().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                Item itemExcedente = new Item(entry.getKey(), entry.getValue());

                ResultadoDijkstra ruta = grafo.obtenerCofreExcedenteMasCercano(prov);
                CofreAlmacenamiento cofreDestino = (CofreAlmacenamiento) ruta.nodo;

                realizarEntrega(cofreDestino, itemExcedente, prov);

                int cantidad = prov.getOfrecimientos().getOrDefault(entry.getKey(), 0);

                if (cantidad == 0) {
                    it.remove();
                }
            }
        }

    }
}
