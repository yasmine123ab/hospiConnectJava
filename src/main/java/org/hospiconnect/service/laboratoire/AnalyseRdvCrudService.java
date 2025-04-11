package org.hospiconnect.service.laboratoire;

import org.hospiconnect.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AnalyseRdvCrudService {

    private static final AnalyseRdvCrudService instance = new AnalyseRdvCrudService();

    public static AnalyseRdvCrudService getInstance() {
        return instance;
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
}
