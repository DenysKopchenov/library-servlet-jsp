package com.dkop.library.dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDao<E> extends AutoCloseable {
    void create(E e) throws SQLException;

    List<E> findAll();

    E findById(int id) throws SQLException;

    void update(E e) throws SQLException;

    void delete(int id) throws SQLException;

    void close();
}