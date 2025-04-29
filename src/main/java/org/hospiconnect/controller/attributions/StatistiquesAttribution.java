package org.hospiconnect.controller.attributions;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.service.AttributionDonService;


import java.sql.SQLException;
import java.util.Map;

public class StatistiquesAttribution {
    @FXML
    private PieChart donPieChart;
    @FXML private BarChart<String, Number> donBarChart;
    @FXML private Label totalLabel;
    @FXML private Button retourButton;

    // Service pour accéder aux dons
    private final AttributionDonService attributionDonService = AttributionDonService.getInstance();

    private void loadDonationStats() {
        try {
            Map<String, Integer> stats = attributionDonService.getAttributionStatisticsByStatut();
            // PieChart
            stats.forEach((type, count) -> {
                PieChart.Data slice = new PieChart.Data(type, count);
                donPieChart.getData().add(slice);
            });


        } catch (SQLException e) {
            e.printStackTrace();
            totalLabel.setText("Erreur chargement stats");
        }
    }
    @FXML
    public void initialize() {
        loadDonationStats();
    }


    @FXML
    private void handleRetour() {
        // Retour à la liste des dons
        SceneUtils.openNewScene("/Dons/ShowDon.fxml", retourButton.getScene(), null);
    }
}
