package org.hospiconnect.model;

import java.time.LocalDateTime;

public class Operation {
    private Long id;
    private String commentaire;
    private LocalDateTime dateOperation;
    private String gravite;
    private RendezVous rendezVous;

    // Constructeurs
    public Operation() {}

    public Operation(Long id, String commentaire, LocalDateTime dateOperation, String gravite, RendezVous rendezVous) {
        this.id = id;
        this.commentaire = commentaire;
        this.dateOperation = dateOperation;
        this.gravite = gravite;
        this.rendezVous = rendezVous;
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

    public LocalDateTime getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDateTime dateOperation) {
        this.dateOperation = dateOperation;
    }

    public String getGravite() {
        return gravite;
    }

    public void setGravite(String gravite) {
        this.gravite = gravite;
    }

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", commentaire='" + commentaire + '\'' +
                ", dateOperation=" + dateOperation +
                ", gravite='" + gravite + '\'' +
                ", rendezVous=" + rendezVous +
                '}';
    }
}
