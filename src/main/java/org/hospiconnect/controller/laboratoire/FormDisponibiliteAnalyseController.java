package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.TypeAnalyseCrudService;
import org.hospiconnect.service.laboratoire.TypeAnalyseValidationService;

public class FormDisponibiliteAnalyseController {

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
    private DatePicker dispoAnalyseFormDateDispoDatePicker;
    @FXML
    private TextField dispoAnalyseFormHeureDebutTextField;
    @FXML
    private TextField dispoAnalyseFormHeureFinTextField;
    @FXML
    private Spinner<Integer> dispoAnalyseFormNbrPlaceSpinner;
    @FXML
    private Button dispoAnalyseFormEnregistrerButton;

    @FXML
    public void initialize() {
        DisponibiliteAnalyse toEdit = (DisponibiliteAnalyse) SceneUtils.getContext();
        boolean editMode = toEdit != null;


        if (editMode) {
            fillForm(toEdit);
        }

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        dispoAnalyseFormEnregistrerButton.setOnAction(e -> {
            if (saveForm(toEdit)) {
                SceneUtils.openNewScene(
                        "/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml",
                        dispoAnalyseFormEnregistrerButton.getScene(),
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

    private void fillForm(DisponibiliteAnalyse toEdit) {
        dispoAnalyseFormDateDispoDatePicker.setValue(toEdit.getDebut().toLocalDate());
        dispoAnalyseFormHeureDebutTextField.setText(toEdit.getDebut().toString());
        dispoAnalyseFormHeureFinTextField.setText(toEdit.getFin().toString());
        dispoAnalyseFormNbrPlaceSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10)
        );
        dispoAnalyseFormNbrPlaceSpinner.getValueFactory().setValue(toEdit.getNbrPlaces());
    }

    private boolean saveForm(DisponibiliteAnalyse toEdit) {
        DisponibiliteAnalyse toSave = (toEdit == null) ? new DisponibiliteAnalyse() : toEdit;
        toSave.set(analyseFormDatePrelevDatePicker.getValue());
        toSave.setDebut(dispoAnalyseFormHeureDebutTextField.getText().);


        toSave.setNom(typeAnalyseFormNomTextField.getText());
        toSave.setPrix(Float.valueOf(typeAnalyseFormPrixTextField.getText()));

        /*var errors = TypeAnalyseValidationService.getInstance().validate(toSave);
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
        }*/
        return true;
    }

}
