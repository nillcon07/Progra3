package modelo;

public class VentanaTiempo {
    private final int desde;
    private final int hasta;

    public VentanaTiempo(int desde, int hasta) {
        if (desde < 0 || hasta < desde)
            throw new IllegalArgumentException("Ventana invalida: " + desde + " a " + hasta);
        this.desde = desde;
        this.hasta = hasta;
    }

    public int getDesde() { return desde; }
    public int getHasta() { return hasta; }
    public boolean contiene(double tiempo) { return tiempo >= desde && tiempo <= hasta; }
}
