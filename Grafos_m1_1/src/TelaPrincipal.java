import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Janela principal do programa.
 *
 * Reúne, em um único lugar, o menu com todas as operações sobre o grafo,
 * a visualização gráfica do grafo ativo e uma área de saída em texto com
 * o resultado de cada operação.
 */
public class TelaPrincipal extends JFrame {

    private Grafo grafo;

    private final PainelGrafo painelGrafo;
    private final JTextArea areaSaida = new JTextArea();
    private final JLabel barraStatus = new JLabel();

    public TelaPrincipal() {
        super("Teoria dos Grafos - Kauan Rocha Dalfovo e Gustavo Odilon");

        this.grafo = new Grafo(false);
        this.painelGrafo = new PainelGrafo(grafo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(montarMenu());
        montarConteudo();

        carregarExemploNaoDirigido();

        setSize(1120, 780);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);
    }

    // ============================================================
    // MONTAGEM DA INTERFACE
    // ============================================================

    private void montarConteudo() {
        JPanel moldura = new JPanel(new BorderLayout(8, 8));
        moldura.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelDesenho = new JPanel(new BorderLayout());
        painelDesenho.setBorder(BorderFactory.createTitledBorder("Visualização do grafo ativo"));
        painelDesenho.add(painelGrafo, BorderLayout.CENTER);

        areaSaida.setEditable(false);
        areaSaida.setLineWrap(false);
        areaSaida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaSaida.setMargin(new java.awt.Insets(6, 8, 6, 8));

        JScrollPane rolagem = new JScrollPane(areaSaida);
        rolagem.setBorder(BorderFactory.createTitledBorder("Saída das operações"));
        rolagem.setPreferredSize(new Dimension(1080, 230));

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelDesenho, rolagem);
        divisor.setResizeWeight(0.72);
        divisor.setBorder(null);

        barraStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xCE, 0xD4)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        barraStatus.setHorizontalAlignment(SwingConstants.LEFT);

        moldura.add(divisor, BorderLayout.CENTER);
        moldura.add(barraStatus, BorderLayout.SOUTH);

        setContentPane(moldura);
    }

    private JMenuBar montarMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu menuGrafo = new JMenu("Grafo");
        menuGrafo.add(item("Novo grafo não dirigido", e -> novoGrafo(false)));
        menuGrafo.add(item("Novo grafo dirigido", e -> novoGrafo(true)));
        menuGrafo.addSeparator();
        menuGrafo.add(item("Carregar exemplo não dirigido", e -> carregarExemploNaoDirigido()));
        menuGrafo.add(item("Carregar exemplo dirigido", e -> carregarExemploDirigido()));
        menuGrafo.addSeparator();
        menuGrafo.add(item("Mostrar lista de adjacência", e -> imprimir(grafo.formatarGrafo())));
        menuGrafo.addSeparator();
        menuGrafo.add(item("Sair", e -> dispose()));

        JMenu menuVertice = new JMenu("Vértice");
        menuVertice.add(item("Inserir vértice isolado", e -> inserirVertice()));
        menuVertice.add(item("Remover vértice", e -> removerVertice()));

        JMenu menuAresta = new JMenu("Aresta/Arco");
        menuAresta.add(item("Inserir aresta/arco", e -> inserirAresta()));
        menuAresta.add(item("Remover aresta/arco", e -> removerAresta()));
        menuAresta.addSeparator();
        menuAresta.add(item("Verificar se dois vértices são adjacentes", e -> verificarAdjacencia()));
        menuAresta.add(item("Valor (peso) da aresta/arco", e -> valorAresta()));
        menuAresta.add(item("Extremidades da aresta/arco", e -> extremidadesAresta()));

        JMenu menuMatrizes = new JMenu("Matrizes");
        menuMatrizes.add(item("Matriz de adjacência", e -> imprimir(grafo.formatarMatrizAdjacencia())));
        menuMatrizes.add(item("Matriz de incidência", e -> imprimir(grafo.formatarMatrizIncidencia())));

        JMenu menuAlgoritmos = new JMenu("Algoritmos");
        menuAlgoritmos.add(item("Prim: árvore geradora mínima", e -> executarPrim()));
        menuAlgoritmos.add(item("Busca em profundidade guiada", e -> executarBusca()));
        menuAlgoritmos.add(item("Roy: componentes conexas/fortemente conexas", e -> executarRoy()));

        JMenu menuExibicao = new JMenu("Exibição");
        menuExibicao.add(item("Reorganizar vértices em círculo", e -> painelGrafo.layoutCircular()));
        menuExibicao.add(item("Limpar destaques do desenho", e -> painelGrafo.limparDestaques()));
        menuExibicao.add(item("Limpar área de saída", e -> areaSaida.setText("")));

        JMenu menuAjuda = new JMenu("Ajuda");
        menuAjuda.add(item("Sobre", e -> mostrarSobre()));

        barra.add(menuGrafo);
        barra.add(menuVertice);
        barra.add(menuAresta);
        barra.add(menuMatrizes);
        barra.add(menuAlgoritmos);
        barra.add(menuExibicao);
        barra.add(menuAjuda);
        return barra;
    }

    private JMenuItem item(String texto, ActionListener acao) {
        JMenuItem mi = new JMenuItem(texto);
        mi.addActionListener(acao);
        return mi;
    }

    // ============================================================
    // OPERAÇÕES DO MENU: GRAFO
    // ============================================================

    private void novoGrafo(boolean dirigido) {
        grafo = new Grafo(dirigido);
        painelGrafo.setGrafo(grafo);
        areaSaida.setText("");
        imprimir("Novo grafo " + (dirigido ? "dirigido" : "não dirigido") + " criado.");
    }

    private void carregarExemploNaoDirigido() {
        grafo = new Grafo(false);
        for (String id : new String[]{"A", "B", "C", "D", "E", "F"}) {
            grafo.inserirVertice(id);
        }
        grafo.inserirAresta("a1", "A", "B", 4);
        grafo.inserirAresta("a2", "A", "C", 2);
        grafo.inserirAresta("a3", "B", "C", 1);
        grafo.inserirAresta("a4", "B", "D", 5);
        grafo.inserirAresta("a5", "C", "D", 8);
        grafo.inserirAresta("a6", "C", "E", 10);
        grafo.inserirAresta("a7", "D", "E", 2);
        grafo.inserirAresta("a8", "D", "F", 6);
        grafo.inserirAresta("a9", "E", "F", 3);

        painelGrafo.setGrafo(grafo);
        areaSaida.setText("");
        imprimir("Exemplo não dirigido carregado.");
        imprimir(grafo.formatarGrafo());
    }

    private void carregarExemploDirigido() {
        grafo = new Grafo(true);
        for (String id : new String[]{"A", "B", "C", "D", "E", "F"}) {
            grafo.inserirVertice(id);
        }
        grafo.inserirAresta("r1", "A", "B", 3);
        grafo.inserirAresta("r2", "B", "C", 2);
        grafo.inserirAresta("r3", "C", "A", 4);
        grafo.inserirAresta("r4", "C", "D", 7);
        grafo.inserirAresta("r5", "D", "E", 1);
        grafo.inserirAresta("r6", "E", "D", 5);
        grafo.inserirAresta("r7", "E", "F", 6);

        painelGrafo.setGrafo(grafo);
        areaSaida.setText("");
        imprimir("Exemplo dirigido carregado.");
        imprimir(grafo.formatarGrafo());
    }

    // ============================================================
    // OPERAÇÕES DO MENU: VÉRTICE E ARESTA
    // ============================================================

    private void inserirVertice() {
        String id = JOptionPane.showInputDialog(this,
                "Identificador do novo vértice:", "Inserir vértice isolado", JOptionPane.QUESTION_MESSAGE);
        if (id == null) {
            return;
        }
        try {
            grafo.inserirVertice(id.trim());
            atualizar();
            imprimir("Vértice inserido: " + id.trim());
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void removerVertice() {
        String id = escolherVertice("Remover vértice", "Vértice a remover (as ligações também são removidas):");
        if (id == null) {
            return;
        }
        try {
            grafo.removerVertice(id);
            atualizar();
            imprimir("Vértice removido: " + id);
            imprimir(grafo.formatarGrafo());
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void inserirAresta() {
        if (grafo.getVertices().isEmpty()) {
            avisar("Insira vértices antes de criar ligações.");
            return;
        }

        String id = JOptionPane.showInputDialog(this,
                "Identificador da aresta/arco:", "Inserir aresta/arco", JOptionPane.QUESTION_MESSAGE);
        if (id == null) {
            return;
        }

        String origem = escolherVertice("Inserir aresta/arco",
                grafo.isDirigido() ? "Vértice de origem (cauda):" : "Primeiro vértice:");
        if (origem == null) {
            return;
        }

        String destino = escolherVertice("Inserir aresta/arco",
                grafo.isDirigido() ? "Vértice de destino (cabeça):" : "Segundo vértice:");
        if (destino == null) {
            return;
        }

        Double peso = lerPeso("Peso (custo) da aresta/arco:");
        if (peso == null) {
            return;
        }

        try {
            grafo.inserirAresta(id.trim(), origem, destino, peso);
            atualizar();
            imprimir("Ligação inserida: " + grafo.buscarAresta(id.trim()));
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void removerAresta() {
        Aresta a = escolherAresta("Remover aresta/arco", "Ligação a remover:");
        if (a == null) {
            return;
        }
        try {
            grafo.removerAresta(a.getId());
            atualizar();
            imprimir("Ligação removida: " + a.getId());
            imprimir(grafo.formatarGrafo());
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void verificarAdjacencia() {
        String v = escolherVertice("Adjacência", "Primeiro vértice (v):");
        if (v == null) {
            return;
        }
        String w = escolherVertice("Adjacência", "Segundo vértice (w):");
        if (w == null) {
            return;
        }

        boolean adjacentes = grafo.saoAdjacentes(v, w);
        String sentido = grafo.isDirigido() ? " (existe arco de " + v + " para " + w + ")" : "";
        imprimir(v + " e " + w + " são adjacentes? " + (adjacentes ? "SIM" : "NÃO") + sentido);
    }

    private void valorAresta() {
        Aresta a = escolherAresta("Valor da aresta/arco", "Ligação:");
        if (a == null) {
            return;
        }
        imprimir("Peso de " + a.getId() + ": " + Aresta.formatarPeso(grafo.retornarValorAresta(a.getId())));
    }

    private void extremidadesAresta() {
        Aresta a = escolherAresta("Extremidades", "Ligação:");
        if (a == null) {
            return;
        }
        imprimir(grafo.formatarExtremidades(a.getId()));
    }

    // ============================================================
    // ALGORITMO DE PRIM
    // ============================================================

    private void executarPrim() {
        if (grafo.getVertices().isEmpty()) {
            avisar("O grafo não possui vértices.");
            return;
        }

        if (grafo.isDirigido()) {
            int opcao = JOptionPane.showConfirmDialog(this,
                    "O algoritmo de Prim é definido para grafo não dirigido.\n"
                            + "Aplicar sobre o grafo subjacente, ignorando o sentido dos arcos?",
                    "Grafo dirigido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcao != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String inicial = escolherVertice("Algoritmo de Prim", "Vértice inicial:");
        if (inicial == null) {
            return;
        }

        try {
            Grafo.ResultadoAGM resultado = grafo.prim(inicial);

            StringBuilder sb = new StringBuilder();
            sb.append("===== ALGORITMO DE PRIM: ÁRVORE GERADORA MÍNIMA =====\n");
            sb.append("Vértice inicial: ").append(resultado.getInicial().getId()).append('\n');

            if (resultado.getArestas().isEmpty()) {
                sb.append("Nenhuma aresta selecionada: não há ligações entre os vértices.\n");
            } else {
                sb.append("Arestas da AGM:\n");
                for (Aresta a : resultado.getArestas()) {
                    sb.append("  ").append(a.getId())
                            .append(": ").append(a.getOrigem().getId())
                            .append(" -- ").append(a.getDestino().getId())
                            .append("  peso ").append(a.getPesoFormatado()).append('\n');
                }
            }

            sb.append("Total de arestas: ").append(resultado.getArestas().size()).append('\n');
            sb.append("CUSTO TOTAL DA AGM: ").append(Aresta.formatarPeso(resultado.getCusto()));

            if (resultado.isFloresta()) {
                sb.append("\nAtenção: o grafo é desconexo (").append(resultado.getComponentes())
                        .append(" componentes). O resultado é uma floresta geradora mínima,\n")
                        .append("com uma árvore mínima por componente.");
            }

            imprimir(sb.toString());

            Set<String> ids = new LinkedHashSet<>();
            for (Aresta a : resultado.getArestas()) {
                ids.add(a.getId());
            }

            String rodape = "Custo total da AGM: " + Aresta.formatarPeso(resultado.getCusto())
                    + "   |   Arestas: " + resultado.getArestas().size()
                    + "   |   Vértice inicial: " + resultado.getInicial().getId()
                    + (resultado.isFloresta()
                    ? "   |   Floresta com " + resultado.getComponentes() + " árvores" : "");

            abrirJanelaResultado("Árvore Geradora Mínima (Prim)",
                    resultado.getArestas(), ids, null, rodape);

        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    // ============================================================
    // BUSCA EM PROFUNDIDADE GUIADA
    // ============================================================

    private void executarBusca() {
        if (grafo.getVertices().isEmpty()) {
            avisar("O grafo não possui vértices.");
            return;
        }

        String origem = escolherVertice("Busca em profundidade", "Vértice de saída:");
        if (origem == null) {
            return;
        }
        String destino = escolherVertice("Busca em profundidade", "Vértice de chegada:");
        if (destino == null) {
            return;
        }

        try {
            Grafo.ResultadoBusca resultado = grafo.buscaEmProfundidade(origem, destino);

            StringBuilder sb = new StringBuilder();
            sb.append("===== BUSCA EM PROFUNDIDADE GUIADA =====\n");
            sb.append("Vértice de saída: ").append(resultado.getOrigem().getId()).append('\n');
            sb.append("Vértice de chegada: ").append(resultado.getDestino().getId()).append('\n');

            sb.append("Ordem de visita: ");
            for (int i = 0; i < resultado.getOrdemVisita().size(); i++) {
                sb.append(i > 0 ? " > " : "").append(resultado.getOrdemVisita().get(i).getId());
            }
            sb.append('\n');

            sb.append("Arestas da árvore de busca:\n");
            if (resultado.getArestasArvore().isEmpty()) {
                sb.append("  (nenhuma)\n");
            } else {
                for (Aresta a : resultado.getArestasArvore()) {
                    sb.append("  ").append(a.getId())
                            .append(": ").append(a.getOrigem().getId())
                            .append(grafo.isDirigido() ? " -> " : " -- ")
                            .append(a.getDestino().getId())
                            .append("  peso ").append(a.getPesoFormatado()).append('\n');
                }
            }

            String rodape;
            if (resultado.isEncontrou()) {
                StringBuilder caminho = new StringBuilder();
                for (int i = 0; i < resultado.getCaminhoVertices().size(); i++) {
                    caminho.append(i > 0 ? " -> " : "").append(resultado.getCaminhoVertices().get(i).getId());
                }
                sb.append("CAMINHO ENCONTRADO: ").append(caminho).append('\n');
                sb.append("Custo do caminho: ").append(Aresta.formatarPeso(resultado.getCustoCaminho()));

                rodape = "Caminho: " + caminho
                        + "   |   Custo: " + Aresta.formatarPeso(resultado.getCustoCaminho())
                        + "   |   Vértices visitados: " + resultado.getOrdemVisita().size();
            } else {
                sb.append("CAMINHO NÃO ENCONTRADO: ")
                        .append(resultado.getDestino().getId())
                        .append(" não é alcançável a partir de ")
                        .append(resultado.getOrigem().getId()).append('.');

                rodape = "Vértice de chegada não alcançável a partir de "
                        + resultado.getOrigem().getId()
                        + "   |   Vértices visitados: " + resultado.getOrdemVisita().size();
            }

            imprimir(sb.toString());

            Set<String> destaques = new LinkedHashSet<>();
            for (Aresta a : resultado.getCaminhoArestas()) {
                destaques.add(a.getId());
            }

            Map<String, Color> cores = new HashMap<>();
            cores.put(resultado.getOrigem().getId(), new Color(0xA8, 0xE0, 0xB0));
            cores.put(resultado.getDestino().getId(), new Color(0xF5, 0xB4, 0xA8));

            abrirJanelaResultado("Árvore da busca em profundidade guiada",
                    resultado.getArestasArvore(), destaques, cores, rodape);

        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    // ============================================================
    // ALGORITMO DE ROY
    // ============================================================

    private void executarRoy() {
        if (grafo.getVertices().isEmpty()) {
            avisar("O grafo não possui vértices.");
            return;
        }

        List<List<Vertice>> componentes = grafo.royComponentes();

        String tipo = grafo.isDirigido()
                ? "COMPONENTES FORTEMENTE CONEXAS"
                : "COMPONENTES CONEXAS";

        StringBuilder sb = new StringBuilder();
        sb.append("===== ALGORITMO DE ROY: ").append(tipo).append(" =====\n");
        sb.append(grafo.formatarMatrizAlcancabilidade()).append('\n');
        sb.append("\nTotal de componentes: ").append(componentes.size()).append('\n');

        Map<String, Color> cores = new HashMap<>();
        for (int i = 0; i < componentes.size(); i++) {
            Color cor = PainelGrafo.PALETA_COMPONENTES[i % PainelGrafo.PALETA_COMPONENTES.length];

            sb.append("C").append(i + 1).append(" = { ");
            List<Vertice> componente = componentes.get(i);
            for (int j = 0; j < componente.size(); j++) {
                sb.append(j > 0 ? ", " : "").append(componente.get(j).getId());
                cores.put(componente.get(j).getId(), cor);
            }
            sb.append(" }\n");
        }

        if (componentes.size() == 1) {
            sb.append(grafo.isDirigido()
                    ? "O grafo é fortemente conexo."
                    : "O grafo é conexo.");
        } else {
            sb.append(grafo.isDirigido()
                    ? "O grafo não é fortemente conexo."
                    : "O grafo é desconexo.");
        }

        imprimir(sb.toString());

        painelGrafo.setCoresVertices(cores);

        String rodape = tipo.charAt(0) + tipo.substring(1).toLowerCase()
                + ": " + componentes.size()
                + "   |   cada cor representa um conjunto";

        abrirJanelaResultado("Roy: " + tipo.toLowerCase(),
                new ArrayList<>(grafo.getArestas()), null, cores, rodape);
    }

    // ============================================================
    // JANELA DE RESULTADO GRÁFICO
    // ============================================================

    private void abrirJanelaResultado(String titulo, List<Aresta> arestas,
                                      Set<String> destaques, Map<String, Color> cores,
                                      String rodape) {

        PainelGrafo painel = new PainelGrafo(grafo);
        painel.setPosicoes(painelGrafo.getPosicoes());
        painel.setArestasVisiveis(arestas);
        painel.setArestasDestacadas(destaques == null ? new HashSet<>() : destaques);
        painel.setCoresVertices(cores);
        painel.setPreferredSize(new Dimension(
                Math.max(painelGrafo.getWidth(), 780),
                Math.max(painelGrafo.getHeight(), 520)));

        JPanel conteudo = new JPanel(new BorderLayout(8, 8));
        conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel moldura = new JPanel(new BorderLayout());
        moldura.setBorder(BorderFactory.createTitledBorder(titulo));
        moldura.add(painel, BorderLayout.CENTER);

        JLabel info = new JLabel(rodape);
        info.setFont(info.getFont().deriveFont(Font.BOLD, 13f));
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8, 0xCE, 0xD4)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));

        conteudo.add(moldura, BorderLayout.CENTER);
        conteudo.add(info, BorderLayout.SOUTH);

        JDialog janela = new JDialog(this, titulo, false);
        janela.setContentPane(conteudo);
        janela.pack();
        janela.setLocationRelativeTo(this);
        janela.setVisible(true);
    }

    // ============================================================
    // APOIO
    // ============================================================

    private void atualizar() {
        painelGrafo.repaint();
        atualizarStatus();
    }

    private void atualizarStatus() {
        barraStatus.setText("Tipo: " + (grafo.isDirigido() ? "grafo dirigido" : "grafo não dirigido")
                + "     |     Vértices: " + grafo.getVertices().size()
                + "     |     " + (grafo.isDirigido() ? "Arcos: " : "Arestas: ") + grafo.getArestas().size()
                + "     |     Representação: lista de adjacência"
                + "     |     Dica: arraste os vértices para reposicionar");
    }

    private void imprimir(String texto) {
        areaSaida.append(texto + "\n\n");
        areaSaida.setCaretPosition(areaSaida.getDocument().getLength());
        atualizarStatus();
        painelGrafo.repaint();
    }

    private void avisar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Operação inválida", JOptionPane.ERROR_MESSAGE);
        imprimir("ERRO: " + ex.getMessage());
    }

    private String escolherVertice(String titulo, String mensagem) {
        List<Vertice> lista = grafo.getVertices();
        if (lista.isEmpty()) {
            avisar("O grafo não possui vértices.");
            return null;
        }
        String[] ids = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            ids[i] = lista.get(i).getId();
        }
        return (String) JOptionPane.showInputDialog(this, mensagem, titulo,
                JOptionPane.QUESTION_MESSAGE, null, ids, ids[0]);
    }

    private Aresta escolherAresta(String titulo, String mensagem) {
        List<Aresta> lista = grafo.getArestas();
        if (lista.isEmpty()) {
            avisar("O grafo não possui arestas/arcos.");
            return null;
        }
        ItemAresta[] itens = new ItemAresta[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            itens[i] = new ItemAresta(lista.get(i), grafo.isDirigido());
        }
        ItemAresta escolhido = (ItemAresta) JOptionPane.showInputDialog(this, mensagem, titulo,
                JOptionPane.QUESTION_MESSAGE, null, itens, itens[0]);
        return (escolhido == null) ? null : escolhido.aresta;
    }

    private Double lerPeso(String mensagem) {
        String texto = JOptionPane.showInputDialog(this, mensagem, "Peso", JOptionPane.QUESTION_MESSAGE);
        if (texto == null) {
            return null;
        }
        try {
            return Double.valueOf(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            avisar("Valor numérico inválido: " + texto);
            return null;
        }
    }

    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this,
                "Teoria dos Grafos - Trabalho M1\n\n"
                        + "Kauan Rocha Dalfovo\n"
                        + "Gustavo Odilon\n\n"
                        + "Grafo dirigido ou não dirigido representado por lista de adjacência.\n"
                        + "Operações: inserir e remover vértice, inserir e remover aresta/arco,\n"
                        + "adjacência, peso, extremidades, matriz de adjacência, matriz de incidência,\n"
                        + "Prim, busca em profundidade guiada e algoritmo de Roy.",
                "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Item exibido na lista de seleção de arestas. */
    private static class ItemAresta {

        private final Aresta aresta;
        private final boolean dirigido;

        ItemAresta(Aresta aresta, boolean dirigido) {
            this.aresta = aresta;
            this.dirigido = dirigido;
        }

        @Override
        public String toString() {
            return aresta.getId() + "  (" + aresta.getOrigem().getId()
                    + (dirigido ? " -> " : " -- ") + aresta.getDestino().getId()
                    + ", peso " + aresta.getPesoFormatado() + ")";
        }
    }
}
