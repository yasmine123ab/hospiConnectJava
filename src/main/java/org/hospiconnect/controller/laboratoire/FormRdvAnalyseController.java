package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.RdvAnalyse;
import org.hospiconnect.service.laboratoire.*;

public class FormRdvAnalyseController {

    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;
    @FXML
    private Button menuRdvAnalyseButton;
    @FXML
    private Button menuHospiChatButton;

    @FXML
    private ComboBox<ComboBoxItemWithId> rdvAnalyseFormPatientComboBox;
    @FXML
    private DatePicker rdvAnalyseFormDateRdvDatePicker;
    @FXML
    private ComboBox<ComboBoxItemWithId> rdvAnalyseFormDispoComboBox;

    @FXML
    private Button rdvAnalyseFormEnregistrerButton;

    @FXML
    public void initialize() {
        RdvAnalyse toEdit = (RdvAnalyse) SceneUtils.getContext();
        boolean editMode = toEdit != null;

        initComponents();

        if (editMode) {
            fillForm(toEdit);
        }

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        rdvAnalyseFormEnregistrerButton.setOnAction(e -> {
            if (saveForm(toEdit)) {
                SceneUtils.openNewScene(
                        "/laboratoireFront/rdvAnalyse/listRdvAnalyse.fxml",
                        rdvAnalyseFormEnregistrerButton.getScene(),
                        null
                );
            }
        });

        menuRdvAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireFront/rdvAnalyse/listRdvAnalyse.fxml", menuRdvAnalyseButton.getScene(), null));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
    }

    private void initComponents() {
        rdvAnalyseFormPatientComboBox.getItems().clear();
        rdvAnalyseFormPatientComboBox.getItems().addAll(UserServiceLight.getInstance()
                .getUsersWithId()
                .entrySet()
                .stream()
                .map(e -> new ComboBoxItemWithId(e.getKey(), e.getValue()))
                .toList()
        );
        rdvAnalyseFormDispoComboBox.getItems().clear();
        rdvAnalyseFormDispoComboBox.getItems().addAll(DisponibiliteAnalyseCrudService.getInstance()
                .findAll()
                .stream()
                .map(e -> new ComboBoxItemWithId(e.getId(), e.getDebut().toString()))
                .toList()
        );
    }

    private void fillForm(RdvAnalyse toEdit) {
        rdvAnalyseFormPatientComboBox.setValue(rdvAnalyseFormPatientComboBox
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(toEdit.getIdPatient()))
                .findFirst()
                .orElse(null)
        );
        rdvAnalyseFormDateRdvDatePicker.setValue(toEdit.getDateRdv());
        rdvAnalyseFormDispoComboBox.setValue(rdvAnalyseFormDispoComboBox
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(toEdit.getIdDisponibilite()))
                .findFirst()
                .orElse(null)
        );
    }

        private boolean saveForm (RdvAnalyse toEdit) {
            RdvAnalyse toSave = (toEdit == null) ? new RdvAnalyse() : toEdit;
            toSave.setIdPatient(rdvAnalyseFormPatientComboBox.getValue() != null ? rdvAnalyseFormPatientComboBox.getValue().getId() : null);
            toSave.setDateRdv(rdvAnalyseFormDateRdvDatePicker.getValue());
            toSave.setIdDisponibilite(rdvAnalyseFormDispoComboBox.getValue() != null ? rdvAnalyseFormDispoComboBox.getValue().getId() : null);

            var errors = RdvAnalyseValidationService.getInstance().validate(toSave);
            if (!errors.isEmpty()) {
                var msg = String.join("\n- ", errors);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Rdv Analyse invalide");
                alert.setContentText("Rdv Analyse saisie est invalide:\n- "+msg);
                alert.show();
                return false;
            }

            if (toEdit != null) {
                AnalyseRdvCrudService.getInstance().update(toSave);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rdv Analyse mise à jour");
                alert.setContentText("Rdv Analyse mise à jour avec succés!");
                alert.show();
            } else {
                toSave.setStatut("En attente");
                AnalyseRdvCrudService.getInstance().createNew(toSave);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rdv Analyse crée");
                alert.setContentText("Rdv Analyse insérée avec succés!");
                alert.show();
            }
            return true;
        }






}
