package com.randez_trying.novel.Models;

public class Credentials {
    private String apiKey;
    private String email;

    private String id;
    private String isActive;
    private String isBanned;
    private String password;
    private String personalId;
    private String signUpDate;

    public Credentials(String id, String personalId, String password, String email, String apiKey, String signUpDate, String isActive, String isBanned) {
        this.id = id;
        this.personalId = personalId;
        this.password = password;
        this.email = email;
        this.apiKey = apiKey;
        this.signUpDate = signUpDate;
        this.isActive = isActive;
        this.isBanned = isBanned;
    }

    public Credentials() {
        this.id = "";
        this.personalId = "";
        this.password = "";
        this.email = "";
        this.apiKey = "";
        this.signUpDate = "";
        this.isActive = "";
        this.isBanned = "";
    }

    public String getId() {return this.id;}
    public void setId(String id) {this.id = id;}
    public String getPersonalId() {return this.personalId;}
    public void setPersonalId(String personalId) {this.personalId = personalId;}
    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}
    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}
    public String getApiKey() {return this.apiKey;}
    public void setApiKey(String apiKey) {this.apiKey = apiKey;}
    public String getSignUpDate() {return this.signUpDate;}
    public void setSignUpDate(String signUpDate) {this.signUpDate = signUpDate;}
    public String getIsActive() {return this.isActive;}
    public void setIsActive(String isActive) {this.isActive = isActive;}
    public String getIsBanned() {return this.isBanned;}
    public void setIsBanned(String isBanned) {this.isBanned = isBanned;}

    @Override
    public String toString() {
        return "Credentials{" +
                "apiKey='" + apiKey + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", isActive='" + isActive + '\'' +
                ", isBanned='" + isBanned + '\'' +
                ", password='" + password + '\'' +
                ", personalId='" + personalId + '\'' +
                ", signUpDate='" + signUpDate + '\'' +
                '}';
    }
}
