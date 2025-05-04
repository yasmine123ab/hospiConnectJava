package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hospiconnect.model.RendezVous;
import org.hospiconnect.service.RendezVousService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ListeRendezVousController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private VBox tableContainer;
    @FXML private VBox dataRows;

    private final ObservableList<RendezVous> data = FXCollections.observableArrayList();
    private RendezVousService rendezVousService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rendezVousService = new RendezVousService();

        // Charger les données initiales
        refreshData();

        // Configurer la recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerRendezVous(newValue);
        });

        // Configurer le ComboBox de tri
        sortComboBox.getItems().addAll(
                "Nom (A-Z)", "Nom (Z-A)",
                "Date (Ancien -> Récent)", "Date (Récent -> Ancien)",
                "Type (A-Z)", "Type (Z-A)"
        );
        sortComboBox.setOnAction(event -> trierRendezVous());
    }

    public void refreshData() {
        try {
            List<RendezVous> rendezVousList = rendezVousService.findAll();
            data.setAll(rendezVousList);
            updateTableView();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des rendez-vous: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTableView() {
        dataRows.getChildren().clear();
        for (RendezVous rdv : data) {
            HBox row = createRow(rdv);
            dataRows.getChildren().add(row);
        }
        // Placeholder si vide
        if (data.isEmpty()) {
            Label placeholder = new Label("Aucun rendez-vous trouvé.");
            placeholder.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a3d7c; -fx-padding: 10;");
            dataRows.getChildren().add(placeholder);
        }
    }

    private HBox createRow(RendezVous rdv) {
        HBox row = new HBox();
        row.setPrefHeight(40.0);
        row.setStyle("-fx-border-color: #c3d7ff; -fx-border-width: 0 0 1 0; -fx-background-color: white;");

        // Créer les labels pour chaque champ
        Label nomLabel = new Label(rdv.getNom() != null ? rdv.getNom() : "");
        nomLabel.setPrefWidth(100.0);
        nomLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label prenomLabel = new Label(rdv.getPrenom() != null ? rdv.getPrenom() : "");
        prenomLabel.setPrefWidth(100.0);
        prenomLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label telLabel = new Label(rdv.getTelephone() != null ? rdv.getTelephone() : "");
        telLabel.setPrefWidth(110.0);
        telLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label emailLabel = new Label(rdv.getEmail() != null ? rdv.getEmail() : "");
        emailLabel.setPrefWidth(150.0);
        emailLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label dateLabel = new Label(rdv.getDate() != null ? rdv.getDate().toString() : "");
        dateLabel.setPrefWidth(90.0);
        dateLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label heureLabel = new Label(rdv.getHeure() != null ? rdv.getHeure().toString() : "");
        heureLabel.setPrefWidth(80.0);
        heureLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label typeLabel = new Label(rdv.getType() != null ? rdv.getType() : "");
        typeLabel.setPrefWidth(110.0);
        typeLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label graviteLabel = new Label(rdv.getGravite() != null ? rdv.getGravite() : "");
        graviteLabel.setPrefWidth(80.0);
        graviteLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        Label commentaireLabel = new Label(rdv.getCommentaire() != null ? rdv.getCommentaire() : "");
        commentaireLabel.setPrefWidth(140.0);
        commentaireLabel.setStyle("-fx-padding: 0 10 0 10; -fx-alignment: center-left;");

        // Boutons d'action
        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 4;");
        btnModifier.setOnAction(event -> modifierRendezVous(rdv));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #d93d3d; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 4; -fx-margin: 0 5 0 5;");
        btnSupprimer.setOnAction(event -> supprimerRendezVous(rdv));

        HBox actionBox = new HBox(btnModifier, btnSupprimer);
        actionBox.setPrefWidth(110.0);
        actionBox.setStyle("-fx-alignment: center;");

        row.getChildren().addAll(nomLabel, prenomLabel, telLabel, emailLabel, dateLabel, heureLabel, typeLabel, graviteLabel, commentaireLabel, actionBox);

        // Effet de survol
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #c3d7ff; -fx-border-width: 0 0 1 0;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-border-color: #c3d7ff; -fx-border-width: 0 0 1 0;"));

        return row;
    }

    private void filtrerRendezVous(String filtre) {
        if (filtre == null || filtre.isEmpty()) {
            updateTableView();
            return;
        }

        ObservableList<RendezVous> filteredList = FXCollections.observableArrayList();
        for (RendezVous rdv : data) {
            if ((rdv.getNom() != null && rdv.getNom().toLowerCase().contains(filtre.toLowerCase())) ||
                    (rdv.getPrenom() != null && rdv.getPrenom().toLowerCase().contains(filtre.toLowerCase())) ||
                    (rdv.getTelephone() != null && rdv.getTelephone().toLowerCase().contains(filtre.toLowerCase())) ||
                    (rdv.getEmail() != null && rdv.getEmail().toLowerCase().contains(filtre.toLowerCase())) ||
                    (rdv.getDate() != null && rdv.getDate().toString().contains(filtre)) ||
                    (rdv.getHeure() != null && rdv.getHeure().toString().contains(filtre)) ||
                    (rdv.getType() != null && rdv.getType().toLowerCase().contains(filtre.toLowerCase())) ||
                    (rdv.getGravite() != null && rdv.getGravite().toLowerCase().contains(filtre.toLowerCase())) ||
                    (rdv.getCommentaire() != null && rdv.getCommentaire().toLowerCase().contains(filtre.toLowerCase()))) {
                filteredList.add(rdv);
            }
        }

        dataRows.getChildren().clear();
        for (RendezVous rdv : filteredList) {
            HBox row = createRow(rdv);
            dataRows.getChildren().add(row);
        }
        if (filteredList.isEmpty()) {
            Label placeholder = new Label("Aucun rendez-vous trouvé.");
            placeholder.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a3d7c; -fx-padding: 10;");
            dataRows.getChildren().add(placeholder);
        }
    }

    private void trierRendezVous() {
        String selectedOption = sortComboBox.getSelectionModel().getSelectedItem();
        if (selectedOption == null) return;

        Comparator<RendezVous> comparator = null;

        switch (selectedOption) {
            case "Nom (A-Z)" -> comparator = Comparator.comparing(RendezVous::getNom, Comparator.nullsLast(String::compareToIgnoreCase));
            case "Nom (Z-A)" -> comparator = Comparator.comparing(RendezVous::getNom, Comparator.nullsLast(String::compareToIgnoreCase)).reversed();
            case "Date (Ancien -> Récent)" -> comparator = Comparator.comparing(RendezVous::getDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "Date (Récent -> Ancien)" -> comparator = Comparator.comparing(RendezVous::getDate, Comparator.nullsLast(Comparator.reverseOrder()));
            case "Type (A-Z)" -> comparator = Comparator.comparing(RendezVous::getType, Comparator.nullsLast(String::compareToIgnoreCase));
            case "Type (Z-A)" -> comparator = Comparator.comparing(RendezVous::getType, Comparator.nullsLast(String::compareToIgnoreCase)).reversed();
        }

        if (comparator != null) {
            FXCollections.sort(data, comparator);
            updateTableView();
        }
    }

    private void modifierRendezVous(RendezVous rdv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendezVousForm.fxml"));
            Parent root = loader.load();
            ModifierRendezVousController controller = loader.getController();
            controller.setRendezVous(rdv);

            Stage stage = new Stage();
            stage.setTitle("Modifier Rendez-vous");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshData();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'interface de modification");
            e.printStackTrace();
        }
    }

    private void supprimerRendezVous(RendezVous rdv) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le rendez-vous ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rendezVousService.delete(rdv);
                refreshData();
                showAlert("Succès", "Rendez-vous supprimé avec succès");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void retourAjout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AddRendezVousForm.fxml"));
            searchField.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void afficherCalendrier(ActionEvent event) {
        if (data == null || data.isEmpty()) {
            showAlert("Information", "Aucun rendez-vous à afficher");
            return;
        }

        LocalDate currentDate = LocalDate.now();
        AtomicReference<LocalDate> displayedDate = new AtomicReference<>(currentDate);

        AnchorPane root = new AnchorPane();
        root.setPrefSize(1259, 730);
        root.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = createLabel("Calendrier des Rendez-vous", 300, 20, "#1605ac", 24);

        Button closeButton = createButton("×", 1218, 10, "transparent", "#f40505", 18);
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());

        Button minimizeButton = createButton("-", 1190, 10, "transparent", "#07f259", 18);
        minimizeButton.setOnAction(e -> ((Stage) minimizeButton.getScene().getWindow()).setIconified(true));

        Label monthYearLabel = createLabel("", 600, 80, "#1605ac", 18);

        Button prevMonthButton = createButton("<", 300, 80, "#e0e0e0", "#000000", 14);
        Button nextMonthButton = createButton(">", 350, 80, "#e0e0e0", "#000000", 14);
        Button todayButton = createButton("Aujourd'hui", 400, 80, "#e0e0e0", "#000000", 14);

        ComboBox<String> monthComboBox = new ComboBox<>();
        monthComboBox.setLayoutX(500);
        monthComboBox.setLayoutY(80);
        monthComboBox.setPrefWidth(100);
        monthComboBox.getItems().addAll(getMonthNames());

        ComboBox<Integer> yearComboBox = new ComboBox<>();
        yearComboBox.setLayoutX(610);
        yearComboBox.setLayoutY(80);
        yearComboBox.setPrefWidth(80);
        yearComboBox.getItems().addAll(getYears(2020, 2030));

        GridPane calendarGrid = createCalendarGrid();

        Consumer<LocalDate> updateCalendar = date -> {
            calendarGrid.getChildren().clear();
            displayedDate.set(date);

            String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            monthYearLabel.setText(monthName + " " + date.getYear());

            monthComboBox.getSelectionModel().select(date.getMonthValue() - 1);
            yearComboBox.getSelectionModel().select((Integer) date.getYear());

            String[] joursSemaine = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
            for (int i = 0; i < 7; i++) {
                Label dayHeader = createLabel(joursSemaine[i], 0, 0, "#ffffff", 14);
                dayHeader.setPrefSize(130, 30);
                dayHeader.setAlignment(Pos.CENTER);
                dayHeader.setStyle("-fx-background-color: #1605ac; -fx-text-fill: white; -fx-font-weight: bold;");
                calendarGrid.add(dayHeader, i, 0);
            }

            YearMonth yearMonth = YearMonth.from(date);
            int daysInMonth = yearMonth.lengthOfMonth();
            LocalDate firstOfMonth = date.withDayOfMonth(1);
            int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
            if (dayOfWeek == 7) dayOfWeek = 0;

            int dayOfMonth = 1;
            for (int row = 1; row <= 6; row++) {
                for (int col = 0; col < 7; col++) {
                    VBox dayBox = new VBox();
                    dayBox.setPrefSize(130, 80);
                    dayBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

                    if ((row == 1 && col < dayOfWeek) || dayOfMonth > daysInMonth) {
                        calendarGrid.add(dayBox, col, row);
                    } else {
                        Label dayNumber = new Label(String.valueOf(dayOfMonth));
                        dayNumber.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
                        dayBox.getChildren().add(dayNumber);

                        LocalDate currentDay = LocalDate.of(date.getYear(), date.getMonth(), dayOfMonth);
                        addAppointmentsToDay(dayBox, currentDay);
                        calendarGrid.add(dayBox, col, row);
                        dayOfMonth++;
                    }
                }
            }
        };

        prevMonthButton.setOnAction(e -> updateCalendar.accept(displayedDate.get().minusMonths(1)));
        nextMonthButton.setOnAction(e -> updateCalendar.accept(displayedDate.get().plusMonths(1)));
        todayButton.setOnAction(e -> updateCalendar.accept(LocalDate.now()));
        monthComboBox.setOnAction(e -> updateMonthYear(monthComboBox, yearComboBox, updateCalendar));
        yearComboBox.setOnAction(e -> updateMonthYear(monthComboBox, yearComboBox, updateCalendar));

        updateCalendar.accept(currentDate);

        root.getChildren().addAll(titleLabel, closeButton, minimizeButton,
                prevMonthButton, nextMonthButton, todayButton,
                monthYearLabel, monthComboBox, yearComboBox, calendarGrid);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Calendrier des Rendez-vous");
        stage.show();
    }

    private Label createLabel(String text, double x, double y, String color, int size) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setTextFill(Color.web(color));
        label.setFont(Font.font("System", FontWeight.BOLD, size));
        return label;
    }

    private Button createButton(String text, double x, double y, String bgColor, String textColor, int size) {
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + ";");
        button.setFont(Font.font("System", FontWeight.BOLD, size));
        return button;
    }

    private GridPane createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.setLayoutX(280);
        grid.setLayoutY(130);
        grid.setPrefSize(920, 586);
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5;");
        return grid;
    }

    private void addAppointmentsToDay(VBox dayBox, LocalDate dayDate) {
        VBox rdvContainer = new VBox();
        rdvContainer.setSpacing(2);
        rdvContainer.setPadding(new Insets(0, 5, 5, 5));

        List<RendezVous> dailyAppointments = data.stream()
                .filter(rdv -> rdv.getDate() != null && rdv.getDate().equals(dayDate))
                .sorted(Comparator.comparing(RendezVous::getDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        for (RendezVous rdv : dailyAppointments) {
            Label rdvLabel = new Label(rdv.getNom() != null ? rdv.getNom() : "");
            rdvLabel.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 2; -fx-border-radius: 3;");
            rdvLabel.setMaxWidth(120);
            rdvLabel.setWrapText(true);
            rdvContainer.getChildren().add(rdvLabel);
        }

        dayBox.getChildren().add(rdvContainer);
    }

    private void updateMonthYear(ComboBox<String> monthBox, ComboBox<Integer> yearBox, Consumer<LocalDate> updateFn) {
        int month = monthBox.getSelectionModel().getSelectedIndex() + 1;
        int year = yearBox.getValue();
        updateFn.accept(LocalDate.of(year, month, 1));
    }

    private List<String> getMonthNames() {
        return Arrays.stream(Month.values())
                .map(m -> m.getDisplayName(TextStyle.FULL, Locale.FRENCH))
                .collect(Collectors.toList());
    }

    private List<Integer> getYears(int start, int end) {
        List<Integer> years = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            years.add(year);
        }
        return years;
    }
}