package org.hospiconnect.model;

import java.util.Date;

public class MouvementMaterielJoint {
    private int id;
    private String nomMateriel;
    private String nomPersonnel;
    private int quantite;
    private Date dateMouvement;
    private String motif;
    private String typeMouvement;

    public MouvementMaterielJoint(int id, String nomMateriel, int quantite, Date dateMouvement, String motif, String typeMouvement) {
        this.id = id;
        this.nomMateriel = nomMateriel;
        this.nomPersonnel = nomPersonnel;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.motif = motif;
        this.typeMouvement = typeMouvement;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomMateriel() {
        return nomMateriel;
    }

    public void setNomMateriel(String nomMateriel) {
        this.nomMateriel = nomMateriel;
    }

    public String getNomPersonnel() {
        return nomPersonnel;
    }

    public void setNomPersonnel(String nomPersonnel) {
        this.nomPersonnel = nomPersonnel;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Date getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(Date dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(String typeMouvement) {
        this.typeMouvement = typeMouvement;
    }


    // Getters & setters (ajoute si besoin)

    @Override
    public String toString() {
        return nomMateriel + " | nom personnel: " +  nomPersonnel + " | Quantité: " +  quantite + " | Date: " + dateMouvement + " | Motif: " + motif + " | Type: " + typeMouvement;
    }

}
