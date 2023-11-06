package com.randez_trying.novel.Models;

public class Place {

    private String categoryId;
    private String city;
    private String name;
    private String description;
    private String image;
    private String address;
    private String link;

    public Place(String categoryId, String city, String name, String description, String image, String address, String link) {
        this.categoryId = categoryId;
        this.city = city;
        this.name = name;
        this.description = description;
        this.image = image;
        this.address = address;
        this.link = link;
    }

    public Place() {}
    public String getCategoryId() {return categoryId;}
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getImage() {return image;}
    public void setImage(String image) {this.image = image;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getLink() {return link;}
    public void setLink(String link) {this.link = link;}
}