package org.hospiconnect.controller.attributions;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.AttributionsDons;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.service.AttributionDonService;
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

public class ShowAttribution {

    @FXML
    private VBox attributionListContainer;
    @FXML
    private Button addAttributionButton;
    @FXML
    private Button menuHomeButton;
    @FXML
    private javafx.scene.control.TextField searchField;

    @FXML private Button sortButton;
    @FXML private Button exportPdfButton;

    private final AttributionDonService attributionDonService = new AttributionDonService();
    private List<AttributionsDons> attributionsDons = new ArrayList<>();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();
    private boolean isAscending = true;

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
    public void handleStatClick(ActionEvent event) {
        try {
            // Charger la page ShowDon.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attributions/StatistiquesAttribution.fxml"));
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
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));

        try {
            attributionsDons = attributionService.findAll(); // On garde la liste originale pour tri/filtrage
            displayFilteredAttributions(attributionsDons, "");
            // Export PDF
            exportPdfButton.setOnAction(e -> {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Enregistrer PDF");
                chooser.setInitialFileName("attributions.pdf");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
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
            showErrorAlert("Erreur de récupération", "Erreur lors de la récupération des attributions : " + e.getMessage());
        }

        // Bouton ajout
        addAttributionButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attributions/AddAttribution.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) addAttributionButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 🔍 Événement sur le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            displayFilteredAttributions(attributionsDons, newValue);
        });

        // Bouton de tri
        sortButton.setOnAction(e -> sortAttributionsByType());
    }

    private void sortAttributionsByType() {
        List<AttributionsDons> sortedList = attributionsDons.stream()
                .sorted((d1, d2) -> {
                    String statut1 = d1.getStatut() != null ? d1.getStatut() : "";
                    String statut2 = d2.getStatut() != null ? d2.getStatut() : "";
                    return isAscending ? statut1.compareToIgnoreCase(statut2)
                            : statut2.compareToIgnoreCase(statut1);
                })
                .collect(Collectors.toList());

        isAscending = !isAscending;
        sortButton.setText(isAscending ? "Trier ↑" : "Trier ↓");

        displayFilteredAttributions(sortedList, searchField.getText());
    }


    @FXML
    public void handleDelete(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        AttributionsDons attribution = (AttributionsDons) sourceButton.getUserData();

        try {
            attributionService.delete(attribution);

            attributionListContainer.getChildren().removeIf(node -> {
                if (node instanceof VBox) {
                    VBox vbox = (VBox) node;
                    return vbox.getId().equals("attribution-card-" + attribution.getId());
                }
                return false;
            });

            showSuccessAlert("Succès", "L'attribution a été supprimée.");

        } catch (SQLException e) {
            showErrorAlert("Erreur de suppression", "Erreur lors de la suppression de l'attribution : " + e.getMessage());
        }
    }
    private void displayFilteredAttributions(List<AttributionsDons> attributions, String filter) {
        attributionListContainer.getChildren().clear();
        String lowerFilter = filter.toLowerCase();

        for (AttributionsDons attribution : attributions) {
            // Exemple complet corrigé :
            String donateur = (attribution.getDon() != null && attribution.getDon().getDonateur() != null)
                    ? attribution.getDon().getDonateur().getNom().toLowerCase() : "";

            String patientNom = (attribution.getDemande() != null && attribution.getDemande().getPatient() != null)
                    ? attribution.getDemande().getPatient().getNom().toLowerCase() : "";

            String patientPrenom = (attribution.getDemande() != null && attribution.getDemande().getPatient() != null)
                    ? attribution.getDemande().getPatient().getPrenom().toLowerCase() : "";


            if (donateur.contains(lowerFilter) || patientNom.contains(lowerFilter) || patientPrenom.contains(lowerFilter)) {
                VBox card = createAttributionCard(attribution);
                attributionListContainer.getChildren().add(card);
            }
        }
    }


    private VBox createAttributionCard(AttributionsDons attribution) {
        VBox card = new VBox(8);
        card.setStyle("""
            -fx-padding: 10;
            -fx-border-color: #cccccc;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-background-color: #f9f9f9;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);    
        """);

        card.setId("attribution-card-" + attribution.getId());

        Label donateurLabel = new Label("Donateur : " +
                (attribution.getDon() != null && attribution.getDon().getDonateur() != null
                        ? attribution.getDon().getDonateur().getNom() + " " + attribution.getDon().getDonateur().getPrenom()
                        : "N/A")
        );

        Label beneficiaireLabel = new Label("Bénéficiaire : " +
                (attribution.getDemande() != null && attribution.getDemande().getPatient() != null
                        ? attribution.getDemande().getPatient().getNom() + " " + attribution.getDemande().getPatient().getPrenom()
                        : "N/A")
        );

        Label typeDonLabel = new Label("Type de don : " +
                (attribution.getDon() != null ? attribution.getDon().getTypeDon() : "N/A")
        );

        Label typeBesoinLabel = new Label("Type de besoin : " +
                (attribution.getDemande() != null ? attribution.getDemande().getTypeBesoin() : "N/A")
        );

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = attribution.getDateAttribution() != null
                ? dateFormat.format(attribution.getDateAttribution())
                : "N/A";
        Label dateLabel = new Label("Date d'attribution : " + formattedDate);

        Label statutLabel = new Label("Statut : " + (attribution.getStatut() != null ? attribution.getStatut() : "N/A"));
        donateurLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");
        beneficiaireLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        HBox buttonContainer = new HBox(10);
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        // Appliquer un style CSS pour le bouton "Modifier" (vert)
        modifyButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

// Appliquer un style CSS pour le bouton "Supprimer" (rouge)
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        modifyButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attributions/ModifyAttribution.fxml"));
                BorderPane modifyPage = loader.load();

                ModifyAttribution modifyController = loader.getController();
                modifyController.initialize(attribution);

                // Récupérer la fenêtre actuelle à partir du bouton cliqué
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Remplacer la scène actuelle avec la nouvelle page de modification
                Scene newScene = new Scene(modifyPage);
                currentStage.setScene(newScene);
                currentStage.setTitle("Modifier l'attribution");

            } catch (IOException e) {
                showErrorAlert("Erreur de chargement", "Erreur lors du chargement de la page de modification : " + e.getMessage());
            }
        });

        deleteButton.setUserData(attribution);
        deleteButton.setOnAction(this::handleDelete);

        buttonContainer.getChildren().addAll(modifyButton, deleteButton);
        card.getChildren().addAll(donateurLabel, beneficiaireLabel,typeDonLabel,typeBesoinLabel, dateLabel ,statutLabel, buttonContainer);

        return card;
    }
    private void exportToPdf(File file) throws Exception {
        String logoUri = getClass().getResource("/images/logo.png").toURI().toString();
        StringBuilder html = new StringBuilder();

        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">")
                .append("<head>")
                .append("<meta charset='UTF-8'/>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; }")
                .append("h2 { text-align: center; color: #333; }")
                .append(".logo { display: block; margin: 0 auto 20px auto; width: 100px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }")
                .append("th { background-color: #f2f2f2; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<img src='").append(logoUri).append("' class='logo'/>")
                .append("<h2>Liste des attributions</h2>")
                .append("<table>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>Bénéficiaire</th>")
                .append("<th>Statut</th>")
                .append("<th>Don</th>")
                .append("<th>Date d'attribution</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        for (AttributionsDons d : attributionsDons) {
            html.append("<tr>");

            String nomBeneficiaire = "Inconnu";
            if (d.getBeneficiaireId() != 0 && d.getBeneficiaire() != null) {
                nomBeneficiaire = d.getBeneficiaire().getNom() + " " + d.getBeneficiaire().getPrenom();
            }

            html.append("<td>").append(nomBeneficiaire).append("</td>")
                    .append("<td>").append(d.getStatut()).append("</td>")
                    .append("<td>").append(d.getDon().getTypeDon()).append("</td>")
                    .append("<td>").append(df.format(d.getDateAttribution())).append("</td>")
                    .append("</tr>");
        }


        html.append("</tbody>")
                .append("</table>")
                .append("</body>")
                .append("</html>");

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html.toString());
        renderer.layout();

        try (FileOutputStream os = new FileOutputStream(file)) {
            renderer.createPDF(os);
        }
    }
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
