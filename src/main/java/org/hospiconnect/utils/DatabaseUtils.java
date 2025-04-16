package org.hospiconnect.utils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DatabaseUtils {

    private static final String url = "jdbc:mysql://127.0.0.1:3307/HISTO";
    private static final String user = "root";
    private static final String password = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static Date toSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    public static LocalDate fromSqlDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    public static Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
        return localDateTime != null ? Timestamp.valueOf(localDateTime) : null;
    }

    public static LocalDateTime fromSqlTimestamp(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
