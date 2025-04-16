package org.hospiconnect.model;
import java.sql.Date;
public class AttributionsDons {
    private int id;
    private Date dateAttribution;
    private String statut;

    // Champs de clés étrangères
    private int donId;
    private int demandeId;
    private int beneficiaireId;

    // Objets liés (facultatifs mais pratiques)
    private Dons don;
    private DemandesDons demande;
    private User beneficiaire;

    public AttributionsDons() {}

    public AttributionsDons(int id, Date dateAttribution, String statut, int donId, int demandeId, int beneficiaireId) {
        this.id = id;
        this.dateAttribution = dateAttribution;
        this.statut = statut;
        this.donId = donId;
        this.demandeId = demandeId;
        this.beneficiaireId = beneficiaireId;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(Date dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getDonId() {
        return donId;
    }

    public void setDonId(int donId) {
        this.donId = donId;
    }

    public int getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(int demandeId) {
        this.demandeId = demandeId;
    }

    public int getBeneficiaireId() {
        return beneficiaireId;
    }

    public void setBeneficiaireId(int beneficiaireId) {
        this.beneficiaireId = beneficiaireId;
    }

    public Dons getDon() {
        return don;
    }

    public void setDon(Dons don) {
        this.don = don;
    }

    public DemandesDons getDemande() {
        return demande;
    }

    public void setDemande(DemandesDons demande) {
        this.demande = demande;
    }

    public User getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(User beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    @Override
    public String toString() {
        return "AttributionDon{" +
                "id=" + id +
                ", dateAttribution=" + dateAttribution +
                ", statut='" + statut + '\'' +
                ", donId=" + donId +
                ", demandeId=" + demandeId +
                ", beneficiaireId=" + beneficiaireId +
                ", donateur=" + (don != null && don.getDonateur() != null ? don.getDonateur().getNom() : "null") +
                ", demande=" + (demande != null && demande.getPatient() != null ? demande.getPatient().getNom() : "null") +
                ", beneficiaire=" + (beneficiaire != null ? beneficiaire.getNom() : "null") +
                '}';
    }
}
