package com.example.redbird;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import javax.crypto.spec.IvParameterSpec;

//Grace
public class MainActivity2 extends AppCompatActivity {
    private String pass;

    TextView tv;
    TextView tv1;
    TextView tv2;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
    private String website;
    private String username;
    private  String websiteUserName;
    private String master;
    private String salt;
    //private IvParameterSpec iv;
    private String stringIvHolder;
    private long rLastClickTime = 0;
    private DatabaseReference userDb;

    protected void onCreate(Bundle savedInstanceState) {
//        try {
//            Kimetsu kimetsu = new Kimetsu();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);


        Intent intent = getIntent(); //get the intent
        username = intent.getStringExtra("username");
        master = intent.getStringExtra("masterPass");
        salt = intent.getStringExtra("salt");
        website = intent.getStringExtra("website");
        websiteUserName = intent.getStringExtra("user");//for the specific list item you click on








        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ivRef = storageRef.child(user.getEmail().replace(".", "-")).child(website.replace(".", "-")).child("iv");

        final long ONE_MEGABYTE = 1024 * 1024;
        ivRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("Success", "iv successfully downloaded");
                IvParameterSpec iv =  new IvParameterSpec(bytes);
                System.out.println("length of iv is " + iv.getIV().length);
                try {
                    Intent intent = getIntent(); //get the intent

                    String tempPassholder = intent.getStringExtra("pass");
                    username = intent.getStringExtra("username");
                    master = intent.getStringExtra("masterPass");
                    salt = intent.getStringExtra("salt");
                    website = intent.getStringExtra("website");
                    websiteUserName = intent.getStringExtra("user");//for the specific list item you click on
                    AsyncTask<Void, Void, String> response = new IPFSConfig(tempPassholder, FireBaseDB.decrypt(pass, salt, master, iv), false, true).execute();
                    pass = response.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }



                tv = findViewById(R.id.website);
                tv1 = findViewById(R.id.username);
                tv2 = findViewById(R.id.pass);


                tv.setText(website);//calling parts
                tv1.setText(websiteUserName);
                tv2.setText(pass);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("Failure", "iv not downloaded" );

            }
        });





























    }


    public void delete(View view) throws IOException {
        if (SystemClock.elapsedRealtime() - rLastClickTime < 1000) {
            return;
        }
        rLastClickTime = SystemClock.elapsedRealtime();
        userDb = mDatabase.child("users").child(username).child("storedPasswords").child("websites").child(website.replace(".", "-"));
        userDb.removeValue();
        Intent intent = new Intent(this, MainActivity3.class);
        intent.putExtra("username", username);
        startActivity(intent);

    }

    public void copyUsername(View view){
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("Username copied to clipboard", websiteUserName);
        clipboard.setPrimaryClip(clip);

        //alerts user that username text has been copied
        Context context = getApplicationContext();
        CharSequence text = clip.getDescription().getLabel();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public void copyPassword(View view){
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("Password copied to clipboard", pass);
        clipboard.setPrimaryClip(clip);

        //alerts user that password text has been copied
        Context context = getApplicationContext();
        CharSequence text = clip.getDescription().getLabel();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean shouldAllowBack() {
        return true;
    }

    @Override //back button
    public void onBackPressed() {
        if (shouldAllowBack()) {
            if (SystemClock.elapsedRealtime() - rLastClickTime < 1000) {
                return;
            }
            rLastClickTime = SystemClock.elapsedRealtime();
            super.onBackPressed();
        } else {
        }
    }

    public void back(View view) {
        if (shouldAllowBack()) {
            if (SystemClock.elapsedRealtime() - rLastClickTime < 1000) {
                return;
            }
            rLastClickTime = SystemClock.elapsedRealtime();
            super.onBackPressed();

        }

    }


}





