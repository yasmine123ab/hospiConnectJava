package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.RendezVous;
import org.hospiconnect.service.RendezVousService;

import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ListeCalendrierController {

    @FXML private Label monthYearLabel;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private Button todayButton;
    @FXML private ComboBox<String> viewModeComboBox;
    @FXML private GridPane calendarGrid;
    @FXML private Button fermerFenetreButton, reduireFenetreButton;
    @FXML private Button menuAddRendezVousButton, menuListRendezVousButton, menuHomeButton;

    private LocalDate currentDate;
    private final RendezVousService rendezVousService = new RendezVousService();

    // Dans ListeCalendrierController.java

    private ObservableList<RendezVous> rendezVousList;




    @FXML
    public void initialize() {
        currentDate = LocalDate.now();
        viewModeComboBox.setItems(FXCollections.observableArrayList("Mois", "Semaine"));
        viewModeComboBox.setValue("Mois");
        try {
            updateCalendar();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }

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
        menuAddRendezVousButton.setOnAction(e -> SceneUtils.openNewScene("/AddRendezVous.fxml", menuAddRendezVousButton.getScene(), null));
        menuListRendezVousButton.setOnAction(e -> SceneUtils.openNewScene("/ListeRendezVous.fxml", menuListRendezVousButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene("/HomePages/backList.fxml", menuHomeButton.getScene(), null));
        fermerFenetreButton.setOnAction(e -> ((Stage) fermerFenetreButton.getScene().getWindow()).close());
        reduireFenetreButton.setOnAction(e -> ((Stage) reduireFenetreButton.getScene().getWindow()).setIconified(true));
    }

    public void setRendezVousList(ObservableList<RendezVous> rendezVousList) {
        this.rendezVousList = FXCollections.observableArrayList(rendezVousList);
        updateCalendar(); // Rafraîchir le calendrier avec les nouvelles données
    }


    // Modifier la signature de updateCalendar pour qu'elle ne déclare pas throws SQLException
    private void updateCalendar() {
        try {
            calendarGrid.getChildren().clear();
            calendarGrid.getColumnConstraints().clear();
            calendarGrid.getRowConstraints().clear();

            List<RendezVous> allRendezVous = rendezVousService.findAll();
            if (viewModeComboBox.getValue().equals("Mois")) {
                buildMonthView(allRendezVous);
            } else {
                buildWeekView(allRendezVous);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les rendez-vous: " + e.getMessage());
        }
    }

    // Ajouter cette méthode utilitaire
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void buildMonthView(List<RendezVous> rendezVousList) {
        YearMonth yearMonth = YearMonth.from(currentDate);
        monthYearLabel.setText(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + yearMonth.getYear());

        LocalDate firstDay = yearMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        // Configure grid
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(120);
            calendarGrid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < 6; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(80);
            calendarGrid.getRowConstraints().add(rc);
        }

        // Add day headers
        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = createCell(days[i], true);
            calendarGrid.add(dayLabel, i, 0);
        }

        int row = 1;
        int col = (startDayOfWeek - 1) % 7;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            VBox box = new VBox();
            box.setPrefSize(120, 80);
            box.setSpacing(5);
            box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5;");

            long rdvCount = rendezVousList.stream().filter(r -> r.getDate().equals(date)).count();

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            box.getChildren().add(dayLabel);

            if (rdvCount > 0) {
                Label countLabel = new Label(rdvCount + " RDV");
                countLabel.setStyle("-fx-font-size: 12;");
                box.getChildren().add(countLabel);
            }

            String bgColor = rdvCount >= 5 ? "#f8d7da" : rdvCount >= 3 ? "#fff3cd" : rdvCount > 0 ? "#d4edda" : "";
            if (date.equals(LocalDate.now())) {
                box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5; -fx-background-color: #cfe2ff;");
            } else if (!bgColor.isEmpty()) {
                box.setStyle("-fx-border-color: #cccccc; -fx-padding: 5; -fx-background-color: " + bgColor + ";");
            }

            if (rdvCount > 0) {
                box.setOnMouseClicked(e -> showRendezVousPopup(date, rendezVousList));
            }

            calendarGrid.add(box, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void buildWeekView(List<RendezVous> rendezVousList) {
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        monthYearLabel.setText("Semaine du " + startOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Configure grid
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

        // Add day headers
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            Label dayLabel = createCell(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + date.getDayOfMonth(), true);
            calendarGrid.add(dayLabel, i + 1, 0);
        }

        // Add hour labels
        for (int hour = 8; hour <= 16; hour++) {
            Label hourLabel = createCell(hour + ":00", true);
            calendarGrid.add(hourLabel, 0, (hour - 8) + 1);
        }

        // Populate rendezvous
        for (int day = 0; day < 7; day++) {
            LocalDate date = startOfWeek.plusDays(day);
            List<RendezVous> rdvDuJour = rendezVousList.stream()
                    .filter(r -> r.getDate().equals(date))
                    .sorted((r1, r2) -> r1.getHeure().compareTo(r2.getHeure()))
                    .toList();

            for (int hour = 8; hour <= 16; hour++) {
                VBox box = new VBox();
                box.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0.5px;");
                box.setPrefHeight(60);
                box.setPrefWidth(150);

                for (RendezVous rdv : rdvDuJour) {
                    if (rdv.getHeure().getHour() == hour) {
                        Label event = new Label(rdv.getHeure().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + rdv.getPrenom() + " " + rdv.getNom());
                        String bgColor = rdv.getGravite().equals("Elevée") ? "#f8d7da" : rdv.getGravite().equals("Moyenne") ? "#fff3cd" : "#d4edda";
                        event.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 5px; -fx-padding: 2; -fx-border-color: #cccccc; -fx-border-width: 0.5px;");
                        event.setMaxWidth(Double.MAX_VALUE);
                        box.getChildren().add(event);
                    }
                }

                if (!box.getChildren().isEmpty()) {
                    final LocalDate finalDate = date;
                    final int finalHour = hour;
                    box.setOnMouseClicked(e -> showRendezVousPopup(finalDate, finalHour, rendezVousList));
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

    private void showRendezVousPopup(LocalDate date, List<RendezVous> rendezVousList) {
        List<RendezVous> dayRendezVous = rendezVousList.stream()
                .filter(r -> r.getDate().equals(date))
                .toList();
        StringBuilder message = new StringBuilder();
        for (RendezVous rdv : dayRendezVous) {
            message.append("- ").append(rdv.getHeure().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .append(": ").append(rdv.getPrenom()).append(" ").append(rdv.getNom())
                    .append(" (").append(rdv.getType()).append(", ").append(rdv.getGravite()).append(")\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rendez-vous le " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        alert.setHeaderText("Détails des rendez-vous :");
        alert.setContentText(message.length() > 0 ? message.toString() : "Aucun rendez-vous.");
        alert.showAndWait();
    }

    private void showRendezVousPopup(LocalDate date, int hour, List<RendezVous> rendezVousList) {
        List<RendezVous> hourRendezVous = rendezVousList.stream()
                .filter(r -> r.getDate().equals(date) && r.getHeure().getHour() == hour)
                .toList();
        StringBuilder message = new StringBuilder();
        for (RendezVous rdv : hourRendezVous) {
            message.append("- ").append(rdv.getHeure().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .append(": ").append(rdv.getPrenom()).append(" ").append(rdv.getNom())
                    .append(" (").append(rdv.getType()).append(", ").append(rdv.getGravite()).append(")\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rendez-vous le " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " à " + hour + ":00");
        alert.setHeaderText("Détails des rendez-vous :");
        alert.setContentText(message.length() > 0 ? message.toString() : "Aucun rendez-vous.");
        alert.showAndWait();
    }
}