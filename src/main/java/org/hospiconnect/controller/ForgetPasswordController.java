package org.hospiconnect.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgetPasswordController {

    // Déclarez la référence de l'élément FXML
    @FXML
    private Hyperlink linkBackToLogin;

    // Méthode appelée lors du clic sur le lien "Retour à la connexion"
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            StackPane loginRoot = loader.load();

            // Créer une nouvelle scène pour la page de connexion
            Scene loginScene = new Scene(loginRoot);

            // Récupérer le stage actuel (celui qui contient la scène ForgetPassword)
            Stage stage = (Stage) linkBackToLogin.getScene().getWindow();

            // Mettre la scène de login sur le stage actuel
            stage.setScene(loginScene);
            stage.setTitle("Page de connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
