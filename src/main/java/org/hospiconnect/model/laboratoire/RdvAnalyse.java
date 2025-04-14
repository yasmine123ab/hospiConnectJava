package org.hospiconnect.model.laboratoire;

import java.time.LocalDate;

public class RdvAnalyse {

    private Long id;
    private Long idDisponibilite;
    private Long idPatient;
    private LocalDate dateRdv;
    private String statut;

    public RdvAnalyse() {}

    public RdvAnalyse(Long id, Long idDisponibilite, Long idPatient, LocalDate dateRdv, String statut) {
        this.id = id;
        this.idDisponibilite = idDisponibilite;
        this.idPatient = idPatient;
        this.dateRdv = dateRdv;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDisponibilite() {
        return idDisponibilite;
    }

    public void setIdDisponibilite(Long idDisponibilite) {
        this.idDisponibilite = idDisponibilite;
    }

    public Long getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Long idPatient) {
        this.idPatient = idPatient;
    }

    public LocalDate getDateRdv() {
        return dateRdv;
    }

    public void setDateRdv(LocalDate dateRdv) {
        this.dateRdv = dateRdv;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Rdv Analyse{" +
                "id=" + id +
                ", idDisponibilite=" + idDisponibilite +
                ", idPatient=" + idPatient +
                ", dateRdv=" + dateRdv +
                ", statut='" + statut +
                '}';
    }







}
