package com.noen.controleOficia;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;


@RestController
@RequestMapping("/services")
public class Control {

    private Connection connection;
    Map <Double, Services> servicesMap = new HashMap<>();


    public Control() {
        this.servicesMap = new HashMap<>();
        String dbURL = "jdbc:mysql://192.168.1.115:3306/oficina";
        String dbUser = "admin";
        String dbPassword = "senha123";

        try {
            if (dbURL == null || dbUser == null || dbPassword == null) {
                throw new IllegalArgumentException("Variáveis de ambiente não configuradas corretamente");
            }


            this.connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);


            createTableIfNotExists();
            loadFromDatabase();
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();

        }
    }




    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS services (service_key DOUBLE PRIMARY KEY, nameClient VARCHAR(255), nameService VARCHAR(255), description TEXT, price DOUBLE, status VARCHAR(50), date DATE)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    private void loadFromDatabase() throws SQLException {
        String sql = "SELECT * FROM services";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                double key = resultSet.getDouble("key");
                Services service = new Services(
                        resultSet.getString("nameClient"),
                        resultSet.getString("nameService"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getString("status"),
                        resultSet.getDate("date").toLocalDate()
                );
                servicesMap.put(key, service);
            }
        }
    }


    @PostMapping("/add")
    public String addService(@RequestBody ServiceRequest request) {

        try {
            if (request.getNameClient() == null || request.getNameClient().isEmpty() ||
                    request.getNameService() == null || request.getNameService().isEmpty()) {
                return "Falha ao adicionar serviço, campos inválidos.";
            }

            Services services = new Services(
                    request.getNameClient(),
                    request.getNameService(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getStatus(),
                    LocalDate.now()

            );



            Double key = generateKey();
            servicesMap.put(key, services);

            insertIntoDatabase(key, services);
        } catch (NullPointerException | IllegalArgumentException e) {
            return "Falha ao adicionar serviço, caractere inválido.";
        } catch (Exception e) {
            return "Erro inesperado";
        }
        return "Serviço adicionado com sucesso.";
    }

        public void insertIntoDatabase(Double key, Services service) {

            String sql = "INSERT INTO services (key, nameClient, nameService, description, price, status) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, key); // Ou você pode definir manualmente a chave aqui, se desejar
                statement.setString(2, service.getNameClient());
                statement.setString(3, service.getNameService());
                statement.setString(4, service.getDescription());
                statement.setDouble(5, service.getPrice());
                statement.setString(6, service.getStatus());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }


    public Double generateKey (){


        Double number = 1000d + servicesMap.size();

        while (servicesMap.containsKey((double) number)) {

            number++;

        }

        return number;

    }

    @GetMapping ("/searchName")
    public List<Services> searchName(@RequestBody String nameClient) {
        List<Services> foundServices = new ArrayList<>();

        for (Services services : servicesMap.values()) {
            if (services.getNameClient().contains(nameClient)) {
                foundServices.add(services);
            }
        }

        return foundServices.isEmpty() ? null : foundServices;
    }

    @GetMapping ("/searchKey")
    public Services searchKey(@RequestBody Double key) {
        return servicesMap.get(key);

    }

    @DeleteMapping("/deleteServiceName")
    public void deleteServiceName(@RequestBody String nameClient) {
        Iterator<Map.Entry<Double, Services>> iterator = servicesMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Double, Services> entry = iterator.next();
            Services service = entry.getValue();

            if (service.getNameClient().equals(nameClient)) {
                iterator.remove();
            }
        }
    }

    @DeleteMapping ("/deleteServiceKey")
    public void deleteServiceKey (@RequestBody Double key) {
        Iterator<Map.Entry<Double,Services>> iterator = servicesMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Double, Services> entry = iterator.next();
            Double service = entry.getKey();

            if(service.equals(key)) {
                iterator.remove();
            }

        }
    }

    @GetMapping("/display")
    public List<Services> displayServices() {
        List<Services> serviceList = null;

        serviceList.addAll(servicesMap.values());
        return serviceList;
    }




}
