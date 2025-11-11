package com.hoianweb.model.bean;

import java.io.Serializable;

public class Image implements Serializable {

    private int id;
    private String imageUrl;    // (was urlHinhAnh)
    private int locationId;  // (was idDiaDiem)

    public Image() {
    }

    public Image(int id, String imageUrl, int locationId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.locationId = locationId;
    }
    public Image(String imageUrl, int locationId) {
    	this.imageUrl = imageUrl;
        this.locationId = locationId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
}