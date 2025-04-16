package org.hospiconnect.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.hospiconnect.model.MouvementMaterielJoint;
import org.hospiconnect.model.mouvement_stock;
import org.hospiconnect.service.mouvementService;

import java.sql.Date;
import java.time.LocalDate;

public class AddMouvement {

    @FXML
    private DatePicker date_mouvement;

    @FXML
    private TextField id_materiel;

    @FXML
    private TextField id_personnel;

    @FXML
    private TextField motif;

    @FXML
    private TextField qunatite;

    @FXML
    private TextField type_mouvement;

    // Méthode d'ajout d'un mouvement avec contrôle de saisie
    @FXML
    void ajouterMouvement(ActionEvent event) {
        if (!validateForm()) {
            return;  // Arrêter l'exécution si la validation échoue
        }

        try {
            // Créer un objet mouvement_stock à partir des champs
            mouvement_stock m = new mouvement_stock();
            m.setId_materiel_id(Integer.parseInt(id_materiel.getText()));
            m.setId_personnel_id(Integer.parseInt(id_personnel.getText()));
            m.setQunatite(Integer.parseInt(qunatite.getText()));
            m.setDate_mouvement(Date.valueOf(date_mouvement.getValue()));
            m.setMotif(motif.getText());
            m.setType_mouvement(type_mouvement.getText());

            // Appeler le service pour insérer le mouvement
            mouvementService service = new mouvementService();
            service.insert(m);

            // Affichage d'une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("✅ Mouvement ajouté avec succès !");
            alert.showAndWait();

            // Réinitialiser les champs
            id_materiel.clear();
            id_personnel.clear();
            qunatite.clear();
            motif.clear();
            type_mouvement.clear();
            date_mouvement.setValue(null);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ajouter le mouvement");
            alert.setContentText("❌ Vérifiez vos données ou contactez l'administrateur.");
            alert.showAndWait();
        }
    }

    // Validation des champs
    private boolean validateForm() {
        // Vérification de l'ID matériel
        if (id_materiel.getText().isEmpty()) {
            showErrorAlert("L'ID du matériel est obligatoire.");
            return false;
        }

        // Vérification de l'ID personnel
        if (id_personnel.getText().isEmpty()) {
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

    public void setMouvementExistant(MouvementMaterielJoint m) {
        this.mouvementExistant = m;

        // Remplir les champs avec les données du mouvement à modifier
        id_materiel.setText(String.valueOf(m.getNomMateriel()));
        id_personnel.setText(String.valueOf(m.getNomPersonnel()));
        qunatite.setText(String.valueOf(m.getQuantite()));
        motif.setText(m.getMotif());
        type_mouvement.setText(m.getTypeMouvement());
        date_mouvement.setValue(LocalDate.now());
    }
}
