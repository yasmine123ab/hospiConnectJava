package org.hospiconnect.controller.attributions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
            if (attribution.getDon() != null)
                donComboBox.getSelectionModel().select(attribution.getDon());

            if (attribution.getDemande() != null)
                demandeComboBox.getSelectionModel().select(attribution.getDemande());

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
            if (donComboBox.getValue() == null) {
                showErrorAlert("Erreur", "Veuillez sélectionner un don.");
                return;
            }

            if (demandeComboBox.getValue() == null) {
                showErrorAlert("Erreur", "Veuillez sélectionner une demande.");
                return;
            }

            if (dateAttributionDP.getValue() == null) {
                showErrorAlert("Erreur", "Veuillez sélectionner une date.");
                return;
            }

            attributionToModify.setDon(donComboBox.getValue());
            attributionToModify.setDemande(demandeComboBox.getValue());
            attributionToModify.setDateAttribution(Date.valueOf(dateAttributionDP.getValue()));

            attributionService.update(attributionToModify);

            // 👉 Redirection vers la page ShowDon.fxml
            SceneUtils.openNewScene("/Attributions/ShowAttribution.fxml", saveButton.getScene(), null);


        } catch (SQLException e) {
            showErrorAlert("Erreur", "Échec de modification : " + e.getMessage());
        }
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
