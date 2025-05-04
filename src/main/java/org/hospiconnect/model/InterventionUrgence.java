package org.hospiconnect.model;

import java.time.LocalDateTime;

public class InterventionUrgence {
    private Long id;
    private String nom;
    private String prenom;
    private String commentaire;
    private LocalDateTime dateIntervention;
    private String gravite;
    private RendezVous rendezVous;


    public InterventionUrgence() {}

    public InterventionUrgence(RendezVous rendezVous) {
        this.nom = rendezVous.getNom();
        this.prenom = rendezVous.getPrenom();
        this.commentaire = rendezVous.getCommentaire();
        this.gravite = rendezVous.getGravite();
        this.dateIntervention = LocalDateTime.of(rendezVous.getDate(), rendezVous.getHeure());
        this.rendezVous = rendezVous;

    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateIntervention() { return dateIntervention; }
    public void setDateIntervention(LocalDateTime dateIntervention) { this.dateIntervention = dateIntervention; }

    public String getGravite() { return gravite; }
    public void setGravite(String gravite) { this.gravite = gravite; }

    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }


    @Override
    public String toString() {
        return "InterventionUrgence{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", dateIntervention=" + dateIntervention +
                ", gravite='" + gravite + '\'' +
                '}';
    }
}
