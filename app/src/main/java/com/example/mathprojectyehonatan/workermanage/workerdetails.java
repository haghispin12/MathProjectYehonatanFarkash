package com.example.mathprojectyehonatan.workermanage;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;

import java.time.LocalDate;
import java.util.Calendar;


public class workerdetails extends AppCompatActivity {
    private TextView etNameworker;
    private TextView etIdworker;
    private TextView etDate;
    private TextView etEntryTime;
    private  TextView etDeparturetime;
    private worker wrk1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workerdetails);

    }
    private void initviews() {
        etNameworker = findViewById(R.id.nameworker);
        etIdworker = findViewById(R.id.idworker);
        etDate = findViewById(R.id.date);



        setEtDate(etDate);
        etIdworker.setText(wrk1.getId());

    }

    public void setEtDate(TextView etDate) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        etDate.setText(day+"."+month+"."+year);
    }
}