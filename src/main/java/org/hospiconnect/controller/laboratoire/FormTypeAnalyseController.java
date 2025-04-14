package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.*;

import java.util.stream.Collectors;

public class FormTypeAnalyseController {

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
    private TextField typeAnalyseFormLibelleTextField;
    @FXML
    private TextField typeAnalyseFormNomTextField;
    @FXML
    private TextField typeAnalyseFormPrixTextField;
    @FXML
    private Button typeAnalyseFormEnregistrerButton;


    @FXML
    public void initialize() {
        TypeAnalyse toEdit = (TypeAnalyse) SceneUtils.getContext();
        boolean editMode = toEdit != null;


        if (editMode) {
            fillForm(toEdit);
        }

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        typeAnalyseFormEnregistrerButton.setOnAction(e -> {
            if (saveForm(toEdit)) {
                SceneUtils.openNewScene(
                        "/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml",
                        typeAnalyseFormEnregistrerButton.getScene(),
                        null
                );
            }
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

    private void fillForm(TypeAnalyse toEdit) {
        typeAnalyseFormLibelleTextField.setText(toEdit.getLibelle());
        typeAnalyseFormNomTextField.setText(toEdit.getNom());
        typeAnalyseFormPrixTextField.setText(toEdit.getPrix().toString());
    }

    private boolean saveForm(TypeAnalyse toEdit) {
        TypeAnalyse toSave = (toEdit == null) ? new TypeAnalyse() : toEdit;
        toSave.setLibelle(typeAnalyseFormLibelleTextField.getText());
        toSave.setNom(typeAnalyseFormNomTextField.getText());
        toSave.setPrix(Float.valueOf(typeAnalyseFormPrixTextField.getText()));

        var errors = TypeAnalyseValidationService.getInstance().validate(toSave);
        if (!errors.isEmpty()) {
            var msg = String.join("\n- ", errors);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Type analyse invalide");
            alert.setContentText("Type analyse saisie est invalide:\n- "+msg);
            alert.show();
            return false;
        }

        if (toEdit != null) {
            TypeAnalyseCrudService.getInstance().update(toSave);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Type analyse mise à jour");
            alert.setContentText("Type analyse mise à jour avec succés!");
            alert.show();
        } else {
            TypeAnalyseCrudService.getInstance().createNew(toSave);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Type analyse crée");
            alert.setContentText("Type analyse insérée avec succés!");
            alert.show();
        }
        return true;
    }




}
