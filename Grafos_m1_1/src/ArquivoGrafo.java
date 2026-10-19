import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 Leitura e gravação de grafo em arquivo de texto.

 Formato, uma linha por item, campos separados por ponto e vírgula:

 <pre>
 # linhas iniciadas por # são comentário e linhas em branco são ignoradas
 NAO_DIRIGIDO                           primeira linha útil: tipo do grafo
 V;Curitiba;-25.4133;-49.2679           vértice com coordenada geográfica
 V;Toledo                               vértice sem coordenada
 A;e1;Curitiba;Toledo;114               aresta: id, origem, destino, peso
 </pre>

 A coordenada é opcional: vértice sem ela funciona normalmente, apenas não
 contribui com heurística para o A*.
 */
public class ArquivoGrafo {

    private ArquivoGrafo() {
    }

    /** Lê um grafo do arquivo. Indica a linha quando encontra erro de formato. */
    public static Grafo carregar(File arquivo) throws IOException {
        Grafo grafo = null;
        int numeroLinha = 0;

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {

            String linha;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                String[] campos = linha.split(";");
                for (int i = 0; i < campos.length; i++) {
                    campos[i] = campos[i].trim();
                }

                try {
                    if (grafo == null) {
                        grafo = new Grafo(lerTipo(campos[0]));
                    } else {
                        lerItem(grafo, campos);
                    }
                } catch (RuntimeException ex) {
                    throw new IOException("Linha " + numeroLinha + ": " + ex.getMessage());
                }
            }
        }

        if (grafo == null) {
            throw new IOException("Arquivo sem conteúdo: falta a linha com o tipo do grafo.");
        }
        return grafo;
    }

    /** Grava o grafo no arquivo, no mesmo formato que o carregar espera. */
    public static void salvar(Grafo grafo, File arquivo) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {

            escritor.write("# Grafo gravado pelo programa");
            escritor.newLine();
            escritor.write(grafo.isDirigido() ? "DIRIGIDO" : "NAO_DIRIGIDO");
            escritor.newLine();

            for (Vertice v : grafo.getVertices()) {
                if (v.temCoordenadas()) {
                    escritor.write("V;" + v.getId() + ";" + v.getLatitude() + ";" + v.getLongitude());
                } else {
                    escritor.write("V;" + v.getId());
                }
                escritor.newLine();
            }

            for (Aresta a : grafo.getArestas()) {
                escritor.write("A;" + a.getId() + ";" + a.getOrigem().getId()
                        + ";" + a.getDestino().getId() + ";" + a.getPesoFormatado());
                escritor.newLine();
            }
        }
    }

    private static boolean lerTipo(String texto) {
        if (texto.equalsIgnoreCase("DIRIGIDO")) {
            return true;
        }
        if (texto.equalsIgnoreCase("NAO_DIRIGIDO") || texto.equalsIgnoreCase("NÃO_DIRIGIDO")) {
            return false;
        }
        throw new IllegalArgumentException(
                "a primeira linha deve ser DIRIGIDO ou NAO_DIRIGIDO, e veio: " + texto);
    }

    private static void lerItem(Grafo grafo, String[] campos) {
        if (campos[0].equalsIgnoreCase("V")) {
            if (campos.length == 2) {
                grafo.inserirVertice(campos[1]);
            } else if (campos.length == 4) {
                grafo.inserirVertice(campos[1], lerNumero(campos[2]), lerNumero(campos[3]));
            } else {
                throw new IllegalArgumentException(
                        "vértice deve ser V;id ou V;id;latitude;longitude");
            }

        } else if (campos[0].equalsIgnoreCase("A")) {
            if (campos.length != 5) {
                throw new IllegalArgumentException(
                        "aresta/arco deve ser A;id;origem;destino;peso");
            }
            grafo.inserirAresta(campos[1], campos[2], campos[3], lerNumero(campos[4]));

        } else {
            throw new IllegalArgumentException(
                    "a linha deve começar com V de vértice ou A de aresta, e veio: " + campos[0]);
        }
    }

    private static double lerNumero(String texto) {
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("valor numérico inválido: " + texto);
        }
    }
}
