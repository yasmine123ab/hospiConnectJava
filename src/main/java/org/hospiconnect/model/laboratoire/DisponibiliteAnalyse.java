package org.hospiconnect.model.laboratoire;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DisponibiliteAnalyse {

    private Long id;
    private LocalDate dispo;
    private LocalTime debut;
    private LocalTime fin;
    private Integer nbrPlaces;

    public DisponibiliteAnalyse() {
    }

    public DisponibiliteAnalyse(Long id,LocalDate dispo, LocalTime debut, LocalTime fin, Integer nbrPlaces) {
        this.id = id;
        this.dispo = dispo;
        this.debut = debut;
        this.fin = fin;
        this.nbrPlaces = nbrPlaces;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDispo() {
        return dispo;
    }

    public void setDispo(LocalDate dispo) {
        this.dispo = dispo;
    }

    public LocalTime getDebut() {
        return debut;
    }

    public void setDebut(LocalTime debut) {
        this.debut = debut;
    }

    public LocalTime getFin() {
        return fin;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }

    public Integer getNbrPlaces() {
        return nbrPlaces;
    }

    public void setNbrPlaces(Integer nbrPlaces) {
        this.nbrPlaces = nbrPlaces;
    }

    @Override
    public String toString() {
        return "DisponibiliteAnalyse{" +
                "id=" + id +
                ", dispo=" + dispo +
                ", debut=" + debut +
                ", fin=" + fin +
                ", nbrPlaces=" + nbrPlaces +
                '}';
    }
}
