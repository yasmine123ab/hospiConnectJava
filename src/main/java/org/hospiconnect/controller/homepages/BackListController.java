package org.hospiconnect.controller.homepages;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;

public class BackListController {

    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLaboButton;
    @FXML
    private Button menuStockButton;
    @FXML
    private Button menuBFHomeButton;

    @FXML
    public void initialize() {

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));

        // Menu navigation
        menuUserButton.setOnAction(e -> SceneUtils.openNewScene(
                "/dashboard.fxml", menuUserButton.getScene(), null));
        menuLaboButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml", menuLaboButton.getScene(), null));
        menuStockButton.setOnAction(e -> SceneUtils.openNewScene(
                "/ListMateriel.fxml", menuStockButton.getScene(), null));
        menuBFHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontBack.fxml", menuBFHomeButton.getScene(), null));

    }


}
