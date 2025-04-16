package org.hospiconnect.service;

import org.hospiconnect.model.RendezVous;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RendezVousService implements ICrud<RendezVous> {

    @Override
    public void insert(RendezVous rdv) throws SQLException {
        String query = "INSERT INTO rendez_vous (patient_id, medecin_id, date_rendezvous, heure_rendezvous, type_rendezvous, nom, prenom, numtel_patient, mail_patient, gravite, commentaire, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, null, Types.INTEGER); // patient_id
            stmt.setObject(2, null, Types.INTEGER); // medecin_id
            stmt.setDate(3, DatabaseUtils.toSqlDate(rdv.getDate()));
            stmt.setTime(4, Time.valueOf(rdv.getHeure()));
            stmt.setString(5, rdv.getType());
            stmt.setString(6, rdv.getNom());
            stmt.setString(7, rdv.getPrenom());
            stmt.setString(8, rdv.getTelephone());
            stmt.setString(9, rdv.getEmail());
            stmt.setString(10, rdv.getGravite());
            stmt.setString(11, rdv.getCommentaire());
            stmt.setString(12, rdv.getStatut());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(RendezVous rdv) throws SQLException {
        String query = "UPDATE rendez_vous SET patient_id = ?, medecin_id = ?, date_rendezvous = ?, heure_rendezvous = ?, type_rendezvous = ?, nom = ?, prenom = ?, numtel_patient = ?, mail_patient = ?, gravite = ?, commentaire = ?, statut = ? WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, null, Types.INTEGER); // patient_id
            stmt.setObject(2, null, Types.INTEGER); // medecin_id
            stmt.setDate(3, DatabaseUtils.toSqlDate(rdv.getDate()));
            stmt.setTime(4, Time.valueOf(rdv.getHeure()));
            stmt.setString(5, rdv.getType());
            stmt.setString(6, rdv.getNom());
            stmt.setString(7, rdv.getPrenom());
            stmt.setString(8, rdv.getTelephone());
            stmt.setString(9, rdv.getEmail());
            stmt.setString(10, rdv.getGravite());
            stmt.setString(11, rdv.getCommentaire());
            stmt.setString(12, rdv.getStatut());
            stmt.setInt(13, rdv.getId());
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
                int id = rs.getInt("id");
                LocalDate date = rs.getDate("date_rendezvous") != null ? rs.getDate("date_rendezvous").toLocalDate() : null;
                LocalTime heure = rs.getTime("heure_rendezvous") != null ? rs.getTime("heure_rendezvous").toLocalTime() : null;
                String type = rs.getString("type_rendezvous");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String telephone = rs.getString("numtel_patient");
                String email = rs.getString("mail_patient");
                String gravite = rs.getString("gravite");
                String commentaire = rs.getString("commentaire");
                String statut = rs.getString("statut");

                RendezVous rdv = new RendezVous(nom, prenom, telephone, email, date, heure, type, gravite, statut, commentaire);
                rdv.setId(id);
                rendezVousList.add(rdv);
            }
        }
        return rendezVousList;
    }
}