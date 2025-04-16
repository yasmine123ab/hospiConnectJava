package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.service.laboratoire.*;

import java.util.stream.Collectors;

public class FormAnalyseController {
    private static final String EN_ATTENTE = "en attente";
    private static final String EN_COURS = "en cours";
    private static final String TERMINE = "termine";

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
    private ComboBox<ComboBoxItemWithId> analyseFormPatientComboBox;
    @FXML
    private ComboBox<ComboBoxItemWithId> analyseFormPersonnelComboBox;
    @FXML
    private ComboBox<ComboBoxItemWithId> analyseFormRdvComboBox;
    @FXML
    private DatePicker analyseFormDatePrelevDatePicker;
    @FXML
    private ComboBox<ComboBoxItemWithId> analyseFormTypeAnalyseComboBox;

    @FXML
    private RadioButton analyseFormEnAttenteRadioButton;
    @FXML
    private RadioButton analyseFormTermineRadioButton;
    @FXML
    private RadioButton analyseFormEnCoursRadioButton;

    @FXML
    private DatePicker analyseFormDateResultatDatePicker;
    @FXML
    private TextField analyseFormResultatTextField;
    @FXML
    private Button analyseFormEnregistrerButton;

    @FXML
    public void initialize() {
        Analyse toEdit = (Analyse) SceneUtils.getContext();
        boolean editMode = toEdit != null;

        initComponents();

        if (editMode) {
            fillForm(toEdit);
        }

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        analyseFormEnregistrerButton.setOnAction(e -> {
            if (saveForm(toEdit)) {
                SceneUtils.openNewScene(
                        "/laboratoireBack/analyse/listAnalyse.fxml",
                        analyseFormEnregistrerButton.getScene(),
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

    private void initComponents() {
        analyseFormPatientComboBox.getItems().clear();
        analyseFormPatientComboBox.getItems().addAll(UserServiceLight.getInstance()
                .getUsersWithId()
                .entrySet()
                .stream()
                .map(e -> new ComboBoxItemWithId(e.getKey(), e.getValue()))
                .toList()
        );

        analyseFormPersonnelComboBox.getItems().clear();
        analyseFormPersonnelComboBox.getItems().addAll(UserServiceLight.getInstance()
                .getUsersWithId()
                .entrySet()
                .stream()
                .map(e -> new ComboBoxItemWithId(e.getKey(), e.getValue()))
                .toList()
        );

        analyseFormRdvComboBox.getItems().clear();
        analyseFormRdvComboBox.getItems().addAll(AnalyseRdvCrudService.getInstance()
                .findRdvsWithId()
                .entrySet()
                .stream()
                .map(e -> new ComboBoxItemWithId(e.getKey(), e.getValue()))
                .toList()
        );

        analyseFormTypeAnalyseComboBox.getItems().clear();
        analyseFormTypeAnalyseComboBox.getItems().addAll(TypeAnalyseCrudService.getInstance()
                .findAll()
                .stream()
                .map(e -> new ComboBoxItemWithId(e.getId(), e.getLibelle()))
                .toList()
        );

        ToggleGroup tg = new ToggleGroup();
        analyseFormEnAttenteRadioButton.setToggleGroup(tg);
        analyseFormTermineRadioButton.setToggleGroup(tg);
        analyseFormEnCoursRadioButton.setToggleGroup(tg);
    }

    private void fillForm(Analyse toEdit) {
        analyseFormPatientComboBox.setValue(analyseFormPatientComboBox
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(toEdit.getIdPatient()))
                .findFirst()
                .orElse(null)
        );

        analyseFormPersonnelComboBox.setValue(analyseFormPersonnelComboBox
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(toEdit.getIdPersonnel()))
                .findFirst()
                .orElse(null)
        );

        analyseFormRdvComboBox.setValue(analyseFormRdvComboBox
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(toEdit.getIdRdv()))
                .findFirst()
                .orElse(null)
        );

        analyseFormTypeAnalyseComboBox.setValue(analyseFormTypeAnalyseComboBox
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(toEdit.getIdTypeAnalyse()))
                .findFirst()
                .orElse(null)
        );

        switch (toEdit.getEtat()) {
            case EN_ATTENTE -> analyseFormEnAttenteRadioButton.selectedProperty().setValue(true);
            case EN_COURS -> analyseFormEnCoursRadioButton.selectedProperty().setValue(true);
            case TERMINE -> analyseFormTermineRadioButton.selectedProperty().setValue(true);
        }

        analyseFormDatePrelevDatePicker.setValue(toEdit.getDatePrelevement());
        analyseFormDateResultatDatePicker.setValue(toEdit.getDateResultat());
        analyseFormResultatTextField.setText(toEdit.getResultat());
    }

    private boolean saveForm(Analyse toEdit) {
        Analyse toSave = (toEdit == null) ? new Analyse() : toEdit;
        //System.out.println(toSave);
        toSave.setIdPatient(analyseFormPatientComboBox.getValue() != null ? analyseFormPatientComboBox.getValue().getId() : null);
        toSave.setIdPersonnel(analyseFormPersonnelComboBox.getValue() != null ? analyseFormPersonnelComboBox.getValue().getId() : null);
        toSave.setIdRdv(analyseFormRdvComboBox.getValue() != null ? analyseFormRdvComboBox.getValue().getId() : null);
        toSave.setIdTypeAnalyse(analyseFormTypeAnalyseComboBox.getValue() != null ? analyseFormTypeAnalyseComboBox.getValue().getId() : null);

        if (analyseFormEnAttenteRadioButton.selectedProperty().get()) {
            toSave.setEtat(EN_ATTENTE);
        } else if (analyseFormEnCoursRadioButton.selectedProperty().get()) {
            toSave.setEtat(EN_COURS);
        } else if (analyseFormTermineRadioButton.selectedProperty().get()) {
            toSave.setEtat(TERMINE);
        }
        toSave.setDatePrelevement(analyseFormDatePrelevDatePicker.getValue());
        toSave.setDateResultat(analyseFormDateResultatDatePicker.getValue());
        toSave.setResultat(analyseFormResultatTextField.getText());
        //System.out.println(toSave);
        //System.out.println(AnalyseCrudService.getInstance().findAll());

        var errors = AnalyseValidationService.getInstance().validate(toSave);
        if (!errors.isEmpty()) {
            var msg = String.join("\n- ", errors);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Analyse invalide");
            alert.setContentText("Analyse saisie est invalide:\n- "+msg);
            alert.show();
            return false;
        }

        if (toEdit != null) {
            AnalyseCrudService.getInstance().update(toSave);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analyse mise à jour");
            alert.setContentText("Analyse mise à jour avec succés!");
            alert.show();
        } else {
            AnalyseCrudService.getInstance().createNew(toSave);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analyse crée");
            alert.setContentText("Analyse insérée avec succés!");
            alert.show();
        }
        return true;
    }

}
