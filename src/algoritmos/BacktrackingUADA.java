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

    public BacktrackingUADA(Mapa mapa) {
        this.grafo = mapa.getGrafo();
        this.listaVertices = mapa.getVertices();
        this.indiceInicio = mapa.getIndiceInicio();

        Greedy greedyInicial = new Greedy(mapa);
        Solucion solGreedy = greedyInicial.resolver();
        if (solGreedy != null) {
            this.cotaInferior = solGreedy.getTiempoTotal();
        }
    }

    public void backtrackingPrincipal(int posicionActual, List<Vertice> lno, List<Movimiento> listaMovimientos, double bateriaActual, double tiempoAcumulado) {

        if (posicionActual == indiceInicio && lno.isEmpty() && !listaMovimientos.isEmpty()) {
            actualizarTop3MejoresOpciones(listaMovimientos, tiempoAcumulado);
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
            }
        }

        int totalVertices = listaVertices.size();

        for (int i = 0; i < totalVertices; i++) {
            Arista aristaActual = grafo[posicionActual][i];
            Vertice verticeDestino = listaVertices.get(i);

            if (aristaActual != null && !verticeDestino.estaVisitado() && verticeDestino.getProhibido() == 0) {
                int indiceTransporte = 0;
                List<TipoMovimiento> movimientosPosibles = aristaActual.getMovimientosPosibles();

                while (indiceTransporte < movimientosPosibles.size()) {
                    TipoMovimiento movimiento = movimientosPosibles.get(indiceTransporte);
                    double bateriaGastada = calcularBateriaGastada(aristaActual.getTiempoBase(), movimiento);
                    double bateriaRestante = bateriaActual - bateriaGastada;

                    if (bateriaRestante >= 0) {
                        double taNuevo = tiempoAcumulado + calcularTiempoMov(movimiento, aristaActual.getTiempoBase());

                        if (taNuevo >= cotaInferior) {
                            indiceTransporte++;
                            continue;
                        }

                        verticeDestino.setVisitado(true);
                        boolean eraObligatorio = false;

                        if (verticeDestino.esObligatorio() && lno.contains(verticeDestino)) {
                            lno.remove(verticeDestino);
                            eraObligatorio = true;
                        }

                        double bateriaRecarga = verticeDestino.calcularRecargaBateria();
                        double espacioRecargaDispo = 100.0 - bateriaRestante;

                        if (bateriaRecarga > espacioRecargaDispo) {bateriaRecarga = espacioRecargaDispo;}

                        double baNuevo = bateriaRestante + bateriaRecarga;
                        Movimiento nuevoMov = new Movimiento(listaVertices.get(posicionActual), verticeDestino, movimiento, bateriaGastada, taNuevo, baNuevo, bateriaRecarga);
                        listaMovimientos.add(nuevoMov);
                        backtrackingPrincipal(i, lno, listaMovimientos, baNuevo, taNuevo);
                        listaMovimientos.remove(listaMovimientos.size() - 1);

                        if (eraObligatorio) {
                            lno.add(verticeDestino);
                        }
                        verticeDestino.setVisitado(false);
                    }
                    indiceTransporte++;
                }
            }
        }
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
            mejoresSoluciones.remove(3); // O remove(mejoresSoluciones.size() - 1)
        }

        if (mejoresSoluciones.size() == 3) {
            this.cotaInferior = mejoresSoluciones.get(2).getTiempoTotal();
        }
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
                return tiempoBase / 2;
            case SALTANDO:
                return tiempoBase * 2;
        }
        throw new IllegalArgumentException("Movimiento desconocido");
    }
    public List<Solucion> getMejoresSoluciones() {
        if (mejoresSoluciones.isEmpty() && !mejorRutaTruncada.isEmpty()) {
            double tiempoTruncado = mejorRutaTruncada.get(mejorRutaTruncada.size() - 1).getTiempoAcumulado();
            Solucion solucionTruncada = new Solucion(new ArrayList<>(mejorRutaTruncada), tiempoTruncado);

            List<Solucion> fallback = new ArrayList<>();
            fallback.add(solucionTruncada);
            return fallback;
        }
        return mejoresSoluciones;
    }

    public boolean isSolucionCompletaEncontrada() {
        return solucionCompletaEncontrada;
    }
}

