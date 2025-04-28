package org.hospiconnect.controller.laboratoire;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;
import org.hospiconnect.service.laboratoire.DisponibiliteAnalyseCrudService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendrierLaboController {

    @FXML
    private Label monthYearLabel;
    @FXML
    private Button prevMonthButton;
    @FXML
    private Button nextMonthButton;
    @FXML
    private Button todayButton;
    @FXML
    private ComboBox<String> viewModeComboBox;
    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

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
    private Button menuCalendrierButton;
    @FXML
    private Button menuHomeButton;

    private LocalDate currentDate;
    private final DisponibiliteAnalyseCrudService dispoService = DisponibiliteAnalyseCrudService.getInstance();

    @FXML
    public void initialize() {
        currentDate = LocalDate.now();

        viewModeComboBox.setItems(FXCollections.observableArrayList("Mois", "Semaine"));
        viewModeComboBox.setValue("Mois");

        updateCalendar();

        prevMonthButton.setOnAction(e -> {
            switch (viewModeComboBox.getValue()) {
                case "Mois" -> currentDate = currentDate.minusMonths(1);
                case "Semaine" -> currentDate = currentDate.minusWeeks(1);
            }
            updateCalendar();
        });

        nextMonthButton.setOnAction(e -> {
            switch (viewModeComboBox.getValue()) {
                case "Mois" -> currentDate = currentDate.plusMonths(1);
                case "Semaine" -> currentDate = currentDate.plusWeeks(1);
            }
            updateCalendar();
        });

        todayButton.setOnAction(e -> {
            currentDate = LocalDate.now();
            updateCalendar();
        });

        viewModeComboBox.setOnAction(e -> updateCalendar());

        setupNavigationMenu();
    }

    private void setupNavigationMenu() {
        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene("/laboratoireBack/analyse/listAnalyse.fxml", menuAnalyseButton.getScene(), null));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene("/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml", menuTypeAnalyseButton.getScene(), null));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene("/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml", menuDispoAnalyseButton.getScene(), null));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene("/laboratoireBack/dashboardLabo.fxml", menuDashboardButton.getScene(), null));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene("/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
        menuCalendrierButton.setOnAction(e -> SceneUtils.openNewScene("/laboratoireBack/calendrierLabo.fxml", menuCalendrierButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene("/HomePages/backList.fxml", menuHomeButton.getScene(), null));

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        List<DisponibiliteAnalyse> allDispos = dispoService.findAll();

        switch (viewModeComboBox.getValue()) {
            case "Mois" -> buildMonthView(allDispos);
            case "Semaine" -> buildWeekView(allDispos);
        }
    }

    private void buildMonthView(List<DisponibiliteAnalyse> dispos) {
        YearMonth yearMonth = YearMonth.from(currentDate);
        monthYearLabel.setText(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + yearMonth.getYear());

        LocalDate firstDay = yearMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        int row = 0;
        int col = (startDayOfWeek - 1) % 7;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            VBox box = new VBox();
            box.setPrefSize(120, 80);
            box.setSpacing(5);
            box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5;");

            boolean hasDispo = dispos.stream().anyMatch(d -> d.getDispo().equals(date));

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            box.getChildren().add(dayLabel);

            if (date.equals(LocalDate.now())) {
                box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5; -fx-background-color: #cfe2ff;");
            } else if (hasDispo) {
                box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5; -fx-background-color: #d4edda;");
            }

            if (hasDispo) {
                box.setOnMouseClicked(e -> showDisponibilitesPopup(date, dispos));
            }

            calendarGrid.add(box, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void buildWeekView(List<DisponibiliteAnalyse> dispos) {
        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        monthYearLabel.setText("Semaine du " + startOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Définir les contraintes de colonnes
        for (int i = 0; i <= 7; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            if (i == 0) {
                colConst.setPrefWidth(80); // Colonne horaire
            } else {
                colConst.setPrefWidth(130); // Colonne jour
            }
            calendarGrid.getColumnConstraints().add(colConst);
        }

        // Définir les contraintes de lignes
        for (int i = 0; i <= (16 - 8) * 2 + 1; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(40); // Chaque tranche de 30 minutes = 40px
            calendarGrid.getRowConstraints().add(rowConst);
        }

        // En-tête des jours
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(startOfWeek.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-alignment: center; -fx-text-alignment: center;");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setMaxHeight(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, i + 1, 0);
        }

        // Colonne des heures
        for (int i = 0, hour = 8; hour <= 16; hour++, i += 2) {
            Label hourLabel = new Label(hour + ":00");
            hourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center-right;");
            calendarGrid.add(hourLabel, 0, i + 1);

            if (hour < 16) {
                Label halfHourLabel = new Label(hour + ":30");
                halfHourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center-right;");
                calendarGrid.add(halfHourLabel, 0, i + 2);
            }
        }

        // Ajouter les disponibilités
        for (DisponibiliteAnalyse dispo : dispos) {
            if (!dispo.getDispo().isBefore(startOfWeek) && !dispo.getDispo().isAfter(startOfWeek.plusDays(6))) {
                int col = dispo.getDispo().getDayOfWeek().getValue();
                int rowStart = ((dispo.getDebut().getHour() - 8) * 2) + (dispo.getDebut().getMinute() >= 30 ? 1 : 0) + 1;
                int rowEnd = ((dispo.getFin().getHour() - 8) * 2) + (dispo.getFin().getMinute() > 0 ? 1 : 0) + 1;
                int rowSpan = Math.max(1, rowEnd - rowStart);

                Label event = new Label(dispo.getDebut() + " - " + dispo.getFin() + " (" + dispo.getNbrPlaces() + ")");
                event.setStyle("-fx-background-color: #90ee90; -fx-padding: 5; -fx-border-color: black; -fx-alignment: center; -fx-text-alignment: center;");
                event.setMaxWidth(Double.MAX_VALUE);
                event.setMaxHeight(Double.MAX_VALUE);
                calendarGrid.add(event, col, rowStart, 1, rowSpan);
            }
        }
    }


    private void showDisponibilitesPopup(LocalDate date, List<DisponibiliteAnalyse> dispos) {
        List<DisponibiliteAnalyse> dayDispos = dispos.stream()
                .filter(d -> d.getDispo().equals(date))
                .toList();

        StringBuilder message = new StringBuilder();
        for (DisponibiliteAnalyse dispo : dayDispos) {
            message.append("- Début : ").append(dispo.getDebut())
                    .append(", Fin : ").append(dispo.getFin())
                    .append(", Places : ").append(dispo.getNbrPlaces()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disponibilités le " + date);
        alert.setHeaderText("Créneaux disponibles :");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }
}
