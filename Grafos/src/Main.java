import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada do programa.
 *
 * Abre a interface gráfica com a moldura, o menu de operações e a
 * visualização do grafo ativo.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // Mantém a aparência padrão do Swing caso o sistema não permita trocar.
        }

        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
