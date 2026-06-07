package com.example.mathprojectyehonatan.workermanage;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;

public class EnterWorker extends AppCompatActivity {

    private Button btnIdScann;
    private Button btnEntereWorker;
    private Uri uri;
    public ActivityResultLauncher<Intent> startCamera = registerForActivityResult(   // האזנה לסגירת startcamera

            new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override

                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {


                    }

                }

            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_worker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initviews() {
        btnIdScann = findViewById(R.id.idScann);
        btnEntereWorker = findViewById(R.id.EntereWorker);

        btnIdScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.TITLE,"NEW PICTURE");
                values.put(MediaStore.Images.Media.DESCRIPTION,"FROM CANERA");

                uri = EnterWorker.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startCamera.launch(cameraIntent);
            }
        });
        btnEntereWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
