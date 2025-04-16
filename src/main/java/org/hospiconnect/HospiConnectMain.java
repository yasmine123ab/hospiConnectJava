package org.hospiconnect;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class HospiConnectMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println( "Hello World!");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ListMateriel.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Ajouter materiel");
        stage.show();

    }
    public static void main(String[] args) {
        launch(args);

    }
}