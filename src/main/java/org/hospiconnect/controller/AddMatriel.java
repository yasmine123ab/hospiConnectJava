package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import kotlin.text.UStringsKt;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Materiel;
import org.hospiconnect.service.MaterielService1;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;


public class AddMatriel {
    @FXML
    private TextField nom;

    @FXML
    private TextField categorie;

    @FXML
    private ComboBox<String> etat;

    @FXML
    private TextField quantite;

    @FXML
    private TextField emplacement;


    @FXML
    private DatePicker date;

    @FXML
    private Button retourner;
    private final ObservableList<String> etats = FXCollections.observableArrayList(
            "Neuf", "Usagé", "En réparation"
    );


    public void initialize() {

        retourner.setOnAction(e -> SceneUtils.openNewScene(
                "/ListMateriel.fxml", retourner.getScene(), null));
    }

    @FXML
    void ajouter(ActionEvent event) {
        // Vérification que tous les champs sont remplis
        if (nom.getText().isEmpty()) {
            showErrorAlert("Le champ 'Nom' est obligatoire.");
            return;
        }
        if (categorie.getText().isEmpty()) {
            showErrorAlert("Le champ 'Catégorie' est obligatoire.");
            return;
        }
        if (etat.getValue() == null) {
            showErrorAlert("Le champ 'etat' est obligatoire.");
            return ;
        }


        if (quantite.getText().isEmpty()) {
            showErrorAlert("Le champ 'Quantité' est obligatoire.");
            return;
        }
        if (emplacement.getText().isEmpty()) {
            showErrorAlert("Le champ 'Emplacement' est obligatoire.");
            return;
        }
        if (date.getValue() == null) {
            showErrorAlert("Le champ 'Date' est obligatoire.");
            return;
        }

        // Vérification que la quantité est un entier positif
        int qte;
        try {
            qte = Integer.parseInt(quantite.getText());
            if (qte < 0) {
                throw new NumberFormatException("Quantité négative");
            }
        } catch (NumberFormatException e) {
            showErrorAlert("La quantité doit être un entier positif.");
            return;
        }
        // Vérification de la date
        LocalDate selectedDate = date.getValue();
        LocalDate today = LocalDate.now();
        if (selectedDate.isAfter(today)) {
            showErrorAlert("La date doit être inférieure ou égale à la date d'aujourd'hui.");
            return;
        }

        // Création du matériel
        MaterielService1 us = new MaterielService1();
        Materiel m = new Materiel(
                qte,
                nom.getText(),
                categorie.getText(),
                etat.getValue(),
                emplacement.getText(),
                java.sql.Date.valueOf(date.getValue()));

        try {
            us.insert(m);

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Matériel ajouté avec succès !");
            alert.showAndWait();

            // Réinitialisation des champs
            quantite.clear();
            nom.clear();
            categorie.clear();
            etat.getSelectionModel().selectFirst();
            emplacement.clear();
            date.setValue(null);

        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du matériel : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de l'ajout.");
            alert.showAndWait();
        }
    }

    // Méthode pour afficher une alerte d'erreur avec un message personnalisé
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Materiel materielEnCours;

    public void setMateriel(Materiel m) {
        // Pré-remplir les champs ici
        this.materielEnCours = m;
        nom.setText(m.getNom());
        quantite.setText(String.valueOf(m.getQuantite()));
        categorie.setText(m.getCategorie());
        etat.setValue(m.getEtat());
        emplacement.setText(m.getEmplacement());
        date.setValue(LocalDate.now());

        // Validation des champs

        // Vérification du nom
        if (m.getNom() == null || m.getNom().isEmpty()) {
            showErrorAlert("Le nom du matériel est obligatoire.");
            return;
        }

        // Vérification de la catégorie
        if (m.getCategorie() == null || m.getCategorie().isEmpty()) {
            showErrorAlert("La catégorie du matériel est obligatoire.");
            return;
        }

        // Vérification de l'état
        if (m.getEtat() == null || m.getEtat().isEmpty()) {
            showErrorAlert("L'état du matériel est obligatoire.");
            return;
        }

        // Vérification de la quantité
        if (m.getQuantite() < 0) {
            showErrorAlert("La quantité du matériel ne peut pas être négative.");
            return;
        }

        // Vérification de l'emplacement
        if (m.getEmplacement() == null || m.getEmplacement().isEmpty()) {
            showErrorAlert("L'emplacement du matériel est obligatoire.");
            return;
        }

        // Vérification de la date
        LocalDate today = LocalDate.now();
        LocalDate selectedDate = date.getValue();
        if (selectedDate.isAfter(today)) {
            showErrorAlert("La date doit être inférieure ou égale à la date d'aujourd'hui.");
            return;
        }

        // Si tous les champs sont valides, procéder avec l'édition du matériel
        // ... (votre logique pour mettre à jour le matériel)
    }




}