package org.hospiconnect.controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hospiconnect.model.RendezVous;
import org.hospiconnect.service.RendezVousSMSService;
import org.hospiconnect.service.RendezVousService;
import org.hospiconnect.service.OpenAiService;
import org.hospiconnect.utils.SceneUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class AddRendezVousController {

    @FXML
    private TextField nomTF;
    @FXML
    private TextField prenomTF;
    @FXML
    private TextField telTF;
    @FXML
    private TextField emailTF;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField heureTF;
    @FXML
    private ComboBox<String> typeCB;
    @FXML
    private ComboBox<String> graviteCB;
    @FXML
    private TextField commentaireTF;

    private final RendezVousService rendezVousService = new RendezVousService();

    @FXML
    public void initialize() {
        // Initialisation des ComboBox
        typeCB.setItems(FXCollections.observableArrayList("Opération", "Intervention Urgence"));
        graviteCB.setItems(FXCollections.observableArrayList("Faible", "Moyenne", "Elevée"));
    }

    @FXML
    void AjouterRendezVous(ActionEvent event) {
        // Validation des champs
        if (!validerFormulaire()) {
            return;
        }

        try {
            // Création du rendez-vous
            RendezVous rdv = new RendezVous();
            rdv.setNom(nomTF.getText().trim());
            rdv.setPrenom(prenomTF.getText().trim());
            rdv.setTelephone(telTF.getText().trim());
            rdv.setEmail(emailTF.getText().trim());
            rdv.setDate(datePicker.getValue());
            rdv.setHeure(LocalTime.parse(heureTF.getText().trim(), DateTimeFormatter.ofPattern("HH:mm")));
            rdv.setType(typeCB.getValue());
            rdv.setGravite(graviteCB.getValue());
            rdv.setCommentaire(commentaireTF.getText().trim());


            // Ajout en base de données
            rendezVousService.insert(rdv);

            // Envoi du SMS de confirmation
            envoyerSMSConfirmation(rdv);


            // Affichage du message de succès
            showAlert("Succès", "Rendez-vous ajouté avec succès");
            clearForm();
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format d'heure invalide (HH:mm attendu)");
        } catch (Exception e) { // Changed from SQLException to Exception
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }


    }

    private boolean validerFormulaire() {
        // Validation des champs obligatoires
        if (nomTF.getText().trim().isEmpty() || prenomTF.getText().trim().isEmpty() ||
                telTF.getText().trim().isEmpty() || emailTF.getText().trim().isEmpty() ||
                datePicker.getValue() == null || heureTF.getText().trim().isEmpty() ||
                typeCB.getValue() == null || graviteCB.getValue() == null) {
            showAlert("Erreur", "Tous les champs obligatoires doivent être remplis");
            return false;
        }

        // Validation email
        if (!emailTF.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Erreur Email", "Format d'email invalide");
            return false;
        }

        // Validation téléphone
        String phoneNumber = formatPhoneNumber(telTF.getText().trim());
        if (!phoneNumber.matches("^\\+216[0-9]{8}$")) {
            showAlert("Erreur Téléphone", "Le numéro de téléphone doit être au format tunisien (+216 suivi de 8 chiffres)");
            return false;
        }
        telTF.setText(phoneNumber); // Mettre à jour le champ avec le numéro formaté

        // Validation heure
        if (!heureTF.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert("Erreur Heure", "Format d'heure invalide (HH:mm attendu)");
            return false;
        }

        // Validation date
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showAlert("Erreur Date", "La date ne peut pas être dans le passé");
            return false;
        }

        return true;
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Supprimer tous les caractères non numériques
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        // Si le numéro commence par 216, ajouter le +
        if (digits.startsWith("216")) {
            return "+" + digits;
        }

        // Si le numéro commence par 0, le remplacer par +216
        if (digits.startsWith("0")) {
            return "+216" + digits.substring(1);
        }

        // Si le numéro a 8 chiffres, ajouter +216
        if (digits.length() == 8) {
            return "+216" + digits;
        }

        return phoneNumber; // Retourner le numéro original si aucun format ne correspond
    }

    private void clearForm() {
        nomTF.clear();
        prenomTF.clear();
        telTF.clear();
        emailTF.clear();
        datePicker.setValue(null);
        heureTF.clear();
        typeCB.setValue(null);
        graviteCB.setValue(null);
        commentaireTF.clear();
    }

    @FXML
    void AfficherListe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeRendezVous.fxml"));
            Parent root = loader.load();

            // Rafraîchir les données dans le contrôleur de liste
            ListeRendezVousController controller = loader.getController();
            controller.refreshData();

            // Changer de scène
            Stage stage = (Stage) nomTF.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des rendez-vous");
            e.printStackTrace();
        }
    }


    private void envoyerSMSConfirmation(RendezVous rdv) {
        try {
            String message = String.format(
                    "Bonjour %s %s,\nVotre rendez-vous pour %s est confirmé le %s à %s.\nMerci!",
                    rdv.getPrenom(),
                    rdv.getNom(),
                    rdv.getType(),
                    rdv.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    rdv.getHeure().format(DateTimeFormatter.ofPattern("HH:mm"))
            );

            RendezVousSMSService.getInstance().sendSms(message, rdv.getTelephone());
        } catch (Exception e) {
            // On catch l'exception mais on ne bloque pas le flux
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void openChatbot() {
        // Configuration de la fenêtre
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #ffffff;");

        // Composants UI
        Label title = new Label("Assistant Virtuel HospiConnect");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #003366;");

        TextField inputField = new TextField();
        inputField.setPromptText("Posez votre question médicale...");
        inputField.setPrefWidth(350);
        inputField.setStyle("-fx-border-color: #003366; -fx-border-radius: 5; -fx-background-color: #F0F8FF;");

        Button sendButton = new Button("Envoyer");
        sendButton.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white; -fx-font-weight: bold;");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        // Organisation de l'interface
        HBox inputBox = new HBox(10, inputField, sendButton, progressIndicator);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        VBox chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setPrefHeight(400);

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);

        layout.getChildren().addAll(title, scrollPane, inputBox);

        // Configuration de la scène
        Scene scene = new Scene(layout, 550, 500);
        stage.setTitle("HospiChat");
        stage.setScene(scene);
        stage.show();

        // Message de bienvenue
        addBotMessage(chatBox, "Bonjour ! Je suis HospiChat, votre assistant médical virtuel. Comment puis-je vous aider aujourd'hui ?");

        // Gestion des interactions
        sendButton.setOnAction(e -> {
            String question = inputField.getText().trim();
            if (!question.isEmpty()) {
                addUserMessage(chatBox, question);
                inputField.clear();
                progressIndicator.setVisible(true);

                new Thread(() -> {
                    try {
                        String response = OpenAiService.getInstance().askOpenAi(question);

                        Platform.runLater(() -> {
                            addBotMessage(chatBox, response);
                            scrollPane.setVvalue(1.0);
                            progressIndicator.setVisible(false);
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            addBotMessage(chatBox, "Désolé, une erreur est survenue : " + ex.getMessage());
                            progressIndicator.setVisible(false);
                        });
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        inputField.setOnAction(e -> sendButton.fire());
    }

    private void addUserMessage(VBox chatBox, String message) {
        Label label = new Label("Vous: " + message);
        label.setStyle("-fx-background-color: #e1f0ff; -fx-padding: 10; -fx-background-radius: 10;");
        label.setWrapText(true);
        Platform.runLater(() -> chatBox.getChildren().add(label));
    }

    private void addBotMessage(VBox chatBox, String message) {
        Label label = new Label("HospiChat: " + message);
        label.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 10;");
        label.setWrapText(true);
        Platform.runLater(() -> chatBox.getChildren().add(label));
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}