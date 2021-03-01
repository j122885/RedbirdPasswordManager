package com.example.redbird;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

//Grace
public class MainActivity2 extends AppCompatActivity {
    String pass;

    TextView tv;
    TextView tv1;
    TextView tv2;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
    String website;
    String username;
    String websiteUserName;
    private long rLastClickTime = 0;
    private DatabaseReference userDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);



        Intent intent = getIntent(); //get the intent
        username = intent.getStringExtra("username");
        pass = intent.getStringExtra("pass");
        website = intent.getStringExtra("website");
        websiteUserName = intent.getStringExtra("user");//for the specific list item you click on




        tv = findViewById(R.id.website);
        tv1 = findViewById(R.id.username);
        tv2 = findViewById(R.id.pass);


        tv.setText(website);//calling parts
        tv1.setText(websiteUserName);
        tv2.setText(pass);
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





