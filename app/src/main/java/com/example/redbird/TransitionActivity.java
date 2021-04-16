package com.example.redbird;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class TransitionActivity extends AppCompatActivity {


    private String use;//holds username from previous page
    private long mLastClickTime = 0;
    private String master;
    private boolean biometricLogin;
    private int count = 0;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
    TextView passCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        System.out.println("working this");
        Intent intent = getIntent(); //get the intent
        master = intent.getStringExtra("masterPass");
        use = intent.getStringExtra("username");
        passCounter =  findViewById(R.id.passwordCount);
        DatabaseReference userDb = mDatabase.child("users").child(use.replace(".", "-")).child("storedPasswords");
        ChildEventListener childEventListener = new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                count = (int) snapshot.getChildrenCount();
                passCounter.setText(String.valueOf(count));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userDb.addChildEventListener(childEventListener);

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
