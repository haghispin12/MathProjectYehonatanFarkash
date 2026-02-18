package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;
import com.example.mathprojectyehonatan.mathproject.LoginActivity;
import com.example.mathprojectyehonatan.mathproject.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

public class MainActivityLogin extends AppCompatActivity {
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPass;
    private FirebaseAuth auth =  FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main_login);
        initviews();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int n =0;


    }
        private void initviews() {
        btnLogin = findViewById(R.id.btnLogIn);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            auth.signInWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString()).addOnCompleteListener(MainActivityLogin.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(MainActivityLogin.this, "success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivityLogin.this, factorymanager.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivityLogin.this,"failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            }
        });

        }

}