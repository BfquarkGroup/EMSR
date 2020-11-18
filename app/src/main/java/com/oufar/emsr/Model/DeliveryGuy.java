package com.oufar.emsr.Model;

public class DeliveryGuy {


    private String guyId;
    private String guyName;
    private String guyPhone;
    private String guyDescription;
    private String guyImageURL;
    private String guyEmail;
    private String wilaya;
    private String city;

    public DeliveryGuy() {
    }

    public DeliveryGuy(String guyId, String guyName, String guyPhone, String guyDescription, String guyImageURL, String guyEmail, String wilaya, String city) {
        this.guyId = guyId;
        this.guyName = guyName;
        this.guyPhone = guyPhone;
        this.guyDescription = guyDescription;
        this.guyImageURL = guyImageURL;
        this.guyEmail = guyEmail;
        this.wilaya = wilaya;
        this.city = city;
    }

    public String getGuyId() {
        return guyId;
    }

    public void setGuyId(String guyId) {
        this.guyId = guyId;
    }

    public String getGuyName() {
        return guyName;
    }

    public void setGuyName(String guyName) {
        this.guyName = guyName;
    }

    public String getGuyPhone() {
        return guyPhone;
    }

    public void setGuyPhone(String guyPhone) {
        this.guyPhone = guyPhone;
    }

    public String getGuyDescription() {
        return guyDescription;
    }

    public void setGuyDescription(String guyDescription) {
        this.guyDescription = guyDescription;
    }

    public String getGuyImageURL() {
        return guyImageURL;
    }

    public void setGuyImageURL(String guyImageURL) {
        this.guyImageURL = guyImageURL;
    }

    public String getGuyEmail() {
        return guyEmail;
    }

    public void setGuyEmail(String guyEmail) {
        this.guyEmail = guyEmail;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

