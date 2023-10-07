package com.randez_trying.novel.Models;

import androidx.annotation.NonNull;

public class User {
    private String bDate;
    private String balance;
    private String boosts;
    private String city;
    private String company;
    private String education;
    private String growth;

    private String id;
    private String interests;
    private String job;
    private String languages;
    private String mediaLinks;
    private String name;
    private String gender;
    private String about;
    private String familyPlans;
    private String relationshipGoals;
    private String sports;
    private String alcohol;
    private String smoke;
    private String personalId;
    private String personalityType;
    private String socialMediaLinks;
    private String status;
    private String subscriptionType;
    private String zodiacSign;
    private String playlist;
    private String location;
    private String talkStyle;
    private String loveLang;
    private String pets;
    private String food;

    public User(String bDate, String balance, String city,
                String company, String education, String growth, String id,
                String interests, String job, String languages, String mediaLinks,
                String name, String gender, String about, String familyPlans,
                String relationshipGoals, String sports, String alcohol, String smoke, String personalId, String personalityType,
                String socialMediaLinks, String status, String subscriptionType, String zodiacSign,
                String playlist, String location, String talkStyle, String loveLang, String pets, String food) {
        this.relationshipGoals = relationshipGoals;
        this.sports = sports;
        this.alcohol = alcohol;
        this.smoke = smoke;
        this.bDate = bDate;
        this.balance = balance;
        this.city = city;
        this.company = company;
        this.education = education;
        this.growth = growth;
        this.id = id;
        this.interests = interests;
        this.job = job;
        this.languages = languages;
        this.mediaLinks = mediaLinks;
        this.name = name;
        this.gender = gender;
        this.about = about;
        this.familyPlans = familyPlans;
        this.personalId = personalId;
        this.personalityType = personalityType;
        this.socialMediaLinks = socialMediaLinks;
        this.status = status;
        this.subscriptionType = subscriptionType;
        this.zodiacSign = zodiacSign;
        this.playlist = playlist;
        this.location = location;
        this.talkStyle = talkStyle;
        this.loveLang = loveLang;
        this.pets = pets;
        this.food = food;
    }

    public User() {
        this.relationshipGoals = "";
        this.sports = "";
        this.alcohol = "";
        this.smoke = "";
        this.bDate = "";
        this.balance = "0";
        this.boosts = "0";
        this.city = "";
        this.company = "";
        this.education = "";
        this.growth = "";
        this.id = "";
        this.interests = "";
        this.job = "";
        this.languages = "";
        this.mediaLinks = "";
        this.name = "";
        this.gender = "";
        this.about = "";
        this.familyPlans = "";
        this.personalId = "";
        this.personalityType = "";
        this.socialMediaLinks = "";
        this.status = "";
        this.subscriptionType = "0";
        this.zodiacSign = "";
        this.playlist = "";
        this.location = "";
        this.talkStyle = "";
        this.loveLang = "";
        this.pets = "";
        this.food = "";
    }
    public String getbDate() {return bDate;}
    public void setbDate(String bDate) {this.bDate = bDate;}
    public String getBalance() {return balance;}
    public void setBalance(String balance) {this.balance = balance;}
    public String getBoosts() {return boosts;}
    public void setBoosts(String boosts) {this.boosts = boosts;}
    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}
    public String getCompany() {return company;}
    public void setCompany(String company) {this.company = company;}
    public String getEducation() {return education;}
    public void setEducation(String education) {this.education = education;}
    public String getGrowth() {return growth;}
    public void setGrowth(String growth) {this.growth = growth;}
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getInterests() {return interests;}
    public void setInterests(String interests) {this.interests = interests;}
    public String getJob() {return job;}
    public void setJob(String job) {this.job = job;}
    public String getLanguages() {
        return languages;
    }
    public void setLanguages(String languages) {
        this.languages = languages;
    }
    public String getMediaLinks() {
        return mediaLinks;
    }
    public void setMediaLinks(String mediaLinks) {
        this.mediaLinks = mediaLinks;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public String getFamilyPlans() {return familyPlans;}
    public void setFamilyPlans(String familyPlans) {
        this.familyPlans = familyPlans;
    }
    public String getRelationshipGoals() {
        return relationshipGoals;
    }
    public void setRelationshipGoals(String relationshipGoals) {
        this.relationshipGoals = relationshipGoals;
    }
    public String getSports() {
        return sports;
    }
    public void setSports(String sports) {
        this.sports = sports;
    }
    public String getAlcohol() {
        return alcohol;
    }
    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }
    public String getSmoke() {
        return smoke;
    }
    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }
    public String getPersonalId() {
        return personalId;
    }
    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }
    public String getPersonalityType() {
        return personalityType;
    }
    public void setPersonalityType(String personalityType) {this.personalityType = personalityType;}
    public String getSocialMediaLinks() {
        return socialMediaLinks;
    }
    public void setSocialMediaLinks(String socialMediaLinks) {this.socialMediaLinks = socialMediaLinks;}
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSubscriptionType() {
        return subscriptionType;
    }
    public void setSubscriptionType(String subscriptionType) {this.subscriptionType = subscriptionType;}
    public String getZodiacSign() {return zodiacSign;}
    public void setZodiacSign(String zodiacSign) {this.zodiacSign = zodiacSign;}
    public String getPlaylist() {return playlist;}
    public void setPlaylist(String string) {this.playlist = string;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public String getTalkStyle() {return talkStyle;}
    public void setTalkStyle(String talkStyle) {this.talkStyle = talkStyle;}
    public String getLoveLang() {return loveLang;}
    public void setLoveLang(String loveLang) {this.loveLang = loveLang;}
    public String getPets() {return pets;}
    public void setPets(String pets) {this.pets = pets;}
    public String getFood() {return food;}
    public void setFood(String food) {this.food = food;}

    @Override
    public String toString() {
        return "User{" +
                "bDate='" + bDate + '\'' +
                ", balance='" + balance + '\'' +
                ", boosts='" + boosts + '\'' +
                ", city='" + city + '\'' +
                ", company='" + company + '\'' +
                ", education='" + education + '\'' +
                ", growth='" + growth + '\'' +
                ", id='" + id + '\'' +
                ", interests='" + interests + '\'' +
                ", job='" + job + '\'' +
                ", languages='" + languages + '\'' +
                ", mediaLinks='" + mediaLinks + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", about='" + about + '\'' +
                ", familyPlans='" + familyPlans + '\'' +
                ", relationshipGoals='" + relationshipGoals + '\'' +
                ", sports='" + sports + '\'' +
                ", alcohol='" + alcohol + '\'' +
                ", smoke='" + smoke + '\'' +
                ", personalId='" + personalId + '\'' +
                ", personalityType='" + personalityType + '\'' +
                ", socialMediaLinks='" + socialMediaLinks + '\'' +
                ", status='" + status + '\'' +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", zodiacSign='" + zodiacSign + '\'' +
                ", playlist='" + playlist + '\'' +
                ", location='" + location + '\'' +
                ", talkStyle='" + talkStyle + '\'' +
                ", loveLang='" + loveLang + '\'' +
                ", pets='" + pets + '\'' +
                ", food='" + food + '\'' +
                '}';
    }
}