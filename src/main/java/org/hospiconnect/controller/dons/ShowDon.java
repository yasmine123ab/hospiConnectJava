package org.hospiconnect.controller.dons;

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
import org.hospiconnect.model.Dons;
import org.hospiconnect.service.DonService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowDon {

    @FXML private VBox donListContainer;
    @FXML private Button menuHomeButton;
    @FXML private Button addDonButton;
    @FXML private TextField searchField;
    @FXML private Button sortButton;

    private final DonService donService = new DonService();
    private boolean isAscending = true;
    private List<Dons> donList = new ArrayList<>(); // Stocker les dons chargés

    @FXML
    public void initialize() {
        try {
            donList = donService.findAll();
            displayFilteredDons(donList, "");

            sortButton.setOnAction(e -> sortDonsByType());

            searchField.textProperty().addListener((obs, oldValue, newValue) ->
                    displayFilteredDons(donList, newValue)
            );

            addDonButton.setOnAction(event -> openScene("/Dons/AddDon.fxml", addDonButton));
            menuHomeButton.setOnAction(event ->
                    SceneUtils.openNewScene("/HomePages/frontList.fxml", menuHomeButton.getScene(), null)
            );

        } catch (SQLException e) {
            showErrorAlert("Erreur de récupération", "Erreur lors de la récupération des dons : " + e.getMessage());
        }
    }

    private void sortDonsByType() {
        List<Dons> sortedList = donList.stream()
                .sorted((d1, d2) -> {
                    String type1 = d1.getTypeDon() != null ? d1.getTypeDon() : "";
                    String type2 = d2.getTypeDon() != null ? d2.getTypeDon() : "";
                    int result = type1.compareToIgnoreCase(type2);
                    return isAscending ? result : -result;
                })
                .collect(Collectors.toList());

        isAscending = !isAscending;
        sortButton.setText(isAscending ? "Trier ↑" : "Trier ↓");
        displayFilteredDons(sortedList, searchField.getText());
    }

    private void displayFilteredDons(List<Dons> dons, String filter) {
        donListContainer.getChildren().clear();
        String lowerFilter = filter.toLowerCase();

        for (Dons don : dons) {
            String type = don.getTypeDon() != null ? don.getTypeDon().toLowerCase() : "";
            String nom = (don.getDonateur() != null && don.getDonateur().getNom() != null)
                    ? don.getDonateur().getNom().toLowerCase() : "";
            String prenom = (don.getDonateur() != null && don.getDonateur().getPrenom() != null)
                    ? don.getDonateur().getPrenom().toLowerCase() : "";

            if (type.contains(lowerFilter) || nom.contains(lowerFilter) || prenom.contains(lowerFilter)) {
                VBox card = createDonCard(don);
                donListContainer.getChildren().add(card);
            }
        }
    }

    @FXML
    public void handleFaireUnDonClick(ActionEvent event) {
        openScene("/Dons/ShowDon.fxml", event.getSource());
    }

    @FXML
    public void handleFaireUneDemandeClick(ActionEvent event) {
        openScene("/Demandes/ShowDemande.fxml", event.getSource());
    }

    @FXML
    public void handleFaireUneAttributionClick(ActionEvent event) {
        openScene("/Attributions/ShowAttribution.fxml", event.getSource());
    }

    private void openScene(String fxmlPath, Object source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) source).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur", "Impossible de charger la page : " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        Dons don = (Dons) sourceButton.getUserData();

        try {
            donService.delete(don);
            donList.removeIf(d -> d.getId() == don.getId());
            displayFilteredDons(donList, searchField.getText());
            showSuccessAlert("Succès", "Le don a été supprimé.");
        } catch (SQLException e) {
            showErrorAlert("Erreur de suppression", "Erreur lors de la suppression du don : " + e.getMessage());
        }
    }

    private VBox createDonCard(Dons don) {
        VBox card = new VBox(8);
        card.setStyle("""
            -fx-padding: 10;
            -fx-border-color: #cccccc;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-background-color: #f4f4f4;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);
        card.setId("don-card-" + don.getId());

        Label typeLabel = new Label("Type : " + don.getTypeDon());
        Label montantLabel = new Label("Montant : " + don.getMontant() + " DNT");
        Label descLabel = new Label("Description : " + don.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Label dateLabel = new Label("Date : " + dateFormat.format(don.getDateDon()));

        String donateurName = (don.getDonateur() != null)
                ? don.getDonateur().getNom() + " " + don.getDonateur().getPrenom()
                : "Inconnu";
        Label donateurLabel = new Label("Donateur : " + donateurName);
        Label disponibiliteLabel = new Label("Disponibilité : " + (don.getDisponibilite() ? "Disponible" : "Non disponible"));

        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        montantLabel.setStyle("-fx-font-size: 13;");
        descLabel.setStyle("-fx-font-size: 13;");
        dateLabel.setStyle("-fx-font-size: 13;");
        donateurLabel.setStyle("-fx-font-size: 13;");

        HBox buttonContainer = new HBox(10);
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        modifyButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setUserData(don);
        deleteButton.setOnAction(this::handleDelete);

        modifyButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/ModifyDon.fxml"));
                BorderPane modifyPage = loader.load();
                ModifyDon controller = loader.getController();
                controller.initialize(don);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(modifyPage));
                stage.setTitle("Modifier le Don");
            } catch (IOException e) {
                showErrorAlert("Erreur de chargement", "Erreur lors du chargement de la page de modification : " + e.getMessage());
            }
        });

        buttonContainer.getChildren().addAll(modifyButton, deleteButton);
        card.getChildren().addAll(typeLabel, montantLabel, descLabel, dateLabel, donateurLabel, disponibiliteLabel, buttonContainer);
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
