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

    @FXML private Label monthYearLabel;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private Button todayButton;
    @FXML private ComboBox<String> viewModeComboBox;
    @FXML private GridPane calendarGrid;
    @FXML private Button FermerFenetreButton, ReduireFenetreButton;
    @FXML private Button menuAnalyseButton, menuTypeAnalyseButton, menuDispoAnalyseButton;
    @FXML private Button menuDashboardButton, menuHospiChatButton, menuCalendrierButton, menuHomeButton;

    private LocalDate currentDate;
    private final DisponibiliteAnalyseCrudService dispoService = DisponibiliteAnalyseCrudService.getInstance();

    @FXML
    public void initialize() {
        currentDate = LocalDate.now();
        viewModeComboBox.setItems(FXCollections.observableArrayList("Mois", "Semaine"));
        viewModeComboBox.setValue("Mois");
        updateCalendar();

        prevMonthButton.setOnAction(e -> {
            currentDate = viewModeComboBox.getValue().equals("Mois") ? currentDate.minusMonths(1) : currentDate.minusWeeks(1);
            updateCalendar();
        });

        nextMonthButton.setOnAction(e -> {
            currentDate = viewModeComboBox.getValue().equals("Mois") ? currentDate.plusMonths(1) : currentDate.plusWeeks(1);
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
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        List<DisponibiliteAnalyse> allDispos = dispoService.findAll();
        if (viewModeComboBox.getValue().equals("Mois")) {
            buildMonthView(allDispos);
        } else {
            buildWeekView(allDispos);
        }
    }

    private void buildMonthView(List<DisponibiliteAnalyse> dispos) {
        // Reste inchangé pour le mode mois
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

            boolean hasDispo = dispoService.findAll().stream().anyMatch(d -> d.getDispo().equals(date));

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            box.getChildren().add(dayLabel);

            if (date.equals(LocalDate.now())) {
                box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5; -fx-background-color: #cfe2ff;");
            } else if (hasDispo) {
                box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5; -fx-background-color: #d4edda;");
            }

            if (hasDispo) {
                box.setOnMouseClicked(e -> showDisponibilitesPopup(date, dispoService.findAll()));
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
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        monthYearLabel.setText("Semaine du " + startOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        for (int i = 0; i <= 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(i == 0 ? 80 : 150);
            calendarGrid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i <= 8; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(60);
            calendarGrid.getRowConstraints().add(rc);
        }

        for (int i = 0; i < 7; i++) {
            Label dayLabel = createCell(startOfWeek.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH), true);
            calendarGrid.add(dayLabel, i + 1, 0);
        }

        for (int hour = 8; hour <= 16; hour++) {
            Label hourLabel = createCell(hour + ":00", true);
            calendarGrid.add(hourLabel, 0, (hour - 8) + 1);
        }

        for (int day = 0; day < 7; day++) {
            LocalDate date = startOfWeek.plusDays(day);
            List<DisponibiliteAnalyse> disposDuJour = dispos.stream()
                    .filter(d -> d.getDispo().equals(date))
                    .sorted((d1, d2) -> d1.getDebut().compareTo(d2.getDebut()))
                    .toList();

            for (int hour = 8; hour <= 16; hour++) {
                VBox box = new VBox();
                box.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0.5px;");
                box.setPrefHeight(60);
                box.setPrefWidth(150);

                for (DisponibiliteAnalyse dispo : disposDuJour) {
                    if (dispo.getDebut().getHour() == hour) {
                        Label event = new Label(dispo.getDebut() + " - " + dispo.getFin() + " (" + dispo.getNbrPlaces() + ")");
                        String bgColor = (dispo.getNbrPlaces() <= 3) ? "#f8d7da" : (dispo.getNbrPlaces() <= 7) ? "#fff3cd" : "#d4edda";
                        event.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 5px; -fx-padding: 2; -fx-border-color: #cccccc; -fx-border-width: 0.5px;");
                        event.setMaxWidth(Double.MAX_VALUE);
                        box.getChildren().add(event);
                    }
                }

                calendarGrid.add(box, day + 1, (hour - 8) + 1);
            }
        }
    }

    private Label createCell(String text, boolean isHeader) {
        Label label = new Label(text);
        label.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0.5px; -fx-alignment: center; -fx-text-alignment: center; -fx-font-size: " + (isHeader ? "14px" : "12px") + ";");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void showDisponibilitesPopup(LocalDate date, List<DisponibiliteAnalyse> dispos) {
        List<DisponibiliteAnalyse> dayDispos = dispos.stream()
                .filter(d -> d.getDispo().equals(date))
                .toList();
        StringBuilder message = new StringBuilder();
        for (DisponibiliteAnalyse dispo : dayDispos) {
            message.append("- Début : ").append(dispo.getDebut()).append(", Fin : ").append(dispo.getFin()).append(", Places : ").append(dispo.getNbrPlaces()).append("\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disponibilités le " + date);
        alert.setHeaderText("Créneaux disponibles :");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }
}
