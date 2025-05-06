package org.hospiconnect.dao;

import org.hospiconnect.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.hospiconnect.utils.DatabaseUtils;

public class ReservationDAO {
    
    private boolean noteColumnExists = false;
    
    // Check if note column exists in reservation table
    private boolean hasNoteColumn() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            // First try to get metadata directly from database
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet columns = dbMetaData.getColumns(null, null, "reservation", "note");
            
            if (columns.next()) {
                return true;
            }
            
            // If that didn't work, try a direct query to test if the column exists
            try (Statement stmt = conn.createStatement()) {
                // Just retrieving one row to test if the column exists
                stmt.executeQuery("SELECT note FROM reservation LIMIT 1");
                return true;
            } catch (SQLException e) {
                // If this query fails, the column doesn't exist
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking for note column: " + e.getMessage());
            return false;
        }
    }
    
    // Add note column to reservation table if it doesn't exist
    private void addNoteColumn() {
        if (!hasNoteColumn()) {
            try (Connection conn = DatabaseUtils.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("ALTER TABLE reservation ADD COLUMN note TEXT");
                System.out.println("Added 'note' column to reservation table");
                noteColumnExists = true;
            } catch (SQLException e) {
                System.out.println("Failed to add note column: " + e.getMessage());
                noteColumnExists = false;
            }
        } else {
            noteColumnExists = true;
        }
    }
    
    public ReservationDAO() {
        // Check and add note column when DAO is initialized
        addNoteColumn();
    }
    
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";
        
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
    
    public Reservation getReservationById(int id) {
        String query = "SELECT * FROM reservation WHERE id = ?";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean createReservation(Reservation reservation) {
        String query;
        if (noteColumnExists) {
            query = "INSERT INTO reservation (duree_traitement, nom_medicament, nombres_fois, note) VALUES (?, ?, ?, ?)";
        } else {
            query = "INSERT INTO reservation (duree_traitement, nom_medicament, nombres_fois) VALUES (?, ?, ?)";
        }
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, DatabaseUtils.toSqlTimestamp(reservation.getDureeTraitement()));
            pstmt.setString(2, reservation.getNomMedicament());
            pstmt.setInt(3, reservation.getNombresFois());
            
            if (noteColumnExists && reservation.getNote() != null) {
                pstmt.setString(4, reservation.getNote());
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateReservation(Reservation reservation) {
        String query;
        if (noteColumnExists) {
            query = "UPDATE reservation SET duree_traitement = ?, nom_medicament = ?, nombres_fois = ?, note = ? WHERE id = ?";
        } else {
            query = "UPDATE reservation SET duree_traitement = ?, nom_medicament = ?, nombres_fois = ? WHERE id = ?";
        }
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, DatabaseUtils.toSqlTimestamp(reservation.getDureeTraitement()));
            pstmt.setString(2, reservation.getNomMedicament());
            pstmt.setInt(3, reservation.getNombresFois());
            
            if (noteColumnExists) {
                pstmt.setString(4, reservation.getNote());
                pstmt.setInt(5, reservation.getId());
            } else {
                pstmt.setInt(4, reservation.getId());
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteReservation(int id) {
        String query = "DELETE FROM reservation WHERE id = ?";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        String note = null;
        
        if (noteColumnExists) {
            try {
                note = rs.getString("note");
            } catch (SQLException e) {
                // If there's an error getting note, use null
                System.out.println("Note column access error, using null value");
            }
        }
        
        return new Reservation(
            rs.getInt("id"),
            DatabaseUtils.fromSqlTimestamp(rs.getTimestamp("duree_traitement")),
            rs.getString("nom_medicament"),
            rs.getInt("nombres_fois"),
            note
        );
    }
}
