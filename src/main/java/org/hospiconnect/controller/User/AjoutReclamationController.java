package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.hospiconnect.model.Reclamation;
import org.hospiconnect.service.ReclamationService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AjoutReclamationController {

    // Champs de formulaire
    @FXML private TextField tfTitle;
    @FXML private TextArea taDescription;
    @FXML private DatePicker dpDateReclamation;
    @FXML private ComboBox<String> comboCategory;
    @FXML private ComboBox<String> comboAnonymous;

    private final ReclamationService reclamationService = new ReclamationService();
    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    public void initialize() {

        // Initialisation des valeurs par défaut
        dpDateReclamation.setValue(LocalDate.now());

        // Initialisation des choix des ComboBox
        comboCategory.getItems().addAll("Technique", "Administrative", "Autre");
        comboCategory.getSelectionModel().selectFirst();

        comboAnonymous.getItems().addAll("Oui", "Non");
        comboAnonymous.getSelectionModel().select("Non");
    }

    @FXML
    private void handleBrowseAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.xlsx"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            // Vérifier la présence de gros mots avant de créer et enregistrer la réclamation
            String description = taDescription.getText();
            if (containsBadWords(description)) {
                showAlert(Alert.AlertType.ERROR, "Contenu inapproprié", "Votre description contient des mots inappropriés. Veuillez la corriger.");
                return; // Stopper ici : on n'ajoute pas la réclamation
            }

            try {
                Reclamation reclamation = createReclamationFromForm();
                reclamationService.addReclamation(reclamation);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation enregistrée avec succès !");
                clearForm();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Reclamation createReclamationFromForm() {
        Reclamation reclamation = new Reclamation();
        reclamation.setTitle(tfTitle.getText());
        reclamation.setDescription(taDescription.getText());
        reclamation.setDateReclamation(convertToDate(dpDateReclamation.getValue()));
        reclamation.setCategory(comboCategory.getValue());
        reclamation.setIsAnonymous("Oui".equals(comboAnonymous.getValue()));
        String priority = determinePriority(taDescription.getText());
        reclamation.setPriority(priority);
        reclamation.setUserId((long) userId);

        return reclamation;
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private String determinePriority(String description) {
        // Tout mettre en minuscule
        String lowerDesc = description.toLowerCase();

        // Supprimer tous les espaces pour détecter les mots fragmentés
        String cleanedDesc = lowerDesc.replaceAll("\\s+", "");

        // Mots-clés qui déclenchent HIGH
        if (cleanedDesc.contains("urgent") || cleanedDesc.contains("immédiat") || cleanedDesc.contains("critique") || cleanedDesc.contains("important")) {
            return "HIGH";
        }
        // Mots-clés qui déclenchent MEDIUM
        else if (cleanedDesc.contains("lent") || cleanedDesc.contains("retard") || cleanedDesc.contains("problèmemineur")) {
            return "MEDIUM";
        }
        // Sinon, LOW par défaut
        else {
            return "LOW";
        }
    }

    private boolean containsBadWords(String description) {
        String[] badWords = {
                // Français
                "merde", "con", "connard", "salope", "putain", "enculé", "bâtard", "chiant", "bordel", "foutre",
                // Anglais
                "fuck", "shit", "bitch", "asshole", "bastard", "crap", "damn", "motherfucker", "douchebag", "piss", "slut"
        };

        String lowerDesc = description.toLowerCase();

        for (String badWord : badWords) {
            if (lowerDesc.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        String title = tfTitle.getText().trim();
        String description = taDescription.getText().trim();

        // Vérification du titre
        if (title.isEmpty()) {
            errors.append("- Le titre est obligatoire.\n");
        } else {
            if (title.length() > 100) {
                errors.append("- Le titre ne doit pas dépasser 100 caractères.\n");
            }
            if (!title.matches("[a-zA-ZÀ-ÿ0-9\\s\\-_,.?!()]+")) {
                errors.append("- Le titre contient des caractères non autorisés.\n");
            }
        }

        // Vérification de la description
        if (description.isEmpty()) {
            errors.append("- La description est obligatoire.\n");
        } else {
            if (description.length() > 1000) {
                errors.append("- La description ne doit pas dépasser 1000 caractères.\n");
            }
        }

        // Vérification de la catégorie
        if (comboCategory.getValue() == null || comboCategory.getValue().trim().isEmpty()) {
            errors.append("- La catégorie est obligatoire.\n");
        }

        // Vérification de l'anonymat
        if (comboAnonymous.getValue() == null || comboAnonymous.getValue().trim().isEmpty()) {
            errors.append("- Veuillez spécifier si la réclamation est anonyme.\n");
        }

        // Vérification de la date
        LocalDate selectedDate = dpDateReclamation.getValue();
        LocalDate today = LocalDate.now();
        if (selectedDate == null) {
            errors.append("- La date de réclamation est obligatoire.\n");
        } else if (selectedDate.isBefore(today)) {
            errors.append("- La date ne peut pas être antérieure à aujourd'hui.\n");
        }

        // Affichage des erreurs si elles existent
        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validation", errors.toString());
            return false;
        }

        return true;
    }

    private void clearForm() {
        tfTitle.clear();
        taDescription.clear();
        dpDateReclamation.setValue(LocalDate.now());
        comboCategory.getSelectionModel().selectFirst();
        comboAnonymous.getSelectionModel().select("Non");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleVoiceInput(ActionEvent event) {
        try {
            // Passer la référence au TextArea taDescription
            VoiceRecognition.recognizeSpeech(taDescription);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la reconnaissance vocale: " + e.getMessage());
        }
    }

}
