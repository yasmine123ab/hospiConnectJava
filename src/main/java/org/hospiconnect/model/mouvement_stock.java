package org.hospiconnect.model;

import java.util.Date;

public class mouvement_stock {
    private int id;
    private int id_materiel_id;
    private int 	id_personnel_id;
    private int qunatite;
    private Date 	date_mouvement;
    private String motif;
    private String type_mouvement;

public mouvement_stock() {
}

public mouvement_stock(int id_materiel_id, int id_personnel_id, int qunatite, Date date_mouvement, String motif, String type_mouvement) {
    this.id_materiel_id = id_materiel_id;
    this.id_personnel_id = id_personnel_id;
    this.qunatite = qunatite;
    this.date_mouvement = date_mouvement;
    this.motif = motif;
    this.type_mouvement = type_mouvement;
}

public mouvement_stock(int id, int id_materiel_id, int id_personnel_id, int qunatite, Date date_mouvement, String motif, String type_mouvement) {
    this.id = id;
    this.id_materiel_id = id_materiel_id;
    this.id_personnel_id = id_personnel_id;
    this.qunatite = qunatite;
    this.date_mouvement = date_mouvement;
    this.motif = motif;
    this.type_mouvement = type_mouvement;
}

// Getters & Setters

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public int getId_materiel_id() {
    return id_materiel_id;
}

public void setId_materiel_id(int id_materiel_id) {
    this.id_materiel_id = id_materiel_id;
}

public int getId_personnel_id() {
    return id_personnel_id;
}

public void setId_personnel_id(int id_personnel_id) {
    this.id_personnel_id = id_personnel_id;
}

public int getQunatite() {
    return qunatite;
}

public void setQunatite(int qunatite) {
    this.qunatite = qunatite;
}

public Date getDate_mouvement() {
    return date_mouvement;
}

public void setDate_mouvement(Date date_mouvement) {
    this.date_mouvement = date_mouvement;
}

public String getMotif() {
    return motif;
}

public void setMotif(String motif) {
    this.motif = motif;
}

public String getType_mouvement() {
    return type_mouvement;
}

public void setType_mouvement(String type_mouvement) {
    this.type_mouvement = type_mouvement;
}

@Override
public String toString() {
    return "MouvementMateriel{" +
            "id=" + id +
            ", id_materiel_id=" + id_materiel_id +
            ", id_personnel_id=" + id_personnel_id +
            ", qunatite=" + qunatite +
            ", date_mouvement=" + date_mouvement +
            ", motif='" + motif + '\'' +
            ", type_mouvement='" + type_mouvement + '\'' +
            '}';
}
}