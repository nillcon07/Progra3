package archivos;

import java.io.IOException;

public class FormatoMapaException extends IOException {
    public FormatoMapaException(int linea, IllegalArgumentException causa) {
        super("Linea " + linea + ": " + causa.getMessage(), causa);
    }

    public FormatoMapaException(String mensaje) {
        super(mensaje);
    }
}