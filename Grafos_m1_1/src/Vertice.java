import java.util.ArrayList;
import java.util.List;

/**
 * Vértice do grafo.
 *
 * Cada vértice guarda seu identificador e a sua própria lista de
 * adjacência, que é a lista de arestas/arcos incidentes sobre ele.
 */
public class Vertice {

    private final String id;

    /** Lista de adjacência: arestas/arcos incidentes sobre este vértice. */
    private final List<Aresta> adjacencias;

    public Vertice(String id) {
        this.id = id;
        this.adjacencias = new ArrayList<>();
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return id;
    }
}
