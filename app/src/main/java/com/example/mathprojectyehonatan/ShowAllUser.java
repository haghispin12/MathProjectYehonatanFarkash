package com.example.mathprojectyehonatan;

import static android.app.Activity.RESULT_OK;

import android.app.Instrumentation;
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
import android.widget.TextView;

import com.google.gson.Gson;


public class ShowAllUser extends Fragment {
    private EditText UsrN;
    private TextView Score;
    private TextView Rating;
    private Button btnAddPicture;
    private ImageView imgVi;
    private Button AddUser;
    private User uss;
    Uri uri;



        public  ActivityResultLauncher<Intent> startCamera = registerForActivityResult(   // האזנה לסגירת startcamera

                    new ActivityResultContracts.StartActivityForResult(),

                    new ActivityResultCallback<ActivityResult>() {

                        @Override

                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == RESULT_OK) {
                                imgVi.setImageURI(uri);
                                uss.setPctr(uri);

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
        View view = inflater.inflate(R.layout.showalluser, container, false);
        initviews(view);
        String userStr = getArguments().getString("us1");
        Gson gson = new Gson(); // יצירת אובייקט בשביל להמיר לאובייקט מחדש
        uss = gson.fromJson(userStr, User.class); // שליפת הסטרינג בחזרה לאובייקט
        UsrN.setText(uss.getUserName()); // הכנסת נתון מהאובייקט למיקום הנכון בפרגמנט
        Score.setText(uss.getScore()+"");//הכנסת נתון מהאובייקט למיקום הנכון בפרגמנט
        Rating.setText(uss.getRating()+""); //הכנסת נתון מהאובייקט למיקום הנכון בפרגמנט
        return view;


    }

    private void initviews(View view) {
//        קישור לxml
        UsrN = view.findViewById(R.id.UsrN);
        Score = view.findViewById(R.id.Score);
        Rating = view.findViewById(R.id.Rating);
        btnAddPicture = view.findViewById(R.id.addpctr);
        imgVi = view.findViewById(R.id.imageView);
        AddUser = view.findViewById(R.id.adusr);
//        פתיחת האזנות

        btnAddPicture.setOnClickListener(new View.OnClickListener() {
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