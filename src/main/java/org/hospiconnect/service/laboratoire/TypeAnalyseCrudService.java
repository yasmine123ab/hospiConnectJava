package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.TypeAnalyse;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeAnalyseCrudService {

    private static final TypeAnalyseCrudService instance = new TypeAnalyseCrudService();

    private static final String ID_COL = "id";
    private static final String LIBELLE_COL = "libelle";
    private static final String NOM_COL = "nom";
    private static final String PRIX_COL = "prix";

    public static TypeAnalyseCrudService getInstance() {
        return instance;
    }

    public List<TypeAnalyse> findAll() {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from type_analyse");
                ResultSet rs = ps.executeQuery()
        ) {
            List<TypeAnalyse> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public TypeAnalyse findById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("select * from type_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                throw new RuntimeException("Object TypeAnalyse not found with id " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    public void createNew(TypeAnalyse newTypeAnalyse) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "insert into type_analyse(%s,%s,%s,%s) values (?,?,?,?)",
                        ID_COL,
                        LIBELLE_COL,
                        NOM_COL,
                        PRIX_COL
                ))
        ) {
            ps.setLong(1, newTypeAnalyse.getId());
            ps.setString(2, newTypeAnalyse.getLibelle());
            ps.setString(3, newTypeAnalyse.getNom());
            ps.setFloat(4, newTypeAnalyse.getPrix());

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public boolean update(TypeAnalyse typeAnalyse) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(String.format(
                        "update type_analyse set %s=?, %s=?, %s=? where %s = ?",
                        LIBELLE_COL,
                        NOM_COL,
                        PRIX_COL,
                        ID_COL
                ))
        ) {
            ps.setString(1, typeAnalyse.getLibelle());
            ps.setString(2, typeAnalyse.getNom());
            ps.setFloat(3, typeAnalyse.getPrix());
            ps.setLong(4, typeAnalyse.getId());

            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save due to an SQL Error", e);
        }
    }

    public void deleteById(Long id) {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement ps = c.prepareStatement("delete from type_analyse where id = ?")
        ) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed due to an SQL Error", e);
        }
    }

    private TypeAnalyse mapRow(ResultSet rs) throws SQLException {
        return new TypeAnalyse(
                rs.getLong(ID_COL),
                rs.getString(LIBELLE_COL),
                rs.getString(NOM_COL),
                rs.getFloat(PRIX_COL)
        );

    }







}
