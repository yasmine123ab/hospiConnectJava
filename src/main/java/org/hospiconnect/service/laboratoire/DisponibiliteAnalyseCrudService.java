package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisponibiliteAnalyseCrudService {

    private static final DisponibiliteAnalyseCrudService instance = new DisponibiliteAnalyseCrudService();

    private static final String ID_COL = "id";
    private static final String DATE_DISPO_COL = "date_dispo";
    private static final String HEURE_DEBUT_COL = "heure_debut";
    private static final String HEURE_FIN_COL = "heure_fin";
    private static final String NBR_PLACES_COL = "nb_places";

    public static DisponibiliteAnalyseCrudService getInstance() {
        return instance;
    }

    public List<DisponibiliteAnalyse> findAll() {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from disponibilite_analyse");
                ResultSet rs = ps.executeQuery()
        ) {
            List<DisponibiliteAnalyse> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public DisponibiliteAnalyse findById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from disponibilite_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                throw new RuntimeException("Object DisponibiliteAnalyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public String findDispoPlaceById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select nb_places from disponibilite_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nb_places");
                }
                throw new RuntimeException("Object Dispo Analyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public String findDispoHDebutById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select heure_debut from disponibilite_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("heure_debut");
                }
                throw new RuntimeException("Object Dispo Analyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public void createNew(DisponibiliteAnalyse newDisponibilite) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "insert into disponibilite_analyse(%s,%s,%s,%s) values (?,?,?,?)",
                        DATE_DISPO_COL,
                        HEURE_DEBUT_COL,
                        HEURE_FIN_COL,
                        NBR_PLACES_COL
                ))
        ) {
            ps.setDate(1, DatabaseUtils.toSqlDate(newDisponibilite.getDispo()));
            ps.setTime(2, Time.valueOf(newDisponibilite.getDebut()));
            ps.setTime(3, Time.valueOf(newDisponibilite.getFin()));
            ps.setLong(4, newDisponibilite.getNbrPlaces());

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public boolean update(DisponibiliteAnalyse disponibilite) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "update disponibilite_analyse set %s=?, %s=?, %s=?, %s=? where %s = ?",
                        DATE_DISPO_COL,
                        HEURE_DEBUT_COL,
                        HEURE_FIN_COL,
                        NBR_PLACES_COL,
                        ID_COL
                ))
        ) {
            ps.setDate(1, DatabaseUtils.toSqlDate(disponibilite.getDispo()));
            ps.setTime(2, Time.valueOf(disponibilite.getDebut()));
            ps.setTime(3, Time.valueOf(disponibilite.getFin()));
            ps.setLong(4, disponibilite.getNbrPlaces());
            ps.setLong(5, disponibilite.getId());

            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public void deleteById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("delete from disponibilite_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    private DisponibiliteAnalyse mapRow(ResultSet rs) throws SQLException {
        Date dateDispo = rs.getDate(DATE_DISPO_COL);
        return new DisponibiliteAnalyse(
                rs.getLong(ID_COL),
                rs.getDate(DATE_DISPO_COL).toLocalDate(),   // LocalDate
                rs.getTime(HEURE_DEBUT_COL).toLocalTime(),  // LocalTime
                rs.getTime(HEURE_FIN_COL).toLocalTime(),
                rs.getInt(NBR_PLACES_COL)
        );

    }
}
