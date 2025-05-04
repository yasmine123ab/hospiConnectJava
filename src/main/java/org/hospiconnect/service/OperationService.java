package org.hospiconnect.service;

import org.hospiconnect.model.Operation;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;

public class OperationService {

    // Dans OperationService.java
    public void insertWithConnection(Operation operation, Connection conn) throws SQLException {
        String query = "INSERT INTO operation (nom, prenom, commentaire, date_operation, gravite, rendez_vous_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, operation.getNom());
            stmt.setString(2, operation.getPrenom());
            stmt.setString(3, operation.getCommentaire());
            stmt.setTimestamp(4, DatabaseUtils.toSqlTimestamp(operation.getDateOperation()));
            stmt.setString(5, operation.getGravite());
            stmt.setLong(6, operation.getRendezVous().getId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    operation.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public void deleteByRendezVousId(int rendezVousId, Connection conn) throws SQLException {
        String query = "DELETE FROM operation WHERE rendez_vous_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rendezVousId);
            stmt.executeUpdate();
        }
    }
}