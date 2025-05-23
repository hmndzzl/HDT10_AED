package main.java.com.estructuradatos;

import java.io.*;
import java.util.*;

/**
 * Sistema de logística que utiliza el algoritmo de Floyd-Warshall
 * para optimizar rutas de transporte considerando condiciones climáticas
 */
public class SistemaLogistica {
    private Grafo grafo;
    private Scanner scanner;
    private Map<String, Map<String, Double[]>> datosCompletos; // Para almacenar todos los tiempos climáticos
    
    // Índices para los diferentes tipos de clima
    private static final int CLIMA_NORMAL = 0;
    private static final int CLIMA_LLUVIA = 1;
    private static final int CLIMA_NIEVE = 2;
    private static final int CLIMA_TORMENTA = 3;
    private static final String[] NOMBRES_CLIMA = {"normal", "lluvia", "nieve", "tormenta"};
    
    public SistemaLogistica() {
        this.grafo = new Grafo();
        this.scanner = new Scanner(System.in);
        this.datosCompletos = new HashMap<>();
    }
    
    /**
     * Lee el archivo de logística y construye el grafo
     */
    public void leerArchivo(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            Set<String> ciudades = new HashSet<>();
            List<String[]> conexiones = new ArrayList<>();
            
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.trim().split("\\s+");
                if (partes.length == 6) {
                    String ciudad1 = partes[0];
                    String ciudad2 = partes[1];
                    
                    ciudades.add(ciudad1);
                    ciudades.add(ciudad2);
                    conexiones.add(partes);
                }
            }
            
            // Inicializar grafo con todas las ciudades
            grafo.inicializarGrafo(ciudades.size());
            for (String ciudad : ciudades) {
                grafo.agregarCiudad(ciudad);
            }
            
            // Agregar conexiones con tiempo normal por defecto
            for (String[] conexion : conexiones) {
                String ciudad1 = conexion[0];
                String ciudad2 = conexion[1];
                double tiempoNormal = Double.parseDouble(conexion[2]);
                double tiempoLluvia = Double.parseDouble(conexion[3]);
                double tiempoNieve = Double.parseDouble(conexion[4]);
                double tiempoTormenta = Double.parseDouble(conexion[5]);
                
                // Almacenar todos los datos climáticos
                if (!datosCompletos.containsKey(ciudad1)) {
                    datosCompletos.put(ciudad1, new HashMap<>());
                }
                Double[] tiempos = {tiempoNormal, tiempoLluvia, tiempoNieve, tiempoTormenta};
                datosCompletos.get(ciudad1).put(ciudad2, tiempos);
                
                // Agregar arista con tiempo normal
                grafo.agregarArista(ciudad1, ciudad2, tiempoNormal);
            }
            
            System.out.println("Archivo cargado exitosamente.");
            System.out.println("Ciudades encontradas: " + ciudades.size());
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error en el formato de los datos: " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta el programa principal con el menú interactivo
     */
    public void ejecutar() {
        System.out.println("=== Sistema de Optimización Logística ===");
        System.out.println("Utilizando el Algoritmo de Floyd-Warshall\n");
        
        // Leer archivo
        System.out.print("Ingrese el nombre del archivo (ej: logistica.txt): ");
        String nombreArchivo = scanner.nextLine();
        leerArchivo(nombreArchivo);
        
        // Aplicar algoritmo de Floyd
        System.out.println("\nAplicando algoritmo de Floyd-Warshall...");
        grafo.algoritmFloyd();
        
        // Mostrar matriz de adyacencia
        grafo.mostrarMatrizAdyacencia();
        
        // Menú principal
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    consultarRuta();
                    break;
                case 2:
                    mostrarCentroGrafo();
                    break;
                case 3:
                    modificarGrafo();
                    break;
                case 4:
                    continuar = false;
                    System.out.println("¡Gracias por usar el sistema!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }
    
    /**
     * Muestra el menú principal
     */
    private void mostrarMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MENÚ PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Consultar ruta más corta entre ciudades");
        System.out.println("2. Mostrar centro del grafo");
        System.out.println("3. Modificar grafo");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    /**
     * Lee y valida la opción del menú
     */
    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Consulta la ruta más corta entre dos ciudades
     */
    private void consultarRuta() {
        System.out.println("\n--- Consultar Ruta Más Corta ---");
        
        System.out.print("Ciudad origen: ");
        String origen = scanner.nextLine().trim();
        
        System.out.print("Ciudad destino: ");
        String destino = scanner.nextLine().trim();
        
        if (!grafo.existeCiudad(origen)) {
            System.out.println("Error: La ciudad origen '" + origen + "' no existe.");
            return;
        }
        
        if (!grafo.existeCiudad(destino)) {
            System.out.println("Error: La ciudad destino '" + destino + "' no existe.");
            return;
        }
        
        double distancia = grafo.obtenerDistancia(origen, destino);
        List<String> camino = grafo.obtenerCamino(origen, destino);
        
        if (distancia == Double.MAX_VALUE || camino.isEmpty()) {
            System.out.println("No existe ruta entre " + origen + " y " + destino);
        } else {
            System.out.println("\nRESULTADO:");
            System.out.println("Distancia más corta: " + distancia + " horas");
            System.out.println("Ruta: " + String.join(" → ", camino));
        }
    }
    
    /**
     * Muestra el centro del grafo
     */
    private void mostrarCentroGrafo() {
        System.out.println("\n--- Centro del Grafo ---");
        String centro = grafo.calcularCentroGrafo();
        if (!centro.isEmpty()) {
            System.out.println("El centro del grafo es: " + centro);
            System.out.println("Esta ciudad tiene la menor distancia máxima a cualquier otra ciudad.");
        } else {
            System.out.println("No se pudo determinar el centro del grafo.");
        }
    }
    
    /**
     * Permite modificar el grafo
     */
    private void modificarGrafo() {
        System.out.println("\n--- Modificar Grafo ---");
        System.out.println("1. Interrumpir tráfico entre ciudades");
        System.out.println("2. Establecer nueva conexión");
        System.out.println("3. Cambiar condiciones climáticas");
        System.out.print("Seleccione una opción: ");
        
        int opcion = leerOpcion();
        
        switch (opcion) {
            case 1:
                interrumpirTrafico();
                break;
            case 2:
                establecerConexion();
                break;
            case 3:
                cambiarClima();
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }
        
        // Recalcular rutas
        System.out.println("Recalculando rutas...");
        grafo.algoritmFloyd();
        System.out.println("Rutas recalculadas exitosamente.");
    }
    
    /**
     * Interrumpe el tráfico entre dos ciudades
     */
    private void interrumpirTrafico() {
        System.out.print("Ciudad origen: ");
        String origen = scanner.nextLine().trim();
        
        System.out.print("Ciudad destino: ");
        String destino = scanner.nextLine().trim();
        
        if (grafo.existeCiudad(origen) && grafo.existeCiudad(destino)) {
            grafo.eliminarArista(origen, destino);
            System.out.println("Tráfico interrumpido entre " + origen + " y " + destino);
        } else {
            System.out.println("Una o ambas ciudades no existen.");
        }
    }
    
    /**
     * Establece una nueva conexión entre ciudades
     */
    private void establecerConexion() {
        System.out.print("Ciudad origen: ");
        String origen = scanner.nextLine().trim();
        
        System.out.print("Ciudad destino: ");
        String destino = scanner.nextLine().trim();
        
        if (!grafo.existeCiudad(origen) || !grafo.existeCiudad(destino)) {
            System.out.println("Una o ambas ciudades no existen.");
            return;
        }
        
        try {
            System.out.print("Tiempo con clima normal: ");
            double tiempoNormal = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Tiempo con lluvia: ");
            double tiempoLluvia = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Tiempo con nieve: ");
            double tiempoNieve = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Tiempo con tormenta: ");
            double tiempoTormenta = Double.parseDouble(scanner.nextLine());
            
            // Almacenar datos completos
            if (!datosCompletos.containsKey(origen)) {
                datosCompletos.put(origen, new HashMap<>());
            }
            Double[] tiempos = {tiempoNormal, tiempoLluvia, tiempoNieve, tiempoTormenta};
            datosCompletos.get(origen).put(destino, tiempos);
            
            // Agregar arista con tiempo normal por defecto
            grafo.agregarArista(origen, destino, tiempoNormal);
            System.out.println("Conexión establecida exitosamente.");
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores numéricos válidos.");
        }
    }
    
    /**
     * Cambia las condiciones climáticas entre ciudades
     */
    private void cambiarClima() {
        System.out.print("Ciudad origen: ");
        String origen = scanner.nextLine().trim();
        
        System.out.print("Ciudad destino: ");
        String destino = scanner.nextLine().trim();
        
        if (!grafo.existeCiudad(origen) || !grafo.existeCiudad(destino)) {
            System.out.println("Una o ambas ciudades no existen.");
            return;
        }
        
        if (!datosCompletos.containsKey(origen) || !datosCompletos.get(origen).containsKey(destino)) {
            System.out.println("No existe conexión entre estas ciudades.");
            return;
        }
        
        System.out.println("Condiciones climáticas disponibles:");
        for (int i = 0; i < NOMBRES_CLIMA.length; i++) {
            System.out.println((i + 1) + ". " + NOMBRES_CLIMA[i]);
        }
        
        System.out.print("Seleccione el clima: ");
        int climaIndex = leerOpcion() - 1;
        
        if (climaIndex >= 0 && climaIndex < NOMBRES_CLIMA.length) {
            Double[] tiempos = datosCompletos.get(origen).get(destino);
            double nuevoTiempo = tiempos[climaIndex];
            
            grafo.agregarArista(origen, destino, nuevoTiempo);
            System.out.println("Clima cambiado a " + NOMBRES_CLIMA[climaIndex] + 
                             ". Nuevo tiempo: " + nuevoTiempo + " horas");
        } else {
            System.out.println("Opción de clima no válida.");
        }
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        SistemaLogistica sistema = new SistemaLogistica();
        sistema.ejecutar();
    }
}