package org.hospiconnect.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Materiel;
import org.hospiconnect.model.MouvementMaterielJoint;
import org.hospiconnect.service.MaterielService1;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import org.hospiconnect.service.mouvementService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class afficherMateriel {

    @FXML
    private ListView<Materiel> matrielTable;

    @FXML
    private Button menuHomeButton;

    @FXML
    private TextField recherche;
    @FXML
    private ComboBox<String> critereRecherche;


    private boolean triCroissant = false;
    private final PauseTransition pauseQueue = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));

        // Ajout du bouton "Exporter vers Excel"
        Button exportExcelButton = new Button("Exporter Excel");
        exportExcelButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white;");
        exportExcelButton.setOnAction(e -> exporterVersExcel());

        HBox buttonsBox = new HBox(10, menuHomeButton, exportExcelButton);
        ((VBox) matrielTable.getParent()).getChildren().add(0, buttonsBox);  // ajouter le bouton en haut

        // Configuration de la recherche avec délai
        recherche.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseQueue.setOnFinished(event -> filtrerMateriels(newValue));
            pauseQueue.playFromStart();
        });

        // Affichage initial des matériels
        afficherMateriels(null);
        critereRecherche.setItems(FXCollections.observableArrayList(
                "Nom", "Catégorie", "État", "Emplacement", "Quantité", "Date d'ajout"
        ));
        critereRecherche.getSelectionModel().selectFirst(); // Sélectionner "Nom" par défaut

    }

    @FXML
    void afficherMateriels(ActionEvent event) {
        MaterielService1 service = new MaterielService1();
        try {
            List<Materiel> materiels = service.findAll();
            matrielTable.getItems().clear();
            matrielTable.getItems().addAll(materiels);

            matrielTable.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Materiel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        HBox content = new HBox(10);
                        content.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                        // Création des labels (simulant des colonnes)
                        Label nomLabel = new Label(item.getNom());
                        Label qteLabel = new Label(String.valueOf(item.getQuantite()));
                        Label catLabel = new Label(item.getCategorie());
                        Label etatLabel = new Label(item.getEtat());
                        Label empLabel = new Label(item.getEmplacement());
                        Label dateLabel = new Label(item.getDate_ajout().toString());

                        // Largeur fixe ou maxWidth avec priorités
                        for (Label label : new Label[]{nomLabel, qteLabel, catLabel, etatLabel, empLabel, dateLabel}) {
                            label.setPrefWidth(120);
                            label.setWrapText(true);
                        }

                        // Boutons
                        Button btnModifier = new Button("Modifier");
                        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnModifier.setOnAction(e -> modifierMateriel(item));

                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        btnSupprimer.setOnAction(e -> supprimerMateriel(item));

                        Button btnQRCode = new Button("QR Code");
                        btnQRCode.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white;");
                        btnQRCode.setOnAction(e -> afficherQRCode(item));


                        content.getChildren().addAll(
                                nomLabel, qteLabel, catLabel, etatLabel, empLabel, dateLabel,
                                btnModifier, btnSupprimer,btnQRCode
                        );
                        setGraphic(content);
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert("Erreur", "Erreur lors du chargement des matériels", e.getMessage());
        }
    }
    private void afficherQRCode(Materiel materiel) {
        try {
            // Récupère les mouvements pour le matériel
            mouvementService service = new mouvementService();
            List<MouvementMaterielJoint> mouvements = service.getMouvementsByMaterielId(materiel.getId());

            if (mouvements.isEmpty()) {
                showAlert("Aucun mouvement", "Ce matériel n'a pas de mouvements enregistrés", "");
                return;
            }

            // Construction du contenu du QR Code
            StringBuilder qrContent = new StringBuilder("Historique des mouvements pour: " + materiel.getNom() + "\n\n");
            for (MouvementMaterielJoint mouvement : mouvements) {
                qrContent.append("Date: ").append(mouvement.getDateMouvement()).append("\n");
                qrContent.append("Type: ").append(mouvement.getTypeMouvement()).append("\n");
                qrContent.append("Quantité: ").append(mouvement.getQuantite()).append("\n");
                qrContent.append("Motif: ").append(mouvement.getMotif()).append("\n\n");
            }

            // Génération du QR Code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);  // Réduit la marge autour du QR Code
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent.toString(), BarcodeFormat.QR_CODE, 200, 200, hints);

            // Conversion du BitMatrix en BufferedImage
            BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < 200; i++) {
                for (int j = 0; j < 200; j++) {
                    bufferedImage.setRGB(i, j, bitMatrix.get(i, j) ? 0x000000 : 0xFFFFFF);  // Noir ou blanc
                }
            }

            // Conversion du BufferedImage en Image JavaFX
            Image qrCodeImage = SwingFXUtils.toFXImage(bufferedImage, null);

            // Affichage du QR code dans un ImageView
            ImageView qrCodeView = new ImageView(qrCodeImage);
            qrCodeView.setFitWidth(200);
            qrCodeView.setFitHeight(200);

            // Affichage du QR Code dans une nouvelle fenêtre
            Stage qrStage = new Stage();
            qrStage.setTitle("QR Code - Historique des mouvements");
            VBox vbox = new VBox(qrCodeView);
            Scene qrScene = new Scene(vbox);
            qrStage.setScene(qrScene);
            qrStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le QR Code", e.getMessage());
        }
    }


    // Export vers Excel
    private void exporterVersExcel() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Matériels");

            // Entêtes
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "Nom", "Quantité", "Référence", "Disponibilité", "Catégorie",
                    "État", "Emplacement", "Date d'ajout", "Âge (mois)", "Commentaire automatique"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Remplissage des données
            int rowNum = 1;
            for (Materiel materiel : matrielTable.getItems()) {
                Row row = sheet.createRow(rowNum++);

                int ageMois = calculerAgeEnMois(materiel.getDate_ajout());
                String commentaire = genererCommentaire(materiel);

                row.createCell(0).setCellValue(materiel.getNom());
                row.createCell(1).setCellValue(materiel.getQuantite());
                row.createCell(2).setCellValue("MAT-" + String.format("%05d", materiel.getId())); // Référence formatée
                row.createCell(3).setCellValue(materiel.getQuantite() > 0 ? 1 : 0); // Disponibilité (1 ou 0)
                row.createCell(4).setCellValue(materiel.getCategorie());
                row.createCell(5).setCellValue(materiel.getEtat());
                row.createCell(6).setCellValue(materiel.getEmplacement());
                row.createCell(7).setCellValue(materiel.getDate_ajout() != null ? materiel.getDate_ajout().toString() : "");
                row.createCell(8).setCellValue(ageMois + " mois");
                row.createCell(9).setCellValue(commentaire);
            }

            // Auto-size colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Choix de l'emplacement du fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(matrielTable.getScene().getWindow());

            if (file != null) {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();

                showInformation("Succès", "Exportation réussie", "✅ Les matériels ont été exportés avec succès.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur d'exportation", e.getMessage());
        }
    }
    private void filtrerMateriels(String motCle) {
        if (motCle == null || motCle.isEmpty()) {
            afficherMateriels(null);
            return;
        }

        String critere = critereRecherche.getSelectionModel().getSelectedItem();

        try {
            MaterielService1 service = new MaterielService1();
            List<Materiel> tousMateriels = service.findAll();

            List<Materiel> materielsFiltres = tousMateriels.stream()
                    .filter(m -> {
                        switch (critere) {
                            case "Nom":
                                return m.getNom().toLowerCase().contains(motCle.toLowerCase());
                            case "Catégorie":
                                return m.getCategorie().toLowerCase().contains(motCle.toLowerCase());
                            case "État":
                                return m.getEtat().toLowerCase().contains(motCle.toLowerCase());
                            case "Emplacement":
                                return m.getEmplacement().toLowerCase().contains(motCle.toLowerCase());
                            case "Quantité":
                                return String.valueOf(m.getQuantite()).contains(motCle);
                            case "Date d'ajout":
                                return m.getDate_ajout().toString().contains(motCle);
                            default:
                                return true;
                        }
                    })
                    .collect(Collectors.toList());

            matrielTable.getItems().clear();
            matrielTable.getItems().addAll(materielsFiltres);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur de recherche", "Une erreur est survenue", e.getMessage());
        }
    }


    private void modifierMateriel(Materiel m) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMatriel.fxml"));
            Parent root = loader.load();

            AddMatriel controller = loader.getController();
            controller.setMateriel(m);

            Stage stageActuel = (Stage) matrielTable.getScene().getWindow();
            stageActuel.getScene().setRoot(root);
            stageActuel.setTitle("Modifier Matériel - " + m.getNom());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur", "Le formulaire de modification n'a pas pu être chargé.");
        }
    }

    private void supprimerMateriel(Materiel m) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le matériel");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + m.getNom() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            MaterielService1 service = new MaterielService1();
            try {
                service.delete(m);
                afficherMateriels(null); // refresh
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMatriel.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.getScene().setRoot(root);
            currentStage.setTitle("Ajouter un Matériel");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page", "❌ Erreur lors du chargement de la page d'ajout.");
        }
    }

    @FXML
    void mouvement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListMouvement.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node)event.getSource()).getScene();
            currentScene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page", "❌ Une erreur est survenue lors du chargement de la page de mouvement.");
        }
    }

    @FXML
    void trier(ActionEvent event) {
        ObservableList<Materiel> items = matrielTable.getItems();

        if (triCroissant) {
            matrielTable.setItems(items.sorted(Comparator.comparing(Materiel::getDate_ajout)));
        } else {
            matrielTable.setItems(items.sorted(Comparator.comparing(Materiel::getDate_ajout).reversed()));
        }

        triCroissant = !triCroissant;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private int calculerAgeEnMois(java.util.Date dateAjout) {
        if (dateAjout == null) {
            return 0;
        }

        java.sql.Date sqlDateAjout = new java.sql.Date(dateAjout.getTime()); // conversion
        java.time.LocalDate localDateAjout = sqlDateAjout.toLocalDate();     // puis LocalDate

        java.time.Period period = java.time.Period.between(localDateAjout, java.time.LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }




    private String genererCommentaire(Materiel materiel) {
        if (materiel.getEtat() != null && materiel.getEtat().toLowerCase().contains("réparer")) {
            return "À remplacer rapidement";
        } else if (materiel.getQuantite() == 0) {
            return "Stock épuisé, à réapprovisionner";
        } else {
            return "Disponible";
        }
    }

    private void showInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
