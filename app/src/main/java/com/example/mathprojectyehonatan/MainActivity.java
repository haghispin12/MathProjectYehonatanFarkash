package com.example.mathprojectyehonatan;

import android.app.Activity;
import android.content.Intent;
import android.health.connect.datatypes.units.Length;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

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
private Toast tos1;
private Exercise Ex1;
private User us1;
private Button btnRate;
private Button btnshowAllUser;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult resS) { // listener when rate activity close
                int resRate = resS.getData().getIntExtra("Rate_key", -1);
                Toast.makeText(MainActivity.this, "your rate: "+resRate, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(this, "wrong... try again", Toast.LENGTH_SHORT).show();

                }
            });

    /**
     *when activity is start
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
        Ex1 = new Exercise(this);

    }

    /**
     * connection design and code each layout separately(בנפרד)
     */
        private void initviews() {
            btnetger = findViewById(R.id.btnetger);
            btncefelad20 = findViewById(R.id.btncefelad20);
            btnloohahacefal = findViewById(R.id.btnloohahacefel);
            whitebox1 = findViewById(R.id.txvwhitebox1);
            whitebox2 = findViewById(R.id.txvwhitebox2);
            tshoova = findViewById(R.id.ettshoova);
            btnbdika = findViewById(R.id.btnbdika);
            btnRate = findViewById(R.id.btnRate);
            btnshowAllUser = findViewById(R.id.btnshoalus);


            String userName = getIntent().getStringExtra("userKey"); // name to object of user, the name gets from intent from loginactivity
            us1 = new User(userName);
            Toast.makeText(this, "wellcome " + userName, Toast.LENGTH_SHORT).show();
            tos = new Toast(this);
            tos.setDuration(tos.LENGTH_SHORT);
            btnetger.setOnClickListener(new View.OnClickListener() { // listener of button etger(כפל במספרים של עד 99)
                @Override
                public void onClick(View v) { //
                    Ex1.Ranint100();
                }
            });
            btncefelad20.setOnClickListener(new View.OnClickListener() { // listener of button cefel up to 20(כפל עד 20)
                @Override
                public void onClick(View v) {

                    Ex1.Ranint();
                }
            });
            btnloohahacefal.setOnClickListener(new View.OnClickListener() { // listener of button of multiplication table (לוח הכפל)
                @Override
                public void onClick(View v) {
                    Ex1.Ranint10();
                }
            });
            btnbdika.setOnClickListener(new View.OnClickListener() { // listener of button that check the result of the user
                @Override
                public void onClick(View v) {
                    IfEq();
                }

            });
            btnRate.setOnClickListener(new View.OnClickListener() { // listener of button - the user rate the app
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RateActivity.class);
                    activityResultLauncher.launch(intent);
                }
            });
            btnshowAllUser.setOnClickListener(new View.OnClickListener() { // listener of button that show the parameter of user
                @Override
                public void onClick(View v) {
                    ShowAllUser fragment = new ShowAllUser();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment, "anyTagName").commit();
                    Gson gson = new Gson();
                    String json = gson.toJson(us1);
                    Bundle bundle = new Bundle();
                    bundle.putString("us1",json);
                    fragment.setArguments(bundle);
                }
            });
        }

        public void IfEq(){
            String s = Ex1.GetRes()+"";
            String ss = "success";
            String ee = "wrong...Try again";
            if (s.equals(tshoova.getText().toString())) {
                if (Ex1.getPub() == 1)
                    us1.setScore(5);
                if (Ex1.getPub() == 2)
                    us1.setScore(10);
                if (Ex1.getPub() == 3)
                    us1.setScore(20);
                success();
            }
            else
                incorrect();
        }
        public void incorrect() {
            Toast.makeText(this, "wrong... try again", Toast.LENGTH_SHORT).show();
        }
        public void success() {Toast.makeText(this,"very good!! - now your sum of score "+us1.getScore(), Toast.LENGTH_SHORT).show(); // מראה את סכום הנקודות שהמתשמש צבר עד עכשיו

        }

    @Override
    public void Showvi(int n1, int n2) {
        whitebox1.setText(n1+"");
        whitebox2.setText(n2+"");
    }


}