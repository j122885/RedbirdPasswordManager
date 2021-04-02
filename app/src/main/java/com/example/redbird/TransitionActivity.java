package com.example.redbird;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class TransitionActivity extends AppCompatActivity {


    private String use;//holds username from previous page
    private long mLastClickTime = 0;
    private String master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        System.out.println("working this");
        Intent intent = getIntent(); //get the intent
        master = intent.getStringExtra("masterPass");
        use = intent.getStringExtra("username");
    }


    public void toPassword(View view) throws Exception {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 10000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Intent intt = new Intent(this, MainActivity3.class);
        intt.putExtra("username", use);
        intt.putExtra("masterPass", master);
        startActivity(intt);
    }


    public void logout(View view) throws Exception {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

     try {
          mGoogleSignInClient.signOut()
                  .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          // ...
                      }
                  });
          mGoogleSignInClient.revokeAccess()
                  .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          // ...
                      }
                  });
      } catch(NullPointerException e){
            System.out.println("Google Account sign error");
        }
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
