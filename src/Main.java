import archivos.LectorMapa;
import modelo.Arista;
import modelo.Mapa;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Indica la ruta del mapa como argumento del programa.");
            return;
        }
        try {
            Mapa mapa = new LectorMapa().leerMapa(args[0]);
            int conexiones = 0;
            for (Arista[] fila : mapa.getGrafo())
                for (Arista arista : fila)
                    if (arista != null) conexiones++;
            System.out.println("Lugares: " + mapa.getVertices().size());
            System.out.println("Conexiones dirigidas: " + conexiones);
            System.out.println("Obligatorios: " + mapa.getObligatorios().size());
            System.out.println("Inicio: " + mapa.getVertices().get(mapa.getIndiceInicio()).getNombre());
        } catch (IOException e) {
            System.err.println("No se pudo leer el mapa: " + e.getMessage());
        }
    }
}
