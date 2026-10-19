import java.util.ArrayList;
import java.util.List;

/**
 Vértice do grafo.
 Cada vértice guarda seu id e a sua própria lista de
 adjacência, que é a lista de arestas/arcos incidentes sobre ele.
 Pode guardar também a coordenada geográfica, usada como heurística
 do A*. Vértice sem coordenada continua válido, para os grafos comuns.
 */
public class Vertice {

    /** Raio médio da Terra em quilômetros, usado pela fórmula de Haversine. */
    private static final double RAIO_TERRA_KM = 6371.0;

    private final String id;

    private final double latitude;
    private final double longitude;
    private final boolean temCoordenadas;

    /** Lista de adjacência: arestas/arcos incidentes sobre este vértice. */
    private final List<Aresta> adjacencias;

    /** Vértice comum, sem coordenada geográfica. */
    public Vertice(String id) {
        this(id, 0.0, 0.0, false);
    }

    /** Vértice com coordenada geográfica, em graus decimais. */
    public Vertice(String id, double latitude, double longitude) {
        this(id, latitude, longitude, true);
    }

    private Vertice(String id, double latitude, double longitude, boolean temCoordenadas) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temCoordenadas = temCoordenadas;
        this.adjacencias = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean temCoordenadas() {
        return temCoordenadas;
    }

    public List<Aresta> getAdjacencias() {
        return adjacencias;
    }

    public void adicionarAresta(Aresta aresta) {
        adjacencias.add(aresta);
    }

    public void removerAresta(Aresta aresta) {
        adjacencias.remove(aresta);
    }

    /** Grau do vértice considerando a lista de adjacência armazenada. */
    public int getGrau() {
        return adjacencias.size();
    }

    /**
     Distância em linha reta, em quilômetros, entre este vértice e outro,
     pela fórmula de Haversine.

     Haversine mede sobre a superfície da esfera, então o valor nunca é maior
     que a distância percorrida por estrada. É isso que torna a heurística
     admissível e garante que o A* encontre o caminho de menor custo.

     Quando algum dos dois vértices não tem coordenada, devolve zero: sem
     heurística o A* se comporta como o algoritmo de Dijkstra, que continua
     correto, apenas explora mais vértices.
     */
    public double distanciaHaversine(Vertice outro) {
        if (!this.temCoordenadas || !outro.temCoordenadas) {
            return 0.0;
        }

        double deltaLat = Math.toRadians(outro.latitude - this.latitude);
        double deltaLon = Math.toRadians(outro.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(this.latitude))
                * Math.cos(Math.toRadians(outro.latitude))
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));

        return RAIO_TERRA_KM * c;
    }

    @Override
    public String toString() {
        return id;
    }
}
