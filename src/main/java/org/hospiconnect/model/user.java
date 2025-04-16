package org.hospiconnect.model;

import java.time.LocalTime;
import java.util.Date;

public class user {

    private int id;
    private String nom;
    private String prenom;
    private Date date_n;
    private String email;
    private String mdp;
    private Date date_c;
    private String statut_compte;
    private String empreinte;
    private String role;
    private LocalTime inactivite;
    private String groupe_sanguin;
    private String tel;
    private String adresse;
    private String zipcode;
    private String gouvernorat;
    private String Sexe;
    private float poids;
    private float taille;
    private float imc;
    private String img;
    private String reset_token;
    private String block;
    private int tc;
    private String reset_token_tel;



    //CONSTRUCTOR
    public user(String nom, String prenom , Date date_n , String email, String mdp, Date date_c, String statut_compte, String empreinte, String role, String groupe_sanguin, LocalTime inactivite, String tel, String adresse, String zipcode, String gouvernorat, String sexe, float poids, float taille, float imc, String img, String  reset_token , String block, int tc, String reset_token_tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.date_n = date_n;
        this.email = email;
        this.mdp = mdp;
        this.date_c = date_c;
        this.statut_compte = statut_compte;
        this.empreinte = empreinte;
        this.role = role;
        this.groupe_sanguin = groupe_sanguin;
        this.inactivite = inactivite;
        this.tel = tel;
        this.adresse = adresse;
        this.zipcode = zipcode;
        this.gouvernorat = gouvernorat;
        this.Sexe = sexe;
        this.poids = poids;
        this.taille = taille;
        this.imc = imc;
        this.img = img;
        this.reset_token = reset_token;
        this.block = block;
        this.tc = tc;
        this.reset_token_tel = reset_token_tel;
    }
    public user(){}

    public user(String tel) {
        this.tel = tel;
    }


    //GETTERS & SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDate_n() {
        return date_n;
    }

    public void setDate_n(Date date_n) {
        this.date_n = date_n;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public Date getDate_c() {
        return date_c;
    }

    public void setDate_c(Date date_c) {
        this.date_c = date_c;
    }

    public String getStatut_compte() {
        return statut_compte;
    }

    public void setStatut_compte(String statut_compte) {
        this.statut_compte = statut_compte;
    }

    public String getEmpreinte() {
        return empreinte;
    }

    public void setEmpreinte(String empreinte) {
        this.empreinte = empreinte;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalTime getInactivite() {
        return inactivite;
    }

    public void setInactivite(LocalTime inactivite) {
        this.inactivite = inactivite;
    }

    public String getGroupe_sanguin() {
        return groupe_sanguin;
    }

    public void setGroupe_sanguin(String groupe_sanguin) {
        this.groupe_sanguin = groupe_sanguin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getGouvernorat() {
        return gouvernorat;
    }

    public void setGouvernorat(String gouvernorat) {
        this.gouvernorat = gouvernorat;
    }

    public String getSexe() {
        return Sexe;
    }

    public void setSexe(String sexe) {
        Sexe = sexe;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getReset_token() {
        return reset_token;
    }

    public void setReset_token(String reset_token) {
        this.reset_token = reset_token;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getTc() {
        return tc;
    }

    public void setTc(int tc) {
        this.tc = tc;
    }

    public String getReset_token_tel() {
        return reset_token_tel;
    }

    public void setReset_token_tel(String reset_token_tel) {
        this.reset_token_tel = reset_token_tel;
    }


    //TOSTRING


    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", date_n=" + date_n +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", date_c=" + date_c +
                ", statut_compte='" + statut_compte + '\'' +
                ", empreinte='" + empreinte + '\'' +
                ", role='" + role + '\'' +
                ", inactivite=" + inactivite +
                ", groupe_sanguin='" + groupe_sanguin + '\'' +
                ", tel='" + tel + '\'' +
                ", adresse='" + adresse + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", gouvernorat='" + gouvernorat + '\'' +
                ", Sexe='" + Sexe + '\'' +
                ", poids=" + poids +
                ", taille=" + taille +
                ", imc=" + imc +
                ", img='" + img + '\'' +
                ", reset_token='" + reset_token + '\'' +
                ", block='" + block + '\'' +
                ", tc=" + tc +
                ", reset_token_tel='" + reset_token_tel + '\'' +
                '}';
    }
}
