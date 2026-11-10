package modelo;

public class Movimiento {
    private Vertice origen;
    private Vertice destino;
    private TipoMovimiento tipoMovimiento;
    private double bateriaGastada;
    private double tiempoAcumulado;
    private double bateriaRestante;
    private double bateriaRecargada;


    public Movimiento(Vertice origen, Vertice destino, TipoMovimiento tipoMovimiento, double bateriaGastada, double tiempoAcumulado, double bateriaRestante, double bateriaRecargada) {
        this.origen = origen;
        this.destino = destino;
        this.tipoMovimiento = tipoMovimiento;
        this.bateriaGastada = bateriaGastada;
        this.tiempoAcumulado = tiempoAcumulado;
        this.bateriaRestante = bateriaRestante;
        this.bateriaRecargada = bateriaRecargada;
    }

    public Vertice getOrigen() {
        return origen;
    }

    public void setOrigen(Vertice origen) {
        this.origen = origen;
    }

    public double getTiempoAcumulado() {
        return tiempoAcumulado;
    }

    public void setTiempoAcumulado(double tiempoAcumulado) {
        this.tiempoAcumulado = tiempoAcumulado;
    }

    public double getBateriaGastada() {
        return bateriaGastada;
    }

    public void setBateriaGastada(double bateriaGastada) {
        this.bateriaGastada = bateriaGastada;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Vertice getDestino() {
        return destino;
    }

    public void setDestino(Vertice destino) {
        this.destino = destino;
    }

    public double getBateriaRestante() {
        return bateriaRestante;
    }

    public void setBateriaRestante(double bateriaRestante) {
        this.bateriaRestante = bateriaRestante;
    }
    public double getBateriaRecargada() {
        return bateriaRecargada;
    }
    public void setBateriaRecargada(double bateriaRecargada) {
        this.bateriaRecargada = bateriaRecargada;
    }
}
