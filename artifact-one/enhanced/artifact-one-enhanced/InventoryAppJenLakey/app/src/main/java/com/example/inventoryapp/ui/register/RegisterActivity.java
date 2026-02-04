package com.example.inventoryapp.ui.register;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;
import com.example.inventoryapp.db.DbExecutor;
import com.example.inventoryapp.repository.UserRepository;
import com.example.inventoryapp.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * RegisterActivity
 * ----------------
 * Handles new user registration.
 * Collects username, password, and role.
 * Validates input and persists user via UserRepository.
 * Provides feedback with Snackbars.
 *
 * Best practice:
 * - Validate all required fields before persistence.
 * - Keep optional fields (like phone number) out of registration if they depend on permissions.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI components
    private EditText etNewUsername, etNewPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Toolbar setup ---
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle(getString(R.string.register_page));
        }

        // --- Bind UI components ---
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // --- Setup role spinner adapter ---
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Admin", "Staff", "Owner", "Auditor"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // --- Create account handler ---
        btnCreateAccount.setOnClickListener(v -> {
            String u = etNewUsername.getText().toString().trim();
            String p = etNewPassword.getText().toString();
            String c = etConfirmPassword.getText().toString();
            String role = spinnerRole.getSelectedItem().toString();

            // --- Validation using ValidationUtils ---
            if (!ValidationUtils.isValidUsername(u)) {
                etNewUsername.setError("Invalid username (min 3 chars)");
                return;
            }
            if (!ValidationUtils.isValidPassword(p)) {
                etNewPassword.setError("Invalid password (min 6 chars)");
                return;
            }
            if (!ValidationUtils.doPasswordsMatch(p, c)) {
                etConfirmPassword.setError("Passwords must match");
                return;
            }

            // --- Persist user asynchronously ---
            DbExecutor.IO.execute(() -> {
                UserRepository repo = new UserRepository(getApplicationContext());
                try {
                    // Pass null for phone number at registration
                    boolean ok = repo.register(u, p, role, null);
                    runOnUiThread(() -> {
                        if (ok) {
                            Snackbar.make(btnCreateAccount, "Account created successfully", Snackbar.LENGTH_SHORT).show();
                            finish(); // return to login
                        } else {
                            Snackbar.make(btnCreateAccount, "Registration failed", Snackbar.LENGTH_LONG).show();
                            etNewUsername.setError("Registration failed");
                        }
                    });
                } catch (Exception ex) {
                    runOnUiThread(() ->
                            Snackbar.make(btnCreateAccount, "Error: " + ex.getMessage(), Snackbar.LENGTH_LONG).show()
                    );
                }
            });
        });
    }
}