package org.hospiconnect.controller.laboratoire;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.*;

import java.util.Comparator;

public class ListTypeAnalyseController {

    private boolean trierTypeNomCroissant = true;

    private final TypeAnalyseCrudService typeAnalyseCrudService = TypeAnalyseCrudService.getInstance();

    @FXML
    private ComboBox<String> typeAnalyseTrierComboBox;
    @FXML
    private ComboBox<String> typeAnalyseRechercherComboBox;

    @FXML
    private ListView<TypeAnalyse> typeAnalyseListView;

    @FXML
    private Button typeAnalyseTrierButton;
    @FXML
    private TextField typeAnalyseRechercherTextField;

    @FXML
    private Button typeAnalyseAjouterButton;
    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

    @FXML
    private Button menuAnalyseButton;
    @FXML
    private Button menuTypeAnalyseButton;
    @FXML
    private Button menuDispoAnalyseButton;
    @FXML
    private Button menuDashboardButton;
    @FXML
    private Button menuHospiChatButton;
    @FXML
    private Button menuHomeButton;

    @FXML
    public void initialize() {
        resetListItems();

        // Remplir les choix de tri
        typeAnalyseTrierComboBox.setItems(FXCollections.observableArrayList(
                "Nom", "Libelle", "Prix"
        ));
        typeAnalyseTrierComboBox.getSelectionModel().selectFirst(); // sélection par défaut

// Remplir les choix de recherche
        typeAnalyseRechercherComboBox.setItems(FXCollections.observableArrayList(
                "Nom", "Libelle"
        ));
        typeAnalyseRechercherComboBox.getSelectionModel().selectFirst(); // sélection par défaut

        typeAnalyseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TypeAnalyse typeAnalyse, boolean empty) {
                super.updateItem(typeAnalyse, empty);
                if (empty || typeAnalyse == null) {
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label libelle = new Label(typeAnalyse.getLibelle());
                    Label nom = new Label(typeAnalyse.getNom());
                    Label prix = new Label(typeAnalyse.getPrix().toString());

                    EditRemoveButtonsBox<TypeAnalyse> actionsFactory = new EditRemoveButtonsBox<>();
                    HBox actionBox = actionsFactory.create(typeAnalyse,
                            a -> SceneUtils.openNewScene(
                                    "/laboratoireBack/typeAnalyse/formTypeAnalyse.fxml",
                                    typeAnalyseAjouterButton.getScene(),
                                    a
                            ),
                            a -> {
                                typeAnalyseCrudService.deleteById(a.getId());
                                resetListItems();
                            }
                    );

                    // Set width for consistent alignment
                    libelle.setPrefWidth(140);
                    nom.setPrefWidth(140);
                    prix.setPrefWidth(100);
                    actionBox.setPrefWidth(100);

                    row.getChildren().addAll(libelle, nom, prix, actionBox);
                    setGraphic(row);
                }
            }
        });

        typeAnalyseTrierButton.setOnAction(e -> {
            String champTri = typeAnalyseTrierComboBox.getSelectionModel().getSelectedItem();
            Comparator<TypeAnalyse> comparateur = null;

            if ("Nom".equals(champTri)) {
                comparateur = Comparator.comparing(TypeAnalyse::getNom, String.CASE_INSENSITIVE_ORDER);
            } else if ("Libelle".equals(champTri)) {
                comparateur = Comparator.comparing(TypeAnalyse::getLibelle, String.CASE_INSENSITIVE_ORDER);
            } else if ("Prix".equals(champTri)) {
                comparateur = Comparator.comparing(TypeAnalyse::getPrix, Comparator.nullsLast(Comparator.naturalOrder()));
            }

            if (comparateur != null) {
                if (!trierTypeNomCroissant) {
                    comparateur = comparateur.reversed();
                }
                var triListe = typeAnalyseListView.getItems().stream()
                        .sorted(comparateur)
                        .toList();
                typeAnalyseListView.setItems(FXCollections.observableList(triListe));
                trierTypeNomCroissant = !trierTypeNomCroissant;
            }
        });


        typeAnalyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = typeAnalyseRechercherTextField.getText().strip().toLowerCase();
            String champChoisi = typeAnalyseRechercherComboBox.getSelectionModel().getSelectedItem();

            typeAnalyseListView.setItems(FXCollections.observableList(
                    typeAnalyseCrudService.findAll().stream()
                            .filter(a -> {
                                String valeurChamp = "";
                                if ("Nom".equals(champChoisi)) {
                                    valeurChamp = (a.getNom() != null) ? a.getNom() : "";
                                } else if ("Libelle".equals(champChoisi)) {
                                    valeurChamp = (a.getLibelle() != null) ? a.getLibelle() : "";
                                }
                                return valeurChamp.toLowerCase().contains(recherche);
                            })
                            .toList()
            ));
        });

        typeAnalyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/formTypeAnalyse.fxml",
                typeAnalyseAjouterButton.getScene(),
                null
        ));

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));

        // Menu navigation
        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml", menuAnalyseButton.getScene(), null));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml", menuTypeAnalyseButton.getScene(), null));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml", menuDispoAnalyseButton.getScene(), null));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/dashboardLabo.fxml", menuDashboardButton.getScene(), null));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));
    }

    private void resetListItems() {
        typeAnalyseListView.setItems(FXCollections.observableList(typeAnalyseCrudService.findAll()));
    }
}
