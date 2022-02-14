package com.dkop.library.dao.impls;

import com.dkop.library.dao.UserDao;
import com.dkop.library.model.User;
import com.dkop.library.model.exceptions.DoesNotExistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private final Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    public void create(User user) throws SQLException {
        String INSERT_USERS_SQL = "INSERT INTO users" +
                " (first_name, last_name, email, password, role, status) VALUES " +
                " (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getRole());
            preparedStatement.setString(6, user.getStatus());
            preparedStatement.executeUpdate();
        }
    }

//    public void checkUser(String email) throws AlreadyExistException {
//        String SELECT_EMAIL_SQL = "SELECT email FROM users WHERE email = ?;";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EMAIL_SQL)) {
//            preparedStatement.setString(1, email);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    String emailFromDB = resultSet.getString("email");
//                    if (emailFromDB.equals(email)) {
//                        throw new AlreadyExistException("Email " + email + " already exist!");
//                    }
//                }
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }

    public List<User> findAll() {
        List<User> allUsers = new ArrayList<>();
        String SELECT_USERS = "SELECT * FROM users WHERE role NOT LIKE 'admin';";
        try (ResultSet resultSet = connection.createStatement().executeQuery(SELECT_USERS)) {
            while (resultSet.next()) {
                User user = User.newBuilder()
                        .firstName(resultSet.getString("first_name"))
                        .lastName(resultSet.getString("last_name"))
                        .email(resultSet.getString("email"))
                        .role(resultSet.getString("role"))
                        .status(resultSet.getString("status"))
                        .id(resultSet.getInt("id"))
                        .build();
                allUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    @Override
    public User findById(int id) {
        return null;
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(int id) {

    }

    public User findByEmail(String email) throws DoesNotExistException {
        String SELECT_USER = "SELECT * FROM users WHERE email = ?;";
        User user = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = User.newBuilder()
                            .firstName(resultSet.getString("first_name"))
                            .lastName(resultSet.getString("last_name"))
                            .email(resultSet.getString("email"))
                            .password(resultSet.getString("password"))
                            .role(resultSet.getString("role"))
                            .status(resultSet.getString("status"))
                            .id(resultSet.getInt("id"))
                            .build();
                } else {
                    throw new DoesNotExistException(email + " does not exist!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
