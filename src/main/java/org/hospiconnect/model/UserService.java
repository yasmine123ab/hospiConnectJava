package org.hospiconnect.model;

import org.hospiconnect.model.User;
import org.hospiconnect.utils.DatabaseUtils;
import org.hospiconnect.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                if (PasswordUtils.checkPassword(passwordPlain, hashedPassword)) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur login : " + e.getMessage());
        }
        return null;
    }

    public List<User> searchUsersPageSQL(String keyword, int page, int pageSize) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? OR groupe_sanguin LIKE ? OR tel LIKE ? OR sexe LIKE ? LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);
            ps.setString(4, likeKeyword);  // Recherche par groupe sanguin
            ps.setString(5, likeKeyword);  // Recherche par téléphone
            ps.setString(6, likeKeyword);  // Recherche par sexe
            ps.setInt(7, pageSize);
            ps.setInt(8, (page - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = extractUser(rs); // Récupère les utilisateurs avec tous les champs
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche utilisateurs : " + e.getMessage());
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
        String sql = "UPDATE user SET nom=?, prenom=?, email=?, photo=?, password=? WHERE id=?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoto());
            ps.setString(5, user.getPassword());
            ps.setInt(6, user.getId());
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
        user.setRoles(rs.getString("roles"));
        user.setStatut(rs.getString("statut"));
        user.setPhoto(rs.getString("photo"));
        user.setGroupe_Sanguin(rs.getString("groupe_sanguin"));
        user.setTel(rs.getString("tel"));
        user.setSexe(rs.getString("sexe")); // Récupère le champ Sexe
        user.setAdresse(rs.getString("adresse")); // Ajoute l'adresse
        user.setZipCode(rs.getString("zipcode")); // Ajoute le code postal
        user.setGouvernorat(rs.getString("gouvernorat")); // Ajoute le gouvernorat
        user.setPoids(rs.getFloat("poids")); // Ajoute le poids
        user.setTaille(rs.getFloat("taille")); // Ajoute la taille
        user.setImc(rs.getFloat("imc")); // Ajoute l'IMC
        return user;
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


}
