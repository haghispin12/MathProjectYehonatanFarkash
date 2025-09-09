package com.example.mathprojectyehonatan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

private Button etger;
private Button cefelad20;
private Button loohahacefal;
private TextView whitebox1;
private TextView whitebox2;
private EditText tshoova;
private Button bdika;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etger = findViewById(R.id.btnetger);
        cefelad20 = findViewById(R.id.btncefelad20);
           //loohahacefal = findViewById(R.id.)
    }
}