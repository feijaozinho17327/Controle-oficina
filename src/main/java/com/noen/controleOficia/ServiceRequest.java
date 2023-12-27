package com.noen.controleOficia;

import java.util.Date;

public class ServiceRequest {


    private String nameClient;
    private String nameService;
    private String description;
    private double price;
    private String status;


    public ServiceRequest(String nameClient, String nameService, String description, double price, String status) {

        this.nameClient = nameClient;
        this.nameService = nameService;
        this.description = description;
        this.price = price;
        this.status = status;

    }



    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    public String getNameService() {
        return nameService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
