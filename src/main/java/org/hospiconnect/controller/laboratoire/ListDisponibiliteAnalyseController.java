package org.hospiconnect.controller.laboratoire;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;
import org.hospiconnect.service.laboratoire.DisponibiliteAnalyseCrudService;

public class ListDisponibiliteAnalyseController {

    private final DisponibiliteAnalyseCrudService dispoAnalyseCrudService = DisponibiliteAnalyseCrudService.getInstance();

    @FXML
    private TableView<DisponibiliteAnalyse> dispoAnalyseTableView;

    @FXML
    private TableColumn<DisponibiliteAnalyse, Long> dispoAnalyseNumTableColumn;
    @FXML
    private TableColumn<DisponibiliteAnalyse, String> dispoAnalyseDateDispoTableColumn;
    @FXML
    private TableColumn<DisponibiliteAnalyse, String> dispoAnalyseHeureDebutTableColumn;
    @FXML
    private TableColumn<DisponibiliteAnalyse, String> dispoAnalyseHeureFinTableColumn;
    @FXML
    private TableColumn<DisponibiliteAnalyse, String> dispoAnalyseNbrPlacesTableColumn;
    @FXML
    private TableColumn<DisponibiliteAnalyse, Button> dispoAnalyseActionsTableColumn;

    /*@FXML
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
    }*/



}
