package com.example.redbird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Base64;

public class MasterActivity extends AppCompatActivity {
EditText masterPassword1;
EditText masterPassword2;
TextView error;
private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        error =(TextView) findViewById(R.id.textView21);
        error.setVisibility(View.INVISIBLE);
        masterPassword1 = (EditText) findViewById(R.id.masterPassword1);
        masterPassword2 = (EditText) findViewById(R.id.masterPassword2);
        Intent intent = getIntent();
        username=  intent.getStringExtra("username");
    }

    public void validate(View view){

        if(masterPassword1.getText().toString().equals(masterPassword2.getText().toString())){
            Intent intent = new Intent(this, TransitionActivity.class);
            intent.putExtra("username", username);
            String pw_hash = BCrypt.hashpw(masterPassword2.getText().toString(), BCrypt.gensalt());
            FireBaseDB db = new FireBaseDB(pw_hash);
            db.inputMasterPW();
            intent.putExtra("masterPass", masterPassword1.getText().toString());
            startActivity(intent);
        }else{
            error.setVisibility(View.VISIBLE);
        }

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