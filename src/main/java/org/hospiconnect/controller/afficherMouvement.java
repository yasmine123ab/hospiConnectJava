package org.hospiconnect.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hospiconnect.model.MouvementMaterielJoint;
import org.hospiconnect.model.mouvement_stock;
import org.hospiconnect.service.mouvementService;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

public class afficherMouvement {

    @FXML
    private ListView<MouvementMaterielJoint> mouvementTable;
    @FXML
    private TextField rechercheMouvement;

    private final mouvementService service = new mouvementService();
    private final PauseTransition pauseQueue = new PauseTransition(Duration.millis(300));
    private ObservableList<MouvementMaterielJoint> mouvementsList;

    @FXML
    public void initialize() {
        // Configuration de la recherche avec délai
        rechercheMouvement.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseQueue.setOnFinished(event -> filtrerMouvements(newValue));
            pauseQueue.playFromStart();
        });

        // Affichage initial des mouvements
        affichem(null);
    }

    @FXML
    void affichem(ActionEvent event) {
        try {
            List<MouvementMaterielJoint> mouvements = service.getMouvementsAvecNomMateriel();
            mouvementsList = FXCollections.observableArrayList(mouvements);
            mouvementTable.setItems(mouvementsList);

            mouvementTable.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(MouvementMaterielJoint item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        HBox content = new HBox(10);
                        content.setStyle("-fx-padding: 10; -fx-alignment: CENTER_LEFT;");

                        // Création des labels (simulant des colonnes)
                        Label nomLabel = new Label(item.getNomMateriel());

                        Label qteLabel = new Label(String.valueOf(item.getQuantite()));
                        Label etatLabel = new Label(item.getMotif());
                        Label empLabel = new Label(item.getTypeMouvement());
                        Label dateLabel = new Label(item.getDateMouvement().toString());




                        // Largeur fixe ou maxWidth avec priorités
                        for (Label label : new Label[]{nomLabel, qteLabel, etatLabel, empLabel, dateLabel}) {
                            label.setPrefWidth(120);
                            label.setWrapText(true);
                        }

                        Button btnModifier = new Button("Modifier");
                        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnModifier.setOnAction(e -> modifierMouvement(item));

                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        btnSupprimer.setOnAction(e -> supprimerMouvement(item));


                        content.getChildren().addAll(
                                nomLabel, qteLabel, etatLabel, empLabel, dateLabel,
                                btnModifier, btnSupprimer
                        );
                        setGraphic(content);
                    }
                }
            });

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des mouvements", e.getMessage());
        }
    }

    private void filtrerMouvements(String motCle) {
        if (motCle == null || motCle.isEmpty()) {
            mouvementTable.setItems(mouvementsList);
            return;
        }

        FilteredList<MouvementMaterielJoint> filteredData = new FilteredList<>(mouvementsList);
        filteredData.setPredicate(createPredicate(motCle));
        mouvementTable.setItems(filteredData);
    }

    private Predicate<MouvementMaterielJoint> createPredicate(String motCle) {
        return mouvement -> {
            String lowerCaseFilter = motCle.toLowerCase();

            if (mouvement.getNomMateriel().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            if (mouvement.getTypeMouvement().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            if (String.valueOf(mouvement.getQuantite()).contains(motCle)) {
                return true;
            }
            if (mouvement.getDateMouvement().toString().contains(motCle)) {
                return true;
            }
            return false;
        };
    }

    private void modifierMouvement(MouvementMaterielJoint m) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMouvement.fxml"));
            Parent root = loader.load();

            AddMouvement controller = loader.getController();
            controller.setMouvementExistant(m);

            Stage stageActuel = (Stage) mouvementTable.getScene().getWindow();
            stageActuel.getScene().setRoot(root);
            stageActuel.setTitle("Modifier Mouvement");

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur", e.getMessage());
        }
    }

    private void supprimerMouvement(MouvementMaterielJoint m) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le mouvement");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce mouvement?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                mouvement_stock mouvement = new mouvement_stock();
                mouvement.setId(m.getId());
                service.delete(mouvement);
                affichem(null);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    @FXML
    void ajouterM(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMouvement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter mouvement");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre", "❌ Une erreur est survenue lors de l'ouverture de la fenêtre de mouvement.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}