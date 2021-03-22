package com.example.redbird;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;

public class MainActivity4 extends AppCompatActivity {

    EditText inputUrl;
    EditText inputId;
    EditText inputUPass;
    TextView error;
    String use;
    private long mLastClickTime = 0;
    private long rLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4_main);

        Intent intent = getIntent();
        use = intent.getStringExtra("username");
        inputUrl = (EditText) findViewById(R.id.website);
        inputId = (EditText) findViewById(R.id.username);
        inputUPass = (EditText) findViewById(R.id.uPass);
        error = findViewById(R.id.error);
        error.setVisibility(View.INVISIBLE);


    }

    public void method1(View view) throws Exception {
        error.setVisibility(View.INVISIBLE);
        String theUrl = inputUrl.getText().toString();
        String theId = inputId.getText().toString();
        String theUPass = inputUPass.getText().toString();


        if (ifEmpty(theUrl, theId, theUPass) == false) {
            FireBaseDB entry = new FireBaseDB(theUrl, theId, theUPass);
            entry.createNewWebsitePassword();
            User test = new User(theUrl, theId, theUPass);
            // list.add(test);
            submit(view);
        } else
            error.setVisibility(View.VISIBLE);
    }

    public boolean ifEmpty(String url, String id, String pass) {
        if (url.isEmpty() || url.contains(" ") || id.isEmpty() || id.contains(" ") || pass.isEmpty() || pass.contains(" ")) { //you need to change this so that it also rejects it if it only has whitespace(spaces)
            return true;
        } else
            return false;
    }


    public static String generatePasswordText(int len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@$";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    public void generatePassword(View view) {
        String generated = generatePasswordText(15);
        inputUPass.setText(generated);
    }

    public void submit(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();


        Intent intent = new Intent(this, MainActivity3.class);
        intent.putExtra("username", use);
        startActivity(intent);

    }


    private boolean shouldAllowBack() {
        return true;
    }

    @Override
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