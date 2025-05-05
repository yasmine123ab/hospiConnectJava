package org.hospiconnect.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private LocalDateTime dureeTraitement;
    private String nomMedicament;
    private int nombresFois;
    private String note; // AI-generated notes about the medication

    public Reservation() {
    }

    public Reservation(int id, LocalDateTime dureeTraitement, String nomMedicament, int nombresFois) {
        this.id = id;
        this.dureeTraitement = dureeTraitement;
        this.nomMedicament = nomMedicament;
        this.nombresFois = nombresFois;
    }
    
    public Reservation(int id, LocalDateTime dureeTraitement, String nomMedicament, int nombresFois, String note) {
        this.id = id;
        this.dureeTraitement = dureeTraitement;
        this.nomMedicament = nomMedicament;
        this.nombresFois = nombresFois;
        this.note = note;
    }

    // Constructor without ID for new reservations
    public Reservation(LocalDateTime dureeTraitement, String nomMedicament, int nombresFois) {
        this.dureeTraitement = dureeTraitement;
        this.nomMedicament = nomMedicament;
        this.nombresFois = nombresFois;
    }
    
    public Reservation(LocalDateTime dureeTraitement, String nomMedicament, int nombresFois, String note) {
        this.dureeTraitement = dureeTraitement;
        this.nomMedicament = nomMedicament;
        this.nombresFois = nombresFois;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", dureeTraitement=" + dureeTraitement +
                ", nomMedicament='" + nomMedicament + '\'' +
                ", nombresFois=" + nombresFois +
                ", note='" + note + '\'' +
                '}';
    }
}
