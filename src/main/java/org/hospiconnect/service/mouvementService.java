package org.hospiconnect.service;

import org.hospiconnect.model.MouvementMaterielJoint;
import org.hospiconnect.model.mouvement_stock;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mouvementService implements Icrud<mouvement_stock> {
    private Connection con;

    public mouvementService() {
        try {
            this.con = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public List<MouvementMaterielJoint> getMouvementsAvecNomMateriel() throws SQLException {
        List<MouvementMaterielJoint> resultats = new ArrayList<>();

        String sql = """
        SELECT m.id, mat.nom AS nom_materiel, m.qunatite, m.date_mouvement, m.motif, m.type_mouvement
        FROM mouvements_stock m
        INNER JOIN materiel mat ON m.id_materiel_id = mat.id
    """;

        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            MouvementMaterielJoint mvt = new MouvementMaterielJoint(
                    rs.getInt("id"),
                    rs.getString("nom_materiel"),
                    rs.getInt("qunatite"),
                    rs.getDate("date_mouvement"),
                    rs.getString("motif"),
                    rs.getString("type_mouvement")
            );
            resultats.add(mvt);
        }

        return resultats;
    }


    @Override
    public void insert(mouvement_stock obj) throws SQLException {
        String sql = "INSERT INTO mouvements_stock(id_materiel_id, id_personnel_id, qunatite, date_mouvement, motif, type_mouvement) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, obj.getId_materiel_id());
        ps.setInt(2, obj.getId_personnel_id());
        ps.setInt(3, obj.getQunatite());
        ps.setDate(4, new java.sql.Date(obj.getDate_mouvement().getTime()));
        ps.setString(5, obj.getMotif());
        ps.setString(6, obj.getType_mouvement());
        ps.executeUpdate();
    }

    @Override
    public void update(mouvement_stock obj) throws SQLException {
        String sql = "UPDATE mouvements_stock SET id_materiel_id=?, id_personnel_id=?, qunatite=?, date_mouvement=?, motif=?, type_mouvement=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, obj.getId_materiel_id());
        ps.setInt(2, obj.getId_personnel_id());
        ps.setInt(3, obj.getQunatite());
        ps.setDate(4, new java.sql.Date(obj.getDate_mouvement().getTime()));
        ps.setString(5, obj.getMotif());
        ps.setString(6, obj.getType_mouvement());
        ps.setInt(7, obj.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(mouvement_stock obj) throws SQLException {
        String sql = "DELETE FROM mouvements_stock WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, obj.getId());
        ps.executeUpdate();
    }

    @Override
    public List<mouvement_stock> findAll() throws SQLException {
        String sql = "SELECT * FROM mouvements_stock";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<mouvement_stock> mouvements = new ArrayList<>();
        while (rs.next()) {
            mouvement_stock mouvement = new mouvement_stock();
            mouvement.setId(rs.getInt("id"));
            mouvement.setId_materiel_id(rs.getInt("id_materiel_id"));
            mouvement.setId_personnel_id(rs.getInt("id_personnel_id"));
            mouvement.setQunatite(rs.getInt("qunatite"));
            mouvement.setDate_mouvement(rs.getDate("date_mouvement"));
            mouvement.setMotif(rs.getString("motif"));
            mouvement.setType_mouvement(rs.getString("type_mouvement"));
            mouvements.add(mouvement);
        }

        return mouvements;
    }
}
