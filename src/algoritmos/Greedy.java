package algoritmos;

import modelo.*;

import java.util.ArrayList;
import java.util.List;

public class Greedy {
    private static final double BATERIA_INICIAL = 100;

    private final Arista[][] grafo;
    private final List<Vertice> vertices;
    private final int inicio;


    private boolean[] visitado;
    private int obligatoriosPendientes;
    private int actual;
    private double bateria;
    private int tiempo;
    private final List<Movimiento> recorrido = new ArrayList<>();

    public Greedy(Mapa mapa) {
        this.grafo = mapa.getGrafo();
        this.vertices = mapa.getVertices();
        this.inicio = mapa.getIndiceInicio();
    }


    public Solucion resolver() {
        inicializar();

        while (!esSolucion()) {
            Candidato elegido = seleccion();
            if (elegido == null) return null;
            agregarASolucion(elegido);
        }
        return new Solucion(new ArrayList<>(recorrido), tiempo);
    }

    private void inicializar() {
        visitado = new boolean[vertices.size()];
        visitado[inicio] = true;
        obligatoriosPendientes = 0;
        for (int i = 0; i < vertices.size(); i++)
            if (i != inicio && vertices.get(i).esObligatorio()) obligatoriosPendientes++;
        actual = inicio;
        bateria = BATERIA_INICIAL;
        tiempo = 0;
        recorrido.clear();
    }


    private boolean esSolucion() {
        return actual == inicio && obligatoriosPendientes == 0 && !recorrido.isEmpty();
    }


    private Candidato seleccion() {
        Candidato mejor = null;
        for (int j = 0; j < vertices.size(); j++) {
            Arista arista = grafo[actual][j];
            if (arista == null) continue;
            for (TipoMovimiento mov : arista.getMovimientosPosibles()) {
                Candidato c = new Candidato(j, mov,
                        tiempo + tiempoMovimiento(arista.getTiempoBase(), mov),
                        bateriaGastada(arista.getTiempoBase(), mov));
                if (esFactible(c) && (mejor == null || c.esMejorQue(mejor)))
                    mejor = c;
            }
        }
        return mejor;
    }

    private boolean esFactible(Candidato c) {
        Vertice destino = vertices.get(c.destino);
        if (bateria - c.gasto <= 0) return false;
        if (c.destino == inicio) return obligatoriosPendientes == 0;
        if (visitado[c.destino]) return false;
        if (destino.getProhibido() != 0) return false;
        return destino.estaDisponible(c.llegada);
    }

    private void agregarASolucion(Candidato c) {
        Vertice destino = vertices.get(c.destino);
        double bateriaRecargada = destino.calcularRecargaBateria();
        double bateriaRestante = bateria - c.gasto + bateriaRecargada;
        recorrido.add(new Movimiento(vertices.get(actual), destino, c.movimiento, c.gasto, c.llegada, bateriaRestante, bateriaRecargada));
        bateria = bateria - c.gasto + destino.calcularRecargaBateria();
        tiempo = c.llegada;
        visitado[c.destino] = true;
        if (c.destino != inicio && destino.esObligatorio()) obligatoriosPendientes--;
        actual = c.destino;
    }

    static int tiempoMovimiento(int tiempoBase, TipoMovimiento mov) {
        switch (mov) {
            case CAMINANDO:    return tiempoBase;
            case SALTANDO:     return tiempoBase / 2;
            case PATAS_ARRIBA: return tiempoBase * 2;
        }
        throw new IllegalArgumentException("Movimiento desconocido");
    }


    static double bateriaGastada(int tiempoBase, TipoMovimiento mov) {
        double caminando = tiempoBase / 60.0;
        switch (mov) {
            case CAMINANDO:    return caminando;
            case SALTANDO:     return caminando * 2.2;
            case PATAS_ARRIBA: return caminando * 0.45;
        }
        throw new IllegalArgumentException("Movimiento desconocido");
    }

    public List<Movimiento> getRecorrido() { return List.copyOf(recorrido); }
    public double getBateria() { return bateria; }
    public int getTiempo() { return tiempo; }

    private static class Candidato {
        final int destino;
        final TipoMovimiento movimiento;
        final int llegada;
        final double gasto;

        Candidato(int destino, TipoMovimiento movimiento, int llegada, double gasto) {
            this.destino = destino;
            this.movimiento = movimiento;
            this.llegada = llegada;
            this.gasto = gasto;
        }

        boolean esMejorQue(Candidato otro) {
            return llegada < otro.llegada || (llegada == otro.llegada && gasto < otro.gasto);
        }
    }
}

