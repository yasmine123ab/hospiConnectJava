package org.hospiconnect.service;

import org.hospiconnect.model.AttributionsDons;
import org.hospiconnect.model.DemandesDons;
import org.hospiconnect.model.Dons;
import org.hospiconnect.model.User;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributionDonService implements ICrud<AttributionsDons> {
    private Connection con;
    private static AttributionDonService instance;
    // Méthode statique pour obtenir l'instance unique
    public static AttributionDonService getInstance() {
        if (instance == null) {
            instance = new AttributionDonService();
        }
        return instance;
    }

    public AttributionDonService() {
        try {
            this.con = DatabaseUtils.getConnection(); // Connexion à la base de données via DatabaseUtils
        } catch (SQLException e) {
            e.printStackTrace(); // Gestion des erreurs de connexion
        }
    }

    @Override
    public void insert(AttributionsDons obj) throws SQLException {
        String sql = "INSERT INTO attributions_dons(date_attribution, statut, don_id, demande_id, beneficiaire_id) VALUES(?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setDate(1, obj.getDateAttribution());
        ps.setString(2, obj.getStatut());
        ps.setInt(3, obj.getDon().getId());  // Assumer que don est une instance de Don
        ps.setInt(4, obj.getDemande().getId());  // Assumer que demande est une instance de Demande
        ps.setInt(5, obj.getBeneficiaire().getId());  // Assumer que beneficiaire est une instance de User
        ps.executeUpdate();
    }

    @Override
    public void update(AttributionsDons obj) throws SQLException {
        String sql = "UPDATE attributions_dons SET date_attribution=?, statut=?, don_id=?, demande_id=?, beneficiaire_id=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setDate(1, obj.getDateAttribution());
        ps.setString(2, obj.getStatut());
        ps.setInt(3, obj.getDon().getId());
        ps.setInt(4, obj.getDemande().getId());
        ps.setInt(5, obj.getBeneficiaire().getId());
        ps.setInt(6, obj.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(AttributionsDons obj) throws SQLException {
        String sql = "DELETE FROM attributions_dons WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, obj.getId());
        ps.executeUpdate();
    }

    @Override
    public List<AttributionsDons> findAll() throws SQLException {
        // Requête SQL corrigée avec les bons espaces et formatage
        String sql = "SELECT \n" +
                "    ad.date_attribution,\n" +
                "    ad.statut,\n" +
                "    d.id AS don_id, \n" +
                "    d.type_don,\n" +
                "    u_donateur.id AS donateur_id, \n" +
                "    u_donateur.nom AS donateur_nom, \n" +
                "    u_donateur.prenom AS donateur_prenom,\n" +
                "    de.id AS demande_id,\n" +
                "    de.type_besoin,\n" +
                "    u_beneficiaire.id AS beneficiaire_id, \n" +
                "    u_beneficiaire.nom AS beneficiaire_nom, \n" +
                "    u_beneficiaire.prenom AS beneficiaire_prenom\n" +
                "FROM attributions_dons ad\n" +
                "JOIN dons d ON ad.don_id = d.id\n" +
                "JOIN user u_donateur ON d.donateur_id = u_donateur.id\n" +
                "JOIN demandes_dons de ON ad.demande_id = de.id\n" +
                "JOIN user u_beneficiaire ON ad.beneficiaire_id = u_beneficiaire.id;\n";

        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<AttributionsDons> list = new ArrayList<>();
        while (rs.next()) {
            // Crée l'objet AttributionsDons
            AttributionsDons attribution = new AttributionsDons();
            attribution.setDateAttribution(rs.getDate("date_attribution"));
            attribution.setStatut(rs.getString("statut"));

            // Récupération du donateur
            User donateur = new User();
            donateur.setId(rs.getInt("donateur_id"));
            donateur.setNom(rs.getString("donateur_nom"));
            donateur.setPrenom(rs.getString("donateur_prenom"));

            // Récupération du don
            Dons don = new Dons();
            don.setId(rs.getInt("don_id"));
            don.setTypeDon(rs.getString("type_don"));
            don.setDonateur(donateur);
            attribution.setDon(don);

            // Récupération du bénéficiaire
            User beneficiaire = new User();
            beneficiaire.setId(rs.getInt("beneficiaire_id"));
            beneficiaire.setNom(rs.getString("beneficiaire_nom"));
            beneficiaire.setPrenom(rs.getString("beneficiaire_prenom"));

            // Récupération de la demande
            DemandesDons demande = new DemandesDons();
            demande.setId(rs.getInt("demande_id"));
            demande.setTypeBesoin(rs.getString("type_besoin"));
            demande.setPatient(beneficiaire); // association du bénéficiaire à la demande
            attribution.setDemande(demande);

            // Ajout de l'attribution à la liste
            list.add(attribution);
        }
        return list;
    }
    /** Statistiques : compte par statut */
    public Map<String, Integer> getAttributionStatisticsByStatut() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT statut, COUNT(*) AS total FROM attributions_dons GROUP BY statut";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("statut"),
                        rs.getInt("total"));
            }
        }
        return stats;
    }



}

