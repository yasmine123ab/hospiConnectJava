package org.hospiconnect.service;

import java.sql.SQLException;
import java.util.List;

public interface ICrud<T> {
    void insert(T obj) throws SQLException;
    void update(T obj) throws SQLException;
    void delete(T obj) throws SQLException;
    List<T> findAll() throws SQLException;
}