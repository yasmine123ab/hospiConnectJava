package org.hospiconnect.controller.dons;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.DonService;
import org.hospiconnect.utils.DatabaseUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModifyDon {
    @FXML
    private TextField typeTF;
    @FXML
    private TextField montantTF;
    @FXML
    private TextField descriptionTF;
    @FXML
    private DatePicker dateDonDP;
    @FXML
    private ComboBox<User> donateurComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button annulerButton;
    @FXML
    private CheckBox disponibilite1CheckBox;
    @FXML
    private CheckBox disponibilite2CheckBox;

    @FXML
    private Button menuHomeButton;

    private final DonService donService = new DonService();
    private Dons donToModify;



    // Cette méthode sera appelée depuis la fenêtre principale (ShowDon) pour initialiser la page de modification
    public void initialize(Dons don) {

        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));

        donToModify = don;
        disponibilite1CheckBox.setOnAction(event -> {
            if (disponibilite1CheckBox.isSelected()) {
                disponibilite2CheckBox.setSelected(false);
            }
        });

        disponibilite2CheckBox.setOnAction(event -> {
            if (disponibilite2CheckBox.isSelected()) {
                disponibilite1CheckBox.setSelected(false);
            }
        });

        // Remplissage des champs avec les valeurs du don existant
        typeTF.setText(don.getTypeDon());
        montantTF.setText(String.valueOf(don.getMontant()));
        descriptionTF.setText(don.getDescription());

        if (don.getDateDon() != null) {
            dateDonDP.setValue(don.getDateDon().toLocalDate());
        }

        try {
            ObservableList<User> donateurs = FXCollections.observableArrayList(loadUsersFromDB());

            // Si la liste est vide, affichons un message dans la console
            if (donateurs.isEmpty()) {
                System.out.println("Aucun donateur trouvé.");
            }

            donateurComboBox.setItems(donateurs);

            // Affichage des noms et prénoms dans le ComboBox
            donateurComboBox.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });

            donateurComboBox.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des donateurs : " + e.getMessage());
        }
    }
    private List<User> loadUsersFromDB() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, nom, prenom FROM user";
        Connection conn = DatabaseUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            users.add(u);
        }

        return users;
    }

    @FXML
    public void handleSave() {
        try {
            // Validation du montant
            String montantText = montantTF.getText();
            if (montantText.isEmpty()) {
                showErrorAlert("Erreur", "Le montant ne peut pas être vide.");
                return;
            }
            double montant;
            try {
                montant = Double.parseDouble(montantText);
                if (montant < 0) {
                    showErrorAlert("Erreur", "Le montant doit être positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Erreur", "Le montant doit être un nombre valide.");
                return;
            }

            // Vérification du type de don
            String typeDon = typeTF.getText();
            if (typeDon.isEmpty()) {
                showErrorAlert("Erreur", "Le type de don ne peut pas être vide.");
                return;
            }

            // Vérification de la description
            String description = descriptionTF.getText();
            if (description.isEmpty()) {
                showErrorAlert("Erreur", "La description ne peut pas être vide.");
                return;
            }

            // Vérification de la date de don
            if (dateDonDP.getValue() == null) {
                showErrorAlert("Erreur", "La date de don doit être sélectionnée.");
                return;
            }
            if (dateDonDP.getValue().isBefore(java.time.LocalDate.now())) {
                showErrorAlert("Erreur", "La date de don ne peut pas être dans le passé.");
                return;
            }

            // Vérification du donateur
            if (donateurComboBox.getValue() == null) {
                showErrorAlert("Erreur", "Un donateur doit être sélectionné.");
                return;
            }

            // 👉 MAJ DE L'OBJET donToModify
            donToModify.setMontant(montant);
            donToModify.setTypeDon(typeDon);
            donToModify.setDescription(description);
            donToModify.setDateDon(java.sql.Date.valueOf(dateDonDP.getValue()));
            donToModify.setDonateurId(donateurComboBox.getValue().getId());

            // ➡ Gestion de la disponibilité selon les cases cochées
            boolean disponibilite;
            if (disponibilite1CheckBox.isSelected()) {
                disponibilite = true;
            } else if (disponibilite2CheckBox.isSelected()) {
                disponibilite = false;
            } else {
                showErrorAlert("Erreur", "Veuillez sélectionner la disponibilité.");
                return;
            }
            donToModify.setDisponibilite(disponibilite);

            // Appeler la méthode de mise à jour
            donService.update(donToModify);

            showSuccessAlert("Succès", "Le don a été modifié avec succès.");

            // 👉 Redirection vers la page ShowDon.fxml
            SceneUtils.openNewScene("/Dons/ShowDon.fxml", saveButton.getScene(), null);

        } catch (NumberFormatException e) {
            showErrorAlert("Erreur de saisie", "Le montant doit être un nombre valide.");
        } catch (SQLException e) {
            showErrorAlert("Erreur de modification", "Erreur lors de la mise à jour du don : " + e.getMessage());
        }
    }
    @FXML
    public void handleCancel() {
        if (saveButton != null) {
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close(); // Ferme la fenêtre
        } else {
            System.out.println("saveButton est null !"); // Pour vérifier
        }
    }
    @FXML
    public void handleFaireUnDonClick(ActionEvent event) {
        openScene("/Dons/ShowDon.fxml", event);
    }

    @FXML
    public void handleFaireUneDemandeClick(ActionEvent event) {
        openScene("/Demandes/ShowDemande.fxml", event);
    }

    @FXML
    public void handleFaireUneAttributionClick(ActionEvent event) {
        openScene("/Attributions/ShowAttribution.fxml", event);
    }
    private void openScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour afficher un message d'alerte de succès
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher un message d'alerte d'erreur
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}