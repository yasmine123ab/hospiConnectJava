package org.hospiconnect.model;

import java.time.LocalDateTime;

public class InterventionUrgence {

    private Long id;
    private String commentaire;
    private LocalDateTime dateIntervention;
    private String gravite;

    // Constructeurs
    public InterventionUrgence() {
    }

    public InterventionUrgence(Long id, String commentaire, LocalDateTime dateIntervention, String gravite) {
        this.id = id;
        this.commentaire = commentaire;
        this.dateIntervention = dateIntervention;
        this.gravite = gravite;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateIntervention() {
        return dateIntervention;
    }

    public void setDateIntervention(LocalDateTime dateIntervention) {
        this.dateIntervention = dateIntervention;
    }

    public String getGravite() {
        return gravite;
    }

    public void setGravite(String gravite) {
        this.gravite = gravite;
    }

    @Override
    public String toString() {
        return "InterventionUrgence{" +
                "id=" + id +
                ", commentaire='" + commentaire + '\'' +
                ", dateIntervention=" + dateIntervention +
                ", gravite='" + gravite + '\'' +
                '}';
    }
}
