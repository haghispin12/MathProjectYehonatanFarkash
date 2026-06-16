package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectyehonatan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityLogin extends AppCompatActivity {
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPass;
    private FirebaseAuth auth1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        initviews();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth1 = FirebaseAuth.getInstance();

        if(auth1.getCurrentUser()!=null){
            loginTospecificActivity();
            int n=0;
        }

    }

    private void initviews() {
        btnLogin = findViewById(R.id.btnLogIn);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                prefs.edit().putBoolean("isManager", false).apply();
                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(MainActivityLogin.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ניסיון התחברות לפיירבייס
                auth1.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(MainActivityLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (auth1.getCurrentUser() != null) {
                                        loginTospecificActivity();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // החזרת הודעת השגיאה ל-Toast
                                Toast.makeText(MainActivityLogin.this, "שגיאה בהתחברות: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void loginTospecificActivity() {
        if (auth1.getCurrentUser() == null) return;

        String email = auth1.getCurrentUser().getEmail();

        // מנהל מפעל
        if (email.charAt(0) == 'b' && email.length() == 15) {
            Toast.makeText(MainActivityLogin.this, "התחברות מנהל הצליחה", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityLogin.this, factorymanager.class);
            startActivity(intent);
            // אחרי שווידאת ב-Firebase שהמשתמש הוא אכן מנהל:
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("isManager", true).apply();
        }
        // מאבטח
        else if (email.charAt(0) == 'm' && email.length() == 16) {
            Toast.makeText(MainActivityLogin.this, "התחברות מאבטח הצליחה", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityLogin.this, EnterWorker.class);
            startActivity(intent);
        }
        // האדמין הראשי / יונתן
        else if (email.charAt(0) == 'y' && email.length() == 23) {
            Toast.makeText(MainActivityLogin.this, "התחברות מנהל הצליחה", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityLogin.this, areaManager.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivityLogin.this, "המייל אינו תואם להגדרות המערכת", Toast.LENGTH_SHORT).show();
        }
    }
}