package org.hospiconnect.controller.User;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.hospiconnect.model.User;
import org.hospiconnect.service.HistoriqueConnexionService;
import org.hospiconnect.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Pattern;

public class SignupController {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnRegister;
    @FXML private ImageView logoImage;
    @FXML private ImageView googleIcon;
    @FXML private Hyperlink linkLogin;
    @FXML private Label passwordStrengthLabel;
    @FXML private ProgressBar passwordStrengthBar;

    private final UserService userService = new UserService();
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize() {
        // Charger les images
        logoImage.setImage(new Image(getClass().getResourceAsStream("/HOSPI.png")));
        googleIcon.setImage(new Image(getClass().getResourceAsStream("/google.png")));

        btnRegister.setOnAction(event -> {
            afficherDonnees();
            handleRegister();
        });

        linkLogin.setOnAction(e -> navigateToLogin());

        // Ajouter un listener sur le champ de mot de passe
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
        });
    }

    @FXML
    private void handleGoogleSignup() {
        try {
            String clientId = "617338538271-ie7un0phaqu2drto2rv06t3fhreviaid.apps.googleusercontent.com";
            String redirectUri = "http://localhost:8080/oauth2callback";
            String scope = "email profile";

            String authUrl = "https://accounts.google.com/o/oauth2/auth?" +
                    "client_id=" + clientId +
                    "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                    "&response_type=code" +
                    "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                    "&access_type=offline";

            openAuthWebView(authUrl);

        } catch (Exception e) {
            showError("Erreur Google Auth: " + e.getMessage());
        }
    }

    private void openAuthWebView(String authUrl) {
        Stage stage = new Stage();
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        engine.load(authUrl);

        engine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl != null && newUrl.contains("?code=")) {
                String code = newUrl.split("code=")[1].split("&")[0];
                exchangeCodeForToken(code);
                stage.close();
            } else if (newUrl != null && newUrl.contains("error=")) {
                Platform.runLater(() -> {
                    showError("Erreur d'authentification: " + newUrl.split("error=")[1]);
                    stage.close();
                });
            }
        });

        stage.setOnCloseRequest(event -> {
            Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Connexion Google annulée"));
        });

        stage.setScene(new Scene(webView, 600, 800));
        stage.setTitle("Connexion Google");
        stage.show();
    }

    private void exchangeCodeForToken(String code) {
        try {
            String clientId = "617338538271-ie7un0phaqu2drto2rv06t3fhreviaid.apps.googleusercontent.com";
            String clientSecret = "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"; // À remplacer
            String redirectUri = "http://localhost:8080/oauth2callback";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "code=" + code +
                                    "&client_id=" + clientId +
                                    "&client_secret=" + clientSecret +
                                    "&redirect_uri=" + redirectUri +
                                    "&grant_type=authorization_code"))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            String accessToken = jsonResponse.get("access_token").getAsString();

            getUserInfo(accessToken);

        } catch (Exception e) {
            Platform.runLater(() -> showError("Erreur lors de l'échange du code: " + e.getMessage()));
        }
    }

    private void getUserInfo(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject userInfo = JsonParser.parseString(response.body()).getAsJsonObject();

        Platform.runLater(() -> {
            String email = userInfo.get("email").getAsString();
            String givenName = userInfo.has("given_name") ? userInfo.get("given_name").getAsString() : "";
            String familyName = userInfo.has("family_name") ? userInfo.get("family_name").getAsString() : "";

            txtEmail.setText(email);
            txtPrenom.setText(givenName);
            txtNom.setText(familyName);

            String randomPassword = generateRandomPassword();
            txtPassword.setText(randomPassword);
            txtConfirmPassword.setText(randomPassword);

            showAlert(Alert.AlertType.INFORMATION, "Informations Google importées avec succès !");
        });
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) linkLogin.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Erreur lors du chargement de la page de connexion");
        }
    }

    private void afficherDonnees() {
        System.out.println("====== Données saisies ======");
        System.out.println("Nom: " + txtNom.getText());
        System.out.println("Prénom: " + txtPrenom.getText());
        System.out.println("Email: " + txtEmail.getText());
        System.out.println("Mot de passe: " + txtPassword.getText());
        System.out.println("Confirmation: " + txtConfirmPassword.getText());
    }

    private void handleRegister() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "⚠️ Tous les champs sont obligatoires !");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "⚠️ L'email n'est pas valide !");
            return;
        }

        if (!isValidName(nom) || !isValidName(prenom)) {
            showAlert(Alert.AlertType.ERROR, "⚠️ Le nom et le prénom doivent contenir uniquement des lettres.");
            return;
        }

        if (userService.emailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "⚠️ Cet email est déjà utilisé !");
            return;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "⚠️ Le mot de passe doit contenir au moins 6 caractères, une lettre majuscule et un chiffre !");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "⚠️ Les mots de passe ne correspondent pas !");
            return;
        }

        String hashedPassword =password;
        User user = new User(nom, prenom, email, hashedPassword);

        userService.register(user);
        User registeredUser = userService.getUserByEmail(email);
        new HistoriqueConnexionService().enregistrerConnexion(registeredUser);

        System.out.println("✅ Inscription réussie pour : " + registeredUser.getEmail());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/front.fxml"));
            Parent frontRoot = loader.load();

            FrontClientController frontController = loader.getController();
            frontController.setSession(registeredUser);

            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(frontRoot));
            stage.setTitle("Edayetna - Bienvenue");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Erreur lors du chargement de l'accueil !");
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
        return Pattern.matches(regex, email);
    }

    private boolean isValidPassword(String password) {
        return calculatePasswordStrength(password) >= 2;
    }

    private boolean isValidName(String input) {
        return input.matches("^[\\p{L} '-]+$");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, message);
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);
        String strengthText = "";
        String advice = "";

        switch (strength) {
            case 0:
                strengthText = "Très faible";
                advice = "Le mot de passe doit contenir au moins 8 caractères";
                passwordStrengthBar.setStyle("-fx-accent: #ff0000;");
                break;
            case 1:
                strengthText = "Faible";
                advice = "Ajoutez des lettres majuscules et des chiffres";
                passwordStrengthBar.setStyle("-fx-accent: #ff6b6b;");
                break;
            case 2:
                strengthText = "Moyen";
                advice = "Ajoutez des caractères spéciaux (!@#$%^&*)";
                passwordStrengthBar.setStyle("-fx-accent: #ffd93d;");
                break;
            case 3:
                strengthText = "Fort";
                advice = "Bon mot de passe, mais vous pouvez l'améliorer";
                passwordStrengthBar.setStyle("-fx-accent: #6bff6b;");
                break;
            case 4:
                strengthText = "Très fort";
                advice = "Excellent mot de passe !";
                passwordStrengthBar.setStyle("-fx-accent: #00ff00;");
                break;
        }

        passwordStrengthBar.setProgress((double) strength / 4);
        passwordStrengthLabel.setText("Force du mot de passe: " + strengthText + "\nConseil: " + advice);
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        // Vérifier la longueur
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        // Vérifier la présence de majuscules
        if (password.matches(".*[A-Z].*")) strength++;

        // Vérifier la présence de chiffres
        if (password.matches(".*\\d.*")) strength++;

        // Vérifier la présence de caractères spéciaux
        if (password.matches(".*[!@#$%^&*].*")) strength++;

        return Math.min(strength, 4);
    }
}