package org.hospiconnect.service;


import org.hospiconnect.model.Dons;
import org.hospiconnect.model.user;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DonService implements ICrud<Dons> {
    private Connection con;

    public DonService() {
        try {
            this.con = DatabaseUtils.getConnection(); // Connexion à la base de données via DatabaseUtils
        } catch (SQLException e) {
            e.printStackTrace(); // Gestion des erreurs de connexion
        }
    }

    @Override
    public void insert(Dons don) throws SQLException {
        String sql = "INSERT INTO dons(type_don, montant, description, date_don, disponibilite, donateur_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, don.getTypeDon());
        ps.setDouble(2, don.getMontant());
        ps.setString(3, don.getDescription());
        ps.setDate(4, don.getDateDon());
        ps.setBoolean(5, don.getDisponibilite());
        ps.setInt(6, don.getDonateurId());
        ps.executeUpdate();
    }

    @Override
    public void update(Dons don) throws SQLException {
        String sql = "UPDATE dons SET type_don=?, montant=?, description=?, date_don=?, disponibilite=?, donateur_id=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, don.getTypeDon());
        ps.setDouble(2, don.getMontant());
        ps.setString(3, don.getDescription());
        ps.setDate(4, don.getDateDon());
        ps.setBoolean(5, don.getDisponibilite());
        ps.setInt(6, don.getDonateurId());
        ps.setInt(7, don.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(Dons don) throws SQLException {
        String sql = "DELETE FROM dons WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, don.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Dons> findAll() throws SQLException {
        String sql = "SELECT d.*, u.nom, u.prenom FROM dons d JOIN user u ON d.donateur_id = u.id";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Dons> list = new ArrayList<>();

        while (rs.next()) {
            Dons don = new Dons();
            don.setId(rs.getInt("id"));
            don.setTypeDon(rs.getString("type_don"));
            don.setMontant(rs.getDouble("montant"));
            don.setDescription(rs.getString("description"));
            don.setDateDon(rs.getDate("date_don"));
            don.setDisponibilite(rs.getBoolean("disponibilite"));
            don.setDonateurId(rs.getInt("donateur_id"));

            // Créer l'objet donateur
            user donateur = new user();
            donateur.setId(rs.getInt("donateur_id"));
            donateur.setNom(rs.getString("nom"));
            donateur.setPrenom(rs.getString("prenom"));

            don.setDonateur(donateur);

            list.add(don);
        }

        return list;
    }


}
