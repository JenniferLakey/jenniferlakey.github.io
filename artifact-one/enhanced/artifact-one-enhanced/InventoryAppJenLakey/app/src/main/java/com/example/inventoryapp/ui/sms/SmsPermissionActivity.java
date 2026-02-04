package com.example.inventoryapp.ui.sms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.inventoryapp.R;
import com.example.inventoryapp.ui.notifications.NotificationActivity;
import com.google.android.material.snackbar.Snackbar;

/**
 * SmsPermissionActivity
 * ---------------------
 * Handles requesting SEND_SMS permission from the user.
 * Provides clear feedback when permission is granted or denied.
 *
 * Flow:
 * - If granted → launches PhoneNumberActivity to collect the user's phone number.
 * - If denied → launches NotificationActivity to continue app setup.
 */
public class SmsPermissionActivity extends AppCompatActivity {

    private Button btnAllow, btnDeny;
    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        // --- Bind UI components ---
        btnAllow = findViewById(R.id.btnAllowSms);
        btnDeny = findViewById(R.id.btnDenySms);

        // --- Allow button handler ---
        btnAllow.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SMS_PERMISSION_CODE
                );
            } else {
                Snackbar.make(btnAllow, "SMS Permission already granted", Snackbar.LENGTH_SHORT).show();
                // Already granted → go straight to PhoneNumberActivity
                startActivity(new Intent(this, PhoneNumberActivity.class));
                finish();
            }
        });

        // --- Deny button handler ---
        btnDeny.setOnClickListener(v -> {
            Snackbar.make(btnDeny, "SMS Permission denied", Snackbar.LENGTH_SHORT).show();
            // Permission denied → go to NotificationActivity
            startActivity(new Intent(this, NotificationActivity.class));
            finish();
        });
    }

    /**
     * Handle the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(btnAllow, "SMS Permission granted", Snackbar.LENGTH_SHORT).show();
                // Launch PhoneNumberActivity after permission granted
                startActivity(new Intent(this, PhoneNumberActivity.class));
                finish();
            } else {
                Snackbar.make(btnAllow, "SMS Permission denied", Snackbar.LENGTH_LONG).show();
                // Permission denied → go to NotificationActivity
                startActivity(new Intent(this, NotificationActivity.class));
                finish();
            }
        }
    }
}