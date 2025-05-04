package org.hospiconnect.service;

import org.hospiconnect.model.InterventionUrgence;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;

public class InterventionUrgenceService {

    // Dans InterventionUrgenceService.java
    public void insertWithConnection(InterventionUrgence intervention, Connection conn) throws SQLException {
        String query = "INSERT INTO intervention_urgence (nom, prenom, commentaire, date_intervention, gravite, rendez_vous_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, intervention.getNom());
            stmt.setString(2, intervention.getPrenom());
            stmt.setString(3, intervention.getCommentaire());
            stmt.setTimestamp(4, DatabaseUtils.toSqlTimestamp(intervention.getDateIntervention()));
            stmt.setString(5, intervention.getGravite());
            stmt.setLong(6, intervention.getRendezVous().getId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    intervention.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public void deleteByRendezVousId(int rendezVousId, Connection conn) throws SQLException {
        String query = "DELETE FROM intervention_urgence WHERE rendez_vous_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rendezVousId);
            stmt.executeUpdate();
        }
    }
}