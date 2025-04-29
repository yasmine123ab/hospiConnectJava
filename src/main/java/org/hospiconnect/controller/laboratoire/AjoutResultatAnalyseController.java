package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.service.laboratoire.*;

import java.util.List;

public class AjoutResultatAnalyseController {

    private Analyse analyse;
    private final TypeAnalyseCrudService typeAnalyseCrudService = TypeAnalyseCrudService.getInstance();

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
    private Button menuCalendrierButton;

    @FXML
    private CheckBox envoyerMailCheckBox;

    @FXML
    private TextArea ResultatTextArea;
    @FXML
    private Button resultatAnalyseEnregistrerButton;


    @FXML
    public void initialize() {
        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        resultatAnalyseEnregistrerButton.setOnAction(e -> {
            String resultat = ResultatTextArea.getText();
            if (resultat.isBlank()) {
                String typeNom = typeAnalyseCrudService.findTypeNameById(analyse.getIdTypeAnalyse()).toLowerCase();
                resultat = TypeAnalyseService.getInstance().getTemplate(typeNom);
            }
            analyse.setResultat(resultat);
            AnalyseCrudService.getInstance().update(analyse);
            // Génération du PDF avec Résultat + Commentaire IA
            if (envoyerMailCheckBox.isSelected()) {
                String pdfFilePath = "ResultatAnalyse.pdf";
                ResultatAnalyseAiPdfService.getInstance().generateAnalysePdf(
                        resultat,
                        pdfFilePath
                );

                // Envoyer l'email

                try {
                    String patientEmail = UserServiceLight.getInstance().findUserEmailById(analyse.getIdPatient());
                    if (patientEmail != null && !patientEmail.isBlank()) {
                        MailService.getInstance().sendEmailWithAttachment(
                                patientEmail,
                                "Résultat d'analyse médicale disponible",
                                """
                                        Bonjour,
                                        
                                        Vos résultats d'analyses sont désormais disponibles en pièce jointe (format PDF).
                                        
                                        Merci de votre confiance,
                                        HospiConnect – votre partenaire santé.
                                        """,
                                pdfFilePath
                        );
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur Email");
                    alert.setContentText("L'envoi de l'email au patient a échoué.");
                    alert.show();
                }
            }

            SceneUtils.openNewScene(
                    "/laboratoireBack/analyse/listAnalyse.fxml",
                    resultatAnalyseEnregistrerButton.getScene(),
                    null
            );
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
        menuCalendrierButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/calendrierLabo.fxml", menuCalendrierButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));

        this.analyse = (Analyse) SceneUtils.getContext();

        ResultatTextArea.setText(analyse.getResultat());
    }


}
