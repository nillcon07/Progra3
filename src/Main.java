import algoritmos.BacktrackingUADA;
import archivos.LectorMapa;
import modelo.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        args = new String[1];
        args[0] = "D:/Facultad/archivos/mapa_grande.txt"; //ACA VA EL ARCHIVO, IR MODIFICANDO CUANDO SE PRUEBA CON OTROS ARCHIVOS//
        try {
            Mapa mapa = new LectorMapa().leerMapa(args[0]);
            int conexiones = 0;
            for (Arista[] fila : mapa.getGrafo()) {
                for (Arista arista : fila) {
                    if (arista != null) {
                        conexiones++;
                    }
                }
            }

            System.out.println("Lugares: " + mapa.getVertices().size());
            System.out.println("Conexiones dirigidas: " + conexiones);
            System.out.println("Obligatorios: " + mapa.getObligatorios().size());
            System.out.println("Inicio: " + mapa.getVertices().get(mapa.getIndiceInicio()).getNombre());

            BacktrackingUADA backtracking = new BacktrackingUADA(mapa);
            int posicionInicial = mapa.getIndiceInicio();
            List<Vertice> obligatorios = new ArrayList<>(mapa.getObligatorios());
            List<Movimiento> movimientos = new ArrayList<>();
            double bateriaInicial = 100.0;
            double tiempoInicial = 0.0;

            backtracking.backtrackingPrincipal(
                    posicionInicial,
                    obligatorios,
                    movimientos,
                    bateriaInicial,
                    tiempoInicial
            );

            System.out.println("Total de nodos explorados: " + backtracking.getNodosExplorados());

            List<Solucion> mejoresRutas = backtracking.getMejoresSoluciones();

                if (!backtracking.isSolucionCompletaEncontrada()) {
                    System.out.println("\n==================================================================");
                    System.out.println("AVISO DE CONTINGENCIA: No existe solución completa posible.");
                    System.out.println("Mostrando la ruta donde Uada estuvo más cerca del objetivo.");
                    System.out.println("==================================================================");
                } else {
                    System.out.println("\n Se encontraron soluciones completas con éxito");
                }

                int numeroSolucion = 1;

                for (Solucion solucion : mejoresRutas) {
                    System.out.println();
                    System.out.println("--- Opción / Ruta N° " + numeroSolucion + " ---");
                    System.out.println("Cuyo tiempo total (o acumulado parcial) fue de: " + solucion.getTiempoTotal() + " Segundos");

                    for (Movimiento movimiento : solucion.getRuta()) {
                        System.out.println(
                                movimiento.getOrigen().getNombre() + " -> " +
                                        movimiento.getDestino().getNombre() + " | " +
                                        movimiento.getTipoMovimiento() +
                                        " | Tiempo: " + movimiento.getTiempoAcumulado() +
                                        " Bateria: " + String.format("%.2f", movimiento.getBateriaRestante()) + "%"
                        );
                        if (movimiento.getBateriaRecargada() > 0) {
                            System.out.println("(recargo: +" + String.format("%.2f", movimiento.getBateriaRecargada()) + "%)");
                        }
                        System.out.println();
                    }
                    numeroSolucion++;
                }
        } catch (IOException e) {
            System.err.println("No se pudo leer el mapa: " + e.getMessage());
        }
    }
}











