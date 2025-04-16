package org.hospiconnect.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import service.HistoriqueConnexionService;
import service.UserService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private ImageView logoImage;
    @FXML private ImageView googleIcon;
    @FXML private ImageView faceIcon;
    @FXML private Hyperlink linkSignup;

    @FXML
    public void initialize() {
        // Chargement des images
        logoImage.setImage(new Image(getClass().getResourceAsStream("/HOSPI.png")));
        googleIcon.setImage(new Image(getClass().getResourceAsStream("/google.png")));
        faceIcon.setImage(new Image(getClass().getResourceAsStream("/face-id.png")));

        btnLogin.setOnAction(e -> handleLogin());
        linkSignup.setOnAction(e -> openSignupPage());

    }
    private void openSignupPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            StackPane signupRoot = loader.load();
            Scene scene = new Scene(signupRoot);
            Stage stage = (Stage) linkSignup.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont requis !");
            return;
        }

        UserService userService = new UserService();
        User user = userService.login(email, password);


        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect.");
            return;
        }
        new HistoriqueConnexionService().enregistrerConnexion(user);

        // Vérification du rôle
        String roles = user.getRoles();
        if (roles.contains("ROLE_ADMIN")) {
            // Rediriger vers dashboard.fxml
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();
                controller.initialize(user); // si tu veux passer les infos du user
                Stage stage = (Stage) txtEmail.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Tableau de bord admin");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (roles.contains("ROLE_CLIENT")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/front.fxml"));
                Parent root = loader.load();

// Récupérer le contrôleur
                FrontClientController controller = loader.getController();

// Transmettre l'utilisateur connecté
                controller.setConnectedUser(user); // ← ton objet User authentifié

// Changer la scène
                Stage stage = (Stage) txtEmail.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Espace client");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (roles.contains("ROLE_ARTISAN")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) txtEmail.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Espace artisan");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Rôle inconnu.");
        }

    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ForgetPassword.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Mot de passe oublié");
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace(); // tu peux aussi afficher une alerte ici
        }
    }
}
