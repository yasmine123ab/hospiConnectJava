package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hospiconnect.model.User;
import org.hospiconnect.service.UserService;
import org.hospiconnect.service.VERIFY;

import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ProfilController {

    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfEmail;
    @FXML private TextField    tfTelephone;
    @FXML private TextField tfAdresse;
    @FXML private PasswordField tfOldPassword;
    @FXML private PasswordField tfNewPassword;
    @FXML private Button btnHistorique;
    @FXML private ImageView iconHistorique;
    @FXML private Label alertMessage;

    @FXML private ComboBox  cbGroupeSanguin;
    @FXML private ComboBox   cbGouvernorat;
    @FXML private TextField tfZipCode;
    @FXML private TextField tfTaille;
    @FXML private TextField  tfPoids;

    @FXML private ImageView photoProfil;
    @FXML private ImageView editIcon;
    private boolean photoModifiee = false;
    @FXML private Label labelNomPrenom;
    @FXML private Label labelDiplomePath;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressPercentage;

    private File diplomeFile;

    private User user;
    private final UserService userService = new UserService();


    public void setUser(User user) {

        progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            double progress = newVal.doubleValue();
            progressPercentage.setText(String.format("%.0f%%", progress * 100));


            if (progress >= 0.3 && progress < 0.5) {

                showMotivatingMessage("Presque à la moitié, continue comme ça !");
            } else if (progress >= 0.5 && progress < 0.7) {
                showMotivatingMessage("Tu avances bien, plus que quelques étapes !");
            } else if (progress >= 0.7 && progress < 0.9) {

                showMotivatingMessage("C'est presque terminé, tu es sur la bonne voie !");
            } else if (progress >= 0.9 && progress < 1.0) {

                showMotivatingMessage("Félicitations, tu es presque au sommet !");
            } else if (progress == 1.0) {
                showMotivatingMessage("Succès ! Ton profil est complet !");
                addVerifiedBadge();
            }

        });

        // Mise à jour de la ProgressBar
        progressBar.setProgress(0.7);

        // Mise à jour des informations de l'utilisateur
        this.user = user;
        labelDiplomePath.setText(user.getDiplome());
        labelNomPrenom.setText(user.getPrenom() + " " + user.getNom());
        tfNom.setText(user.getNom());
        tfPrenom.setText(user.getPrenom());
        tfEmail.setText(user.getEmail());
        iconHistorique.setImage(new Image(getClass().getResourceAsStream("/assets/icons/historique.png")));
        tfTelephone.setText(user.getTel());
        tfAdresse.setText(user.getAdresse());
        cbGroupeSanguin.setValue(user.getGroupe_Sanguin());
        cbGouvernorat.setValue(user.getGouvernorat());
        tfZipCode.setText(user.getZipCode());
        tfTaille.setText(String.valueOf(user.getTaille()));
        tfPoids.setText(String.valueOf(user.getPoids()));

        updateProgress();
        if (progressBar.getProgress() == 1.0) {
            addVerifiedBadge();
        }

        try {
            String path = "/assets/users/" + user.getPhoto();
            photoProfil.setImage(new Image(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            photoProfil.setImage(new Image(getClass().getResourceAsStream("/assets/userf.png")));
        }

        Circle clip = new Circle(photoProfil.getFitWidth() / 2, photoProfil.getFitHeight() / 2, photoProfil.getFitWidth() / 2);
        photoProfil.setClip(clip);
        editIcon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/modifier.png")));
        editIcon.setOnMouseClicked(event -> handleBrowsePhoto());
    }

    private void addVerifiedBadge() {
        Image badgeImage = new Image(getClass().getResourceAsStream("/assets/users/1.png"));
        ImageView badgeImageView = new ImageView(badgeImage);
        badgeImageView.setFitWidth(30);
        badgeImageView.setFitHeight(30);

        StackPane parent = (StackPane) labelNomPrenom.getParent();
        parent.getChildren().add(badgeImageView);

        StackPane.setAlignment(badgeImageView, Pos.TOP_RIGHT);
        StackPane.setMargin(badgeImageView, new Insets(-40, 100, 0, 0));  // Décalage à gauche
    }






    // Méthode pour afficher les messages motivants
    private void showMotivatingMessage(String message) {
        // Afficher le message motivant dans une alert, un label ou tout autre composant de l'interface
        alertMessage.setText(message);  // Par exemple, si tu as un Label `alertMessage` pour afficher le message
    }


    private void updateProgress() {
        int totalChamps = 11; // nombre total de champs à vérifier
        int champsRemplis = 0;
        if(isFieldValid(labelDiplomePath.getText()))champsRemplis++;
        if (isFieldValid(tfNom.getText())) champsRemplis++;
        if (isFieldValid(tfPrenom.getText())) champsRemplis++;
        if (isFieldValid(tfEmail.getText())) champsRemplis++;
        if (isFieldValid(tfTelephone.getText())) champsRemplis++;
        if (isFieldValid(tfAdresse.getText())) champsRemplis++;
        if (isFieldValid(tfZipCode.getText())) champsRemplis++;

        if (isValidTaille(tfTaille.getText())) champsRemplis++;
        if (isValidPoids(tfPoids.getText())) champsRemplis++;

        if (cbGroupeSanguin.getValue() != null && !cbGroupeSanguin.getValue().toString().equalsIgnoreCase("N/D")) champsRemplis++;
        if (cbGouvernorat.getValue() != null && !cbGouvernorat.getValue().toString().equalsIgnoreCase("N/D")) champsRemplis++;

        double progress = (double) champsRemplis / totalChamps;
        progressBar.setProgress(progress);
        progressPercentage.setText(String.format("%.0f%%", progress * 100));

        // ➔ Gestion du badge ici :
        if (progress < 1.0) {

        }
    }


    // Vérifie un champ texte classique
    private boolean isFieldValid(String text) {
        return text != null && !text.trim().isEmpty() && !text.equalsIgnoreCase("N/D");
    }

    // Vérifie que la taille est valide et différente de 0
    private boolean isValidTaille(String text) {
        if (isFieldValid(text)) {
            try {
                double value = Double.parseDouble(text.trim());
                return value != 0.0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    // Vérifie que le poids est valide et différent de 0
    private boolean isValidPoids(String text) {
        if (isFieldValid(text)) {
            try {
                double value = Double.parseDouble(text.trim());
                return value != 0.0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }



    @FXML
    private void handleAfficherHistorique() {
        handleHistory(user);
    }

    @FXML
    private void handleSave() {
        if (tfNom.getText().isEmpty() || tfPrenom.getText().isEmpty() || tfEmail.getText().isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        boolean isModified = false;

        if (!user.getNom().equals(tfNom.getText())) {
            user.setNom(tfNom.getText());
            isModified = true;
        }

        if (user.getDiplome() == null || !user.getDiplome().equals(labelDiplomePath.getText())) {
            user.setDiplome(labelDiplomePath.getText());
            isModified = true;
        }

        if (!user.getPrenom().equals(tfPrenom.getText())) {
            user.setPrenom(tfPrenom.getText());
            isModified = true;
        }

        if (!user.getEmail().equals(tfEmail.getText())) {
            user.setEmail(tfEmail.getText());
            isModified = true;
        }

        if (!user.getTel().equals(tfTelephone.getText())) {
            user.setTel(tfTelephone.getText());
            isModified = true;
        }

        if (!user.getAdresse().equals(tfAdresse.getText())) {
            user.setAdresse(tfAdresse.getText());
            isModified = true;
        }

        if (user.getGroupe_Sanguin() == null || !user.getGroupe_Sanguin().equals(cbGroupeSanguin.getValue())) {
            user.setGroupe_Sanguin((String) cbGroupeSanguin.getValue());
            isModified = true;
        }

        if (user.getGouvernorat() == null || !user.getGouvernorat().equals(cbGouvernorat.getValue())) {
            user.setGouvernorat((String) cbGouvernorat.getValue());
            isModified = true;
        }

        if (!user.getZipCode().equals(tfZipCode.getText())) {
            user.setZipCode(tfZipCode.getText());
            isModified = true;
        }

        try {
            if (!tfTaille.getText().isEmpty()) {
                float taille = Float.parseFloat(tfTaille.getText());
                if (user.getTaille() != taille) {
                    user.setTaille(taille);
                    isModified = true;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Veuillez entrer une taille valide.");
            return;
        }

        try {
            if (!tfPoids.getText().isEmpty()) {
                float poids = Float.parseFloat(tfPoids.getText());
                if (user.getPoids() != poids) {
                    user.setPoids(poids);
                    isModified = true;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Veuillez entrer un poids valide.");
            return;
        }

        if (photoModifiee) {
            isModified = true;
        }

        // Gestion du mot de passe
        String oldPass = tfOldPassword.getText();
        String newPass = tfNewPassword.getText();

        if (!newPass.isEmpty()) {
            if (oldPass.isEmpty()) {
                showAlert("Erreur", "Veuillez saisir l'ancien mot de passe.");
                return;
            }

            if (!oldPass.equals(user.getPassword())) {
                showAlert("Erreur", "Mot de passe actuel incorrect.");
                return;
            }

            if (!isValidPassword(newPass)) {
                showAlert("Erreur", "Le nouveau mot de passe doit contenir au moins 6 caractères, une lettre majuscule et un chiffre.");
                return;
            }

            user.setPassword(newPass); // pas de hash ici
            isModified = true;
        }


        if (!isModified) {
            showAlert("ℹ️ Aucun changement", "Aucune modification détectée.");
            return;
        }

        // ✅ Confirmation de l'enregistrement
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Enregistrer les modifications ?");
        confirm.setContentText("Êtes-vous sûr de vouloir sauvegarder les modifications de votre profil ?");

        ButtonType oui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(oui, non);

        confirm.showAndWait().ifPresent(response -> {
            if (response == oui) {
                // Enregistrer les modifications dans la base de données
                userService.updateUserWithPassword(user);

                refreshUserData();


                showAlert("✅ Succès", "Profil mis à jour avec succès.");
            } else {
                System.out.println("❌ Modification annulée par l'utilisateur.");
            }
        });
    }

    private void refreshUserData() {
        // Mettre à jour tous les champs avec les nouvelles valeurs
        labelDiplomePath.setText(user.getDiplome());
        tfNom.setText(user.getNom());
        tfPrenom.setText(user.getPrenom());
        tfEmail.setText(user.getEmail());
        tfTelephone.setText(user.getTel());
        tfAdresse.setText(user.getAdresse());
        cbGroupeSanguin.setValue(user.getGroupe_Sanguin());
        cbGouvernorat.setValue(user.getGouvernorat());
        tfZipCode.setText(user.getZipCode());
        tfTaille.setText(String.valueOf(user.getTaille()));
        tfPoids.setText(String.valueOf(user.getPoids()));
        if (user.getPhoto() != null) {
            // Mettre à jour l'image de l'utilisateur (si applicable)
        }
        updateProgress();
    }



    @FXML
    private void handleBrowsePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une nouvelle photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(photoProfil.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName();

                // Nouveau chemin vers le répertoire d'images
                String newPath = "C:/wamp64/www/images/" + fileName;
                File destination = new File(newPath);
                destination.getParentFile().mkdirs();  // Crée les répertoires parents si nécessaire

                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mise à jour dans l'objet user
                user.setPhoto(fileName);
                photoProfil.setImage(new Image(new FileInputStream(newPath)));  // Utilisation de FileInputStream ici

                photoModifiee = true; // 👉 indique que la photo a été modifiée

                System.out.println("✅ Photo de profil mise à jour : " + fileName);
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de la copie : " + e.getMessage());
                showAlert("Erreur", "Impossible de copier la photo.");
            }
        }
    }


    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&    // au moins une majuscule
                password.matches(".*\\d.*");        // au moins un chiffre
    }

    private void handleHistory(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HistoriqueConnexion.fxml"));
            Parent root = loader.load();

            HistoriqueConnexionController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Historique de " + user.getNom());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'historique.");
        }
    }

    @FXML
    private void handleBrowseDiplome(ActionEvent event) {
        // Création d'un FileChooser pour sélectionner le fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un diplôme");

        // Optionnel : Filtrage pour n'accepter que les fichiers PDF
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Ouverture du FileChooser
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File diplomeFile = fileChooser.showOpenDialog(stage);

        // Vérification si un fichier a été sélectionné
        if (diplomeFile != null) {
            // Dossier où le fichier sera enregistré
            File destinationDir = new File("src/main/resources/assets/diplomes/");

            // Création du dossier s'il n'existe pas
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }

            // Création du fichier de destination avec le même nom que le fichier source
            File destinationFile = new File(destinationDir, diplomeFile.getName());

            // Copie du fichier sélectionné dans le dossier de destination
            try {
                Files.copy(diplomeFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Lecture du contenu du fichier PDF
                try (PDDocument document = PDDocument.load(destinationFile)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String pdfContent = stripper.getText(document);
                    System.out.println("Contenu du fichier PDF :");
                    System.out.println(pdfContent); // Affiche le texte du fichier PDF dans la console

                    // Extraction des informations spécifiques
                    String nomEtudiant = extractNomEtudiant(pdfContent);  // Extraire le nom de l'étudiant
                    String numeroDiplome = extractNumeroDiplome(pdfContent); // Extraire le numéro du diplôme
                    String nomUniversite = extractNomUniversite(pdfContent); // Extraire le nom de l'université
                    System.out.println("Nom étudiant: " + nomEtudiant);
                    System.out.println("Numéro diplôme: " + numeroDiplome);
                    System.out.println("Nom université: " + nomUniversite);

                    // Vérification dans la base de données
                    boolean isDiplomeValide = VERIFY.verifyDiplomeInDatabase(nomEtudiant, numeroDiplome, nomUniversite);

                    if (isDiplomeValide) {
                        labelDiplomePath.setText("Diplôme valide : " + destinationFile.getName());
                    } else {
                        labelDiplomePath.setText("Le diplôme n'est pas valide.");
                    }

                } catch (IOException e) {
                    System.err.println("Erreur lors de la lecture du fichier PDF");
                    e.printStackTrace();
                }

            } catch (IOException e) {
                // Gestion des erreurs lors de la copie du fichier
                e.printStackTrace();
            }
        } else {
            // Cas où l'utilisateur annule le choix de fichier
            System.out.println("Aucun fichier sélectionné.");
        }
    }

    private String extractNomEtudiant(String pdfContent) {
        // Exemple d'extraction du nom de l'étudiant
        String nomEtudiant = "Nom Non Trouvé";  // Valeur par défaut
        if (pdfContent.contains("Nom :")) {
            nomEtudiant = pdfContent.split("Nom :")[1].split("\n")[0].trim();
        }
        return nomEtudiant;
    }

    private String extractNumeroDiplome(String pdfContent) {
        // Exemple d'extraction du numéro du diplôme
        String numeroDiplome = "Numéro Non Trouvé";  // Valeur par défaut
        if (pdfContent.contains("Numéro du diplôme :")) {
            numeroDiplome = pdfContent.split("Numéro du diplôme :")[1].split("\n")[0].trim();
        }
        return numeroDiplome;
    }

    private String extractNomUniversite(String pdfContent) {
        // Exemple d'extraction du nom de l'université
        String nomUniversite = "Université Non Trouvée";  // Valeur par défaut
        if (pdfContent.contains("Université :")) {
            nomUniversite = pdfContent.split("Université :")[1].split("\n")[0].trim();
        }
        return nomUniversite;
    }

}
