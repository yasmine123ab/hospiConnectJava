package org.hospiconnect.service;

import org.hospiconnect.model.RendezVous;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousService implements ICrud<RendezVous> {

    @Override
    public int insert(RendezVous rdv) throws SQLException {
        String query = "INSERT INTO rendez_vous (patient_id, medecin_id, date_rendezvous, heure_rendezvous, type_rendezvous, nom, prenom, num_tel_patient, mail_patient, gravite, commentaire) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, null); // patient_id (null pour l'instant)
            stmt.setObject(2, null); // medecin_id (null pour l'instant)
            stmt.setDate(3, DatabaseUtils.toSqlDate(rdv.getDate()));
            stmt.setTime(4, rdv.getHeure() != null ? Time.valueOf(rdv.getHeure()) : null);
            stmt.setString(5, rdv.getType());
            stmt.setString(6, rdv.getNom());
            stmt.setString(7, rdv.getPrenom());
            stmt.setString(8, rdv.getTelephone());
            stmt.setString(9, rdv.getEmail());
            stmt.setString(10, rdv.getGravite());
            stmt.setString(11, rdv.getCommentaire());

            stmt.executeUpdate();

            // Récupérer l'id généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Échec de l'insertion : aucun ID généré.");
                }
            }
        }
    }

    @Override
    public void update(RendezVous rdv) throws SQLException {
        String query = "UPDATE rendez_vous SET patient_id = ?, medecin_id = ?, date_rendezvous = ?, heure_rendezvous = ?, type_rendezvous = ?, nom = ?, prenom = ?, num_tel_patient = ?, mail_patient = ?, gravite = ?, commentaire = ? WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, null); // patient_id
            stmt.setObject(2, null); // medecin_id
            stmt.setDate(3, DatabaseUtils.toSqlDate(rdv.getDate()));
            stmt.setTime(4, rdv.getHeure() != null ? Time.valueOf(rdv.getHeure()) : null);
            stmt.setString(5, rdv.getType());
            stmt.setString(6, rdv.getNom());
            stmt.setString(7, rdv.getPrenom());
            stmt.setString(8, rdv.getTelephone());
            stmt.setString(9, rdv.getEmail());
            stmt.setString(10, rdv.getGravite());
            stmt.setString(11, rdv.getCommentaire());
            stmt.setInt(12, rdv.getId()); // correction ici: c'était 13 par erreur
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(RendezVous rdv) throws SQLException {
        String query = "DELETE FROM rendez_vous WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, rdv.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<RendezVous> findAll() throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT * FROM rendez_vous";
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RendezVous rdv = new RendezVous();
                rdv.setId(rs.getInt("id"));
                rdv.setDate(rs.getDate("date_rendezvous") != null ? rs.getDate("date_rendezvous").toLocalDate() : null);
                rdv.setHeure(rs.getTime("heure_rendezvous") != null ? rs.getTime("heure_rendezvous").toLocalTime() : null);
                rdv.setType(rs.getString("type_rendezvous"));
                rdv.setNom(rs.getString("nom"));
                rdv.setPrenom(rs.getString("prenom"));
                rdv.setTelephone(rs.getString("num_tel_patient"));
                rdv.setEmail(rs.getString("mail_patient"));
                rdv.setGravite(rs.getString("gravite"));
                rdv.setCommentaire(rs.getString("commentaire"));

                rendezVousList.add(rdv);
            }
            System.out.println("RendezVous récupérés de la base de données : " + rendezVousList.size());
        }
        return rendezVousList;
    }

    public void add(RendezVous rdv) {
    }
}
