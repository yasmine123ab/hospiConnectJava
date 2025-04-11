package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnalyseCrudService {

    private static final AnalyseCrudService instance = new AnalyseCrudService();

    private static final String ID_COL = "id";
    private static final String RDV_ID_COL = "rdv_id";
    private static final String PATIENT_ID_COL = "patient_id";
    private static final String PERSONNEL_ID_COL = "personnel_id";
    private static final String ETAT_COL = "etat";
    private static final String TYPE_ANALYSE_COL = "type_analyse_id";
    private static final String RESULTAT_COL = "resultat";
    private static final String DATE_RESULTAT_COL = "date_resultat";
    private static final String DATE_PRELEVEMENT_COL = "date_prelevement";


    public static AnalyseCrudService getInstance() {
        return instance;
    }

    public List<Analyse> findAll() {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from analyse");
                ResultSet rs = ps.executeQuery()
        ) {
            List<Analyse> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public Analyse findById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                throw new RuntimeException("Object Analyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public void createNew(Analyse newAnalyse) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "insert into analyse(%s,%s,%s,%s,%s,%s,%s,%s) values (?,?,?,?,?,?,?,?)",
                        RDV_ID_COL,
                        PATIENT_ID_COL,
                        PERSONNEL_ID_COL,
                        ETAT_COL,
                        TYPE_ANALYSE_COL,
                        RESULTAT_COL,
                        DATE_RESULTAT_COL,
                        DATE_PRELEVEMENT_COL
                ))
        ) {
            ps.setObject(1, newAnalyse.getIdRdv()); //in case rdv is null
            ps.setLong(2, newAnalyse.getIdPatient());
            ps.setLong(3, newAnalyse.getIdPersonnel());
            ps.setString(4, newAnalyse.getEtat());
            ps.setLong(5, newAnalyse.getIdTypeAnalyse());
            ps.setString(6, newAnalyse.getResultat());
            ps.setDate(7, DatabaseUtils.toSqlDate(newAnalyse.getDateResultat()));
            ps.setDate(8, DatabaseUtils.toSqlDate(newAnalyse.getDatePrelevement()));

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public boolean update(Analyse analyse) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "update analyse set %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? where %s = ?",
                        RDV_ID_COL,
                        PATIENT_ID_COL,
                        PERSONNEL_ID_COL,
                        ETAT_COL,
                        TYPE_ANALYSE_COL,
                        RESULTAT_COL,
                        DATE_RESULTAT_COL,
                        DATE_PRELEVEMENT_COL,
                        ID_COL
                ))
        ) {
            ps.setLong(1, analyse.getIdRdv());
            ps.setLong(2, analyse.getIdPatient());
            ps.setLong(3, analyse.getIdPersonnel());
            ps.setString(4, analyse.getEtat());
            ps.setLong(5, analyse.getIdTypeAnalyse());
            ps.setString(6, analyse.getResultat());
            ps.setDate(7, DatabaseUtils.toSqlDate(analyse.getDateResultat()));
            ps.setDate(8, DatabaseUtils.toSqlDate(analyse.getDatePrelevement()));
            ps.setLong(9, analyse.getId());

            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public void deleteById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("delete from analyse where id = ?")
        ) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    private Analyse mapRow(ResultSet rs) throws SQLException {
        return new Analyse(
                rs.getLong(ID_COL),
                rs.getLong(RDV_ID_COL),
                rs.getLong(PATIENT_ID_COL),
                rs.getLong(PERSONNEL_ID_COL),
                rs.getString(ETAT_COL),
                rs.getLong(TYPE_ANALYSE_COL),
                rs.getString(RESULTAT_COL),
                DatabaseUtils.fromSqlDate(rs.getDate(DATE_RESULTAT_COL)),
                DatabaseUtils.fromSqlDate(rs.getDate(DATE_PRELEVEMENT_COL))
        );

    }
}
