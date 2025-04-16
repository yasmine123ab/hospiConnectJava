package org.hospiconnect.service;

import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.User;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DemandeDonService implements ICrud<DemandesDons> {
    private Connection con;

    public DemandeDonService() {
        try {
            this.con = DatabaseUtils.getConnection(); // Connexion à la base de données via DatabaseUtils
        } catch (SQLException e) {
            e.printStackTrace(); // Gestion des erreurs de connexion
        }
    }

    @Override
    public void insert(DemandesDons obj) throws SQLException {
        String sql = "INSERT INTO demandes_dons(type_besoin, details, date_demande, statut, patient_id) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, obj.getTypeBesoin());
        ps.setString(2, obj.getDetails());
        ps.setDate(3, obj.getDateDemande());
        ps.setString(4, obj.getStatut());
        ps.setInt(5, obj.getPatientId());
        ps.executeUpdate();
    }

    @Override
    public void update(DemandesDons obj) throws SQLException {
        String sql = "UPDATE demandes_dons SET type_besoin=?, details=?, date_demande=?, statut=?, patient_id=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, obj.getTypeBesoin());
        ps.setString(2, obj.getDetails());
        ps.setDate(3, obj.getDateDemande());
        ps.setString(4, obj.getStatut());
        ps.setInt(5, obj.getPatientId());
        ps.setInt(6, obj.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(DemandesDons obj) throws SQLException {
        String sql = "DELETE FROM demandes_dons WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, obj.getId());
        ps.executeUpdate();
    }

    @Override
    public List<DemandesDons> findAll() throws SQLException {
        String sql = "SELECT d.*, u.id AS patient_id, u.nom, u.prenom FROM demandes_dons d JOIN user u ON d.patient_id = u.id";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<DemandesDons> list = new ArrayList<>();

        while (rs.next()) {
            DemandesDons demande = new DemandesDons();
            demande.setId(rs.getInt("id"));
            demande.setTypeBesoin(rs.getString("type_besoin"));
            demande.setDetails(rs.getString("details"));
            demande.setDateDemande(rs.getDate("date_demande"));
            demande.setStatut(rs.getString("statut"));

            // Création d'un objet user pour le patient

            User patient = new User();
            patient.setId(rs.getInt("patient_id"));
            patient.setNom(rs.getString("nom"));
            patient.setPrenom(rs.getString("prenom"));

            // Affectation du patient à la demande
            demande.setPatient(patient);

            list.add(demande);
        }
        return list;
    }

}
