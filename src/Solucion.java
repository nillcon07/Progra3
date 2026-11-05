import java.util.List;

public class Solucion {
    private List<Movimiento> ruta;
    private int tiempoTotal;

    public Solucion(List<Movimiento> ruta, int tiempoTotal) {
        this.ruta = ruta;
        this.tiempoTotal = tiempoTotal;
    }

    public int getTiempoTotal() {
        return tiempoTotal;
    }

    public List<Movimiento> getRuta() {
        return ruta;
    }
}
