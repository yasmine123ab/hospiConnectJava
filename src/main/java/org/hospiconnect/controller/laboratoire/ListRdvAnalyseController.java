package org.hospiconnect.controller.laboratoire;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.hospiconnect.model.laboratoire.RdvAnalyse;
import org.hospiconnect.service.laboratoire.*;

import java.util.Comparator;

public class ListRdvAnalyseController {

    private boolean trierRdvCroissant = true;

    private final AnalyseRdvCrudService analyseRdvCrudService = AnalyseRdvCrudService.getInstance();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();
    private final DisponibiliteAnalyseCrudService disponibiliteAnalyseCrudService = DisponibiliteAnalyseCrudService.getInstance();

    @FXML
    private ListView<RdvAnalyse> rdvAnalyseListView;

    @FXML
    private ComboBox<String> rdvAnalyseTrierComboBox;
    @FXML
    private ComboBox<String> rdvAnalyseRechercherComboBox;
    @FXML
    private TextField rdvAnalyseRechercherTextField;
    @FXML
    private Button rdvAnalyseTrierButton;
    @FXML
    private Button rdvAnalyseAjouterButton;
    @FXML
    private Button rdvAnalysePdfButton;
    @FXML
    private Button FermerFenetreButton;
    @FXML
    private Button ReduireFenetreButton;

    @FXML
    private Button menuRdvAnalyseButton;
    //@FXML
    //private Button menuHospiChatButton;
    @FXML
    private Button menuHomeButton;


    @FXML
    public void initialize() {
        resetListItems();

        // Remplir choix tri et recherche
        rdvAnalyseTrierComboBox.setItems(FXCollections.observableArrayList(
                "Patient", "Date Rdv", "Disponibilité"
        ));
        rdvAnalyseTrierComboBox.getSelectionModel().selectFirst();

        rdvAnalyseRechercherComboBox.setItems(FXCollections.observableArrayList(
                "Patient", "Date Rdv", "Disponibilité"
        ));
        rdvAnalyseRechercherComboBox.getSelectionModel().selectFirst();

        rdvAnalyseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RdvAnalyse rdvAnalyse, boolean empty) {
                super.updateItem(rdvAnalyse, empty);
                if (empty || rdvAnalyse == null) {
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label patient = new Label(userServiceLight.findUserNameById(rdvAnalyse.getIdPatient()));
                    Label dateRdv = new Label(rdvAnalyse.getDateRdv().toString());
                    Label dispo = new Label(disponibiliteAnalyseCrudService.findDispoHDebutById(rdvAnalyse.getIdDisponibilite()));

                    EditRemoveButtonsBox<RdvAnalyse> actionsFactory = new EditRemoveButtonsBox<>();
                    HBox actionBox = actionsFactory.create(rdvAnalyse,
                            a -> SceneUtils.openNewScene(
                                    "/laboratoireFront/rdvAnalyse/formRdvAnalyse.fxml",
                                    rdvAnalyseAjouterButton.getScene(),
                                    a
                            ),
                            a -> {
                                analyseRdvCrudService.deleteById(a.getId());
                                resetListItems();
                            }
                    );

                    // Set width for consistent alignment
                    patient.setPrefWidth(140);
                    dateRdv.setPrefWidth(140);
                    dispo.setPrefWidth(140);
                    actionBox.setPrefWidth(100);

                    row.getChildren().addAll(patient, dateRdv, dispo, actionBox);
                    setGraphic(row);
                }
            }
        });

        rdvAnalyseTrierButton.setOnAction(e -> {
            String champ = rdvAnalyseTrierComboBox.getSelectionModel().getSelectedItem();
            Comparator<RdvAnalyse> comparateur = null;

            if ("Patient".equals(champ)) {
                comparateur = Comparator.comparing(a -> userServiceLight.findUserNameById(a.getIdPatient()), String.CASE_INSENSITIVE_ORDER);
            } else if ("Date Rdv".equals(champ)) {
                comparateur = Comparator.comparing(RdvAnalyse::getDateRdv, Comparator.nullsLast(Comparator.naturalOrder()));
            } else if ("Disponibilité".equals(champ)) {
                comparateur = Comparator.comparing(a -> disponibiliteAnalyseCrudService.findDispoHDebutById(a.getIdDisponibilite()), String.CASE_INSENSITIVE_ORDER);
            }

            if (comparateur != null) {
                if (!trierRdvCroissant) {
                    comparateur = comparateur.reversed();
                }
                var triListe = rdvAnalyseListView.getItems().stream()
                        .sorted(comparateur)
                        .toList();
                rdvAnalyseListView.setItems(FXCollections.observableList(triListe));
                trierRdvCroissant = !trierRdvCroissant;
            }
        });

        // --- Recherche dynamique avec setOnKeyReleased ---
        rdvAnalyseRechercherTextField.setOnKeyReleased(e -> {
            String recherche = rdvAnalyseRechercherTextField.getText().strip().toLowerCase();
            String champChoisi = rdvAnalyseRechercherComboBox.getSelectionModel().getSelectedItem();

            rdvAnalyseListView.setItems(FXCollections.observableList(
                    analyseRdvCrudService.findAll().stream()
                            .filter(rdv -> {
                                String valeurChamp = "";

                                if ("Patient".equals(champChoisi)) {
                                    valeurChamp = userServiceLight.findUserNameById(rdv.getIdPatient());
                                } else if ("Date Rdv".equals(champChoisi)) {
                                    valeurChamp = rdv.getDateRdv() != null ? rdv.getDateRdv().toString() : "";
                                } else if ("Disponibilité".equals(champChoisi)) {
                                    valeurChamp = disponibiliteAnalyseCrudService.findDispoHDebutById(rdv.getIdDisponibilite());
                                }

                                return valeurChamp.toLowerCase().contains(recherche);
                            })
                            .toList()
            ));
        });

        rdvAnalyseAjouterButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireFront/rdvAnalyse/formRdvAnalyse.fxml",
                rdvAnalyseAjouterButton.getScene(),
                null
        ));
        FermerFenetreButton.setOnAction(e -> ((Stage) FermerFenetreButton.getScene().getWindow()).close());
        ReduireFenetreButton.setOnAction(e -> ((Stage) ReduireFenetreButton.getScene().getWindow()).setIconified(true));

        // Menu navigation
        menuRdvAnalyseButton.setOnAction(e -> SceneUtils.openNewScene(
                "/laboratoireFront/rdvAnalyse/listRdvAnalyse.fxml", menuRdvAnalyseButton.getScene(), null));
        //menuHospiChatButton.setOnAction(e -> SceneUtils.openNewScene(
         //       "/laboratoireBack/hospiChatLabo.fxml", menuHospiChatButton.getScene(), null));
        menuHomeButton.setOnAction(e -> SceneUtils.openNewScene(
                "/HomePages/frontList.fxml", menuHomeButton.getScene(), null));
    }

    private void resetListItems() {
        rdvAnalyseListView.setItems(FXCollections.observableList(analyseRdvCrudService.findAll()));
    }








}
