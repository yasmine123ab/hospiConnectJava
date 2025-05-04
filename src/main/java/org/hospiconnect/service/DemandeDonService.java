package org.hospiconnect.service;

import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.User;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DemandeDonService implements ICrud<DemandesDons> {
    private Connection con;
    private static DemandeDonService instance;
    // Méthode statique pour obtenir l'instance unique
    public static DemandeDonService getInstance() {
        if (instance == null) {
            instance = new DemandeDonService();
        }
        return instance;
    }

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
    /** Statistiques : compte par type de besoin */
    public Map<String, Integer> getDemandeStatisticsByTypeBesoin() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT type_besoin, COUNT(*) AS total FROM demandes_dons GROUP BY type_besoin";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("type_besoin"),
                        rs.getInt("total"));
            }
        }
        return stats;
    }
    /** Statistiques : compte par statut */
    public Map<String, Integer> getDemandeStatisticsByStatut() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT statut, COUNT(*) AS total FROM demandes_dons GROUP BY statut";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("statut"),
                        rs.getInt("total"));
            }
        }
        return stats;
    }
    public boolean isDemandeLinkedToAttribution(DemandesDons demande) throws SQLException {
        String query = "SELECT COUNT(*) FROM attributions_dons WHERE demande_id = ?";  // Supposez que la table d'attribution ait une colonne `demande_id`

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, demande.getId()); // Utilisez l'ID de la demande
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Si le nombre d'enregistrements est supérieur à 0, cela signifie qu'il est lié à une attribution
            }
        }
        return false;
    }



}
