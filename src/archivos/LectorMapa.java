package archivos;

import modelo.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorMapa {
    public Mapa leerMapa(String ruta) throws IOException {
        return leerMapa(Path.of(ruta));
    }

    public Mapa leerMapa(Path ruta) throws IOException {
        List<Vertice> vertices = new ArrayList<>();
        Map<String, Integer> indices = new HashMap<>();
        List<String[]> pendientes = new ArrayList<>();
        List<Integer> numerosLinea = new ArrayList<>();

        // Primero se crean los lugares. Las referencias se resuelven despues.
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] datos = linea.split(";", -1); //limit -1 para que en caso de dejar campo vacio se guarde un espacio vacio
                for (int i = 0; i < datos.length; i++) datos[i] = datos[i].trim();
                try {
                    switch (datos[0]) {
                        case "LUGAR":
                            validarCampos(datos, 7);
                            String nombre = datos[1];
                            if (indices.containsKey(nombre))
                                throw new IllegalArgumentException("Lugar duplicado: " + nombre);
                            int aula = convertirEntero(datos[2]);
                            int x = Integer.parseInt(datos[3]);
                            int y = Integer.parseInt(datos[4]);
                            boolean obligatorio = convertirBooleano(datos[5]);
                            int prohibido = convertirBooleano(datos[6]) ? 1 : 0;
                            indices.put(nombre, vertices.size()); //para que la lista vertices y el Map indices estén alineados, de modo que el mismo número identifique al mismo lugar en las dos estructuras
                            vertices.add(new Vertice(nombre, x, y, aula, obligatorio, prohibido));
                            break;
                        // Se leen primero los LUGAR y se dejan para después ARISTA, VENTANA e INICIO porque estos requieren que todos los vértices ya existan
                        case "ARISTA":
                            pendientes.add(datos);
                            numerosLinea.add(numero);
                            break;
                        case "VENTANA":
                            pendientes.add(datos);
                            numerosLinea.add(numero);
                            break;
                        case "INICIO":
                            pendientes.add(datos);
                            numerosLinea.add(numero);
                            break;
                        default:
                            throw new IllegalArgumentException("Tipo de registro desconocido: " + datos[0]);
                    }
                } catch (IllegalArgumentException e) {
                    throw new FormatoMapaException(numero,e.getMessage());
                }
            }
        }
        if (vertices.isEmpty()) throw new FormatoMapaException("El mapa no contiene lugares.");
        Arista[][] grafo = new Arista[vertices.size()][vertices.size()];
        int inicio = -1;
        for (int i = 0; i < pendientes.size(); i++) {
            String[] datos = pendientes.get(i);
            try {
                switch (datos[0]) {
                    case "ARISTA":
                        validarCampos(datos, 5);
                        int origen = buscadorIndice(indices, datos[1]);
                        int destino = buscadorIndice(indices, datos[2]);
                        int tiempo = convertirEntero(datos[3]);
                        if (grafo[origen][destino] != null)
                            throw new IllegalArgumentException("Conexion duplicada: " + datos[1] + " -> " + datos[2]);
                        List<TipoMovimiento> movimientos = new ArrayList<>();
                        for (char codigo : datos[4].toCharArray()) {
                            TipoMovimiento movimiento;
                            switch (codigo) {
                                case 'C': movimiento = TipoMovimiento.CAMINANDO; break;
                                case 'S': movimiento = TipoMovimiento.SALTANDO; break;
                                case 'P': movimiento = TipoMovimiento.PATAS_ARRIBA; break;
                                default: throw new IllegalArgumentException("Modo desconocido: " + codigo);
                            }
                            if (movimientos.contains(movimiento))
                                throw new IllegalArgumentException("Modo repetido: " + codigo);
                            movimientos.add(movimiento);
                        }
                        // El archivo no tiene prohibicion de aristas ni conexion inversa implicita.
                        grafo[origen][destino] = new Arista(tiempo, movimientos, 0);
                        break;
                    case "VENTANA":
                        validarCampos(datos, 4);
                        int lugar = buscadorIndice(indices, datos[1]);
                        int desde = convertirEntero(datos[2]);
                        int hasta = convertirEntero(datos[3]);
                        vertices.get(lugar).agregarVentana(new VentanaTiempo(desde, hasta));
                        break;
                    case "INICIO":
                        validarCampos(datos, 2);
                        if (inicio != -1) throw new IllegalArgumentException("Hay mas de un INICIO.");
                        inicio = buscadorIndice(indices, datos[1]);
                        break;
                }
            } catch (IllegalArgumentException e) {
                throw new FormatoMapaException(numerosLinea.get(i), e.getMessage());
            }
        }
        if (inicio == -1) throw new FormatoMapaException("Falta el registro INICIO.");
        return new Mapa(vertices, grafo, inicio);
    }

    // Revisa que esten todos los campos y que ninguno este vacio
    private static void validarCampos(String[] datos, int cantidad) {
        if (datos.length != cantidad)
            throw new IllegalArgumentException(datos[0] + " requiere " + cantidad + " campos, recibidos: " + datos.length);
        for (String dato : datos)
            if (dato.isEmpty()) throw new IllegalArgumentException("Hay un campo vacio.");
    }

    // Busca el indice del lugar por su nombre. Si no existe, lanza un error
    private static int buscadorIndice(Map<String, Integer> indices, String nombre) {
        Integer indice = indices.get(nombre);
        if (indice == null) throw new IllegalArgumentException("Lugar inexistente: " + nombre);
        return indice;
    }

    // Convierte el texto a entero y comprueba que sea mayor o igual a cero
    private static int convertirEntero(String dato) {
        int valor = Integer.parseInt(dato);
        if (valor < 0) throw new IllegalArgumentException("Se esperaba un numero no negativo: " + dato);
        return valor;
    }

    // Convierte 1 en true y 0 en false. No acepta otros valores
    private static boolean convertirBooleano(String dato) {
        if (!dato.equals("0") && !dato.equals("1"))
            throw new IllegalArgumentException("Se esperaba 0 o 1: " + dato);
        return dato.equals("1");
    }

}
