package com.example.inventoryapp.ui.notifications;

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
import com.example.inventoryapp.ui.inventory.InventoryActivity;
import com.google.android.material.snackbar.Snackbar;

/**
 * NotificationActivity
 * --------------------
 * Requests POST_NOTIFICATIONS permission from the user.
 * If granted, navigates to InventoryActivity.
 * If denied, shows feedback and finishes.
 */
public class NotificationActivity extends AppCompatActivity {

    private Button btnAllow, btnDeny;
    private static final int NOTIF_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Check permission before showing UI ---
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted (or not needed pre-Android 13)
            startActivity(new Intent(this, InventoryActivity.class));
            finish();
            return;
        }

        // --- Otherwise show the request UI ---
        setContentView(R.layout.activity_notification);

        btnAllow = findViewById(R.id.btnAllowNotif);
        btnDeny = findViewById(R.id.btnDenyNotif);

        btnAllow.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIF_PERMISSION_CODE
            );
        });

        btnDeny.setOnClickListener(v -> {
            Snackbar.make(btnDeny, "Notifications denied", Snackbar.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIF_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(btnAllow, "Notifications allowed", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
            } else {
                Snackbar.make(btnAllow, "Notifications denied", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }
}