package org.hospiconnect.model;

import java.time.LocalDate;

public class Consultation {
    private int id;
    private String typeConsultation;
    private LocalDate dateConsultation;
    private String note;
    private String firstname;
    private String lastname;

    public Consultation() {
    }

    public Consultation(int id, String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname) {
        this.id = id;
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
    }


    public Consultation(String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname) {
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeConsultation() {
        return typeConsultation;
    }

    public void setTypeConsultation(String typeConsultation) {
        this.typeConsultation = typeConsultation;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDate dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", typeConsultation='" + typeConsultation + '\'' +
                ", dateConsultation=" + dateConsultation +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
