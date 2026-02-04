package com.example.inventoryapp.ui.sms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventoryapp.R;
import com.example.inventoryapp.db.DbExecutor;
import com.example.inventoryapp.repository.UserRepository;
import com.example.inventoryapp.ui.notifications.NotificationActivity;
import com.example.inventoryapp.utils.ValidationUtils;
import com.google.android.material.snackbar.Snackbar;

/**
 * PhoneNumberActivity
 * -------------------
 * Prompts the user to enter a phone number after SMS permission is granted.
 * Validates input using ValidationUtils and persists it via UserRepository.
 * Provides feedback with Snackbars.
 *
 * Flow:
 * - User enters phone number.
 * - Validation ensures format is acceptable.
 * - Repository updates the logged-in user's record.
 * - On success → NotificationActivity (to request notification permission).
 */
public class PhoneNumberActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private Button btnSavePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_number);

        // Handle system insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Bind UI components ---
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSavePhone = findViewById(R.id.btnSavePhone);

        // --- Save button handler ---
        btnSavePhone.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText().toString().trim();

            // Validate phone number
            if (!ValidationUtils.isValidPhoneNumber(phone)) {
                etPhoneNumber.setError("Invalid phone number format");
                return;
            }

            // Persist asynchronously
            DbExecutor.IO.execute(() -> {
                UserRepository repo = new UserRepository(getApplicationContext());

                // Get current logged-in user from SharedPreferences
                int userId = getSharedPreferences("InventoryAppPrefs", MODE_PRIVATE)
                        .getInt("user_id", -1);

                if (userId == -1) {
                    runOnUiThread(() ->
                            Snackbar.make(btnSavePhone, "No logged-in user found", Snackbar.LENGTH_LONG).show()
                    );
                    return;
                }

                int rows = repo.updatePhoneNumber(userId, phone);
                runOnUiThread(() -> {
                    if (rows > 0) {
                        Snackbar.make(btnSavePhone, "Phone number saved successfully", Snackbar.LENGTH_SHORT).show();
                        // After saving number → go to NotificationActivity
                        startActivity(new Intent(PhoneNumberActivity.this, NotificationActivity.class));
                        finish();
                    } else {
                        Snackbar.make(btnSavePhone, "Failed to save phone number", Snackbar.LENGTH_LONG).show();
                    }
                });
            });
        });
    }
}