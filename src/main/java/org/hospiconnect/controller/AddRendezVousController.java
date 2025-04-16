package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.hospiconnect.model.RendezVous;
import org.hospiconnect.service.RendezVousService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddRendezVousController {

    @FXML private DatePicker datePicker;
    @FXML private TextField heureTF;
    @FXML private ComboBox<String> typeCB;
    @FXML private ComboBox<String> graviteCB;
    @FXML private TextField commentaireTF;

    @FXML private TextField nomTF;
    @FXML private TextField prenomTF;
    @FXML private TextField telTF;
    @FXML private TextField emailTF;

    private RendezVousService rendezVousService = new RendezVousService();

    public void initialize() {
        ObservableList<String> typeOptions = FXCollections.observableArrayList("Opération", "Intervention Urgence");
        typeCB.setItems(typeOptions);

        ObservableList<String> graviteOptions = FXCollections.observableArrayList("Faible", "Moyenne", "Elevée");
        graviteCB.setItems(graviteOptions);
    }

    @FXML
    void AjouterRendezVous(ActionEvent event) {
        String nom = nomTF.getText().trim();
        String prenom = prenomTF.getText().trim();
        String tel = telTF.getText().trim();
        String email = emailTF.getText().trim();
        String commentaire = commentaireTF.getText().trim();
        String heureStr = heureTF.getText().trim();
        String type = typeCB.getValue();
        String gravite = graviteCB.getValue();
        LocalDate date = datePicker.getValue();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || tel.isEmpty() || email.isEmpty() || heureStr.isEmpty() || date == null || type == null || gravite == null) {
            showAlert("Erreur", "Tous les champs obligatoires doivent être remplis.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Erreur Email", "Adresse email invalide.");
            return;
        }

        if (!tel.matches("\\d{8}")) {
            showAlert("Erreur Téléphone", "Le numéro de téléphone doit contenir 8 chiffres.");
            return;
        }

        if (!heureStr.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$")) {
            showAlert("Erreur Heure", "Heure invalide (format attendu : HH:MM 24h).");
            return;
        }

        try {
            // Parse the heure String into LocalTime
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime heure = LocalTime.parse(heureStr, timeFormatter);

            // Create a new RendezVous object
            // Set statut to "Confirmé" by default since there's no field for it in the form
            RendezVous rdv = new RendezVous(nom, prenom, tel, email, date, heure, type, gravite, "Confirmé", commentaire);



            // Show success message
            showAlert("Succès", "Rendez-vous enregistré avec succès !");

            // Clear the form after successful submission
            clearForm();
        } catch (DateTimeParseException e) {
            showAlert("Erreur Heure", "Erreur de format de l'heure : " + e.getMessage());
            System.out.println("DateTimeParseException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nomTF.clear();
        prenomTF.clear();
        telTF.clear();
        emailTF.clear();
        commentaireTF.clear();
        heureTF.clear();
        typeCB.setValue(null);
        graviteCB.setValue(null);
        datePicker.setValue(null);
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void AfficherListe(ActionEvent event) {
        try {
            // Correction du chemin du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeRendezVous.fxml"));

            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) datePicker.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des rendez-vous");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des rendez-vous : " + e.getMessage());
            System.out.println("IOException dans AfficherListe : " + e.getMessage());
            e.printStackTrace();
        }
    }
}