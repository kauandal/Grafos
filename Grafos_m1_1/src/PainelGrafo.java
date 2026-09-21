import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Desenho do grafo: vértices como círculos rotulados e arestas/arcos como
 * linhas com identificador e peso. Em grafo dirigido o arco recebe seta.
 *
 * O painel serve tanto para o grafo ativo quanto para exibir resultados de
 * algoritmos, bastando limitar as arestas visíveis e destacar um subconjunto.
 * Os vértices podem ser arrastados com o mouse.
 */
public class PainelGrafo extends JPanel {

    private static final int RAIO = 22;

    private static final Color COR_VERTICE = new Color(0xDC, 0xE9, 0xF7);
    private static final Color COR_BORDA_VERTICE = new Color(0x1F, 0x4E, 0x79);
    private static final Color COR_ARESTA = new Color(0x5A, 0x65, 0x70);
    private static final Color COR_DESTAQUE = new Color(0xC0, 0x39, 0x2B);
    private static final Color COR_TEXTO_ARESTA = new Color(0x2C, 0x3E, 0x50);

    private static final Font FONTE_VERTICE = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONTE_ROTULO = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONTE_AVISO = new Font("SansSerif", Font.ITALIC, 13);

    /** Paleta usada para colorir componentes conexas/fortemente conexas. */
    public static final Color[] PALETA_COMPONENTES = {
            new Color(0xBF, 0xE3, 0xC6), new Color(0xFB, 0xDF, 0xB5),
            new Color(0xC9, 0xD8, 0xF5), new Color(0xF2, 0xC6, 0xD1),
            new Color(0xDD, 0xD0, 0xF0), new Color(0xC6, 0xEC, 0xEE),
            new Color(0xEE, 0xE2, 0xBC), new Color(0xD8, 0xE8, 0xB8)
    };

    private Grafo grafo;

    /** Quando nulo, todas as arestas do grafo são desenhadas. */
    private List<Aresta> arestasVisiveis;

    private final Map<String, Point2D.Double> posicoes = new HashMap<>();
    private Set<String> arestasDestacadas = new HashSet<>();
    private Map<String, Color> coresVertices = new HashMap<>();

    private String arrastando;

    public PainelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(780, 520));
        habilitarArraste();
    }

    // ============================================================
    // CONFIGURAÇÃO
    // ============================================================

    public void setGrafo(Grafo grafo) {
        this.grafo = grafo;
        this.arestasVisiveis = null;
        this.arestasDestacadas = new HashSet<>();
        this.coresVertices = new HashMap<>();
        posicoes.clear();
        repaint();
    }

    public void setArestasVisiveis(List<Aresta> arestas) {
        this.arestasVisiveis = arestas;
        repaint();
    }

    public void setArestasDestacadas(Set<String> ids) {
        this.arestasDestacadas = (ids == null) ? new HashSet<>() : new HashSet<>(ids);
        repaint();
    }

    public void setCoresVertices(Map<String, Color> cores) {
        this.coresVertices = (cores == null) ? new HashMap<>() : new HashMap<>(cores);
        repaint();
    }

    public void limparDestaques() {
        arestasDestacadas.clear();
        coresVertices.clear();
        arestasVisiveis = null;
        repaint();
    }

    public Map<String, Point2D.Double> getPosicoes() {
        Map<String, Point2D.Double> copia = new LinkedHashMap<>();
        for (Map.Entry<String, Point2D.Double> e : posicoes.entrySet()) {
            copia.put(e.getKey(), new Point2D.Double(e.getValue().x, e.getValue().y));
        }
        return copia;
    }

    public void setPosicoes(Map<String, Point2D.Double> novas) {
        posicoes.clear();
        if (novas != null) {
            for (Map.Entry<String, Point2D.Double> e : novas.entrySet()) {
                posicoes.put(e.getKey(), new Point2D.Double(e.getValue().x, e.getValue().y));
            }
        }
        repaint();
    }

    /** Redistribui todos os vértices em círculo. */
    public void layoutCircular() {
        posicoes.clear();
        repaint();
    }

    // ============================================================
    // POSICIONAMENTO
    // ============================================================

    private void garantirPosicoes() {
        List<Vertice> lista = grafo.getVertices();

        Set<String> atuais = new HashSet<>();
        for (Vertice v : lista) {
            atuais.add(v.getId());
        }
        posicoes.keySet().retainAll(atuais);

        int n = lista.size();
        if (n == 0) {
            return;
        }

        double largura = Math.max(getWidth(), 420);
        double altura = Math.max(getHeight(), 320);
        double cx = largura / 2.0;
        double cy = altura / 2.0;
        double raio = Math.max(90.0, Math.min(largura, altura) / 2.0 - 75.0);

        for (int i = 0; i < n; i++) {
            Vertice v = lista.get(i);
            if (posicoes.containsKey(v.getId())) {
                continue;
            }
            if (n == 1) {
                posicoes.put(v.getId(), new Point2D.Double(cx, cy));
            } else {
                double angulo = -Math.PI / 2 + (2 * Math.PI * i) / n;
                posicoes.put(v.getId(),
                        new Point2D.Double(cx + raio * Math.cos(angulo), cy + raio * Math.sin(angulo)));
            }
        }
    }

    private void habilitarArraste() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                arrastando = verticeEm(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                arrastando = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastando != null) {
                    posicoes.put(arrastando, new Point2D.Double(e.getX(), e.getY()));
                    repaint();
                }
            }
        });
    }

    private String verticeEm(Point ponto) {
        for (Map.Entry<String, Point2D.Double> e : posicoes.entrySet()) {
            if (e.getValue().distance(ponto.x, ponto.y) <= RAIO) {
                return e.getKey();
            }
        }
        return null;
    }

    // ============================================================
    // DESENHO
    // ============================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (grafo == null || grafo.getVertices().isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(FONTE_AVISO);
            g2.drawString("Grafo sem vértices. Use o menu Vértice para inserir.", 24, 34);
            g2.dispose();
            return;
        }

        garantirPosicoes();

        List<Aresta> lista = (arestasVisiveis != null) ? arestasVisiveis : grafo.getArestas();

        // Arestas entre o mesmo par são curvadas em leque para não se sobrepor.
        Map<String, List<Aresta>> grupos = new LinkedHashMap<>();
        for (Aresta a : lista) {
            grupos.computeIfAbsent(chavePar(a), k -> new ArrayList<>()).add(a);
        }

        for (List<Aresta> grupo : grupos.values()) {
            int total = grupo.size();
            for (int i = 0; i < total; i++) {
                Aresta a = grupo.get(i);
                double deslocamento = (i - (total - 1) / 2.0) * 64.0;

                // A perpendicular usada na curvatura segue o sentido origem -> destino.
                // Arestas do mesmo par em sentido invertido precisam do deslocamento
                // espelhado, senão as duas curvas caem uma sobre a outra.
                if (a.getOrigem().getId().compareTo(a.getDestino().getId()) > 0) {
                    deslocamento = -deslocamento;
                }

                desenharAresta(g2, a, deslocamento);
            }
        }

        for (Vertice v : grafo.getVertices()) {
            desenharVertice(g2, v);
        }

        g2.dispose();
    }

    private String chavePar(Aresta a) {
        String x = a.getOrigem().getId();
        String y = a.getDestino().getId();
        String menor = (x.compareTo(y) <= 0) ? x : y;
        String maior = (x.compareTo(y) <= 0) ? y : x;
        return menor.length() + ":" + menor + ":" + maior;
    }

    private void desenharAresta(Graphics2D g2, Aresta a, double deslocamento) {
        Point2D.Double p1 = posicoes.get(a.getOrigem().getId());
        Point2D.Double p2 = posicoes.get(a.getDestino().getId());

        if (p1 == null || p2 == null) {
            return;
        }

        boolean destaque = arestasDestacadas.contains(a.getId());
        g2.setColor(destaque ? COR_DESTAQUE : COR_ARESTA);
        g2.setStroke(new BasicStroke(destaque ? 3.2f : 1.6f));

        if (a.isLaco()) {
            desenharLaco(g2, a, p1, destaque);
            return;
        }

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double comprimento = Math.hypot(dx, dy);
        if (comprimento < 1e-6) {
            return;
        }

        // Ponto de controle deslocado na perpendicular gera a curvatura.
        double cx = (p1.x + p2.x) / 2.0 - (dy / comprimento) * deslocamento;
        double cy = (p1.y + p2.y) / 2.0 + (dx / comprimento) * deslocamento;

        List<Point2D.Double> pontos = amostrar(new QuadCurve2D.Double(p1.x, p1.y, cx, cy, p2.x, p2.y), 48);

        int inicio = 0;
        while (inicio < pontos.size() - 1 && pontos.get(inicio).distance(p1) < RAIO) {
            inicio++;
        }
        int fim = pontos.size() - 1;
        while (fim > inicio && pontos.get(fim).distance(p2) < RAIO) {
            fim--;
        }

        Path2D.Double caminho = new Path2D.Double();
        caminho.moveTo(pontos.get(inicio).x, pontos.get(inicio).y);
        for (int k = inicio + 1; k <= fim; k++) {
            caminho.lineTo(pontos.get(k).x, pontos.get(k).y);
        }
        g2.draw(caminho);

        if (grafo.isDirigido() && fim > inicio) {
            desenharSeta(g2, pontos.get(fim - 1), pontos.get(fim));
        }

        Point2D.Double meio = pontos.get((inicio + fim) / 2);
        desenharRotulo(g2, rotulo(a), meio.x, meio.y, destaque);
    }

    private void desenharLaco(Graphics2D g2, Aresta a, Point2D.Double p, boolean destaque) {
        double largura = 40;
        double altura = 34;
        double topo = p.y - RAIO - altura + 8;

        g2.draw(new Ellipse2D.Double(p.x - largura / 2, topo, largura, altura));

        if (grafo.isDirigido()) {
            Point2D.Double de = new Point2D.Double(p.x + largura / 2 - 6, topo + altura - 10);
            Point2D.Double ate = new Point2D.Double(p.x + 6, p.y - RAIO + 2);
            desenharSeta(g2, de, ate);
        }

        desenharRotulo(g2, rotulo(a), p.x, topo - 8, destaque);
    }

    private void desenharSeta(Graphics2D g2, Point2D.Double de, Point2D.Double ate) {
        double angulo = Math.atan2(ate.y - de.y, ate.x - de.x);
        double tamanho = 13;
        double abertura = Math.toRadians(22);

        Path2D.Double seta = new Path2D.Double();
        seta.moveTo(ate.x, ate.y);
        seta.lineTo(ate.x - tamanho * Math.cos(angulo - abertura), ate.y - tamanho * Math.sin(angulo - abertura));
        seta.lineTo(ate.x - tamanho * Math.cos(angulo + abertura), ate.y - tamanho * Math.sin(angulo + abertura));
        seta.closePath();
        g2.fill(seta);
    }

    private void desenharRotulo(Graphics2D g2, String texto, double x, double y, boolean destaque) {
        Font fonteAnterior = g2.getFont();
        Color corAnterior = g2.getColor();

        g2.setFont(FONTE_ROTULO);
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto);
        int altura = fm.getAscent();

        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect((int) x - largura / 2 - 4, (int) y - altura / 2 - 4, largura + 8, altura + 8, 8, 8);

        g2.setColor(destaque ? COR_DESTAQUE : COR_TEXTO_ARESTA);
        g2.drawString(texto, (int) x - largura / 2, (int) y + altura / 2 - 1);

        g2.setFont(fonteAnterior);
        g2.setColor(corAnterior);
    }

    private void desenharVertice(Graphics2D g2, Vertice v) {
        Point2D.Double p = posicoes.get(v.getId());
        if (p == null) {
            return;
        }

        Color preenchimento = coresVertices.getOrDefault(v.getId(), COR_VERTICE);
        Ellipse2D.Double circulo = new Ellipse2D.Double(p.x - RAIO, p.y - RAIO, RAIO * 2, RAIO * 2);

        g2.setColor(preenchimento);
        g2.fill(circulo);

        g2.setColor(COR_BORDA_VERTICE);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(circulo);

        g2.setFont(FONTE_VERTICE);
        FontMetrics fm = g2.getFontMetrics();
        String id = v.getId();
        int largura = fm.stringWidth(id);

        g2.setColor(contraste(preenchimento));
        g2.drawString(id, (int) (p.x - largura / 2.0), (int) (p.y + fm.getAscent() / 2.0 - 2));
    }

    private Color contraste(Color fundo) {
        double luminancia = 0.299 * fundo.getRed() + 0.587 * fundo.getGreen() + 0.114 * fundo.getBlue();
        return (luminancia < 140) ? Color.WHITE : new Color(0x1B, 0x2A, 0x38);
    }

    private String rotulo(Aresta a) {
        return a.getId() + " (" + a.getPesoFormatado() + ")";
    }

    /** Converte a curva em uma sequência de pontos, o que facilita recortar e apontar a seta. */
    private List<Point2D.Double> amostrar(QuadCurve2D.Double curva, int divisoes) {
        List<Point2D.Double> pontos = new ArrayList<>(divisoes + 1);
        for (int i = 0; i <= divisoes; i++) {
            double t = i / (double) divisoes;
            double u = 1 - t;
            double x = u * u * curva.x1 + 2 * u * t * curva.ctrlx + t * t * curva.x2;
            double y = u * u * curva.y1 + 2 * u * t * curva.ctrly + t * t * curva.y2;
            pontos.add(new Point2D.Double(x, y));
        }
        return pontos;
    }
}
