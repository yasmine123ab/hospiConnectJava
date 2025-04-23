package org.hospiconnect.controller.demandes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.service.DemandeDonService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowDemande {

    @FXML private VBox demandeListContainer;
    @FXML private Button addDonButton;
    @FXML private Button menuHomeButton;
    @FXML private TextField searchField;
    @FXML private Button sortButton;

    private final DemandeDonService demandeService = new DemandeDonService();
    private List<DemandesDons> demandeList = new ArrayList<>();
    private boolean isAscending = true;

    @FXML
    public void initialize() {
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));

        try {
            demandeList = demandeService.findAll();
            displayFilteredDemandes(demandeList, "");

            // Recherche
            searchField.textProperty().addListener((obs, oldVal, newVal) ->
                    displayFilteredDemandes(demandeList, newVal));

            // Tri
            sortButton.setOnAction(e -> sortDemandesByType());

        } catch (SQLException e) {
            showErrorAlert("Erreur de récupération", "Erreur lors de la récupération des demandes : " + e.getMessage());
        }

        // Ajouter une nouvelle demande
        addDonButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/AddDemande.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) addDonButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sortDemandesByType() {
        List<DemandesDons> sortedList = demandeList.stream()
                .sorted((d1, d2) -> {
                    String type1 = d1.getTypeBesoin() != null ? d1.getTypeBesoin() : "";
                    String type2 = d2.getTypeBesoin() != null ? d2.getTypeBesoin() : "";
                    int result = type1.compareToIgnoreCase(type2);
                    return isAscending ? result : -result;
                })
                .collect(Collectors.toList());

        isAscending = !isAscending;
        sortButton.setText(isAscending ? "Trier ↑" : "Trier ↓");
        displayFilteredDemandes(sortedList, searchField.getText());
    }

    private void displayFilteredDemandes(List<DemandesDons> demandes, String filter) {
        demandeListContainer.getChildren().clear();
        String lowerFilter = filter.toLowerCase();

        for (DemandesDons demande : demandes) {
            String typeBesoin = demande.getTypeBesoin() != null ? demande.getTypeBesoin().toLowerCase() : "";
            String nom = (demande.getPatient() != null && demande.getPatient().getNom() != null)
                    ? demande.getPatient().getNom().toLowerCase() : "";
            String prenom = (demande.getPatient() != null && demande.getPatient().getPrenom() != null)
                    ? demande.getPatient().getPrenom().toLowerCase() : "";

            if (typeBesoin.contains(lowerFilter) || nom.contains(lowerFilter) || prenom.contains(lowerFilter)) {
                VBox card = createDemandeCard(demande);
                demandeListContainer.getChildren().add(card);
            }
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
        String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(demande.getDateDemande());
        Label dateLabel = new Label("Date : " + formattedDate);
        Label statutLabel = new Label("Statut : " + demande.getStatut());

        // Styles
        patientLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");
        typeLabel.setStyle("-fx-font-size: 13;");
        detailsLabel.setStyle("-fx-font-size: 13;");
        dateLabel.setStyle("-fx-font-size: 13;");
        statutLabel.setStyle("-fx-font-size: 13;");

        // Boutons
        HBox buttonContainer = new HBox(10);
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        modifyButton.setOnAction(event -> openModifyPage(demande, event));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setUserData(demande);
        deleteButton.setOnAction(this::handleDelete);

        buttonContainer.getChildren().addAll(modifyButton, deleteButton);

        card.getChildren().addAll(patientLabel, typeLabel, detailsLabel, dateLabel, statutLabel, buttonContainer);
        return card;
    }

    private void openModifyPage(DemandesDons demande, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/ModifyDemande.fxml"));
            BorderPane modifyPage = loader.load();
            ModifyDemande modifyController = loader.getController();
            modifyController.initialize(demande);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(modifyPage));
            currentStage.setTitle("Modifier la Demande");
        } catch (IOException e) {
            showErrorAlert("Erreur de chargement", "Erreur lors du chargement de la page de modification : " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        DemandesDons demande = (DemandesDons) sourceButton.getUserData();

        try {
            demandeService.delete(demande);
            demandeList.remove(demande);  // Mise à jour de la liste principale
            displayFilteredDemandes(demandeList, searchField.getText());
            showSuccessAlert("Succès", "La demande a été supprimée.");
        } catch (SQLException e) {
            showErrorAlert("Erreur de suppression", "Erreur lors de la suppression de la demande : " + e.getMessage());
        }
    }

    @FXML
    public void handleFaireUnDonClick(ActionEvent event) {
        openScene("/Dons/ShowDon.fxml", event);
    }

    @FXML
    public void handleFaireUneDemandeClick(ActionEvent event) {
        openScene("/Demandes/ShowDemande.fxml", event);
    }

    @FXML
    public void handleFaireUneAttributionClick(ActionEvent event) {
        openScene("/Attributions/ShowAttribution.fxml", event);
    }

    private void openScene(String path, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
