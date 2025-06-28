package utils;

import Item.Item;
import cofre.*;
import robopuerto.Robopuerto;
import robot.Robot;


import java.util.*;

public class Grafo {
    private double[][] matrizAdyacencia;
    public ArrayList<Object> nodos;
    private int cantidadRobopuertos;
    private int cantidadCofres;


    public Grafo(ArrayList<Robopuerto> robopuertos, ArrayList<Cofre> cofres) {
        this.nodos = new ArrayList<>();
        nodos.addAll(robopuertos);
        nodos.addAll(cofres);

        this.cantidadCofres = cofres.size();
        this.cantidadRobopuertos = robopuertos.size();
        int n = nodos.size();
        matrizAdyacencia = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrizAdyacencia[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
            }
        }

        construirMatriz(robopuertos, cofres);
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

    public void mostrarMatriz() {
        System.out.println("Matriz de Adyacencia:");
        for (double[] doubles : matrizAdyacencia) {
            for (double aDouble : doubles) {
                if (aDouble == Double.POSITIVE_INFINITY) {
                    System.out.print("INF ");
                } else {
                    System.out.printf("%.1f ", aDouble);
                }
            }
            System.out.println();
        }
    }

    public void mostrarNodos() {
        System.out.println("Nodos:");
        for (int i = 0; i < nodos.size(); i++) {
            System.out.println(i + ": " + nodos.get(i));
        }
    }

    public ResultadoDijkstra obtenerRobopuertoConRobotMasCercano(Object origen) {
        ResultadoDijkstra resul = null;
        List<Object> caminoARobotMasCercano = new ArrayList<>();
        int sucesores[] = new int[this.cantidadRobopuertos]; // despues puedo reconstruir el camino
        Robot robotMasCercano = null;
        PriorityQueue<Arista> heap = new PriorityQueue<>();
        double distancia[] = new double[this.cantidadRobopuertos]; // solo voy a tomar la distancia a los robopuertos
        boolean visitados[] = new boolean[this.cantidadRobopuertos];
        int o = nodos.indexOf(origen); // obtengo su posicion en la matriz de adyacencia
        for(int i=0; i<this.cantidadRobopuertos; i++) { // agrego todas las aristas de los robopuertos adyacentes
            heap.add(new Arista(o,i,matrizAdyacencia[o][i]));
            distancia[i] = matrizAdyacencia[o][i];
            visitados[i] = false;
            sucesores[i] = o;
        }


        while(!heap.isEmpty()) {
            Arista masCercano = heap.poll();
            int u = masCercano.destino;
            if(visitados[u]) continue;
            visitados[u] = true;

            for(int v=0; v<this.cantidadRobopuertos; v++) {
                if(u == v)continue;

                if(!visitados[v] && (distancia[v] > (distancia[u]+matrizAdyacencia[u][v]))){
                    distancia[v] = distancia[u]+matrizAdyacencia[u][v]; // si me conviene pasar por u
                    sucesores[v] = u; // para ir a v, paso por u
                    heap.add(new Arista(u,v,distancia[v]));
                // distancia[v] es distancia de cofre a V, distancia[u] es distancia de cofre a donde estoy parado ahora
                    //distancia[u]+matrizAdyacencia[u][v] es distancia a U + distancia de U a V, osea veo si conviene pasar por u para ir a v
                }

            }
        }

        double menorDistancia = Double.MAX_VALUE;
        int indiceRobopuerto = -1;
        Robopuerto robopuertoMasCercano = null;

        for (int i = 0; i < this.cantidadRobopuertos; i++) {
            robopuertoMasCercano = (Robopuerto) nodos.get(i);
            if (distancia[i] < menorDistancia && !robopuertoMasCercano.getRobotsActuales().isEmpty()) {
                menorDistancia = distancia[i];
                indiceRobopuerto = i;
            }
        }

        robopuertoMasCercano = (Robopuerto)nodos.get(indiceRobopuerto);

        robotMasCercano = robopuertoMasCercano.getRobotsActuales().getFirst();

        if(robotMasCercano != null){ // si hay algun robot en ese robopuerto
            int actual = indiceRobopuerto;
            caminoARobotMasCercano.addFirst(nodos.get(actual));

            while (sucesores[actual] != o) {
                actual = sucesores[actual];
                caminoARobotMasCercano.addFirst(nodos.get(actual));
            }

            caminoARobotMasCercano.addFirst(origen); // El cofre (inicio)
            resul = new ResultadoDijkstra(robopuertoMasCercano,caminoARobotMasCercano, menorDistancia);
        }



        return resul;

    }

    public ResultadoDijkstra obtenerCofreConObjetoMasCercano(Object origen, Item itemSolicitado) {
        ResultadoDijkstra resul = null;
        List<Object> caminoACofreMasCercano = new ArrayList<>();
        int sucesores[] = new int[matrizAdyacencia.length]; // despues puedo reconstruir el camino
        Cofre cofreMasCercano = null;
        PriorityQueue<Arista> heap = new PriorityQueue<>();
        double distancia[] = new double[matrizAdyacencia.length];
        boolean visitados[] = new boolean[matrizAdyacencia.length];
        int o = nodos.indexOf(origen); // obtengo su posicion en la matriz de

        for(int i=0; i< matrizAdyacencia.length; i++) { // agrego todas las aristas del robopuerto actual
            heap.add(new Arista(o,i,matrizAdyacencia[o][i]));
            distancia[i] = matrizAdyacencia[o][i];
            visitados[i] = false;
            sucesores[i] = o;
        }


        while(!heap.isEmpty()) {
            Arista masCercano = heap.poll();
            int u = masCercano.destino;
            if(visitados[u]) continue;
            visitados[u] = true;

            for(int v=0; v<matrizAdyacencia.length; v++) {
                if(u == v)continue;

                if(!visitados[v] && (distancia[v] > (distancia[u]+matrizAdyacencia[u][v]))){
                    distancia[v] = distancia[u]+matrizAdyacencia[u][v]; // si me conviene pasar por u
                    sucesores[v] = u; // para ir a v, paso por u
                    heap.add(new Arista(u,v,distancia[v]));
                    // distancia[v] es distancia de cofre a V, distancia[u] es distancia de cofre a donde estoy parado ahora
                    //distancia[u]+matrizAdyacencia[u][v] es distancia a U + distancia de U a V, osea veo si conviene pasar por u para ir a v
                }

            }
        }

        double menorDistancia = Double.MAX_VALUE;
        int indiceCofre = -1;

        for (int i = this.cantidadRobopuertos; i < this.cantidadRobopuertos+this.cantidadCofres; i++) { // busco el cofre con el item solicitado mas cercano
            cofreMasCercano = (Cofre) nodos.get(i);
            if (distancia[i-cantidadRobopuertos] < menorDistancia && cofreMasCercano instanceof CofreProveedor &&
                    (((CofreProveedor) cofreMasCercano).getOfrecimientos().get(itemSolicitado.getNombre())) >= itemSolicitado.getCantidad() ) { // TODO: agregar atributo "tipo" a Cofre, para sacar el instanceof
                menorDistancia = distancia[i-cantidadRobopuertos];
                indiceCofre = i;
            }
        }

        cofreMasCercano = (Cofre) nodos.get(indiceCofre);



        if(cofreMasCercano != null){
            int actual = indiceCofre;
            caminoACofreMasCercano.addFirst(nodos.get(actual));

            while (sucesores[actual] != o) {
                actual = sucesores[actual];
                caminoACofreMasCercano.addFirst(nodos.get(actual));
            }

            caminoACofreMasCercano.addFirst(origen); // El robopuerto
            resul = new ResultadoDijkstra(cofreMasCercano,caminoACofreMasCercano, menorDistancia);
        }



        return resul;

    }

    public ResultadoDijkstra obtenerMejorRuta(Object origen, Object destino) {
        ResultadoDijkstra resul = null;
        List<Object> caminoADestino = new ArrayList<>();
        int sucesores[] = new int[matrizAdyacencia.length]; // despues puedo reconstruir el camino
        PriorityQueue<Arista> heap = new PriorityQueue<>();
        double distancia[] = new double[matrizAdyacencia.length];
        boolean visitados[] = new boolean[matrizAdyacencia.length];
        int o = nodos.indexOf(origen); // obtengo su posicion en la matriz de

        for(int i=0; i< matrizAdyacencia.length; i++) { // agrego todas las aristas del robopuerto actual
            heap.add(new Arista(o,i,matrizAdyacencia[o][i]));
            distancia[i] = matrizAdyacencia[o][i];
            visitados[i] = false;
            sucesores[i] = o;
        }


        while(!heap.isEmpty()) {
            Arista masCercano = heap.poll();
            int u = masCercano.destino;
            if(visitados[u]) continue;
            visitados[u] = true;

            for(int v=0; v<matrizAdyacencia.length; v++) {
                if(u == v)continue;

                if(!visitados[v] && (distancia[v] > (distancia[u]+matrizAdyacencia[u][v]))){
                    distancia[v] = distancia[u]+matrizAdyacencia[u][v]; // si me conviene pasar por u
                    sucesores[v] = u; // para ir a v, paso por u
                    heap.add(new Arista(u,v,distancia[v]));
                    // distancia[v] es distancia de cofre a V, distancia[u] es distancia de cofre a donde estoy parado ahora
                    //distancia[u]+matrizAdyacencia[u][v] es distancia a U + distancia de U a V, osea veo si conviene pasar por u para ir a v
                }

            }
        }


        int actual = nodos.indexOf(destino);
        caminoADestino.addFirst(nodos.get(actual));

        while (sucesores[actual] != o) {
                actual = sucesores[actual];
                caminoADestino.addFirst(nodos.get(actual));
        }

        caminoADestino.addFirst(origen);
        resul = new ResultadoDijkstra(destino, caminoADestino, distancia[nodos.indexOf(destino)]);




        return resul;

    }
    public ResultadoDijkstra obtenerCofreExcedenteMasCercano(Object origen){
        ResultadoDijkstra resul = null;
        List<Object> caminoACofreMasCercano = new ArrayList<>();
        int sucesores[] = new int[matrizAdyacencia.length]; // despues puedo reconstruir el camino
        Cofre cofreMasCercano = null;
        PriorityQueue<Arista> heap = new PriorityQueue<>();
        double distancia[] = new double[matrizAdyacencia.length];
        boolean visitados[] = new boolean[matrizAdyacencia.length];
        int o = nodos.indexOf(origen); // obtengo su posicion en la matriz de

        for(int i=0; i< matrizAdyacencia.length; i++) { // agrego todas las aristas del robopuerto actual
            heap.add(new Arista(o,i,matrizAdyacencia[o][i]));
            distancia[i] = matrizAdyacencia[o][i];
            visitados[i] = false;
            sucesores[i] = o;
        }


        while(!heap.isEmpty()) {
            Arista masCercano = heap.poll();
            int u = masCercano.destino;
            if(visitados[u]) continue;
            visitados[u] = true;

            for(int v=0; v<matrizAdyacencia.length; v++) {
                if(u == v)continue;

                if(!visitados[v] && (distancia[v] > (distancia[u]+matrizAdyacencia[u][v]))){
                    distancia[v] = distancia[u]+matrizAdyacencia[u][v]; // si me conviene pasar por u
                    sucesores[v] = u; // para ir a v, paso por u
                    heap.add(new Arista(u,v,distancia[v]));
                    // distancia[v] es distancia de cofre a V, distancia[u] es distancia de cofre a donde estoy parado ahora
                    //distancia[u]+matrizAdyacencia[u][v] es distancia a U + distancia de U a V, osea veo si conviene pasar por u para ir a v
                }

            }
        }

        double menorDistancia = Double.MAX_VALUE;
        int indiceCofre = -1;

        for (int i = this.cantidadRobopuertos; i < this.cantidadRobopuertos+this.cantidadCofres; i++) { // busco el cofre con el item solicitado mas cercano
            cofreMasCercano = (Cofre) nodos.get(i);
            if (distancia[i-cantidadRobopuertos] < menorDistancia && cofreMasCercano instanceof CofreAlmacenamiento) { // TODO: agregar atributo "tipo" a Cofre, para sacar el instanceof
                menorDistancia = distancia[i-cantidadRobopuertos];
                indiceCofre = i;
            }
        }

        cofreMasCercano = (Cofre) nodos.get(indiceCofre);



        if(cofreMasCercano != null){
            int actual = indiceCofre;
            caminoACofreMasCercano.addFirst(nodos.get(actual));

            while (sucesores[actual] != o) {
                actual = sucesores[actual];
                caminoACofreMasCercano.addFirst(nodos.get(actual));
            }

            caminoACofreMasCercano.addFirst(origen); // El robopuerto
            resul = new ResultadoDijkstra(cofreMasCercano,caminoACofreMasCercano, menorDistancia);
        }



        return resul;
    }
    public ResultadoDijkstra obtenerRobopuertoMasCercano(Object origen) {
        ResultadoDijkstra resul = null;
        List<Object> caminoARobopuertoMasCercano = new ArrayList<>();
        int sucesores[] = new int[this.cantidadRobopuertos]; // despues puedo reconstruir el camino
        Robot robotMasCercano = null;
        PriorityQueue<Arista> heap = new PriorityQueue<>();
        double distancia[] = new double[this.cantidadRobopuertos]; // solo voy a tomar la distancia a los robopuertos
        boolean visitados[] = new boolean[this.cantidadRobopuertos];
        int o = nodos.indexOf(origen); // obtengo su posicion en la matriz de adyacencia
        for(int i=0; i<this.cantidadRobopuertos; i++) { // agrego todas las aristas de los robopuertos adyacentes
            heap.add(new Arista(o,i,matrizAdyacencia[o][i]));
            distancia[i] = matrizAdyacencia[o][i];
            visitados[i] = false;
            sucesores[i] = o;
        }


        while(!heap.isEmpty()) {
            Arista masCercano = heap.poll();
            int u = masCercano.destino;
            if(visitados[u]) continue;
            visitados[u] = true;

            for(int v=0; v<this.cantidadRobopuertos; v++) {
                if(u == v)continue;

                if(!visitados[v] && (distancia[v] > (distancia[u]+matrizAdyacencia[u][v]))){
                    distancia[v] = distancia[u]+matrizAdyacencia[u][v]; // si me conviene pasar por u
                    sucesores[v] = u; // para ir a v, paso por u
                    heap.add(new Arista(u,v,distancia[v]));
                    // distancia[v] es distancia de cofre a V, distancia[u] es distancia de cofre a donde estoy parado ahora
                    //distancia[u]+matrizAdyacencia[u][v] es distancia a U + distancia de U a V, osea veo si conviene pasar por u para ir a v
                }

            }
        }

        double menorDistancia = Double.MAX_VALUE;
        int indiceRobopuerto = -1;
        Robopuerto robopuertoMasCercano = null;

        for (int i = 0; i < this.cantidadRobopuertos; i++) {
            robopuertoMasCercano = (Robopuerto) nodos.get(i);
            if (distancia[i] < menorDistancia) {
                menorDistancia = distancia[i];
                indiceRobopuerto = i;
            }
        }

        robopuertoMasCercano = (Robopuerto)nodos.get(indiceRobopuerto);


        int actual = indiceRobopuerto;
        caminoARobopuertoMasCercano.addFirst(nodos.get(actual));

        while (sucesores[actual] != o) {
            actual = sucesores[actual];
            caminoARobopuertoMasCercano.addFirst(nodos.get(actual));
        }

        caminoARobopuertoMasCercano.addFirst(origen); // El cofre (inicio)
        resul = new ResultadoDijkstra(robopuertoMasCercano, caminoARobopuertoMasCercano, menorDistancia);
        return resul;

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



    public ResultadoDijkstra planificarRutaConRecargas(int origen, int destino, Robot robot) {
        class Estado {
            int nodo;
            double bateriaRestante;
            List<Object> camino;
            double distanciaTotal;

            Estado(int nodo, double bateriaRestante, List<Object> camino, double distanciaTotal) {
                this.nodo = nodo;
                this.bateriaRestante = bateriaRestante;
                this.camino = camino;
                this.distanciaTotal = distanciaTotal;
            }
        }

        boolean debeRecargar = false;
        double distanciaADestino = this.getDistancia(origen, destino);
        if(!esRobopuerto(destino) && robot.alcanzaBateria(distanciaADestino)) { // debo asegurarme que no queda en el aire despues, ya que va a ir directo
            ResultadoDijkstra RutaRobopuertoMasCercanoADestino = obtenerRobopuertoMasCercano(this.getNodo(destino));
            double consumoDestinoARobopuertoMasCercano = RutaRobopuertoMasCercanoADestino.distancia * Robot.getFactorConsumo();
            double bateriaQueQuedaAlLlegar = robot.getBateriaActual()-distanciaADestino*Robot.getFactorConsumo();
            if ( consumoDestinoARobopuertoMasCercano > bateriaQueQuedaAlLlegar ) {
                // CONSUMO DE DESTINO A ROBOPUERTO MAS CERCANO > BATERIA QUE ME QUEDA AL LLEGAR
                debeRecargar = true; // obligo a que, aunque llegue directo, pare en el medio a recargar
                System.out.println("Debe recargar en el medio, para no quedarse sin bateria luego");
            }
        }


        PriorityQueue<Estado> cola = new PriorityQueue<>(Comparator.comparingDouble(e -> e.distanciaTotal));
        Map<Integer, Double> mejorBateriaEnNodo = new HashMap<>();

        List<Object> caminoInicial = new ArrayList<>();
        caminoInicial.add(getNodo(origen));

        double bateriaInicial = robot.getBateriaActual();
        cola.add(new Estado(origen, bateriaInicial, caminoInicial, 0));

        System.out.println("🔄 Planificando desde nodo: " + getNodo(origen) + " hacia: " + getNodo(destino) + " con batería inicial: " + bateriaInicial);

        while (!cola.isEmpty()) {
            Estado actual = cola.poll();

            System.out.println("📍 Evaluando nodo: " + getNodo(actual.nodo) + ", batería: " + actual.bateriaRestante);

            if (mejorBateriaEnNodo.containsKey(actual.nodo)
                    && mejorBateriaEnNodo.get(actual.nodo) >= actual.bateriaRestante) {
                System.out.println("⛔ Ya visitado con igual o más batería. Se omite.");
                continue;
            }
            mejorBateriaEnNodo.put(actual.nodo, actual.bateriaRestante);

            if(esRobopuerto(actual.nodo) && actual.nodo != origen && debeRecargar) {
                distanciaADestino = getDistancia(actual.nodo,destino);
                ResultadoDijkstra RutaRobopuertoMasCercanoADestino = obtenerRobopuertoMasCercano(this.getNodo(destino));
                double consumoDestinoARobopuertoMasCercano = RutaRobopuertoMasCercanoADestino.distancia * Robot.getFactorConsumo();
                double bateriaQueQuedaAlLlegar = actual.bateriaRestante-distanciaADestino*Robot.getFactorConsumo();
                if ( consumoDestinoARobopuertoMasCercano <= bateriaQueQuedaAlLlegar ) {
                    // CONSUMO DE DESTINO A ROBOPUERTO MAS CERCANO > BATERIA QUE ME QUEDA AL LLEGAR
                    debeRecargar = false; // si cargando en este robopuerto, me alcanza la bateria para despues, recargo aca

                }


            }

            if (actual.nodo == destino) {



                System.out.println("✅ Se llegó al destino: " + getNodo(actual.nodo));

                return new ResultadoDijkstra(getNodo(actual.nodo), actual.camino, actual.distanciaTotal);
            }

            for (int vecino = 0; vecino < matrizAdyacencia.length; vecino++) {
                double distancia = getDistancia(actual.nodo, vecino);
                if (distancia == Double.POSITIVE_INFINITY) continue;
                if(vecino == origen || vecino == actual.nodo)continue;

                double consumo = distancia * Robot.getFactorConsumo();
                double nuevaBateria = actual.bateriaRestante - consumo;

                System.out.print("➡️ Intentando ir a " + getNodo(vecino) + " (distancia: " + distancia + ", consumo: " + consumo + ")... ");

                if (nuevaBateria < 0) {
                    System.out.println("❌ No alcanza la batería.");
                    continue;
                }

                if (esRobopuerto(vecino)) {
                    nuevaBateria = Robot.getBateriaTotal();
                    System.out.println("⚡ Es robopuerto, recarga batería.");

                }

                else {
                    System.out.println("✔️ Llega sin recarga.");
                }
                if(debeRecargar && !esRobopuerto(vecino)) { // necesito recargar en el medio
                    continue;
                }

                List<Object> nuevoCamino = new ArrayList<>(actual.camino);
                nuevoCamino.add(getNodo(vecino));

                cola.add(new Estado(vecino, nuevaBateria, nuevoCamino, actual.distanciaTotal + distancia));
            }
        }

        System.out.println("🚫 No se encontró una ruta viable desde " + getNodo(origen) + " hasta " + getNodo(destino));
        return null;
    }

    public void aplicarRuta(ResultadoDijkstra resultado, Robot robot) {
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

            System.out.println("🔋 Robot viaja de " + desde + " a " + hasta + " (dist: " + distancia + "), batería: " + robot.getBateriaActual());
        }
    }




}
