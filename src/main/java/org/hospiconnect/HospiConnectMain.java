package org.hospiconnect;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HospiConnectMain extends Application {
    public static Stage primaryStage;

    private static HostServices appHostServices;

    public static HostServices getAppHostServices() {
        return appHostServices;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        appHostServices = getHostServices();

        var file= getClass().getResource("/views/signup.fxml");
        Parent root = FXMLLoader.load(file);
        primaryStage.setScene(new Scene(root));
        //set stage borderless
        //primaryStage.initStyle(StageStyle.UNDECORATED);

        HospiConnectMain.primaryStage = primaryStage;
        primaryStage.show();
    }


    public static void main(String[] args) {
        //System.setProperty("prism.maxvram", "5G");
        //System.setProperty("prism.order", "sw");
        launch(args);
    }
}