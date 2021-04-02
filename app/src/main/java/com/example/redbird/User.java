package com.example.redbird;

import java.io.Serializable;

import javax.crypto.spec.IvParameterSpec;

public class User implements Serializable {
    String uPass;
    String website;
    String username;
    String salt;
    IvParameterSpec iv;

    public User(String website, String username, String uPass, String salt ) {
        this.website = website;
        this.username = username;
        this.uPass = uPass;
        this.salt = salt;


    }

    public String toString() {
        return website + " " + username + " " + uPass + " " + salt + " " + iv;
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
