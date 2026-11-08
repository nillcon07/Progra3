package modelo;

import java.util.ArrayList;
import java.util.List;

public class Vertice {
    private int numeroAula;
    private final List<VentanaTiempo> ventanas = new ArrayList<>();
    private String nombre;
    private int x;
    private int y;
    private boolean esAula;
    private boolean obligatorio;
    private int prohibido;
    private boolean visitado;

    public Vertice(String nombre, int x, int y, int numeroAula,
                   boolean obligatorio, int prohibido) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.numeroAula = numeroAula;
        this.esAula = numeroAula != 0;
        this.obligatorio = obligatorio;
        this.prohibido = prohibido;
        this.visitado = false;
    }

    public int getNumeroAula() { return numeroAula; }

    public void agregarVentana(VentanaTiempo ventana) { ventanas.add(ventana); }

    public List<VentanaTiempo> getVentanas() { return List.copyOf(ventanas); }

    public boolean estaDisponible(double tiempo) {
        if (prohibido != 0) return false;
        for (VentanaTiempo ventana : ventanas)
            if (ventana.contiene(tiempo)) return false;
        return true;
    }

    public double calcularRecargaBateria() {
        if (!esAula)
            return 0;
        int sumaDigitos = 0;
        for (char c : (String.valueOf(numeroAula)).toCharArray()) {
            sumaDigitos += Character.getNumericValue(c);
        }
        return sumaDigitos / 5.0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean esAula() {
        return esAula;
    }

    public boolean esObligatorio() {
        return obligatorio;
    }

    public int getProhibido() {
        return prohibido;
    }

    public boolean estaVisitado() {
        return visitado; }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }
}

