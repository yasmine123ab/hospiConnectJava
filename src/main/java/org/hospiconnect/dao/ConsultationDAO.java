package org.hospiconnect.dao;

import org.hospiconnect.model.Consultation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.hospiconnect.utils.DatabaseUtils;

public class ConsultationDAO {
    
    public List<Consultation> getAllConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        String query = "SELECT * FROM consultation";
        
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Consultation consultation = mapResultSetToConsultation(rs);
                consultations.add(consultation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultations;
    }
    
    public Consultation getConsultationById(int id) {
        String query = "SELECT * FROM consultation WHERE id = ?";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConsultation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean createConsultation(Consultation consultation) {
        String query = "INSERT INTO consultation (type_consultation, date_consultation, note, firstname, lastname) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, consultation.getTypeConsultation());
            pstmt.setDate(2, DatabaseUtils.toSqlDate(consultation.getDateConsultation()));
            pstmt.setString(3, consultation.getNote());
            pstmt.setString(4, consultation.getFirstname());
            pstmt.setString(5, consultation.getLastname());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        consultation.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateConsultation(Consultation consultation) {
        String query = "UPDATE consultation SET type_consultation = ?, date_consultation = ?, note = ?, firstname = ?, lastname = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, consultation.getTypeConsultation());
            pstmt.setDate(2, DatabaseUtils.toSqlDate(consultation.getDateConsultation()));
            pstmt.setString(3, consultation.getNote());
            pstmt.setString(4, consultation.getFirstname());
            pstmt.setString(5, consultation.getLastname());
            pstmt.setInt(6, consultation.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteConsultation(int id) {
        String query = "DELETE FROM consultation WHERE id = ?";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        return new Consultation(
            rs.getInt("id"),
            rs.getString("type_consultation"),
            DatabaseUtils.fromSqlDate(rs.getDate("date_consultation")),
            rs.getString("note"),
            rs.getString("firstname"),
            rs.getString("lastname")
        );
    }
}
