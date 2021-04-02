package com.example.redbird;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class FireBaseDB implements LifecycleOwner {

    private String website;
    private String userName;
    private String password;
    private FirebaseAuth mAuth;
    private String masterPassword;
    private IvParameterSpec ivParameterSpec;
    private String hash;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
    String response = null;

    public FireBaseDB() {

    }

    public FireBaseDB(String website, String userName, String password, String masterPassword) {
        this.website = website;
        this.userName = userName;
        this.password = password;
        this.masterPassword = masterPassword;
        System.out.print("New entry created ");
    }
    public FireBaseDB(String hash){
        this.hash = hash;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewWebsitePassword() throws Exception {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-"));
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("website").setValue(website);
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("userName").setValue(userName);
        String salt = new String(getNextSalt());
        String cipherText = encrypt(masterPassword, salt, password);
        //System.out.println("Decrypted password is " + decrypt(cipherText,salt, masterPassword, ivParameterSpec ));
        AsyncTask<Void, Void, String> response = new IPFSConfig(null, cipherText, true, false).execute(); //start instance of IPFS
        try {

            System.out.println("Response from main activity: " + response.get()); //user response .get() to retrieve result from AsyncTask- put result into password db?
            mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("password").setValue(response.get()); //Stores encrypted password inside firebase
            mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("storedPasswords").child("websites").child(website.replace(".", "-")).child("salt").setValue(salt);
            StorageReference ivRef = storageRef.child(user.getEmail().replace(".", "-")).child(website.replace(".", "-")).child("iv");
            ivRef.putBytes(ivParameterSpec.getIV());


            //hash gets stored in firebase - make sure in activity 3 that you are fetching the correct password from IPFS
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Entry added to db");
    }
    public void inputMasterPW(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(user.getEmail().replace(".", "-"));
        mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("login").setValue(hash);
    }


    public String encrypt(String masterPassword, String salt, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        ivParameterSpec = AESUtil.generateIv();
        SecretKey key = AESUtil.getKeyFromPassword(masterPassword,salt);
        String cipherText = AESUtil.encryptPasswordBased(password, key, ivParameterSpec);
        return cipherText;
    }

    public static String decrypt(String cipherText, String salt, String masterPassword, IvParameterSpec ivParameterSpec) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        SecretKey key = AESUtil.getKeyFromPassword(masterPassword,salt);
        String decryptedCipherText = AESUtil.decryptPasswordBased( cipherText, key, ivParameterSpec);
        return decryptedCipherText;

    }
    public static byte[] getNextSalt() {
        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}
