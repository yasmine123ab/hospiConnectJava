package org.hospiconnect.controller.laboratoire;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;
import org.hospiconnect.model.laboratoire.RdvAnalyse;
import org.hospiconnect.service.laboratoire.*;

import java.util.Comparator;

public class ListDisponibiliteAnalyseController {

    private boolean trierDispoCroissant = true;

    private final DisponibiliteAnalyseCrudService disponibiliteAnalyseCrudService = DisponibiliteAnalyseCrudService.getInstance();


    @FXML
    private ListView<DisponibiliteAnalyse> dispoAnalyseListView;

    @FXML
    private TextField dispoAnalyseRechercherTextField;
    @FXML
    private Button dispoAnalyseTrierButton;
    @FXML
    private Button dispoAnalyseAjouterButton;
    @FXML
    private Button dispoAnalysePdfButton;
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

        dispoAnalyseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(DisponibiliteAnalyse dispoAnalyse, boolean empty) {
                super.updateItem(dispoAnalyse, empty);
                if (empty || dispoAnalyse == null) {
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label dispo = new Label(dispoAnalyse.getDispo().toString());
                    Label heureDebut = new Label(dispoAnalyse.getDebut().toString());
                    Label heureFin = new Label(dispoAnalyse.getFin().toString());
                    Label nbrPlaces = new Label(dispoAnalyse.getNbrPlaces().toString());

                    EditRemoveButtonsBox<DisponibiliteAnalyse> actionsFactory = new EditRemoveButtonsBox<>();
                    HBox actionBox = actionsFactory.create(dispoAnalyse,
                            a -> SceneUtils.openNewScene(
                                    "/laboratoireBack/disponibiliteAnalyse/formDispoAnalyse.fxml",
                                    dispoAnalyseAjouterButton.getScene(),
                                    a
                            ),
                            a -> {
                                disponibiliteAnalyseCrudService.deleteById(a.getId());
                                resetListItems();
                            }
                    );


                    dispo.setPrefWidth(150);
                    heureDebut.setPrefWidth(150);
                    heureFin.setPrefWidth(150);
                    nbrPlaces.setPrefWidth(80);

                    actionBox.setPrefWidth(100);

                    row.getChildren().addAll(dispo, heureDebut, heureFin, nbrPlaces, actionBox);
                    setGraphic(row);
                }
            }
        });

        dispoAnalyseTrierButton.setOnAction(e -> {
            Comparator<DisponibiliteAnalyse> comparateur = Comparator.comparing(DisponibiliteAnalyse::getDebut);
            if (!trierDispoCroissant) {
                comparateur = comparateur.reversed();
            }

            var triListe = dispoAnalyseListView.getItems().stream()
                    .sorted(comparateur)
                    .toList();
            dispoAnalyseListView.setItems(FXCollections.observableList(triListe));

            trierDispoCroissant = !trierDispoCroissant;
        });

        dispoAnalyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = dispoAnalyseRechercherTextField.getText().strip().toLowerCase();

            dispoAnalyseListView.setItems(FXCollections.observableList(
                    disponibiliteAnalyseCrudService.findAll().stream()
                            .filter(a -> {
                                String dateDispoStr = a.getDispo() != null ? a.getDispo().toString().toLowerCase() : "";
                                String heureDebutStr = a.getDebut() != null ? a.getDebut().toString().toLowerCase() : "";
                                String nbrPlacesStr = a.getNbrPlaces() != null ? a.getNbrPlaces().toString() : "";

                                return recherche.isBlank()
                                        || dateDispoStr.contains(recherche)
                                        || heureDebutStr.contains(recherche)
                                        || nbrPlacesStr.contains(recherche);
                            })
                            .toList()
            ));
        });

        dispoAnalyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/disponibiliteAnalyse/formDispoAnalyse.fxml",
                dispoAnalyseAjouterButton.getScene(),
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
        dispoAnalyseListView.setItems(FXCollections.observableList(disponibiliteAnalyseCrudService.findAll()));
    }
}
