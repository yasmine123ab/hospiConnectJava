package org.hospiconnect.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Materiel;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.service.MaterielService1;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class afficherMateriel {
    @FXML
    private ListView<Materiel> matrielTable;

    @FXML
    private Button menuHomeButton;

    @FXML
    private TextField recherche;

    private boolean triCroissant = false;
    private final PauseTransition pauseQueue = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));

        // Configuration de la recherche avec délai
        // Remplacer cette partie dans la méthode initialize()
        recherche.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseQueue.setOnFinished(event -> filtrerMateriels(newValue)); // Ajout du paramètre 'event'
            pauseQueue.playFromStart();

        });

        // Affichage initial des matériels
        afficherMateriels(null);
    }

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
                        HBox content = new HBox(10);
                        content.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                        // Création des labels (simulant des colonnes)
                        Label nomLabel = new Label(item.getNom());
                        Label qteLabel = new Label(String.valueOf(item.getQuantite()));
                        Label catLabel = new Label(item.getCategorie());
                        Label etatLabel = new Label(item.getEtat());
                        Label empLabel = new Label(item.getEmplacement());
                        Label dateLabel = new Label(item.getDate_ajout().toString());

                        // Largeur fixe ou maxWidth avec priorités
                        for (Label label : new Label[]{nomLabel, qteLabel, catLabel, etatLabel, empLabel, dateLabel}) {
                            label.setPrefWidth(120);
                            label.setWrapText(true);
                        }

                        // Boutons
                        Button btnModifier = new Button("Modifier");
                        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnModifier.setOnAction(e -> modifierMateriel(item));

                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        btnSupprimer.setOnAction(e -> supprimerMateriel(item));

                        content.getChildren().addAll(
                                nomLabel, qteLabel, catLabel, etatLabel, empLabel, dateLabel,
                                btnModifier, btnSupprimer
                        );
                        setGraphic(content);
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert("Erreur", "Erreur lors du chargement des matériels", e.getMessage());
        }
    }

    private void filtrerMateriels(String motCle) {
        if (motCle == null || motCle.isEmpty()) {
            // Si le champ est vide, afficher tous les matériels
            afficherMateriels(null);
            return;
        }

        try {
            MaterielService1 service = new MaterielService1();
            List<Materiel> tousMateriels = service.findAll();

            // Filtrer la liste
            List<Materiel> materielsFiltres = tousMateriels.stream()
                    .filter(m ->
                            m.getNom().toLowerCase().contains(motCle.toLowerCase()) ||
                                    m.getCategorie().toLowerCase().contains(motCle.toLowerCase()) ||
                                    m.getEtat().toLowerCase().contains(motCle.toLowerCase()) ||
                                    m.getEmplacement().toLowerCase().contains(motCle.toLowerCase()) ||
                                    String.valueOf(m.getQuantite()).contains(motCle) ||
                                    m.getDate_ajout().toString().contains(motCle))
                    .collect(Collectors.toList());

            // Mettre à jour la ListView
            matrielTable.getItems().clear();
            matrielTable.getItems().addAll(materielsFiltres);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert("Erreur de recherche", "Une erreur est survenue", e.getMessage());
        }
    }

    private void modifierMateriel(Materiel m) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMatriel.fxml"));
            Parent root = loader.load();

            AddMatriel controller = loader.getController();
            controller.setMateriel(m);

            Stage stageActuel = (Stage) matrielTable.getScene().getWindow();
            stageActuel.getScene().setRoot(root);
            stageActuel.setTitle("Modifier Matériel - " + m.getNom());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur", "Le formulaire de modification n'a pas pu être chargé.");
        }
    }

    private void supprimerMateriel(Materiel m) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le matériel");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + m.getNom() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            MaterielService1 service = new MaterielService1();
            try {
                service.delete(m);
                afficherMateriels(null); // refresh
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMatriel.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.getScene().setRoot(root);
            currentStage.setTitle("Ajouter un Matériel");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page", "❌ Erreur lors du chargement de la page d'ajout.");
        }
    }

    @FXML
    void mouvement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListMouvement.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node)event.getSource()).getScene();
            currentScene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page", "❌ Une erreur est survenue lors du chargement de la page de mouvement.");
        }
    }

    @FXML
    void trier(ActionEvent event) {
        ObservableList<Materiel> items = matrielTable.getItems();

        if (triCroissant) {
            // Tri croissant (plus ancien au plus récent)
            matrielTable.setItems(items.sorted(Comparator.comparing(Materiel::getDate_ajout)));
        } else {
            // Tri décroissant (plus récent au plus ancien)
            matrielTable.setItems(items.sorted(Comparator.comparing(Materiel::getDate_ajout).reversed()));
        }

        // Inverse l'état pour le prochain clic
        triCroissant = !triCroissant;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}