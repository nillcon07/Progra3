package modelo;

import java.util.List;

public class Solucion {
    private List<Movimiento> ruta;
    private double tiempoTotal;

    public Solucion(List<Movimiento> ruta, double tiempoTotal) {
        this.ruta = ruta;
        this.tiempoTotal = tiempoTotal;
    }

    public double getTiempoTotal() {
        return tiempoTotal;
    }

    public List<Movimiento> getRuta() {
        return ruta;
    }
}
