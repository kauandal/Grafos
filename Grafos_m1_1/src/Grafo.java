import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Grafo dirigido ou não dirigido representado por LISTA DE ADJACÊNCIA.
 *
 * Atributos da representação:
 * - dirigido: define se os pares (v, w) são ordenados (arco) ou não (aresta);
 * - vertices: os vértices do grafo, cada um com sua própria lista de adjacência;
 * - arestas: lista geral das arestas/arcos, usada para busca por identificador
 *   e para montar as matrizes de adjacência e de incidência.
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

    /** Posição de cada vértice na ordem atual da lista, usada pelas matrizes. */
    private Map<Vertice, Integer> indices() {
        Map<Vertice, Integer> indices = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            indices.put(vertices.get(i), i);
        }
        return indices;
    }

    /**
     * Monta, a partir das listas de adjacência, o mapa de arestas incidentes
     * de cada vértice.
     *
     * respeitarDirecao = true  -> apenas arcos que saem do vértice;
     * respeitarDirecao = false -> toda aresta que incide no vértice.
     */
    private Map<Vertice, List<Aresta>> mapaIncidencia(boolean respeitarDirecao) {
        Map<Vertice, List<Aresta>> mapa = new LinkedHashMap<>();
        for (Vertice v : vertices) {
            mapa.put(v, new ArrayList<>());
        }
        for (Aresta a : arestas) {
            mapa.get(a.getOrigem()).add(a);
            if (!respeitarDirecao && !a.isLaco()) {
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

        StringBuilder sb = new StringBuilder();
        sb.append("Aresta/arco: ").append(aresta.getId()).append('\n');

        if (dirigido) {
            sb.append("Extremidade inicial (cauda): ").append(aresta.getOrigem().getId()).append('\n');
            sb.append("Extremidade final (cabeça): ").append(aresta.getDestino().getId()).append('\n');
        } else {
            sb.append("Extremidade 1: ").append(aresta.getOrigem().getId()).append('\n');
            sb.append("Extremidade 2: ").append(aresta.getDestino().getId()).append('\n');
        }

        sb.append("Peso: ").append(aresta.getPesoFormatado());
        return sb.toString();
    }

    public void retornarExtremidades(String idAresta) {
        System.out.println(formatarExtremidades(idAresta));
    }

    // ============================================================
    // MATRIZ DE ADJACÊNCIA
    // ============================================================

    /**
     * Matriz de adjacência com os pesos.
     *
     * Quando existem arestas paralelas entre o mesmo par de vértices,
     * prevalece o menor peso, para não depender da ordem de inserção.
     */
    public double[][] matrizAdjacencia() {
        int n = vertices.size();
        double[][] matriz = new double[n][n];
        boolean[][] existe = new boolean[n][n];
        Map<Vertice, Integer> indices = indices();

        for (Aresta a : arestas) {
            int i = indices.get(a.getOrigem());
            int j = indices.get(a.getDestino());

            if (!existe[i][j] || a.getPeso() < matriz[i][j]) {
                matriz[i][j] = a.getPeso();
                existe[i][j] = true;
            }

            if (!dirigido && (!existe[j][i] || a.getPeso() < matriz[j][i])) {
                matriz[j][i] = a.getPeso();
                existe[j][i] = true;
            }
        }
        return matriz;
    }

    public String formatarMatrizAdjacencia() {
        if (vertices.isEmpty()) {
            return "O grafo não possui vértices.";
        }

        double[][] matriz = matrizAdjacencia();
        StringBuilder sb = new StringBuilder();

        sb.append("========== MATRIZ DE ADJACÊNCIA ==========\n");
        sb.append(String.format("%8s", ""));
        for (Vertice v : vertices) {
            sb.append(String.format("%10s", v.getId()));
        }
        sb.append('\n');

        for (int i = 0; i < matriz.length; i++) {
            sb.append(String.format("%8s", vertices.get(i).getId()));
            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(String.format("%10s", Aresta.formatarPeso(matriz[i][j])));
            }
            sb.append('\n');
        }

        sb.append("Observação: 0 indica ausência de aresta/arco entre o par.");
        return sb.toString();
    }

    public void mostrarMatrizAdjacencia() {
        System.out.println(formatarMatrizAdjacencia());
    }

    // ============================================================
    // MATRIZ DE INCIDÊNCIA
    // ============================================================

    /**
     * Matriz de incidência vértice x aresta.
     *
     * Não dirigido: 1 nas extremidades e 2 no laço.
     * Dirigido: -1 na cauda, 1 na cabeça e 0 no laço.
     */
    public int[][] matrizIncidencia() {
        int n = vertices.size();
        int m = arestas.size();
        int[][] matriz = new int[n][m];
        Map<Vertice, Integer> indices = indices();

        for (int j = 0; j < m; j++) {
            Aresta a = arestas.get(j);
            int origem = indices.get(a.getOrigem());
            int destino = indices.get(a.getDestino());

            if (a.isLaco()) {
                matriz[origem][j] = dirigido ? 0 : 2;
                continue;
            }

            if (dirigido) {
                matriz[origem][j] = -1;
                matriz[destino][j] = 1;
            } else {
                matriz[origem][j] = 1;
                matriz[destino][j] = 1;
            }
        }
        return matriz;
    }

    public String formatarMatrizIncidencia() {
        if (vertices.isEmpty()) {
            return "O grafo não possui vértices.";
        }
        if (arestas.isEmpty()) {
            return "O grafo não possui arestas/arcos.";
        }

        int[][] matriz = matrizIncidencia();
        StringBuilder sb = new StringBuilder();

        sb.append("========== MATRIZ DE INCIDÊNCIA ==========\n");
        sb.append(String.format("%8s", ""));
        for (Aresta a : arestas) {
            sb.append(String.format("%10s", a.getId()));
        }
        sb.append('\n');

        for (int i = 0; i < matriz.length; i++) {
            sb.append(String.format("%8s", vertices.get(i).getId()));
            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(String.format("%10d", matriz[i][j]));
            }
            sb.append('\n');
        }

        if (dirigido) {
            sb.append("Convenção: -1 cauda, 1 cabeça, 0 laço ou não incidente.");
        } else {
            sb.append("Convenção: 1 extremidade, 2 laço, 0 não incidente.");
        }
        return sb.toString();
    }

    public void mostrarMatrizIncidencia() {
        System.out.println(formatarMatrizIncidencia());
    }

    // ============================================================
    // LISTA DE ADJACÊNCIA EM TEXTO
    // ============================================================

    public String formatarGrafo() {
        StringBuilder sb = new StringBuilder();

        sb.append("========== LISTA DE ADJACÊNCIA ==========\n");
        sb.append(dirigido ? "Tipo: GRAFO DIRIGIDO\n" : "Tipo: GRAFO NÃO DIRIGIDO\n");

        if (vertices.isEmpty()) {
            sb.append("O grafo não possui vértices.");
            return sb.toString();
        }

        for (Vertice v : vertices) {
            sb.append(v.getId()).append(" -> ");

            if (v.getAdjacencias().isEmpty()) {
                sb.append("(isolado)\n");
                continue;
            }

            for (Aresta a : v.getAdjacencias()) {
                String vizinho = a.oposto(v).getId();
                String ligacao = dirigido ? " -> " : " -- ";
                sb.append('[').append(a.getId()).append(ligacao).append(vizinho)
                        .append(", peso=").append(a.getPesoFormatado()).append("] ");
            }
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    public void mostrarGrafo() {
        System.out.println(formatarGrafo());
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

        Map<Vertice, List<Aresta>> incidencia = mapaIncidencia(false);
        Set<Vertice> visitados = new LinkedHashSet<>();
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

        Map<Vertice, List<Aresta>> incidencia = mapaIncidencia(dirigido);

        Set<Vertice> visitados = new LinkedHashSet<>();
        List<Vertice> ordem = new ArrayList<>();
        List<Aresta> arvore = new ArrayList<>();
        Map<Vertice, Aresta> chegada = new HashMap<>();

        boolean encontrou = explorar(origem, destino, incidencia, visitados, ordem, arvore, chegada);

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
     * Percurso em profundidade com pilha explícita, o que evita estouro de
     * pilha de recursão em grafos grandes. A ordem de visita é a mesma da
     * versão recursiva: segue sempre pela primeira aresta ainda não explorada.
     */
    private boolean explorar(Vertice origem, Vertice destino,
                             Map<Vertice, List<Aresta>> incidencia,
                             Set<Vertice> visitados, List<Vertice> ordem,
                             List<Aresta> arvore, Map<Vertice, Aresta> chegada) {

        visitados.add(origem);
        ordem.add(origem);

        if (origem == destino) {
            return true;
        }

        Deque<Vertice> pilhaVertices = new ArrayDeque<>();
        Deque<Iterator<Aresta>> pilhaArestas = new ArrayDeque<>();

        pilhaVertices.push(origem);
        pilhaArestas.push(incidencia.get(origem).iterator());

        while (!pilhaVertices.isEmpty()) {
            Vertice atual = pilhaVertices.peek();
            Iterator<Aresta> it = pilhaArestas.peek();
            boolean avancou = false;

            while (it.hasNext()) {
                Aresta a = it.next();
                Vertice proximo = a.oposto(atual);

                if (visitados.contains(proximo)) {
                    continue;
                }

                visitados.add(proximo);
                ordem.add(proximo);
                arvore.add(a);
                chegada.put(proximo, a);

                if (proximo == destino) {
                    return true;
                }

                pilhaVertices.push(proximo);
                pilhaArestas.push(incidencia.get(proximo).iterator());
                avancou = true;
                break;
            }

            if (!avancou) {
                pilhaVertices.pop();
                pilhaArestas.pop();
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
        Map<Vertice, Integer> indices = indices();

        // Todo vértice alcança a si mesmo.
        for (int i = 0; i < n; i++) {
            r[i][i] = true;
        }

        for (Aresta a : arestas) {
            int i = indices.get(a.getOrigem());
            int j = indices.get(a.getDestino());
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

    public String formatarMatrizAlcancabilidade() {
        if (vertices.isEmpty()) {
            return "O grafo não possui vértices.";
        }

        boolean[][] r = matrizAlcancabilidade();
        StringBuilder sb = new StringBuilder();

        sb.append("===== MATRIZ DE ALCANÇABILIDADE (ROY) =====\n");
        sb.append(String.format("%8s", ""));
        for (Vertice v : vertices) {
            sb.append(String.format("%10s", v.getId()));
        }
        sb.append('\n');

        for (int i = 0; i < r.length; i++) {
            sb.append(String.format("%8s", vertices.get(i).getId()));
            for (int j = 0; j < r[i].length; j++) {
                sb.append(String.format("%10d", r[i][j] ? 1 : 0));
            }
            sb.append('\n');
        }
        return sb.toString().trim();
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
