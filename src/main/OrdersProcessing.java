package main;

import main.dao.impl.CustomerDAOImpl;
import main.dao.impl.OrderDAOImpl;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


class OrdersProcessingThread extends Thread {
    @Override
    public void run() {
        while (true) {
            File inputDirectory = new File("data/input");
            File[] files = inputDirectory.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    processJsonThread(file);
                }
            }

            try {
                // Attendre une heure (3600000 ms)
                Thread.sleep(3600000);
            } catch (InterruptedException e) {
                System.out.println("Thread interrompu : " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processJsonThread(File file) {
        File outputDirectory = new File("data/output");
        File errorDirectory = new File("data/error");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Lire tout le contenu du fichier JSON
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Créer un objet JSON et extraire la clé "customer_id"
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            int customerId = jsonObject.getInt("customer_id");

            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            if (customerDAO.customerExists(customerId)) {
                processValidOrder(jsonObject, customerId, outputDirectory, file);
            } else {
                moveToErrorDirectory(jsonObject, errorDirectory, file);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + file.getName());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement du fichier JSON : " + file.getName());
            e.printStackTrace();
        } finally {
            // Vérifiez si le fichier peut être supprimé
            if (file.exists() && !file.delete()) {
                System.err.println("Impossible de supprimer le fichier : " + file.getName());
            }
        }
    }

    private void processValidOrder(JSONObject jsonObject, int customerId, File outputDirectory, File file) {
        try {
            Order order = new Order(
                    jsonObject.getInt("id"),
                    LocalDateTime.parse(jsonObject.getString("date")),
                    jsonObject.getDouble("amount"),
                    customerId,
                    jsonObject.getString("status")
            );
            OrderDAOImpl orderDAO = new OrderDAOImpl();
            orderDAO.saveOrder(order);

            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            Customer customer = customerDAO.getCustomer(customerId);
            String customerJsonObject = customerToJson(customer);

            // Supprimer "customer_id" et ajouter "customer"
            jsonObject.remove("customer_id");
            jsonObject.put("customer", new JSONObject(customerJsonObject));

            // Sauvegarder le fichier dans le répertoire "data/output"
            File outputFile = new File(outputDirectory, file.getName());
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(jsonObject.toString(4)); // Beautify JSON avec 4 espaces
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'ordre valide : " + file.getName());
            e.printStackTrace();
        }
    }

    private void moveToErrorDirectory(JSONObject jsonObject, File errorDirectory, File file) {
        try {
            File errorFile = new File(errorDirectory, file.getName());
            try (FileWriter writer = new FileWriter(errorFile)) {
                writer.write(jsonObject.toString(4)); // Beautify JSON avec 4 espaces
            }

            // Supprimer le fichier d'entrée
            /*if (!file.delete()) {
                System.err.println("Erreur lors de la suppression du fichier : " + file.getName());
            }*/
        } catch (IOException e) {
            System.err.println("Erreur lors du déplacement vers le répertoire d'erreur : " + file.getName());
            e.printStackTrace();
        }
    }

    private String customerToJson(Customer customer) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", customer.getId());
        jsonObject.put("nom", customer.getNom());
        jsonObject.put("email", customer.getEmail());
        jsonObject.put("phone", customer.getPhone());

        return jsonObject.toString(); // Retourne la chaîne JSON
    }
}


public class OrdersProcessing {
    public static void main(String[] args) {
        String filePath = "data/orders.txt"; // Remplacez par le chemin réel
        List<Order> orders = readOrdersFromFile(filePath);
        clearFileContent(filePath);
        saveOrdersAsJson(orders,"data/input");
        OrdersProcessingThread thread = new OrdersProcessingThread();
        thread.start(); // Lancer le thread
    }
    public static List<Order> readOrdersFromFile(String filePath) {
        List<Order> orders = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Diviser chaque ligne par la virgule
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    LocalDateTime date = LocalDateTime.parse(parts[1].trim(), formatter);
                    double amount = Double.parseDouble(parts[2].trim());
                    int customerId = Integer.parseInt(parts[3].trim());
                    String status = parts[4].trim();

                    orders.add(new Order(id, date, amount, customerId, status));
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return orders;
    }

    // Méthode pour vider le contenu du fichier
    public static void clearFileContent(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(""); // Écrire une chaîne vide pour vider le fichier
        } catch (IOException e) {
            System.err.println("Erreur lors du vidage du fichier : " + e.getMessage());
        }
    }
    // Méthode pour sauvegarder les commandes en fichiers JSON
    public static void saveOrdersAsJson(List<Order> orders, String directoryPath) {
        // Vérifiez que le dossier existe, sinon créez-le
        File directory = new File(directoryPath);
        // Parcourir chaque commande et générer un fichier JSON
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for (Order order : orders) {
            try {
                // Créer l'objet JSON
                JSONObject json = new JSONObject();
                json.put("id", order.getId());
                json.put("date", order.getDate().format(formatter));
                json.put("amount", order.getAmount());
                json.put("customer_id", order.getCustomer_id());
                json.put("status", order.getStatus());

                // Générer un nom de fichier unique pour chaque commande
                String fileName = directoryPath + File.separator + "order_" + System.currentTimeMillis() + ".json";

                // Sauvegarder le fichier JSON
                try (FileWriter writer = new FileWriter(fileName)) {
                    writer.write(json.toString(4)); // Beautify le JSON avec une indentation de 4 espaces
                }

            } catch (IOException e) {
                System.err.println("Erreur lors de la création du fichier JSON pour l'ordre ID " + order.getId() + ": " + e.getMessage());
            }
        }
    }
}
