package org.hospiconnect.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hospiconnect.model.Consultation;
import org.hospiconnect.service.ConsultationService;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsultationController implements Initializable {

    @FXML private TableView<Consultation> consultationTable;
    @FXML private TableColumn<Consultation, Integer> idColumn;
    @FXML private TableColumn<Consultation, String> typeColumn;
    @FXML private TableColumn<Consultation, String> dateColumn;
    @FXML private TableColumn<Consultation, String> firstnameColumn;
    @FXML private TableColumn<Consultation, String> lastnameColumn;
    
    @FXML private TextField typeField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    
    private ConsultationService consultationService;
    private ObservableList<Consultation> consultationList;
    private Consultation selectedConsultation;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        consultationService = new ConsultationService();
        loadTableColumns();
        loadConsultations();
        setupListeners();
        clearForm();
    }
    
    private void loadTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeConsultation"));
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateConsultation();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
    }
    
    private void loadConsultations() {
        consultationList = FXCollections.observableArrayList(consultationService.getAllConsultations());
        consultationTable.setItems(consultationList);
    }
    
    private void setupListeners() {
        consultationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedConsultation = newSelection;
                populateForm(selectedConsultation);
                deleteButton.setDisable(false);
            } else {
                deleteButton.setDisable(true);
            }
        });
    }
    
    private void populateForm(Consultation consultation) {
        typeField.setText(consultation.getTypeConsultation());
        datePicker.setValue(consultation.getDateConsultation());
        noteArea.setText(consultation.getNote());
        firstnameField.setText(consultation.getFirstname());
        lastnameField.setText(consultation.getLastname());
    }
    
    @FXML
    private void handleSave() {
        // Validate form and collect validation errors
        List<String> validationErrors = validateForm();
        if (!validationErrors.isEmpty()) {
            showValidationErrorAlert(validationErrors);
            return;
        }
        
        Consultation consultation;
        if (selectedConsultation != null) {
            consultation = selectedConsultation;
        } else {
            consultation = new Consultation();
        }
        
        consultation.setTypeConsultation(typeField.getText());
        consultation.setDateConsultation(datePicker.getValue());
        consultation.setNote(noteArea.getText());
        consultation.setFirstname(firstnameField.getText());
        consultation.setLastname(lastnameField.getText());
        
        boolean success = consultationService.saveConsultation(consultation);
        
        if (success) {
            loadConsultations();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Consultation saved successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save consultation.");
        }
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    @FXML
    private void handleDelete() {
        if (selectedConsultation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Consultation");
            alert.setContentText("Are you sure you want to delete this consultation?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = consultationService.deleteConsultation(selectedConsultation.getId());
                
                if (success) {
                    loadConsultations();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Consultation deleted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete consultation.");
                }
            }
        }
    }
    
    private void clearForm() {
        selectedConsultation = null;
        typeField.clear();
        datePicker.setValue(LocalDate.now());
        noteArea.clear();
        firstnameField.clear();
        lastnameField.clear();
        deleteButton.setDisable(true);
        consultationTable.getSelectionModel().clearSelection();
    }
    
    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();
        
        // Validate type
        if (typeField.getText().trim().isEmpty()) {
            errors.add("Consultation type is required");
        } else if (typeField.getText().trim().length() < 2) {
            errors.add("Consultation type must be at least 2 characters");
        }
        
        // Validate date
        if (datePicker.getValue() == null) {
            errors.add("Consultation date is required");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.add("Consultation date cannot be in the past");
        }
        
        // Validate first name
        if (firstnameField.getText().trim().isEmpty()) {
            errors.add("First name is required");
        } else if (!firstnameField.getText().trim().matches("[a-zA-Z\\s-]{2,30}")) {
            errors.add("First name must contain only letters, spaces, or hyphens (2-30 characters)");
        }
        
        // Validate last name
        if (lastnameField.getText().trim().isEmpty()) {
            errors.add("Last name is required");
        } else if (!lastnameField.getText().trim().matches("[a-zA-Z\\s-]{2,30}")) {
            errors.add("Last name must contain only letters, spaces, or hyphens (2-30 characters)");
        }
        
        // Optional validation for notes (if entered)
        if (noteArea.getText() != null && noteArea.getText().length() > 1000) {
            errors.add("Notes cannot exceed 1000 characters");
        }
        
        return errors;
    }
    
    private void showValidationErrorAlert(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please correct the following errors:");
        
        // Create formatted content with bullet points for each error
        StringBuilder errorContent = new StringBuilder();
        for (String error : errors) {
            errorContent.append("• ").append(error).append("\n");
        }
        
        alert.setContentText(errorContent.toString());
        alert.showAndWait();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
