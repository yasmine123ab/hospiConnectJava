package org.hospiconnect.controller.laboratoire;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.RdvAnalyse;
import org.hospiconnect.service.laboratoire.AnalyseCrudService;
import org.hospiconnect.service.laboratoire.AnalyseRdvCrudService;
import org.hospiconnect.service.laboratoire.AnalyseService;
import org.hospiconnect.service.laboratoire.UserServiceLight;

import java.util.Comparator;

public class ListAnalyseController {

    private final AnalyseCrudService analyseCrudService = AnalyseCrudService.getInstance();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();
    private final AnalyseRdvCrudService analyseRdvCrudService = AnalyseRdvCrudService.getInstance();


    @FXML
    private ListView<Analyse> analyseListView;

    @FXML
    private TextField analyseRechercherTextField;
    @FXML
    private Button analyseTrierButton;
    @FXML
    private Button analyseAjouterButton;
    @FXML
    private Button analysePdfButton;
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

        analyseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Analyse analyse, boolean empty) {
                super.updateItem(analyse, empty);
                if (empty || analyse == null) {
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label patient = new Label(userServiceLight.findUserNameById(analyse.getIdPatient()));
                    Label personnel = new Label(userServiceLight.findUserNameById(analyse.getIdPersonnel()));
                    String dateRdvStr = analyseRdvCrudService.findDateRdvByIdOrNull(analyse.getIdRdv());
                    Label dateRdv = new Label(dateRdvStr != null ? dateRdvStr : "");

                    Label datePrelevement = new Label(
                            analyse.getDatePrelevement() != null ? analyse.getDatePrelevement().toString() : ""
                    );
                    Label etat = new Label(analyse.getEtat());
                    Label resultat = new Label(analyse.getResultat());
                    Label dateResultat = new Label(
                            analyse.getDateResultat() != null ? analyse.getDateResultat().toString() : ""
                    );
                    EditRemoveButtonsBox<Analyse> actionsFactory = new EditRemoveButtonsBox<>();
                    HBox actionBox = actionsFactory.create(analyse,
                            a -> SceneUtils.openNewScene(
                                    "/laboratoireBack/analyse/formAnalyse.fxml",
                                    analyseAjouterButton.getScene(),
                                    a
                            ),
                            a -> {
                                analyseCrudService.deleteById(a.getId());
                                resetListItems();
                            }
                    );

                    // Set width for consistent alignment
                    patient.setPrefWidth(140);
                    personnel.setPrefWidth(140);
                    dateRdv.setPrefWidth(100);
                    datePrelevement.setPrefWidth(140);
                    etat.setPrefWidth(80);
                    resultat.setPrefWidth(100);
                    dateResultat.setPrefWidth(120);
                    actionBox.setPrefWidth(100);

                    row.getChildren().addAll(patient, personnel, dateRdv, datePrelevement, etat, resultat, dateResultat, actionBox);
                    setGraphic(row);
                }
            }
        });

        analyseTrierButton.setOnAction(e -> {
            analyseListView.setItems(analyseListView.getItems().sorted(Comparator.comparing(Analyse::getDatePrelevement)));
        });

        analyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = analyseRechercherTextField.getText().strip();
            analyseListView.setItems(FXCollections.observableList(
                    analyseCrudService.findAll().stream()
                            .filter(a -> recherche.isBlank()
                                    || userServiceLight.findUserNameById(a.getIdPatient()).toLowerCase().contains(recherche.toLowerCase()))
                            .toList()
            ));
        });

        analysePdfButton.setOnAction(e -> AnalyseService.getInstance().exportAnalyses("analyses.pdf"));
        analyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/formAnalyse.fxml",
                analyseAjouterButton.getScene(),
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
        System.out.println(analyseListView);
        analyseListView.setItems(FXCollections.observableList(analyseCrudService.findAll()));
    }
}
