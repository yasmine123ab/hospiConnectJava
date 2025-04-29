package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.AnalyseCrudService;
import org.hospiconnect.service.laboratoire.TypeAnalyseCrudService;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardLaboController {

    @FXML private Button FermerFenetreButton;
    @FXML private Button ReduireFenetreButton;

    @FXML
    private Button menuAnalyseButton;
    @FXML
    private Button menuTypeAnalyseButton;
    @FXML
    private Button menuDispoAnalyseButton;
    @FXML
    private Button menuDashboardButton;
    @FXML
    private Button menuHospiChatButton;
    @FXML
    private Button menuHomeButton;
    @FXML
    private Button menuCalendrierButton;

    @FXML private PieChart laboPieChart;
    @FXML private BarChart<String, Number> laboBarChart;
    @FXML private LineChart<String, Number> laboLineChart;

    private final AnalyseCrudService analyseService = AnalyseCrudService.getInstance();
    private final TypeAnalyseCrudService typeService = TypeAnalyseCrudService.getInstance();

    @FXML
    public void initialize() {
        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml", menuAnalyseButton.getScene(), null));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml", menuTypeAnalyseButton.getScene(), null));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml", menuDispoAnalyseButton.getScene(), null));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/dashboardLabo.fxml", menuDashboardButton.getScene(), null));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
        menuCalendrierButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/calendrierLabo.fxml", menuCalendrierButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));

        loadPieChart();
        loadBarChart();
        loadLineChart();
    }

    private void loadPieChart() {
        Map<String, Long> etatCounts = analyseService.findAll().stream()
                .collect(Collectors.groupingBy(Analyse::getEtat, Collectors.counting()));

        laboPieChart.getData().clear();
        etatCounts.forEach((etat, count) -> {
            laboPieChart.getData().add(new PieChart.Data(etat, count));
        });
    }

    private void loadBarChart() {
        List<Analyse> analyses = analyseService.findAll();
        List<TypeAnalyse> types = typeService.findAll();

        Map<Long, Long> countByTypeId = analyses.stream()
                .collect(Collectors.groupingBy(Analyse::getIdTypeAnalyse, Collectors.counting()));

        Map<String, Long> countByTypeName = new HashMap<>();
        for (TypeAnalyse type : types) {
            Long count = countByTypeId.getOrDefault(type.getId(), 0L);
            countByTypeName.put(type.getNom(), count);
        }

        laboBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Analyses par type");

        countByTypeName.forEach((nom, count) -> {
            series.getData().add(new XYChart.Data<>(nom, count));
        });

        laboBarChart.getData().add(series);
    }

    private void loadLineChart() {
        List<Analyse> analyses = analyseService.findAll();

        Map<String, Long> analysesParMois = new TreeMap<>();

        // Initialiser tous les mois à 0
        for (Month month : Month.values()) {
            analysesParMois.put(month.name(), 0L);
        }

        for (Analyse a : analyses) {
            if (a.getDatePrelevement() != null) {
                Month mois = a.getDatePrelevement().getMonth();
                String nomMois = mois.name();
                analysesParMois.put(nomMois, analysesParMois.get(nomMois) + 1);
            }
        }

        laboLineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Évolution annuelle");

        analysesParMois.forEach((mois, count) -> {
            series.getData().add(new XYChart.Data<>(mois, count));
        });

        laboLineChart.getData().add(series);
    }
}
