package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.Customer;
import main.util.DataBaseConnectivity;

public class CustomerDAO {

    public boolean customerExists(int id) {
        String query = "SELECT COUNT(*) FROM customer WHERE id = ?";
        try (Connection connection = DataBaseConnectivity.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;  // Retourne true si le client existe, sinon false
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Retourne false en cas d'exception ou si aucun résultat
    }
    public Customer getCustomer(int id) {
        String query = "SELECT nom,email,phone FROM customer WHERE id = ?";
        try (Connection connection = DataBaseConnectivity.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Customer customer = new Customer(id, resultSet.getString("nom"), resultSet.getString("email"), resultSet.getString("phone"));
                return customer;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
