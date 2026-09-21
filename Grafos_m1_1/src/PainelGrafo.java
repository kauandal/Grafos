import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Desenho do grafo: vértices em círculo e ligações como retas com o
 * identificador e o peso. Em grafo dirigido o arco recebe seta.
 *
 * O mesmo painel mostra o grafo ativo e o resultado dos algoritmos: basta
 * limitar as arestas desenhadas e destacar um subconjunto.
 */
public class PainelGrafo extends JPanel {

    private static final int RAIO = 20;
    private static final Color COR_VERTICE = new Color(0xDC, 0xE9, 0xF7);
    private static final Color COR_BORDA = new Color(0x1F, 0x4E, 0x79);
    private static final Color COR_DESTAQUE = new Color(0xC0, 0x39, 0x2B);

    /** Cores usadas para separar as componentes no algoritmo de Roy. */
    public static final Color[] PALETA = {
            new Color(0xBF, 0xE3, 0xC6), new Color(0xFB, 0xDF, 0xB5),
            new Color(0xC9, 0xD8, 0xF5), new Color(0xF2, 0xC6, 0xD1),
            new Color(0xDD, 0xD0, 0xF0), new Color(0xC6, 0xEC, 0xEE)
    };

    private Grafo grafo;
    private List<Aresta> arestasVisiveis;
    private Set<String> destacadas = new HashSet<>();
    private Map<String, Color> coresVertices = new HashMap<>();

    private final Map<String, Point> posicoes = new HashMap<>();

    public PainelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(760, 470));
    }

    /** Troca o grafo desenhado e apaga qualquer realce anterior. */
    public void mostrar(Grafo grafo) {
        this.grafo = grafo;
        limpar();
    }

    /** Realça um resultado: arestas desenhadas, arestas em vermelho e cores de vértice. */
    public void realcar(List<Aresta> visiveis, Set<String> destacadas, Map<String, Color> cores) {
        this.arestasVisiveis = visiveis;
        this.destacadas = (destacadas == null) ? new HashSet<>() : destacadas;
        this.coresVertices = (cores == null) ? new HashMap<>() : cores;
        repaint();
    }

    public void limpar() {
        arestasVisiveis = null;
        destacadas = new HashSet<>();
        coresVertices = new HashMap<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (grafo.getVertices().isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("Grafo sem vértices.", 20, 30);
            return;
        }

        posicionarEmCirculo();

        List<Aresta> lista = (arestasVisiveis == null) ? grafo.getArestas() : arestasVisiveis;
        for (Aresta a : lista) {
            desenharAresta(g2, a);
        }
        for (Vertice v : grafo.getVertices()) {
            desenharVertice(g2, v);
        }
    }

    /** Distribui os vértices igualmente em uma circunferência. */
    private void posicionarEmCirculo() {
        List<Vertice> lista = grafo.getVertices();
        int n = lista.size();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int raio = Math.max(60, Math.min(getWidth(), getHeight()) / 2 - 60);

        posicoes.clear();
        for (int i = 0; i < n; i++) {
            double angulo = -Math.PI / 2 + (2 * Math.PI * i) / n;
            posicoes.put(lista.get(i).getId(),
                    new Point((int) (cx + raio * Math.cos(angulo)), (int) (cy + raio * Math.sin(angulo))));
        }
    }

    private void desenharAresta(Graphics2D g2, Aresta a) {
        Point p1 = posicoes.get(a.getOrigem().getId());
        Point p2 = posicoes.get(a.getDestino().getId());

        boolean destaque = destacadas.contains(a.getId());
        g2.setColor(destaque ? COR_DESTAQUE : Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(destaque ? 3f : 1.5f));

        String texto = a.getId() + " (" + a.getPesoFormatado() + ")";

        if (a.isLaco()) {
            g2.drawOval(p1.x - 18, p1.y - RAIO - 26, 36, 30);
            escrever(g2, texto, p1.x, p1.y - RAIO - 32);
            return;
        }

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double dist = Math.hypot(dx, dy);
        double ux = dx / dist;
        double uy = dy / dist;

        // Em grafo dirigido a reta é deslocada para a lateral. Como a
        // perpendicular acompanha o sentido origem -> destino, o arco de ida e
        // o de volta entre o mesmo par caem em lados opostos automaticamente.
        int desvio = grafo.isDirigido() ? 16 : 0;
        int deslocX = (int) (-uy * desvio);
        int deslocY = (int) (ux * desvio);

        // A reta começa e termina na borda dos círculos, não no centro.
        int x1 = (int) (p1.x + ux * RAIO) + deslocX;
        int y1 = (int) (p1.y + uy * RAIO) + deslocY;
        int x2 = (int) (p2.x - ux * RAIO) + deslocX;
        int y2 = (int) (p2.y - uy * RAIO) + deslocY;

        g2.drawLine(x1, y1, x2, y2);

        if (grafo.isDirigido()) {
            desenharSeta(g2, x2, y2, ux, uy);
        }

        escrever(g2, texto, (x1 + x2) / 2, (y1 + y2) / 2);
    }

    private void desenharSeta(Graphics2D g2, int x, int y, double ux, double uy) {
        double angulo = Math.atan2(uy, ux);
        double tamanho = 12;
        double abertura = Math.toRadians(22);

        int[] xs = {x, (int) (x - tamanho * Math.cos(angulo - abertura)),
                (int) (x - tamanho * Math.cos(angulo + abertura))};
        int[] ys = {y, (int) (y - tamanho * Math.sin(angulo - abertura)),
                (int) (y - tamanho * Math.sin(angulo + abertura))};

        g2.fillPolygon(xs, ys, 3);
    }

    /** Escreve o rótulo centralizado, com fundo branco para não sumir sobre a linha. */
    private void escrever(Graphics2D g2, String texto, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto);
        Color cor = g2.getColor();

        g2.setColor(Color.WHITE);
        g2.fillRect(x - largura / 2 - 2, y - fm.getAscent(), largura + 4, fm.getHeight());
        g2.setColor(cor);
        g2.drawString(texto, x - largura / 2, y);
    }

    private void desenharVertice(Graphics2D g2, Vertice v) {
        Point p = posicoes.get(v.getId());

        g2.setColor(coresVertices.getOrDefault(v.getId(), COR_VERTICE));
        g2.fillOval(p.x - RAIO, p.y - RAIO, RAIO * 2, RAIO * 2);

        g2.setColor(COR_BORDA);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(p.x - RAIO, p.y - RAIO, RAIO * 2, RAIO * 2);

        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(v.getId(), p.x - fm.stringWidth(v.getId()) / 2, p.y + fm.getAscent() / 2 - 2);
    }
}
