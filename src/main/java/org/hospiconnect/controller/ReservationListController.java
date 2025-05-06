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
import javafx.scene.layout.StackPane;
import org.hospiconnect.model.Reservation;
import org.hospiconnect.service.ReservationService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationListController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> dureeColumn;
    @FXML private TableColumn<Reservation, String> medicamentColumn;
    @FXML private TableColumn<Reservation, Integer> foisColumn;
    @FXML private TableColumn<Reservation, String> noteColumn; // Add this new column for notes
    
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    
    private ReservationService reservationService;
    private ObservableList<Reservation> reservationList;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationService = new ReservationService();
        
        // Debug FXML resources
        debugResource("reservation_form_view.fxml");
        
        // Safely set the row height for the table if it exists
        if (reservationTable != null) {
            reservationTable.setFixedCellSize(40);
            
            loadTableColumns();
            loadReservations();
            setupListeners();
        }
    }
    
    private void loadTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dureeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDureeTraitement();
            if (dateTime != null) {
                // Format date and time in a readable way
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new SimpleStringProperty(dateTime.format(formatter));
            } else {
                return new SimpleStringProperty("");
            }
        });
        medicamentColumn.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        foisColumn.setCellValueFactory(new PropertyValueFactory<>("nombresFois"));
        
        // Set up the note column
        if (noteColumn != null) {
            noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
            noteColumn.setCellFactory(col -> {
                TableCell<Reservation, String> cell = createWrappingCell();
                cell.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 5px; -fx-pref-width: 200px;");
                return cell;
            });
        }
        
        // Apply custom cell factory to all columns
        idColumn.setCellFactory(col -> createWrappingCell());
        dureeColumn.setCellFactory(col -> createWrappingCell());
        medicamentColumn.setCellFactory(col -> createWrappingCell());
        foisColumn.setCellFactory(col -> createWrappingCell());
    }
    
    /**
     * Creates table cells with proper text wrapping and fixed height to prevent text clipping
     */
    private <T> TableCell<Reservation, T> createWrappingCell() {
        TableCell<Reservation, T> cell = new TableCell<Reservation, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setWrapText(true);
                    setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 5px;");
                }
            }
        };
        
        // Ensure the cell height is sufficient
        cell.setPrefHeight(40);
        return cell;
    }
    
    private void loadReservations() {
        reservationList = FXCollections.observableArrayList(reservationService.getAllReservations());
        reservationTable.setItems(reservationList);
    }
    
    private void setupListeners() {
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
    }
    
    @FXML
    private void handleAddNew() {
        try {
            // Explicitly check resource exists
            URL resourceUrl = getClass().getClassLoader().getResource("reservation_form_view.fxml");
            if (resourceUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Resource Not Found", 
                        "Could not find reservation_form_view.fxml in the classpath");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            
            // Set special error handler to get more details about FXML loading errors
            loader.setBuilderFactory(new javafx.util.BuilderFactory() {
                @Override
                public javafx.util.Builder<?> getBuilder(Class<?> type) {
                    return null; // Default behavior
                }
            });
            
            // Load the view with more detailed error catching
            Parent formView;
            try {
                formView = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("FXML Loading Error Details: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("Caused by: " + e.getCause().getMessage());
                    e.getCause().printStackTrace();
                }
                showAlert(Alert.AlertType.ERROR, "FXML Loading Error", 
                        "Error details: " + e.getMessage() + 
                        (e.getCause() != null ? "\nCause: " + e.getCause().getMessage() : ""));
                return;
            }
            
            // Get the controller
            ReservationFormController controller = loader.getController();
            controller.setMode(ReservationFormController.FormMode.ADD);
            
            // Get the content area
            StackPane contentArea = (StackPane) reservationTable.getScene().lookup("#contentArea");
            if (contentArea == null) {
                showAlert(Alert.AlertType.ERROR, "UI Error", "Content area not found");
                return;
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(formView);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", 
                    "Could not load form view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEdit() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                URL resourceUrl = getClass().getClassLoader().getResource("reservation_form_view.fxml");
                if (resourceUrl == null) {
                    showAlert(Alert.AlertType.ERROR, "Resource Not Found", 
                            "Could not find reservation_form_view.fxml in the classpath");
                    return;
                }
                
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent formView = loader.load();
                
                // Get the controller
                ReservationFormController controller = loader.getController();
                controller.setMode(ReservationFormController.FormMode.EDIT);
                controller.setReservation(selectedReservation);
                
                // Get the content area
                StackPane contentArea = (StackPane) reservationTable.getScene().lookup("#contentArea");
                if (contentArea == null) {
                    showAlert(Alert.AlertType.ERROR, "UI Error", "Content area not found");
                    return;
                }
                
                contentArea.getChildren().clear();
                contentArea.getChildren().add(formView);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Loading Error", 
                        "Could not load form view: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleDelete() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
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
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation deleted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reservation.");
                }
            }
        }
    }
    
    @FXML
    private void handleBackToList() {
        try {
            // Create a new instance of this controller's view
            URL resource = getClass().getClassLoader().getResource("reservation_list_view.fxml");
            if (resource == null) {
                showAlert(Alert.AlertType.ERROR, "Resource Error", 
                        "Could not find reservation_list_view.fxml resource.");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent listView = loader.load();
            
            StackPane contentArea = (StackPane) reservationTable.getScene().lookup("#contentArea");
            if (contentArea == null) {
                showAlert(Alert.AlertType.ERROR, "UI Error", "Could not find content area in the scene.");
                return;
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(listView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", 
                    "Could not load list view: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Unexpected error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("reservation_form_view.fxml");
            if (resourceUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Resource Not Found", 
                        "Could not find reservation_form_view.fxml in the classpath");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent formView = loader.load();
            
            // Get the controller
            ReservationFormController controller = loader.getController();
            controller.clearForm();
            
            // Get the content area
            StackPane contentArea = (StackPane) reservationTable.getScene().lookup("#contentArea");
            if (contentArea == null) {
                showAlert(Alert.AlertType.ERROR, "UI Error", "Content area not found");
                return;
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(formView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", 
                    "Could not load form view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handlePrintCertificate() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                // Create loader with explicit resource path
                URL resource = getClass().getClassLoader().getResource("reservation_form_view.fxml");
                if (resource == null) {
                    showAlert(Alert.AlertType.ERROR, "Resource Error", 
                            "Could not find reservation_form_view.fxml resource.");
                    return;
                }
                
                FXMLLoader loader = new FXMLLoader(resource);
                Parent formView = loader.load();
                
                ReservationFormController controller = loader.getController();
                if (controller != null) {
                    controller.setReservation(selectedReservation);
                    
                    // Show information alert about printing
                    showAlert(Alert.AlertType.INFORMATION, "Print Certificate", 
                             "Certificate for " + selectedReservation.getNomMedicament() + 
                             " would be printed here.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Controller Error", 
                            "Could not access form controller.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Loading Error", 
                        "Could not load form view: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", 
                        "Error generating certificate: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a reservation to print a certificate for.");
        }
    }
    
    @FXML
    private void handleSave() {
        // This handler might be referenced in reservation_form_view.fxml
        showAlert(Alert.AlertType.INFORMATION, "Save Action", 
                 "Save action detected in list controller. This should normally be handled by the form controller.");
    }
    
    @FXML
    private void handleGenerateQRCode() {
        // This handler might be referenced in reservation_form_view.fxml
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            showAlert(Alert.AlertType.INFORMATION, "Generate QR Code", 
                     "QR Code generation for " + selectedReservation.getNomMedicament() + 
                     " would be handled here.");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a reservation to generate a QR code for.");
        }
    }
    
    @FXML
    private void handleGenerateNotes() {
        // This handler might be referenced in reservation_form_view.fxml
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            showAlert(Alert.AlertType.INFORMATION, "Generate Notes", 
                     "AI-generated notes for " + selectedReservation.getNomMedicament() + 
                     " would be created here.");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a reservation to generate notes for.");
        }
    }
    
    @FXML
    private void handlePrintQRCode() {
        // This handler might be referenced in reservation_form_view.fxml
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            showAlert(Alert.AlertType.INFORMATION, "Print QR Code", 
                     "QR Code for " + selectedReservation.getNomMedicament() + 
                     " would be printed here.");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a reservation to print a QR code for.");
        }
    }
    
    public void refreshTable() {
        loadReservations();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Add a debug method to help with troubleshooting
    private void debugResourceLoading(String resourcePath) {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            System.out.println("ERROR: Resource not found: " + resourcePath);
        } else {
            System.out.println("SUCCESS: Resource found: " + resource.toString());
        }
    }
    
    // Helper method to debug resource loading issues
    private void debugResource(String resourceName) {
        URL resource = getClass().getClassLoader().getResource(resourceName);
        System.out.println("Resource " + resourceName + ": " + (resource != null ? "FOUND" : "NOT FOUND"));
        if (resource != null) {
            System.out.println("Resource path: " + resource.getPath());
        }
    }
    
    /**
     * Helper method to load FXML with detailed error handling
     */
    private Parent loadFXML(String fxmlPath) throws IOException {
        URL resourceUrl = getClass().getClassLoader().getResource(fxmlPath);
        if (resourceUrl == null) {
            throw new IOException("Resource not found: " + fxmlPath);
        }
        
        FXMLLoader loader = new FXMLLoader(resourceUrl);
        try {
            return loader.load();
        } catch (IOException e) {
            System.err.println("FXML Loading Error in " + fxmlPath + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            throw e;
        }
    }
}