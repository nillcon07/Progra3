package algoritmos;

import modelo.*;

import java.util.ArrayList;
import java.util.List;

public class BacktrackingUADA {
    private List<Solucion> mejoresSoluciones = new ArrayList<>();
    private double cotaInferior = Double.POSITIVE_INFINITY;
    private Arista[][] grafo;
    private List<Vertice> listaVertices;
    private int indiceInicio;
    private List<Movimiento> mejorRutaTruncada = new ArrayList<>();
    private int menosObligatoriosPendientes = Integer.MAX_VALUE;
    private double menorDistanciaAlObjetivo = Double.MAX_VALUE;
    private boolean solucionCompletaEncontrada = false;
    private double[] tiemposMinimosAlInicio;
    private long nodosExplorados = 0;


    public BacktrackingUADA(Mapa mapa) {
        this.grafo = mapa.getGrafo();
        this.listaVertices = mapa.getVertices();
        this.indiceInicio = mapa.getIndiceInicio();

        double[][] matrizCompleta = calcularMatrizFloyd();

        this.tiemposMinimosAlInicio = new double[listaVertices.size()];
        for (int i = 0; i < listaVertices.size(); i++) {
            this.tiemposMinimosAlInicio[i] = matrizCompleta[i][indiceInicio];
        }
    }

    public void backtrackingPrincipal(int posicionActual, List<Vertice> lno, List<Movimiento> listaMovimientos, double bateriaActual, double tiempoAcumulado) {
        nodosExplorados++;
        int nivel = listaMovimientos.size();
        String origen = listaVertices.get(posicionActual).getNombre();
        log(nivel, "ENTRO a " + origen + " | tiempo=" + tiempoAcumulado + " | bateria=" + bateriaActual + " | obligatorios pendientes=" + lno.size());

        if (posicionActual == indiceInicio && lno.isEmpty() && !listaMovimientos.isEmpty()) {
            log(nivel, "SOLUCION COMPLETA: volvi al inicio y visite todos los obligatorios. Tiempo=" + tiempoAcumulado);
            actualizarTop3MejoresOpciones(listaMovimientos, tiempoAcumulado);
            log(nivel, "TERMINO esta rama y regreso a la llamada anterior.");
            return;
        }

        if (mejoresSoluciones.isEmpty() && !listaMovimientos.isEmpty()) {
            int obligatoriosPendientes = lno.size();
            Vertice vActual = listaVertices.get(posicionActual);
            Vertice vInicio = listaVertices.get(indiceInicio);

            double distanciaActual = Math.sqrt(
                    Math.pow(vActual.getPosicionX() - vInicio.getPosicionX(), 2) + Math.pow(vActual.getPosicionY() - vInicio.getPosicionY(), 2)
            );

            if (obligatoriosPendientes < menosObligatoriosPendientes
                    || (obligatoriosPendientes == menosObligatoriosPendientes
                    && distanciaActual < menorDistanciaAlObjetivo)) {
                menosObligatoriosPendientes = obligatoriosPendientes;
                menorDistanciaAlObjetivo = distanciaActual;
                mejorRutaTruncada = new ArrayList<>(listaMovimientos);
                log(nivel, "GUARDO RUTA PARCIAL: pendientes=" + obligatoriosPendientes + " | distancia al inicio=" + distanciaActual);
            }
        }

        int totalVertices = listaVertices.size();

        for (int i = 0; i < totalVertices; i++) {
            Arista aristaActual = grafo[posicionActual][i];
            Vertice verticeDestino = listaVertices.get(i);
            if (i == indiceInicio && !lno.isEmpty()) continue;

            if (aristaActual != null && !verticeDestino.estaVisitado() && verticeDestino.getProhibido() == 0) {
                int indiceTransporte = 0;
                List<TipoMovimiento> movimientosPosibles = new ArrayList<>();

                TipoMovimiento[] orden = {
                        TipoMovimiento.SALTANDO,
                        TipoMovimiento.CAMINANDO,
                        TipoMovimiento.PATAS_ARRIBA
                };

                for (TipoMovimiento movimiento : orden) {
                    if (aristaActual.getMovimientosPosibles().contains(movimiento)) {
                        movimientosPosibles.add(movimiento);
                    } else {
                        log(nivel, "DESCARTO " + movimiento + " hacia " + verticeDestino.getNombre() + ": modo no permitido.");
                    }
                }
                if (movimientosPosibles.isEmpty()) log(nivel, "DESCARTO " + verticeDestino.getNombre() + ": no tiene modos permitidos.");

                while (indiceTransporte < movimientosPosibles.size()) {
                    TipoMovimiento movimiento = movimientosPosibles.get(indiceTransporte);
                    double bateriaGastada = calcularBateriaGastada(aristaActual.getTiempoBase(), movimiento);
                    double bateriaRestante = bateriaActual - bateriaGastada;
                    log(nivel, "PRUEBO " + origen + " -> " + verticeDestino.getNombre() + " con " + movimiento + " | bateria al llegar=" + bateriaRestante);

                    if (bateriaRestante > 0) {
                        double taNuevo = tiempoAcumulado + calcularTiempoMov(movimiento, aristaActual.getTiempoBase());

                        if (!verticeDestino.estaDisponible(taNuevo)) {
                            log(nivel, "PODA POR HORARIO: " + verticeDestino.getNombre() + " no esta disponible al llegar en " + taNuevo);
                            indiceTransporte++;
                            continue; // Si a ese tiempo el vértice está cerrado/no disponible, saltamos al siguiente movimiento
                        }

                        if (taNuevo >= cotaInferior) {
                            log(nivel, "PODA POR TIEMPO: " + taNuevo + " >= " + cotaInferior);
                            indiceTransporte++;
                            continue;
                        }

                        double tiempoEstimadoRegreso = tiemposMinimosAlInicio[i];
                        if (Double.isFinite(cotaInferior)
                                && taNuevo + tiempoEstimadoRegreso >= cotaInferior) {
                            log(nivel, "PODA POR FLOYD: tiempo " + taNuevo + " + regreso minimo " + tiempoEstimadoRegreso + " >= limite " + cotaInferior);
                            indiceTransporte++;
                            continue;
                        }

                        verticeDestino.setVisitado(true);
                        boolean eraObligatorio = false;

                        if (verticeDestino.esObligatorio() && lno.contains(verticeDestino)) {
                            lno.remove(verticeDestino);
                            eraObligatorio = true;
                            log(nivel, "QUITO de pendientes a " + verticeDestino.getNombre());
                        }

                        double bateriaRecarga = verticeDestino.calcularRecargaBateria();
                        double espacioRecargaDispo = 100.0 - bateriaRestante;

                        if (bateriaRecarga > espacioRecargaDispo) {bateriaRecarga = espacioRecargaDispo;}

                        double baNuevo = bateriaRestante + bateriaRecarga;
                        log(nivel, "RECARGA en " + verticeDestino.getNombre() + ": " + bateriaRecarga + " | bateria final=" + baNuevo);
                        Movimiento nuevoMov = new Movimiento(listaVertices.get(posicionActual), verticeDestino, movimiento, bateriaGastada, taNuevo, baNuevo, bateriaRecarga);
                        listaMovimientos.add(nuevoMov);
                        log(nivel, "AVANZO a " + verticeDestino.getNombre() + " con " + movimiento + " | tiempo=" + taNuevo + " | marco visitado y agrego el movimiento.");
                        backtrackingPrincipal(i, lno, listaMovimientos, baNuevo, taNuevo);
                        log(nivel, "VOLVIO la llamada desde " + verticeDestino.getNombre() + ". Quito el ultimo movimiento.");
                        listaMovimientos.remove(listaMovimientos.size() - 1);

                        if (eraObligatorio) {
                            lno.add(verticeDestino);
                            log(nivel, "REPONGO como pendiente a " + verticeDestino.getNombre());
                        }
                        verticeDestino.setVisitado(false);
                        log(nivel, "RETROCEDO a " + origen + ": desmarque " + verticeDestino.getNombre() + " | tiempo=" + tiempoAcumulado + " | bateria=" + bateriaActual + " | pendientes=" + lno.size());
                    } else {
                        log(nivel, "PODA POR BATERIA: quedaria " + bateriaRestante + "; debe ser mayor que cero.");
                    }
                    indiceTransporte++;
                }
            } else if (aristaActual == null) {
                continue; // Sin conexion: pasa al siguiente destino sin imprimir.
            } else if (verticeDestino.estaVisitado()) {
                log(nivel, "DESCARTO " + verticeDestino.getNombre() + ": ya esta visitado en este recorrido.");
            } else {
                log(nivel, "DESCARTO " + verticeDestino.getNombre() + ": esta prohibido.");
            }
        }
        log(nivel, "TERMINE las alternativas desde " + origen + ". Finaliza esta llamada.");
    }

    // Cada movimiento de la ruta agrega un nivel de sangria.
    private void log(int nivel, String mensaje) {
        System.out.println("  ".repeat(nivel) + mensaje);
    }

    private void actualizarTop3MejoresOpciones(List<Movimiento> rutaActual, double tiempoTotal) {
        solucionCompletaEncontrada = true;
        Solucion nuevaSolucion = new Solucion(new ArrayList<>(rutaActual), tiempoTotal);

        if (mejoresSoluciones.isEmpty()) {
            mejoresSoluciones.add(nuevaSolucion);
        } else {
            boolean insertado = false;
            for (int i = 0; i < mejoresSoluciones.size(); i++) {
                if (nuevaSolucion.getTiempoTotal() < mejoresSoluciones.get(i).getTiempoTotal()) {
                    mejoresSoluciones.add(i, nuevaSolucion);
                    insertado = true;
                    break;
                }
            }
            if (!insertado) {
                mejoresSoluciones.add(nuevaSolucion);
            }
        }

        if (mejoresSoluciones.size() > 3) {
            log(rutaActual.size(), "QUITO la cuarta solucion del top 3. Tiempo=" + mejoresSoluciones.get(3).getTiempoTotal());
            mejoresSoluciones.remove(3); // O remove(mejoresSoluciones.size() - 1)
        }

        if (mejoresSoluciones.size() == 3) {
            this.cotaInferior = mejoresSoluciones.get(2).getTiempoTotal();
            log(rutaActual.size(), "LIMITE DEL TOP 3: " + cotaInferior);
        }
        log(rutaActual.size(), "SOLUCIONES GUARDADAS: " + mejoresSoluciones.size());
    }

    private double calcularBateriaGastada(int tiempoBase, TipoMovimiento mov) {
        double consumoCaminando = tiempoBase / 60.0;
        switch (mov){
            case CAMINANDO:
                return consumoCaminando;
            case PATAS_ARRIBA:
                return consumoCaminando * 0.45;
            case SALTANDO:
                return consumoCaminando * 2.2;
        }
        throw new IllegalArgumentException("Movimiento desconocido");
    }

    public double calcularTiempoMov(TipoMovimiento mov, double tiempoBase){
        switch (mov){
            case CAMINANDO:
                return tiempoBase;
            case PATAS_ARRIBA:
                return tiempoBase * 2;
            case SALTANDO:
                return tiempoBase / 2;
        }
        throw new IllegalArgumentException("Movimiento desconocido");
    }
    public List<Solucion> getMejoresSoluciones() {
        if (mejoresSoluciones.isEmpty()) {
            double tiempoTruncado = 0.0;

            if (!mejorRutaTruncada.isEmpty()) {
                tiempoTruncado = mejorRutaTruncada.getLast().getTiempoAcumulado();
            }

            List<Solucion> retroceso = new ArrayList<>();
            retroceso.add(new Solucion(new ArrayList<>(mejorRutaTruncada), tiempoTruncado));

            return retroceso;
        }

        return mejoresSoluciones;
    }
    public boolean isSolucionCompletaEncontrada() {
        return solucionCompletaEncontrada;
    }

    private double[][] calcularMatrizFloyd() {
        int n = listaVertices.size();
        double[][] dist = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0.0;
                } else if (grafo[i][j] != null) {
                    dist[i][j] = grafo[i][j].getTiempoBase() / 2.0;
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                }
            }
        }

        return dist;
    }
    public long getNodosExplorados() {
        return nodosExplorados;
    }
}

