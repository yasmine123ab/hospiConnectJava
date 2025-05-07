package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hospiconnect.model.User;
import org.hospiconnect.service.UserService;

import java.io.IOException;

public class A2FController {

    @FXML private TextField txtVerificationCode;
    @FXML private Button btnVerifyCode;
    @FXML private Button btnBackToLogin;

    private User session;
    private final UserService userService;

    // Constructeur pour injecter UserService (pour éviter la recréation)
    public A2FController() {
        this.userService = new UserService();
    }

    // Méthode pour passer l'utilisateur connecté de LoginController à A2FController
    public void setUser(User user) {
        this.session = user;
    }

    @FXML
    public void handleVerifyCode(ActionEvent actionEvent) {
        String verificationCode = txtVerificationCode.getText().trim();

        if (verificationCode.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Le code de vérification est requis !");
            return;
        }

        if (session == null) {
            showAlert(Alert.AlertType.ERROR, "Utilisateur non connecté.");
            return;
        }

        // Récupérer l'utilisateur connecté via son email
        String email = session.getEmail();
        User userFromDb = userService.findUserByEmail(email);

        if (userFromDb == null) {
            showAlert(Alert.AlertType.ERROR, "Utilisateur non trouvé.");
            return;
        } else {
            // Afficher toutes les données de l'utilisateur dans la console
            System.out.println("Détails de l'utilisateur récupéré:");
            System.out.println("Nom: " + userFromDb.getNom());
            System.out.println("Prénom: " + userFromDb.getPrenom());
            System.out.println("Email: " + userFromDb.getEmail());
            System.out.println("Roles: " + userFromDb.getRoles());
            System.out.println("Statut: " + userFromDb.getStatut());
            System.out.println("Photo: " + userFromDb.getPhoto());
            System.out.println("Groupe sanguin: " + userFromDb.getGroupe_Sanguin());
            System.out.println("Numéro de téléphone: " + userFromDb.getTel());
            System.out.println("Sexe: " + userFromDb.getSexe());
            System.out.println("Code postal: " + userFromDb.getZipCode());
            System.out.println("Adresse: " + userFromDb.getAdresse());
            System.out.println("Gouvernorat: " + userFromDb.getGouvernorat());
            System.out.println("Poids: " + userFromDb.getPoids());
            System.out.println("Taille: " + userFromDb.getTaille());
            System.out.println("IMC: " + userFromDb.getImc());
            System.out.println("Diplôme: " + userFromDb.getDiplome());
            System.out.println("A2F: " + userFromDb.getA2F());
        }

        // Vérification si le code de vérification est nul
        if (userFromDb.getA2F() == null) {
            showAlert(Alert.AlertType.ERROR, "Le code de vérification n'a pas été généré.");
            return;
        }

        // Comparer le code de la base de données avec celui saisi
        if (userFromDb.getA2F().equals(verificationCode)) {
            showAlert(Alert.AlertType.INFORMATION, "Vérification réussie !");
            loadUserDashboard(); // Charger la page en fonction du rôle de l'utilisateur
        } else {
            showAlert(Alert.AlertType.ERROR, "Code incorrect. Veuillez réessayer.");
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent actionEvent) {
        // Retourner à la page de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBackToLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Page de connexion");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de connexion.");
            e.printStackTrace();
        }
    }

    private void loadUserDashboard() {
        if (session == null) {
            showAlert(Alert.AlertType.ERROR, "Utilisateur non connecté.");
            return;
        }

        String roles = session.getRoles();

        // Vérification des rôles et appel de la méthode loadPage
        if (roles.contains("ADMIN") || roles.contains("SUPER_ADMIN")) {
            loadPage("/HomePages/backList.fxml", "Tableau de bord admin", session);
        } else if (roles.contains("MEDECIN") || roles.contains("PERSONNEL") || roles.contains("CLIENT")) {
            loadPage("/HomePages/frontList.fxml", "Espace client", session);
        } else if (roles.contains("ROLE_ARTISAN")) {
            loadPage("/artisan_dashboard.fxml", "Espace artisan", null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Rôle inconnu.");
        }
    }

    private void loadPage(String fxmlPath, String title, User userFromDb) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Vérifier si un utilisateur est passé pour initialiser les pages spécifiques
            if (userFromDb != null) {
                if (fxmlPath.equals("/HomePages/backList.fxml")) {
                    // Initialisation pour le DashboardController
                    DashboardController.setSession(userFromDb);  // Passer l'utilisateur au contrôleur
                } else if (fxmlPath.equals("/HomePages/frontList.fxml")) {
                    // Initialisation pour le FrontClientController

                    FrontClientController.setSession(userFromDb);  // Passer l'utilisateur au contrôleur
                }
            }

            // Mise à jour de la scène à partir du bouton (btnVerifyCode)
            Stage stage = (Stage) btnVerifyCode.getScene().getWindow();  // Utiliser btnVerifyCode
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
