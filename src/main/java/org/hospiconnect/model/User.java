package org.hospiconnect.model;

import java.sql.Date;
import java.time.LocalDate;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;



    private String password;
    private String roles;
    private String statut;
    private String photo;
    private String Groupe_Sanguin;
    private String Tel;
    private String Sexe;
    private String ZipCode;
    private String Adresse;
    private String Gouvernorat;
    private float poids;
    private float taille;
    private float imc;
    private String diplome;
    private String A2F;

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getA2F() {
        return A2F;
    }

    public void setA2F(String a2F) {
        A2F = a2F;
    }

    public String getFullName() {
        return prenom + " " + nom;
    }

    public String getSexe() {
        return Sexe;
    }

    public void setSexe(String sexe) {
        Sexe = sexe;
    }



    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }



    public String getGroupe_Sanguin() {
        return Groupe_Sanguin;
    }

    public void setGroupe_Sanguin(String groupe_Sanguin) {
        Groupe_Sanguin = groupe_Sanguin;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getAdresse() {
        return Adresse;
    }

    public void setAdresse(String adresse) {
        Adresse = adresse;
    }

    public String getGouvernorat() {
        return Gouvernorat;
    }

    public void setGouvernorat(String gouvernorat) {
        Gouvernorat = gouvernorat;
    }

    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public float getTaille() {
        return taille;
    }

    public void setTaille(float taille) {
        this.taille = taille;
    }

    public float getImc() {
        return imc;
    }

    public void setImc(float imc) {
        this.imc = imc;
    }

    public User() {
        // Constructeur par défaut nécessaire pour instancier via `new User()`
    }

    // Constructeur sans ID (pour inscription)
    public User(String nom, String prenom, String email, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roles = "[\"CLIENT\"]"; // chaîne JSON valide
        this.statut = "active";     // valeur par défaut
    }

    public User(int id, String nom, String prenom, String email, String password, String roles, String statut, String photo, String groupe_Sanguin, String tel, String sexe, String zipCode, String adresse, String gouvernorat, float poids, float taille, float imc, String diplome, String a2F) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.statut = statut;
        this.photo = photo;
        Groupe_Sanguin = groupe_Sanguin;
        Tel = tel;
        Sexe = sexe;
        ZipCode = zipCode;
        Adresse = adresse;
        Gouvernorat = gouvernorat;
        this.poids = poids;
        this.taille = taille;
        this.imc = imc;
        this.diplome = diplome;
        A2F = a2F;
    }

    public User(String nom, String prenom, String email, String password, String roles, String statut, String photo, String groupe_Sanguin, String tel, String sexe, String zipCode, String adresse, String gouvernorat, float poids, float taille, float imc, String diplome, String a2F) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.statut = statut;
        this.photo = photo;
        Groupe_Sanguin = groupe_Sanguin;
        Tel = tel;
        Sexe = sexe;
        ZipCode = zipCode;
        Adresse = adresse;
        Gouvernorat = gouvernorat;
        this.poids = poids;
        this.taille = taille;
        this.imc = imc;
        this.diplome = diplome;
        A2F = a2F;
    }

    //ADDUSER
    public User(String nom, String prenom, String email, String password, String roles, String statut, String photo, String groupe_Sanguin, String tel, String sexe, String zipCode, String adresse, String gouvernorat, float poids, float taille, float imc) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.statut = statut;
        this.photo = photo;
        Groupe_Sanguin = groupe_Sanguin;
        Tel = tel;
        Sexe = sexe;
        ZipCode = zipCode;
        Adresse = adresse;
        Gouvernorat = gouvernorat;
        this.poids = poids;
        this.taille = taille;
        this.imc = imc;
    }



    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRoles() { return roles; }
    public String getStatut() { return statut; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRoles(String roles) { this.roles = roles; }
    public void setStatut(String statut) { this.statut = statut; }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", statut='" + statut + '\'' +
                ", photo='" + photo + '\'' +
                ", Groupe_Sanguin='" + Groupe_Sanguin + '\'' +
                ", Tel='" + Tel + '\'' +
                ", Sexe='" + Sexe + '\'' +
                ", ZipCode='" + ZipCode + '\'' +
                ", Adresse='" + Adresse + '\'' +
                ", Gouvernorat='" + Gouvernorat + '\'' +
                ", poids=" + poids +
                ", taille=" + taille +
                ", imc=" + imc +
                '}';
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photoProfil) {
        this.photo = photoProfil;
    }

    public char[] getIMC() {
        // Vérification des valeurs du poids et de la taille
        if (this.poids <= 0 || this.taille <= 0) {
            return "Invalid".toCharArray();  // Retourne "Invalid" si les valeurs sont incorrectes
        }

        // Calcul de l'IMC
        double imc = this.poids / (this.taille * this.taille);  // IMC = Poids / (Taille^2)

        // Formater l'IMC pour afficher avec 2 décimales
        String imcString = String.format("%.2f", imc);

        // Retourner l'IMC sous forme de tableau de caractères
        return imcString.toCharArray();
    }



}
