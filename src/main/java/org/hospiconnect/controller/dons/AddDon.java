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
import javafx.util.StringConverter;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.DonService;
import org.hospiconnect.utils.DatabaseUtils;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class AddDon {

    @FXML
    private TextField typeTF;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField montantTF;

    @FXML
    private DatePicker dateDonDP;

    @FXML
    private ComboBox<User> donateurComboBox;
    @FXML
    private Button revenirButton;

    private DonService donService;

    public AddDon() {
        donService = new DonService();
    }
    @FXML
    public void handleFaireUnDonClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/ShowDon.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleFaireUneDemandeClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demandes/ShowDemande.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleFaireUneAttributionClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attributions/ShowAttribution.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize() {
        try {
            ObservableList<User> donateurs = FXCollections.observableArrayList(loadUsersFromDB());

            if (donateurs.isEmpty()) {
                System.out.println("Aucun donateur trouvé.");
            }

            donateurComboBox.setItems(donateurs);

            // Affichage des noms et prénoms dans la liste déroulante
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

            // Affichage du nom sélectionné dans le bouton
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

            // Ajout du converter pour bien lier texte <-> objet
            donateurComboBox.setConverter(new StringConverter<User>() {
                @Override
                public String toString(User user) {
                    if (user == null) return "";
                    return user.getNom() + " " + user.getPrenom();
                }

                @Override
                public User fromString(String string) {
                    return null; // Non utilisé car la sélection se fait via la liste
                }
            });

        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des donateurs : " + e.getMessage());
        }
    }
    @FXML
    public void handleRevenir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/ShowDon.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir du bouton
            Stage stage = (Stage) revenirButton.getScene().getWindow();

            // Remplacer le contenu de la scène avec la page ShowDon
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
            u.setId(rs.getInt("id")); // <-- ESSENTIEL !
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            users.add(u);
        }

        return users;
    }


    @FXML
    private void handleSubmit(ActionEvent event) {
        AjouterDon();
    }

    private void AjouterDon() {
        try {
            String type = typeTF.getText().trim();
            String description = descriptionTF.getText().trim();
            String montantStr = montantTF.getText().trim();
            LocalDate selectedDate = dateDonDP.getValue();

            // Vérifications
            if (type.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Le type de don est obligatoire.");
                return;
            }

            if (description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "La description est obligatoire.");
                return;
            }


            double montant;
            try {
                montant = Double.parseDouble(montantStr);
                if (montant <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Montant invalide", "Le montant doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Montant invalide", "Veuillez entrer un nombre valide pour le montant.");
                return;
            }

            if (selectedDate == null) {
                showAlert(Alert.AlertType.WARNING, "Date manquante", "Veuillez choisir une date.");
                return;
            }

            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.WARNING, "Date invalide", "La date ne peut pas être dans le passé.");
                return;
            }

            User selectedDonateur = donateurComboBox.getValue();
            if (selectedDonateur == null) {
                showAlert(Alert.AlertType.WARNING, "Donateur manquant", "Veuillez sélectionner un donateur.");
                return;
            }

            // Construction du don après validation
            Dons don = new Dons();
            don.setTypeDon(type);
            don.setDescription(description);
            don.setMontant(montant);
            don.setDateDon(Date.valueOf(selectedDate));
            don.setDonateurId(selectedDonateur.getId());
            don.setDisponibilite(true); // Par défaut

            donService.insert(don);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Don ajouté avec succès !");
            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        typeTF.clear();
        montantTF.clear();
        descriptionTF.clear();
        dateDonDP.setValue(null);
        donateurComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void AfficherDons() {
        try {
            for (Dons don : donService.findAll()) {
                System.out.println(don);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des dons : " + e.getMessage());
        }
    }
}
