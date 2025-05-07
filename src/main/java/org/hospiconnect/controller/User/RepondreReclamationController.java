package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class RepondreReclamationController {

    @FXML
    private Button btnEnvoyer;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnSpeech;

    @FXML
    private TextArea reponseTextArea;

    @FXML
    private Label titreLabel;




    // Action pour le bouton Annuler
    @FXML
    public void handleAnnuler(ActionEvent actionEvent) {
        reponseTextArea.clear();
    }

    // Action pour le bouton de reconnaissance vocale
    @FXML
    public void handleSpeech(ActionEvent actionEvent) {
        // Pour le moment, on simule une dictée automatique
        String simulatedSpeechText = "Ceci est une réponse dictée automatiquement.";
        reponseTextArea.appendText(simulatedSpeechText);
    }

    // Cette méthode permet de passer le titre depuis le handleRispond
    public void setReclamationTitle(String title) {
        titreLabel.setText(title);
    }
}
