package org.hospiconnect.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.MouvementMaterielJoint;
import org.hospiconnect.model.mouvement_stock;
import org.hospiconnect.service.mouvementService;

import java.sql.Date;
import java.time.LocalDate;

public class AddMouvement {

    @FXML
    private DatePicker date_mouvement;

    @FXML
    private TextField id_matriel;

    @FXML
    private TextField id_personnelll;

    @FXML
    private TextField motif;

    @FXML
    private TextField qunatite;

    @FXML
    private TextField type_mouvement;
    @FXML
    private Button retourner;
    @FXML
    private Button listeMateriels;

    @FXML
    private Button listeMouvement;

    @FXML
    private Button menuDashboardButton;

    @FXML
    private Button menuHomeButton;
    public void initialize() {
        retourner.setOnAction(e -> SceneUtils.openNewScene(
                "/ListMouvement.fxml", retourner.getScene(), null));
        listeMateriels.setOnAction(e -> SceneUtils.openNewScene(
                "/ListMateriel.fxml", listeMateriels.getScene(), null));
        listeMouvement.setOnAction(e -> SceneUtils.openNewScene(
                "/ListMouvement.fxml", listeMouvement.getScene(), null));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "//laboratoireBack/dashboardLabo.fxml", menuDashboardButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));

    }

    // Méthode d'ajout d'un mouvement avec contrôle de saisie

    @FXML
    void ajouterMouvement(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            mouvement_stock m = new mouvement_stock();
            m.setId_materiel_id(Integer.parseInt(id_matriel.getText()));
            m.setId_personnel_id(Integer.parseInt(id_personnelll.getText()));
            m.setQunatite(Integer.parseInt(qunatite.getText()));
            m.setDate_mouvement(Date.valueOf(date_mouvement.getValue()));
            m.setMotif(motif.getText());
            m.setType_mouvement(type_mouvement.getText());

            mouvementService service = new mouvementService();

            if (mouvementModifie != null) {
                m.setId(mouvementModifie.getId()); // essentiel pour l’UPDATE
                service.update(m);
                showSuccess("✅ Mouvement modifié avec succès !");
            } else {
                service.insert(m);
                showSuccess("✅ Mouvement ajouté avec succès !");
            }

            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
            showError("❌ Vérifiez vos données ou contactez l'administrateur.");
        }
    }


    // Validation des champs
    private boolean validateForm() {
        // Vérification de l'ID matériel
        if (id_matriel.getText().isEmpty()) {
            showErrorAlert("L'ID du matériel est obligatoire.");
            return false;
        }

        // Vérification de l'ID personnel
        if (id_personnelll.getText().isEmpty()) {
            showErrorAlert("L'ID du personnel est obligatoire.");
            return false;
        }

        // Vérification de la quantité
        if (qunatite.getText().isEmpty() || Integer.parseInt(qunatite.getText()) <= 0) {
            showErrorAlert("La quantité doit être un nombre positif.");
            return false;
        }

        // Vérification de la date du mouvement
        if (date_mouvement.getValue() == null || date_mouvement.getValue().isAfter(LocalDate.now())) {
            showErrorAlert("La date du mouvement doit être inférieure ou égale à la date d'aujourd'hui.");
            return false;
        }

        // Vérification du motif
        if (motif.getText().isEmpty()) {
            showErrorAlert("Le motif est obligatoire.");
            return false;
        }

        // Vérification du type de mouvement
        if (type_mouvement.getText().isEmpty()) {
            showErrorAlert("Le type de mouvement est obligatoire.");
            return false;
        }

        return true;
    }

    // Affichage d'une alerte d'erreur
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Validation échouée");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private MouvementMaterielJoint mouvementExistant;

    private mouvement_stock mouvementModifie = null;
    public void setMouvementExistant(MouvementMaterielJoint m) {
        mouvementModifie = new mouvement_stock();
        mouvementModifie.setId(m.getId()); // Pour le WHERE id=?

        id_matriel.setText(String.valueOf(m.getNomMateriel())); // met bien l’ID, pas le nom
        id_personnelll.setText(String.valueOf(m.getNomPersonnel()));
        qunatite.setText(String.valueOf(m.getQuantite()));
        motif.setText(m.getMotif());
        type_mouvement.setText(m.getTypeMouvement());
        date_mouvement.setValue(LocalDate.now());
    }
    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur lors de l'enregistrement");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void resetForm() {
        id_matriel.clear();
        id_personnelll.clear();
        qunatite.clear();
        motif.clear();
        type_mouvement.clear();
        date_mouvement.setValue(null);
        mouvementModifie = null;
    }

}