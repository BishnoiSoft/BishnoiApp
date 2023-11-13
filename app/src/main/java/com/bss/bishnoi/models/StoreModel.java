package com.bss.bishnoi.models;

public class StoreModel {
    String id;
    String title;
    String category;
    Integer price;
    String seller;
    String url;
    String imageUrl;

    public StoreModel(String id, String title, String category, Integer price, String seller, String url, String imageUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.seller = seller;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public StoreModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
