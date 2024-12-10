package main.dao.impl;

import main.Order;
import main.dao.OrderDAO;
import main.util.DataBaseConnectivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDAOImpl implements OrderDAO {

    /**
     * Sauvegarde un objet Order dans la base de données.
     *
     * @param order L'objet Order à sauvegarder.
     */
    public void saveOrder(Order order) {
        String sql = "INSERT INTO orders (id, date, amount, customer_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DataBaseConnectivity.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Remplir les paramètres de la requête SQL
            statement.setInt(1, order.getId());
            statement.setString(2, order.getDate().toString()); // Convertir LocalDateTime en String
            statement.setDouble(3, order.getAmount());
            statement.setInt(4, order.getCustomer_id());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la commande : " + e.getMessage());
        }
    }
}
