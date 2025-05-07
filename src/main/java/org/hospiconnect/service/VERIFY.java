package org.hospiconnect.service;


import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VERIFY {

    public static boolean verifyDiplomeInDatabase(String nomEtudiant, String numeroDiplome, String UNIV) {
        // Requête SQL avec jointure entre Diplomes et Etablissements
        String sql = "SELECT COUNT(*) FROM Diplomes d " +
                "JOIN Etablissements e ON d.id_etablissement = e.id " +
                "WHERE d.nom_etudiant = ? AND d.numero_diplome = ? AND e.nom = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Remplissage des paramètres
            ps.setString(1, nomEtudiant);
            ps.setString(2, numeroDiplome);
            ps.setString(3, UNIV);

            // Exécution de la requête
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Retourne vrai si le diplôme existe dans la base de données
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du diplôme : " + e.getMessage());
        }

        return false;  // Retourne faux si le diplôme n'est pas trouvé
    }


}
