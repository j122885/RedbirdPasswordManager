package com.example.redbird;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

public class FireBaseDB {

    private String website;
    private String userName;
    private String password;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();

    public FireBaseDB() {

    }

    public FireBaseDB(String website, String userName, String password) {
        this.website = website;
        this.userName = userName;
        this.password = password;
        System.out.print("New entry created ");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewWebsitePassword() throws Exception {
        Kimetsu kimetsu = new Kimetsu();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-"));
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("website").setValue(website);
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("userName").setValue(userName);
        //mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("password").setValue(Kimetsu.encrypt(password));
        //new IPFSConfig(null, password, true, false).execute(); //start instance of IPFS - return hash for storage?
        AsyncTask<Void, Void, String> response = new IPFSConfig(null, password, true, false).execute(); //start instance of IPFS
        try {
            System.out.println("Response from main activity: " + response.get()); //user response .get() to retrieve result from AsyncTask- put result into password db?
            mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("password").setValue(Kimetsu.encrypt(response.get()));
            //hash gets stored in firebase - make sure in activity 3 that you are fetching the correct password from IPFS
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Entry added to db");
    }


}
