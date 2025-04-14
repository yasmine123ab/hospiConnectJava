package org.hospiconnect.service;

import org.hospiconnect.model.Materiel;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class MaterielService1 implements Icrud<Materiel>  {
    private Connection con;

    public MaterielService1() {
        try {
            this.con = DatabaseUtils.getConnection(); // ou MyDb.getInstance().getConnection();
            // ton code avec la connexion ici
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void insert(Materiel obj) throws SQLException {
        String sql = "INSERT INTO materiel(nom,categorie,etat,quantite,emplacement,date_ajout) VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(4, obj.getQuantite());
        ps.setString(1, obj.getNom());
        ps.setString(2, obj.getCategorie());
        ps.setString(3, obj.getEtat());
        ps.setString(5, obj.getEmplacement());
        ps.setDate(6, new java.sql.Date(obj.getDate_ajout().getTime()));

        ps.executeUpdate();
    }
    @Override
    public void update(Materiel obj) throws SQLException {
        String sql = "UPDATE materiel SET nom=?, categorie=?, etat=?, quantite=?, emplacement=?, date_ajout=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, obj.getNom());
        ps.setString(2, obj.getCategorie());
        ps.setString(3, obj.getEtat());
        ps.setInt(4, obj.getQuantite());
        ps.setString(5, obj.getEmplacement());
        ps.setDate(6, new java.sql.Date(obj.getDate_ajout().getTime())); // Conversion de java.util.Date → java.sql.Date
        ps.setInt(7, obj.getId()); // L'ID pour identifier quel materiel mettre à jour
        ps.executeUpdate();
    }
    @Override
    public void delete(Materiel obj) throws SQLException {
        String sql = "DELETE FROM materiel WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, obj.getId());
        ps.executeUpdate();
    }
    @Override
    public List<Materiel> findAll() throws SQLException {
        String sql = "SELECT * FROM materiel";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Materiel> list = new ArrayList<>();
        while (rs.next()) {
            Materiel materiel = new Materiel();
            materiel.setId(rs.getInt("id"));
            materiel.setNom(rs.getString("nom"));
            materiel.setCategorie(rs.getString("categorie"));
            materiel.setEtat(rs.getString("etat"));
            materiel.setQuantite(rs.getInt("quantite"));
            materiel.setEmplacement(rs.getString("emplacement"));
            materiel.setDate_ajout(rs.getDate("date_ajout")); // Retourne un java.sql.Date
            list.add(materiel);
        }
        return list;
    }


}
