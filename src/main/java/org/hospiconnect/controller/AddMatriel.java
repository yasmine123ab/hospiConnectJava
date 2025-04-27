package org.hospiconnect.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.hospiconnect.controller.laboratoire.SceneUtils;
import org.hospiconnect.model.Materiel;
import org.hospiconnect.service.MaterielService1;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import java.time.LocalDate;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


public class AddMatriel {
    @FXML
    private TextField nom;

    @FXML
    private TextField categorie;

    @FXML
    private ComboBox<String> etat;

    @FXML
    private TextField quantite;

    @FXML
    private TextField emplacement;

    @FXML
    private DatePicker date;

    @FXML
    private Button retourner;

    private final ObservableList<String> etats = FXCollections.observableArrayList(
            "Neuf", "Usagé", "En réparation"
    );

    private Materiel materielEnCours;

    public void initialize() {
        retourner.setOnAction(e -> SceneUtils.openNewScene(
                "/ListMateriel.fxml", retourner.getScene(), null));
    }

    @FXML
    void ajouter(ActionEvent event) {
        // Vérification que tous les champs sont remplis
        if (nom.getText().isEmpty()) {
            showErrorAlert("Le champ 'Nom' est obligatoire.");
            return;
        }
        if (categorie.getText().isEmpty()) {
            showErrorAlert("Le champ 'Catégorie' est obligatoire.");
            return;
        }
        if (etat.getValue() == null) {
            showErrorAlert("Le champ 'Etat' est obligatoire.");
            return;
        }
        if (quantite.getText().isEmpty()) {
            showErrorAlert("Le champ 'Quantité' est obligatoire.");
            return;
        }
        if (emplacement.getText().isEmpty()) {
            showErrorAlert("Le champ 'Emplacement' est obligatoire.");
            return;
        }
        if (date.getValue() == null) {
            showErrorAlert("Le champ 'Date' est obligatoire.");
            return;
        }

        // Vérification que la quantité est un entier positif
        int qte;
        try {
            qte = Integer.parseInt(quantite.getText());
            if (qte < 0) {
                throw new NumberFormatException("Quantité négative");
            }
        } catch (NumberFormatException e) {
            showErrorAlert("La quantité doit être un entier positif.");
            return;
        }

        // Vérification de la date
        LocalDate selectedDate = date.getValue();
        LocalDate today = LocalDate.now();
        if (selectedDate.isAfter(today)) {
            showErrorAlert("La date doit être inférieure ou égale à aujourd'hui.");
            return;
        }

        // Création du matériel
        MaterielService1 us = new MaterielService1();
        Materiel m = new Materiel(
                qte,
                nom.getText(),
                categorie.getText(),
                etat.getValue(),
                emplacement.getText(),
                java.sql.Date.valueOf(date.getValue())
        );

        try {
            us.insert(m);

            // Vérification seuil après insertion
            int seuil = 5;
            if (m.getQuantite() <= seuil) {
                envoyerAlerteMail(m);
            }

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Matériel ajouté avec succès !");
            alert.showAndWait();

            // Réinitialisation des champs
            quantite.clear();
            nom.clear();
            categorie.clear();
            etat.getSelectionModel().selectFirst();
            emplacement.clear();
            date.setValue(null);

        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du matériel : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de l'ajout.");
            alert.showAndWait();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setMateriel(Materiel m) {
        this.materielEnCours = m;
        nom.setText(m.getNom());
        quantite.setText(String.valueOf(m.getQuantite()));
        categorie.setText(m.getCategorie());
        etat.setValue(m.getEtat());
        emplacement.setText(m.getEmplacement());
        date.setValue(LocalDate.now());

        if (m.getNom() == null || m.getNom().isEmpty()) {
            showErrorAlert("Le nom du matériel est obligatoire.");
            return;
        }
        if (m.getCategorie() == null || m.getCategorie().isEmpty()) {
            showErrorAlert("La catégorie du matériel est obligatoire.");
            return;
        }
        if (m.getEtat() == null || m.getEtat().isEmpty()) {
            showErrorAlert("L'état du matériel est obligatoire.");
            return;
        }
        if (m.getQuantite() < 0) {
            showErrorAlert("La quantité du matériel ne peut pas être négative.");
            return;
        }
        if (m.getEmplacement() == null || m.getEmplacement().isEmpty()) {
            showErrorAlert("L'emplacement du matériel est obligatoire.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate selectedDate = date.getValue();
        if (selectedDate.isAfter(today)) {
            showErrorAlert("La date doit être inférieure ou égale à aujourd'hui.");
            return;
        }
    }

    // 📩 Méthode pour envoyer un mail d'alerte
    public void envoyerAlerteMail(Materiel materiel) {
        String to = "saoudihamadi2003@gmail.com"; // <-- ici tu mets l'email du destinataire final (ex: ton chef, toi-même, un collègue...)

        String from = "mahdisaoufi@gmail.com";       // <-- ici tu mets TON adresse Gmail d'envoi

        String host = "smtp.gmail.com";           // <-- laisse comme ça pour Gmail

        final String username = "mahdisaoufi@gmail.com";    // <-- TON adresse Gmail (pareil que "from")
        final String password = "dbjq npas xbiv ecfz";     // <-- TON mot de passe d'application (voir ci-dessous 👇)

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Alerte - Stock Faible");

            String emailContent = "<html>"
                    + "<body>"
                    + "<h2>Attention !</h2>"
                    + "<p><strong>Le matériel</strong> : '" + materiel.getNom() + "'</p>"
                    + "<p><strong>Quantité restante</strong> : " + materiel.getQuantite() + "</p>"
                    + "<p><strong>Date de dernière mise à jour</strong> : " + materiel.getDate_ajout() + "</p>"
                    + "<p><strong>Action requise</strong> : Merci de renouveler le stock dès que possible.</p>"
                    + "<hr>"
                    + "<p>Merci de votre attention.</p>"
                    + "</body>"
                    + "</html>";

            message.setContent(emailContent, "text/html");

            Transport.send(message);

            System.out.println("Alerte email envoyée avec succès.");

        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi du mail : " + e.getMessage());
        }
    }

}
