package org.hospiconnect.controller.homepages;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;

public class FrontListController {

    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLaboButton;
    @FXML
    private Button menuDonButton;
    @FXML
    private Button menuOpButton;
    @FXML
    private Button menuConsulButton;
    @FXML
    private Button menuBFHomeButton;

    @FXML
    public void initialize() {

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));

        // Menu navigation
        menuUserButton.setOnAction(e -> SceneUtils.openNewScene(
                "/views/mes-reclamations.fxml", menuUserButton.getScene(), null));
        menuLaboButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireFront/rdvAnalyse/listRdvAnalyse.fxml", menuLaboButton.getScene(), null));
        menuDonButton.setOnAction(e -> SceneUtils.openNewScene(
                "/Demandes/ShowDemande.fxml", menuDonButton.getScene(), null));
        menuOpButton.setOnAction(e -> SceneUtils.openNewScene(
                "/ListeRendezVous.fxml", menuOpButton.getScene(), null));
        menuConsulButton.setOnAction(e -> SceneUtils.openNewScene(
                "/main_view.fxml", menuConsulButton.getScene(), null));
        menuBFHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontBack.fxml", menuBFHomeButton.getScene(), null));

    }
}
