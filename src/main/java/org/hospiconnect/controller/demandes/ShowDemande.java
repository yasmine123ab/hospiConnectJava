package org.hospiconnect.controller.demandes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.service.DemandeDonService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowDemande {

    @FXML
    private VBox demandeListContainer;
    @FXML
    private Button addDonButton;

    private final DemandeDonService demandeService = new DemandeDonService();
    @FXML
    public void handleFaireUnDonClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/ShowDon.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleFaireUneDemandeClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/ShowDemande.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleFaireUneAttributionClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attributions/ShowAttribution.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize() {
        try {
            List<DemandesDons> demandeList = demandeService.findAll();

            for (DemandesDons demande : demandeList) {
                VBox card = createDemandeCard(demande);
                demandeListContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            showErrorAlert("Erreur de récupération", "Erreur lors de la récupération des demandes : " + e.getMessage());
        }
        // Ajouter l'écouteur d'événement pour le bouton "Ajouter Don"
        addDonButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/AddDemande.fxml"));
                Parent root = loader.load();

                // Obtenir la scène actuelle à partir du bouton
                Stage stage = (Stage) addDonButton.getScene().getWindow();

                // Remplacer le contenu de la scène avec la nouvelle page
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        DemandesDons demande = (DemandesDons) sourceButton.getUserData();

        try {
            demandeService.delete(demande);
            demandeListContainer.getChildren().removeIf(node ->
                    node instanceof VBox && node.getId().equals("demande-card-" + demande.getId()));

            showSuccessAlert("Succès", "La demande a été supprimée.");
        } catch (SQLException e) {
            showErrorAlert("Erreur de suppression", "Erreur lors de la suppression de la demande : " + e.getMessage());
        }
    }

    private VBox createDemandeCard(DemandesDons demande) {
        VBox card = new VBox(8);
        card.setStyle("""
        -fx-padding: 10;
        -fx-border-color: #cccccc;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        -fx-background-color: #f9f9f9;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
    """);

        card.setId("demande-card-" + demande.getId());

        String nomPatient = (demande.getPatient() != null)
                ? demande.getPatient().getNom() + " " + demande.getPatient().getPrenom()
                : "Inconnu";
        Label patientLabel = new Label("Patient : " + nomPatient);
        Label typeLabel = new Label("Type de Besoin : " + demande.getTypeBesoin());
        Label detailsLabel = new Label("Détails : " + demande.getDetails());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = dateFormat.format(demande.getDateDemande());
        Label dateLabel = new Label("Date : " + formattedDate);

        Label statutLabel = new Label("Statut : " + demande.getStatut());

        // Mise en gras pour le type de besoin
        typeLabel.setStyle("-fx-font-size: 13;");

        patientLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");
        detailsLabel.setStyle("-fx-font-size: 13;");
        dateLabel.setStyle("-fx-font-size: 13;");
        statutLabel.setStyle("-fx-font-size: 13;");

        HBox buttonContainer = new HBox(10);
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        // Appliquer un style CSS pour le bouton "Modifier" (vert)
        modifyButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

// Appliquer un style CSS pour le bouton "Supprimer" (rouge)
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        modifyButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/ModifyDemande.fxml"));
                BorderPane modifyPage = loader.load();

                ModifyDemande modifyController = loader.getController();
                modifyController.initialize(demande);

                Stage modifyStage = new Stage();
                modifyStage.setTitle("Modifier la Demande");
                modifyStage.setScene(new Scene(modifyPage));
                modifyStage.show();
            } catch (IOException e) {
                showErrorAlert("Erreur de chargement", "Erreur lors du chargement de la page de modification : " + e.getMessage());
            }
        });

        deleteButton.setUserData(demande);
        deleteButton.setOnAction(this::handleDelete);

        buttonContainer.getChildren().addAll(modifyButton, deleteButton);

        card.getChildren().addAll(patientLabel, typeLabel, detailsLabel, dateLabel, statutLabel, buttonContainer);
        return card;
    }


    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
