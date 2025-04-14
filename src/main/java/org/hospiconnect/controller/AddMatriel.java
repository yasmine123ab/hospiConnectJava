package org.hospiconnect.controller;

import org.hospiconnect.model.Materiel;
import org.hospiconnect.service.MaterielService1;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;



public class AddMatriel {
    @FXML
    private TextField nom;

    @FXML
    private TextField categorie;

    @FXML
    private TextField etat;

    @FXML
    private TextField quantite;

    @FXML
    private TextField emplacement;

    @FXML
    private DatePicker date;
    @FXML
    void ajouter(ActionEvent event){
        MaterielService1 us = new MaterielService1();
        Materiel m = new Materiel(
                Integer.parseInt(quantite.getText()),
                nom.getText(),
                categorie.getText(),
                etat.getText(),
                emplacement.getText(),
                java.sql.Date.valueOf(date.getValue()));

        quantite.clear();
        nom.clear();
        categorie.clear();
        etat.clear();
        emplacement.clear();
        date.setValue(null);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText("Ajout un usuario");
        alert.showAndWait();
        try {
            us.insert(m);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }


}
