package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.service.laboratoire.DisponibiliteAnalyseCrudService;
import org.hospiconnect.service.laboratoire.DisponibiliteAnalyseValidationService;
import org.hospiconnect.service.laboratoire.TypeAnalyseCrudService;
import org.hospiconnect.service.laboratoire.TypeAnalyseValidationService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
    private Button menuHomeButton;

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

        dispoAnalyseFormNbrPlaceSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10));

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
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));
    }

    private void fillForm(DisponibiliteAnalyse toEdit) {
        dispoAnalyseFormDateDispoDatePicker.setValue(toEdit.getDispo());
        dispoAnalyseFormHeureDebutTextField.setText(toEdit.getDebut().toString());
        dispoAnalyseFormHeureFinTextField.setText(toEdit.getFin().toString());
        dispoAnalyseFormNbrPlaceSpinner.getValueFactory().setValue(toEdit.getNbrPlaces());
    }

    private boolean saveForm(DisponibiliteAnalyse toEdit) {
        DisponibiliteAnalyse toSave = (toEdit == null) ? new DisponibiliteAnalyse() : toEdit;
        var errors = new ArrayList<String>();
        var timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        toSave.setDispo(dispoAnalyseFormDateDispoDatePicker.getValue());
        try{
            toSave.setDebut(dispoAnalyseFormHeureDebutTextField.getText().isBlank() ? null : LocalTime.parse(dispoAnalyseFormHeureDebutTextField.getText(),timeFormat));
        } catch (DateTimeParseException e) {
            errors.add("Heure Debut doit avoir le format HH:mm");
        }
        try{
            toSave.setFin(dispoAnalyseFormHeureFinTextField.getText().isBlank() ? null : LocalTime.parse(dispoAnalyseFormHeureFinTextField.getText(),timeFormat));
        } catch (DateTimeParseException e) {
            errors.add("Heure Fin doit avoir le format HH:mm");
        }
        toSave.setNbrPlaces(dispoAnalyseFormNbrPlaceSpinner.getValue());
        errors.addAll(DisponibiliteAnalyseValidationService.getInstance().validate(toSave));
        if (!errors.isEmpty()) {
            var msg = String.join("\n- ", errors);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dispo analyse invalide");
            alert.setContentText("Dispo analyse saisie est invalide:\n- "+msg);
            alert.show();
            return false;
        }

        if (toEdit != null) {
            DisponibiliteAnalyseCrudService.getInstance().update(toSave);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dispo analyse mise à jour");
            alert.setContentText("Dispo analyse mise à jour avec succés!");
            alert.show();
        } else {
            DisponibiliteAnalyseCrudService.getInstance().createNew(toSave);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dispo analyse crée");
            alert.setContentText("Dispo analyse insérée avec succés!");
            alert.show();
        }
        return true;
    }

}
