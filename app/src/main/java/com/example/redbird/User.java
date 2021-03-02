package com.example.redbird;

import java.io.Serializable;

public class User implements Serializable {
    String uPass;
    String website;
    String username; //Username


    public User(String website, String username, String uPass) {
        this.website = website;
        this.username = username;
        this.uPass = uPass;


    }

    public String toString() {
        return website + " " + username + " " + uPass + " ";
    }

    public String getuPass() {
        return uPass;
    }

    public void setuPass(String uPass) {
        this.uPass = uPass;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
