package com.example.schoolattendence;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ApiService apiService;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if the required permissions are granted
        if (!checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
                || !checkPermission(android.Manifest.permission.INTERNET))
                 {
            // Request the required permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.INTERNET,
            }, REQUEST_CODE_PERMISSIONS);
        } else {
            // The required permissions are already granted
            setupActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                setupActivity();
            } else {
                // permission denied, close the app
                Toast.makeText(this, "Permissions are required to use this app", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    private void setupActivity() {
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.login_button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.trifrnd.in/services/eng/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            login(username, password);
        });

    }
    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void login(String username, String password) {
        apiService.login(username, password).enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                switch (response.body()) {
                    case "Unregistered": {
                        Intent intentu = new Intent(MainActivity.this, Teacher1Activity.class);
                        intentu.putExtra("username", username);
                        startActivity(intentu);
                        finish();
                        break;
                    }

                    case "Active": {
                        Intent intenta = new Intent(MainActivity.this, TeacherActivity.class);
                        intenta.putExtra("username", username);
                        startActivity(intenta);
                        finish();
                        break;
                    }
                    default:
                        Toast.makeText(MainActivity.this, "Error: Invalid username or password", Toast.LENGTH_LONG).show();
                        break;
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: Failed to connect to server" , Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}