package utils;

import cofre.Cofre;
import robopuerto.Robopuerto;
import robot.Robot;


import java.util.*;

public class Grafo {
    private final double[][] matrizAdyacencia;
    public ArrayList<Object> nodos;
    public final int cantidadRobopuertos;
    public final int cantidadCofres;
    public ResultadoDijkstra[] dijkstraNodos;

    public Grafo(ArrayList<Robopuerto> robopuertos, ArrayList<Cofre> cofres) {
        this.nodos = new ArrayList<>();
        nodos.addAll(robopuertos);
        nodos.addAll(cofres);

        this.cantidadCofres = cofres.size();
        this.cantidadRobopuertos = robopuertos.size();
        int n = nodos.size();
        dijkstraNodos = new ResultadoDijkstra[n];
        matrizAdyacencia = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrizAdyacencia[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
            }
        }

        construirMatriz(robopuertos, cofres);
        calcularDijkstraNodos();
    }

    private void construirMatriz(ArrayList<Robopuerto> robopuertos, ArrayList<Cofre> cofres) {
        int offset = robopuertos.size();

        for (int i = 0; i < robopuertos.size(); i++) {
            Robopuerto r1 = robopuertos.get(i);

            for (int j = 0; j < robopuertos.size(); j++) {
                if (i == j) continue;

                Robopuerto r2 = robopuertos.get(j);
                double distancia = calcularDistancia(r1.getPosicionX(), r1.getPosicionY(), r2.getPosicionX(), r2.getPosicionY());
                if (distancia <= r1.getAlcance() * 2 || r1.getRobopuertosVecinos().contains(r2)) {
                    matrizAdyacencia[i][j] = distancia;
                    matrizAdyacencia[j][i] = distancia;
                }
                else
                {
                    matrizAdyacencia[i][j] = Double.POSITIVE_INFINITY;
                    matrizAdyacencia[j][i] = Double.POSITIVE_INFINITY;
                }
            }

            for (int k = 0; k < cofres.size(); k++) {
                Cofre c = cofres.get(k);
                int j = offset + k;
                double distancia = calcularDistancia(r1.getPosicionX(), r1.getPosicionY(), c.getPosicionX(), c.getPosicionY());
                if (distancia <= r1.getAlcance() || r1.getCofresIncluidos().contains(c)) {
                    matrizAdyacencia[i][j] = distancia;
                    matrizAdyacencia[j][i] = distancia; // conexión inversa
                }
                else
                {
                    matrizAdyacencia[i][j] = Double.POSITIVE_INFINITY;
                    matrizAdyacencia[j][i] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int i = 0; i < cofres.size(); i++) {
            Cofre c1 = cofres.get(i);
            int indexC1 = offset + i;

            for (int j = 0; j < cofres.size(); j++) {
                if (i == j) continue;

                Cofre c2 = cofres.get(j);
                int indexC2 = offset + j;

                boolean conectados = false;

                for (Robopuerto r : robopuertos) {
                    boolean incluyeC1 = r.getCofresIncluidos().stream().anyMatch(c -> c.getId() == c1.getId());
                    boolean incluyeC2 = r.getCofresIncluidos().stream().anyMatch(c -> c.getId() == c2.getId());

                    if (incluyeC1 && incluyeC2) {
                        conectados = true;
                        break;
                    }
                }

                if (conectados) {
                    double distancia = calcularDistancia(c1.getPosicionX(), c1.getPosicionY(), c2.getPosicionX(), c2.getPosicionY());
                    matrizAdyacencia[indexC1][indexC2] = distancia;
                    matrizAdyacencia[indexC2][indexC1] = distancia;
                } else {
                    matrizAdyacencia[indexC1][indexC2] = Double.POSITIVE_INFINITY;
                    matrizAdyacencia[indexC2][indexC1] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }

    private double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    public Object getNodo(int indice) {
        return nodos.get(indice);
    }

    public int getIndice(Object nodo) {
        return nodos.indexOf(nodo);
    }

    public boolean esRobopuerto(int indice) {
        return indice < cantidadRobopuertos;
    }



    private double getDistancia(int nodo, int vecino) {
        return matrizAdyacencia[nodo][vecino];
    }

    public ResultadoRutas  planificarRutaConRecargas(int origen, int destino, Robot robot) {
        class Estado {
            final int nodo;
            final double bateriaRestante;
            final List<Object> camino;
            final double distanciaTotal;

            Estado(int nodo, double bateriaRestante, List<Object> camino, double distanciaTotal) {
                this.nodo = nodo;
                this.bateriaRestante = bateriaRestante;
                this.camino = camino;
                this.distanciaTotal = distanciaTotal;
            }
        }

        boolean debeRecargar = false;
        double distanciaADestino = this.getDistancia(origen, destino);
        List<Object> caminoInicial = new ArrayList<>();
        caminoInicial.add(getNodo(origen));

        ResultadoDijkstra dijkstraDestino = dijkstraNodos[destino];

        double menor = Double.MAX_VALUE;
        Robopuerto robopuertoMasCercano = null;
        for(int i=0;i<cantidadRobopuertos;i++){
            Robopuerto robopuerto = (Robopuerto) nodos.get(i);
            if(dijkstraDestino.distancias[i] < menor){
                robopuertoMasCercano = robopuerto;
            }
        }
        int indiceRobopuerto = nodos.indexOf(robopuertoMasCercano);
        double distanciaRobopuertoADestino = dijkstraDestino.distancias[indiceRobopuerto];
        if(!esRobopuerto(destino) && robot.alcanzaBateria(distanciaADestino)) { // debo asegurarme que no queda en el aire despues, ya que va a ir directo

            double consumoDestinoARobopuertoMasCercano = distanciaRobopuertoADestino * Robot.getFactorConsumo();
            double bateriaQueQuedaAlLlegar = robot.getBateriaActual()-distanciaADestino*Robot.getFactorConsumo();
            if ( consumoDestinoARobopuertoMasCercano > bateriaQueQuedaAlLlegar ) {
                // CONSUMO DE DESTINO A ROBOPUERTO MAS CERCANO > BATERIA QUE ME QUEDA AL LLEGAR
                debeRecargar = true; // obligo a que, aunque llegue directo, pare en el medio a recargar
            }
        }

        PriorityQueue<Estado> cola = new PriorityQueue<>(Comparator.comparingDouble(e -> e.distanciaTotal));
        Map<Integer, Double> mejorBateriaEnNodo = new HashMap<>();



        double bateriaInicial = robot.getBateriaActual();
        cola.add(new Estado(origen, bateriaInicial, caminoInicial, 0));

        System.out.println("🔄 Planificando desde nodo: " + getNodo(origen) + " hacia: " + getNodo(destino) + " con batería inicial: " + String.format("%.2f", bateriaInicial));
        System.out.println();

        while (!cola.isEmpty()) {
            Estado actual = cola.poll();



            if (mejorBateriaEnNodo.containsKey(actual.nodo)
                    && mejorBateriaEnNodo.get(actual.nodo) >= actual.bateriaRestante) {

                continue;
            }
            mejorBateriaEnNodo.put(actual.nodo, actual.bateriaRestante);

            if(esRobopuerto(actual.nodo) && actual.nodo != origen && debeRecargar) {
                distanciaADestino = getDistancia(actual.nodo,destino);

                double consumoDestinoARobopuertoMasCercano = distanciaRobopuertoADestino* Robot.getFactorConsumo();
                double bateriaQueQuedaAlLlegar = actual.bateriaRestante-distanciaADestino*Robot.getFactorConsumo();
                if ( consumoDestinoARobopuertoMasCercano <= bateriaQueQuedaAlLlegar ) {
                    // CONSUMO DE DESTINO A ROBOPUERTO MAS CERCANO > BATERIA QUE ME QUEDA AL LLEGAR
                    debeRecargar = false; // si cargando en este robopuerto, me alcanza la bateria para despues, recargo aca

                }
            }

            if (actual.nodo == destino) {
                return new ResultadoRutas(getNodo(actual.nodo), actual.camino, actual.distanciaTotal);
            }

            for (int vecino = 0; vecino < matrizAdyacencia.length; vecino++) {
                double distancia = getDistancia(actual.nodo, vecino);
                if (distancia == Double.POSITIVE_INFINITY) continue;
                if(vecino == origen || vecino == actual.nodo)continue;

                double consumo = distancia * Robot.getFactorConsumo();
                double nuevaBateria = actual.bateriaRestante - consumo;

                if (nuevaBateria < 0) {

                    continue;
                }

                if (esRobopuerto(vecino)) {
                    nuevaBateria = Robot.getBateriaTotal();


                }

                else {

                }
                if(debeRecargar && !esRobopuerto(vecino)) { // necesito recargar en el medio
                    continue;
                }

                List<Object> nuevoCamino = new ArrayList<>(actual.camino);
                nuevoCamino.add(getNodo(vecino));

                cola.add(new Estado(vecino, nuevaBateria, nuevoCamino, actual.distanciaTotal + distancia));
            }
        }
        return null;
    }

    public void aplicarRuta(ResultadoRutas  resultado, Robot robot) {
        for (int i = 1; i < resultado.camino.size(); i++) {
            Object desde = resultado.camino.get(i - 1);
            Object hasta = resultado.camino.get(i);
            int d = this.getIndice(desde);
            int h = this.getIndice(hasta);

            double distancia = this.getDistancia(d,h);
            robot.consumirBateria(distancia);

            if (this.esRobopuerto(h)) {
                robot.recargar();
            }

            System.out.println("🔋 El robot viaja desde " + desde + " hacia " + hasta + " (dist: " + String.format("%.2f",distancia) + "), batería resultante: " + String.format("%.2f", robot.getBateriaActual()));
            System.out.println();
        }
    }

    public void calcularDijkstraNodos(){
        for(int i=0;i<nodos.size();i++){
            ResultadoDijkstra resultado;
            resultado = dijkstra(nodos.get(i));
            dijkstraNodos[i] = resultado;
        }
    }

    public ResultadoDijkstra dijkstra(Object origen){

        class Nodo {
            int nodo;
            double distancia;

            Nodo(int nodo, double distanciaTotal) {
                this.nodo = nodo;
                this.distancia = distanciaTotal;
            }
        }
        ResultadoDijkstra resul = null;
        int[] sucesores = new int[matrizAdyacencia.length];
        PriorityQueue<Nodo> heap = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distancia));
        double[] distancia = new double[matrizAdyacencia.length];
        boolean[] visitados = new boolean[matrizAdyacencia.length];
        int o = nodos.indexOf(origen);

        for(int i=0; i< matrizAdyacencia.length; i++) { // agrego todas las aristas del robopuerto actual
            distancia[i] = matrizAdyacencia[o][i];
            visitados[i] = false;
            sucesores[i] = o;
        }
        heap.add(new Nodo(o,0));
        while(!heap.isEmpty()) {
            Nodo masCercano = heap.poll();
            int u = masCercano.nodo;
            if(visitados[u]) continue;
            visitados[u] = true;

            for(int v=0; v<matrizAdyacencia.length; v++) {
                if(u == v)continue;

                if(!visitados[v] && (distancia[v] > (distancia[u]+matrizAdyacencia[u][v]))){
                    distancia[v] = distancia[u]+matrizAdyacencia[u][v]; // si me conviene pasar por u
                    sucesores[v] = u; // para ir a v, paso por u
                    heap.add(new Nodo(v,distancia[v]));

                }

            }
        }


        resul = new ResultadoDijkstra(distancia,sucesores);


        return resul;

    }
}

