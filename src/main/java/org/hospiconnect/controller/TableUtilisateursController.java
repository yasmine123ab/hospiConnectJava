package org.hospiconnect.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hospiconnect.model.User;
import org.hospiconnect.model.UserService;

import java.io.IOException;
import java.util.List;

public class TableUtilisateursController {

    @FXML private TextField searchField;
    @FXML private VBox userListContainer;
    @FXML private HBox paginationContainer;
    @FXML private HBox searchBox;

    private final UserService userService = new UserService();
    private int currentPage = 1;
    private final int rowsPerPage = 5;

    @FXML
    public void initialize() {
        loadUsersPage(currentPage);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadUsersPage(currentPage);
        });

        animateSearchBar();
    }

    private void loadUsersPage(int page) {
        String keyword = searchField.getText();
        List<User> users = userService.searchUsersPageSQL(keyword, page, rowsPerPage);
        userListContainer.getChildren().clear();

        for (User user : users) {
            HBox row = new HBox(5);
            row.getStyleClass().add("table-row");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinWidth(1200);

            // ✅ Photo
            ImageView imageView = new ImageView();
            String imagePath = "/assets/users/" + user.getPhoto();
            try {
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                if (image.isError()) throw new Exception();
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(new Image(getClass().getResourceAsStream("/assets/userf.png")));
            }
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            Circle clip = new Circle(20, 20, 20);
            imageView.setClip(clip);
            imageView.setSmooth(true);
            imageView.setCache(true);
            Label photoLabel = new Label();
            photoLabel.setGraphic(imageView);
            photoLabel.setPrefWidth(50);

            // ✅ Nom & Prénom
            Label nameLabel = new Label(user.getNom() + " " + user.getPrenom());
            nameLabel.setPrefWidth(250);
            nameLabel.getStyleClass().add("cell-text");

            // ✅ Email
            Label emailLabel = new Label(user.getEmail());
            emailLabel.setPrefWidth(250);
            emailLabel.getStyleClass().add("cell-text");

            // ✅ Groupe Sanguin
            Label gsLabel = new Label(user.getGroupe_Sanguin());
            gsLabel.setPrefWidth(250);
            gsLabel.getStyleClass().add("cell-text");

            // ✅ Téléphone
            Label telLabel = new Label(user.getTel());
            telLabel.setPrefWidth(250);
            telLabel.getStyleClass().add("cell-text");

            // ✅ Sexe
            Label SexeLabel = new Label(user.getSexe());
            SexeLabel.setPrefWidth(250);
            SexeLabel.getStyleClass().add("cell-text");

            // ✅ Statut
            Label statutLabel = new Label(user.getStatut());
            statutLabel.setPrefWidth(200);
            statutLabel.setAlignment(Pos.CENTER);
            statutLabel.setStyle("-fx-background-radius: 20;");
            if ("active".equalsIgnoreCase(user.getStatut())) {
                statutLabel.getStyleClass().add("statut-actif");
            } else {
                statutLabel.getStyleClass().add("statut-bloque");
            }

            // ✅ Actions
            HBox actions = new HBox(5);
            actions.setPrefWidth(200);
            actions.setAlignment(Pos.CENTER);

            Button btnInfo = createIconButton("/assets/icons/infos.png", () -> handleInfo(user));
            Button btnEdit = createIconButton("/assets/icons/modifier.png", () -> handleEdit(user));
            Button btnDelete = createIconButton("/assets/icons/poubelle.png", () -> handleDelete(user));
            Button btnHistory = createIconButton("/assets/icons/historique.png", () -> handleHistory(user));

            actions.getChildren().addAll(btnInfo, btnEdit, btnDelete, btnHistory);

            // Ajout à la ligne
            row.getChildren().addAll(photoLabel, nameLabel, emailLabel, SexeLabel, gsLabel, telLabel, statutLabel, actions);
            VBox.setMargin(row, new Insets(5, 0, 5, 0));
            userListContainer.getChildren().add(row);
        }

        generatePagination();
    }



    private Button createIconButton(String iconPath, Runnable action) {
        ImageView icon = new ImageView(getClass().getResource(iconPath).toExternalForm());
        icon.setFitHeight(20);
        icon.setFitWidth(20);
        Button btn = new Button("", icon);
        btn.getStyleClass().add("action-icon-button");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void generatePagination() {
        paginationContainer.getChildren().clear();
        int total = userService.countUsersWithKeyword(searchField.getText());
        int totalPages = (int) Math.ceil((double) total / rowsPerPage);

        for (int i = 1; i <= totalPages; i++) {
            int pageIndex = i;

            Button pageBtn = new Button(String.valueOf(pageIndex));
            pageBtn.setOnAction(e -> {
                currentPage = pageIndex;
                loadUsersPage(pageIndex);
            });

            pageBtn.getStyleClass().add("pagination-button");

            if (pageIndex == currentPage) {
                pageBtn.setStyle("-fx-background-color: #3a4c68; -fx-text-fill: white;");
            }

            paginationContainer.getChildren().add(pageBtn);
        }
    }

    private void handleInfo(User user) {
        String info = String.format(
                "Nom & Prénom    : %s %s\n" +
                        "Email           : %s\n" +
                        "Statut          : %s\n" +
                        "Rôle            : %s\n" +
                        "Téléphone       : %s\n" +
                        "Sexe            : %s\n" +
                        "Groupe Sanguin  : %s\n" +
                        "Code Postal     : %s\n" +
                        "Adresse         : %s\n" +
                        "Gouvernorat     : %s\n" +
                        "Poids           : %.2f kg\n" +
                        "Taille          : %.2f cm\n" +
                        "IMC             : %.2f\n" +
                        "Photo           : %s",
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getStatut(),
                user.getRoles(),
                user.getTel(),
                user.getSexe(),
                user.getGroupe_Sanguin(),
                user.getZipCode(),
                user.getAdresse(),
                user.getGouvernorat(),
                user.getPoids(),
                user.getTaille(),
                user.getImc(),
                user.getPhoto()
        );

        showAlert("🧾 Informations Utilisateur", info);
    }


    private void handleEdit(User user) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditUser.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du fichier FXML et lui transmettre l'utilisateur à éditer
            EditUserController controller = loader.getController();
            controller.setUser(user); // Passer l'utilisateur à éditer au contrôleur

            // Créer une nouvelle fenêtre pour l'édition
            Stage stage = new Stage();
            stage.setTitle("Modifier Utilisateur");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Afficher la fenêtre et attendre sa fermeture

            // Après la fermeture de la fenêtre, rafraîchir la liste des utilisateurs
            loadUsersPage(currentPage); // Méthode pour recharger la liste des utilisateurs
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleDelete(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmAlert.setContentText("Utilisateur : " + user.getNom() + " " + user.getPrenom());

        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        confirmAlert.getButtonTypes().setAll(btnOui, btnNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == btnOui) {
                userService.deleteUserById(user.getId());
                showAlert("✅ Supprimé", "L'utilisateur a été supprimé avec succès.");
                loadUsersPage(currentPage); // Recharge la liste
            }
        });
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
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void animateSearchBar() {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), searchBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ajoutAdmin.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Un Administrateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
