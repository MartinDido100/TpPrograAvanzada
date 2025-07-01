import Item.Item;
import cofre.*;
import mapa.Mapa;
import robopuerto.Robopuerto;
import robot.Robot;
import utils.Grafo;
import utils.ResultadoDijkstra;
import utils.ResultadoRutas;

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
    Map<Cofre,Item> pedidosNoCumplidos;
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
        this.pedidosNoCumplidos = new HashMap<>();
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
            String mensaje = "ATENDIENDO PEDIDO EN: " + cofre;
            int ancho = mensaje.length() + 4;

            System.out.println("\n\t" + "┌" + "─".repeat(ancho) + "┐");
            System.out.println("\t" + "│  " + mensaje + "  │");
            System.out.println("\t" + "└" + "─".repeat(ancho) + "┘");



            while(!cofre.getSolicitudes().isEmpty()){ // mientras quede pedidos
                Iterator<Item> it = cofre.getSolicitudes().iterator();

                int indice = grafo.nodos.indexOf(cofre);
                ResultadoDijkstra dijkstraCofreSolicitud = grafo.dijkstraNodos[indice];

                double menor = Double.MAX_VALUE;
                Robopuerto robopuertoMasCercano = null;
                for(int i=0;i<grafo.cantidadRobopuertos;i++){
                    Robopuerto robopuerto = (Robopuerto) grafo.nodos.get(i);
                    if(dijkstraCofreSolicitud.distancias[i] < menor && !robopuerto.getRobotsActuales().isEmpty() ){
                        robopuertoMasCercano = robopuerto;
                        menor = dijkstraCofreSolicitud.distancias[i];
                    }
                }
                if(robopuertoMasCercano == null){
                    System.out.println("El cofre no tiene a ningun robopuerto en cobertura, no se puede completar el pedido");
                    for(Item i : cofre.getSolicitudes()){
                        pedidosNoCumplidos.put((Cofre) cofre,i);
                    }
                    return;

                }

                while (it.hasNext()) {
                    Item itemSolicitado = it.next();

                    System.out.println("\n💎 Se solicita el item: " + itemSolicitado);

                    CofreProveedor proveedor = null;
                    int cantidadTotalUniverso = 0;


                    for (Cofre p : robopuertoMasCercano.getCofresIncluidos()) { // solo los que pueden llegar a llegar
                        if(p instanceof CofreProveedor proveedores){
                            if (proveedores.getOfrecimientos().containsKey(itemSolicitado.getNombre())) {
                                cantidadTotalUniverso += proveedores.getOfrecimientos().get(itemSolicitado.getNombre());
                            }
                        }

                    }
                    if (itemSolicitado.getCantidad() > cantidadTotalUniverso) {
                        System.out.println("⚠️ Se solicitaron " + itemSolicitado.getCantidad() + " y solo hay la siguiente cantidad en todo el universo: " + cantidadTotalUniverso);
                        System.out.println();
                        it.remove();
                        pedidosNoCumplidos.put((Cofre) cofre,itemSolicitado);
                        continue;
                    }
                    for (CofreProvisionActiva prov : this.cofresActivos) { // prioridad
                        if (prov.getOfrecimientos().containsKey(itemSolicitado.getNombre())) {
                            proveedor = prov;
                        }
                        if (proveedor != null)
                            break;
                    }

                    boolean completado = realizarEntrega((Cofre) cofre, itemSolicitado, proveedor,robopuertoMasCercano);

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

    public boolean realizarEntrega(Cofre cofre, Item itemSolicitado, CofreProveedor proveedor,Robopuerto robopuertoConRobotMasCercano){

        boolean completado = false; // si no complete totalmente el pedido, no lo saco de la lista

            Robot robot = robopuertoConRobotMasCercano.getRobotsActuales().removeFirst();
            int indiceRobopuertoConRobotMasCercano = grafo.nodos.indexOf(robopuertoConRobotMasCercano);

            if(proveedor == null){ // si no hubo proveedor activo, busco uno pasivo
                double menorDistancia = Double.MAX_VALUE;
                int indiceCofre = -1;
                Cofre cofreMasCercano = null;
                for (int i = grafo.cantidadRobopuertos; i < grafo.cantidadRobopuertos+grafo.cantidadCofres; i++) { // busco el cofre con el item solicitado mas cercano
                    cofreMasCercano = (Cofre) grafo.nodos.get(i);
                    if (grafo.dijkstraNodos[indiceRobopuertoConRobotMasCercano].distancias[i] < menorDistancia && cofreMasCercano instanceof CofreProveedor &&
                            (((CofreProveedor) cofreMasCercano).getOfrecimientos().containsKey(itemSolicitado.getNombre())) ) {
                        menorDistancia = grafo.dijkstraNodos[indiceRobopuertoConRobotMasCercano].distancias[i];
                        indiceCofre = i;
                    }
                }
                if (indiceCofre == -1)return false;
                proveedor = (CofreProveedor) grafo.nodos.get(indiceCofre);
            }
            int indiceRobopuerto = grafo.nodos.indexOf(cofre);
            ResultadoDijkstra dijkstraCofreSolicitud = grafo.dijkstraNodos[indiceRobopuerto];

            double menor = Double.MAX_VALUE;
            Robopuerto robopuertoMasCercano = null;
            for(int i=0;i<grafo.cantidadRobopuertos;i++){
                Robopuerto robopuerto = (Robopuerto) grafo.nodos.get(i);
                if(dijkstraCofreSolicitud.distancias[i] < menor){
                    robopuertoMasCercano = robopuerto;
                    menor = dijkstraCofreSolicitud.distancias[i];
                }
            }
            System.out.println(robopuertoMasCercano);
            // Paso 1: del robopuerto del robot al cofre proveedor

            int indiceCofreProveedor = grafo.nodos.indexOf(proveedor);
            int indiceCofreSolicitador = grafo.nodos.indexOf(cofre);
            int indiceRobopuertoMasCercano = grafo.nodos.indexOf(robopuertoMasCercano);
            ResultadoRutas  tramo1 = grafo.planificarRutaConRecargas(
                    indiceRobopuertoConRobotMasCercano,
                    indiceCofreProveedor,
                    robot
            ); // puede ir directo o parar a recargar, este algoritmo sirve para ambos
            // no recarga la bateria real del robot, toma como una bateria virtual, para eso el metodo de abajo

            if (tramo1 == null) {
                System.out.println("No se puede llegar al cofre proveedor.");
                robopuertoConRobotMasCercano.getRobotsActuales().addFirst(robot);
                pedidosNoCumplidos.put((Cofre)cofre,itemSolicitado);
                return true; // si no hay ruta, no puedo cumplir el pedido, devuelvo true para que lo saque de la lista
            }

            System.out.println(Main.ROBOT_EMOJI + " El " + robot + " sera el encargado de realizar el pedido");

            System.out.println("🛣️ En el tramo 1 de la ruta:\n");
            grafo.aplicarRuta(tramo1,robot); // aplicar ruta lo que hace es consumir la bateria real del robot, y decir que ruta tomo hasta el objetivo


            // Paso 2: del cofre proveedor al cofre solicitador (con batería llena)
            ResultadoRutas tramo2 = grafo.planificarRutaConRecargas(
                    indiceCofreProveedor,
                    indiceCofreSolicitador,
                    robot
            );

            if (tramo2 == null) {
                System.out.println("No se puede entregar desde proveedor al solicitador.");
                robopuertoConRobotMasCercano.getRobotsActuales().addFirst(robot);
                robot.recargar(); // ya que al final no se pudo realizar, le restauro la bateria consumida
                pedidosNoCumplidos.put((Cofre)cofre,itemSolicitado);
                return true;// si no hay ruta, no puedo cumplir el pedido, devuelvo true para que lo saque de la lista

            }

            System.out.println("🛣️ En el tramo 2 de la ruta:");
            grafo.aplicarRuta(tramo2,robot);


            // Paso 3: del cofre solicitador al robopuerto más cercano (para que el robot recargue)
            ResultadoRutas  tramo3 = grafo.planificarRutaConRecargas(
                    indiceCofreSolicitador,
                    indiceRobopuertoMasCercano,
                    robot
            );

            if (tramo3 == null) {
                System.out.println("No se puede regresar a ningún robopuerto después de entregar.");
                robopuertoConRobotMasCercano.getRobotsActuales().addFirst(robot);
                robot.recargar(); // ya que al final no se pudo realizar, le restauro la bateria consumida
                pedidosNoCumplidos.put((Cofre)cofre,itemSolicitado);
                return true;// si no hay ruta, no puedo cumplir el pedido, devuelvo true para que lo saque de la lista
            }

        int capacidadRobot = Robot.getCapacidadCarga();
        int cantidadParaOfrecer = proveedor.getOfrecimientos().get(itemSolicitado.getNombre());

        if(cantidadParaOfrecer>capacidadRobot){
            cantidadParaOfrecer = capacidadRobot;
        }
        if(cofre instanceof CofreSolicitador){

            if(cantidadParaOfrecer >= itemSolicitado.getCantidad()){
                proveedor.ofrecer(itemSolicitado.getNombre(), itemSolicitado.getCantidad());
                itemSolicitado.setCantidad(0);
                ((CofreSolicitador)cofre).cumplirSolicitud(itemSolicitado);
                completado = true; // si devuelvo true, saca el pedido de la lista
            }
            else
            {
                proveedor.ofrecer(itemSolicitado.getNombre(), cantidadParaOfrecer); // doy todooo
                itemSolicitado.setCantidad(itemSolicitado.getCantidad()-cantidadParaOfrecer); // no se cumplio todoo
            }
        }
        else if(cofre instanceof CofreAlmacenamiento){
            ((CofreAlmacenamiento)cofre).almacenar(itemSolicitado);

        }

            System.out.println("🛣️ En el tramo 3 de la ruta:");
            grafo.aplicarRuta(tramo3,robot);

            String titulo = "RUTA TOTAL REALIZADA";
            int ancho = titulo.length() + 4;

            System.out.println("\n\t" + "┌" + "─".repeat(ancho) + "┐");
            System.out.println("\t" + "│  " + titulo + "  │");
            System.out.println("\t" + "└" + "─".repeat(ancho) + "┘\n");
            System.out.println("Tramo 1 (al proveedor):");
            tramo1.camino.forEach(nodo -> System.out.println("\t---> " + nodo));

            System.out.println("Tramo 2 (al solicitador):");
            tramo2.camino.forEach(nodo -> System.out.println("\t---> " + nodo));

            System.out.println("Tramo 3 (a recarga final):");
            tramo3.camino.forEach(nodo -> System.out.println("\t---> " + nodo));


            Robopuerto robopuertoFinal = (Robopuerto) tramo3.nodo;
            robopuertoFinal.getRobotsActuales().add(robot);

        return completado;
    }

   public void chequearExcedentes(){


        for (CofreProveedor prov : cofresProveedores) {

            if(cofresAlmacenamiento.isEmpty()){
                Iterator<Map.Entry<String, Integer>> it = prov.getOfrecimientos().entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry<String, Integer> entry = it.next();
                    Item itemExcedente = new Item(entry.getKey(), entry.getValue());

                    pedidosNoCumplidos.put((Cofre)prov,itemExcedente);
                }
                System.out.println("No existe ningun cofre de almacenamiento, por lo que no es posible completar este pedido");
                continue;
            }
            int indiceCofreProveedor = grafo.nodos.indexOf(prov);
            ResultadoDijkstra DijkstraProv = grafo.dijkstraNodos[indiceCofreProveedor];


            CofreAlmacenamiento CofreAlmacenador = null;
            double menorDistancia = Double.MAX_VALUE;
            for(int i=grafo.cantidadRobopuertos;i<grafo.nodos.size();i++){
                Cofre cofre = (Cofre) grafo.nodos.get(i);

                if(DijkstraProv.distancias[i] < menorDistancia && cofre instanceof CofreAlmacenamiento){
                    menorDistancia = DijkstraProv.distancias[i];
                    CofreAlmacenador = (CofreAlmacenamiento) cofre;
                }
            }
            if(CofreAlmacenador == null)continue;

            menorDistancia = Double.MAX_VALUE;
            Robopuerto robopuertoMasCercano = null;
            ResultadoDijkstra DijkstraAlmacenador = grafo.dijkstraNodos[grafo.nodos.indexOf(CofreAlmacenador)];
            for(int i=0;i<grafo.cantidadRobopuertos;i++){
                Robopuerto robopuerto = (Robopuerto) grafo.nodos.get(i);
                if(DijkstraAlmacenador.distancias[i] < menorDistancia && !robopuerto.getRobotsActuales().isEmpty() ){
                    robopuertoMasCercano = robopuerto;
                    menorDistancia = DijkstraAlmacenador.distancias[i];
                }
            }
            if(robopuertoMasCercano == null){
                System.out.println("El cofre no tiene a ningun robopuerto en cobertura, no se puede completar la entrega");
                return;
            }

            Iterator<Map.Entry<String, Integer>> it = prov.getOfrecimientos().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                Item itemExcedente = new Item(entry.getKey(), entry.getValue());



                realizarEntrega(CofreAlmacenador, itemExcedente,prov,robopuertoMasCercano);

                int cantidad = prov.getOfrecimientos().getOrDefault(entry.getKey(), 0);

                if (cantidad == 0) {
                    it.remove();
                }
            }
        }
    }

    public void mostrarAlmacenamiento() {
        for (CofreAlmacenamiento cofre : this.cofresAlmacenamiento) {
            System.out.println("Cofre de almacenamiento " + cofre.getId() + " en posición (" + cofre.getPosicionX() + ", " + cofre.getPosicionY() + "):");

            for (Map.Entry<String, Integer> entry : cofre.getAlmacenamiento().entrySet()) {
                String nombre = entry.getKey();
                Integer cantidad = entry.getValue();
                System.out.println(" - " + nombre + ": " + cantidad);
            }
        }
    }

}

