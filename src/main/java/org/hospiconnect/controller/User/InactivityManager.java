package org.hospiconnect.controller.User;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InactivityManager {

    private static PauseTransition pause;

    public static void applyInactivityListener(Scene scene, Stage stage) {
        pause = new PauseTransition(Duration.minutes(1));

        pause.setOnFinished(event -> {
            try {
                // Redirection vers login.fxml
                Parent root = FXMLLoader.load(InactivityManager.class.getResource("/login.fxml"));
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        EventHandler<javafx.event.Event> resetTimer = event -> {
            pause.stop();
            pause.play(); // Restart the timer on any activity
        };

        scene.addEventFilter(MouseEvent.ANY, resetTimer);
        scene.addEventFilter(KeyEvent.ANY, resetTimer);

        pause.play(); // Start the timer
    }
}
