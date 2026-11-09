package archivos;

import java.io.IOException;

public class FormatoMapaException extends IOException {
    // Error en una línea del archivo.txt se indica cual linea.
    public FormatoMapaException(int linea, String m) {
        super("Linea " + linea + ": " + m);
    }
    // error global del archivo, sin linea en especifico
    public FormatoMapaException(String mensaje) {
        super(mensaje);
    }
}