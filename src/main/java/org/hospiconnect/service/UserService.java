package org.hospiconnect.service;


import org.hospiconnect.model.User;

import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;




public class UserService {




    public void register(User user) {
        String sql = "INSERT INTO user(nom, prenom, email, password, roles, statut) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRoles());
            ps.setString(6, user.getStatut());
            ps.executeUpdate();
            System.out.println("✅ Utilisateur inséré !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur insertion : " + e.getMessage());
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification email : " + e.getMessage());
        }
        return false;
    }

    public List<User> getUsersPage(int page, int pageSize) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération page utilisateurs : " + e.getMessage());
        }
        return users;
    }

    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM user";
        try (Connection conn = DatabaseUtils.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur comptage utilisateurs : " + e.getMessage());
        }
        return 0;
    }

    public User login(String email, String passwordPlain) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (passwordPlain.equals(hashedPassword)) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur login : " + e.getMessage());
        }
        return null;
    }

    public List<User> searchUsersPageSQL(String keyword, int page, int pageSize, String roleFilter, String bloodGroupFilter, String statusFilter) {
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM user WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            queryBuilder.append(" AND (nom LIKE ? OR prenom LIKE ? OR email LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        if (roleFilter != null && !roleFilter.isEmpty()) {
            queryBuilder.append(" AND roles = ?");
            parameters.add(roleFilter);
        }
        
        if (bloodGroupFilter != null && !bloodGroupFilter.isEmpty()) {
            queryBuilder.append(" AND Groupe_Sanguin = ?");
            parameters.add(bloodGroupFilter);
        }
        
        if (statusFilter != null && !statusFilter.isEmpty()) {
            queryBuilder.append(" AND statut = ?");
            parameters.add(statusFilter);
        }
        
        // Appliquer le tri uniquement si un filtre est sélectionné
        if (roleFilter != null && !roleFilter.isEmpty()) {
            queryBuilder.append(" ORDER BY CASE roles ");
            queryBuilder.append("WHEN 'ADMIN' THEN 1 ");
            queryBuilder.append("WHEN 'PERSONNEL' THEN 2 ");
            queryBuilder.append("WHEN 'MEDECIN' THEN 3 ");
            queryBuilder.append("WHEN 'PATIENT' THEN 4 ");
            queryBuilder.append("ELSE 5 END");
            
            // Tri secondaire par groupe sanguin si le filtre est sélectionné
            if (bloodGroupFilter != null && !bloodGroupFilter.isEmpty()) {
                queryBuilder.append(", Groupe_Sanguin");
            }
        }
        
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
                    users.add(extractUser(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des utilisateurs : " + e.getMessage());
        }
        return users;
    }




    public int countUsersWithKeyword(String keyword) {
        String sql = "SELECT COUNT(*) FROM user WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur comptage recherche : " + e.getMessage());
        }
        return 0;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération utilisateurs : " + e.getMessage());
        }
        return users;
    }

    public void deleteUserById(int userId) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            System.out.println("🗑️ Utilisateur supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression : " + e.getMessage());
        }
    }

    public void blockUserById(int userId) {
        String sql = "UPDATE user SET statut = 'BLOQUER' WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            System.out.println("🚫 Utilisateur bloqué avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du blocage : " + e.getMessage());
        }
    }

    public void ActiveUserById(int userId) {
        String sql = "UPDATE user SET statut = 'active' WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            System.out.println("🚫 Utilisateur bloqué avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du blocage : " + e.getMessage());
        }
    }

    public boolean isUserBlockedByEmail(String email) {
        String sql = "SELECT statut FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String statut = rs.getString("statut");
                return "BLOQUER".equalsIgnoreCase(statut);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du statut de l'utilisateur : " + e.getMessage());
        }
        return false; // Si l'utilisateur n'existe pas ou erreur
    }


    public void updateUser(User user) {
        String sql = "UPDATE user SET nom=?, prenom=?, email=?, roles=?, statut=?, photo=?, " +
                "groupe_sanguin=?, gouvernorat=?, poids=?, taille=?, imc=?, sexe=?, zipcode=?, adresse=? " +
                "WHERE id=?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Remplissage des paramètres de la requête
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRoles());
            ps.setString(5, user.getStatut());
            ps.setString(6, user.getPhoto());
            ps.setString(7, user.getGroupe_Sanguin());
            ps.setString(8, user.getGouvernorat());
            ps.setFloat(9, user.getPoids());
            ps.setFloat(10, user.getTaille());
            ps.setFloat(11, user.getImc());
            ps.setString(12, user.getSexe());
            ps.setString(13, user.getZipCode());
            ps.setString(14, user.getAdresse());
            ps.setInt(15, user.getId());

            // Exécution de la mise à jour
            ps.executeUpdate();
            System.out.println("✅ Utilisateur mis à jour avec tous les détails !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour : " + e.getMessage());
        }
    }


    public void updateUserWithPassword(User user) {
        String sql = "UPDATE user SET nom=?, prenom=?, email=?, photo=?, password=?, tel=?, adresse=?, groupe_sanguin=?, gouvernorat=?, zipcode=?, taille=?, poids=?, diplome=? WHERE id=?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoto());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getTel());
            ps.setString(7, user.getAdresse());
            ps.setString(8, user.getGroupe_Sanguin());
            ps.setString(9, user.getGouvernorat());
            ps.setString(10, user.getZipCode());
            ps.setFloat(11, user.getTaille());
            ps.setFloat(12, user.getPoids());
            ps.setString(13, user.getDiplome()); // correction ici
            ps.setInt(14, user.getId());

            ps.executeUpdate();
            System.out.println("✅ Utilisateur mis à jour avec succès !");

        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour utilisateur : " + e.getMessage());
        }
    }




    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération utilisateur par email : " + e.getMessage());
        }
        return null;
    }

    // ✅ Fonction utilitaire pour DRY
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhoto(rs.getString("photo"));
        user.setRoles(rs.getString("roles"));
        user.setAdresse(rs.getString("adresse"));
        user.setStatut(rs.getString("statut"));
        user.setGroupe_Sanguin(rs.getString("Groupe_Sanguin"));
        user.setTel(rs.getString("tel"));
        user.setSexe(rs.getString("sexe"));
        user.setZipCode(rs.getString("zipcode"));
        user.setGouvernorat(rs.getString("gouvernorat"));
        user.setPoids(rs.getFloat("poids"));
        user.setTaille(rs.getFloat("taille"));
        user.setImc(rs.getFloat("imc"));
        user.setDiplome(rs.getString("diplome"));
        user.setA2F(rs.getString("A2F"));
        return user;
    }


    public User findUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Extraire l'utilisateur et le retourner
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur findUserByEmail : " + e.getMessage());
        }
        return null;
    }


    public void incrementerTC(String email) {
        String sqlSelect = "SELECT tc FROM user WHERE email = ?";
        String sqlUpdateTC = "UPDATE user SET tc = ? WHERE email = ?";
        String sqlUpdateStatut = "UPDATE user SET statut = 'BLOQUER' WHERE email = ?";

        try (Connection conn =DatabaseUtils.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {

            psSelect.setString(1, email);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                int currentTC = rs.getInt("tc");
                int newTC = currentTC + 1;

                // Mise à jour du champ tc
                try (PreparedStatement psUpdateTC = conn.prepareStatement(sqlUpdateTC)) {
                    psUpdateTC.setInt(1, newTC);
                    psUpdateTC.setString(2, email);
                    psUpdateTC.executeUpdate();
                }

                // Si tc atteint 3, on bloque l'utilisateur
                if (newTC >= 3) {
                    try (PreparedStatement psUpdateStatut = conn.prepareStatement(sqlUpdateStatut)) {
                        psUpdateStatut.setString(1, email);
                        psUpdateStatut.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation de tc ou mise à jour du statut : " + e.getMessage());
        }
    }


    public void resetPassword(String email) {
        String sqlSelect = "SELECT * FROM user WHERE email = ?";
        String sqlUpdateMot = "UPDATE user SET mot = ? WHERE email = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {

            psSelect.setString(1, email);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                // Générer un mot de passe aléatoire (8 caractères alphanumériques)
                String newPassword = generateRandomPassword(8);

                // Mise à jour du champ 'mot' avec le nouveau mot de passe
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateMot)) {
                    psUpdate.setString(1, newPassword);
                    psUpdate.setString(2, email);
                    psUpdate.executeUpdate();
                    System.out.println("Nouveau mot de passe généré pour " + email + " : " + newPassword);
                }
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la réinitialisation du mot de passe : " + e.getMessage());
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }



    public void AddUser(User user) {
        String sql = "INSERT INTO user (nom, prenom, email, roles, tel, statut, photo, groupe_sanguin, gouvernorat, " +
                "poids, taille, imc, sexe, zipcode, adresse) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Remplissage des paramètres de la requête
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRoles());
            ps.setString(5, user.getTel()); // <-- Ajout du téléphone ici
            ps.setString(6, user.getStatut());
            ps.setString(7, user.getPhoto());
            ps.setString(8, user.getGroupe_Sanguin());
            ps.setString(9, user.getGouvernorat());
            ps.setFloat(10, user.getPoids());
            ps.setFloat(11, user.getTaille());
            ps.setFloat(12, user.getImc());
            ps.setString(13, user.getSexe());
            ps.setString(14, user.getZipCode());
            ps.setString(15, user.getAdresse());

            // Exécution de l'ajout
            ps.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout utilisateur : " + e.getMessage());
        }
    }


    public void setA2FCode(String email, String a2fCode) {
        String sql = "UPDATE user SET A2F = ? WHERE email = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // On met le code A2F dans la base de données
            ps.setString(1, a2fCode);  // A2F est un code (String)
            ps.setString(2, email);     // email est un String

            // Exécution de la mise à jour
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Code A2F mis à jour avec succès !");
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec cet email.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du code A2F : " + e.getMessage());
        }
    }

}
