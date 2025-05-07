package org.hospiconnect.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import org.hospiconnect.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FrontClientController {

    @FXML private ImageView logoImage;
    @FXML private ImageView userIcon;
    @FXML private MenuButton userMenu;
    @FXML private MenuItem profilMenuItem;
    @FXML private MenuItem historiqueMenuItem;
    @FXML private MenuItem deconnexionMenuItem;
    @FXML private StackPane contentPane;
    @FXML private Hyperlink linkAccueil;
    @FXML private Hyperlink linkReclamation;
    private User session;

    private List<Object> navLinks;

    public void setSession(User user) {
        this.session = user;
        if (session != null) {
            // Chargement photo profil
            String photoPath = session.getPhoto();
            Image image;
            try {
                image = new Image(getClass().getResourceAsStream("/assets/users/" + photoPath));
                if (image.isError()) throw new Exception();
            } catch (Exception e) {
                image = new Image(getClass().getResourceAsStream("/assets/userf.png"));
            }
            userIcon.setImage(image);
            Circle clip = new Circle(16, 16, 16);
            userIcon.setClip(clip);
        }
    }

    @FXML
    public void initialize() {
        logoImage.setImage(new Image(getClass().getResourceAsStream("/HOSPI.png")));
        
        // Ajout dans la liste de navigation
        navLinks = new ArrayList<>();
        navLinks.add(linkAccueil);
        navLinks.add(linkReclamation);

        setActiveNav(linkAccueil);
        loadDashboardHome();
    }

    private void loadDashboardHome() {
        loadPage("/fornt views/accueil.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            contentPane.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNavLinkClick(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Hyperlink || source instanceof MenuItem || source instanceof Button) {
            setActiveNav(source);

            if (source == linkAccueil) {
                loadPage("/fornt views/accueil.fxml");
            } else if (source == linkReclamation) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mes-reclamations.fxml"));
                    Parent reclamationsView = loader.load();
                    
                    // Passer la session à la vue des réclamations
                    MesReclamationsController controller = loader.getController();
                    controller.setSession(session);
                    
                    contentPane.getChildren().setAll(reclamationsView);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Impossible de charger la page des réclamations");
                    alert.setContentText("Une erreur est survenue lors du chargement de la page.");
                    alert.showAndWait();
                }
            } else if (source instanceof MenuItem) {
                MenuItem item = (MenuItem) source;
                String text = item.getText();
                if ("Profil".equals(text)) {
                    loadPage("/fornt views/profil.fxml");
                }
            }
        }
    }

    private void setActiveNav(Object activeLink) {
        for (Object link : navLinks) {
            if (link instanceof Hyperlink) {
                ((Hyperlink) link).getStyleClass().remove("active");
            }
        }

        if (activeLink instanceof Hyperlink) {
            ((Hyperlink) activeLink).getStyleClass().add("active");
        }
    }

    @FXML
    public void handleProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fornt views/profil.fxml"));
            Parent profilView = loader.load();

            // Accès au contrôleur de profil
            ProfilController controller = loader.getController();
            controller.setUser(this.session);
            contentPane.getChildren().setAll(profilView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHistorique(ActionEvent event) {
        System.out.println("🧾 Historique cliqué");
    }

    @FXML
    public void handleDeconnexion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmation de déconnexion");
        alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");

        ButtonType oui = new ButtonType("Oui");
        ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(oui, non);

        alert.showAndWait().ifPresent(response -> {
            if (response == oui) {
                try {
                    // Charger la page de connexion
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent root = loader.load();
                    
                    // Récupérer la scène actuelle et la remplacer
                    contentPane.getScene().setRoot(root);
                    System.out.println("🔓 Déconnexion réussie");
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Erreur de déconnexion");
                    errorAlert.setContentText("Une erreur est survenue lors de la déconnexion.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    @FXML
    public void handleMouseEnterIcon(MouseEvent event) {
        userMenu.show();
    }

    @FXML
    public void handleMouseExitIcon(MouseEvent event) {
        userMenu.hide();
    }

    @FXML
    public void handleAddReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajout-reclamation.fxml"));
            Parent reclamationView = loader.load();

            // Récupérer le contrôleur et passer l'ID de l'utilisateur connecté
            AjoutReclamationController controller = loader.getController();
            if (session != null) {
                controller.setUserId(session.getId());
            }

            contentPane.getChildren().setAll(reclamationView);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page d'ajout de réclamation");
            alert.setContentText("Une erreur est survenue lors du chargement de la page.");
            alert.showAndWait();
        }
    }
}
