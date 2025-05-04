package org.hospiconnect.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class RendezVous {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private LocalDate date;
    private LocalTime heure;
    private String type;
    private String gravite;
    private String statut;
    private String commentaire;



    // Constructeur complet modifié
    public RendezVous() {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.date = date;
        this.heure = heure;
        this.type = type;
        this.gravite = gravite;
        this.statut = statut;
        this.commentaire = commentaire;
    }



    public RendezVous( String nom, String prenom, String telephone, String email, LocalDate date, LocalTime heure, String type, String gravite, String commentaire) {
    }

    // Ajout des getters/setters pour id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Getters et setters modifiés pour date/heure
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeure() { return heure; }
    public void setHeure(LocalTime heure) { this.heure = heure; }


    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }



    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGravite() { return gravite; }
    public void setGravite(String gravite) { this.gravite = gravite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }


}