package org.hospiconnect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HospiConnectMain extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        var file= getClass().getResource("/login.fxml");
        Parent root = FXMLLoader.load(file);
        primaryStage.setScene(new Scene(root));
        //set stage borderless
        primaryStage.initStyle(StageStyle.UNDECORATED);

        HospiConnectMain.primaryStage = primaryStage;
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.setProperty("prism.maxvram", "2G");
        launch(args);
    }
}