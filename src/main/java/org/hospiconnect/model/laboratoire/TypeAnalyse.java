package org.hospiconnect.model.laboratoire;

public class TypeAnalyse {

    private Long id;
    private String libelle;
    private String nom;
    private Float prix;

    public TypeAnalyse() {}

    public TypeAnalyse(Long id, String libelle, String nom, Float prix) {
        this.id = id;
        this.libelle = libelle;
        this.nom = nom;
        this.prix = prix;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Float getPrix() {
        return prix;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Type Analyse{" +
                "id=" + id +
                ", libelle=" + libelle +
                ", nom=" + nom +
                ", prix=" + prix +
                '}';
    }
}
