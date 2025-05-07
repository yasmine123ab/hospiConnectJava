package org.hospiconnect.service;


import org.hospiconnect.model.Reclamation;
import org.hospiconnect.utils.DatabaseUtils;




import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {

    public static Reclamation findByTitle(String titre) {
        String query = "SELECT * FROM reclamations WHERE title = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, titre);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractReclamation(rs); // Tu réutilises ta méthode extractReclamation
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la réclamation par titre : " + e.getMessage());
        }
        return null; // Si pas trouvé
    }


    public List<Reclamation> getReclamationsPage(int page, int pageSize) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamations WHERE Status = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "En cours");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reclamations.add(extractReclamation(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération réclamations en cours : " + e.getMessage());
        }
        return reclamations;
    }

    public int countReclamations(String keyword) {
        String sql = "SELECT COUNT(*) FROM reclamations WHERE status LIKE ? OR category LIKE ?OR title LIKE ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur comptage réclamations : " + e.getMessage());
        }
        return 0;
    }

    public List<Reclamation> searchReclamationsPageSQL(String keyword, int page, int pageSize, String priorityFilter, String statusFilter) {
        List<Reclamation> reclamations = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder queryBuilder = new StringBuilder("SELECT r.*, " +
                "u1.nom as resolved_by_nom, u1.prenom as resolved_by_prenom, " +
                "u2.nom as user_nom, u2.prenom as user_prenom " +
                "FROM reclamations r " +
                "LEFT JOIN user u1 ON r.resolved_by = u1.id " +
                "LEFT JOIN user u2 ON r.user_id = u2.id " +
                "WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            queryBuilder.append(" AND (r.title LIKE ? OR r.description LIKE ? OR r.category LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        if (priorityFilter != null && !priorityFilter.isEmpty()) {
            queryBuilder.append(" AND r.priority = ?");
            parameters.add(priorityFilter);
        }
        
        if (statusFilter != null && !statusFilter.isEmpty()) {
            queryBuilder.append(" AND r.status = ?");
            parameters.add(statusFilter);
        }
        
        // Tri automatique par priorité (HIGH > MEDIUM > LOW)
        queryBuilder.append(" ORDER BY CASE r.priority ");
        queryBuilder.append("WHEN 'HIGH' THEN 1 ");
        queryBuilder.append("WHEN 'MEDIUM' THEN 2 ");
        queryBuilder.append("WHEN 'LOW' THEN 3 ");
        queryBuilder.append("ELSE 4 END");
        
        // Tri secondaire par date (les plus récentes en premier)
        queryBuilder.append(", r.date_reclamation DESC");
        
        queryBuilder.append(" LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(offset);
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reclamation reclamation = extractReclamation(rs);
                    
                    // Récupération du nom de l'utilisateur qui a résolu
                    String resolvedByNom = rs.getString("resolved_by_nom");
                    String resolvedByPrenom = rs.getString("resolved_by_prenom");
                    if (resolvedByNom != null && resolvedByPrenom != null) {
                        reclamation.setResolvedByUsername(resolvedByNom + " " + resolvedByPrenom);
                    }
                    
                    // Récupération du nom de l'utilisateur qui a créé la réclamation
                    String userNom = rs.getString("user_nom");
                    String userPrenom = rs.getString("user_prenom");
                    if (userNom != null && userPrenom != null) {
                        reclamation.setUserName(userNom + " " + userPrenom);
                    } else {
                        reclamation.setUserName("Anonyme");
                    }
                    
                    reclamations.add(reclamation);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des réclamations : " + e.getMessage());
        }
        return reclamations;
    }



    public List<Reclamation> getReclamationsEnCours() {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamations WHERE status = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "En cours");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reclamations.add(extractReclamation(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération réclamations en cours : " + e.getMessage());
        }
        return reclamations;
    }


    public void deleteReclamationById(long id) {
        String sql = "DELETE FROM reclamations WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            System.out.println("🗑️ Réclamation supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression réclamation : " + e.getMessage());
        }
    }

    public static boolean updateReclamation(Reclamation reclamation) {
        String sql = "UPDATE reclamations SET resolution_date=?, response=?, resolved_by=? WHERE title=?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Mise à jour de la date de résolution à la date actuelle
            ps.setDate(1, new Date(System.currentTimeMillis())); // Date actuelle
            ps.setString(2, reclamation.getResponse());
            ps.setLong(3, reclamation.getResolvedBy() != null ? reclamation.getResolvedBy() : 0); // Valeur par défaut si null
            ps.setString(4, reclamation.getTitle()); // Utilisation de title pour l'identification

            ps.executeUpdate();
            System.out.println("✅ Réclamation mise à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour réclamation : " + e.getMessage());
        }
        return false;
    }

    public static boolean updateReclamationResponse(String titre, String reponse,int id) {
        String sql = "UPDATE reclamations SET response = ?, resolution_date = ?, resolved_by = ?, status = ? WHERE title = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reponse); // Le texte de réponse
            ps.setDate(2, new Date(System.currentTimeMillis())); // Date actuelle
            ps.setLong(3, id); // Id de l'admin qui répond (tu peux changer ici)
            ps.setString(4, "Traité"); // Le statut changé en "Traité"
            ps.setString(5, titre); // Le titre pour trouver la bonne réclamation

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Réclamation mise à jour avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour réclamation : " + e.getMessage());
        }
        return false;
    }


    private static Reclamation extractReclamation(ResultSet rs) throws SQLException {
        Reclamation reclamation = new Reclamation();
        reclamation.setId(rs.getLong("id"));
        reclamation.setUserId(rs.getLong("user_id"));
        reclamation.setDescription(rs.getString("description"));
        reclamation.setDateReclamation(rs.getDate("date_reclamation"));
        reclamation.setStatus(rs.getString("status"));
        reclamation.setCategory(rs.getString("category"));
        reclamation.setPriority(rs.getString("priority"));
        reclamation.setResolutionDate(rs.getDate("resolution_date"));
        reclamation.setResponse(rs.getString("response"));
        reclamation.setResolvedBy(rs.getLong("resolved_by"));
        reclamation.setIsAnonymous(rs.getBoolean("is_anonymous"));
        reclamation.setAttachment(rs.getString("attachment"));
        reclamation.setTitle(rs.getString("title"));
        return reclamation;
    }

    public void addReclamation(Reclamation reclamation) {
        String sql = "INSERT INTO reclamations (title, description, date_reclamation, category, is_anonymous, priority, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reclamation.getTitle());
            ps.setString(2, reclamation.getDescription());
            ps.setDate(3, new Date(reclamation.getDateReclamation().getTime()));
            ps.setString(4, reclamation.getCategory());
            ps.setInt(5, reclamation.getIsAnonymous() ? 1 : 0);
            ps.setString(6, reclamation.getPriority());
            ps.setLong(7, reclamation.getUserId());

            ps.executeUpdate();
            System.out.println("✅ Réclamation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout réclamation : " + e.getMessage());
        }
    }

    public List<Reclamation> getReclamationsByUserId(Long userId) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamations WHERE user_id = ? ORDER BY date_reclamation DESC";
        
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reclamations.add(extractReclamation(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réclamations de l'utilisateur : " + e.getMessage());
        }
        
        return reclamations;
    }

}
