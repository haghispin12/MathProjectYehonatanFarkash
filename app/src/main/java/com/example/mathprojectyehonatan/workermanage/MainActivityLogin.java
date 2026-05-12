package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;
import com.example.mathprojectyehonatan.mathproject.LoginActivity;
import com.example.mathprojectyehonatan.mathproject.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

public class MainActivityLogin extends AppCompatActivity {
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPass;
    private FirebaseAuth auth1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main_login);
        initviews();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth1 = FirebaseAuth.getInstance();
        if(auth1.getCurrentUser()!=null){
            loginTospecificActivity();
            int n=0;
        }
        int n =0;


    }
        private void initviews() {
        btnLogin = findViewById(R.id.btnLogIn);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth1.signInWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString()).addOnCompleteListener(MainActivityLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                               if (auth1.getCurrentUser() != null) {
                                    loginTospecificActivity();
                              }else
                                   Toast.makeText(MainActivityLogin.this,"failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }



    private void loginTospecificActivity() {
        if (auth1.getCurrentUser().getEmail().toString().charAt(0) == 'b' && auth1.getCurrentUser().getEmail().toString().length() == 15) {//     אם המייל הוא 15 תווים והתו הראשון הוא b -מנהל מפעל
            Toast.makeText(MainActivityLogin.this, "success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityLogin.this, factorymanager.class);
            startActivity(intent);
        } else if (auth1.getCurrentUser().getEmail().toString().charAt(0) == 'm' && auth1.getCurrentUser().getEmail().toString().length() == 16) {  //אם האות הראשונה היא m והמייל בוארך 16 תווים{ - מאבטח
            Toast.makeText(MainActivityLogin.this, "success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityLogin.this, EnterWorker.class);
            startActivity(intent);
        }
        else if (auth1.getCurrentUser().getEmail().toString().charAt(0) == 'c'&& auth1.getCurrentUser().getEmail().toString().length() == 17) {    // אם האות הראשונה היא c והמייל באורך 17 תווים - עובד
            Toast.makeText(MainActivityLogin.this, "success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityLogin.this, workerdetails.class);
            startActivity(intent);
        } else if (auth1.getCurrentUser().getEmail().toString().charAt(0) == 'y' && auth1.getCurrentUser().getEmail().toString().length() == 23) { // אני - yon.f.sh14144@gmail.com, yonyon
            Toast.makeText(MainActivityLogin.this, "success", Toast.LENGTH_SHORT).show(); //בעיה לא עובד
            Intent intent = new Intent(MainActivityLogin.this, factorymanager.class);
            startActivity(intent);
        }
    }


}