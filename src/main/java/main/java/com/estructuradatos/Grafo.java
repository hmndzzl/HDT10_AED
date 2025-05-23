package main.java.com.estructuradatos;

import java.util.*;

/**
 * Implementación de un grafo dirigido usando matriz de adyacencia
 * para el algoritmo de Floyd-Warshall
 */
public class Grafo {
    private Map<String, Integer> ciudadIndice;
    private Map<Integer, String> indiceCiudad;
    private double[][] matrizAdyacencia;
    private int[][] siguiente; // Para reconstruir caminos
    private int numCiudades;
    private static final double INFINITO = Double.MAX_VALUE;
    
    public Grafo() {
        this.ciudadIndice = new HashMap<>();
        this.indiceCiudad = new HashMap<>();
        this.numCiudades = 0;
    }
    
    /**
     * Inicializa el grafo con un número específico de ciudades
     */
    public void inicializarGrafo(int numCiudades) {
        this.numCiudades = numCiudades;
        this.matrizAdyacencia = new double[numCiudades][numCiudades];
        this.siguiente = new int[numCiudades][numCiudades];
        
        // Inicializar matriz con infinito y diagonal con 0
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i == j) {
                    matrizAdyacencia[i][j] = 0;
                } else {
                    matrizAdyacencia[i][j] = INFINITO;
                }
                siguiente[i][j] = -1;
            }
        }
    }
    
    /**
     * Agrega una nueva ciudad al grafo
     */
    public void agregarCiudad(String ciudad) {
        if (!ciudadIndice.containsKey(ciudad)) {
            int indice = numCiudades;
            ciudadIndice.put(ciudad, indice);
            indiceCiudad.put(indice, ciudad);
            numCiudades++;
        }
    }
    
    /**
     * Agrega o actualiza una arista entre dos ciudades
     */
    public void agregarArista(String origen, String destino, double peso) {
        int indiceOrigen = ciudadIndice.get(origen);
        int indiceDestino = ciudadIndice.get(destino);
        
        matrizAdyacencia[indiceOrigen][indiceDestino] = peso;
        siguiente[indiceOrigen][indiceDestino] = indiceDestino;
    }
    
    /**
     * Elimina una arista entre dos ciudades
     */
    public void eliminarArista(String origen, String destino) {
        int indiceOrigen = ciudadIndice.get(origen);
        int indiceDestino = ciudadIndice.get(destino);
        
        matrizAdyacencia[indiceOrigen][indiceDestino] = INFINITO;
        siguiente[indiceOrigen][indiceDestino] = -1;
    }
    
    /**
     * Implementación del algoritmo de Floyd-Warshall
     */
    public void algoritmFloyd() {
        // Inicializar matriz siguiente para caminos directos
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j && matrizAdyacencia[i][j] != INFINITO) {
                    siguiente[i][j] = j;
                }
            }
        }
        
        // Algoritmo de Floyd-Warshall
        for (int k = 0; k < numCiudades; k++) {
            for (int i = 0; i < numCiudades; i++) {
                for (int j = 0; j < numCiudades; j++) {
                    if (matrizAdyacencia[i][k] != INFINITO && 
                        matrizAdyacencia[k][j] != INFINITO &&
                        matrizAdyacencia[i][k] + matrizAdyacencia[k][j] < matrizAdyacencia[i][j]) {
                        
                        matrizAdyacencia[i][j] = matrizAdyacencia[i][k] + matrizAdyacencia[k][j];
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }
    }
    
    /**
     * Obtiene el camino más corto entre dos ciudades
     */
    public List<String> obtenerCamino(String origen, String destino) {
        List<String> camino = new ArrayList<>();
        
        if (!ciudadIndice.containsKey(origen) || !ciudadIndice.containsKey(destino)) {
            return camino;
        }
        
        int indiceOrigen = ciudadIndice.get(origen);
        int indiceDestino = ciudadIndice.get(destino);
        
        if (matrizAdyacencia[indiceOrigen][indiceDestino] == INFINITO) {
            return camino; // No hay camino
        }
        
        camino.add(origen);
        int actual = indiceOrigen;
        
        while (actual != indiceDestino) {
            actual = siguiente[actual][indiceDestino];
            if (actual == -1) break;
            camino.add(indiceCiudad.get(actual));
        }
        
        return camino;
    }
    
    /**
     * Obtiene la distancia más corta entre dos ciudades
     */
    public double obtenerDistancia(String origen, String destino) {
        if (!ciudadIndice.containsKey(origen) || !ciudadIndice.containsKey(destino)) {
            return INFINITO;
        }
        
        int indiceOrigen = ciudadIndice.get(origen);
        int indiceDestino = ciudadIndice.get(destino);
        
        return matrizAdyacencia[indiceOrigen][indiceDestino];
    }
    
    /**
     * Calcula el centro del grafo
     * El centro es el vértice que minimiza la máxima distancia a cualquier otro vértice
     */
    public String calcularCentroGrafo() {
        double menorExcentricidad = INFINITO;
        String centro = "";
        
        for (int i = 0; i < numCiudades; i++) {
            double excentricidad = 0;
            
            // Calcular la excentricidad del vértice i
            for (int j = 0; j < numCiudades; j++) {
                if (i != j && matrizAdyacencia[i][j] != INFINITO) {
                    excentricidad = Math.max(excentricidad, matrizAdyacencia[i][j]);
                }
            }
            
            // Si esta excentricidad es menor, actualizar el centro
            if (excentricidad < menorExcentricidad) {
                menorExcentricidad = excentricidad;
                centro = indiceCiudad.get(i);
            }
        }
        
        return centro;
    }
    
    /**
     * Muestra la matriz de adyacencia
     */
    public void mostrarMatrizAdyacencia() {
        System.out.println("\nMatriz de Adyacencia:");
        System.out.print("        ");
        
        // Encabezados de columnas
        for (int j = 0; j < numCiudades; j++) {
            System.out.printf("%12s", indiceCiudad.get(j));
        }
        System.out.println();
        
        // Filas con datos
        for (int i = 0; i < numCiudades; i++) {
            System.out.printf("%8s", indiceCiudad.get(i));
            for (int j = 0; j < numCiudades; j++) {
                if (matrizAdyacencia[i][j] == INFINITO) {
                    System.out.printf("%12s", "∞");
                } else {
                    System.out.printf("%12.1f", matrizAdyacencia[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Verifica si una ciudad existe en el grafo
     */
    public boolean existeCiudad(String ciudad) {
        return ciudadIndice.containsKey(ciudad);
    }
    
    /**
     * Obtiene todas las ciudades del grafo
     */
    public Set<String> obtenerCiudades() {
        return ciudadIndice.keySet();
    }
    
    /**
     * Obtiene el número de ciudades
     */
    public int getNumCiudades() {
        return numCiudades;
    }
}