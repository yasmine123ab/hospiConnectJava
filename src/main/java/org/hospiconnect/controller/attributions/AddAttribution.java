package org.hospiconnect.controller.attributions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.hospiconnect.model.AttributionsDons;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.User;
import org.hospiconnect.service.AttributionDonService;
import org.hospiconnect.utils.DatabaseUtils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAttribution {

    @FXML private ComboBox<Dons> donComboBox;
    @FXML private ComboBox<DemandesDons> demandeComboBox;
    @FXML private ComboBox<User> beneficiaireComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Button revenirButton;

    private final AttributionDonService attributionService = new AttributionDonService();
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
    public void handleRevenir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attributions/ShowAttribution.fxml"));
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



    @FXML
    public void initialize() {
        try {
            ObservableList<Dons> dons = FXCollections.observableArrayList(loadDonsFromDB());
            ObservableList<DemandesDons> demandes = FXCollections.observableArrayList(loadDemandesFromDB());
            ObservableList<User> beneficiaires = FXCollections.observableArrayList(loadBeneficiairesFromDB());

            donComboBox.setItems(dons);
            demandeComboBox.setItems(demandes);
            beneficiaireComboBox.setItems(beneficiaires);

            configureComboBox(donComboBox, don -> don.getTypeDon());
            configureComboBox(demandeComboBox, demande -> demande.getTypeBesoin());
            configureComboBox(beneficiaireComboBox, u -> u.getNom() + " " + u.getPrenom());

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur de chargement des données : " + e.getMessage());
        }
    }

    private <T> void configureComboBox(ComboBox<T> comboBox, java.util.function.Function<T, String> toStringFunction) {
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : toStringFunction.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null; // Non utilisé
            }
        });

        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toStringFunction.apply(item));
            }
        });
    }

    private List<Dons> loadDonsFromDB() throws SQLException {
        List<Dons> dons = new ArrayList<>();
        String sql = "SELECT * FROM dons";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dons don = new Dons();
                don.setId(rs.getInt("id"));
                don.setTypeDon(rs.getString("type_don"));
                dons.add(don);
            }
        }
        return dons;
    }

    private List<DemandesDons> loadDemandesFromDB() throws SQLException {
        List<DemandesDons> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_dons";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DemandesDons demande = new DemandesDons();
                demande.setId(rs.getInt("id"));
                demande.setTypeBesoin(rs.getString("type_besoin"));
                demandes.add(demande);
            }
        }
        return demandes;
    }

    private List<User> loadBeneficiairesFromDB() throws SQLException {
        List<User> beneficiaires = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                beneficiaires.add(u);
            }
        }
        return beneficiaires;
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        ajouterAttribution();
    }

    private void ajouterAttribution() {
        try {
            String statut = statutComboBox.getValue();
            Dons selectedDon = donComboBox.getValue();
            DemandesDons selectedDemande = demandeComboBox.getValue();
            User selectedBeneficiaire = beneficiaireComboBox.getValue();

            // Contrôle du champ statut
            if (statut == null || statut.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Veuillez sélectionner un statut.");
                return;
            }

            // Vérifier si le statut est parmi une liste valide (si applicable)
            List<String> statutsValides = Arrays.asList("Attribué", "En attente", "Refusé");
            if (!statutsValides.contains(statut)) {
                showAlert(Alert.AlertType.WARNING, "Statut invalide", "Le statut sélectionné n'est pas valide.");
                return;
            }

            // Contrôle de sélection des objets liés
            if (selectedDon == null) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Veuillez sélectionner un don.");
                return;
            }

            if (selectedDemande == null) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Veuillez sélectionner une demande.");
                return;
            }

            if (selectedBeneficiaire == null) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Veuillez sélectionner un bénéficiaire.");
                return;
            }

            AttributionsDons attribution = new AttributionsDons();
            attribution.setDateAttribution(new Date(System.currentTimeMillis()));
            attribution.setStatut(statut);
            attribution.setDon(selectedDon);
            attribution.setDemande(selectedDemande);
            attribution.setBeneficiaire(selectedBeneficiaire);


            attributionService.insert(attribution);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Attribution enregistrée avec succès.");
//            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur inattendue", e.getMessage());
        }
    }

//    private void clearFields() {
//        statutTF.clear();
//        donComboBox.getSelectionModel().clearSelection();
//        demandeComboBox.getSelectionModel().clearSelection();
//        beneficiaireComboBox.getSelectionModel().clearSelection();
//    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
