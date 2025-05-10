import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import datechooser.beans.DateChooserCombo;

public class FrmTemperaturas extends JFrame {

    // Componentes para la consulta de extremos
    private DateChooserCombo dccExtremos;
    private JButton btnConsultarExtremos;
    private JLabel lblCiudadMasCalurosa;
    private JLabel lblCiudadMasFria;

    // Componentes para la consulta promedios
    private DateChooserCombo dccDesde , dccHasta;
    private JButton btnMostrarGrafica;
    private JPanel pnlGrafica;

    private List<RegistroTemperatura> registros;

    public FrmTemperaturas() {
        setTitle("Registro de Temperaturas");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        registros = ProcesadorTemperaturas.cargarDesdeCSV("./data/Temperaturas.csv");

        // Panel superior: Consulta extremos

        JPanel panelTemperaturasExtremas = new JPanel();
        panelTemperaturasExtremas.setLayout(new BorderLayout());
        dccExtremos = new DateChooserCombo();
        btnConsultarExtremos = new JButton("Consultar extremos");
        lblCiudadMasCalurosa = new JLabel("Ciudad más calurosa: ");
        lblCiudadMasFria = new JLabel("Ciudad más fría: ");

        // Subpanel para la seleccion de fecha y el boton
        JPanel panelSeleccionFecha = new JPanel();
        panelSeleccionFecha.add(new JLabel("Fecha: "));
        panelSeleccionFecha.add(dccExtremos);
        panelSeleccionFecha.add(btnConsultarExtremos);

        // Subpanel para mostrar resultados
        JPanel panelResultadosExtremos = new JPanel();
        panelResultadosExtremos.setLayout(new GridLayout(2,1));
        panelResultadosExtremos.add(lblCiudadMasCalurosa);
        panelResultadosExtremos.add(lblCiudadMasFria);

        panelTemperaturasExtremas.setBorder(BorderFactory.createTitledBorder("Consulta de temperaturas extremas por fecha"));
        panelTemperaturasExtremas.add(panelSeleccionFecha, BorderLayout.NORTH);
        panelTemperaturasExtremas.add(panelResultadosExtremos, BorderLayout.CENTER);

        // Panel inferior: Consulta de promedios

        JPanel panelTemperaturasPromedio = new JPanel();
        dccDesde = new DateChooserCombo();
        dccHasta = new DateChooserCombo();
        btnMostrarGrafica = new JButton("Mostrar gráfica");

        panelTemperaturasPromedio.setBorder(
                BorderFactory.createTitledBorder("Consulta de Promedios de Temperatura por rango de fechas"));
        panelTemperaturasPromedio.add(new JLabel("Desde: "));
        panelTemperaturasPromedio.add(dccDesde);
        panelTemperaturasPromedio.add(new JLabel("Hasta: "));
        panelTemperaturasPromedio.add(dccHasta);
        panelTemperaturasPromedio.add(btnMostrarGrafica);

        // Panel para la gráfica
        pnlGrafica = new JPanel();
        pnlGrafica.setPreferredSize(new Dimension(700, 500));
        pnlGrafica.setBorder(BorderFactory.createTitledBorder("Gráfica de temperaturas promedio por ciudad"));
        ((javax.swing.border.TitledBorder) pnlGrafica.getBorder()).setTitleColor(Color.GRAY);
        
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelTemperaturasPromedio, BorderLayout.NORTH);
        panelCentro.add(pnlGrafica, BorderLayout.CENTER);

        add(panelTemperaturasExtremas, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);

        // Eventos de botones

        btnConsultarExtremos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarExtremos();
            }
        });

        btnMostrarGrafica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarGraficaPromedios();
            }
        });

    }

    private void consultarExtremos() {
        try {
            LocalDate fecha = dccExtremos.getSelectedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            ResultadoExtremos resultado = ProcesadorTemperaturas.temperaturasExtremasPorFecha(fecha, registros);
            lblCiudadMasCalurosa.setText("Ciudad más calurosa: " + resultado.getCiudadMasCalurosa() + " (" + resultado.getTemperaturaMax() + "°C)");
            lblCiudadMasFria.setText("Ciudad más fría: " + resultado.getCiudadMasFria() + " (" + resultado.getTemperaturaMin() + "°C)");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida o error al consultar los extremos.");
        }
    }
    
    private void mostrarGraficaPromedios() {
        try {
            // Obtener fechas seleccionadas en los calendarios
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            List<RegistroTemperatura> filtrados = ProcesadorTemperaturas.filtrarPorFecha(desde, hasta, registros);

            if (filtrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para el rango de fechas especificado.");
                return;
            }

            Map<String, Double> datosPromedio = ProcesadorTemperaturas.calcularTemperaturaPromedioPorCiudad(filtrados);

            // crear dataset para grafica de barras
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Double> entrada : datosPromedio.entrySet()) {
                dataset.addValue(entrada.getValue(), "Temperatura promedio", entrada.getKey());
            }

            // Crear gráfica de barras
            JFreeChart grafica = ChartFactory.createBarChart(
                    "Temperatura Promedio por Ciudad",
                    "Ciudad",
                    "Temperatura (°C)",
                    dataset);

            // Mostrar gráfica en el panel
            ChartPanel panel = new ChartPanel(grafica);
            panel.setPreferredSize(new Dimension(700, 400));

            pnlGrafica.removeAll();
            pnlGrafica.setLayout(new BorderLayout());
            pnlGrafica.add(panel, BorderLayout.CENTER);
            pnlGrafica.revalidate();
            pnlGrafica.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener las fechas seleccionadas.");
        }
    }
}
