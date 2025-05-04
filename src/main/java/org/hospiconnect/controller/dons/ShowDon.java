package org.hospiconnect.controller.dons;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hospiconnect.controller.demandes.ModifyDemande;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.Dons;
import org.hospiconnect.service.DonService;
import org.hospiconnect.service.laboratoire.UserServiceLight;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowDon {

    @FXML private VBox donListContainer;
    @FXML private Button menuHomeButton;
    @FXML private Button addDonButton;
    @FXML private TextField searchField;
    @FXML private Button sortButton;
    @FXML private Button exportPdfButton;
    @FXML private Button statistiquesButton;

    private final DonService donService = new DonService();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();

    private boolean isAscending = true;
    private List<Dons> donList = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            donList = donService.findAll();
            displayFilteredDons(donList, "");

            sortButton.setOnAction(e -> sortDonsByType());
            searchField.textProperty().addListener((obs, oldV, newV) ->
                    displayFilteredDons(donList, newV)
            );

            addDonButton.setOnAction(e -> openScene("/Dons/AddDon.fxml", addDonButton));
            menuHomeButton.setOnAction(e ->
                    SceneUtils.openNewScene("/HomePages/frontList.fxml", menuHomeButton.getScene(), null)
            );

            exportPdfButton.setOnAction(e -> {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Enregistrer PDF");
                chooser.setInitialFileName("dons.pdf");
                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("PDF", "*.pdf")
                );
                File out = chooser.showSaveDialog(exportPdfButton.getScene().getWindow());
                if (out != null) {
                    try {
                        exportToPdf(out);
                        showSuccessAlert("Succès", "PDF généré : " + out.getAbsolutePath());
                    } catch (Exception ex) {
                        showErrorAlert("Erreur PDF", ex.getMessage());
                    }
                }
            });

        } catch (SQLException e) {
            showErrorAlert("Erreur de récupération", "Impossible de charger les dons : " + e.getMessage());
        }
    }

    private void sortDonsByType() {
        donList = donList.stream()
                .sorted((d1, d2) -> {
                    String t1 = d1.getTypeDon() != null ? d1.getTypeDon() : "";
                    String t2 = d2.getTypeDon() != null ? d2.getTypeDon() : "";
                    int cmp = t1.compareToIgnoreCase(t2);
                    return isAscending ? cmp : -cmp;
                })
                .collect(Collectors.toList());
        isAscending = !isAscending;
        sortButton.setText(isAscending ? "Trier ↑" : "Trier ↓");
        displayFilteredDons(donList, searchField.getText());
    }

    private void displayFilteredDons(List<Dons> list, String filter) {
        donListContainer.getChildren().clear();
        String f = filter == null ? "" : filter.toLowerCase();

        for (Dons don : list) {
            String type = don.getTypeDon() != null ? don.getTypeDon().toLowerCase() : "";
            String nom = don.getDonateur() != null ? don.getDonateur().getNom().toLowerCase() : "";
            String prenom = don.getDonateur() != null ? don.getDonateur().getPrenom().toLowerCase() : "";

            if (type.contains(f) || nom.contains(f) || prenom.contains(f)) {
                donListContainer.getChildren().add(createDonCard(don));
            }
        }
    }

    @FXML
    public void handleFaireUnDonClick(ActionEvent event) {
        openScene("/Dons/ShowDon.fxml", (Node) event.getSource());
    }
    @FXML
    public void handleFaireUneDemandeClick(ActionEvent event) {
        openScene("/Demandes/ShowDemande.fxml", (Node) event.getSource());
    }
    @FXML
    public void handleFaireUneAttributionClick(ActionEvent event) {
        openScene("/Attributions/ShowAttribution.fxml", (Node) event.getSource());
    }
    @FXML
    public void handleStatClick(ActionEvent event) {
        openScene("/Dons/StatistiquesDon.fxml", (Node) event.getSource());
    }

    private void openScene(String fxmlPath, Node source) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            showErrorAlert("Erreur", "Impossible de charger : " + fxmlPath);
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Button btn = (Button) event.getSource();
        Dons don = (Dons) btn.getUserData();

        try {
            // Vérifier si le don est déjà lié à une attribution
            if (donService.isDonLinkedToAttribution(don)) {
                // Afficher un message d'erreur si le don est lié à une attribution
                showErrorAlert("Erreur", "Ce don est en cours de traitement et ne peut pas être supprimé.");
                return; // Sortir de la méthode sans effectuer la suppression
            }

            // Si le don n'est pas lié à une attribution, le supprimer
            donService.delete(don);
            donList.remove(don);
            displayFilteredDons(donList, searchField.getText());
            showSuccessAlert("Succès", "Don supprimé.");

        } catch (SQLException ex) {
            showErrorAlert("Erreur", "Impossible de supprimer : " + ex.getMessage());
        }
    }


    private VBox createDonCard(Dons don) {
        VBox card = new VBox(8);
        card.setStyle("""
            -fx-padding: 10;
            -fx-border-color: #ccc;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-background-color: #f4f4f4;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5,0,0,2);
        """);

        Label type = new Label("Type : " + don.getTypeDon());
        Label montant = new Label("Montant : " + don.getMontant() + " DNT");
        Label desc = new Label("Description : " + don.getDescription());
        Label date  = new Label("Date : " + new SimpleDateFormat("dd MMM yyyy").format(don.getDateDon()));
        String name = don.getDonateur() != null
                ? don.getDonateur().getNom() + " " + don.getDonateur().getPrenom()
                : "Inconnu";
        Label dono = new Label("Donateur : " + name);
        Label dispo = new Label("Disponibilité : " + (don.getDisponibilite() ? "Oui" : "Non"));

        HBox btns = new HBox(10);
        Button mod = new Button("Modifier");
        mod.setOnAction(event -> openModifyPage(don, event));
        Button del = new Button("Supprimer");
        mod.setStyle("-fx-background-color:green; -fx-text-fill:white;");
        del.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        del.setUserData(don);
        del.setOnAction(this::handleDelete);

        btns.getChildren().addAll(mod, del);
        card.getChildren().addAll(type, montant, desc, date, dono, dispo, btns);
        return card;
    }
    private void openModifyPage(Dons don, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/ModifyDon.fxml"));
            BorderPane modifyPage = loader.load();
            ModifyDon modifyController = loader.getController();
            modifyController.initialize(don);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(modifyPage));
            currentStage.setTitle("Modifier le don");
        } catch (IOException e) {
            showErrorAlert("Erreur de chargement", "Erreur lors du chargement de la page de modification : " + e.getMessage());
        }
    }

    private void exportToPdf(File file) throws Exception {
        // Chemin vers le logo (dans /resources/images/logo.png)
        String logoPath = new File(getClass().getResource("/images/logo.png").toURI()).toURI().toString();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                // Style global
                .append("body{font-family:Arial,sans-serif;margin:20px;} ")
                // Logo centré
                .append(".logo{width:150px;margin-bottom:20px;} ")
                // Centrer le titre
                .append("h2{text-align:center;color:#4CAF50;} ")
                // Style tableau
                .append("table{width:100%;border-collapse:collapse;font-size:12px;} ")
                .append("th{background-color:#4CAF50;color:white;padding:8px;text-align:left;} ")
                .append("td{padding:6px;border:1px solid #ddd;} ")
                // Lignes alternées
                .append("tr:nth-child(even){background-color:#f2f2f2;} ")
                .append("</style></head><body>")
                // Insertion du logo
                .append("<div><img src=\"").append(logoPath).append("\" class=\"logo\"/></div>")
                .append("<h2>Liste des dons</h2>")
                .append("<table><thead><tr>")
                .append("<th>Type</th><th>Montant</th><th>Description</th>")
                .append("<th>Date</th><th>Donateur</th><th>Dispo</th>")
                .append("</tr></thead><tbody>");

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        for (Dons d : donList) {
            html.append("<tr>")
                    .append("<td>").append(d.getTypeDon()).append("</td>")
                    .append("<td>").append(d.getMontant()).append("</td>")
                    .append("<td>").append(d.getDescription()).append("</td>")
                    .append("<td>").append(df.format(d.getDateDon())).append("</td>")
                    .append("<td>").append(userServiceLight
                            .findUserNameById((long)d.getDonateurId()))
                    .append("</td>")
                    .append("<td>").append(d.getDisponibilite() ? "Oui" : "Non").append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></body></html>");

        // Génération du PDF via Flying Saucer / iText
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html.toString());
        renderer.layout();
        try (FileOutputStream os = new FileOutputStream(file)) {
            renderer.createPDF(os);
        }
    }
    private void showSuccessAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    private void showErrorAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
