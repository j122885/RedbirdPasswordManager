package com.example.redbird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Base64;

public class MasterLoginActivity extends AppCompatActivity {
    EditText masterPassword1;
    TextView error;
    private String username;
    private String response= null;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_login);
        error =(TextView) findViewById(R.id.textView21);
        error.setVisibility(View.INVISIBLE);
        masterPassword1 = (EditText) findViewById(R.id.masterPassword1);
        Intent intent = getIntent();
        username=  intent.getStringExtra("username");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://redbird-password-manger-default-rtdb.firebaseio.com/").getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference login = mDatabase.child("users").child(user.getEmail().replace(".", "-")).child("login");
        login.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    response = String.valueOf(task.getResult().getValue());
                }
            }
        });
    }
    public void validate(View view){

       // FireBaseDB db = new FireBaseDB();
        //String hash = db.getInputMasterPW();
        System.out.println("Hash is " + response );

        if(BCrypt.checkpw(masterPassword1.getText().toString(), response)){
            Intent intent = new Intent(this, TransitionActivity.class);
            intent.putExtra("username", username);
            String originalInput = masterPassword1.getText().toString();
            String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes()); //hash master password
            intent.putExtra("masterPass", masterPassword1.getText().toString());
            startActivity(intent);
        }else{
            error.setVisibility(View.VISIBLE);
        }

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