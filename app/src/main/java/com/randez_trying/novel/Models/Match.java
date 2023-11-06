package com.randez_trying.novel.Models;

public class Match {

    private String userOne;
    private String userTwo;

    public Match(String userOne, String userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
    }

    public Match() {}
    public String getUserOne() {return userOne;}
    public void setUserOne(String userOne) {this.userOne = userOne;}
    public String getUserTwo() {return userTwo;}
    public void setUserTwo(String userTwo) {this.userTwo = userTwo;}
}