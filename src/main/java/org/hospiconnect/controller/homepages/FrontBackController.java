package org.hospiconnect.controller.homepages;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;

public class FrontBackController {

    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

    @FXML
    private Button menuFrontButton;
    @FXML
    private Button menuBackButton;

    @FXML
    public void initialize() {

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));

        // Menu navigation
        menuFrontButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuFrontButton.getScene(), null));
        menuBackButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuBackButton.getScene(), null));

    }
}
