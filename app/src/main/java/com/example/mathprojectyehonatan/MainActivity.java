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

public class MainActivity extends AppCompatActivity implements CallBack {

private Button btnetger;
private Button btncefelad20;
private Button btnloohahacefal;
private TextView whitebox1;
private TextView whitebox2;
private EditText tshoova;
private Button btnbdika;
private Toast tos;
private Exercise Ex1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
        Ex1 = new Exercise(this);
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
                Ex1.Ranint100();
                }
            });
           btncefelad20.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   Ex1.Ranint();
               }
           });
            btnloohahacefal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Ex1.Ranint10();
                }
            });
            btnbdika.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 IfEq();
                }
            });
        }


        public void IfEq(){
            String s = Ex1.GetRes()+"";
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

    @Override
    public void Showvi(int n1, int n2) {
        whitebox1.setText(n1+"");
        whitebox2.setText(n2+"");
    }


}