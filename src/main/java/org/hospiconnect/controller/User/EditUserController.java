package org.hospiconnect.controller.User;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hospiconnect.model.User;
import org.hospiconnect.service.UserService;

import java.io.File;

public class EditUserController {

    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfEmail;
    @FXML private ComboBox<String> comboRole;
    @FXML private ComboBox<String> comboStatut;
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

    private User user;
    private final UserService userService = new UserService();

    // Initialisation appelée automatiquement après chargement FXML
    @FXML
    private void initialize() {
        comboRole.getItems().addAll("admin", "artisan", "client");
        comboStatut.getItems().addAll("active", "blocked");
        comboGroupeSanguin.getItems().addAll("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");
        comboGouvernorat.getItems().addAll("Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Bizerte",
                "Kairouan", "Sousse", "Sfax", "Gabes", "Medenine", "Tataouine", "Tozeur",
                "Kasserine", "Sidi Bouzid", "Kef", "Jendouba", "Zaghouan", "Mahdia",
                "Siliana", "Gafsa");
        comboSexe.getItems().addAll("Masculin", "Féminin", "Autre");
    }

    // Reçoit l'utilisateur sélectionné depuis le controller parent
    public void setUser(User user) {
        this.user = user;

        tfNom.setText(user.getNom());
        tfPrenom.setText(user.getPrenom());
        tfEmail.setText(user.getEmail());
        tfPhoto.setText(user.getPhoto());
        comboStatut.setValue(user.getStatut());
        comboRole.setValue(user.getRoles().contains("ROLE_ADMIN") ? "admin" :
                user.getRoles().contains("ROLE_ARTISAN") ? "artisan" : "client");
        comboGroupeSanguin.setValue(user.getGroupe_Sanguin());
        comboGouvernorat.setValue(user.getGouvernorat());
        tfPoids.setText(String.valueOf(user.getPoids()));
        tfTaille.setText(String.valueOf(user.getTaille()));
        tfIMC.setText(String.valueOf(user.getIMC()));
        tfTel.setText(user.getTel());
        comboSexe.setValue(user.getSexe());
        tfCodePostal.setText(user.getZipCode());
        tfAdresse.setText(user.getAdresse());
    }

    // Méthode appelée au clic sur "Enregistrer"
    @FXML
    private void handleSave() {
        // Vérification de chaque champ individuellement
        if (tfNom.getText().isEmpty()) {
            showAlert("Nom manquant", "Veuillez saisir le nom.");
            return;
        }

        if (tfPrenom.getText().isEmpty()) {
            showAlert("Prénom manquant", "Veuillez saisir le prénom.");
            return;
        }

        if (tfEmail.getText().isEmpty()) {
            showAlert("Email manquant", "Veuillez saisir l'email.");
            return;
        }

        if (comboRole.getValue() == null) {
            showAlert("Rôle manquant", "Veuillez sélectionner un rôle.");
            return;
        }

        if (comboStatut.getValue() == null) {
            showAlert("Statut manquant", "Veuillez sélectionner un statut.");
            return;
        }

        if (comboGroupeSanguin.getValue() == null) {
            showAlert("Groupe sanguin manquant", "Veuillez sélectionner un groupe sanguin.");
            return;
        }

        if (comboGouvernorat.getValue() == null) {
            showAlert("Gouvernorat manquant", "Veuillez sélectionner un gouvernorat.");
            return;
        }

        // Vérification et conversion de Poids
        try {
            user.setPoids(Float.parseFloat(tfPoids.getText()));
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le poids doit être un nombre valide.");
            return;
        }

        // Vérification et conversion de Taille
        try {
            user.setTaille(Float.parseFloat(tfTaille.getText()));
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La taille doit être un nombre valide.");
            return;
        }

        // Vérification et conversion de IMC
        try {
            user.setImc(Float.parseFloat(tfIMC.getText()));
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'IMC doit être un nombre valide.");
            return;
        }

        if (tfTel.getText().isEmpty()) {
            showAlert("Téléphone manquant", "Veuillez saisir le numéro de téléphone.");
            return;
        }

        if (comboSexe.getValue() == null) {
            showAlert("Sexe manquant", "Veuillez sélectionner le sexe.");
            return;
        }

        if (tfCodePostal.getText().isEmpty()) {
            showAlert("Code postal manquant", "Veuillez saisir le code postal.");
            return;
        }

        if (tfAdresse.getText().isEmpty()) {
            showAlert("Adresse manquante", "Veuillez saisir l'adresse.");
            return;
        }

        // Vérification du champ photo
        if (tfPhoto.getText().isEmpty()) {
            showAlert("Photo manquante", "Veuillez sélectionner une photo.");
            return;
        }

        // Mise à jour des informations de l'utilisateur
        user.setNom(tfNom.getText());
        user.setPrenom(tfPrenom.getText());
        user.setEmail(tfEmail.getText());
        user.setStatut(comboStatut.getValue());
        user.setPhoto(tfPhoto.getText());
        user.setGroupe_Sanguin(comboGroupeSanguin.getValue());
        user.setGouvernorat(comboGouvernorat.getValue());
        user.setTel(tfTel.getText());
        user.setSexe(comboSexe.getValue());
        user.setZipCode(tfCodePostal.getText());
        user.setAdresse(tfAdresse.getText());

        // Formater rôle selon le format attendu en base de données
        String formattedRole = "ROLE_" + comboRole.getValue().toUpperCase();
        user.setRoles(formattedRole);  // Rôle maintenant envoyé dans le format attendu

        // Mise à jour de l'utilisateur via le service
        userService.updateUser(user);

        // Affichage de la confirmation
        showAlert("✅ Succès", "L'utilisateur a été modifié avec succès.");

        // Fermer la fenêtre après l'enregistrement
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }





    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
                tfPhoto.setText(fileName); // Affiche juste le nom du fichier dans le champ texte

                // Nouvelle destination : C:/wamp64/www/images/
                File destination = new File("C:/wamp64/www/images/" + fileName);

                // Créer le dossier s’il n’existe pas
                destination.getParentFile().mkdirs();

                // Copier l'image sélectionnée dans le dossier cible
                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                System.out.println("✅ Image copiée dans C:/wamp64/www/images/" + fileName);
            } catch (Exception e) {
                System.out.println("❌ Erreur copie image : " + e.getMessage());
                showAlert("Erreur", "Impossible de copier l'image.");
            }
        }
    }

}
