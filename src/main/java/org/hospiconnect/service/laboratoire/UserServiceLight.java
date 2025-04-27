package org.hospiconnect.service.laboratoire;

import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserServiceLight {

    private static final UserServiceLight instance = new UserServiceLight();

    public static UserServiceLight getInstance() {
        return instance;
    }

    public String findUserNameById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select nom, prenom from user where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom") + " " + rs.getString("prenom");
                }
                throw new RuntimeException("Object Analyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public Map<Long, String> getUsersWithId() {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select id, nom, prenom from user");
                ResultSet rs = ps.executeQuery();
        ) {
            Map<Long, String> result = new HashMap<>();
            while (rs.next()) {
                result.put(
                        rs.getLong("id"),
                        rs.getString("nom") + " " + rs.getString("prenom")
                );
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public String findUserEmailById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT email FROM user WHERE id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
                throw new RuntimeException("Email non trouvé pour l'utilisateur avec l'id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de la récupération de l'email", e);
        }
    }

}
