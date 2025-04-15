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

    private final TypeAnalyseCrudService typeAnalyseCrudService = TypeAnalyseCrudService.getInstance();


    @FXML
    private ListView<TypeAnalyse> typeAnalyseListView;

    @FXML
    private Button typeAnalyseTrierButton;
    @FXML
    private TextField typeAnalyseRechercherTextField;

    @FXML
    private Button typeAnalysePdfButton;
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
    public void initialize() {
        resetListItems();

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
            typeAnalyseListView.setItems(typeAnalyseListView.getItems().sorted(Comparator.comparing(TypeAnalyse::getNom)));
        });

        typeAnalyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = typeAnalyseRechercherTextField.getText().strip();
            typeAnalyseListView.setItems(FXCollections.observableList(typeAnalyseCrudService.findAll()
                    .stream()
                    .filter(a -> recherche.isBlank() || typeAnalyseCrudService.findTypeNameById(a.getId()).contains(recherche))
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
    }

    private void resetListItems() {
        typeAnalyseListView.setItems(FXCollections.observableList(typeAnalyseCrudService.findAll()));
    }
}
