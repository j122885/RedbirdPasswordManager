package com.example.redbird;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class Settings extends AppCompatActivity {
    private long rLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void showAlertDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DeleteAccountFragment alertDialog = DeleteAccountFragment.newInstance("Delete Account");
        alertDialog.setCancelable(false);
        alertDialog.show(fm, "fragment_alert");
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