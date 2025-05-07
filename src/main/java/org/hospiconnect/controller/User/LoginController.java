package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

import org.hospiconnect.service.UserService;
import org.hospiconnect.model.User;
import javafx.scene.Parent;

import javax.mail.MessagingException;

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

        btnLogin.setOnAction(e -> {
            try {
                handleLogin();
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
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
    private void handleLogin() throws MessagingException {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont requis !");
            return;
        }

        UserService userService = new UserService();

        // Vérifier si l'utilisateur existe
        User userFromDb = userService.findUserByEmail(email);
        if (userFromDb == null) {
            showAlert(Alert.AlertType.ERROR, "Email incorrect.");
            return;
        }

        // Vérifier si l'utilisateur est bloqué
        if ("BLOQUER".equalsIgnoreCase(userFromDb.getStatut())) {
            showAlert(Alert.AlertType.ERROR, "Votre compte est bloqué. Veuillez contacter l’administrateur.");
            return;
        }

        // Vérifier mot de passe
        if (!password.equals(userFromDb.getPassword())) {
            userService.incrementerTC(email);
            showAlert(Alert.AlertType.ERROR, "Mot de passe incorrect.");
            return;
        }


        // Générer un code A2F et l'ajouter à l'utilisateur
        String a2fCode = generateA2FCode();
        userService.setA2FCode(email,a2fCode);
        EmailService.sendVerificationCode(email,a2fCode);
        userService.updateUser(userFromDb);// Assurez-vous que la méthode setA2FCode existe dans la classe User

        // Connexion réussie, transition vers la page A2F
        openA2FPage(userFromDb);
    }


    private String generateA2FCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un code à 6 chiffres
        return String.valueOf(code);
    }

    private void openA2FPage(User userFromDb) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/A2F.fxml"));
            Parent root = loader.load();
            A2FController controller = loader.getController();
            controller.setUser(userFromDb); // Passer l'utilisateur à la page A2F
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Vérification 2FA");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void OublierMotDePasse(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ForgetPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Espace artisan");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleFaceIDLogin(ActionEvent actionEvent) {
        // Logique pour l'identification par FaceID
    }
}
