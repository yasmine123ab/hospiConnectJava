package org.hospiconnect.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.hospiconnect.model.Consultation;
import org.hospiconnect.service.ConsultationService;
import org.hospiconnect.utils.RatingBar;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultationFormController implements Initializable {

    public enum FormMode {
        ADD, EDIT
    }

    @FXML private Label formTitleLabel;
    @FXML private TextField typeField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    
    private RatingBar ratingBar; // Rating control
    private TextField emailField; // Email field for notifications
    
    private ConsultationService consultationService;
    private Consultation consultation;
    private FormMode currentMode = FormMode.ADD;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        consultationService = new ConsultationService();
        
        // Initialize rating bar
        ratingBar = new RatingBar(5);
        
        // Initialize email field
        emailField = new TextField();
        
        // Add rating bar and email field to the form (after noteArea in GridPane)
        noteArea.getParent().getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof GridPane)
                .findFirst()
                .ifPresent(gridPane -> {
                    GridPane grid = (GridPane) gridPane;
                    
                    // Add rating label and control
                    Label ratingLabel = new Label("Rating:");
                    ratingLabel.getStyleClass().add("form-label");
                    grid.add(ratingLabel, 0, 5);
                    grid.add(ratingBar, 1, 5);
                    
                    // Add email label and control
                    Label emailLabel = new Label("Email:");
                    emailLabel.getStyleClass().add("form-label");
                    grid.add(emailLabel, 0, 6);
                    grid.add(emailField, 1, 6);
                });
        
        clearForm();
    }
    
    public void setMode(FormMode mode) {
        this.currentMode = mode;
        
        if (mode == FormMode.ADD) {
            formTitleLabel.setText("Add New Consultation");
            saveButton.setText("Save");
            clearForm();
        } else {
            formTitleLabel.setText("Edit Consultation");
            saveButton.setText("Update");
        }
    }
    
    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
        populateForm();
    }
    
    private void populateForm() {
        if (consultation != null) {
            typeField.setText(consultation.getTypeConsultation());
            datePicker.setValue(consultation.getDateConsultation());
            noteArea.setText(consultation.getNote());
            firstnameField.setText(consultation.getFirstname());
            lastnameField.setText(consultation.getLastname());
            ratingBar.setRating(consultation.getRating());
            emailField.setText(consultation.getEmail());
        }
    }
    
    @FXML
    private void handleSave() {
        // Validate form
        List<String> validationErrors = validateForm();
        if (!validationErrors.isEmpty()) {
            showValidationErrorAlert(validationErrors);
            return;
        }
        
        // Prepare consultation object
        if (currentMode == FormMode.ADD) {
            consultation = new Consultation();
        }
        
        consultation.setTypeConsultation(typeField.getText().trim());
        consultation.setDateConsultation(datePicker.getValue());
        consultation.setNote(noteArea.getText());
        consultation.setFirstname(firstnameField.getText().trim());
        consultation.setLastname(lastnameField.getText().trim());
        consultation.setRating(ratingBar.getRating());
        consultation.setEmail(emailField.getText().trim());
        
        // Save consultation
        boolean success = consultationService.saveConsultation(consultation);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                    currentMode == FormMode.ADD ? 
                    "Consultation added successfully." : 
                    "Consultation updated successfully.");
            
            // Return to list view
            handleBackToList();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save consultation.");
        }
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    @FXML
    private void handleBackToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("consultation_list_view.fxml"));
            Parent listView = loader.load();
            
            // Get the controller and refresh the data
            ConsultationListController controller = loader.getController();
            controller.refreshTable();
            
            // Get the main content area and load this view
            StackPane contentArea = (StackPane) typeField.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(listView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load list view.");
        }
    }
    
    private void clearForm() {
        typeField.clear();
        datePicker.setValue(LocalDate.now());
        noteArea.clear();
        firstnameField.clear();
        lastnameField.clear();
        emailField.clear();
        if (ratingBar != null) {
            ratingBar.setRating(0);
        }
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
        
        // Validate email
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("Email address is not valid");
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