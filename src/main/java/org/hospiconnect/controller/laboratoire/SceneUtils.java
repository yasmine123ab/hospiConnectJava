package org.hospiconnect.controller.laboratoire;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneUtils {

    private static Object context;

    public static void openNewScene(String fxmlFile, Scene scene, Object context) {
        try {
            // Load the FXML file for the new scene
            SceneUtils.context = context;

            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlFile));
            Parent root = loader.load();

            Scene newScene = new Scene(root);

            Stage stage = (Stage) scene.getWindow();
            // Set the new scene on the existing stage
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            SceneUtils.context = null;
        }
    }

    public static Object getContext() {
        return context;
    }
}

