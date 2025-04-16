package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import org.hospiconnect.model.RendezVous;
import org.hospiconnect.service.RendezVousService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListeRendezVousController implements Initializable {

    @FXML private TableView<RendezVous> tableRendezVous;
    @FXML private TableColumn<RendezVous, String> colNom;
    @FXML private TableColumn<RendezVous, String> colPrenom;
    @FXML private TableColumn<RendezVous, String> colTel;
    @FXML private TableColumn<RendezVous, String> colEmail;
    @FXML private TableColumn<RendezVous, String> colDate;
    @FXML private TableColumn<RendezVous, String> colHeure;
    @FXML private TableColumn<RendezVous, String> colType;
    @FXML private TableColumn<RendezVous, String> colGravite;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, String> colCommentaire;

    private ObservableList<RendezVous> data = FXCollections.observableArrayList();
    private RendezVousService rendezVousService = new RendezVousService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Custom cellValueFactory for LocalDate to String conversion
        colDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        // Custom cellValueFactory for LocalTime to String conversion
        colHeure.setCellValueFactory(cellData -> {
            LocalTime heure = cellData.getValue().getHeure();
            return new SimpleStringProperty(heure != null ? heure.toString() : "");
        });

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colGravite.setCellValueFactory(new PropertyValueFactory<>("gravite"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

        chargerDonnees();
    }

    private void chargerDonnees() {
        try {
            data.clear();
            data.addAll(rendezVousService.findAll());
            tableRendezVous.setItems(data);
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Recherche par type exact (ex : "Opération")
    public void filtrerParType(String typeRecherche) {
        List<RendezVous> filtrés = data.stream()
                .filter(rdv -> rdv.getType().equalsIgnoreCase(typeRecherche))
                .collect(Collectors.toList());
        rafraichirTable(filtrés);
    }

    // Recherche partielle dans nom ou prénom
    public void rechercherParNomOuPrenom(String recherche) {
        String rechercheMin = recherche.toLowerCase();
        List<RendezVous> filtrés = data.stream()
                .filter(rdv ->
                        rdv.getNom().toLowerCase().contains(rechercheMin) ||
                                rdv.getPrenom().toLowerCase().contains(rechercheMin)
                )
                .collect(Collectors.toList());
        rafraichirTable(filtrés);
    }

    // Tri par date croissante
    public void trierParDate() {
        List<RendezVous> triés = data.stream()
                .sorted(Comparator.comparing(RendezVous::getDate))
                .collect(Collectors.toList());
        rafraichirTable(triés);
    }

    // Méthode commune pour rafraîchir la table
    private void rafraichirTable(List<RendezVous> liste) {
        tableRendezVous.setItems(FXCollections.observableArrayList(liste));
    }

    // Retour vers l'écran d'ajout
    @FXML
    void retourAjout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/hospiconnect/resources/AddRendezVous.fxml"));
            tableRendezVous.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de la vue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}