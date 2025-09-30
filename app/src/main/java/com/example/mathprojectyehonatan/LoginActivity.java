package com.example.mathprojectyehonatan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
private EditText etuserName;
private Button btnsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        initviews();


    }

        private void initviews() {
            etuserName = findViewById(R.id.etUs);
            btnsubmit = findViewById(R.id.btnSub);

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userKey", etuserName.getText().toString());
                    startActivity(intent);
                    String userName = getIntent().getStringExtra("userKey");
                    Toast.makeText(LoginActivity.this,userName,Toast.LENGTH_SHORT).show();
                }


            });
        }


    }