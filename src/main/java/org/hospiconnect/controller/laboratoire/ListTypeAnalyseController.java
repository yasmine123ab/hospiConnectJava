package org.hospiconnect.controller.laboratoire;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.AnalyseCrudService;
import org.hospiconnect.service.laboratoire.TypeAnalyseCrudService;
import org.hospiconnect.service.laboratoire.UserServiceLight;

public class ListTypeAnalyseController {

    private final TypeAnalyseCrudService typeAnalyseCrudService = TypeAnalyseCrudService.getInstance();

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
        typeAnalyseTableView.setItems(FXCollections.observableList(typeAnalyseCrudService.findAll()));

        typeAnalyseNumTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeAnalyseLibelleTableColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        typeAnalyseNomTableColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeAnalysePrixTableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        typeAnalyseActionsTableColumn.setCellFactory(cell ->
                new EditRemoveButtons<>(
                        a -> System.out.println("Add " + a.getId()),
                        a -> System.out.println("Remove " + a.getId())
                )
        );
    }











}
