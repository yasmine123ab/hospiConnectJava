package org.hospiconnect.controller.dons;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.user;
import org.hospiconnect.service.DonService;
import org.hospiconnect.utils.DatabaseUtils;

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
    private ComboBox<user> donateurComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button annulerButton;

    private final DonService donService = new DonService();
    private Dons donToModify;



    // Cette méthode sera appelée depuis la fenêtre principale (ShowDon) pour initialiser la page de modification
    public void initialize(Dons don) {
        donToModify = don;

        // Remplissage des champs avec les valeurs du don existant
        typeTF.setText(don.getTypeDon());
        montantTF.setText(String.valueOf(don.getMontant()));
        descriptionTF.setText(don.getDescription());

        if (don.getDateDon() != null) {
            dateDonDP.setValue(don.getDateDon().toLocalDate());
        }

        try {
            ObservableList<user> donateurs = FXCollections.observableArrayList(loadUsersFromDB());

            // Si la liste est vide, affichons un message dans la console
            if (donateurs.isEmpty()) {
                System.out.println("Aucun donateur trouvé.");
            }

            donateurComboBox.setItems(donateurs);

            // Affichage des noms et prénoms dans le ComboBox
            donateurComboBox.setCellFactory(param -> new ListCell<user>() {
                @Override
                protected void updateItem(user user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });

            donateurComboBox.setButtonCell(new ListCell<user>() {
                @Override
                protected void updateItem(user user, boolean empty) {
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
    private List<user> loadUsersFromDB() throws SQLException {
        List<user> users = new ArrayList<>();
        String sql = "SELECT id, nom, prenom FROM user";
        Connection conn = DatabaseUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            user u = new user();
            u.setId(rs.getInt("id")); // <-- ESSENTIEL !
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
                return; // Arrêter l'exécution si le montant est vide
            }
            double montant;
            try {
                montant = Double.parseDouble(montantText);
                if (montant <= 0) {
                    showErrorAlert("Erreur", "Le montant doit être positif.");
                    return; // Arrêter l'exécution si le montant est invalide
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Erreur", "Le montant doit être un nombre valide.");
                return; // Arrêter l'exécution si la conversion échoue
            }

// Vérification du type de don
            String typeDon = typeTF.getText();
            if (typeDon.isEmpty()) {
                showErrorAlert("Erreur", "Le type de don ne peut pas être vide.");
                return; // Arrêter l'exécution si le type de don est vide
            }

// Vérification de la description
            String description = descriptionTF.getText();
            if (description.isEmpty()) {
                showErrorAlert("Erreur", "La description ne peut pas être vide.");
                return; // Arrêter l'exécution si la description est vide
            }

// Vérification de la date de don
            if (dateDonDP.getValue() == null) {
                showErrorAlert("Erreur", "La date de don doit être sélectionnée.");
                return; // Arrêter l'exécution si la date n'est pas sélectionnée
            }
            if (dateDonDP.getValue().isBefore(java.time.LocalDate.now())) {
                showErrorAlert("Erreur", "La date de don ne peut pas être dans le passé.");
                return;
            }

    // Vérification du donateur
            if (donateurComboBox.getValue() == null) {
                showErrorAlert("Erreur", "Un donateur doit être sélectionné.");
                return; // Arrêter l'exécution si aucun donateur n'est sélectionné
            }

            // Appeler la méthode de mise à jour
            donService.update(donToModify);

            showSuccessAlert("Succès", "Le don a été modifié avec succès.");

            // Fermer la fenêtre
            ((Stage) saveButton.getScene().getWindow()).close();

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

