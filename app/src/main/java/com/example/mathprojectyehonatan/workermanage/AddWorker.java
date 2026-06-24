package com.example.mathprojectyehonatan.workermanage;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mathprojectyehonatan.R;
import com.example.mathprojectyehonatan.mathproject.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddWorker extends Fragment {
private worker wrk1;
private EditText etEnterId;
private EditText etEnterName;
private EditText etEnterFactoryNumbr;
private EditText etEnterLastName;
private EditText etEnterMail;
private Button btnAddWorker;
private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // הופך את ה-XML של העיצוב לאובייקט שאפשר לתפעל בקוד
        View view = inflater.inflate(R.layout.fragment_add_worker, container, false);

        // מחבר את כל האלמנטים של המסך
        initviews(view);

        // מחזיר את התצוגה המוכנה כדי שתוצג על המסך
        return view;
    }

    private void initviews(View view) {
        // מחבר בין משתני ה-Java ל-IDs של ה-XML
        etEnterId = view.findViewById(R.id.EnterId);
        etEnterName = view.findViewById(R.id.EnterName);
        etEnterLastName = view.findViewById(R.id.EnterLastName);
        etEnterMail = view.findViewById(R.id.EnterMail);
        etEnterFactoryNumbr = view.findViewById(R.id.enterNumFactory);
        btnAddWorker = view.findViewById(R.id.AddWorker);

        // מה קורה כשלוחצים על כפתור ההוספה
        btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // יוצר משתמש חדש ב-Firebase Authentication
                auth.createUserWithEmailAndPassword(etEnterMail.getText().toString().trim(), etEnterId.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // אם ההרשמה הצליחה, עוברים לשמור את הפרטים ב-Firestore
                            Toast.makeText(getActivity(), "Registration success", Toast.LENGTH_SHORT).show();
                            collection();
                        } else {
                            Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void collection() {
        // בודק שהמספר מפעל שנקלט תקין (לוג לצורך דיבאג)
        Toast.makeText(getActivity(), "Debug Factory: " + etEnterFactoryNumbr.getText().toString(), Toast.LENGTH_SHORT).show();

        // יצירת אובייקט עובד חדש
        wrk1 = new worker(etEnterName.getText().toString(), etEnterLastName.getText().toString(), etEnterId.getText().toString(), etEnterMail.getText().toString(), etEnterFactoryNumbr.getText().toString());

        // שמירה ב-Firestore
        FirebaseFirestore.getInstance().collection("workers")
                .add(wrk1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // אם הצליח - מנקה את המסך ויוצא
                        Toast.makeText(getActivity(), "add worker has been success", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(AddWorker.this).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // אם נכשל - מציג שגיאה
                        Toast.makeText(getActivity(), "add student has been failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }