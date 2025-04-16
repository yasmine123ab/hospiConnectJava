package org.hospiconnect.service;

import org.hospiconnect.model.Materiel;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MaterielService implements ICrud<Materiel> {
    private Connection con;

    public MaterielService() {
        try {
            this.con = DatabaseUtils.getConnection(); // ou MyDb.getInstance().getConnection();
            // ton code avec la connexion ici
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void insert(Materiel obj) throws SQLException {
        String sql = "INSERT INTO materiel(nom,categorie,etat,quantite,emplacement,date_ajout) VALUES('" + obj.getNom() + "','" + obj.getCategorie() + "','" + obj.getEtat() + "','" + obj.getQuantite() + "','" + obj.getEmplacement() + "','" + obj.getDate_ajout() + "')";
        Statement stmt = this.con.createStatement();
        stmt.executeUpdate(sql);
    }

    @Override
    public void update(Materiel obj) throws SQLException {
        String sql = "UPDATE SET materiel VALUES('" + obj.getNom() + "','" + obj.getCategorie() + "','" + obj.getEtat() + "','" + obj.getQuantite() + "'," + obj.getEmplacement() + "','" + obj.getDate_ajout() + "' where id = '" + obj.getId() + " ";
        Statement stmt = this.con.createStatement();
        stmt.executeUpdate(sql);
    }

    @Override
    public void delete(Materiel obj) throws SQLException {
        String sql = "DELETE FROM materiel WHERE id = '" + obj.getId() + "'";
        Statement stmt = this.con.createStatement();
        stmt.executeUpdate(sql);
    }

    @Override
    public List<Materiel> findAll() throws SQLException {
        String sql = "SELECT * FROM materiel";
        Statement stmt = this.con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Materiel> list = new ArrayList<>();
        while (rs.next()) {
            Materiel materiel = new Materiel();
            materiel.setId(rs.getInt(1));
            materiel.setNom(rs.getString(2));
            materiel.setCategorie(rs.getString(3));
            materiel.setEtat(rs.getString(4));
            materiel.setQuantite(rs.getInt(5));
            materiel.setEmplacement(rs.getString(6));
            materiel.setDate_ajout(rs.getDate(7));
            list.add(materiel);
        }

        return list;
    }

}
