package org.hospiconnect.model.laboratoire;

import java.time.LocalDateTime;

public class DisponibiliteAnalyse {

    private Long id;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private Long nbrPlaces;

    public DisponibiliteAnalyse() {
    }

    public DisponibiliteAnalyse(Long id, LocalDateTime debut, LocalDateTime fin, Long nbrPlaces) {
        this.id = id;
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

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public Long getNbrPlaces() {
        return nbrPlaces;
    }

    public void setNbrPlaces(Long nbrPlaces) {
        this.nbrPlaces = nbrPlaces;
    }

    @Override
    public String toString() {
        return "DisponibiliteAnalyse{" +
                "id=" + id +
                ", debut=" + debut +
                ", fin=" + fin +
                ", nbrPlaces=" + nbrPlaces +
                '}';
    }
}
