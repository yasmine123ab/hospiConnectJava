package org.hospiconnect.controller;

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

public class ChatbotController {

    private final OpenAiService openAiService = OpenAiService.getInstance();

    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField userInputField;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox chatVBox;

    @FXML
    public void initialize() {
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        minimizeButton.setOnAction(e -> ((Stage) minimizeButton.getScene().getWindow()).setIconified(true));

        sendButton.setOnAction(event -> handleUserInput());
        userInputField.setOnAction(event -> handleUserInput());

        addMessage("Assistant: Bonjour! Posez-moi votre question dans n’importe quelle langue.", false);
    }

    private void handleUserInput() {
        String message = userInputField.getText().trim();

        if (message.isEmpty()) {
            addMessage("❗ Veuillez écrire une question.", true);
            return;
        }

        addMessage("Vous : " + message, true);
        userInputField.clear();

        Label responseLabel = addStreamingPlaceholder("Assistant : Réflexion en cours...");

        new Thread(() -> {
            try {
                String response = openAiService.askOpenAi(message);
                StringBuilder displayedText = new StringBuilder("Assistant : ");
                for (char c : response.toCharArray()) {
                    Thread.sleep(20); // effet de frappe
                    displayedText.append(c);
                    String currentText = displayedText.toString();
                    Platform.runLater(() -> responseLabel.setText(currentText));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> responseLabel.setText("⚠️ Une erreur est survenue. Veuillez réessayer."));
            }
        }).start();
    }

    private void addMessage(String message, boolean isUser) {
        Label label = createStyledLabel(message, isUser);
        HBox container = wrapLabelInHBox(label, isUser);
        chatVBox.getChildren().add(container);
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    private Label addStreamingPlaceholder(String initialText) {
        Label label = createStyledLabel(initialText, false);
        HBox container = wrapLabelInHBox(label, false);
        chatVBox.getChildren().add(container);
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        return label;
    }

    private Label createStyledLabel(String text, boolean isUser) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(600);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.setPadding(new Insets(10));
        label.setStyle(isUser
                ? "-fx-background-color: #D0E8FF; -fx-background-radius: 10; -fx-text-fill: black;"
                : "-fx-background-color: #EAEAEA; -fx-background-radius: 10; -fx-text-fill: black;");
        return label;
    }

    private HBox wrapLabelInHBox(Label label, boolean isUser) {
        HBox hbox = new HBox(label);
        hbox.setPadding(new Insets(5));
        hbox.setNodeOrientation(isUser
                ? NodeOrientation.RIGHT_TO_LEFT
                : NodeOrientation.LEFT_TO_RIGHT);
        return hbox;
    }
}
