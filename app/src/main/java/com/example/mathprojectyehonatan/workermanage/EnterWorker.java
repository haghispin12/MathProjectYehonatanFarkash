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
    // משתנה שיטפל בסריקת הברקודים של Google ML Kit
// משתנה שיטפל בזיהוי וקריאת טקסט מהתמונה (OCR) של Google ML Kit
    com.google.mlkit.vision.text.TextRecognizer recognizer;

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
        // אתחול סורק הטקסט של גוגל - ירוץ מיד כשהמסך נפתח
        recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        initviews();
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{android.Manifest.permission.CAMERA},
                    100
            );
        }
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



    // ------------------------------------------------------------------------
    // פונקציה א': מקבלת את התמונה שצולמה, ומחלצת ממנה את מספר תעודת הזהות ברקע
    // ------------------------------------------------------------------------


    // פונקציית עזר שמשתמשת ב-Regex (ביטוי רגולרי) כדי למצוא את רצף 9 הספרות הראשון בטקסט
    private String extractTzNumber(String rawText) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d{9}").matcher(rawText);
        if (matcher.find()) {
            return matcher.group(); // מחזיר את 9 הספרות שמצא
        }
        return rawText; // ברירת מחדל למקרה שלא נמצא רצף מדויק
    }

    // ---------------------------------------------------------------------------------
    // פונקציה ב': מקבלת את מספר תעודת הזהות שנסרק ומחפשת אותו באוסף ה-workers ב-Firestore
    // ---------------------------------------------------------------------------------
    private void checkUserInFirestore(String tzNumber) {
        // יצירת חיבור לאוסף ה-workers שלך ב-Firestore
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        // הרצת שאילתה שמחפשת מסמך שבו השדה "id" שווה בדיוק למספר שנסרק
        db.collection("workers")
                .whereEqualTo("id", tzNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // בדיקה: האם השאילתה חזרה עם תוצאות (כלומר מצאה עובד תואם)?
                        if (!task.getResult().isEmpty()) {

                            // שליפת המסמך הראשון שנמצא (העובד המורשה)
                            com.google.firebase.firestore.DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // שליפת השם הפרטי ושם המשפחה מתוך השדות שבצילום המסך שלך
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");

                            // הצגת הודעת ברכה עם שם העובד - הכניסה מאושרת!
                            android.widget.Toast.makeText(this, "הכניסה מאושרת! ברוך הבא " + firstName + " " + lastName, android.widget.Toast.LENGTH_LONG).show();

                        } else {
                            // השאילתה חזרה ריקה - מספר תעודת הזהות הזה לא רשום באוסף
                            android.widget.Toast.makeText(this, "גישה נדחתה: מספר תעודת זהות " + tzNumber + " אינו רשום במערכת!", android.widget.Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // טיפול במקרה של שגיאת תקשורת או בעיה בשליפת הנתונים מהענן
                        android.widget.Toast.makeText(this, "שגיאה בחיבור לענן: " + task.getException().getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // ------------------------------------------------------------------------
    // פונקציה א': סורקת את הטקסט המודפס על התעודה ומחלצת את מספר תעודת הזהות (OCR)
    // ------------------------------------------------------------------------
    private void scanBarcodeFromUri(android.net.Uri imageUri) {
        android.util.Log.d("OCR_Test", "סורק הטקסט הופעל בהצלחה!");

        try {
            // הפיכת הקישור (Uri) לפורמט תמונה שספריית ה-ML Kit יודעת לקרוא
            com.google.mlkit.vision.common.InputImage image =
                    com.google.mlkit.vision.common.InputImage.fromFilePath(this, imageUri);

            // הפעלת סורק הטקסט (recognizer) על התמונה שלנו
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        // שליפת כל הטקסט שנמצא בתמונה כמקשה אחת
                        String fullText = visionText.getText();

                        android.util.Log.d("OCR_Test", "הטקסט שנקרא מהתעודה: " + fullText);

                        if (fullText != null && !fullText.isEmpty()) {
                            // שימוש בפונקציית העזר הקיימת שלך כדי למצוא רצף של 9 ספרות בטקסט
                            String tzNumber = extractTzNumber(fullText);

                            android.util.Log.d("OCR_Test", "מספר תעודת הזהות שחולץ מהטקסט: " + tzNumber);

                            // הפעלת הבדיקה הקיימת שלך מול ה-Firestore
                            checkUserInFirestore(tzNumber);
                        } else {
                            android.widget.Toast.makeText(this, "לא נמצא טקסט ברור בתמונה, נסה לצלם שוב מקרוב", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // במקרה של שגיאה טכנית בפענוח התמונה
                        android.widget.Toast.makeText(this, "שגיאה בקריאת הטקסט: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
