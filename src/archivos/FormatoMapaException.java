package archivos;

import java.io.IOException;

public class FormatoMapaException extends IOException {
    // Error en una línea del archivo.txt se indica cual linea y se guarda en que parte del codigo ocurrio.
    public FormatoMapaException(int linea, IllegalArgumentException causa) {
        super("Linea " + linea + ": " + causa.getMessage(), causa);
    }
    // error global del archivo, sin linea en especifico
    public FormatoMapaException(String mensaje) {
        super(mensaje);
    }
}