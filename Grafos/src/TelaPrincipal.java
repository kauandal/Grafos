import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Janela principal: menu com todas as operações, desenho do grafo ativo
 * e área de texto com o resultado de cada operação.
 */
public class TelaPrincipal extends JFrame {

    private Grafo grafo = new Grafo(false);
    private final PainelGrafo painel = new PainelGrafo(grafo);
    private final JTextArea saida = new JTextArea();
    private final JLabel status = new JLabel();

    public TelaPrincipal() {
        super("Teoria dos Grafos - Kauan Rocha Dalfovo e Gustavo Odilon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(montarMenu());
        montarTela();
        carregarExemplo(false);
        setSize(1000, 720);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        saida.setEditable(false);
        saida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane rolagem = new JScrollPane(saida);
        rolagem.setBorder(BorderFactory.createTitledBorder("Saída das operações"));
        rolagem.setPreferredSize(new Dimension(980, 210));

        JPanel desenho = new JPanel(new BorderLayout());
        desenho.setBorder(BorderFactory.createTitledBorder("Grafo ativo"));
        desenho.add(painel, BorderLayout.CENTER);

        status.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JPanel conteudo = new JPanel(new BorderLayout(8, 8));
        conteudo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        conteudo.add(desenho, BorderLayout.CENTER);
        conteudo.add(rolagem, BorderLayout.SOUTH);
        conteudo.add(status, BorderLayout.NORTH);
        setContentPane(conteudo);
    }

    private JMenuBar montarMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu grafoMenu = new JMenu("Grafo");
        grafoMenu.add(item("Novo grafo não dirigido", e -> novo(false)));
        grafoMenu.add(item("Novo grafo dirigido", e -> novo(true)));
        grafoMenu.add(item("Carregar exemplo não dirigido", e -> carregarExemplo(false)));
        grafoMenu.add(item("Carregar exemplo dirigido", e -> carregarExemplo(true)));
        grafoMenu.add(item("Carregar mapa do Paraná", e -> carregarMapaParana()));
        grafoMenu.addSeparator();
        grafoMenu.add(item("Carregar grafo de arquivo", e -> carregarDeArquivo()));
        grafoMenu.add(item("Salvar grafo em arquivo", e -> salvarEmArquivo()));
        grafoMenu.addSeparator();
        grafoMenu.add(item("Lista de adjacência", e -> imprimir(grafo.formatarGrafo())));
        grafoMenu.add(item("Sair", e -> dispose()));

        JMenu op = new JMenu("Operações");
        op.add(item("Inserir vértice isolado", e -> inserirVertice()));
        op.add(item("Inserir aresta/arco", e -> inserirAresta()));
        op.add(item("Remover vértice", e -> removerVertice()));
        op.add(item("Remover aresta/arco", e -> removerAresta()));
        op.addSeparator();
        op.add(item("Verificar adjacência", e -> adjacencia()));
        op.add(item("Peso da aresta/arco", e -> peso()));
        op.add(item("Extremidades da aresta/arco", e -> extremidades()));

        JMenu alg = new JMenu("Algoritmos");
        alg.add(item("Prim: árvore geradora mínima", e -> prim()));
        alg.add(item("Busca em profundidade guiada", e -> busca()));
        alg.add(item("Roy: componentes conexas", e -> roy()));
        alg.addSeparator();
        alg.add(item("DSATUR: coloração do grafo", e -> dsatur()));
        alg.add(item("A*: caminho mínimo", e -> aEstrela()));

        JMenu exibir = new JMenu("Exibição");
        exibir.add(item("Limpar destaques", e -> painel.limpar()));
        exibir.add(item("Limpar saída", e -> saida.setText("")));

        barra.add(grafoMenu);
        barra.add(op);
        barra.add(alg);
        barra.add(exibir);
        return barra;
    }

    private JMenuItem item(String texto, ActionListener acao) {
        JMenuItem mi = new JMenuItem(texto);
        mi.addActionListener(acao);
        return mi;
    }

    // ============================================================
    // GRAFO
    // ============================================================

    private void novo(boolean dirigido) {
        grafo = new Grafo(dirigido);
        painel.mostrar(grafo);
        saida.setText("");
        imprimir("Novo grafo " + (dirigido ? "dirigido" : "não dirigido") + " criado.");
    }

    private void carregarExemplo(boolean dirigido) {
        grafo = new Grafo(dirigido);
        for (String id : new String[]{"A", "B", "C", "D", "E", "F"}) {
            grafo.inserirVertice(id);
        }
        if (dirigido) {
            grafo.inserirAresta("r1", "A", "B", 3);
            grafo.inserirAresta("r2", "B", "C", 2);
            grafo.inserirAresta("r3", "C", "A", 4);
            grafo.inserirAresta("r4", "C", "D", 7);
            grafo.inserirAresta("r5", "D", "E", 1);
            grafo.inserirAresta("r6", "E", "D", 5);
            grafo.inserirAresta("r7", "E", "F", 6);
        } else {
            grafo.inserirAresta("a1", "A", "B", 4);
            grafo.inserirAresta("a2", "A", "C", 2);
            grafo.inserirAresta("a3", "B", "C", 1);
            grafo.inserirAresta("a4", "B", "D", 5);
            grafo.inserirAresta("a5", "C", "D", 8);
            grafo.inserirAresta("a6", "C", "E", 10);
            grafo.inserirAresta("a7", "D", "E", 2);
            grafo.inserirAresta("a8", "D", "F", 6);
            grafo.inserirAresta("a9", "E", "F", 3);
        }
        painel.mostrar(grafo);
        saida.setText("");
        imprimir(grafo.formatarGrafo());
    }

    /**
     Mapa rodoviário do Paraná usado para testar o A*. Os pesos das arestas
     são a distância por estrada, em quilômetros. As coordenadas geográficas
     ficam nos vértices e alimentam a heurística de Haversine.
     */
    private void carregarMapaParana() {
        grafo = new Grafo(false);

        // Coordenadas geográficas das sedes municipais, conforme o IBGE.
        grafo.inserirVertice("Curitiba", -25.4133, -49.2679);
        grafo.inserirVertice("Paranaguá", -25.5189, -48.5091);
        grafo.inserirVertice("Ponta Grossa", -25.0964, -50.1722);
        grafo.inserirVertice("São Mateus do Sul", -25.8732, -50.3855);
        grafo.inserirVertice("Guarapuava", -25.3863, -51.4699);
        grafo.inserirVertice("Londrina", -23.3355, -51.1506);
        grafo.inserirVertice("Maringá", -23.4240, -51.9395);
        grafo.inserirVertice("Umuarama", -23.7595, -53.3118);
        grafo.inserirVertice("Toledo", -24.7227, -53.7403);
        grafo.inserirVertice("Cascavel", -24.9530, -53.4796);
        grafo.inserirVertice("Foz do Iguaçu", -25.5468, -54.5884);
        grafo.inserirVertice("Francisco Beltrão", -26.0802, -53.0554);

        grafo.inserirAresta("e1", "Umuarama", "Maringá", 190);
        grafo.inserirAresta("e2", "Umuarama", "Toledo", 126);
        grafo.inserirAresta("e3", "Maringá", "Londrina", 114);
        grafo.inserirAresta("e4", "Maringá", "Ponta Grossa", 314);
        grafo.inserirAresta("e5", "Londrina", "Ponta Grossa", 273);
        grafo.inserirAresta("e6", "Toledo", "Cascavel", 50);
        grafo.inserirAresta("e7", "Cascavel", "Foz do Iguaçu", 143);
        grafo.inserirAresta("e8", "Cascavel", "Francisco Beltrão", 186);
        grafo.inserirAresta("e9", "Cascavel", "Guarapuava", 250);
        grafo.inserirAresta("e10", "Guarapuava", "Ponta Grossa", 165);
        grafo.inserirAresta("e11", "Francisco Beltrão", "São Mateus do Sul", 354);
        grafo.inserirAresta("e12", "São Mateus do Sul", "Curitiba", 157);
        grafo.inserirAresta("e13", "Ponta Grossa", "Curitiba", 114);
        grafo.inserirAresta("e14", "Curitiba", "Paranaguá", 90);

        painel.mostrar(grafo);
        saida.setText("");
        imprimir("Mapa do Paraná carregado: 12 cidades e 14 estradas.\n"
                + "Pesos em quilômetros por estrada. Coordenadas geográficas nos vértices.");
        imprimir(grafo.formatarGrafo());
    }

    private void carregarDeArquivo() {
        JFileChooser seletor = new JFileChooser(new File("."));
        seletor.setDialogTitle("Carregar grafo de arquivo");

        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = seletor.getSelectedFile();
        try {
            grafo = ArquivoGrafo.carregar(arquivo);
            painel.mostrar(grafo);
            saida.setText("");
            imprimir("Grafo carregado de " + arquivo.getName() + ".");
            imprimir(grafo.formatarGrafo());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro ao carregar o arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarEmArquivo() {
        if (grafo.getVertices().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O grafo não possui vértices.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser seletor = new JFileChooser(new File("."));
        seletor.setDialogTitle("Salvar grafo em arquivo");
        seletor.setSelectedFile(new File("grafo.txt"));

        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = seletor.getSelectedFile();
        try {
            ArquivoGrafo.salvar(grafo, arquivo);
            imprimir("Grafo gravado em " + arquivo.getName() + ".");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro ao gravar o arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // OPERAÇÕES
    // ============================================================

    private void inserirVertice() {
        String id = perguntar("Identificador do novo vértice:");
        if (id == null) {
            return;
        }
        try {
            grafo.inserirVertice(id);
            imprimir("Vértice inserido: " + id);
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void inserirAresta() {
        String id = perguntar("Identificador da aresta/arco:");
        if (id == null) {
            return;
        }
        String origem = perguntar(grafo.isDirigido() ? "Vértice de origem:" : "Primeiro vértice:");
        if (origem == null) {
            return;
        }
        String destino = perguntar(grafo.isDirigido() ? "Vértice de destino:" : "Segundo vértice:");
        if (destino == null) {
            return;
        }
        String texto = perguntar("Peso (custo):");
        if (texto == null) {
            return;
        }
        try {
            grafo.inserirAresta(id, origem, destino, Double.parseDouble(texto.replace(',', '.')));
            imprimir("Ligação inserida: " + grafo.buscarAresta(id));
        } catch (NumberFormatException ex) {
            erro(new IllegalArgumentException("Peso inválido: " + texto));
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void removerVertice() {
        String id = perguntar("Vértice a remover:");
        if (id == null) {
            return;
        }
        try {
            grafo.removerVertice(id);
            imprimir("Vértice removido: " + id + "\n" + grafo.formatarGrafo());
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void removerAresta() {
        String id = perguntar("Aresta/arco a remover:");
        if (id == null) {
            return;
        }
        try {
            grafo.removerAresta(id);
            imprimir("Ligação removida: " + id + "\n" + grafo.formatarGrafo());
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void adjacencia() {
        String v = perguntar("Primeiro vértice (v):");
        if (v == null) {
            return;
        }
        String w = perguntar("Segundo vértice (w):");
        if (w == null) {
            return;
        }
        imprimir(v + " e " + w + " são adjacentes? " + (grafo.saoAdjacentes(v, w) ? "SIM" : "NÃO"));
    }

    private void peso() {
        String id = perguntar("Identificador da aresta/arco:");
        if (id == null) {
            return;
        }
        try {
            imprimir("Peso de " + id + ": " + Aresta.formatarPeso(grafo.retornarValorAresta(id)));
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void extremidades() {
        String id = perguntar("Identificador da aresta/arco:");
        if (id == null) {
            return;
        }
        try {
            imprimir(grafo.formatarExtremidades(id));
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    // ============================================================
    // ALGORITMOS
    // ============================================================

    private void prim() {
        try {
            // Não há vértice a escolher: a árvore geradora mínima é propriedade
            // do grafo e o custo total não depende de onde a construção começa.
            Grafo.ResultadoAGM agm = grafo.prim();

            String texto = "===== PRIM: ÁRVORE GERADORA MÍNIMA =====\n";
            for (Aresta a : agm.getArestas()) {
                texto += "  " + a.getId() + ": " + a.getOrigem().getId()
                        + " -- " + a.getDestino().getId()
                        + "  peso " + a.getPesoFormatado() + "\n";
            }
            texto += "Arestas: " + agm.getArestas().size()
                    + "   CUSTO TOTAL: " + Aresta.formatarPeso(agm.getCusto());
            if (agm.isFloresta()) {
                texto += "\nGrafo desconexo: floresta com " + agm.getComponentes() + " árvores.";
            }
            imprimir(texto);

            painel.realcar(agm.getArestas(), idsDe(agm.getArestas()), null);
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void busca() {
        String origem = perguntar("Vértice de saída:");
        if (origem == null) {
            return;
        }
        String destino = perguntar("Vértice de chegada:");
        if (destino == null) {
            return;
        }
        try {
            Grafo.ResultadoBusca b = grafo.buscaEmProfundidade(origem, destino);

            String texto = "===== BUSCA EM PROFUNDIDADE GUIADA =====\n";
            texto += "Saída: " + b.getOrigem().getId()
                    + "   Chegada: " + b.getDestino().getId() + "\n";
            texto += "Ordem de visita: " + b.getOrdemVisita() + "\n";
            texto += "Arestas da árvore:\n";
            for (Aresta a : b.getArestasArvore()) {
                texto += "  " + a.getId() + ": " + a.getOrigem().getId()
                        + (grafo.isDirigido() ? " -> " : " -- ") + a.getDestino().getId()
                        + "  peso " + a.getPesoFormatado() + "\n";
            }
            if (b.isEncontrou()) {
                texto += "Caminho: " + b.getCaminhoVertices()
                        + "   custo " + Aresta.formatarPeso(b.getCustoCaminho());
            } else {
                texto += b.getDestino().getId() + " não é alcançável a partir de "
                        + b.getOrigem().getId() + ".";
            }
            imprimir(texto);

            Map<String, Color> cores = new HashMap<>();
            cores.put(b.getOrigem().getId(), new Color(0xA8, 0xE0, 0xB0));
            cores.put(b.getDestino().getId(), new Color(0xF5, 0xB4, 0xA8));
            painel.realcar(b.getArestasArvore(), idsDe(b.getCaminhoArestas()), cores);
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void dsatur() {
        try {
            Grafo.ResultadoColoracao r = grafo.dsatur();

            String texto = "===== DSATUR: COLORAÇÃO DO GRAFO =====\n";
            texto += "Cores utilizadas: " + r.getTotalCores() + "\n\n";

            for (Vertice v : grafo.getVertices()) {
                texto += String.format("  %-20s cor %d%n", v.getId(), r.getCores().get(v));
            }

            texto += "\nColoração própria, nenhuma aresta com as duas pontas da mesma cor: "
                    + (grafo.coloracaoValida(r.getCores()) ? "SIM" : "NÃO");
            imprimir(texto);

            Map<String, Color> cores = new HashMap<>();
            for (Map.Entry<Vertice, Integer> e : r.getCores().entrySet()) {
                cores.put(e.getKey().getId(), PainelGrafo.corDaClasse(e.getValue()));
            }
            painel.realcar(null, null, cores);
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void aEstrela() {
        String origem = perguntar("Cidade de origem:");
        if (origem == null) {
            return;
        }
        String destino = perguntar("Cidade de destino:");
        if (destino == null) {
            return;
        }
        try {
            Grafo.ResultadoCaminho r = grafo.aEstrela(origem, destino);

            String texto = "===== A*: CAMINHO MÍNIMO =====\n";
            texto += "Origem: " + r.getOrigem().getId()
                    + "   Destino: " + r.getDestino().getId() + "\n\n";

            // O enunciado exige a tabela h(n) na tela, montada em relação ao
            // destino escolhido e calculada pela fórmula de Haversine.
            texto += "h(n) em linha reta até " + r.getDestino().getId()
                    + ", pela fórmula de Haversine:\n";
            for (Map.Entry<Vertice, Double> e : grafo.heuristicas(destino).entrySet()) {
                texto += String.format("  %-20s %8.1f km%n", e.getKey().getId(), e.getValue());
            }

            texto += "\nOrdem de expansão: " + r.getOrdemExpansao() + "\n";

            if (r.isEncontrou()) {
                texto += "CAMINHO: " + r.getCaminhoVertices() + "\n";
                texto += "Arestas percorridas:\n";
                for (Aresta a : r.getCaminhoArestas()) {
                    texto += "  " + a.getId() + ": " + a.getOrigem().getId()
                            + (grafo.isDirigido() ? " -> " : " -- ") + a.getDestino().getId()
                            + "  " + a.getPesoFormatado() + " km\n";
                }
                texto += "CUSTO TOTAL: " + Aresta.formatarPeso(r.getCustoTotal()) + " km";
            } else {
                texto += r.getDestino().getId() + " não é alcançável a partir de "
                        + r.getOrigem().getId() + ".";
            }
            imprimir(texto);

            Map<String, Color> cores = new HashMap<>();
            cores.put(r.getOrigem().getId(), new Color(0xA8, 0xE0, 0xB0));
            cores.put(r.getDestino().getId(), new Color(0xF5, 0xB4, 0xA8));
            painel.realcar(null, idsDe(r.getCaminhoArestas()), cores);
        } catch (RuntimeException ex) {
            erro(ex);
        }
    }

    private void roy() {
        List<List<Vertice>> componentes = grafo.royComponentes();

        String texto = "===== ROY: COMPONENTES "
                + (grafo.isDirigido() ? "FORTEMENTE CONEXAS" : "CONEXAS") + " =====\n";

        Map<String, Color> cores = new HashMap<>();
        for (int i = 0; i < componentes.size(); i++) {
            texto += "C" + (i + 1) + " = " + componentes.get(i) + "\n";
            for (Vertice v : componentes.get(i)) {
                cores.put(v.getId(), PainelGrafo.PALETA[i % PainelGrafo.PALETA.length]);
            }
        }
        texto += "Total de componentes: " + componentes.size();
        imprimir(texto);

        painel.realcar(null, null, cores);
    }

    // ============================================================
    // APOIO
    // ============================================================

    private Set<String> idsDe(List<Aresta> arestas) {
        Set<String> ids = new HashSet<>();
        for (Aresta a : arestas) {
            ids.add(a.getId());
        }
        return ids;
    }

    private String perguntar(String mensagem) {
        String texto = JOptionPane.showInputDialog(this, mensagem);
        return (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
    }

    private void imprimir(String texto) {
        saida.append(texto + "\n\n");
        saida.setCaretPosition(saida.getDocument().getLength());
        status.setText("Tipo: " + (grafo.isDirigido() ? "dirigido" : "não dirigido")
                + "     Vértices: " + grafo.getVertices().size()
                + "     Ligações: " + grafo.getArestas().size()
                + "     Representação: lista de adjacência");
        painel.repaint();
    }

    private void erro(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
