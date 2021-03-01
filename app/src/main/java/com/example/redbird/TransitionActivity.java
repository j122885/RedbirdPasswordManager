package com.example.redbird;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class TransitionActivity extends AppCompatActivity {


    String use;//holds username from previous page
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        System.out.println("working this");
        Intent intent = getIntent(); //get the intent

        use = intent.getStringExtra("username");


    }


    public void toPassword(View view) throws Exception {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 10000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();


        Intent intt = new Intent(this, MainActivity3.class);


        intt.putExtra("username", use);

        startActivity(intt);
    }



    public void logout(View view) throws Exception {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 100000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

//        MainActivity.mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
       MainActivity.mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });



        FirebaseAuth.getInstance().signOut();









        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean shouldAllowBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {

        }
    }

}
