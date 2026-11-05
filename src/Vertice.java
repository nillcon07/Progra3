public class Vertice {
    private String nombre;
    private int x;
    private int y;
    private boolean esAula;
    private boolean obligatorio;
    private int prohibido;
    private boolean visitado;

    public Vertice(String nombre, int x, int y, boolean esAula, boolean obligatorio, int prohibido) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.esAula = esAula;
        this.obligatorio = obligatorio;
        this.prohibido = prohibido;
        this.visitado = false;
    }

    public int calcularRecargaBateria() {
        if (!esAula)
            return 0;
        int sumaDigitos = 0;
        for (char c : nombre.toCharArray()) {
            if (Character.isDigit(c)) {
                sumaDigitos += Character.getNumericValue(c);
            }
        }
        return sumaDigitos / 5;
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
