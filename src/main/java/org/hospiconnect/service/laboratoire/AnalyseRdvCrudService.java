package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.RdvAnalyse;
import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyseRdvCrudService {

    private static final AnalyseRdvCrudService instance = new AnalyseRdvCrudService();

    private static final String ID_COL = "id";
    private static final String DISPO_ID_COL = "disponibilite_id";
    private static final String PATIENT_ID_COL = "patient_id";
    private static final String DATE_RDV_COL = "date_rdv";
    private static final String STATUT_COL = "statut";

    public static AnalyseRdvCrudService getInstance() {
        return instance;
    }

    public List<RdvAnalyse> findAll() {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from rendez_vous_analyse");
                ResultSet rs = ps.executeQuery()
        ) {
            List<RdvAnalyse> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public RdvAnalyse findById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from rendez_vous_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                throw new RuntimeException("Object Rdv Analyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public String findDateRdvByIdOrNull(Long idRdv) {
        if (idRdv == null || idRdv == 0) return null;

        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT date_rdv FROM rendez_vous_analyse WHERE id = ?");
        ) {
            ps.setLong(1, idRdv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("date_rdv") != null
                            ? rs.getDate("date_rdv").toLocalDate().toString()
                            : null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }


    public void createNew(RdvAnalyse newRdvAnalyse) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "insert into rendez_vous_analyse (%s,%s,%s,%s) values (?,?,?,?)",
                        DISPO_ID_COL,
                        PATIENT_ID_COL,
                        DATE_RDV_COL,
                        STATUT_COL
                ))
        ) {
            ps.setLong(1, newRdvAnalyse.getIdDisponibilite());
            ps.setLong(2, newRdvAnalyse.getIdPatient());
            ps.setDate(3, DatabaseUtils.toSqlDate(newRdvAnalyse.getDateRdv()));
            ps.setString(4, newRdvAnalyse.getStatut());

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public boolean update(RdvAnalyse rdvAnalyse) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "update rendez_vous_analyse set %s=?, %s=?, %s=?, %s=? where %s = ?",
                        DISPO_ID_COL,
                        PATIENT_ID_COL,
                        DATE_RDV_COL,
                        STATUT_COL,
                        ID_COL
                ))
        ) {
            ps.setLong(1, rdvAnalyse.getIdDisponibilite());
            ps.setLong(2, rdvAnalyse.getIdPatient());
            ps.setDate(3, DatabaseUtils.toSqlDate(rdvAnalyse.getDateRdv()));
            ps.setString(4, rdvAnalyse.getStatut());
            ps.setLong(5, rdvAnalyse.getId());

            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public void deleteById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("delete from rendez_vous_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public String findRdvDateById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select date_rdv from rendez_vous_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("date_rdv");
                }
                throw new RuntimeException("Object Rdv Analyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public Map<Long, String> findRdvsWithId() {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select id, date_rdv from rendez_vous_analyse");
                ResultSet rs = ps.executeQuery();
        ) {
            Map<Long, String> result = new HashMap<>();
            while (rs.next()) {
                result.put(
                        rs.getLong("id"),
                        rs.getDate("date_rdv").toString()
                );
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    private RdvAnalyse mapRow(ResultSet rs) throws SQLException {
        return new RdvAnalyse(
                rs.getLong(ID_COL),
                rs.getLong(DISPO_ID_COL),
                rs.getLong(PATIENT_ID_COL),
                DatabaseUtils.fromSqlDate(rs.getDate(DATE_RDV_COL)),
                rs.getString(STATUT_COL)
        );
    }

}
