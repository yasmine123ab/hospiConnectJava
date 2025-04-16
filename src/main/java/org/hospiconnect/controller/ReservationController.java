package org.hospiconnect.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hospiconnect.model.Reservation;
import org.hospiconnect.service.ReservationService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> dureeColumn;
    @FXML private TableColumn<Reservation, String> medicamentColumn;
    @FXML private TableColumn<Reservation, Integer> foisColumn;
    
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourComboBox;
    @FXML private ComboBox<String> minuteComboBox;
    @FXML private TextField medicamentField;
    @FXML private Spinner<Integer> foisSpinner;
    
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    
    private ReservationService reservationService;
    private ObservableList<Reservation> reservationList;
    private Reservation selectedReservation;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationService = new ReservationService();
        
        setupControls();
        loadTableColumns();
        loadReservations();
        setupListeners();
        clearForm();
    }
    
    private void setupControls() {

        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourComboBox.setItems(hours);
        

        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            minutes.add(String.format("%02d", i));
        }
        minuteComboBox.setItems(minutes);
        

        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        foisSpinner.setValueFactory(valueFactory);
    }
    
    private void loadTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dureeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDureeTraitement();
            return new SimpleStringProperty(dateTime != null ? dateTime.toString() : "");
        });
        medicamentColumn.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        foisColumn.setCellValueFactory(new PropertyValueFactory<>("nombresFois"));
    }
    
    private void loadReservations() {
        reservationList = FXCollections.observableArrayList(reservationService.getAllReservations());
        reservationTable.setItems(reservationList);
    }
    
    private void setupListeners() {
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReservation = newSelection;
                populateForm(selectedReservation);
                deleteButton.setDisable(false);
            } else {
                deleteButton.setDisable(true);
            }
        });
    }
    
    private void populateForm(Reservation reservation) {
        LocalDateTime dateTime = reservation.getDureeTraitement();
        if (dateTime != null) {
            datePicker.setValue(dateTime.toLocalDate());
            hourComboBox.setValue(String.format("%02d", dateTime.getHour()));
            minuteComboBox.setValue(String.format("%02d", dateTime.getMinute()));
        }
        medicamentField.setText(reservation.getNomMedicament());
        foisSpinner.getValueFactory().setValue(reservation.getNombresFois());
    }
    
    @FXML
    private void handleSave() {

        List<String> validationErrors = validateForm();
        if (!validationErrors.isEmpty()) {
            showValidationErrorAlert(validationErrors);
            return;
        }
        
        Reservation reservation;
        if (selectedReservation != null) {
            reservation = selectedReservation;
        } else {
            reservation = new Reservation();
        }
        

        LocalDateTime dateTime = datePicker.getValue().atTime(
            Integer.parseInt(hourComboBox.getValue()),
            Integer.parseInt(minuteComboBox.getValue()),
            0, 0
        );
        
        reservation.setDureeTraitement(dateTime);
        reservation.setNomMedicament(medicamentField.getText());
        reservation.setNombresFois(foisSpinner.getValue());
        
        boolean success = reservationService.saveReservation(reservation);
        
        if (success) {
            loadReservations();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation saved successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save reservation.");
        }
    }
    
    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();
        

        if (datePicker.getValue() == null) {
            errors.add("Treatment date is required");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.add("Treatment date cannot be in the past");
        }
        

        if (hourComboBox.getValue() == null) {
            errors.add("Hour is required");
        }
        
        if (minuteComboBox.getValue() == null) {
            errors.add("Minute is required");
        }
        
        // Validate medication name
        if (medicamentField.getText().trim().isEmpty()) {
            errors.add("Medication name is required");
        } else if (medicamentField.getText().trim().length() < 2) {
            errors.add("Medication name must be at least 2 characters");
        } else if (!medicamentField.getText().trim().matches("[a-zA-Z0-9\\s-]{2,50}")) {
            errors.add("Medication name contains invalid characters");
        }
        

        if (foisSpinner.getValue() == null) {
            errors.add("Times per day is required");
        } else if (foisSpinner.getValue() <= 0) {
            errors.add("Times per day must be greater than 0");
        }
        

        if (datePicker.getValue() != null && hourComboBox.getValue() != null && minuteComboBox.getValue() != null) {
            LocalDateTime selectedDateTime = datePicker.getValue().atTime(
                Integer.parseInt(hourComboBox.getValue()),
                Integer.parseInt(minuteComboBox.getValue()),
                0, 0
            );
            
            if (selectedDateTime.isBefore(LocalDateTime.now())) {
                errors.add("The treatment date and time must be in the future");
            }
        }
        
        return errors;
    }
    
    private void showValidationErrorAlert(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please correct the following errors:");
        

        StringBuilder errorContent = new StringBuilder();
        for (String error : errors) {
            errorContent.append("• ").append(error).append("\n");
        }
        
        alert.setContentText(errorContent.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    @FXML
    private void handleDelete() {
        if (selectedReservation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Reservation");
            alert.setContentText("Are you sure you want to delete this reservation?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = reservationService.deleteReservation(selectedReservation.getId());
                
                if (success) {
                    loadReservations();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation deleted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reservation.");
                }
            }
        }
    }
    
    private void clearForm() {
        selectedReservation = null;
        datePicker.setValue(LocalDateTime.now().toLocalDate());
        hourComboBox.setValue(String.format("%02d", LocalDateTime.now().getHour()));
        minuteComboBox.setValue(String.format("%02d", LocalDateTime.now().getMinute()));
        medicamentField.clear();
        foisSpinner.getValueFactory().setValue(1);
        deleteButton.setDisable(true);
        reservationTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
