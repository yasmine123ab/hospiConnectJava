package org.hospiconnect.controller.demandes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.DemandeDonService;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModifyDemande {

    @FXML
    private TextField typeBesoinTF;

    @FXML
    private TextArea detailsTA;

    @FXML
    private DatePicker dateDemandeDP;

    @FXML
    private ComboBox<User> patientComboBox;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private Button saveButton;
    @FXML
    private Button menuHomeButton;

    @FXML
    private Button annulerButton;

    private final DemandeDonService demandeService = new DemandeDonService();
    private DemandesDons demandeToModify;



    public void initialize(DemandesDons demande) {

        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));

        demandeToModify = demande;

        typeBesoinTF.setText(demande.getTypeBesoin());
        detailsTA.setText(demande.getDetails());

        if (demande.getDateDemande() != null) {
            dateDemandeDP.setValue(demande.getDateDemande().toLocalDate());
        }

        statutComboBox.setItems(FXCollections.observableArrayList("En attente", "Acceptée", "Rejetée"));
        statutComboBox.setValue(demande.getStatut());

        try {
            ObservableList<User> patients = FXCollections.observableArrayList(loadUsersFromDB());
            patientComboBox.setItems(patients);

            patientComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });

            patientComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });

        } catch (SQLException e) {
            showErrorAlert("Erreur", "Impossible de charger les patients : " + e.getMessage());
        }
    }

    private List<User> loadUsersFromDB() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, nom, prenom FROM user";
        Connection conn = DatabaseUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            users.add(u);
        }

        return users;
    }

    @FXML
    public void handleSave() {
        try {
            // Contrôle de saisie du type de besoin
            String typeBesoin = typeBesoinTF.getText();
            if (typeBesoin == null || typeBesoin.trim().isEmpty()) {
                showErrorAlert("Erreur de saisie", "Le type de besoin ne peut pas être vide.");
                return;
            }

            // Contrôle de saisie des détails
            String details = detailsTA.getText();
            if (details == null || details.trim().isEmpty()) {
                showErrorAlert("Erreur de saisie", "Les détails de la demande ne peuvent pas être vides.");
                return;
            }

            // Contrôle de la date
            java.time.LocalDate date = dateDemandeDP.getValue();
            if (date == null) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner une date.");
                return;
            }
            if (date.isBefore(java.time.LocalDate.now())) {
                showErrorAlert("Erreur de saisie", "La date ne peut pas être dans le passé.");
                return;
            }

            // Contrôle de sélection du patient
            User patient = patientComboBox.getValue();
            if (patient == null) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner un patient.");
                return;
            }

            // Contrôle de sélection du statut
            String statut = statutComboBox.getValue();
            if (statut == null || statut.trim().isEmpty()) {
                showErrorAlert("Erreur de saisie", "Veuillez sélectionner un statut.");
                return;
            }



            demandeToModify.setTypeBesoin(typeBesoin);
            demandeToModify.setDetails(details);
            demandeToModify.setDateDemande(Date.valueOf(date));
            demandeToModify.setPatient(patient);
            demandeToModify.setStatut(statut);

            demandeService.update(demandeToModify);

            // 👉 Redirection vers la page ShowDon.fxml
            SceneUtils.openNewScene("/Demandes/ShowDemande.fxml", saveButton.getScene(), null);


        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        if (saveButton != null) {
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
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

