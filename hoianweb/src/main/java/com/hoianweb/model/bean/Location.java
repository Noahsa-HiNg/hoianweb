package com.hoianweb.model.bean;
import java.util.List;
import java.io.Serializable;

public class Location implements Serializable {

    private int id;
    private String name;        // (was tenDiaDiem)
    private String slug;
    private double longitude;   // (was kinhDo)
    private double latitude;    // (was viDo)
    private String description; // (was moTa)
    private int categoryId;  // (was idTheLoai)
    private String categoryName;
    private List<Image> gallery;
    public Location() {
    }

    public Location(int id, String name, String slug, double longitude, double latitude, String description, int categoryId) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.categoryId = categoryId;
    }

    // Getters and Setters (cho tất cả các biến)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Image> getGallery() {
        return gallery;
    }

    public void setGallery(List<Image> gallery) {
        this.gallery = gallery;
    }
}