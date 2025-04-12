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
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.TypeAnalyseCrudService;

import java.util.Comparator;

public class ListTypeAnalyseController {

    private final TypeAnalyseCrudService typeAnalyseCrudService = TypeAnalyseCrudService.getInstance();

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
    private Button typeAnalyseTrierButton;
    @FXML
    private TextField typeAnalyseRechercherTextField;

    @FXML
    private Button typeAnalysePdfButton;
    @FXML
    private Button typeAnalyseAjouterButton;


    @FXML
    private TableView<TypeAnalyse> typeAnalyseTableView;

    @FXML
    private TableColumn<TypeAnalyse, Long> typeAnalyseNumTableColumn;
    @FXML
    private TableColumn<TypeAnalyse, String> typeAnalyseLibelleTableColumn;
    @FXML
    private TableColumn<TypeAnalyse, String> typeAnalyseNomTableColumn;
    @FXML
    private TableColumn<TypeAnalyse, Float> typeAnalysePrixTableColumn;
    @FXML
    private TableColumn<TypeAnalyse, Button> typeAnalyseActionsTableColumn;

    @FXML
    public void initialize() {
        resetTableItems();

        typeAnalyseNumTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeAnalyseLibelleTableColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        typeAnalyseNomTableColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeAnalysePrixTableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        typeAnalyseActionsTableColumn.setCellFactory(cell ->
                new EditRemoveButtons<>(
                        a -> SceneUtils.openNewScene(
                                "/laboratoireBack/typeAnalyse/formTypeAnalyse.fxml",
                                typeAnalyseAjouterButton.getScene(),
                                a
                        ),
                        a -> {
                            TypeAnalyseCrudService.getInstance().deleteById(a.getId());
                            resetTableItems();
                        })
        );

        //analysePdfButton.setOnAction(e -> AnalyseService.getInstance().exportAnalyses("analyses.pdf"));
        typeAnalyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/formTypeAnalyse.fxml",
                typeAnalyseAjouterButton.getScene(),
                null
        ));
        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        typeAnalyseTrierButton.setOnAction(e -> typeAnalyseTableView.setItems(
                typeAnalyseTableView.getItems().sorted(Comparator.comparing(TypeAnalyse::getNom))
        ));
        typeAnalyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = typeAnalyseRechercherTextField.getText().strip();
            typeAnalyseTableView.setItems(FXCollections.observableList(typeAnalyseCrudService.findAll()
                    .stream()
                    .filter(a -> recherche.isBlank() || typeAnalyseCrudService.findTypeNameById(a.getId()).contains(recherche))
                    .toList()
            ));
        });

        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml",
                menuAnalyseButton.getScene(),
                null
        ));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml",
                menuTypeAnalyseButton.getScene(),
                null
        ));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml",
                menuDispoAnalyseButton.getScene(),
                null
        ));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/dashboardLabo.fxml",
                menuDashboardButton.getScene(),
                null
        ));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/hospiChatLabo.fxml",
                menuHospiChatButton.getScene(),
                null
        ));
    }

    private void resetTableItems() {
        typeAnalyseTableView.setItems(FXCollections.observableList(typeAnalyseCrudService.findAll()));
    }

}