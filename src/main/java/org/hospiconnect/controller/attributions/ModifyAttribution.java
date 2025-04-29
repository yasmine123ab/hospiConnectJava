package org.hospiconnect.controller.attributions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.AttributionsDons;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.AttributionDonService;
import org.hospiconnect.utils.DatabaseUtils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModifyAttribution {

    @FXML
    private ComboBox<Dons> donComboBox;
    @FXML
    private ComboBox<DemandesDons> demandeComboBox;
    @FXML
    private ComboBox<User> beneficiaireComboBox;
    @FXML
    private DatePicker dateAttributionDP;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button annulerButton;

    @FXML
    private Button menuHomeButton;



    private final AttributionDonService attributionService = new AttributionDonService();
    private AttributionsDons attributionToModify;

    public void initialize(AttributionsDons attribution) {
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));
        attributionToModify = attribution;

        try {
            ObservableList<Dons> dons = FXCollections.observableArrayList(loadDonsFromDB());
            ObservableList<DemandesDons> demandes = FXCollections.observableArrayList(loadDemandesFromDB());
            ObservableList<User> beneficiaires = FXCollections.observableArrayList(loadBeneficiairesFromDB());

            // ComboBox Dons
            donComboBox.setPromptText("Choisir un don");
            donComboBox.setItems(dons);
            donComboBox.setCellFactory(param -> new ListCell<Dons>() {
                @Override
                protected void updateItem(Dons don, boolean empty) {
                    super.updateItem(don, empty);
                    if (empty || don == null) {
                        setText(null);
                    } else {
                        setText(don.getTypeDon());
                    }
                }
            });
            donComboBox.setButtonCell(new ListCell<Dons>() {
                @Override
                protected void updateItem(Dons don, boolean empty) {
                    super.updateItem(don, empty);
                    if (empty || don == null) {
                        setText("Choisir un don");
                    } else {
                        setText(don.getTypeDon());
                    }
                }
            });

            // ComboBox Demandes
            demandeComboBox.setPromptText("Choisir une demande");
            demandeComboBox.setItems(demandes);
            demandeComboBox.setCellFactory(param -> new ListCell<DemandesDons>() {
                @Override
                protected void updateItem(DemandesDons demande, boolean empty) {
                    super.updateItem(demande, empty);
                    if (empty || demande == null) {
                        setText(null);
                    } else {
                        User patient = demande.getPatient();
                        String nom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
                        setText(demande.getTypeBesoin());
                    }
                }
            });
            demandeComboBox.setButtonCell(new ListCell<DemandesDons>() {
                @Override
                protected void updateItem(DemandesDons demande, boolean empty) {
                    super.updateItem(demande, empty);
                    if (empty || demande == null) {
                        setText("Choisir une demande");
                    } else {
                        User patient = demande.getPatient();
                        String nom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
                        setText(demande.getTypeBesoin());
                    }
                }
            });

            // ComboBox Bénéficiaires
            beneficiaireComboBox.setPromptText("Choisir un bénéficiaire");
            beneficiaireComboBox.setItems(beneficiaires);
            beneficiaireComboBox.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User u, boolean empty) {
                    super.updateItem(u, empty);
                    if (empty || u == null) {
                        setText(null);
                    } else {
                        setText(u.getNom() + " " + u.getPrenom());
                    }
                }
            });
            beneficiaireComboBox.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User u, boolean empty) {
                    super.updateItem(u, empty);
                    if (empty || u == null) {
                        setText("Choisir un bénéficiaire");
                    } else {
                        setText(u.getNom() + " " + u.getPrenom());
                    }
                }
            });

            // Pré-sélections

            if (attribution.getBeneficiaire() != null)
                beneficiaireComboBox.getSelectionModel().select(attribution.getBeneficiaire());

            if (attribution.getDateAttribution() != null)
                dateAttributionDP.setValue(attribution.getDateAttribution().toLocalDate());

        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }


    private List<Dons> loadDonsFromDB() throws SQLException {
        List<Dons> dons = new ArrayList<>();
        String sql = "SELECT * FROM dons";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dons don = new Dons();
                don.setId(rs.getInt("id"));
                don.setTypeDon(rs.getString("type_don"));
                dons.add(don);
            }
        }
        return dons;
    }


    private List<DemandesDons> loadDemandesFromDB() throws SQLException {
        List<DemandesDons> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_dons";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DemandesDons demande = new DemandesDons();
                demande.setId(rs.getInt("id"));
                demande.setTypeBesoin(rs.getString("type_besoin"));
                demandes.add(demande);
            }
        }
        return demandes;
    }

    private List<User> loadBeneficiairesFromDB() throws SQLException {
        List<User> beneficiaires = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                beneficiaires.add(u);
            }
        }
        return beneficiaires;
    }
    @FXML
    public void handleSave() {
        try {
            // 👉 Vérification du don sélectionné
            Dons selectedDon = donComboBox.getValue();
            if (selectedDon == null) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner un don.");
                return;
            }

            // 👉 Vérification de la demande sélectionnée
            DemandesDons selectedDemande = demandeComboBox.getValue();
            if (selectedDemande == null) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner une demande.");
                return;
            }

            // 👉 Vérification de la date
            java.time.LocalDate date = dateAttributionDP.getValue();
            if (date == null) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner une date.");
                return;
            }
            if (date.isBefore(java.time.LocalDate.now())) {
                showErrorAlert("Erreur de saisie", "La date ne peut pas être dans le passé.");
                return;
            }

            // 👉 Vérification du statut
            String statut = statutComboBox.getValue();
            if (statut == null || statut.trim().isEmpty()) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner un statut.");
                return;
            }

            // 👉 Vérification du bénéficiaire
            User selectedBeneficiaire = beneficiaireComboBox.getValue();
            if (selectedBeneficiaire == null) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner un bénéficiaire.");
                return;
            }

            // ✍️ Mise à jour des données
            attributionToModify.setDon(selectedDon);
            attributionToModify.setDonId(selectedDon.getId());

            attributionToModify.setDemande(selectedDemande);
            attributionToModify.setDemandeId(selectedDemande.getId());

            attributionToModify.setDateAttribution(Date.valueOf(date));
            attributionToModify.setStatut(statut);

            attributionToModify.setBeneficiaire(selectedBeneficiaire);
            attributionToModify.setBeneficiaireId(selectedBeneficiaire.getId());

            // 👉 Update en base de données
            attributionService.update(attributionToModify);

            // Après la modification réussie
            envoyerNotificationAuPatient(beneficiaireComboBox.getValue(), statutComboBox.getValue());

            // 👉 Redirection vers la liste
            SceneUtils.openNewScene("/Attributions/ShowAttribution.fxml", saveButton.getScene(), null);

        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    private void envoyerNotificationAuPatient(User patient, String statut) {
        String message = "";

        switch (statut) {
            case "Attribué":
                message = "Bonjour " + patient.getNom() + ", votre demande a été acceptée avec succès ! 🎉";
                break;
            case "En attente":
                message = "Bonjour " + patient.getNom() + ", votre demande est en attente de traitement.";
                break;
            case "Refusé":
                message = "Bonjour " + patient.getNom() + ", malheureusement votre demande a été refusée.";
                break;
            default:
                message = "Bonjour " + patient.getNom() + ", il y a eu une mise à jour concernant votre demande.";
        }

        Notifications.create()
                .title("Mise à jour de votre demande")
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.TOP_RIGHT)
                .showInformation();
    }


    @FXML
    public void handleCancel() {
        if (saveButton != null) {
            ((Stage) saveButton.getScene().getWindow()).close();
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
    private void openScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
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