package org.hospiconnect.controller.demandes;

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
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.DemandeDonService;
import org.hospiconnect.utils.DatabaseUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddDemande {

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
    private Button revenirButton;

    private DemandeDonService demandeService;

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
    public void handleRevenir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/ShowDemande.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir du bouton
            Stage stage = (Stage) revenirButton.getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public AddDemande() {
        demandeService = new DemandeDonService();
    }

    @FXML
    public void initialize() {
        try {
            List<User> patients = loadPatientsFromDB();
            ObservableList<User> observablePatients = FXCollections.observableArrayList(patients);
            patientComboBox.setItems(observablePatients);

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

            statutComboBox.setItems(FXCollections.observableArrayList("En attente", "Acceptée", "Refusée"));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des patients : " + e.getMessage());
        }
    }

    private List<User> loadPatientsFromDB() throws SQLException {
        List<User> users = new ArrayList<>();
        var conn = DatabaseUtils.getConnection();
        var stmt = conn.prepareStatement("SELECT id, nom, prenom FROM user");
        var rs = stmt.executeQuery();
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
    private void handleSubmit(ActionEvent event) {
        try {
            String type = typeBesoinTF.getText().trim();
            String details = detailsTA.getText().trim();
            LocalDate date = dateDemandeDP.getValue();
            String statut = statutComboBox.getValue();
            User selectedPatient = patientComboBox.getValue();

            // Validation
            if (type.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Le type de besoin est obligatoire.");
                return;
            }

            if (details.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Les détails sont obligatoires.");
                return;
            }

            if (date == null) {
                showAlert(Alert.AlertType.WARNING, "Date manquante", "Veuillez choisir une date.");
                return;
            }

            if (date.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.WARNING, "Date invalide", "La date ne peut pas être dans le passé.");
                return;
            }

            if (statut == null || statut.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Statut manquant", "Veuillez sélectionner un statut.");
                return;
            }

            if (selectedPatient == null) {
                showAlert(Alert.AlertType.WARNING, "Patient manquant", "Veuillez sélectionner un patient.");
                return;
            }

            // Création de la demande
            DemandesDons demande = new DemandesDons();
            demande.setTypeBesoin(type);
            demande.setDetails(details);
            demande.setDateDemande(Date.valueOf(date));
            demande.setStatut(statut);
            demande.setPatientId(selectedPatient.getId());

            demandeService.insert(demande);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande ajoutée avec succès !");
//            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void clearFields() {
        typeBesoinTF.clear();
        detailsTA.clear();
        dateDemandeDP.setValue(null);
        statutComboBox.getSelectionModel().clearSelection();
        patientComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

