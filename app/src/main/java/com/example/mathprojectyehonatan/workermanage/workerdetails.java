package com.example.mathprojectyehonatan.workermanage;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;


public class workerdetails extends AppCompatActivity {
    private TextView etNameworker;
    private TextView etIdworker;
    private TextView etDate;
    private TextView etEntryTime;
    private  TextView etDeparturetime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workerdetails);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initviews() {
        etNameworker = findViewById(R.id.nameworker);
        etIdworker = findViewById(R.id.idworker);

    }
}