package org.hospiconnect.dao;

import org.hospiconnect.model.Consultation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hospiconnect.utils.DatabaseUtils;

public class ConsultationDAO {
    
    private boolean ratingColumnExists = false;
    private boolean emailColumnExists = false;
    
    // Check if rating column exists in consultation table
    private boolean hasRatingColumn() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            // First try to get metadata directly from database
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet columns = dbMetaData.getColumns(null, null, "consultation", "rating");
            
            if (columns.next()) {
                return true;
            }
            
            // If that didn't work, try a direct query to test if the column exists
            try (Statement stmt = conn.createStatement()) {
                // Just retrieving one row to test if the column exists
                stmt.executeQuery("SELECT rating FROM consultation LIMIT 1");
                return true;
            } catch (SQLException e) {
                // If this query fails, the column doesn't exist
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking for rating column: " + e.getMessage());
            return false;
        }
    }
    
    // Check if email column exists in consultation table
    private boolean hasEmailColumn() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            // First try to get metadata directly from database
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet columns = dbMetaData.getColumns(null, null, "consultation", "email");
            
            if (columns.next()) {
                return true;
            }
            
            // If that didn't work, try a direct query to test if the column exists
            try (Statement stmt = conn.createStatement()) {
                // Just retrieving one row to test if the column exists
                stmt.executeQuery("SELECT email FROM consultation LIMIT 1");
                return true;
            } catch (SQLException e) {
                // If this query fails, the column doesn't exist
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking for email column: " + e.getMessage());
            return false;
        }
    }
    
    // Add rating column to consultation table if it doesn't exist
    private void addRatingColumn() {
        if (!hasRatingColumn()) {
            try (Connection conn = DatabaseUtils.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("ALTER TABLE consultation ADD COLUMN rating INT DEFAULT 0");
                System.out.println("Added 'rating' column to consultation table");
                ratingColumnExists = true;
            } catch (SQLException e) {
                System.out.println("Failed to add rating column: " + e.getMessage());
                ratingColumnExists = false;
            }
        } else {
            ratingColumnExists = true;
        }
    }
    
    // Add email column to consultation table if it doesn't exist
    private void addEmailColumn() {
        if (!hasEmailColumn()) {
            try (Connection conn = DatabaseUtils.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("ALTER TABLE consultation ADD COLUMN email VARCHAR(255)");
                System.out.println("Added 'email' column to consultation table");
                emailColumnExists = true;
            } catch (SQLException e) {
                System.out.println("Failed to add email column: " + e.getMessage());
                emailColumnExists = false;
            }
        } else {
            emailColumnExists = true;
        }
    }
    
    public ConsultationDAO() {
        // Check and add columns when DAO is initialized
        addRatingColumn();
        addEmailColumn();
    }
    
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
            System.out.println("Error getting all consultations: " + e.getMessage());
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
            System.out.println("Error getting consultation by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean createConsultation(Consultation consultation) {
        String query;
        if (ratingColumnExists && emailColumnExists) {
            query = "INSERT INTO consultation (type_consultation, date_consultation, note, firstname, lastname, rating, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else if (ratingColumnExists) {
            query = "INSERT INTO consultation (type_consultation, date_consultation, note, firstname, lastname, rating) VALUES (?, ?, ?, ?, ?, ?)";
        } else if (emailColumnExists) {
            query = "INSERT INTO consultation (type_consultation, date_consultation, note, firstname, lastname, email) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO consultation (type_consultation, date_consultation, note, firstname, lastname) VALUES (?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, consultation.getTypeConsultation());
            pstmt.setDate(2, DatabaseUtils.toSqlDate(consultation.getDateConsultation()));
            pstmt.setString(3, consultation.getNote());
            pstmt.setString(4, consultation.getFirstname());
            pstmt.setString(5, consultation.getLastname());
            
            int paramIndex = 6;
            
            if (ratingColumnExists) {
                pstmt.setInt(paramIndex++, consultation.getRating());
            }
            
            if (emailColumnExists && consultation.getEmail() != null) {
                pstmt.setString(paramIndex, consultation.getEmail());
            }
            
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
            System.out.println("Error creating consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateConsultation(Consultation consultation) {
        String query;
        if (ratingColumnExists && emailColumnExists) {
            query = "UPDATE consultation SET type_consultation = ?, date_consultation = ?, note = ?, firstname = ?, lastname = ?, rating = ?, email = ? WHERE id = ?";
        } else if (ratingColumnExists) {
            query = "UPDATE consultation SET type_consultation = ?, date_consultation = ?, note = ?, firstname = ?, lastname = ?, rating = ? WHERE id = ?";
        } else if (emailColumnExists) {
            query = "UPDATE consultation SET type_consultation = ?, date_consultation = ?, note = ?, firstname = ?, lastname = ?, email = ? WHERE id = ?";
        } else {
            query = "UPDATE consultation SET type_consultation = ?, date_consultation = ?, note = ?, firstname = ?, lastname = ? WHERE id = ?";
        }
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, consultation.getTypeConsultation());
            pstmt.setDate(2, DatabaseUtils.toSqlDate(consultation.getDateConsultation()));
            pstmt.setString(3, consultation.getNote());
            pstmt.setString(4, consultation.getFirstname());
            pstmt.setString(5, consultation.getLastname());
            
            int paramIndex = 6;
            
            if (ratingColumnExists) {
                pstmt.setInt(paramIndex++, consultation.getRating());
            }
            
            if (emailColumnExists) {
                pstmt.setString(paramIndex++, consultation.getEmail());
            }
            
            pstmt.setInt(paramIndex, consultation.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating consultation: " + e.getMessage());
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
            System.out.println("Error deleting consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        int rating = 0;
        String email = null;
        
        // Get the main required fields from the result set
        int id = rs.getInt("id");
        String type = rs.getString("type_consultation");
        LocalDate date = DatabaseUtils.fromSqlDate(rs.getDate("date_consultation"));
        String note = rs.getString("note");
        String firstname = rs.getString("firstname");
        String lastname = rs.getString("lastname");
        
        // Try to get rating if the column exists
        if (ratingColumnExists) {
            try {
                rating = rs.getInt("rating");
                // Handle potential NULL value in the database by checking if the value was null
                if (rs.wasNull()) {
                    rating = 0;
                }
            } catch (SQLException e) {
                System.out.println("Rating column access error, using default value of 0: " + e.getMessage());
            }
        }
        
        // Try to get email if the column exists
        if (emailColumnExists) {
            try {
                email = rs.getString("email");
                // No need to check wasNull() for String as it returns null for NULL values
            } catch (SQLException e) {
                System.out.println("Email column access error, using null value: " + e.getMessage());
            }
        }
        
        // Create and return the consultation object
        return new Consultation(
            id,
            type,
            date,
            note,
            firstname,
            lastname,
            rating,
            email
        );
    }
}
