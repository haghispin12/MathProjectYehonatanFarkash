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
import com.google.android.gms.tasks.OnFailureListener;


public class AddWorker extends Fragment {
private worker wrk1;
private EditText etEnterId;
private EditText etEnterName;
private EditText etEnterLastName;
private EditText etEnterMail;
private Button btnAddPictre;
private Button btnAddWorker;
private Uri uri;
private ImageView imgvi;
    public ActivityResultLauncher<Intent> startCamera = registerForActivityResult(   // האזנה לסגירת startcamera

            new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override

                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        imgvi.setImageURI(uri);
                        wrk1.setUri1(uri);

                    }

                }

            });
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_worker, container, false); // מה זה
        initviews(view);
        return  view;// מה זה
    }
    private void initviews(View view) {
        etEnterId = view.findViewById(R.id.EnterId);
        etEnterName = view.findViewById(R.id.EnterName);
        etEnterLastName = view.findViewById(R.id.EnterLastName);
        etEnterMail = view.findViewById(R.id.EnterMail);
        btnAddPictre = view.findViewById(R.id.AddPicture);
        btnAddWorker = view.findViewById(R.id.AddWorker);



        btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrk1.setFirstName(etEnterName.getText().toString());
                wrk1.setLastName(etEnterLastName.getText().toString());
                wrk1.setId(etEnterId.getText().toString());
                wrk1.setMail(etEnterMail.getText().toString());
                wrk1.setUri1(uri); //צריך גם פה?
//                FirebaseFirestore.getInstance().collection(“students").document()
//
//                        .set(student1)
//
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//
//                            @Override
//
//                            public void onSuccess(Void aVoid) {
//
//                                Toast.makeText(MainActivity.this,"add student has been
//
//                                        success",Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        }).addOnFailureListener(new OnFailureListener() {
//
//                            @Override
//
//                            public void onFailure(@NonNull Exception e) {
//
//                                Toast.makeText(MainActivity.this,"add student has been
//
//                                        failed",Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        });
            }
        });
        btnAddPictre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.TITLE, "New Picture");

                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

                uri =
                        requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startCamera.launch(cameraIntent);
            }
        });
    }

}