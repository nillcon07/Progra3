import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//oztia puta chaval

public class BacktrackingUADA {
    private List<Solucion> mejoresSoluciones = new ArrayList<>();
    private int cotaInferior = Integer.MAX_VALUE;
    private Arista[][] grafo;
    private List<Vertice> listaVertices;

    public void backtrackingPrincipal(int posicionActual, List<Vertice> lno, List<Movimiento> listaMovimientos, int bateriaActual, int tiempoAcumulado) {

        if (posicionActual == 0 && lno.isEmpty() && !listaMovimientos.isEmpty()) {
            actualizarTop3MejoresOpciones(listaMovimientos, tiempoAcumulado);
            return;
        }

        int totalVertices = listaVertices.size();

        for (int i = 0; i < totalVertices; i++) {
            Arista aristaActual = grafo[posicionActual][i];
            Vertice verticeDestino = listaVertices.get(i);

            if (aristaActual != null && !verticeDestino.estaVisitado() && verticeDestino.getProhibido() == 0) {
                int indiceTransporte = 0;
                List<TipoMovimiento> movimientosPosibles = aristaActual.getMovimientosPosibles();

                while (indiceTransporte < movimientosPosibles.size()) {
                    TipoMovimiento movimiento = movimientosPosibles.get(indiceTransporte);
                    int bateriaGastada = funcBateriaGastada(aristaActual.getTiempoBase(), movimiento);
                    int bateriaRestante = bateriaActual - bateriaGastada;

                    if (bateriaRestante > 0) {
                        int taNuevo = tiempoAcumulado + aristaActual.getTiempoBase();

                        if (taNuevo >= cotaInferior) {
                            indiceTransporte++;
                            continue;
                        }

                        verticeDestino.setVisitado(true);
                        boolean eraObligatorio = false;

                        if (verticeDestino.esObligatorio() && lno.contains(verticeDestino)) {
                            lno.remove(verticeDestino);
                            eraObligatorio = true;
                        }

                        int bateriaRecarga = verticeDestino.calcularRecargaBateria();
                        int baNuevo = bateriaRestante + bateriaRecarga;
                        Movimiento nuevoMov = new Movimiento(listaVertices.get(posicionActual), verticeDestino, movimiento, bateriaGastada, taNuevo);
                        listaMovimientos.add(nuevoMov);
                        backtrackingPrincipal(i, lno, listaMovimientos, baNuevo, taNuevo);
                        listaMovimientos.remove(listaMovimientos.size() - 1);

                        if (eraObligatorio) {
                            lno.add(verticeDestino);
                        }
                        verticeDestino.setVisitado(false);
                    }
                    indiceTransporte++;
                }
            }
        }
    }

    private void actualizarTop3MejoresOpciones(List<Movimiento> rutaActual, int tiempoTotal) {
        Solucion nuevaSolucion = new Solucion(new ArrayList<>(rutaActual), tiempoTotal);

        if (mejoresSoluciones.isEmpty()) {
            mejoresSoluciones.add(nuevaSolucion);
        } else {
            boolean insertado = false;
            for (int i = 0; i < mejoresSoluciones.size(); i++) {
                if (nuevaSolucion.getTiempoTotal() < mejoresSoluciones.get(i).getTiempoTotal()) {
                    mejoresSoluciones.add(i, nuevaSolucion);
                    insertado = true;
                    break;
                }
            }
            if (!insertado) {
                mejoresSoluciones.add(nuevaSolucion);
            }
        }

        if (mejoresSoluciones.size() > 3) {
            mejoresSoluciones.remove(3); // O remove(mejoresSoluciones.size() - 1)
        }

        if (mejoresSoluciones.size() == 3) {
            this.cotaInferior = mejoresSoluciones.get(2).getTiempoTotal();
        }
    }

    private int funcBateriaGastada(int tiempoBase, TipoMovimiento mov) {
        return 0;
    }

}

