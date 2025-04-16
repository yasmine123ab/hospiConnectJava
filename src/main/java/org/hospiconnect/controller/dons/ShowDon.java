package org.hospiconnect.controller.dons;

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
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Dons;
import org.hospiconnect.service.DonService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowDon {

    @FXML
    private VBox donListContainer;
    @FXML
    private Button menuHomeButton;

    private final DonService donService = new DonService();

    @FXML
    private Button addDonButton;

    @FXML
    public void initialize() {
        try {
            // Charger et afficher les dons
            List<Dons> donList = donService.findAll();

            for (Dons don : donList) {
                VBox card = createDonCard(don);
                donListContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            showErrorAlert("Erreur de récupération", "Erreur lors de la récupération des dons : " + e.getMessage());
        }

        // Ajouter l'écouteur d'événement pour le bouton "Ajouter Don"
        addDonButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/AddDon.fxml"));
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

        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));
    }
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



    // Méthode pour gérer la suppression d'un don
    @FXML
    public void handleDelete(ActionEvent event) {
        // Récupérer l'objet Dons associé au bouton "Supprimer"
        Button sourceButton = (Button) event.getSource();
        Dons don = (Dons) sourceButton.getUserData(); // Récupère l'objet Dons depuis le bouton

        try {
            // Appeler la méthode delete du service avec l'objet 'don'
            donService.delete(don); // Supprimer le don via le service

            // Mettre à jour l'interface graphique en supprimant la carte de don
            donListContainer.getChildren().removeIf(node -> {
                if (node instanceof VBox) {
                    VBox vbox = (VBox) node;
                    return vbox.getId().equals("don-card-" + don.getId()); // Identifier la carte par l'ID
                }
                return false;
            });

            showSuccessAlert("Succès", "Le don a été supprimé.");

        } catch (SQLException e) {
            showErrorAlert("Erreur de suppression", "Erreur lors de la suppression du don : " + e.getMessage());
        }
    }


    private VBox createDonCard(Dons don) {
        VBox card = new VBox(8); // Espacement vertical entre les labels
        card.setStyle("""
            -fx-padding: 10;
            -fx-border-color: #cccccc;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-background-color: #f4f4f4;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);

        // Ajouter un identifiant unique à chaque carte (utilisation de l'ID du don)
        card.setId("don-card-" + don.getId()); // Assurez-vous que 'don.getId()' existe et est unique

        Label typeLabel = new Label("Type : " + don.getTypeDon());
        Label montantLabel = new Label("Montant : " + don.getMontant() + " DNT");
        Label descLabel = new Label("Description : " + don.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = dateFormat.format(don.getDateDon());
        Label dateLabel = new Label("Date : " + formattedDate);

        String donateurName = (don.getDonateur() != null)
                ? don.getDonateur().getNom() + " " + don.getDonateur().getPrenom()
                : "Inconnu";
        Label donateurLabel = new Label("Donateur : " + donateurName);
        String disponibilite = don.getDisponibilite() ? "Disponible" : "Non disponible"; // si c'est un booléen
        Label disponibiliteLabel = new Label("Disponibilité : " + disponibilite);

        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        montantLabel.setStyle("-fx-font-size: 13;");
        descLabel.setStyle("-fx-font-size: 13;");
        dateLabel.setStyle("-fx-font-size: 13;");
        donateurLabel.setStyle("-fx-font-size: 13;");

        // Créer les boutons "Modifier" et "Supprimer"
        HBox buttonContainer = new HBox(10); // Espacement entre les boutons
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
// Appliquer un style CSS pour le bouton "Modifier" (vert)
        modifyButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

// Appliquer un style CSS pour le bouton "Supprimer" (rouge)
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        // Lorsque le bouton "Modifier" est cliqué, ouvrir la page de modification
        modifyButton.setOnAction(event -> {
            try {
                // Charger la vue FXML de la page de modification
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/ModifyDon.fxml"));
                BorderPane modifyPage = loader.load();


                // Passer le don à modifier au contrôleur
                ModifyDon modifyController = loader.getController();
                modifyController.initialize(don);

                // Créer une nouvelle scène et une nouvelle fenêtre pour afficher la page de modification
                Stage modifyStage = new Stage();
                modifyStage.setTitle("Modifier le Don");
                modifyStage.setScene(new Scene(modifyPage));
                modifyStage.show();
            } catch (IOException e) {
                showErrorAlert("Erreur de chargement", "Erreur lors du chargement de la page de modification : " + e.getMessage());
            }
        });

        // Associer l'objet Dons au bouton "Supprimer"
        deleteButton.setUserData(don); // Associer l'objet 'don' au bouton

        // Ajouter l'action pour le bouton "Supprimer"
        deleteButton.setOnAction(this::handleDelete);

        buttonContainer.getChildren().addAll(modifyButton, deleteButton);

        card.getChildren().addAll(typeLabel, montantLabel, descLabel, dateLabel, donateurLabel,disponibiliteLabel, buttonContainer);
        return card;

    }

    // Méthode pour afficher un message d'alerte de succès
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher un message d'alerte d'erreur
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
