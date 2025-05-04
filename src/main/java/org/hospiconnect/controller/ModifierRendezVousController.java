package org.hospiconnect.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.RendezVous;

public class ModifierRendezVousController {

    // Champs correspondant exactement au FXML
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telField;
    @FXML private TextField emailField;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private ComboBox<String> typeField;
    @FXML private ComboBox<String> graviteField;
    @FXML private TextArea commentaire;
    @FXML private Button enregistrerBtn;

    private RendezVous rendezVous;

    @FXML
    public void initialize() {
        // Initialisation des ComboBox
        typeField.getItems().addAll("Opération", "Intervention Urgence");
        graviteField.getItems().addAll("Faible", "Moyenne", "Elevée");
    }

    public void setRendezVous(RendezVous rdv) {
        this.rendezVous = rdv;
        if (rdv != null) {
            nomField.setText(rdv.getNom());
            prenomField.setText(rdv.getPrenom());
            telField.setText(rdv.getTelephone());
            emailField.setText(rdv.getEmail());
            datePicker.setValue(rdv.getDate());
            heureField.setText(rdv.getHeure() != null ? rdv.getHeure().toString() : "");
            typeField.setValue(rdv.getType());
            graviteField.setValue(rdv.getGravite());
            commentaire.setText(rdv.getCommentaire());
        }
    }

    @FXML
    private void enregistrerModification() {
        try {
            if (rendezVous == null) return;

            // Validation basique
            if (heureField.getText().isEmpty() || !heureField.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                showAlert("Erreur", "Format d'heure invalide (HH:MM requis)");
                return;
            }

            // Mise à jour du rendez-vous
            rendezVous.setNom(nomField.getText());
            rendezVous.setPrenom(prenomField.getText());
            rendezVous.setTelephone(telField.getText());
            rendezVous.setEmail(emailField.getText());
            rendezVous.setDate(datePicker.getValue());
            rendezVous.setHeure(java.time.LocalTime.parse(heureField.getText()));
            rendezVous.setType(typeField.getValue());
            rendezVous.setGravite(graviteField.getValue());
            rendezVous.setCommentaire(commentaire.getText());

            showAlert("Succès", "Modifications enregistrées !");
            fermerFenetre();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) enregistrerBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}