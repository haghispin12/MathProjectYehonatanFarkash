package com.example.mathprojectyehonatan;

import android.health.connect.datatypes.units.Length;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

import kotlin.random.URandomKt;

public class MainActivity extends AppCompatActivity {

private Button btnetger;
private Button btncefelad20;
private Button btnloohahacefal;
private TextView whitebox1;
private TextView whitebox2;
private EditText tshoova;
private Button btnbdika;
private int result;
private Toast tos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
    }

        private void initviews() { //java קישור בין הפקדים לקוד
            btnetger = findViewById(R.id.btnetger);
            btncefelad20 = findViewById(R.id.btncefelad20);
            btnloohahacefal = findViewById(R.id.btnloohahacefel);
            whitebox1 = findViewById(R.id.txvwhitebox1);
            whitebox2 = findViewById(R.id.txvwhitebox2);
            tshoova = findViewById(R.id.ettshoova);
            btnbdika = findViewById(R.id.btnbdika);


            tos = new Toast(this);
            tos.setDuration(tos.LENGTH_SHORT);
            btnetger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Ranint100();
                }
            });
           btncefelad20.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  Ranint();
               }
           });
            btnloohahacefal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Ranint10();
                }
            });
            btnbdika.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 IfEq();
                }
            });
        }
        public void Ranint() { //מספר רנדומלי 0 עד 20
            Random rr = new Random();
            int num1 = rr.nextInt(9);
            int num2 = rr.nextInt(20);
             result = num1*num2;
            ShowVi(num1,num2);
        }
        public void Ranint10() { //מספר רנדומאלי בין 0 ל9
            Random rr = new Random();
            int num1 = rr.nextInt(9);
            int num2 = rr.nextInt(9);
             result = num1*num2;
            ShowVi(num1,num2);
        }
        public void Ranint100() {
        Random rrr = new Random();
        int num1 = rrr.nextInt(9);
        int num2 = rrr.nextInt(89)+10;
        result = num1*num2;
        ShowVi(num1,num2);
        }
        public void ShowVi(int n1,int n2) { //מקבל שני מספרים ומציק בקופסאות
        whitebox1.setText(n1+"");
        whitebox2.setText(n2+"");
        }

        public void IfEq(){
            String s = result+"";
            String ss = "success";
            String ee = "wrong...Try again";
            if (s.equals(tshoova.getText().toString()))
                success();
            else
                incorrect();
        }
        public void incorrect(){
        tos.setText("wrong...try again");
        tos.show();
        }
        public void success() {
        tos.setText("very good");
        tos.show();
        }

}