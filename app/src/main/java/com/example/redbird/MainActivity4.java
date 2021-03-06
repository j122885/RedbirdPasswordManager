package com.example.redbird;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity4 extends AppCompatActivity {

    EditText inputUrl;
    EditText inputId;
    EditText inputUPass;
    TextView error;
    TextView passwordStrength;
    private String use;
    private String master;
    private long mLastClickTime = 0;
    private long rLastClickTime = 0;
    private final long gLastClickTime = 0;
    ProgressBar simpleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4_main);

        Intent intent = getIntent();
        use = intent.getStringExtra("username");
        master = intent.getStringExtra("masterPass");
        inputUrl = findViewById(R.id.website);
        inputId = findViewById(R.id.username);
        inputUPass = findViewById(R.id.uPass);
        error = findViewById(R.id.error);
        error.setVisibility(View.INVISIBLE);
        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        simpleProgressBar.setMax(100); // 100 maximum value for the progress value

        passwordStrength = findViewById(R.id.passwordStrength);
        inputUPass.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern p = Pattern.compile("[!@@#$%&*()_+=|<>?{}\\\\[\\\\]~-]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(s.toString());
                boolean b = m.find();


                 if(minimumPassword(s.toString() ) && b==false){
                     simpleProgressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                     passwordStrength.setText("Password Strength: Medium");
                }
                else if ( b==true && minimumPassword(s.toString())){
                    simpleProgressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    passwordStrength.setText("Password Strength: Strong");
                }else{
                    simpleProgressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    passwordStrength.setText("Password Strength: Weak");

                }
            }
        });
    }
    public boolean minimumPassword(String pass) {
        ArrayList<Character> chars = new ArrayList<Character>();
        Boolean capital = false;
        Boolean lowercase = false;
        Boolean number = false;
        Boolean notEnoughChars = false;
        if (pass.length() > 7) {
            for (int i = 0; i < pass.length(); i++) {
                if (Character.isUpperCase(pass.charAt(i))) {
                    capital = true;
                }
                if (Character.isLowerCase(pass.charAt(i))) {
                    lowercase = true;
                }
                if (Character.isDigit(pass.charAt(i))) {
                    number = true;
                }
            }
            if (capital && lowercase && number) {
                System.out.println("Password verification passed");
                return true;
            }
        }
        System.out.println("Password verification failed");

        return false;

    }

    public void method1(View view) throws Exception {
        error.setVisibility(View.INVISIBLE);
        String theUrl = inputUrl.getText().toString();
        String theId = inputId.getText().toString();
        String theUPass = inputUPass.getText().toString();

        if (ifEmpty(theUrl, theId, theUPass) == false) {
            Context context = getApplicationContext();
            Toast.makeText(context, "Encrypting...",
                    Toast.LENGTH_SHORT).show();
            FireBaseDB entry = new FireBaseDB(theUrl, theId, theUPass, master);
            entry.createNewWebsitePassword();

            //User test = new User(theUrl, theId, theUPass);
            // list.add(test);
            submit(view);
        } else
            error.setVisibility(View.VISIBLE);
    }

    public boolean ifEmpty(String url, String id, String pass) {
        //you need to change this so that it also rejects it if it only has whitespace(spaces)
        return url.isEmpty() || url.contains(" ") || id.isEmpty() || id.contains(" ") || pass.isEmpty() || pass.contains(" ");
    }


    public static String generatePasswordText(int len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@$";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        Random rand = new Random(); //instance of random class
        int upperbound = 10;
        //generate random values from 0-9
        int int_random = rand.nextInt(upperbound);

        return int_random + sb.toString() + "@";
    }

    public void generatePassword(View view) {
        if (SystemClock.elapsedRealtime() - gLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
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
        intent.putExtra("masterPass", master);
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