package com.bss.bishnoi.models;

public class TirthModel {
    private String imageUrl, title, longitude, latitude, description;

    public TirthModel() {
    }

    public TirthModel(String title, String description, String imageUrl,  String longitude, String latitude) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
