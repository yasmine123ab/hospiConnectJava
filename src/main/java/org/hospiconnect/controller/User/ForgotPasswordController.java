package org.hospiconnect.controller.User;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.hospiconnect.utils.DatabaseUtils;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtCode;
    @FXML
    private TextField txtNewPassword;
    @FXML
    private TextField txtConfirmPassword;

    // Méthode pour afficher les alertes
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void ForgetPassword(ActionEvent actionEvent) {
        String email = txtEmail.getText().trim();
        System.out.println("Email saisi : " + email);
        resetPassword(email);
    }

    private void resetPassword(String email) {
        String sqlSelect = "SELECT * FROM user WHERE email = ?";
        String sqlUpdateMot = "UPDATE user SET mot = ? WHERE email = ?";

        // Paramètres Twilio pour l'envoi du SMS
        String twilioAccountSid = System.getenv("ACbdd76be8b25df5a872b4666fca3f6b06");
        String twilioAuthToken = System.getenv("238900f59cceed2c2fa92b30085ed3ec");
        String twilioPhoneNumber = System.getenv("+19472253205");

        if (twilioAccountSid == null || twilioAuthToken == null || twilioPhoneNumber == null) {
            throw new IllegalStateException("Les identifiants Twilio ne sont pas configurés");
        }

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {

            psSelect.setString(1, email);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                String newPassword = generateRandomPassword(8); // Génère un nouveau mot de passe aléatoire
                String userEmail = rs.getString("email"); // Récupère l'email de l'utilisateur
                String userPhoneNumber = rs.getString("tel"); // Supposons que le numéro de téléphone de l'utilisateur est dans la base de données

                // Mise à jour du mot de passe dans la base de données
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateMot)) {
                    psUpdate.setString(1, newPassword);
                    psUpdate.setString(2, email);
                    psUpdate.executeUpdate();
                    System.out.println("Mot de passe réinitialisé : " + newPassword);

                    // Envoi du SMS avec Twilio
                    String messageBody = "Votre mot de passe a été réinitialisé. Votre nouveau mot de passe est : " + newPassword;
                    sendSmsWithTwilio(userPhoneNumber, messageBody, twilioAccountSid, twilioAuthToken, twilioPhoneNumber);

                    // Afficher une alerte de succès
                    showAlert(AlertType.INFORMATION, "Réinitialisation réussie",
                            "Un nouveau mot de passe a été envoyé à votre téléphone.");

                    // Rediriger vers une autre page si nécessaire
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RESET.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) txtEmail.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                }
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Aucun utilisateur trouvé avec cet email.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur SQL", "Erreur de connexion à la base de données : " + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'opération : " + e.getMessage());
        }
    }

    private void sendSmsWithTwilio(String toPhoneNumber, String messageBody, String accountSid, String authToken, String fromPhoneNumber) {
        try {
            // Initialisation de Twilio
            Twilio.init(accountSid, authToken);

            // Envoi du SMS
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),  // Numéro de téléphone du destinataire
                    new PhoneNumber(fromPhoneNumber),  // Votre numéro Twilio
                    messageBody  // Contenu du message
            ).create();

            System.out.println("Message envoyé à " + toPhoneNumber + ": " + message.getSid());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur SMS", "Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public void handleResetPassword(ActionEvent actionEvent) {
        String code = txtCode.getText().trim();
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        String sqlSelect = "SELECT * FROM user WHERE mot = ?";
        String sqlUpdate = "UPDATE user SET password = ? WHERE mot = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {

            psSelect.setString(1, code);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                String hashedPassword =newPassword;

                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, hashedPassword);
                    psUpdate.setString(2, code);
                    psUpdate.executeUpdate();
                    System.out.println("Mot de passe mis à jour avec succès !");

                    // Afficher une alerte de succès
                    showAlert(AlertType.INFORMATION, "Succès", "Votre mot de passe a été réinitialisé avec succès !");

                    // Rediriger vers la page de connexion
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) txtCode.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                }
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Code invalide.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur SQL", "Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'opération : " + e.getMessage());
        }
    }

    public void Retour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Espace artisan");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur de chargement", "Le fichier FXML n'a pas pu être chargé.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
