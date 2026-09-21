import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 Grafo dirigido ou não dirigido representado por LISTA DE ADJACÊNCIA.
 Atributos da representação:
 -dirigido: define se os pares (v, w) são ordenados (arco) ou não (aresta);
 -vertices: os vértices do grafo, cada um com sua própria lista de adjacência;
 -arestas: lista geral das arestas/arcos, usada para busca por identificador
 e pelo algoritmo de Roy.
 */
public class Grafo {

    private final List<Vertice> vertices;
    private final List<Aresta> arestas;
    private final boolean dirigido;

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
        this.vertices = new ArrayList<>();
        this.arestas = new ArrayList<>();
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }

    // ============================================================
    // BUSCAS AUXILIARES
    // ============================================================

    /** Procura um vértice pelo identificador. Retorna null quando não existe. */
    public Vertice buscarVertice(String id) {
        for (Vertice v : vertices) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    /** Procura uma aresta/arco pelo identificador. Retorna null quando não existe. */
    public Aresta buscarAresta(String id) {
        for (Aresta a : arestas) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    /**
     Arestas incidentes sobre cada vértice, ignorando o sentido.
     Em grafo não dirigido isso já é a própria lista de adjacência. Em grafo
     dirigido a lista de adjacência guarda apenas os arcos que saem do vértice,
     por isso o mapa precisa ser montado à parte. Usado só pelo Prim, que
     trabalha sobre o grafo subjacente.
     */
    private Map<Vertice, List<Aresta>> incidenciaSemSentido() {
        Map<Vertice, List<Aresta>> mapa = new HashMap<>();
        for (Vertice v : vertices) {
            mapa.put(v, new ArrayList<>());
        }
        for (Aresta a : arestas) {
            mapa.get(a.getOrigem()).add(a);
            if (!a.isLaco()) {
                mapa.get(a.getDestino()).add(a);
            }
        }
        return mapa;
    }

    // ============================================================
    // OPERAÇÕES BÁSICAS
    // ============================================================

    /** Insere no grafo um novo vértice isolado. */
    public void inserirVertice(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O identificador do vértice não pode ser vazio.");
        }
        if (buscarVertice(id) != null) {
            throw new IllegalArgumentException("Já existe um vértice com o identificador: " + id);
        }
        vertices.add(new Vertice(id));
    }

    /** Insere no grafo uma aresta/arco entre os vértices v e w. */
    public void inserirAresta(String idAresta, String idOrigem, String idDestino, double peso) {
        if (idAresta == null || idAresta.trim().isEmpty()) {
            throw new IllegalArgumentException("O identificador da aresta/arco não pode ser vazio.");
        }
        if (buscarAresta(idAresta) != null) {
            throw new IllegalArgumentException("Já existe uma aresta/arco com o identificador: " + idAresta);
        }

        Vertice origem = buscarVertice(idOrigem);
        Vertice destino = buscarVertice(idDestino);

        if (origem == null) {
            throw new IllegalArgumentException("O vértice de origem não existe: " + idOrigem);
        }
        if (destino == null) {
            throw new IllegalArgumentException("O vértice de destino não existe: " + idDestino);
        }

        Aresta aresta = new Aresta(idAresta, origem, destino, peso);
        arestas.add(aresta);

        origem.adicionarAresta(aresta);

        // Em grafo não dirigido a aresta incide nas duas extremidades.
        // No laço a aresta é registrada uma única vez para não duplicar.
        if (!dirigido && !aresta.isLaco()) {
            destino.adicionarAresta(aresta);
        }
    }

    /** Remove a ligação (aresta/arco) identificada. */
    public void removerAresta(String idAresta) {
        Aresta aresta = buscarAresta(idAresta);
        if (aresta == null) {
            throw new IllegalArgumentException("Aresta/arco não encontrado: " + idAresta);
        }

        aresta.getOrigem().removerAresta(aresta);
        if (!dirigido && !aresta.isLaco()) {
            aresta.getDestino().removerAresta(aresta);
        }

        arestas.remove(aresta);
    }

    /** Remove o vértice e, por consequência, todas as suas ligações. */
    public void removerVertice(String idVertice) {
        Vertice vertice = buscarVertice(idVertice);
        if (vertice == null) {
            throw new IllegalArgumentException("Vértice não encontrado: " + idVertice);
        }

        List<String> paraRemover = new ArrayList<>();
        for (Aresta a : arestas) {
            if (a.incideEm(vertice)) {
                paraRemover.add(a.getId());
            }
        }
        for (String idAresta : paraRemover) {
            removerAresta(idAresta);
        }

        vertices.remove(vertice);
    }

    // ============================================================
    // CONSULTAS
    // ============================================================

    /** Verifica se v e w são vizinhos (adjacentes). */
    public boolean saoAdjacentes(String idV, String idW) {
        Vertice v = buscarVertice(idV);
        Vertice w = buscarVertice(idW);

        if (v == null || w == null) {
            return false;
        }

        for (Aresta a : v.getAdjacencias()) {
            if (dirigido) {
                if (a.getOrigem() == v && a.getDestino() == w) {
                    return true;
                }
            } else {
                if (a.incideEm(v) && a.incideEm(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Retorna o valor (peso) associado a uma aresta/arco. */
    public double retornarValorAresta(String idAresta) {
        Aresta aresta = buscarAresta(idAresta);
        if (aresta == null) {
            throw new IllegalArgumentException("Aresta/arco não encontrado: " + idAresta);
        }
        return aresta.getPeso();
    }

    /** Retorna, em texto, as extremidades de uma aresta/arco. */
    public String formatarExtremidades(String idAresta) {
        Aresta aresta = buscarAresta(idAresta);
        if (aresta == null) {
            throw new IllegalArgumentException("Aresta/arco não encontrado: " + idAresta);
        }

        String texto = "Aresta/arco: " + aresta.getId() + "\n";

        if (dirigido) {
            texto += "Extremidade inicial (cauda): " + aresta.getOrigem().getId() + "\n";
            texto += "Extremidade final (cabeça): " + aresta.getDestino().getId() + "\n";
        } else {
            texto += "Extremidade 1: " + aresta.getOrigem().getId() + "\n";
            texto += "Extremidade 2: " + aresta.getDestino().getId() + "\n";
        }

        return texto + "Peso: " + aresta.getPesoFormatado();
    }

    // ============================================================
    // LISTA DE ADJACÊNCIA EM TEXTO
    // ============================================================

    public String formatarGrafo() {
        String texto = "========== LISTA DE ADJACÊNCIA ==========\n";
        texto += dirigido ? "Tipo: GRAFO DIRIGIDO\n" : "Tipo: GRAFO NÃO DIRIGIDO\n";

        if (vertices.isEmpty()) {
            return texto + "O grafo não possui vértices.";
        }

        for (Vertice v : vertices) {
            texto += v.getId() + " -> ";

            if (v.getAdjacencias().isEmpty()) {
                texto += "(isolado)\n";
                continue;
            }

            for (Aresta a : v.getAdjacencias()) {
                String ligacao = dirigido ? " -> " : " -- ";
                texto += "[" + a.getId() + ligacao + a.oposto(v).getId()
                        + ", peso=" + a.getPesoFormatado() + "] ";
            }
            texto += "\n";
        }
        return texto.trim();
    }

    // ============================================================
    // ALGORITMO DE PRIM: ÁRVORE GERADORA MÍNIMA
    // ============================================================

    /**
     * Algoritmo de Prim a partir de um vértice inicial.
     *
     * Prim é definido para grafo não dirigido. Em grafo dirigido o
     * algoritmo é aplicado sobre o grafo subjacente, ignorando o sentido.
     * Se o grafo for desconexo, o resultado é uma floresta geradora
     * mínima, com uma árvore por componente.
     */
    public ResultadoAGM prim(String idInicial) {
        if (vertices.isEmpty()) {
            throw new IllegalStateException("O grafo não possui vértices.");
        }

        Vertice inicial = (idInicial == null) ? vertices.get(0) : buscarVertice(idInicial);
        if (inicial == null) {
            throw new IllegalArgumentException("Vértice inicial não existe: " + idInicial);
        }

        Map<Vertice, List<Aresta>> incidencia = incidenciaSemSentido();
        Set<Vertice> visitados = new HashSet<>();
        List<Aresta> arvore = new ArrayList<>();
        double custo = 0.0;
        int componentes = 0;

        // O vértice escolhido é a raiz da primeira árvore; os demais
        // servem de raiz apenas se o grafo for desconexo.
        List<Vertice> raizes = new ArrayList<>();
        raizes.add(inicial);
        for (Vertice v : vertices) {
            if (v != inicial) {
                raizes.add(v);
            }
        }

        for (Vertice raiz : raizes) {
            if (visitados.contains(raiz)) {
                continue;
            }
            componentes++;

            PriorityQueue<Aresta> fila = new PriorityQueue<>(
                    Comparator.comparingDouble(Aresta::getPeso).thenComparing(Aresta::getId));

            expandir(raiz, visitados, incidencia, fila);

            while (!fila.isEmpty()) {
                Aresta a = fila.poll();

                Vertice fora = null;
                if (!visitados.contains(a.getOrigem())) {
                    fora = a.getOrigem();
                } else if (!visitados.contains(a.getDestino())) {
                    fora = a.getDestino();
                }

                // Aresta com as duas extremidades já na árvore formaria ciclo.
                if (fora == null) {
                    continue;
                }

                arvore.add(a);
                custo += a.getPeso();
                expandir(fora, visitados, incidencia, fila);
            }
        }

        return new ResultadoAGM(inicial, arvore, custo, componentes);
    }

    /** Marca o vértice como pertencente à árvore e enfileira suas arestas candidatas. */
    private void expandir(Vertice v, Set<Vertice> visitados,
                          Map<Vertice, List<Aresta>> incidencia,
                          PriorityQueue<Aresta> fila) {
        visitados.add(v);
        for (Aresta a : incidencia.get(v)) {
            if (!visitados.contains(a.oposto(v))) {
                fila.add(a);
            }
        }
    }

    // ============================================================
    // BUSCA EM PROFUNDIDADE GUIADA
    // ============================================================

    /**
     * Busca em profundidade com vértice de saída e de chegada.
     * A busca é interrompida assim que o vértice de chegada é alcançado.
     * Em grafo dirigido o sentido dos arcos é respeitado.
     */
    public ResultadoBusca buscaEmProfundidade(String idOrigem, String idDestino) {
        Vertice origem = buscarVertice(idOrigem);
        Vertice destino = buscarVertice(idDestino);

        if (origem == null) {
            throw new IllegalArgumentException("Vértice de saída não existe: " + idOrigem);
        }
        if (destino == null) {
            throw new IllegalArgumentException("Vértice de chegada não existe: " + idDestino);
        }

        Set<Vertice> visitados = new HashSet<>();
        List<Vertice> ordem = new ArrayList<>();
        List<Aresta> arvore = new ArrayList<>();
        Map<Vertice, Aresta> chegada = new HashMap<>();

        boolean encontrou = explorar(origem, destino, visitados, ordem, arvore, chegada);

        List<Aresta> caminhoArestas = new ArrayList<>();
        List<Vertice> caminhoVertices = new ArrayList<>();
        double custoCaminho = 0.0;

        if (encontrou) {
            Vertice atual = destino;
            caminhoVertices.add(atual);
            while (chegada.containsKey(atual)) {
                Aresta a = chegada.get(atual);
                caminhoArestas.add(0, a);
                custoCaminho += a.getPeso();
                atual = a.oposto(atual);
                caminhoVertices.add(0, atual);
            }
        }

        return new ResultadoBusca(origem, destino, encontrou, ordem, arvore,
                caminhoVertices, caminhoArestas, custoCaminho);
    }

    /**
     * Percurso recursivo em profundidade sobre a lista de adjacência.
     * Visita o vértice atual, e para cada aresta ainda não explorada desce
     * no vizinho não visitado. Retorna verdadeiro assim que alcança o
     * destino, o que interrompe a busca.
     *
     * Em grafo dirigido a lista de adjacência já contém apenas os arcos que
     * saem do vértice, então o sentido é respeitado sem tratamento extra.
     */
    private boolean explorar(Vertice atual, Vertice destino,
                             Set<Vertice> visitados, List<Vertice> ordem,
                             List<Aresta> arvore, Map<Vertice, Aresta> chegada) {

        visitados.add(atual);
        ordem.add(atual);

        if (atual == destino) {
            return true;
        }

        for (Aresta a : atual.getAdjacencias()) {
            Vertice proximo = a.oposto(atual);

            if (visitados.contains(proximo)) {
                continue;
            }

            // Aresta da árvore: foi por ela que o vizinho foi descoberto.
            arvore.add(a);
            chegada.put(proximo, a);

            if (explorar(proximo, destino, visitados, ordem, arvore, chegada)) {
                return true;
            }
        }

        return false;
    }

    // ============================================================
    // ALGORITMO DE ROY: COMPONENTES CONEXAS / FORTEMENTE CONEXAS
    // ============================================================

    /**
     * Matriz de alcançabilidade (fecho transitivo) pelo algoritmo de Roy,
     * também conhecido como Roy-Warshall, aplicado sobre a matriz booleana
     * de adjacência.
     */
    public boolean[][] matrizAlcancabilidade() {
        int n = vertices.size();
        boolean[][] r = new boolean[n][n];

        // Todo vértice alcança a si mesmo.
        for (int i = 0; i < n; i++) {
            r[i][i] = true;
        }

        // A posição do vértice na lista já é o seu índice na matriz.
        for (Aresta a : arestas) {
            int i = vertices.indexOf(a.getOrigem());
            int j = vertices.indexOf(a.getDestino());
            r[i][j] = true;
            if (!dirigido) {
                r[j][i] = true;
            }
        }

        // Fecho transitivo: se i alcança k e k alcança j, então i alcança j.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (!r[i][k]) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (r[k][j]) {
                        r[i][j] = true;
                    }
                }
            }
        }
        return r;
    }

    /**
     * Componentes conexas (grafo não dirigido) ou fortemente conexas
     * (grafo dirigido): v e w pertencem ao mesmo conjunto quando
     * v alcança w e w alcança v na matriz de alcançabilidade.
     */
    public List<List<Vertice>> royComponentes() {
        boolean[][] r = matrizAlcancabilidade();
        int n = vertices.size();
        boolean[] agrupado = new boolean[n];
        List<List<Vertice>> componentes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (agrupado[i]) {
                continue;
            }
            List<Vertice> componente = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (!agrupado[j] && r[i][j] && r[j][i]) {
                    agrupado[j] = true;
                    componente.add(vertices.get(j));
                }
            }
            componentes.add(componente);
        }
        return componentes;
    }

    // ============================================================
    // RESULTADOS DOS ALGORITMOS
    // ============================================================

    /** Resultado do algoritmo de Prim. */
    public static class ResultadoAGM {

        private final Vertice inicial;
        private final List<Aresta> arestas;
        private final double custo;
        private final int componentes;

        public ResultadoAGM(Vertice inicial, List<Aresta> arestas, double custo, int componentes) {
            this.inicial = inicial;
            this.arestas = arestas;
            this.custo = custo;
            this.componentes = componentes;
        }

        public Vertice getInicial() {
            return inicial;
        }

        public List<Aresta> getArestas() {
            return arestas;
        }

        public double getCusto() {
            return custo;
        }

        public int getComponentes() {
            return componentes;
        }

        /** Verdadeiro quando o grafo é desconexo e o resultado é uma floresta. */
        public boolean isFloresta() {
            return componentes > 1;
        }
    }

    /** Resultado da busca em profundidade guiada. */
    public static class ResultadoBusca {

        private final Vertice origem;
        private final Vertice destino;
        private final boolean encontrou;
        private final List<Vertice> ordemVisita;
        private final List<Aresta> arestasArvore;
        private final List<Vertice> caminhoVertices;
        private final List<Aresta> caminhoArestas;
        private final double custoCaminho;

        public ResultadoBusca(Vertice origem, Vertice destino, boolean encontrou,
                              List<Vertice> ordemVisita, List<Aresta> arestasArvore,
                              List<Vertice> caminhoVertices, List<Aresta> caminhoArestas,
                              double custoCaminho) {
            this.origem = origem;
            this.destino = destino;
            this.encontrou = encontrou;
            this.ordemVisita = ordemVisita;
            this.arestasArvore = arestasArvore;
            this.caminhoVertices = caminhoVertices;
            this.caminhoArestas = caminhoArestas;
            this.custoCaminho = custoCaminho;
        }

        public Vertice getOrigem() {
            return origem;
        }

        public Vertice getDestino() {
            return destino;
        }

        public boolean isEncontrou() {
            return encontrou;
        }

        public List<Vertice> getOrdemVisita() {
            return ordemVisita;
        }

        public List<Aresta> getArestasArvore() {
            return arestasArvore;
        }

        public List<Vertice> getCaminhoVertices() {
            return caminhoVertices;
        }

        public List<Aresta> getCaminhoArestas() {
            return caminhoArestas;
        }

        public double getCustoCaminho() {
            return custoCaminho;
        }
    }
}
