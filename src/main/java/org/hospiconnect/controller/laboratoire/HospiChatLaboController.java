package org.hospiconnect.controller.laboratoire;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hospiconnect.service.laboratoire.OpenAiService;

public class HospiChatLaboController {

    private final OpenAiService openAiService = OpenAiService.getInstance();

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
    private Button hospiChatLaboButton;
    @FXML
    private TextField hospiChatLaboQuestionTextField;
    @FXML
    private ScrollPane hospiChatLaboQuestionScrollPane;
    @FXML
    private VBox hospiChatLaboQuestionVBox;

    @FXML
    public void initialize() {

        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));
        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml", menuAnalyseButton.getScene(), null));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml", menuTypeAnalyseButton.getScene(), null));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml", menuDispoAnalyseButton.getScene(), null));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/dashboardLabo.fxml", menuDashboardButton.getScene(), null));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));

        hospiChatLaboButton.setOnAction(event -> {
            String question = hospiChatLaboQuestionTextField.getText().trim();

            if (question.isEmpty()) {
                addMessage("❗ Veuillez entrer une question.", true);
                return;
            }

            addMessage("Vous : " + question, true);
            hospiChatLaboQuestionTextField.clear();

            Label responseLabel = addStreamingPlaceholder("HospiChat : Attendez un instant...");

            new Thread(() -> {
                try {
                    String fullResponse = openAiService.askOpenAi(question);

                    // Affichage caractère par caractère
                    StringBuilder displayedText = new StringBuilder("HospiChat : ");
                    for (char c : fullResponse.toCharArray()) {
                        Thread.sleep(20); // vitesse de "streaming"
                        displayedText.append(c);
                        String currentText = displayedText.toString();
                        Platform.runLater(() -> responseLabel.setText(currentText));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> responseLabel.setText("⚠️ Erreur lors de la communication avec l'IA."));
                }
            }).start();
        });
    }

    private void addMessage(String message, boolean isUser) {
        Label label = createStyledLabel(message, isUser);
        HBox messageContainer = wrapLabelInHBox(label, isUser);
        hospiChatLaboQuestionVBox.getChildren().add(messageContainer);
        Platform.runLater(() -> hospiChatLaboQuestionScrollPane.setVvalue(1.0));
    }

    // 🔄 Nouvelle méthode : créer un message "placeholder" modifiable (streaming)
    private Label addStreamingPlaceholder(String initialText) {
        Label label = createStyledLabel(initialText, false);
        HBox messageContainer = wrapLabelInHBox(label, false);
        hospiChatLaboQuestionVBox.getChildren().add(messageContainer);
        Platform.runLater(() -> hospiChatLaboQuestionScrollPane.setVvalue(1.0));
        return label; // pour pouvoir le modifier après
    }

    private Label createStyledLabel(String text, boolean isUser) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(600);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.setPadding(new Insets(10));
        label.setStyle(isUser
                ? "-fx-background-color: #D0E8FF; -fx-background-radius: 10; -fx-text-fill: black;"
                : "-fx-background-color: #DBDBDB; -fx-background-radius: 10; -fx-text-fill: black;");
        return label;
    }

    private HBox wrapLabelInHBox(Label label, boolean isUser) {
        HBox messageContainer = new HBox(label);
        messageContainer.setPadding(new Insets(5));
        messageContainer.setNodeOrientation(isUser
                ? NodeOrientation.RIGHT_TO_LEFT
                : NodeOrientation.LEFT_TO_RIGHT);
        return messageContainer;
    }

}
