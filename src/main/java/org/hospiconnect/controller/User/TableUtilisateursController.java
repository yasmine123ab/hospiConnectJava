package org.hospiconnect.controller.User;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.hospiconnect.service.UserService;

import java.io.IOException;
import java.util.List;

public class TableUtilisateursController {

    @FXML private TextField searchField;
    @FXML private VBox userListContainer;
    @FXML private HBox paginationContainer;
    @FXML private HBox searchBox;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> bloodGroupComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button btnShowAll;

    private final UserService userService = new UserService();
    private int currentPage = 1;
    private final int rowsPerPage = 5;

    @FXML
    public void initialize() {
        // Initialisation des items des ComboBox
        ObservableList<String> roleItems = FXCollections.observableArrayList(
            "SUPER_ADMIN", "ADMIN", "PERSONNEL", "MEDECIN", "PATIENT", "ROLE_CLIENT"
        );
        roleComboBox.setItems(roleItems);

        ObservableList<String> bloodGroupItems = FXCollections.observableArrayList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        );
        bloodGroupComboBox.setItems(bloodGroupItems);

        ObservableList<String> statusItems = FXCollections.observableArrayList(
            "ACTIVE", "BLOQUE"
        );
        statusComboBox.setItems(statusItems);

        // Initialiser le bouton "Tous les utilisateurs"
        btnShowAll = new Button("Tous les utilisateurs");
        btnShowAll.getStyleClass().add("show-all-button");
        btnShowAll.setOnAction(e -> resetFilters());
        searchBox.getChildren().add(btnShowAll);

        loadUsersPage(currentPage);

        // Écouteurs pour la recherche et les filtres
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadUsersPage(currentPage);
        });

        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadUsersPage(currentPage);
        });

        bloodGroupComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadUsersPage(currentPage);
        });

        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadUsersPage(currentPage);
        });

        animateSearchBar();
    }

    private void loadUsersPage(int page) {
        String keyword = searchField.getText();
        String roleFilter = roleComboBox.getValue();
        String bloodGroupFilter = bloodGroupComboBox.getValue();
        String statusFilter = statusComboBox.getValue();
        
        List<User> users = userService.searchUsersPageSQL(keyword, page, rowsPerPage, roleFilter, bloodGroupFilter, statusFilter);
        userListContainer.getChildren().clear();

        // Ajout de l'en-tête
        HBox header = new HBox(5);
        header.getStyleClass().add("table-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinWidth(1200);

        // En-têtes des colonnes
        Label photoHeader = createHeaderLabel("Photo", 50);
        Label nameHeader = createHeaderLabel("Nom & Prénom", 250);
        Label emailHeader = createHeaderLabel("Email", 250);
        Label roleHeader = createHeaderLabel("Rôle", 150);
        Label sexeHeader = createHeaderLabel("Sexe", 100);
        Label gsHeader = createHeaderLabel("Groupe Sanguin", 100);
        Label telHeader = createHeaderLabel("Téléphone", 150);
        Label statutHeader = createHeaderLabel("Statut", 100);
        Label actionsHeader = createHeaderLabel("Actions", 200);

        header.getChildren().addAll(
                photoHeader,
                nameHeader,
                emailHeader,
                roleHeader,
                sexeHeader,
                gsHeader,
                telHeader,
                statutHeader,
                actionsHeader
        );

        userListContainer.getChildren().add(header);

        for (User user : users) {
            HBox row = new HBox(5);
            row.getStyleClass().add("table-row");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinWidth(1200);

            // Photo
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
            imageView.setClip(new Circle(20, 20, 20));
            imageView.setSmooth(true);
            imageView.setCache(true);
            Label photoLabel = new Label();
            photoLabel.setGraphic(imageView);
            photoLabel.setPrefWidth(50);

            // Nom & Prénom
            Label nameLabel = createDataLabel(user.getNom() + " " + user.getPrenom(), 250);

            // Email
            Label emailLabel = createDataLabel(user.getEmail(), 250);

            // Rôle
            Label roleLabel = createDataLabel(user.getRoles(), 150);

            // Sexe
            Label sexeLabel = createDataLabel(user.getSexe(), 100);

            // Groupe Sanguin
            Label gsLabel = createDataLabel(user.getGroupe_Sanguin(), 100);

            // Téléphone
            Label telLabel = createDataLabel(user.getTel(), 150);

            // Statut
            Label statutLabel = new Label(user.getStatut());
            statutLabel.setPrefWidth(100);
            statutLabel.setAlignment(Pos.CENTER);
            statutLabel.setStyle("-fx-background-radius: 20;");
            if ("active".equalsIgnoreCase(user.getStatut())) {
                statutLabel.getStyleClass().add("statut-actif");
            } else {
                statutLabel.getStyleClass().add("statut-bloque");
            }

            // Actions
            HBox actions = new HBox(5);
            actions.setPrefWidth(200);
            actions.setAlignment(Pos.CENTER);

            Button btnInfo = createIconButton("/assets/icons/infos.png", () -> handleInfo(user));
            Button btnEdit = createIconButton("/assets/icons/modifier.png", () -> handleEdit(user));
            Button btnDelete = createIconButton("/assets/icons/poubelle.png", () -> handleDelete(user));
            Button btnHistory = createIconButton("/assets/icons/historique.png", () -> handleHistory(user));
            Button btnBlockToggle;

            if ("BLOQUER".equalsIgnoreCase(user.getStatut())) {
                btnBlockToggle = createIconButton("/UNBLOCK.png", () -> handleUnblock(user));
            } else {
                btnBlockToggle = createIconButton("/BLOCK.png", () -> handleBlock(user));
            }

            actions.getChildren().addAll(btnInfo, btnEdit, btnDelete, btnHistory, btnBlockToggle);

            // Ajout à la ligne
            row.getChildren().addAll(
                    photoLabel,
                    nameLabel,
                    emailLabel,
                    roleLabel,
                    sexeLabel,
                    gsLabel,
                    telLabel,
                    statutLabel,
                    actions
            );

            // Ajout du tooltip avec plus d'informations
            Tooltip tooltip = new Tooltip(
                "Nom: " + user.getNom() + " " + user.getPrenom() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Rôle: " + user.getRoles() + "\n" +
                "Statut: " + user.getStatut()
            );
            
            for (javafx.scene.Node node : row.getChildren()) {
                Tooltip.install(node, tooltip);
            }

            VBox.setMargin(row, new Insets(5, 0, 5, 0));
            userListContainer.getChildren().add(row);
        }

        generatePagination();
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("header-label");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        return label;
    }

    private Label createDataLabel(String text, double width) {
        Label label = new Label(text != null ? text : "-");
        label.setPrefWidth(width);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("cell-text");
        label.setWrapText(true);
        return label;
    }

    private void handleUnblock(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de Déblocage de de  compte ");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir Débloquer cet utilisateur ?");
        confirmAlert.setContentText("Utilisateur : " + user.getNom() + " " + user.getPrenom());

        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        confirmAlert.getButtonTypes().setAll(btnOui, btnNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if ((response == btnOui ) ) {
                userService.ActiveUserById(user.getId());
                showAlert(Alert.AlertType.ERROR, "✅ BLOQUE", "L'utilisateur a été Déloqué avec succès.");
                loadUsersPage(currentPage); // Recharge la liste
            }
        });
    }


    private void handleBlock(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de Blocage de ce  compte ");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir bloquer cet utilisateur ?");
        confirmAlert.setContentText("Utilisateur : " + user.getNom() + " " + user.getPrenom());

        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        confirmAlert.getButtonTypes().setAll(btnOui, btnNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if ((response == btnOui ) ) {
                userService.blockUserById(user.getId());
                showAlert(Alert.AlertType.ERROR, "✅ BLOQUE", "L'utilisateur a été Bloqué avec succès.");
                loadUsersPage(currentPage); // Recharge la liste
            }
        });
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
        Stage infoStage = new Stage();
        infoStage.setTitle("🧾 Détails Utilisateur");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("info-box");

        // Exemple d'informations
        container.getChildren().addAll(
                createInfoLabel("👤 Nom & Prénom", user.getNom() + " " + user.getPrenom()),
                createInfoLabel("📧 Email", user.getEmail()),
                createInfoLabel("📌 Statut", user.getStatut()),
                createInfoLabel("🛡️ Rôle", user.getRoles()),
                createInfoLabel("📞 Téléphone", user.getTel()),
                createInfoLabel("🚻 Sexe", user.getSexe()),
                createInfoLabel("🩸 Groupe Sanguin", user.getGroupe_Sanguin()),
                createInfoLabel("🏷️ Code Postal", user.getZipCode()),
                createInfoLabel("🏠 Adresse", user.getAdresse()),
                createInfoLabel("📍 Gouvernorat", user.getGouvernorat()),
                createInfoLabel("⚖️ Poids", user.getPoids() + " kg"),
                createInfoLabel("📏 Taille", user.getTaille() + " cm"),
                createInfoLabel("📊 IMC", String.format("%.2f", user.getImc())),
                createInfoLabel("🖼️ Photo", user.getPhoto())
        );

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 450, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/user-info.css").toExternalForm());

        infoStage.setScene(scene);
        infoStage.show();
    }

    private HBox createInfoLabel(String label, String value) {
        Label titleLabel = new Label(label + " : ");
        titleLabel.getStyleClass().add("label-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("label-value");

        HBox row = new HBox(10, titleLabel, valueLabel);
        row.setAlignment(Pos.BASELINE_LEFT);
        return row;
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
        // Vérifier si l'utilisateur a le rôle SUPER_ADMIN
        if ("SUPER_ADMIN".equals(user.getRoles())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de suppression",
                    "Un utilisateur avec le rôle 'SUPER_ADMIN' ne peut pas être supprimé.");
            return; // Empêche la suppression
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmAlert.setContentText("Utilisateur : " + user.getNom() + " " + user.getPrenom());

        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        confirmAlert.getButtonTypes().setAll(btnOui, btnNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == btnOui) {
                try {
                    userService.deleteUserById(user.getId()); // Suppression de l'utilisateur
                    showAlert(Alert.AlertType.INFORMATION, "✅ Supprimé", "L'utilisateur a été supprimé avec succès.");
                    loadUsersPage(currentPage); // Recharge la liste des utilisateurs
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Un problème est survenu lors de la suppression de l'utilisateur.");
                }
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

    private void showAlert(Alert.AlertType error, String title, String content) {
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

            //TableReclamationController controller = loader.getController();
            //controller.setUser(connectedUser);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Un Administrateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetFilters() {
        searchField.clear();
        roleComboBox.setValue(null);
        bloodGroupComboBox.setValue(null);
        statusComboBox.setValue(null);
        currentPage = 1;
        loadUsersPage(currentPage);
    }

}
