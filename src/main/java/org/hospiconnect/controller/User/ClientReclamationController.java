package org.hospiconnect.controller.User;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;


import org.hospiconnect.model.Reclamation;
import org.hospiconnect.service.ReclamationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

public class ClientReclamationController {

    @FXML private Label lblNomClient, lblEmailClient, lblTelephoneClient;
    @FXML private ComboBox<String> cbSujet, cbUrgence; // nom corrigé
    @FXML private TextArea taDescription;
    @FXML private Label lblFileName;

    private File selectedFile;
    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        initComboBoxes();
        loadClientInfo();
        lblFileName.setText("Aucun fichier sélectionné");
    }

    private void initComboBoxes() {
        cbSujet.setItems(FXCollections.observableArrayList(
                "Problème de facturation",
                "Service insatisfaisant",
                "Réclamation médicale",
                "Autre problème"
        ));

        cbUrgence.setItems(FXCollections.observableArrayList(
                "Normal",
                "Urgent",
                "Critique"
        ));
        cbUrgence.getSelectionModel().selectFirst();
    }

    private void loadClientInfo() {
        lblNomClient.setText("Jean Dupont");
        lblEmailClient.setText("jean.dupont@example.com");
        lblTelephoneClient.setText("+33 6 12 34 56 78");
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        try {
            Reclamation reclamation = createReclamation();
            reclamationService.addReclamation(reclamation);
            showSuccessAndReset(reclamation);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'enregistrement: " + e.getMessage());
        }
    }

    private Reclamation createReclamation() {
        Reclamation r = new Reclamation();
        r.setTitle(cbSujet.getValue());
        r.setDescription(taDescription.getText());
        r.setDateReclamation(new Date());
        r.setStatus("En cours");
        r.setCategory(cbSujet.getValue());
        r.setPriority(cbUrgence.getValue());
        r.setAttachment(getAttachmentPath());
        return r;
    }


    public void handleBrowseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une pièce jointe");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            lblFileName.setText(selectedFile.getName());
        } else {
            lblFileName.setText("Aucun fichier sélectionné");
        }
    }

    private String getAttachmentPath() {
        if (selectedFile == null) return null;

        File destDir = new File("uploads/");
        if (!destDir.exists()) destDir.mkdirs();

        File destFile = new File(destDir, selectedFile.getName());

        try {
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        resetForm();
    }

    private void resetForm() {
        cbSujet.setValue(null);
        taDescription.clear();
        cbUrgence.getSelectionModel().selectFirst();
        lblFileName.setText("Aucun fichier sélectionné");
        selectedFile = null;
    }

    private void showSuccessAndReset(Reclamation r) {
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation soumise avec succès !");
        resetForm();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private int getCurrentUserId() {
        // Simulé pour l'instant
        return 1;
    }

}
