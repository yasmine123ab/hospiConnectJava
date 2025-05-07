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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hospiconnect.model.Reclamation;
import org.hospiconnect.model.User;
import org.hospiconnect.service.ReclamationService;

import java.io.IOException;
import java.util.List;

public class TableReclamationController {

    @FXML private TextField searchField;
    @FXML private VBox reclamationListContainer;
    @FXML private HBox paginationContainer;
    @FXML private HBox searchBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button btnShowAll;
    @FXML private TableColumn<Reclamation, String> colStatut;
    @FXML private TableColumn<Reclamation, String> colActions;
    private User session;

    private final ReclamationService reclamationService = new ReclamationService();
    private int currentPage = 1;
    private final int rowsPerPage = 5;

    @FXML
    public void initialize() {
        // Initialisation des items des ComboBox
        ObservableList<String> priorityItems = FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW");
        priorityComboBox.setItems(priorityItems);
        
        ObservableList<String> statusItems = FXCollections.observableArrayList("EN COURS", "TRAITÉ");
        statusComboBox.setItems(statusItems);

        // Définir le tri par priorité par défaut
        priorityComboBox.setValue("HIGH");

        // Initialiser le bouton "Toutes les réclamations"
        btnShowAll = new Button("Toutes les réclamations");
        btnShowAll.getStyleClass().add("show-all-button");
        btnShowAll.setOnAction(e -> resetFilters());
        searchBox.getChildren().add(btnShowAll);

        loadReclamationsPage(currentPage);

        // Écouteurs pour la recherche
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadReclamationsPage(currentPage);
        });

        // Écouteur pour le tri par priorité
        priorityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadReclamationsPage(currentPage);
        });

        // Écouteur pour le filtrage par statut
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            loadReclamationsPage(currentPage);
        });

        animateSearchBar();
    }

    public void setUser(User user) {
        this.session = user;
    }

    private void loadReclamationsPage(int page) {
        String keyword = searchField.getText();
        String priorityFilter = priorityComboBox.getValue();
        String statusFilter = statusComboBox.getValue();
        
        List<Reclamation> reclamations = reclamationService.searchReclamationsPageSQL(keyword, page, rowsPerPage, priorityFilter, statusFilter);
        reclamationListContainer.getChildren().clear();

        // Ajout de l'en-tête
        HBox header = new HBox(5);
        header.getStyleClass().add("table-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinWidth(1200);

        // En-têtes des colonnes
        Label titleHeader = createHeaderLabel("Titre", 150);
        Label categoryHeader = createHeaderLabel("Catégorie", 120);
        Label priorityHeader = createHeaderLabel("Priorité", 100);
        Label dateHeader = createHeaderLabel("Date", 150);
        Label statusHeader = createHeaderLabel("Statut", 100);
        Label resolvedByHeader = createHeaderLabel("Résolu par", 150);
        Label descriptionHeader = createHeaderLabel("Description", 280);
        Label actionsHeader = createHeaderLabel("Actions", 150);

        header.getChildren().addAll(
                titleHeader,
                categoryHeader,
                priorityHeader,
                dateHeader,
                statusHeader,
                resolvedByHeader,
                descriptionHeader,
                actionsHeader
        );

        reclamationListContainer.getChildren().add(header);

        for (Reclamation reclamation : reclamations) {
            HBox row = new HBox(5);
            row.getStyleClass().add("table-row");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinWidth(1200);

            // Titre
            Label titleLabel = createDataLabel(reclamation.getTitle(), 150);

            // Catégorie
            Label categoryLabel = createDataLabel(reclamation.getCategory(), 120);

            // Priorité
            Label priorityLabel = createDataLabel(reclamation.getPriority(), 100);

            // Date
            Label dateLabel = createDataLabel(reclamation.getDateReclamation().toString(), 150);

            // Statut
            Label statutLabel = new Label(reclamation.getStatus());
            statutLabel.setPrefWidth(100);
            statutLabel.setPrefHeight(30);
            statutLabel.setAlignment(Pos.CENTER);
            statutLabel.setStyle("-fx-background-radius: 20; -fx-alignment: center;");
            if ("TRAITÉ".equalsIgnoreCase(reclamation.getStatus())) {
                statutLabel.getStyleClass().add("statut-resolu");
            } else {
                statutLabel.getStyleClass().add("statut-en-attente");
            }

            // Résolu par
            Label resolvedByLabel = createDataLabel(
                reclamation.getResolvedByUsername() != null ? reclamation.getResolvedByUsername() : "-",
                150
            );

            // Description
            Label descriptionLabel = createDataLabel(reclamation.getDescription(), 280);

            // Actions
            HBox actions = new HBox(5);
            actions.setPrefWidth(150);
            actions.setAlignment(Pos.CENTER);

            Button btnInfo = createIconButton("/assets/icons/infos.png", () -> handleInfo(reclamation));
            Button btnEdit = createIconButton("/RISPONDERE.png", () -> handleRispond(reclamation));
            Button btnDelete = createIconButton("/assets/icons/poubelle.png", () -> handleDelete(reclamation));

            actions.getChildren().addAll(btnInfo, btnEdit, btnDelete);

            // Ajout des éléments à la ligne
            row.getChildren().addAll(
                    titleLabel,
                    categoryLabel,
                    priorityLabel,
                    dateLabel,
                    statutLabel,
                    resolvedByLabel,
                    descriptionLabel,
                    actions
            );

            // Ajout du tooltip sur toute la ligne avec plus d'informations
            Tooltip rowTooltip = new Tooltip(
                "Titre: " + reclamation.getTitle() + "\n" +
                "Description: " + reclamation.getDescription() + "\n" +
                "Statut: " + reclamation.getStatus() + "\n" +
                (reclamation.getResolvedByUsername() != null ? "Résolu par: " + reclamation.getResolvedByUsername() : "Non résolu")
            );
            
            for (javafx.scene.Node node : row.getChildren()) {
                Tooltip.install(node, rowTooltip);
            }

            VBox.setMargin(row, new Insets(5, 0, 5, 0));
            reclamationListContainer.getChildren().add(row);
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
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("cell-text");
        label.setWrapText(true);
        return label;
    }

    private void handleRispond(Reclamation reclamation) {
        Stage respondStage = new Stage();
        respondStage.setTitle("✉️ Répondre à la Réclamation");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("respond-box");

        // ➔ Affichage du titre de la réclamation
        Label titleLabel = new Label("📝 Titre : " + reclamation.getTitle());
        titleLabel.getStyleClass().add("title-label");

        // ➔ Zone pour écrire la réponse
        TextArea responseArea = new TextArea();
        responseArea.setPromptText("Écrire votre réponse ici...");
        responseArea.setPrefHeight(200);

        // ➔ Bouton pour envoyer la réponse
        Button sendButton = new Button("Envoyer la Réponse");
        sendButton.setOnAction(event -> {
            String response = responseArea.getText();
            if (response != null && !response.trim().isEmpty()) {
                // Mise à jour de la réclamation avec la réponse
                reclamation.setResponse(response);

                // Mise à jour de la réclamation dans la base de données
                boolean updated = ReclamationService.updateReclamationResponse(reclamation.getTitle(), response, session.getId());
                if (updated) {
                    respondStage.close(); // Fermer la fenêtre après avoir répondu
                } else {
                    // Afficher une alerte en cas d'échec de mise à jour
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Une erreur est survenue lors de la mise à jour de la réclamation.");
                    alert.showAndWait();
                }
            } else {
                // Alerte si la réponse est vide
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez saisir une réponse avant d'envoyer.");
                alert.showAndWait();
            }
        });

        container.getChildren().addAll(titleLabel, responseArea, sendButton);

        Scene scene = new Scene(container, 500, 400);
        // Tu peux aussi ajouter un CSS pour plus de style :
        // scene.getStylesheets().add(getClass().getResource("/styles/repondre-style.css").toExternalForm());

        respondStage.setScene(scene);
        respondStage.show();
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
        int total = reclamationService.countReclamations(searchField.getText());
        int totalPages = (int) Math.ceil((double) total / rowsPerPage);

        for (int i = 1; i <= totalPages; i++) {
            int pageIndex = i;

            Button pageBtn = new Button(String.valueOf(pageIndex));
            pageBtn.setOnAction(e -> {
                currentPage = pageIndex;
                loadReclamationsPage(pageIndex);
            });

            pageBtn.getStyleClass().add("pagination-button");

            if (pageIndex == currentPage) {
                pageBtn.setStyle("-fx-background-color: #3a4c68; -fx-text-fill: white;");
            }

            paginationContainer.getChildren().add(pageBtn);
        }
    }

    private void handleInfo(Reclamation reclamation) {
        Stage infoStage = new Stage();
        infoStage.setTitle("🧾 Détails Réclamation");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("info-box");

        // Ajouter toutes les informations de la réclamation
        container.getChildren().addAll(
                createInfoLabel("📝 Titre", reclamation.getTitle()),
                createInfoLabel("👤 Créé par", reclamation.getUserName() != null ? reclamation.getUserName() : "Anonyme"),
                createInfoLabel("📜 Description", reclamation.getDescription()),
                createInfoLabel("📅 Date Réclamation", reclamation.getDateReclamation() != null ? reclamation.getDateReclamation().toString() : "N/D"),
                createInfoLabel("📌 Statut", reclamation.getStatus()),
                createInfoLabel("📂 Catégorie", reclamation.getCategory()),
                createInfoLabel("⚠️ Priorité", reclamation.getPriority()),
                createInfoLabel("📆 Date de Résolution", reclamation.getResolutionDate() != null ? reclamation.getResolutionDate().toString() : "Non résolue"),
                createInfoLabel("💬 Réponse", reclamation.getResponse() != null ? reclamation.getResponse() : "Pas encore de réponse"),
                createInfoLabel("👨‍💼 Résolu par", reclamation.getResolvedByUsername() != null ? reclamation.getResolvedByUsername() : "Non résolu"),
                createInfoLabel("🕵️ Anonyme", reclamation.getIsAnonymous() != null && reclamation.getIsAnonymous() ? "Oui" : "Non"),
                createInfoLabel("📎 Pièce Jointe", reclamation.getAttachment() != null && !reclamation.getAttachment().isEmpty() ? reclamation.getAttachment() : "Aucune pièce jointe")
        );

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 500);
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

    private void handleEdit(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditReclamation.fxml"));
            Parent root = loader.load();

            //EditReclamationController controller = loader.getController();
            //controller.setReclamation(reclamation);

            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadReclamationsPage(currentPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Reclamation reclamation) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");
        //confirmAlert.setContentText("Réclamation : " + reclamation.getTitle());

        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        confirmAlert.getButtonTypes().setAll(btnOui, btnNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == btnOui) {
                reclamationService.deleteReclamationById(reclamation.getId());
                showAlert("✅ Supprimé", "La réclamation a été supprimée avec succès.");
                loadReclamationsPage(currentPage);
            }
        });
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajout-reclamation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AjoutReclamationController controller = loader.getController();
            controller.setUserId(session.getId()); // Passer l'ID de l'utilisateur connecté

            Stage stage = new Stage();
            stage.setTitle("Ajouter Une Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Nouvelle méthode pour réinitialiser les filtres
    private void resetFilters() {
        searchField.clear();
        priorityComboBox.setValue(null);
        statusComboBox.setValue(null);
        currentPage = 1;
        loadReclamationsPage(currentPage);
    }
}
