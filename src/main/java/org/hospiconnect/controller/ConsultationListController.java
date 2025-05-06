package org.hospiconnect.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import org.hospiconnect.model.Consultation;
import org.hospiconnect.service.ConsultationService;
import org.hospiconnect.utils.EmailService;
import org.hospiconnect.utils.RatingBar;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsultationListController implements Initializable {

    @FXML private TableView<Consultation> consultationTable;
    @FXML private TableColumn<Consultation, Integer> idColumn;
    @FXML private TableColumn<Consultation, String> typeColumn;
    @FXML private TableColumn<Consultation, String> dateColumn;
    @FXML private TableColumn<Consultation, String> firstnameColumn;
    @FXML private TableColumn<Consultation, String> lastnameColumn;
    @FXML private TableColumn<Consultation, Integer> ratingColumn;
    
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button sendEmailButton; // New button for sending test emails
    @FXML private ScrollPane scrollPane; // Added for card view
    @FXML private FlowPane cardContainer; // Container for cards
    
    private ConsultationService consultationService;
    private ObservableList<Consultation> consultationList;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        consultationService = new ConsultationService();
        
        // Create UI elements for card view if not defined in FXML
        if (scrollPane == null) {
            scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.getStyleClass().add("edge-to-edge");
            
            cardContainer = new FlowPane();
            cardContainer.setPadding(new Insets(20));
            cardContainer.setHgap(20);
            cardContainer.setVgap(20);
            cardContainer.setPrefWrapLength(800); // Preferred width before wrapping
            
            scrollPane.setContent(cardContainer);
        }
        
        // Add card view to parent if we can find the parent container
        VBox parentContainer = null;
        if (consultationTable != null) {
            parentContainer = (VBox) consultationTable.getParent();
        }
        
        if (parentContainer != null) {
            // Remove table view
            if (parentContainer.getChildren().contains(consultationTable)) {
                parentContainer.getChildren().remove(consultationTable);
            }
            // Add card view
            if (!parentContainer.getChildren().contains(scrollPane)) {
                parentContainer.getChildren().add(scrollPane);
            }
        }
        
        // Create a "Send Test Email" button if it doesn't exist
        if (sendEmailButton == null && cardContainer != null) {
            sendEmailButton = new Button("Send Test Email");
            sendEmailButton.getStyleClass().addAll("button", "action-button");
            sendEmailButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
            
            // Add the button to a container at the top of the view
            HBox buttonBar = new HBox(10);
            buttonBar.setPadding(new Insets(10));
            buttonBar.setAlignment(Pos.CENTER_RIGHT);
            buttonBar.getChildren().add(sendEmailButton);
            
            // Find parent container to add the button bar
            if (scrollPane != null && scrollPane.getParent() instanceof VBox) {
                parentContainer = (VBox) scrollPane.getParent();
                
                // Insert at the top, before the scroll pane
                int scrollPaneIndex = parentContainer.getChildren().indexOf(scrollPane);
                if (scrollPaneIndex >= 0) {
                    parentContainer.getChildren().add(scrollPaneIndex, buttonBar);
                }
            }
            
            // Set action for the email button
            sendEmailButton.setOnAction(e -> handleSendTestEmail());
        }
        
        // We still need to load columns for selection logic if table exists
        if (consultationTable != null) {
            loadTableColumns();
            
            // Hide the table but keep it for internal data management
            consultationTable.setVisible(false);
            consultationTable.setManaged(false);
        }
        
        loadConsultations();
        setupListeners();
    }
    
    private void loadConsultations() {
        consultationList = FXCollections.observableArrayList(consultationService.getAllConsultations());
        
        // Only set items if the table exists
        if (consultationTable != null) {
            consultationTable.setItems(consultationList);
        }
        
        refreshCardView();
    }
    
    private void refreshCardView() {
        cardContainer.getChildren().clear();
        
        for (Consultation consultation : consultationList) {
            VBox card = createConsultationCard(consultation);
            cardContainer.getChildren().add(card);
        }
    }
    
    private VBox createConsultationCard(Consultation consultation) {
        // Create the main card container
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3); -fx-background-radius: 8;");
        card.setPrefWidth(300);
        card.getStyleClass().add("consultation-card");
        
        // Create and style consultation type header
        Label typeLabel = new Label(consultation.getTypeConsultation());
        typeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3498db;");
        
        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = consultation.getDateConsultation().format(formatter);
        
        Label dateLabel = new Label("Date: " + formattedDate);
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Patient name
        Label nameLabel = new Label(consultation.getFirstname() + " " + consultation.getLastname());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add elements to the card
        card.getChildren().add(typeLabel);
        card.getChildren().add(dateLabel);
        card.getChildren().add(nameLabel);
        
        // Add email information to the card if available
        String email = consultation.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            Label emailLabel = new Label("Email: " + email);
            emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            card.getChildren().add(emailLabel);
        }
        
        // Notes (if available)
        String noteText = consultation.getNote();
        if (noteText != null && !noteText.isEmpty()) {
            // Truncate long notes
            if (noteText.length() > 100) {
                noteText = noteText.substring(0, 97) + "...";
            }
            Label noteLabel = new Label("Notes: " + noteText);
            noteLabel.setWrapText(true);
            noteLabel.setStyle("-fx-font-size: 14px;");
            card.getChildren().add(noteLabel);
        }
        
        card.getChildren().add(new Separator());
        
        // Create stars for rating
        HBox ratingContainer = new HBox(5);
        ratingContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label ratingLabel = new Label("Rating: ");
        ratingLabel.setStyle("-fx-font-size: 14px;");
        
        // Create star rating display
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < consultation.getRating(); i++) {
            stars.append("★"); // Filled star
        }
        for (int i = consultation.getRating(); i < 5; i++) {
            stars.append("☆"); // Empty star
        }
        
        Label starsLabel = new Label(stars.toString());
        // Set color based on rating
        if (consultation.getRating() >= 4) {
            starsLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else if (consultation.getRating() >= 3) {
            starsLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 16px;"); // Orange
        } else if (consultation.getRating() > 0) {
            starsLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px;"); // Red
        } else {
            starsLabel.setText("Not rated");
            starsLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        }
        
        ratingContainer.getChildren().addAll(ratingLabel, starsLabel);
        
        // Action buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        
        Button rateButton = new Button("Rate");
        rateButton.getStyleClass().addAll("button", "action-button");
        rateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        rateButton.setOnAction(event -> showRatingDialog(consultation));
        
        Button editButton = new Button("Edit");
        editButton.getStyleClass().addAll("button", "action-button");
        editButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        editButton.setOnAction(event -> {
            if (consultationTable != null) {
                consultationTable.getSelectionModel().select(consultation);
            }
            handleEdit();
        });
        
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("button", "action-button", "delete-button");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> {
            if (consultationTable != null) {
                consultationTable.getSelectionModel().select(consultation);
            }
            handleDelete();
        });
        
        // Create the email button
        Button emailButton = new Button("Send Email");
        emailButton.getStyleClass().addAll("button", "action-button");
        emailButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        emailButton.setDisable(email == null || email.trim().isEmpty());
        emailButton.setOnAction(event -> {
            if (consultationTable != null) {
                consultationTable.getSelectionModel().select(consultation);
            }
            handleSendTestEmail();
        });
        
        buttonContainer.getChildren().addAll(rateButton, editButton, deleteButton, emailButton);
        
        // Add remaining elements to the card
        card.getChildren().add(ratingContainer);
        card.getChildren().add(buttonContainer);
        
        return card;
    }

    private void loadTableColumns() {
        // Only proceed if columns are properly injected
        if (idColumn == null || typeColumn == null || dateColumn == null || 
            firstnameColumn == null || lastnameColumn == null || ratingColumn == null) {
            return;
        }
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeConsultation"));
        
        // Fix the date column formatting
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateConsultation();
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return new SimpleStringProperty(date.format(formatter));
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        
        // Apply custom row height to all columns to prevent text cutting off
        idColumn.setCellFactory(col -> createFixedHeightCell());
        typeColumn.setCellFactory(col -> createFixedHeightCell());
        dateColumn.setCellFactory(col -> createFixedHeightCell());
        firstnameColumn.setCellFactory(col -> createFixedHeightCell());
        lastnameColumn.setCellFactory(col -> createFixedHeightCell());
        
        // Configure rating column with stars and rate button
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingColumn.setCellFactory(column -> {
            TableCell<Consultation, Integer> cell = new TableCell<Consultation, Integer>() {
                private final Button rateButton = new Button("Rate");
                
                {
                    // Set up the rate button
                    rateButton.getStyleClass().add("button");
                    rateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                    rateButton.setOnAction(event -> {
                        Consultation consultation = getTableView().getItems().get(getIndex());
                        showRatingDialog(consultation);
                    });
                }
                
                @Override
                protected void updateItem(Integer rating, boolean empty) {
                    super.updateItem(rating, empty);
                    
                    if (empty || rating == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Convert rating to stars
                        StringBuilder stars = new StringBuilder();
                        for (int i = 0; i < rating; i++) {
                            stars.append("★"); // Filled star
                        }
                        for (int i = rating; i < 5; i++) {
                            stars.append("☆"); // Empty star
                        }
                        
                        // Show stars with a rate button
                        HBox container = new HBox(5);
                        container.setAlignment(Pos.CENTER);
                        
                        Label starsLabel = new Label(stars.toString());
                        // Set text color based on rating
                        if (rating >= 4) {
                            starsLabel.setStyle("-fx-text-fill: green;");
                        } else if (rating >= 3) {
                            starsLabel.setStyle("-fx-text-fill: #f39c12;"); // Orange
                        } else if (rating > 0) {
                            starsLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red
                        } else {
                            starsLabel.setText("Not rated");
                            starsLabel.setStyle("-fx-text-fill: gray;");
                        }
                        
                        container.getChildren().addAll(starsLabel, rateButton);
                        setGraphic(container);
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            };
            
            // Set fixed cell height
            cell.setPrefHeight(30);
            return cell;
        });
    }
    
    /**
     * Creates table cells with fixed height to prevent text from being cut off
     */
    private <T> TableCell<Consultation, T> createFixedHeightCell() {
        TableCell<Consultation, T> cell = new TableCell<Consultation, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-alignment: CENTER-LEFT;");
                }
            }
        };
        
        // Set fixed cell height
        cell.setPrefHeight(30);
        return cell;
    }
    
    private void setupListeners() {
        // Only set up listeners if the table exists
        if (consultationTable != null) {
            consultationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                editButton.setDisable(!hasSelection);
                deleteButton.setDisable(!hasSelection);
            });
        }
    }
    
    @FXML
    private void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("consultation_form_view.fxml"));
            Parent formView = loader.load();
            
            // Get the controller and set it up for adding a new consultation
            ConsultationFormController controller = loader.getController();
            controller.setMode(ConsultationFormController.FormMode.ADD);
            
            // Get the main content area and load this view
            StackPane contentArea = (StackPane) scrollPane.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(formView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load form view.");
        }
    }
    
    @FXML
    private void handleEdit() {
        // Find the selected consultation either from table or a card
        Consultation selectedConsultation = consultationTable != null ? 
            consultationTable.getSelectionModel().getSelectedItem() : null;
            
        if (selectedConsultation != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("consultation_form_view.fxml"));
                Parent formView = loader.load();
                
                // Get the controller and set it up for editing
                ConsultationFormController controller = loader.getController();
                controller.setMode(ConsultationFormController.FormMode.EDIT);
                controller.setConsultation(selectedConsultation);
                
                // Get the main content area and load this view
                StackPane contentArea = (StackPane) scrollPane.getScene().lookup("#contentArea");
                contentArea.getChildren().clear();
                contentArea.getChildren().add(formView);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load form view.");
            }
        }
    }
    
    @FXML
    private void handleDelete() {
        Consultation selectedConsultation = consultationTable != null ? 
            consultationTable.getSelectionModel().getSelectedItem() : null;
            
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
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Consultation deleted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete consultation.");
                }
            }
        }
    }
    
    @FXML
    private void handleSendTestEmail() {
        // Find the selected consultation
        Consultation selectedConsultation = consultationTable != null ? 
            consultationTable.getSelectionModel().getSelectedItem() : null;
            
        if (selectedConsultation == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a consultation to send a test email.");
            return;
        }
        
        // Check if the consultation has an email address
        if (selectedConsultation.getEmail() == null || selectedConsultation.getEmail().trim().isEmpty()) {
            // Ask user if they want to edit the consultation to add an email
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("No Email Address");
            alert.setHeaderText("The selected consultation doesn't have an email address");
            alert.setContentText("Would you like to edit this consultation to add an email address?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                handleEdit(); // Open the edit form for this consultation
            }
            return;
        }
        
        // Send the test email
        boolean success = EmailService.sendTestReminderEmail(selectedConsultation);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Email Sent", 
                     "Test email sent successfully to " + selectedConsultation.getEmail());
        } else {
            showAlert(Alert.AlertType.ERROR, "Email Error", 
                     "Failed to send test email. Please check your email configuration.");
        }
    }
    
    public void refreshTable() {
        loadConsultations();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show a dialog to rate a consultation
     */
    private void showRatingDialog(Consultation consultation) {
        // Create the custom dialog
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Rate Consultation");
        dialog.setHeaderText("Rate this consultation with " + consultation.getFirstname() + " " + consultation.getLastname());
        
        // Set the button types (OK and Cancel)
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        
        // Create the rating control
        RatingBar ratingBar = new RatingBar(5);
        ratingBar.setRating(consultation.getRating()); // Set current rating
        
        // Create and set the dialog content
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        
        Label instructionLabel = new Label("Select your rating:");
        
        content.getChildren().addAll(
            instructionLabel,
            ratingBar
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Request focus on the rating bar by default
        dialog.setOnShown(e -> ratingBar.requestFocus());
        
        // Convert the result when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return ratingBar.getRating();
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<Integer> result = dialog.showAndWait();
        
        result.ifPresent(rating -> {
            // Update the consultation's rating
            consultation.setRating(rating);
            
            // Save the updated consultation to the database
            boolean success = consultationService.saveConsultation(consultation);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Rating saved successfully!");
                refreshTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save rating.");
            }
        });
    }
}