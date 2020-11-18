package com.oufar.emsr.Model;

public class Plate {

    private String id;
    private String plate;
    private String price;
    private String description;
    private String imageURL;
    private String number;
    private String status;
    private String clientId;
    private String availability;

    public Plate(String id, String plate, String price, String description, String imageURL, String number, String status, String clientId, String availability) {
        this.id = id;
        this.plate = plate;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.number = number;
        this.status = status;
        this.clientId = clientId;
        this.availability = availability;
    }

    public Plate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
