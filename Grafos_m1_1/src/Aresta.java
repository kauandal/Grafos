import java.util.Locale;

/**
 Aresta (não ordenado) ou arco (ordenado) do grafo.
 A interpretação de origem/destino como ordenada ou não ordenada
 depende do grafo ao qual a aresta pertence.
 */
public class Aresta {

    private final String id;
    private final Vertice origem;
    private final Vertice destino;
    private final double peso;

    public Aresta(String id, Vertice origem, Vertice destino, double peso) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    public String getId() {
        return id;
    }

    public Vertice getOrigem() {
        return origem;
    }

    public Vertice getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    /** Retorna a outra extremidade da aresta em relação ao vértice informado. */
    public Vertice oposto(Vertice v) {
        return (v == origem) ? destino : origem;
    }

    /** Verdadeiro quando a aresta é um laço (as duas extremidades são o mesmo vértice). */
    public boolean isLaco() {
        return origem == destino;
    }

    /** Verdadeiro quando o vértice informado é extremidade desta aresta. */
    public boolean incideEm(Vertice v) {
        return origem == v || destino == v;
    }

    /** Peso sem casas decimais inúteis, apenas para exibição. */
    public String getPesoFormatado() {
        return formatarPeso(peso);
    }

    public static String formatarPeso(double peso) {
        if (peso == Math.rint(peso) && !Double.isInfinite(peso)) {
            return String.valueOf((long) peso);
        }
        return String.format(Locale.ROOT, "%.2f", peso);
    }

    @Override
    public String toString() {
        return id + " (" + origem.getId() + " -> " + destino.getId()
                + ", peso=" + getPesoFormatado() + ")";
    }
}
