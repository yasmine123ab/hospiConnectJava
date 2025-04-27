package org.hospiconnect.controller.laboratoire;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.RdvAnalyse;
import org.hospiconnect.service.laboratoire.*;

import java.io.File;
import java.util.Comparator;

public class ListAnalyseController {

    private boolean trierCroissant = true;

    private final AnalyseCrudService analyseCrudService = AnalyseCrudService.getInstance();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();
    private final AnalyseRdvCrudService analyseRdvCrudService = AnalyseRdvCrudService.getInstance();
    private final TypeAnalyseCrudService typeAnalyseCrudService = TypeAnalyseCrudService.getInstance();


    @FXML
    private ListView<Analyse> analyseListView;

    @FXML
    private TextField analyseRechercherTextField;
    @FXML
    private Button analyseTrierButton;
    @FXML
    private Button analyseAjouterButton;
    @FXML
    private Button analysePdfButton;
    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

    @FXML
    private Button menuAnalyseButton;
    @FXML
    private Button menuTypeAnalyseButton;
    @FXML
    private Button menuDispoAnalyseButton;
    @FXML
    private Button menuDashboardButton;
    @FXML
    private Button menuHospiChatButton;
    @FXML
    private Button menuHomeButton;

    @FXML
    public void initialize() {
        resetListItems();

        analyseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Analyse analyse, boolean empty) {
                super.updateItem(analyse, empty);
                if (empty || analyse == null) {
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label patient = new Label(userServiceLight.findUserNameById(analyse.getIdPatient()));
                    Label personnel = new Label(userServiceLight.findUserNameById(analyse.getIdPersonnel()));
                    String dateRdvStr = analyseRdvCrudService.findDateRdvByIdOrNull(analyse.getIdRdv());
                    Label dateRdv = new Label(dateRdvStr != null ? dateRdvStr : "");

                    Label datePrelevement = new Label(
                            analyse.getDatePrelevement() != null ? analyse.getDatePrelevement().toString() : ""
                    );
                    Label etat = new Label(analyse.getEtat());
                    String nomTypeAnalyse = typeAnalyseCrudService.findTypeNameById(analyse.getIdTypeAnalyse());
                    Label typeAnalyse = new Label(nomTypeAnalyse != null ? nomTypeAnalyse : "");
                    Label dateResultat = new Label(
                            analyse.getDateResultat() != null ? analyse.getDateResultat().toString() : ""
                    );

                    HBox actionBox = new HBox(5);
                    actionBox.setAlignment(Pos.CENTER);

// Bouton Modifier
                    Button editButton = new Button();
                    editButton.setStyle("-fx-background-color: transparent;");
                    ImageView editIcon = new ImageView("/imagesLabo/edit.png");
                    editIcon.setFitWidth(16); editIcon.setFitHeight(16);
                    editButton.setGraphic(editIcon);
                    editButton.setOnAction(e -> SceneUtils.openNewScene(
                            "/laboratoireBack/analyse/formAnalyse.fxml",
                            analyseListView.getScene(), analyse));

// Bouton Supprimer
                    Button deleteButton = new Button();
                    deleteButton.setStyle("-fx-background-color: transparent;");
                    ImageView deleteIcon = new ImageView("/imagesLabo/delete.png");
                    deleteIcon.setFitWidth(16); deleteIcon.setFitHeight(16);
                    deleteButton.setGraphic(deleteIcon);
                    deleteButton.setOnAction(e -> {
                        analyseCrudService.deleteById(analyse.getId());
                        resetListItems();
                    });

// Bouton Résultat
                    Button resultButton = new Button();
                    resultButton.setStyle("-fx-background-color: transparent;");
                    ImageView resultIcon = new ImageView("/imagesLabo/result.png");
                    resultIcon.setFitWidth(16); resultIcon.setFitHeight(16);
                    resultButton.setGraphic(resultIcon);
                    resultButton.setOnAction(e -> SceneUtils.openNewScene(
                            "/laboratoireBack/analyse/ajouterResultatAnalyse.fxml",
                            analyseListView.getScene(), analyse));

                    actionBox.getChildren().addAll(editButton, deleteButton, resultButton);



                    // Set width for consistent alignment
                    patient.setPrefWidth(140);
                    personnel.setPrefWidth(140);
                    dateRdv.setPrefWidth(100);
                    datePrelevement.setPrefWidth(140);
                    etat.setPrefWidth(80);
                    typeAnalyse.setPrefWidth(140);
                    dateResultat.setPrefWidth(120);
                    actionBox.setPrefWidth(100);

                    row.getChildren().addAll(patient, personnel, dateRdv, datePrelevement, etat, typeAnalyse, dateResultat, actionBox);
                    setGraphic(row);
                }
            }
        });

        analyseTrierButton.setOnAction(e -> {
            Comparator<Analyse> comparateur = Comparator.comparing(Analyse::getDatePrelevement);
            if (!trierCroissant) {
                comparateur = comparateur.reversed();
            }

            var triListe = analyseListView.getItems().stream()
                    .sorted(comparateur)
                    .toList();
            analyseListView.setItems(FXCollections.observableList(triListe));

            trierCroissant = !trierCroissant; // alterner le sens du tri
        });

        analyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = analyseRechercherTextField.getText().strip().toLowerCase();

            analyseListView.setItems(FXCollections.observableList(
                    analyseCrudService.findAll().stream()
                            .filter(a -> {
                                String patientName = userServiceLight.findUserNameById(a.getIdPatient()).toLowerCase();
                                String datePrelevementStr = a.getDatePrelevement() != null
                                        ? a.getDatePrelevement().toString().toLowerCase()
                                        : "";
                                String etatStr = a.getEtat() != null ? a.getEtat().toLowerCase() : "";

                                return recherche.isBlank()
                                        || patientName.contains(recherche)
                                        || datePrelevementStr.contains(recherche)
                                        || etatStr.contains(recherche);
                            })
                            .toList()
            ));
        });

        analysePdfButton.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Enregistrer le PDF des analyses");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(analysePdfButton.getScene().getWindow());
            if (file != null) {
                AnalyseService.getInstance().exportAnalyses(file.getAbsolutePath());
            }
        });
        analyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/formAnalyse.fxml",
                analyseAjouterButton.getScene(),
                null
        ));
        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));

        // Menu navigation
        menuAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/analyse/listAnalyse.fxml", menuAnalyseButton.getScene(), null));
        menuTypeAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/typeAnalyse/listTypeAnalyse.fxml", menuTypeAnalyseButton.getScene(), null));
        menuDispoAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/disponibiliteAnalyse/listDispoAnalyse.fxml", menuDispoAnalyseButton.getScene(), null));
        menuDashboardButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/dashboardLabo.fxml", menuDashboardButton.getScene(), null));
        menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/backList.fxml", menuHomeButton.getScene(), null));
    }

    private void resetListItems() {
        System.out.println(analyseListView);
        analyseListView.setItems(FXCollections.observableList(analyseCrudService.findAll()));
    }
}
