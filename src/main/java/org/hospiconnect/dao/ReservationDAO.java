package org.hospiconnect.dao;

import org.hospiconnect.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.hospiconnect.utils.DatabaseUtils;

public class ReservationDAO {
    
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
        String query = "INSERT INTO reservation (duree_traitement, nom_medicament, nombres_fois) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, DatabaseUtils.toSqlTimestamp(reservation.getDureeTraitement()));
            pstmt.setString(2, reservation.getNomMedicament());
            pstmt.setInt(3, reservation.getNombresFois());
            
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
        String query = "UPDATE reservation SET duree_traitement = ?, nom_medicament = ?, nombres_fois = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, DatabaseUtils.toSqlTimestamp(reservation.getDureeTraitement()));
            pstmt.setString(2, reservation.getNomMedicament());
            pstmt.setInt(3, reservation.getNombresFois());
            pstmt.setInt(4, reservation.getId());
            
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
        return new Reservation(
            rs.getInt("id"),
            DatabaseUtils.fromSqlTimestamp(rs.getTimestamp("duree_traitement")),
            rs.getString("nom_medicament"),
            rs.getInt("nombres_fois")
        );
    }
}
