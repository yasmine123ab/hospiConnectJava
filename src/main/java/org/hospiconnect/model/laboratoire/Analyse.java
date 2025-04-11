package org.hospiconnect.model.laboratoire;

import java.time.LocalDate;

public class Analyse {

    private Long id;
    private Long idRdv;
    private Long idPatient;
    private Long idPersonnel;
    private String etat;
    private Long idTypeAnalyse;
    private String resultat;
    private LocalDate dateResultat;
    private LocalDate datePrelevement;

    public Analyse() {
    }

    public Analyse(
            Long id,
            Long idRdv,
            Long idPatient,
            Long idPersonnel,
            String etat,
            Long idTypeAnalyse,
            String resultat,
            LocalDate dateResultat,
            LocalDate datePrelevement
    ) {
        this.id = id;
        this.idRdv = idRdv;
        this.idPatient = idPatient;
        this.idPersonnel = idPersonnel;
        this.etat = etat;
        this.idTypeAnalyse = idTypeAnalyse;
        this.resultat = resultat;
        this.dateResultat = dateResultat;
        this.datePrelevement = datePrelevement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdRdv() {
        return idRdv;
    }

    public void setIdRdv(Long idRdv) {
        this.idRdv = idRdv;
    }

    public Long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Long idPatient) {
        this.idPatient = idPatient;
    }

    public Long getIdPersonnel() {
        return idPersonnel;
    }

    public void setIdPersonnel(Long idPersonnel) {
        this.idPersonnel = idPersonnel;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Long getIdTypeAnalyse() {
        return idTypeAnalyse;
    }

    public void setIdTypeAnalyse(Long idTypeAnalyse) {
        this.idTypeAnalyse = idTypeAnalyse;
    }

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public LocalDate getDateResultat() {
        return dateResultat;
    }

    public void setDateResultat(LocalDate dateResultat) {
        this.dateResultat = dateResultat;
    }

    public LocalDate getDatePrelevement() {
        return datePrelevement;
    }

    public void setDatePrelevement(LocalDate datePrelevement) {
        this.datePrelevement = datePrelevement;
    }

    @Override
    public String toString() {
        return "Analyse{" +
                "id=" + id +
                ", idRdv=" + idRdv +
                ", idPatient=" + idPatient +
                ", idPersonnel=" + idPersonnel +
                ", etat='" + etat + '\'' +
                ", idTypeAnalyse=" + idTypeAnalyse +
                ", resultat='" + resultat + '\'' +
                ", dateResultat=" + dateResultat +
                ", datePrelevement=" + datePrelevement +
                '}';
    }

}
