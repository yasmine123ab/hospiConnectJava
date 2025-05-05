package org.hospiconnect.model;

import java.time.LocalDate;

public class Consultation {
    private int id;
    private String typeConsultation;
    private LocalDate dateConsultation;
    private String note;
    private String firstname;
    private String lastname;
    private int rating; // Rating from 1-5
    private String email; // Patient email for notifications

    public Consultation() {
        this.rating = 0; // Default rating is 0 (not rated)
    }

    public Consultation(int id, String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname) {
        this.id = id;
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = 0; // Default rating is 0 (not rated)
    }
    
    public Consultation(int id, String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname, int rating) {
        this.id = id;
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = rating;
    }
    
    public Consultation(int id, String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname, int rating, String email) {
        this.id = id;
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = rating;
        this.email = email;
    }

    // Constructor without ID for new consultations
    public Consultation(String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname) {
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = 0; // Default rating is 0 (not rated)
    }
    
    public Consultation(String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname, int rating) {
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = rating;
    }
    
    public Consultation(String typeConsultation, LocalDate dateConsultation, String note, String firstname, String lastname, int rating, String email) {
        this.typeConsultation = typeConsultation;
        this.dateConsultation = dateConsultation;
        this.note = note;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = rating;
        this.email = email;
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
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", typeConsultation='" + typeConsultation + '\'' +
                ", dateConsultation=" + dateConsultation +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", rating=" + rating +
                ", email='" + email + '\'' +
                '}';
    }
}
