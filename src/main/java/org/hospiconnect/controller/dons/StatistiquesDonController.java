package org.hospiconnect.controller.dons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import org.hospiconnect.service.DonService;
import org.hospiconnect.controller.laboratoire.SceneUtils;

import java.sql.SQLException;
import java.util.Map;

public class StatistiquesDonController {

    @FXML private PieChart donPieChart;
    @FXML private BarChart<String, Number> donBarChart;
    @FXML private Label totalLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button retourButton;

    // Service pour accéder aux dons
    private final DonService donService = DonService.getInstance();

    // Objectif de la cagnotte (modifiable)
    private static final double OBJECTIF = 10000.0;

    @FXML
    public void initialize() {
        loadDonationStats();
        loadCagnotte();
    }

    private void loadDonationStats() {
        try {
            Map<String, Integer> stats = donService.getDonStatistics();
            Map<String, Integer> statsdispo = donService.getDonStatisticsByDisponibilite();
            // PieChart
            stats.forEach((type, count) -> {
                PieChart.Data slice = new PieChart.Data(type, count);
                donPieChart.getData().add(slice);
            });
            // BarChart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            statsdispo.forEach((type, count) ->
                    series.getData().add(new XYChart.Data<>(type, count))
            );
            donBarChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
            totalLabel.setText("Erreur chargement stats");
        }
    }

    private void loadCagnotte() {
        try {
            double total = donService.getTotalMontant();
            totalLabel.setText(String.format("Cagnotte totale : %.2f DT", total));

            // Calcul du % de l'objectif
            double progress = Math.min(total / OBJECTIF, 1.0);
            progressBar.setProgress(progress);

        } catch (SQLException e) {
            e.printStackTrace();
            totalLabel.setText("Erreur chargement cagnotte");
        }
    }

    @FXML
    private void handleRetour() {
        // Retour à la liste des dons
        SceneUtils.openNewScene("/Dons/ShowDon.fxml", retourButton.getScene(), null);
    }
}
