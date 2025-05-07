package org.hospiconnect.controller.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Reclamation;
import org.hospiconnect.model.User;
import org.hospiconnect.service.ReclamationService;

import java.io.IOException;
import java.util.List;

public class MesReclamationsController {

    @FXML
    private VBox reclamationsContainer;

    @FXML
    private Label noReclamationsLabel;

    @FXML
    private Button homeButton;
    @FXML
    private Button btnAddReclamation;

    private User session;
    private ReclamationService reclamationService;

    @FXML
    public void initialize() {
        homeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", homeButton.getScene(), null));
    }

    public MesReclamationsController() {
        reclamationService = new ReclamationService();
    }

    public void setSession(User user) {
        this.session = user;
        loadReclamations();
    }

    private void loadReclamations() {
        reclamationsContainer.getChildren().clear();
        
        if (session == null) {
            noReclamationsLabel.setVisible(true);
            return;
        }

        List<Reclamation> reclamations = reclamationService.getReclamationsByUserId((long) session.getId());

        if (reclamations.isEmpty()) {
            noReclamationsLabel.setVisible(true);
            return;
        }

        noReclamationsLabel.setVisible(false);

        for (Reclamation reclamation : reclamations) {
            VBox card = createReclamationCard(reclamation);
            reclamationsContainer.getChildren().add(card);
        }
    }

    private VBox createReclamationCard(Reclamation reclamation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("reclamation-card");

        Label titleLabel = new Label(reclamation.getTitle());
        titleLabel.getStyleClass().add("reclamation-title");

        Label dateLabel = new Label("Date: " + reclamation.getDateReclamation().toString());
        dateLabel.getStyleClass().add("reclamation-date");

        Label statusLabel = new Label("Statut: " + reclamation.getStatus());
        statusLabel.getStyleClass().add("reclamation-status");
        statusLabel.getStyleClass().add("status-" + reclamation.getStatus().toLowerCase());

        Label descriptionLabel = new Label(reclamation.getDescription());
        descriptionLabel.getStyleClass().add("reclamation-description");
        descriptionLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, dateLabel, statusLabel, descriptionLabel);
        return card;
    }

    @FXML
    private void handleAddReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajout-reclamation.fxml"));
            Parent reclamationView = loader.load();

            AjoutReclamationController controller = loader.getController();
            if (session != null) {
                controller.setUserId(session.getId());
            }

            btnAddReclamation.getScene().setRoot(reclamationView);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page d'ajout de réclamation");
            alert.setContentText("Une erreur est survenue lors du chargement de la page.");
            alert.showAndWait();
        }
    }
} 