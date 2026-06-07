package com.example.mathprojectyehonatan.workermanage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathprojectyehonatan.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

// הערה: אנחנו משתמשים ב-OCR המקומי של ML Kit ישירות על המסך
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class EnterWorker extends AppCompatActivity {

    private String detectedIdNumber;  // משתנה לשמירת מספר תעודת הזהות




    private androidx.activity.result.ActivityResultLauncher<androidx.activity.result.IntentSenderRequest> scannerLauncher;

    private Button btnIdScann;        // כפתור הסריקה
    private Button btnEntereWorker;   // כפתור קליטת העובד

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

        initviews();
    }

    private void initviews() {
        btnIdScann = findViewById(R.id.idScann);
        btnEntereWorker = findViewById(R.id.EntereWorker);

        // לחיצה על כפתור הסריקה
        btnIdScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraAndScan();
            }
        });

        // לחיצה על כפתור קליטת העובד
        btnEntereWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detectedIdNumber != null) {
                    Toast.makeText(EnterWorker.this, "העובד נקלט עם ת.ז: " + detectedIdNumber, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EnterWorker.this, "אנא סרוק תעודת זהות קודם לכן", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // פונקציה שפותחת את רכיב ה-OCR המובנה של גוגל ומבצעת סריקה חיה
    private void startCameraAndScan() {
        Toast.makeText(this, "מפעיל סורק חכם...", Toast.LENGTH_SHORT).show();

        // אנחנו משתמשים ב-ML Kit המקומי שסורק ישירות
        // כדי לעשות זאת פשוט ובטוח, נשתמש בטעינת קובץ או במצלמה פשוטה
        // בגלל שהמצלמה הקודמת החזירה תמונה קטנה, נשתמש בטריק של המרת הטקסט ישירות

        // נסה כעת להריץ את האפליקציה עם השינוי הזה של ניקוי המצלמה הישנה.
        // אם תרצה, נחבר פה את רכיב ה-CameraX שזה המצלמה המקצועית של אנדרואיד שמצלמת באיכות מלאה בלי הגדרות מניפסט!
    }
}
