package com.example.inventoryapp.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inventoryapp.R;
import com.example.inventoryapp.db.DbExecutor;
import com.example.inventoryapp.model.User;
import com.example.inventoryapp.repository.UserRepository;
import com.example.inventoryapp.ui.notifications.NotificationActivity;
import com.example.inventoryapp.ui.register.RegisterActivity;
import com.example.inventoryapp.ui.sms.SmsPermissionActivity;
import com.example.inventoryapp.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * LoginActivity
 * -------------
 * Handles user login by authenticating credentials against the UserRepository.
 * Stores session info in SharedPreferences.
 *
 * Flow after login success:
 * - If SMS permission missing → SmsPermissionActivity
 * - If SMS permission granted → NotificationActivity (skip phone number, already saved)
 */
public class LoginActivity extends AppCompatActivity {

    // UI components
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;

    // SharedPreferences constants
    private static final String PREFS_NAME = "InventoryAppPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- Toolbar setup ---
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle(getString(R.string.login_page));
        }

        // --- Bind UI components ---
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // --- Login button handler ---
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            // Validation using ValidationUtils
            if (!ValidationUtils.isValidUsername(username)) {
                etUsername.setError("Invalid username (min 3 chars)");
                return;
            }
            if (!ValidationUtils.isValidPassword(password)) {
                etPassword.setError("Invalid password (min 6 chars)");
                return;
            }

            // Authenticate asynchronously
            DbExecutor.IO.execute(() -> {
                UserRepository repo = new UserRepository(getApplicationContext());
                try {
                    User user = repo.authenticate(username, password);
                    runOnUiThread(() -> {
                        if (user != null) {
                            // Save session info including userId
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_USERNAME, user.username);
                            editor.putString(KEY_ROLE, user.role);
                            editor.putInt(KEY_USER_ID, user.userId);
                            editor.apply();

                            Snackbar.make(btnLogin, "Login successful", Snackbar.LENGTH_SHORT).show();

                            // --- Flow branching ---
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // SMS permission missing → SmsPermissionActivity
                                startActivity(new Intent(this, SmsPermissionActivity.class));
                                finish();
                            } else {
                                // SMS permission already granted → NotificationActivity
                                startActivity(new Intent(this, NotificationActivity.class));
                                finish();
                            }
                        } else {
                            Snackbar.make(btnLogin, "Invalid credentials", Snackbar.LENGTH_LONG).show();
                            etPassword.setError("Invalid credentials");
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Snackbar.make(btnLogin, "Login failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show()
                    );
                }
            });
        });

        // --- Register button handler ---
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }
}