package org.hospiconnect.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.hospiconnect.model.Materiel;
import org.hospiconnect.service.MaterielService1;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.List;

public class afficherMateriel {
    @FXML
    private ListView<Materiel> matrielTable;

    @FXML
    void afficherMateriels(ActionEvent event) {
        MaterielService1 service = new MaterielService1();
        try {
            List<Materiel> materiels = service.findAll();
            matrielTable.getItems().clear();
            matrielTable.getItems().addAll(materiels);

            matrielTable.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Materiel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        // Création du contenu
                        HBox content = new HBox(10);
                        content.setStyle("-fx-padding: 10; -fx-alignment: CENTER_LEFT;");

                        Label label = new Label(item.getNom() + " | " + item.getQuantite() + " | " +
                                item.getCategorie() + " | " + item.getEtat() + " | " +
                                item.getEmplacement() + " | " + item.getDate_ajout());
                        label.setMaxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);

                        // Boutons
                        Button btnModifier = new Button("Modifier");
                        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnModifier.setOnAction(e -> modifierMateriel(item));

                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        btnSupprimer.setOnAction(e -> supprimerMateriel(item));

                        content.getChildren().addAll(label, btnModifier, btnSupprimer);
                        setGraphic(content);
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Exemple de méthodes appelées par les boutons
    private void modifierMateriel(Materiel m) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMatriel.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AddMatriel controller = loader.getController();
            controller.setMateriel(m); // pré-remplir les champs

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier Matériel");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supprimerMateriel(Materiel m) {
        MaterielService1 service = new MaterielService1();
        try {
            service.delete(m);
            afficherMateriels(null); // refresh
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void ajouter(ActionEvent event) {
        try {
            // Charger le fichier FXML pour AddMatriel
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMatriel.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la racine chargée
            Stage stage = new Stage();
            stage.setTitle("afficher liste de Matériel");
            stage.setScene(new javafx.scene.Scene(root));

            // Afficher la fenêtre
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la fenêtre");
            alert.setContentText("❌ Une erreur est survenue lors de l'ouverture de la fenêtre d'ajout.");
            alert.showAndWait();
        }
    }
    @FXML
    void mouvement(ActionEvent event) { try {
        // Charger le fichier FXML pour AddMatriel
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListMouvement.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la racine chargée
        Stage stage = new Stage();
        stage.setTitle("list de mouvement");
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
    @FXML
    void trier(ActionEvent event) {

    }

}

