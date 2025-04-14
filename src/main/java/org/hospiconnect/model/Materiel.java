package org.hospiconnect.model;

import java.util.Date;

public class Materiel {
    private int id;
    private int quantite;
    private String nom;
    private String categorie;
    private String etat;
    private String emplacement;
    private Date date_ajout;

    public Materiel() {
    }

    public Materiel(int quantite, String nom, String categorie, String etat, String emplacement, Date date_ajout) {
        this.quantite = quantite;
        this.nom = nom;
        this.categorie = categorie;
        this.etat = etat;
        this.emplacement = emplacement;
        this.date_ajout = date_ajout;
    }

    public Materiel(int id, int quantite, String nom, String categorie, String etat, String emplacement, Date date_ajout) {
        this.id = id;
        this.quantite = quantite;
        this.nom = nom;
        this.categorie = categorie;
        this.etat = etat;
        this.emplacement = emplacement;
        this.date_ajout = date_ajout;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public Date getDate_ajout() {
        return date_ajout;
    }

    public void setDate_ajout(Date date_ajout) {
        this.date_ajout = date_ajout;
    }

    @Override
    public String toString() {
        return "Materiel{" +
                "id=" + id +
                ", quantite=" + quantite +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", etat='" + etat + '\'' +
                ", emplacement='" + emplacement + '\'' +
                ", date_ajout=" + date_ajout +
                '}';
    }
}
