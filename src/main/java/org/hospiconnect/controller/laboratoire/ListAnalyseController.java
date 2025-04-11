package org.hospiconnect.controller.laboratoire;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.StringUtils;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.service.laboratoire.AnalyseCrudService;
import org.hospiconnect.service.laboratoire.AnalyseService;
import org.hospiconnect.service.laboratoire.UserServiceLight;

import java.util.Comparator;

public class ListAnalyseController {

    private final AnalyseCrudService analyseCrudService = AnalyseCrudService.getInstance();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();

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
    private Button analyseTrierButton;
    @FXML
    private TextField analyseRechercherTextField;


    @FXML
    private Button analysePdfButton;
    @FXML
    private Button analyseAjouterButton;


    @FXML
    private TableView<Analyse> analyseTableView;

    @FXML
    private TableColumn<Analyse, Long> analyseNumTableColumn;
    @FXML
    private TableColumn<Analyse, String> analysePatientTableColumn;
    @FXML
    private TableColumn<Analyse, String> analysePersonnelTableColumn;
    @FXML
    private TableColumn<Analyse, String> analyseDateRdvTableColumn;
    @FXML
    private TableColumn<Analyse, String> analyseDatePrelevementTableColumn;
    @FXML
    private TableColumn<Analyse, String> analyseEtatTableColumn;
    @FXML
    private TableColumn<Analyse, String> analyseResultatTableColumn;
    @FXML
    private TableColumn<Analyse, String> analyseDateResultatTableColumn;
    @FXML
    private TableColumn<Analyse, Button> analyseActionsTableColumn;


    @FXML
    public void initialize() {
        resetTableItems();

        analyseNumTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        analysePatientTableColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(userServiceLight.findUserNameById(cell.getValue().getIdPatient()))
        );
        analysePersonnelTableColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(userServiceLight.findUserNameById(cell.getValue().getIdPersonnel()))
        );
        analyseDatePrelevementTableColumn.setCellValueFactory(new PropertyValueFactory<>("datePrelevement"));
        analyseEtatTableColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        analyseResultatTableColumn.setCellValueFactory(new PropertyValueFactory<>("resultat"));
        analyseDateResultatTableColumn.setCellValueFactory(new PropertyValueFactory<>("dateResultat"));

        analyseActionsTableColumn.setCellFactory(cell ->
                new EditRemoveButtons<>(
                        a -> SceneUtils.openNewScene(
                                "/laboratoireBack/analyse/formAnalyse.fxml",
                                analyseAjouterButton.getScene(),
                                a
                        ),
                        a -> {
                            AnalyseCrudService.getInstance().deleteById(a.getId());
                            resetTableItems();
                        }
                )
        );

        analysePdfButton.setOnAction(e -> AnalyseService.getInstance().exportAnalyses("analyses.pdf"));
        analyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/formAnalyse.fxml",
                analyseAjouterButton.getScene(),
                null
        ));
        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        analyseTrierButton.setOnAction(e -> analyseTableView.setItems(
                analyseTableView.getItems().sorted(Comparator.comparing(Analyse::getDatePrelevement))
        ));
        analyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = analyseRechercherTextField.getText().strip();
            analyseTableView.setItems(FXCollections.observableList(analyseCrudService.findAll()
                    .stream()
                    .filter(a -> recherche.isBlank() || UserServiceLight.getInstance().findUserNameById(a.getIdPatient()).contains(recherche))
                    .toList()
            ));
        });

        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml",
                menuAnalyseButton.getScene(),
                null
        ));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml",
                menuTypeAnalyseButton.getScene(),
                null
        ));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml",
                menuDispoAnalyseButton.getScene(),
                null
        ));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml",
                menuDashboardButton.getScene(),
                null
        ));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml",
                menuHospiChatButton.getScene(),
                null
        ));
    }

    private void resetTableItems() {
        analyseTableView.setItems(FXCollections.observableList(analyseCrudService.findAll()));
    }
}
