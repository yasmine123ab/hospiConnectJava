package org.hospiconnect.controller.User;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import javafx.scene.control.TextArea;

public class VoiceRecognition {

    public static void recognizeSpeech(TextArea textArea) {
        String apiKey = "1eCsjj8QtNgjjGA2YvOdgr95EDMOAlRMeqiWNwvyCbhDtMuZ8MazJQQJ99BDACYeBjFXJ3w3AAAYACOGpY8Y";
        String region = "eastus";

        try {
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(apiKey, region);
            speechConfig.setSpeechRecognitionLanguage("fr-FR");

            AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();

            try (SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig)) {
                System.out.println("Parlez maintenant...");

                // Ajouter le texte reconnu au TextArea
                recognizer.recognized.addEventListener((s, e) -> {
                    if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                        String recognizedText = e.getResult().getText();
                        // Mettre à jour le TextArea sur le thread JavaFX
                        javafx.application.Platform.runLater(() -> {
                            textArea.appendText(recognizedText + " ");
                        });
                    }
                });

                // Démarrer la reconnaissance
                recognizer.startContinuousRecognitionAsync().get();

                Thread.sleep(10000);

                recognizer.stopContinuousRecognitionAsync().get();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                textArea.appendText("\nErreur de reconnaissance vocale: " + ex.getMessage());
            });
        }
    }
}