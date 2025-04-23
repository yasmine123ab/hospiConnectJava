package org.hospiconnect.controller.attributions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.AttributionsDons;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.AttributionDonService;
import org.hospiconnect.utils.DatabaseUtils;

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
                        setText("");
                    } else {
                        setText(don.getTypeDon() + " - " + don.getMontant() + " DA");
                    }
                }
            });
            donComboBox.setButtonCell(new ListCell<Dons>() {
                @Override
                protected void updateItem(Dons don, boolean empty) {
                    super.updateItem(don, empty);
                    if (empty || don == null) {
                        setText(donComboBox.getPromptText());
                    } else {
                        setText(don.getTypeDon() + " - " + don.getMontant() + " DA");
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
                        setText(demandeComboBox.getPromptText());
                    } else {
                        User patient = demande.getPatient();
                        String nom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
                        setText(demande.getTypeBesoin() + " - " + nom);
                    }
                }
            });
            demandeComboBox.setButtonCell(new ListCell<DemandesDons>() {
                @Override
                protected void updateItem(DemandesDons demande, boolean empty) {
                    super.updateItem(demande, empty);
                    if (empty || demande == null) {
                        setText(demandeComboBox.getPromptText());
                    } else {
                        User patient = demande.getPatient();
                        String nom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
                        setText(demande.getTypeBesoin() + " - " + nom);
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
                        setText(null);
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
        String sql = "SELECT id, type_don, montant, description, date_don FROM dons";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dons don = new Dons();
                don.setId(rs.getInt("id"));
                don.setTypeDon(rs.getString("type_don"));
                don.setMontant(rs.getDouble("montant"));
                don.setDescription(rs.getString("description"));
                dons.add(don);
            }
        }

        return dons;
    }


    private List<DemandesDons> loadDemandesFromDB() throws SQLException {
        List<DemandesDons> demandes = new ArrayList<>();

        String sql = "SELECT dd.*, u.id AS user_id, u.nom, u.prenom " +
                "FROM demandes_dons dd " +
                "JOIN user u ON dd.patient_id = u.id";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DemandesDons demande = new DemandesDons();
                demande.setId(rs.getInt("id"));
                demande.setTypeBesoin(rs.getString("type_besoin"));

                // Création du bénéficiaire
                User patient = new User();
                patient.setId(rs.getInt("id"));
                patient.setNom(rs.getString("nom"));
                patient.setPrenom(rs.getString("prenom"));

                demande.setPatient(patient);

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

            showSuccessAlert("Succès", "Attribution modifiée avec succès.");
            ((Stage) saveButton.getScene().getWindow()).close();

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
