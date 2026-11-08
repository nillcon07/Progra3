package modelo;

import java.util.ArrayList;
import java.util.List;

public class Mapa {
    private final List<Vertice> vertices;
    private final Arista[][] grafo;
    private final int indiceInicio;

    public Mapa(List<Vertice> vertices, Arista[][] grafo, int indiceInicio) {
        this.vertices = List.copyOf(vertices);
        this.grafo = grafo;
        this.indiceInicio = indiceInicio;
    }

    public List<Vertice> getVertices() { return vertices; }
    public Arista[][] getGrafo() { return grafo; }
    public int getIndiceInicio() { return indiceInicio; }

    public List<Vertice> getObligatorios() {
        List<Vertice> obligatorios = new ArrayList<>();
        for (Vertice vertice : vertices)
            if (vertice.esObligatorio()) obligatorios.add(vertice);
        return obligatorios;
    }
}
