package org.hospiconnect.controller;

import com.google.zxing.WriterException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.hospiconnect.model.Reservation;
import org.hospiconnect.service.ReservationService;
import org.hospiconnect.utils.MedicationAIHelper;
import org.hospiconnect.utils.QRCodeGenerator;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationFormController implements Initializable {

    public enum FormMode {
        ADD, EDIT
    }

    @FXML private Label formTitleLabel;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private TextField medicamentField;
    @FXML private Spinner<Integer> foisSpinner;
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    
    // QR code components
    private Button generateQRButton;
    private ImageView qrCodeImageView;
    private Button printQRButton;
    
    // Medication notes components
    @FXML private TextArea noteArea; // Ensure this is bound to the FXML-defined TextArea
    private Button generateNotesButton;
    
    private ReservationService reservationService;
    private Reservation reservation;
    private FormMode currentMode = FormMode.ADD;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationService = new ReservationService();
        
        // Initialize date time picker
        datePicker.setValue(LocalDate.now());
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalDateTime.now().getHour()));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getMinute()));
        
        // Set up the frequency spinner for number of times per day
        foisSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        
        // Set properties on the existing noteArea
        if (noteArea != null) {
            noteArea.setWrapText(true);
            noteArea.setPromptText("AI-generated medication notes will appear here. You can edit if needed.");
        }
        
        // Add QR code and other UI elements
        setupQRCodeSection();
    }

    private void setupQRCodeSection() {
        // Add QR code section to the form grid
        GridPane gridPane = (GridPane) datePicker.getParent();
        
        // Create QR code image view
        qrCodeImageView = new ImageView();
        qrCodeImageView.setFitWidth(150);
        qrCodeImageView.setFitHeight(150);
        qrCodeImageView.setPreserveRatio(true);
        
        // Create generate QR button
        generateQRButton = new Button("Generate QR Code");
        generateQRButton.getStyleClass().addAll("button", "action-button");
        generateQRButton.setOnAction(e -> generateQRCode());
        
        // Create print QR button
        printQRButton = new Button("Print QR Code");
        printQRButton.getStyleClass().addAll("button", "action-button");
        printQRButton.setOnAction(e -> printQRCode());
        printQRButton.setDisable(true); // Initially disabled until QR code is generated
        
        // Create a VBox to hold the QR code components
        VBox qrBox = new VBox(10);
        qrBox.setAlignment(Pos.CENTER);
        qrBox.getChildren().addAll(qrCodeImageView, 
                                  new HBox(10, generateQRButton, printQRButton));
        
        // Add QR code title and components to grid - using row 5 instead of 4
        Label qrLabel = new Label("QR Code:");
        qrLabel.getStyleClass().add("form-label");
        gridPane.add(qrLabel, 0, 5);  // Changed from row 4 to row 5
        gridPane.add(qrBox, 1, 5);    // Changed from row 4 to row 5
    }
    
    /**
     * Handles the generation of AI-powered medication notes.
     * This method is bound to the "Generate AI Notes" button in the FXML.
     */
    @FXML
    public void generateMedicationNotes() {
        if (medicamentField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", 
                     "Please enter a medication name first.");
            return;
        }
        
        String medicationName = medicamentField.getText().trim();
        int timesPerDay = foisSpinner.getValue();
        
        try {
            // Show progress indicator
            showAlert(Alert.AlertType.INFORMATION, "Generating Notes", 
                    "Generating medication notes. This may take a moment...");
            
            // Generate AI notes for this medication
            String aiNotes = MedicationAIHelper.generateMedicationNotes(medicationName, timesPerDay);
            
            // Check if notes were successfully generated
            if (aiNotes != null && !aiNotes.isEmpty()) {
                // Display the notes in the text area
                noteArea.setText(aiNotes);
                
                showAlert(Alert.AlertType.INFORMATION, "Notes Generated", 
                         "AI-generated medication notes have been created. You can edit them if needed.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Notes Generation Failed", 
                         "Could not generate notes. The database may not have information about this medication.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            // Provide a user-friendly error message
            showAlert(Alert.AlertType.ERROR, "API Error", 
                     "Could not connect to the AI service. Using local database instead.");
            
            // Fallback to local database directly
            try {
                String fallbackNotes = generateLocalMedicationNotes(medicationName, timesPerDay);
                noteArea.setText(fallbackNotes);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Failed to generate medication notes: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Fallback method to generate medication notes locally without API
     */
    private String generateLocalMedicationNotes(String medicationName, int timesPerDay) {
        StringBuilder notes = new StringBuilder();
        notes.append("MEDICATION: ").append(medicationName).append("\n\n");
        
        // Add basic dosage information
        notes.append("DOSAGE RECOMMENDATION:\n");
        switch (timesPerDay) {
            case 1:
                notes.append("Take once daily, preferably at the same time each day.");
                break;
            case 2:
                notes.append("Take twice daily, once in the morning and once in the evening.");
                break;
            case 3:
                notes.append("Take three times a day, preferably with meals.");
                break;
            case 4:
                notes.append("Take four times a day, preferably with or after meals and at bedtime.");
                break;
            default:
                notes.append("Take ").append(timesPerDay).append(" times daily as directed by your doctor.");
                break;
        }
        
        notes.append("\n\n");
        notes.append("GENERAL PRECAUTIONS:\n");
        notes.append("- Store medication at room temperature away from moisture and heat.\n");
        notes.append("- Keep all medications out of reach of children.\n");
        notes.append("- Take medication as prescribed by your healthcare provider.\n");
        notes.append("- Do not discontinue without consulting your doctor.\n");
        
        return notes.toString();
    }
    
    private void generateQRCode() {
        // Validate required fields for QR code
        List<String> validationErrors = validateForm();
        if (!validationErrors.isEmpty()) {
            showValidationErrorAlert(validationErrors);
            return;
        }
        
        try {
            // Create reservation details for QR code content
            LocalDateTime dateTime = LocalDateTime.of(
                datePicker.getValue(),
                LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
            );
            
            // Format the certificate content
            String certificateContent = createMedicationCertificate(dateTime);
            
            // Generate QR code
            Image qrImage = QRCodeGenerator.generateQRCode(certificateContent, 300);
            qrCodeImageView.setImage(qrImage);
            
            // Enable print button
            printQRButton.setDisable(false);
            
            showAlert(Alert.AlertType.INFORMATION, "QR Code Certificate Generated", 
                     "QR code certificate for the medication has been generated successfully.");
            
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to generate QR code: " + e.getMessage());
        }
    }
    
    private String createMedicationCertificate(LocalDateTime dateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        StringBuilder certificate = new StringBuilder();
        certificate.append("=== OFFICIAL MEDICATION CERTIFICATE ===\n\n");
        
        // Add hospital/clinic information
        certificate.append("HospiConnect Medical Center\n");
        certificate.append("Certificate ID: ").append(System.currentTimeMillis()).append("\n");
        certificate.append("Issue Date: ").append(LocalDate.now().format(dateFormatter)).append("\n\n");
        
        // Add medication information
        certificate.append("MEDICATION DETAILS\n");
        certificate.append("Medication: ").append(medicamentField.getText().trim()).append("\n");
        certificate.append("Dosage Frequency: ").append(foisSpinner.getValue()).append(" time(s) per day\n");
        certificate.append("Start Date: ").append(dateTime.toLocalDate().format(dateFormatter)).append("\n");
        certificate.append("Start Time: ").append(dateTime.toLocalTime().format(timeFormatter)).append("\n\n");
        
        // Add medication notes if available
        if (noteArea.getText() != null && !noteArea.getText().trim().isEmpty()) {
            certificate.append("INSTRUCTIONS & NOTES\n");
            certificate.append(noteArea.getText().trim()).append("\n\n");
        }
        
        certificate.append("This is an official medication record. Please follow all instructions carefully.");
        certificate.append("\n\n=== END OF CERTIFICATE ===");
        
        return certificate.toString();
    }
    
    private void printQRCode() {
        if (qrCodeImageView.getImage() == null) {
            showAlert(Alert.AlertType.WARNING, "No QR Code", 
                     "Please generate a QR code first before printing.");
            return;
        }
        
        // Create a printer job for the QR code
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            // Show the print dialog
            boolean proceed = job.showPrintDialog(qrCodeImageView.getScene().getWindow());
            if (proceed) {
                // Print the node
                boolean printed = job.printPage(qrCodeImageView);
                if (printed) {
                    job.endJob();
                    showAlert(Alert.AlertType.INFORMATION, "Print", 
                             "QR code sent to printer. Check your printer queue.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Print Error", 
                             "Printing failed. Please try again.");
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Print Error", 
                     "Could not create printer job. No printers may be installed.");
        }
    }
    
    /**
     * Handles printing a certificate for the current reservation
     */
    @FXML
    public void handlePrintCertificate() {
        if (reservation == null) {
            showAlert(Alert.AlertType.WARNING, "No Data", "Please save the reservation before printing a certificate.");
            return;
        }
        
        try {
            // Create certificate content
            LocalDateTime dateTime = LocalDateTime.of(
                datePicker.getValue(),
                LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
            );
            
            String certificateContent = createMedicationCertificate(dateTime);
            
            // Show a message indicating the certificate would be printed
            showAlert(Alert.AlertType.INFORMATION, "Print Certificate", 
                    "Certificate for " + reservation.getNomMedicament() + " ready to print.\n\n" +
                    "In a production environment, this would send the certificate to a printer.");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Print Error", 
                    "Could not print certificate: " + e.getMessage());
        }
    }
    
    public void setMode(FormMode mode) {
        currentMode = mode;
        
        if (mode == FormMode.ADD) {
            formTitleLabel.setText("Add New Medication Reservation");
            saveButton.setText("Save");
        } else {
            formTitleLabel.setText("Edit Medication Reservation");
            saveButton.setText("Update");
        }
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        populateForm();
    }
    
    private void populateForm() {
        if (reservation != null) {
            LocalDateTime dateTime = reservation.getDureeTraitement();
            if (dateTime != null) {
                datePicker.setValue(dateTime.toLocalDate());
                hourSpinner.getValueFactory().setValue(dateTime.getHour());
                minuteSpinner.getValueFactory().setValue(dateTime.getMinute());
            }
            medicamentField.setText(reservation.getNomMedicament());
            foisSpinner.getValueFactory().setValue(reservation.getNombresFois());
            
            // Load notes if available
            if (reservation.getNote() != null && !reservation.getNote().isEmpty()) {
                noteArea.setText(reservation.getNote());
            }
            
            // Generate QR code for existing reservation
            if (qrCodeImageView != null && generateQRButton != null) {
                generateQRCode();
            }
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
        
        // Prepare reservation object
        if (currentMode == FormMode.ADD) {
            reservation = new Reservation();
        }
        
        // Set values from form
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
        reservation.setDureeTraitement(dateTime);
        reservation.setNomMedicament(medicamentField.getText().trim());
        reservation.setNombresFois(foisSpinner.getValue());
        
        // Make sure to save the notes from the noteArea
        if (noteArea != null) {
            reservation.setNote(noteArea.getText());
        }
        
        // Save reservation
        boolean success = reservationService.saveReservation(reservation);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                    currentMode == FormMode.ADD ? 
                    "Medication reservation added successfully." : 
                    "Medication reservation updated successfully.");
            
            // Return to list view
            handleBackToList();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save medication reservation.");
        }
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    @FXML
    private void handleBackToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("reservation_list_view.fxml"));
            Parent listView = loader.load();
            
            // Get the controller and refresh the data
            ReservationListController controller = loader.getController();
            controller.refreshTable();
            
            // Get the main content area and load this view
            StackPane contentArea = (StackPane) datePicker.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(listView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load list view.");
        }
    }
    
    /**
     * Clears all form fields to their default values
     */
    @FXML
    public void clearForm() {
        datePicker.setValue(LocalDate.now());
        hourSpinner.getValueFactory().setValue(LocalDateTime.now().getHour());
        minuteSpinner.getValueFactory().setValue(LocalDateTime.now().getMinute());
        medicamentField.clear();
        foisSpinner.getValueFactory().setValue(1);
        noteArea.clear();
        
        // Clear QR code
        if (qrCodeImageView != null) {
            qrCodeImageView.setImage(null);
        }
        if (printQRButton != null) {
            printQRButton.setDisable(true);
        }
    }
    
    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();
        
        // Validate date
        if (datePicker.getValue() == null) {
            errors.add("Treatment date is required");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.add("Treatment date cannot be in the past");
        }
        
        // Validate time
        if (hourSpinner.getValue() == null) {
            errors.add("Hour is required");
        }
        
        if (minuteSpinner.getValue() == null) {
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
        
        // Validate times per day
        if (foisSpinner.getValue() == null) {
            errors.add("Times per day is required");
        } else if (foisSpinner.getValue() <= 0) {
            errors.add("Times per day must be greater than 0");
        }
        
        // Check if selected date and time is in the future
        if (datePicker.getValue() != null && hourSpinner.getValue() != null && minuteSpinner.getValue() != null) {
            LocalDateTime selectedDateTime = LocalDateTime.of(
                datePicker.getValue(),
                LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
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