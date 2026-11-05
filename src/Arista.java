import java.util.List;

public class Arista {
    private int tiempoBase;
    private List<TipoMovimiento> movimientosPosibles;
    private int prohibido;

    public Arista(int tiempoBase, List<TipoMovimiento> movimientosPosibles, int prohibido) {
        this.tiempoBase = tiempoBase;
        this.movimientosPosibles = movimientosPosibles;
        this.prohibido = prohibido;
    }


    public int getTiempoBase() { return
            tiempoBase;
    }

    public List<TipoMovimiento> getMovimientosPosibles() {
        return movimientosPosibles;
    }

    public int getProhibido() {
        return prohibido;
    }
}
