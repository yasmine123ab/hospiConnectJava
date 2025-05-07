package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;


import org.hospiconnect.model.User;
import org.hospiconnect.service.UserService;

import java.io.File;
import java.io.IOException;

public class AjoutAdminController {

    // Déclaration des champs du formulaire
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfEmail;
    @FXML private ComboBox<String> comboStatut;
    @FXML private ComboBox<String> comboRole;
    @FXML private TextField tfPhoto;
    @FXML private ComboBox<String> comboGroupeSanguin;
    @FXML private ComboBox<String> comboGouvernorat;
    @FXML private TextField tfPoids;
    @FXML private TextField tfTaille;
    @FXML private TextField tfIMC;
    @FXML private TextField tfTel;
    @FXML private ComboBox<String> comboSexe;
    @FXML private TextField tfCodePostal;
    @FXML private TextField tfAdresse;

    private final UserService userService = new UserService();


    @FXML
    private void handleBrowsePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(tfPhoto.getScene().getWindow());

        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName();
                tfPhoto.setText(fileName); // Affiche le nom du fichier dans le champ texte

                // Destination : dossier C:\wamp64\www\images
                File destination = new File("C:/wamp64/www/images/" + fileName);

                // Créer le dossier s’il n’existe pas
                destination.getParentFile().mkdirs();

                // Copier l'image sélectionnée dans le dossier C:/wamp64/www/images
                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                System.out.println("✅ Image copiée dans C:/wamp64/www/images/" + fileName);
            } catch (IOException e) {
                System.out.println("❌ Erreur copie image : " + e.getMessage());
                showAlert("Erreur", "Impossible de copier l'image.");
            }
        }
    }



    // Action pour le bouton "Enregistrer les modifications"
    @FXML
    public void handleSave(ActionEvent actionEvent) {
        // Vérification des champs individuellement

        if (tfNom.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Nom est obligatoire.");
            return;
        }

        if (tfPrenom.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Prénom est obligatoire.");
            return;
        }

        if (tfEmail.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Email est obligatoire.");
            return;
        }

        if (comboStatut.getValue() == null) {
            showAlert("Erreur", "Le champ Statut est obligatoire.");
            return;
        }

        if (comboRole.getValue() == null) {
            showAlert("Erreur", "Le champ Role est obligatoire.");
            return;
        }

        if (tfPhoto.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Photo est obligatoire.");
            return;
        }

        if (comboGroupeSanguin.getValue() == null) {
            showAlert("Erreur", "Le champ Groupe Sanguin est obligatoire.");
            return;
        }

        if (comboGouvernorat.getValue() == null) {
            showAlert("Erreur", "Le champ Gouvernorat est obligatoire.");
            return;
        }

        if (tfPoids.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Poids est obligatoire.");
            return;
        }

        if (tfTaille.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Taille est obligatoire.");
            return;
        }

        if (tfIMC.getText().isEmpty()) {
            showAlert("Erreur", "Le champ IMC est obligatoire.");
            return;
        }

        if (tfTel.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Téléphone est obligatoire.");
            return;
        }

        if (comboSexe.getValue() == null) {
            showAlert("Erreur", "Le champ Sexe est obligatoire.");
            return;
        }

        if (tfCodePostal.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Code Postal est obligatoire.");
            return;
        }

        if (tfAdresse.getText().isEmpty()) {
            showAlert("Erreur", "Le champ Adresse est obligatoire.");
            return;
        }

        // Créer un objet User à partir des champs remplis
        User user = new User();
        user.setNom(tfNom.getText());
        user.setPrenom(tfPrenom.getText());
        user.setEmail(tfEmail.getText());
        user.setStatut(comboStatut.getValue());
        user.setPhoto(tfPhoto.getText());
        user.setGroupe_Sanguin(comboGroupeSanguin.getValue());
        user.setGouvernorat(comboGouvernorat.getValue());
        user.setPoids(Float.parseFloat(tfPoids.getText()));
        user.setTaille(Float.parseFloat(tfTaille.getText()));
        user.setImc(Float.parseFloat(tfIMC.getText()));
        user.setTel(tfTel.getText());
        user.setSexe(comboSexe.getValue());
        user.setZipCode(tfCodePostal.getText());
        user.setAdresse(tfAdresse.getText());

        // Formater rôle selon le format attendu en base de données
        String formattedRole = "ROLE_" + comboRole.getValue().toUpperCase();
        user.setRoles(formattedRole);  // Rôle maintenant envoyé dans le format attendu

        // Ajouter l'utilisateur via le service
        userService.AddUser(user);

        // Si l'enregistrement est réussi
        showAlert("Succès", "Administrateur ajouté avec succès !");
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
