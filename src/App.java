public class App {
    public static void main(String[] args) throws Exception {
        javax.swing.SwingUtilities.invokeLater(() -> {
            FrmTemperaturas frmTemperaturas = new FrmTemperaturas();
            frmTemperaturas.setVisible(true);
        });
    }
}
