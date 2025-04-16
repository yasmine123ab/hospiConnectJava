package org.hospiconnect.model;

import java.sql.Date;

public class Dons {
    private int id;
    private String typeDon;
    private Double montant;
    private String description;
    private Date dateDon;
    private Boolean disponibilite;

    // Option 1 : seulement l'id
    private int donateurId;

    // Option 2 : objet complet (si tu veux plus d’infos sur le donateur)
    private user donateur;

    public Dons() {}

    public Dons(int id, String typeDon, Double montant, String description, Date dateDon, Boolean disponibilite, int donateurId) {
        this.id = id;
        this.typeDon = typeDon;
        this.montant = montant;
        this.description = description;
        this.dateDon = dateDon;
        this.disponibilite = disponibilite;
        this.donateurId = donateurId;
    }

    // 🔸 Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeDon() {
        return typeDon;
    }

    public void setTypeDon(String typeDon) {
        this.typeDon = typeDon;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateDon() {
        return dateDon;
    }

    public void setDateDon(Date dateDon) {
        this.dateDon = dateDon;
    }

    public Boolean getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(Boolean disponibilite) {
        this.disponibilite = disponibilite;
    }


    public int getDonateurId() {
        return donateurId;
    }

    public void setDonateurId(int donateurId) {
        this.donateurId = donateurId;
    }

    public user getDonateur() {
        return donateur;
    }

    public void setDonateur(user donateur) {
        this.donateur = donateur;
    }

    @Override
    public String toString() {
        return "Don{" +
                "id=" + id +
                ", typeDon='" + typeDon + '\'' +
                ", montant=" + montant +
                ", description='" + description + '\'' +
                ", dateDon=" + dateDon +
                ", disponibilite=" + disponibilite +
                ", donateurId=" + donateurId +
                ", donateur=" + (donateur != null ? donateur.getNom() : "null") +
                '}';
    }
}
