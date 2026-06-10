package com.example.mathprojectyehonatan.workermanage;

// יבוא של ספריות מערכת של אנדרואיד לניהול קבצים, כפתורים ומסכים
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

// יבוא ספריות לתמיכה בעיצוב מודרני וניהול תהליכים (כמו הפעלת המצלמה)
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// יבוא קובץ ה-R של הפרויקט (המקשר בין הקוד הלוגי לעיצוב ה-XML)
import com.example.mathprojectyehonatan.R;
// יבוא ספריות לחיפוש ותואם תבניות טקסט (Regex)
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnterWorker extends AppCompatActivity {

    // הגדרת משתנים עבור רכיבי המסך (הכפתורים)
    private Button btnIdScann;
    private Button btnEntereWorker;
    private Button btnExitWorker;

    // משתנה מסוג Uri לשמירת נתיב התמונה שצולמה בזיכרון המכשיר
    private Uri uri;
    private String currentWorkerDocId = null;
    private String currentWorkerName = null;
    private String currentWorkerLastEntryDate = null; // ישמור את התאריך הקיים בענן

    // הגדרת מנוע זיהוי הטקסט של גוגל (ML Kit)
    com.google.mlkit.vision.text.TextRecognizer recognizer;

    // מנגנון חכם להפעלת המצלמה וקבלת התוצאה (התמונה) בחזרה לקוד
    public ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // בדיקה אם פעולת הצילום הצליחה והמשתמש לא ביטל באמצע
                    if (result.getResultCode() == RESULT_OK) {
                        // בדיקה שנתיב שמירת התמונה קיים ותקין בזיכרון
                        if (uri != null) {
                            Log.d("OCR_Test", "התמונה נשמרה במיקום: " + uri.toString());
                            // הפעלת פונקציית הסריקה הראשי ושליחת נתיב התמונה אליה
                            scanBarcodeFromUri(uri);
                        } else {
                            Toast.makeText(EnterWorker.this, "שגיאה: מיקום התמונה לא נמצא", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // הגדרת תצוגת מסך מלא (Edge to Edge) ללא שוליים שחורים
        EdgeToEdge.enable(this);
        // חיבור קוד הג'אווה לקובץ ה-XML של המסך הנוכחי
        setContentView(R.layout.activity_enter_worker);

        // אתחול מנוע זיהוי הטקסט של גוגל (ML Kit המיועד לתווים לטיניים וספרות)
        recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS);

        // התאמת תצוגת המסך כדי שרכיבי ה-UI לא ייכנסו מתחת לסרגל הסטטוס או הניווט של הטלפון
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קריאה לפונקציית אתחול וקישור רכיבי המסך
        initviews();

        // בדיקה בזמן אמת (Runtime Permission) אם האפליקציה קיבלה אישור להשתמש במצלמה. אם לא - נבקש אישור מהמשתמש
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        }
    }

    // פונקציה שמקשרת בין רכיבי ה-XML לקוד הג'אווה ומגדירה מאזיני לחיצה (Listeners)
    private void initviews() {
        // קישור כפתור הסריקה לפי ה-ID שלו ב-XML
        btnIdScann = findViewById(R.id.idScann);
        // קישור כפתור כניסת עובד לפי ה-ID שלו ב-XML
        btnEntereWorker = findViewById(R.id.EntereWorker);
        btnExitWorker = findViewById(R.id.ExitWorker);

        // הגדרת פעולה שתתבצע ברגע שהמשתמש לוחץ על כפתור הסריקה
        btnIdScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // יצירת אובייקט הגדרות עבור קובץ התמונה החדש שעומד להיווצר
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "NEW PICTURE");
                values.put(MediaStore.Images.Media.DESCRIPTION, "FROM CAMERA");

                // יצירת נתיב ריק (Uri) בגלריית המכשיר שבו תינעל התמונה שרגע נצלם
                uri = EnterWorker.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                // יצירת כוונה (Intent) לפתיחת אפליקציית המצלמה המובנית של הטלפון
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // פקודה למצלמה לשמור את הקובץ ישירות לתוך נתיב ה-uri הריק שהכנו מראש
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // שיגור המצלמה לחלל האוויר באמצעות המנגנון שהגדרנו למעלה
                startCamera.launch(cameraIntent);
            }
        });

        // הגדרת פעולה בעת לחיצה על כפתור כניסת העובד
        // הגדרת פעולה בעת לחיצה על כפתור כניסת העובד (לאחר שהעובד כבר זוהה בהצלחה)
        btnEntereWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // אבטחה: מוודאים שיש לנו את מזהה העובד בזיכרון לפני שמנסים לעדכן את הענן
                if (currentWorkerDocId != null) {
                    // קריאה לפונקציית הרישום החכמה ושליחת הנתונים אליה
                    registerWorkerEntry(currentWorkerDocId, currentWorkerName);
                } else {
                    Toast.makeText(EnterWorker.this, "אנא סרוק תעודת זהות תחילה!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnExitWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // בתוך ה-onClick של כפתור היציאה שלך:
                if (currentWorkerDocId != null) {
                    // קריאה לפונקציית רישום היציאה ששמנו בשלב 2
                    registerWorkerExit(currentWorkerDocId, currentWorkerName);
                } else {
                    Toast.makeText(EnterWorker.this, "אנא סרוק תעודת זהות תחילה!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // פונקציה ראשית שטוענת את התמונה מהנתיב הדיגיטלי וממירה אותה לאובייקט Bitmap
    private void scanBarcodeFromUri(Uri imageUri) {
        Log.d("OCR_Test", "מתחיל תהליך סריקה מתקדם...");
        try {
            // המרת ה-Uri (הנתיב) לאובייקט Bitmap (מפת סיביות) כדי שנוכל לסובב ולשחק איתו בזיכרון
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // שליחת התמונה לפונקציה הרקורסיבית החכמה, מתחילים מזווית 0 (הזווית המקורית)
            tryRecognizeWithRotation(bitmap, 0);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת הקובץ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // פונקציה רקורסיבית שמנסה לפענח את התמונה ב-4 זוויות שונות (0, 90, 180, 270) כדי לתמוך בצילום אנכי/אופקי
    private void tryRecognizeWithRotation(Bitmap bitmap, int rotationIndex) {
        // מערך המכיל את ארבע זוויות הסיבוב האפשריות של תמונה במרחב
        int[] rotations = {0, 90, 180, 270};

        // תנאי עצירה לרקורסיה: אם בדקנו את כל 4 הזוויות ועדיין שום מספר לא עבר את אלגוריתם האימות
        if (rotationIndex >= rotations.length) {
            Toast.makeText(this, "לא נמצא מספר תעודת זהות חוקי בתמונה. נסה לצלם שוב מקרוב וישר.", Toast.LENGTH_LONG).show();
            return;
        }

        // שליפת זווית הסיבוב הנוכחית מתוך המערך לפי האינדקס ברקורסיה
        int currentRotation = rotations[rotationIndex];
        Log.d("OCR_Test", "מנסה לפענח את התמונה בזווית: " + currentRotation + " מעלות.");

        // יצירת אובייקט תמונת קלט עבור ML Kit המשלב את הביטמאפ יחד עם זווית הסיבוב הרצויה
        com.google.mlkit.vision.common.InputImage image =
                com.google.mlkit.vision.common.InputImage.fromBitmap(bitmap, currentRotation);

        // הפעלת מנוע זיהוי הטקסט של גוגל על התמונה בזווית הנוכחית
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String fullText = visionText.getText();

                    // שליחת כל הטקסט שחולץ מהתעודה לבדיקה וסינון מספר הזהות החוקי
                    String tzNumber = extractTzNumber(fullText);

                    // אם נמצא מספר תעודת זהות חוקי (שעבר בהצלחה את אלגוריתם לון של משרד הפנים)
                    if (tzNumber != null) {
                        Log.d("OCR_Test", "הצלחה! נמצאה תעודת זהות מאומתת: " + tzNumber);
                        // מעבר לשלב הבא והאחרון: בדיקת קיום העובד בתוך ה-Database בענן של Firestore
                        checkUserInFirestore(tzNumber);
                    } else {
                        // אם לא נמצא מספר ת"ז תקין בזווית הזו, הפונקציה קוראת לעצמה שוב ועוברת אוטומטית לזווית הבאה
                        Log.d("OCR_Test","לא נמצאה ת''ז תקינה בזווית" + currentRotation + ", עובר לזווית הבאה...");
                        tryRecognizeWithRotation(bitmap, rotationIndex + 1);
                    }
                })
                .addOnFailureListener(e -> {
                    // במקרה של כשל טכני כלשהו בעיבוד בזווית הזו, נדלג מיד לניסיון בזווית הבאה
                    tryRecognizeWithRotation(bitmap, rotationIndex + 1);
                });
    }

    // פונקציית עזר שמנקה רווחים, מחפשת רצפי מספרים ומסננת את מספר הכרטיס המטעה
    private String extractTzNumber(String rawText) {
        if (rawText == null || rawText.isEmpty()) return null;

        // ניקוי מוחלט של כל סוגי הרווחים וירידות השורה כדי לחבר ספרות שפוצלו בטעות (כמו בתעודה הביומטרית)
        String cleanText = rawText.replaceAll("\\s+", "");

        // הגדרת תבנית חיפוש (Regex) עבור כל רצף של 8 או 9 ספרות רצופות שקיימות בטקסט שנסרק
        Matcher matcher = Pattern.compile("\\d{8,9}").matcher(cleanText);

        // לולאה שעוברת על כל רצפי המספרים הללו שנמצאו בתמונה (למשל: תתפוס גם את מספר הזהות וגם את מספר הכרטיס)
        while (matcher.find()) {
            String candidate = matcher.group();

            // אם המספר שנמצא הוא בן 8 ספרות (תעודות זהות ישנות), נוסיף לו אפס מוביל כדי להפוך אותו ל-9 ספרות לצורך הבדיקה
            if (candidate.length() == 8) {
                candidate = "0" + candidate;
            }

            // הרצת אלגוריתם לון הרשמי: האם זה מספר תעודת זהות אמיתית ותקנית, או מספר כרטיס אקראי?
            if (isValidIsraeliID(candidate)) {
                return candidate; // בינגו! מצאנו את מספר הזהות האמיתי. נחזיר אותו מיד ונעצור את החיפוש
            }
        }
        return null; // אם סרקנו את כל המספרים בתמונה ואף אחד מהם לא היה ת"ז חוקית ומאומתת
    }

    // אלגוריתם לון (Luhn Check Digit) הרשמי המשמש את משרד הפנים לבדיקת תקינות מתמטית של תעודות זהות
    private boolean isValidIsraeliID(String id) {
        // תעודת זהות ישראלית חייבת להכיל בדיוק 9 ספרות
        if (id == null || id.length() != 9) return false;

        int sum = 0; // משתנה לצבירת סכום החישוב המשוקלל

        // לולאה הרצה ועוברת ספרה-ספרה על פני כל 9 הספרות
        for (int i = 0; i < 9; i++) {
            // המרת התו (Char) במיקום הנוכחי למספר שלם אמיתי (int בין 0 ל-9)
            int digit = Character.getNumericValue(id.charAt(i));
            if (digit < 0 || digit > 9) return false;

            // קביעת המשקל: ספרות במיקומים זוגיים (0, 2, 4...) מוכפלות ב-1, מיקומים אי-זוגיים מוכפלות ב-2
            int weight = (i % 2 == 0) ? 1 : 2;
            int step = digit * weight; // מכפלת הספרה במשקל הייעודי שלה

            // אם תוצאת המכפלה יצאה דו-ספרתית (גדולה מ-9, למשל 14), נחבר את שתי ספרותיה יחד (1 + 4 = 5)
            if (step > 9) {
                step = (step % 10) + (step / 10);
            }
            // הוספת התוצאה המשוקללת של הספרה הנוכחית אל הסכום הכללי המצטבר
            sum += step;
        }
        // תעודת זהות נחשבת לחוקית ותקינה אך ורק אם הסכום הסופי המשוקלל מתחלק ב-10 ללא שום שארית
        return (sum % 10 == 0);
    }

    /**
     * פעולה המבצעת שאילתה מול מסד הנתונים (Firestore) בענן לפי מספר תעודת זהות.
     * הפעולה מאמתת את קיום העובד במערכת, שולפת את נתוניו החיוניים לזיכרון הגלובלי,
     * ובודקת את סטטוס הנוכחות העדכני שלו ("האם נמצא במפעל?").
     * * בהתאם לסטטוס שמתקבל, הפעולה מנהלת את ממשק המשתמש (UI) בצורה חכמה ומאובטחת:
     * - אם העובד כבר נמצא בפנים (isEntered == true): מאפשרת ללחוץ רק על כפתור "יציאה" וחוסמת את כפתור "כניסה".
     * - אם העובד נמצא בחוץ (isEntered == false): מאפשרת ללחוץ רק על כפתור "כניסה" וחוסמת את כפתור "יציאה".
     * - במקרה שהתעודה אינה רשומה או שגיאה: גישה נדחתה ושני הכפתורים ננעלים למניעת טעויות.
     *
     * @param tzNumber מספר תעודת הזהות שנסרק מהתעודה וישמש לחיפוש בענן.
     */
    private void checkUserInFirestore(String tzNumber) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        db.collection("workers").whereEqualTo("id", tzNumber).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {

                        com.google.firebase.firestore.DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // שמירת הנתונים במשתנים הגלובליים
                        currentWorkerDocId = document.getId();
                        currentWorkerName = document.getString("firstName") + " " + document.getString("lastName");
                        currentWorkerLastEntryDate = document.getString("lastEntryDate");

                        // בדיקת הסטטוס הנוכחי של העובד בענן
                        boolean isEntered = document.getBoolean("isEntered") != null ? document.getBoolean("isEntered") : false;

                        if (isEntered) {
                            // תרחיש א': העובד נמצא כרגע במפעל -> נאפשר רק יציאה
                            btnExitWorker.setEnabled(true);
                            btnEntereWorker.setEnabled(false);
                            Toast.makeText(this, "עובד בפנים: " + currentWorkerName + " - ניתן לרשום יציאה.", Toast.LENGTH_SHORT).show();
                        } else {
                            // תרחיש ב': העובד מחוץ למפעל -> נאפשר רק כניסה
                            btnExitWorker.setEnabled(false);
                            btnEntereWorker.setEnabled(true);
                            Toast.makeText(this, "עובד בחוץ: " + currentWorkerName + " - ניתן לרשום כניסה.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // גישה נדחתה - מכבים את שני הכפתורים למניעת תקלות
                        btnEntereWorker.setEnabled(false);
                        btnExitWorker.setEnabled(false);

                        Toast.makeText(this, "גישה נדחתה: מספר תעודת זהות " + tzNumber + " אינו רשום!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // פונקציה חדשה לחלוטין שמבצעת את רישום הכניסה החכם ומגנה על השעה הראשונה/**
    //     * פונקציה חכמה לרישום כניסת עובד למפעל.
    //     * המנגנון מונע דריסה של שעת הכניסה המקורית מהבוקר במקרה של לחיצות כפולות או כניסות חוזרות באותו היום.
    //     * * @param docId ID-ה הייחודי של מסמך העובד בתוך ה-Collection ב-Firestore
    //     * @param workerName שם העובד (לצורך הצגה בהודעות Toast)
     private void registerWorkerEntry(String docId, String workerName) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        // חילוץ תאריך ושעה נוכחיים
        String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String currentTime = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());

        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("isEntered", true); // עדכון סטטוס: העובד במפעל

        // אלגוריתם סינון השעה הראשונה להיום
        if (currentWorkerLastEntryDate == null || !currentWorkerLastEntryDate.equals(todayDate)) {
            updates.put("entryTime", currentTime);   // רושמים שעה רק אם זה ביקור ראשון היום
            updates.put("lastEntryDate", todayDate); // מעדכנים את תאריך הרישום להיום
            Toast.makeText(this, "כניסה ראשונה להיום נרשמה בשעה: " + currentTime, Toast.LENGTH_SHORT).show();
        } else {
            // אם התאריכים זהים - לא מוסיפים את entryTime למפה והשעה המקורית לא נדרסת
            Toast.makeText(this, "העובד כבר נכנס היום. שעת הכניסה המקורית מהבוקר נשמרה.", Toast.LENGTH_SHORT).show();
        }

        // עדכון המסמך ב-Firestore
        db.collection("workers").document(docId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    btnEntereWorker.setEnabled(false); // נעילת הכפתור לאחר הצלחה

                    // איפוס המשתנים לעובד הבא
                    currentWorkerDocId = null;
                    currentWorkerName = null;
                    currentWorkerLastEntryDate = null;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה ברישום הכניסה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    // פונקציה שמבצעת רישום יציאה בענן ומעדכנת את הסטטוס ל"מחוץ למפעל"
    private void registerWorkerExit(String docId, String workerName) {
        // הגנה נוספת: אם ה-id ריק, לא עושים כלום
        if (docId == null) return;
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        // חילוץ השעה הנוכחית של הלחיצה
        String currentTime = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());

        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("isEntered", false); // עדכון הסטטוס: העובד יצא (יהפוך לאדום אצל המנהל)
        updates.put("exitTime", currentTime); // רישום שעת היציאה העדכנית

        // עדכון המסמך הספציפי של העובד ב-Firestore
        db.collection("workers").document(docId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    // נעילת הכפתורים לאחר ביצוע מוצלח (כדי למנוע לחיצות כפולות)
                    btnEntereWorker.setEnabled(false);
                    btnExitWorker.setEnabled(false); // שים לב לשנות כאן לשם של משתנה כפתור היציאה שלך במידת הצורך

                    Toast.makeText(this, "יציאת העובד נרשמה בהצלחה בשעה: " + currentTime, Toast.LENGTH_SHORT).show();

                    // איפוס המשתנים כדי להיות מוכנים לסריקה הבאה
                    currentWorkerDocId = null;
                    currentWorkerName = null;
                    currentWorkerLastEntryDate = null;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה ברישום היציאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}