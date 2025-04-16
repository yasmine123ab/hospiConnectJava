package org.hospiconnect.model;
import java.sql.Date;

public class DemandesDons {
    private int id;
    private String typeBesoin;
    private String details;
    private Date dateDemande;
    private String statut;

    // 🔸 Option 1 : juste l'id du patient
    private int patientId;

    // 🔸 Option 2 : objet User complet si besoin
    private User patient;

    public DemandesDons() {}

    // Constructeur sans User
    public DemandesDons(int id, String typeBesoin, String details, Date dateDemande, String statut, int patientId) {
        this.id = id;
        this.typeBesoin = typeBesoin;
        this.details = details;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.patientId = patientId;
    }

    // Getters et setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeBesoin() {
        return typeBesoin;
    }

    public void setTypeBesoin(String typeBesoin) {
        this.typeBesoin = typeBesoin;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "DemandeDon{" +
                "id=" + id +
                ", typeBesoin='" + typeBesoin + '\'' +
                ", details='" + details + '\'' +
                ", dateDemande=" + dateDemande +
                ", statut='" + statut + '\'' +
                ", patientId=" + patientId +
                ", patient=" + (patient != null ? patient.getNom() : "null") +
                '}';
    }
}
