package com.example.mathprojectyehonatan.mathproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mathprojectyehonatan.R;

public class LoginActivity extends AppCompatActivity {
private EditText etuserName;
private Button btnsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        initviews();
        SharedPreferences sh = getSharedPreferences("myshPs", MODE_PRIVATE);
        String st1 = sh.getString("name", "");
        etuserName.setText(st1);


    }

        private void initviews() {
            etuserName = findViewById(R.id.etUs);
            btnsubmit = findViewById(R.id.btnSub);

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences shPrs = getSharedPreferences("myshPs", MODE_PRIVATE);
                    SharedPreferences.Editor edtSp = shPrs.edit();
                    edtSp.putString("name", etuserName.getText().toString());
                    edtSp.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userKey", etuserName.getText().toString());
                    startActivity(intent);

                }


            });
        }


    }