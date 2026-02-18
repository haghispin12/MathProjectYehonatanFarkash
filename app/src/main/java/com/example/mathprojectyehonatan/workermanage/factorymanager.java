package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;
import com.example.mathprojectyehonatan.mathproject.MainActivity;
import com.example.mathprojectyehonatan.mathproject.RateActivity;
import com.example.mathprojectyehonatan.mathproject.ShowAllUser;
import com.google.gson.Gson;


public class factorymanager extends AppCompatActivity {
    private Button btnAddWorker;
    private worker wrk1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_factorymanager);
        initviews();
    }
    private void initviews() {
        btnAddWorker = findViewById(R.id.addWorker);

        btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWorker fragment = new AddWorker(); //
                getSupportFragmentManager().beginTransaction().replace(R.id.FrAddWorker, fragment, "anyTagName").commit(); //פ
            }
        });
    }
}