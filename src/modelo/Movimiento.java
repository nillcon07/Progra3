package modelo;

public class Movimiento {
    private Vertice origen;
    private Vertice destino;
    private TipoMovimiento tipoMovimiento;
    private int bateriaGastada;
    private int tiempoAcumulado;

    // Constructor
    public Movimiento(Vertice origen, Vertice destino, TipoMovimiento tipoMovimiento, int bateriaGastada, int tiempoAcumulado) {
        this.origen = origen;
        this.destino = destino;
        this.tipoMovimiento = tipoMovimiento;
        this.bateriaGastada = bateriaGastada;
        this.tiempoAcumulado = tiempoAcumulado;
    }

}