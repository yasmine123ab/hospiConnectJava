package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.hospiconnect.model.MouvementMaterielJoint;
import org.hospiconnect.model.mouvement_stock;
import org.hospiconnect.service.mouvementService;

import java.sql.SQLException;
import java.util.List;

public class afficherMouvement {

    @FXML
    private ListView<MouvementMaterielJoint> mouvementTable;

    private final mouvementService service = new mouvementService();

    @FXML
    void affichem(ActionEvent event) {
        try {
            List<MouvementMaterielJoint> mouvements = service.getMouvementsAvecNomMateriel();
            ObservableList<MouvementMaterielJoint> observableList = FXCollections.observableArrayList(mouvements);
            mouvementTable.setItems(observableList);

            mouvementTable.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(MouvementMaterielJoint item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        HBox content = new HBox(10);
                        content.setStyle("-fx-padding: 10; -fx-alignment: CENTER_LEFT;");

                        Label label = new Label(
                                item.getNomMateriel() + " | " +
                                        item.getTypeMouvement() + " | " +
                                        item.getQuantite() + " | " +
                                        item.getDateMouvement()
                        );
                        label.setMaxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(label, Priority.ALWAYS);

                        Button btnModifier = new Button("Modifier");
                        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnModifier.setOnAction(e -> modifierMouvement(item));

                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        btnSupprimer.setOnAction(e -> supprimerMouvement(item));

                        content.getChildren().addAll(label, btnModifier, btnSupprimer);
                        setGraphic(content);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifierMouvement(MouvementMaterielJoint m) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMouvement.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AddMouvement controller = loader.getController();
            controller.setMouvementExistant(m); // pré-remplir les champs

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier Matériel");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supprimerMouvement(MouvementMaterielJoint m) {
        try {
            mouvement_stock mouvement = new mouvement_stock();
            mouvement.setId(m.getId()); // on a besoin que de l'ID
            service.delete(mouvement);
            affichem(null); // refresh
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void ajouterM(ActionEvent event) {
        try {
            // Charger le fichier FXML pour AddMatriel
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMouvement.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la racine chargée
            Stage stage = new Stage();
            stage.setTitle("ajouter mouvement");
            stage.setScene(new javafx.scene.Scene(root));

            // Afficher la fenêtre
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la fenêtre");
            alert.setContentText("❌ Une erreur est survenue lors de l'ouverture de la fenêtre de mouvement.");
            alert.showAndWait();
        }
    }

    }

