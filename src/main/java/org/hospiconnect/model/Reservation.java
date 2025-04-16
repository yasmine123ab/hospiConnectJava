package org.hospiconnect.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private LocalDateTime dureeTraitement;
    private String nomMedicament;
    private int nombresFois;

    public Reservation() {
    }

    public Reservation(int id, LocalDateTime dureeTraitement, String nomMedicament, int nombresFois) {
        this.id = id;
        this.dureeTraitement = dureeTraitement;
        this.nomMedicament = nomMedicament;
        this.nombresFois = nombresFois;
    }


    public Reservation(LocalDateTime dureeTraitement, String nomMedicament, int nombresFois) {
        this.dureeTraitement = dureeTraitement;
        this.nomMedicament = nomMedicament;
        this.nombresFois = nombresFois;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDureeTraitement() {
        return dureeTraitement;
    }

    public void setDureeTraitement(LocalDateTime dureeTraitement) {
        this.dureeTraitement = dureeTraitement;
    }

    public String getNomMedicament() {
        return nomMedicament;
    }

    public void setNomMedicament(String nomMedicament) {
        this.nomMedicament = nomMedicament;
    }

    public int getNombresFois() {
        return nombresFois;
    }

    public void setNombresFois(int nombresFois) {
        this.nombresFois = nombresFois;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", dureeTraitement=" + dureeTraitement +
                ", nomMedicament='" + nomMedicament + '\'' +
                ", nombresFois=" + nombresFois +
                '}';
    }
}
