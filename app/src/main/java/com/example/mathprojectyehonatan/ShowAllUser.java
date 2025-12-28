package com.example.mathprojectyehonatan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

            }
        });
    }

}