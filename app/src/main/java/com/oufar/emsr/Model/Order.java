package com.oufar.emsr.Model;

public class Order {

    private String clientId;
    private String clientName;
    private String clientPhone;
    private String clientAddress;
    private String clientImageURL;

    public Order(String clientId, String clientName, String clientPhone, String clientAddress, String clientImageURL) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientAddress = clientAddress;
        this.clientImageURL = clientImageURL;
    }

    public Order() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientImageURL() {
        return clientImageURL;
    }

    public void setClientImageURL(String clientImageURL) {
        this.clientImageURL = clientImageURL;
    }
}
