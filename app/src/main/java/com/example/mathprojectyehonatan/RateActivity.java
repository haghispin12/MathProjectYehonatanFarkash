package com.example.mathprojectyehonatan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RateActivity extends AppCompatActivity {
    private Button btnsave;
    private SeekBar seekbar;
    private void initviews() {
        btnsave = findViewById(R.id.btnSave);
        seekbar =findViewById(R.id.seekbar);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.putExtra("Rate_key", seekbar.getProgress());
                setResult(RESULT_OK, in);
                finish();
            }
        });
        seekbar = findViewById(R.id.seekbar);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate);
        initviews();

        }
    }
