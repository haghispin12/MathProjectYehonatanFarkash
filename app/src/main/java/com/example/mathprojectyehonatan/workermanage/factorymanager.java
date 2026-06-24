package com.example.mathprojectyehonatan.workermanage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectyehonatan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class factorymanager extends AppCompatActivity {

    // הגדרת משתנים עבור הרכיבים הגרפיים במסך
    private Button btnAddWorker;
    private RecyclerView rcShowWorker;
    private EditText etSearch;
    private MyWorkerAdapter myWorkerAdapter;
    private String myFactoryNumber;
    private TextView tvdate;

    // רשימה ששומרת את העובדים שנמצא כרגע במסך
    ArrayList<worker> workers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factorymanager);
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        // חיבור הכפתורים והעיצוב לקוד
        initviews();

        displayCurrentDate();
        //  זיהוי אוטומטי של המנהל המחובר ושליפת העובדים שלו
        fetchManagerFactoryNumber();

    }

    /**
     * פעולה זו מקשרת בין משתני הקוד לבין רכיבי ה-XML במסך (כפתורים, רשימה וכו').
     * בנוסף, היא מגדירה מה קורה כשלוצים על כפתור הוספת עובד,
     * ופותחת מאזין שמקשיב למה שהמנהל מקליד בשורת החיפוש.
     */
    private void initviews() {
        btnAddWorker = findViewById(R.id.addWorker);
        rcShowWorker = findViewById(R.id.recycle);
        tvdate = findViewById(R.id.date);
// חיבור ההתנתקות לכפתור הוספת העובד (מכיוון שזה הכפתור שיש לך פה)
        setupLogoutGesture(btnAddWorker);
        // לחיצה על כפתור הוספת עובד - פותחת את ה-Fragment המתאים
        btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // . יוצרים את ה"הודעה" (Intent) שתשלח ל-Receiver
                Intent intent = new Intent(factorymanager.this, TimeTriggerReceiver.class);
//  מוסיפים את מספר המפעל (כדי שה-Receiver ידע איזה נתונים לבדוק)
                intent.putExtra("factoryNumber", myFactoryNumber);
                AddWorker fragment = new AddWorker();
                getSupportFragmentManager().beginTransaction().replace(R.id.FrAddWorker, fragment, "anyTagName").addToBackStack(null).commit();

            }
        });

        etSearch = findViewById(R.id.etSearch);

        // מאזין לתיבת החיפוש - מסנן את הרשימה בכל פעם שהמנהל מקליד אות
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                filter(s.toString()); // קריאה לפעולת הסינון
            }
        });
    }

    /**
     * בודקת מיהו המנהל שמחובר כרגע למערכת לפי האימייל שלו,
     * ניגשת לאוסף "factory manager" בענן, ושולפת את מספר המפעל ששייך לו.
     */
    private void fetchManagerFactoryNumber() {
        // שליפת המייל של המנהל שמחובר כרגע (דרך מערכת ההתחברות של פיירבייס)
        String managerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // הגנה: אם משום מה המייל ריק, נעצור כאן
        if (managerEmail == null) {
            Toast.makeText(this, "שגיאה בזיהוי המשתמש. אנא התחבר מחדש.", Toast.LENGTH_SHORT).show();
            return;
        }

        // חיפוש באוסף "factory manager" בענן - מחפשים מסמך שהשדה "mail" בו שווה לאימייל של המנהל
        FirebaseFirestore.getInstance().collection("factory manager")
                .whereEqualTo("mail", managerEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // בדיקה האם מצאנו מסמך כזה בענן
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);


                        // חילוץ מספר המפעל של המנהל (השדה שנקרא "factory number")
                        String factoryNumber = document.getString("factoryNumber");

                        // אם אכן יש מספר מפעל, נעבור לשלב הבא ונשלח את המספר הזה לפעולת שליפת העובדים
                        if (factoryNumber != null) {
                            DataRetrievalFirestore(factoryNumber);
                            scheduleDailyAlarms(factoryNumber);
                        } else {
                            Toast.makeText(this, "לא הוגדר מספר מפעל למנהל זה.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "המייל לא קיים ברשימת המנהלים.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בטעינת נתוני מנהל: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * מקבלת כקלט את מספר המפעל של המנהל, וניגשת לאוסף "workers" בענן.
     * שולפת רק את העובדים שהשדה "factoryNumber" שלהם תואם בדיוק למספר המפעל הזה.
     * * @param factoryNumberForThisManager מספר המפעל של המנהל הנוכחי.
     */
    public void DataRetrievalFirestore(String factoryNumberForThisManager) {
        FirebaseFirestore.getInstance().collection("workers")
                .whereEqualTo("factoryNumber", factoryNumberForThisManager) // סינון חכם לפי מספר המפעל
                .addSnapshotListener((queryDocumentSnapshots, e) -> {

                    // בדיקה אם יש בעיה בתקשורת מול השרת
                    if (e != null) {
                        Toast.makeText(factorymanager.this, "שגיאה בטעינת נתונים: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // אם חזרו נתונים מהענן
                    // בדיקה שהמידע שהתקבל מ-Firebase (או ממקור הנתונים) אינו ריק כדי למנוע קריסה
                    if (queryDocumentSnapshots != null) {
                        performDailyReset(queryDocumentSnapshots);
                        workers.clear(); // מנקים רשימה קיימת כדי שלא יהיו כפילויות במסך

                        // עוברים אחד-אחד על המסמכים שחזרו מהענן
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                // שליפת הנתונים של העובד מהמסמך
                                String firstName = documentSnapshot.getString("firstName");
                                String lastName = documentSnapshot.getString("lastName");
                                String id = documentSnapshot.getString("id");
                                boolean isEntered = documentSnapshot.getBoolean("isEntered") != null ? documentSnapshot.getBoolean("isEntered") : false;
                                String entryTime = documentSnapshot.getString("entryTime");
                                String exitTime = documentSnapshot.getString("exitTime");
                                String factoryNumber = documentSnapshot.getString("factoryNumber");


                                // יצירת אובייקט עובד חדש והגדרת הנתונים שלו
                                worker worker1 = new worker(firstName, lastName, id);
                                worker1.setEntered(isEntered);
                                worker1.setEntryTime(entryTime);
                                worker1.setExitTime(exitTime);
                                worker1.setFactoryNumber(factoryNumber);

                                // הוספת העובד לרשימה שתוצג למנהל
                                workers.add(worker1);
                            }
                        }

                        // רענון התצוגה: אם האדפטר ריק - ניצור אותו פעם אחת. אם לא - רק נעדכן אותו בשינויים
                        if (myWorkerAdapter == null) {
                            connectionAdapter();
                        } else {
                            myWorkerAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    /**
     * פונקציה זו מבצעת איפוס יומי לנתוני העובדים.
     * היא עוברת על כל העובדים שחזרו מהשאילתה, ובודקת האם התאריך האחרון שלהם שונה מהיום.
     * אם כן - היא מאפסת את סטטוס הכניסה והשעות שלהם.
     */
    private void performDailyReset(QuerySnapshot querySnapshot) {
        // 1. יצירת אובייקט WriteBatch:
        // פעולה זו מאפשרת לנו לאסוף את כל עדכוני הנתונים ולשלוח אותם לשרת בבת אחת (Atomic Operation).
        // זה הרבה יותר יעיל ומקצועי מאשר לעדכן כל עובד בנפרד (שיוצר עומס על השרת).
        com.google.firebase.firestore.WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 2. הגדרת התאריך של היום:
        // אנחנו משתמשים בפורמט 'yyyy-MM-dd' כדי שיתאים בדיוק לפורמט השמור בבסיס הנתונים (Firebase).
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());

        boolean hasUpdates = false;

        // 3. לולאה על כל העובדים שחזרו מהשאילתה:
        for (DocumentSnapshot doc : querySnapshot) {
            Boolean isEnteredVal = doc.getBoolean("isEntered");
            boolean isEntered = (isEnteredVal != null && isEnteredVal);
            // שליפת השדה המדויק שבו שמור התאריך האחרון של העובד
            String lastDate = doc.getString("lastEntryDate");

            // 4. לוגיקת הבדיקה (התנאי לאיפוס):
            if (lastDate == null || !lastDate.equals(today)) {
                batch.update(doc.getReference(), "isEntered", false);
                batch.update(doc.getReference(), "entryTime", "טרם נכנס");
                batch.update(doc.getReference(), "exitTime", "טרם יצא");
                // מעדכנים את התאריך האחרון להיום כדי לסמן שהעובד טופל ולא צריך לעבור איפוס נוסף היום.
                batch.update(doc.getReference(), "lastEntryDate", today);
                // מסמנים שבוצע לפחות שינוי אחד, כדי שנדע לשלוח את העדכון לשרת (חסכון במשאבים).
                hasUpdates = true;
            }
        }

        // 5. שליחת כל העדכונים לשרת בבת אחת:
        // הפקודה הזו מוציאה לפועל את כל העדכונים שאספנו בלולאה.
        if (hasUpdates) {
            batch.commit();
        }
    }


        /**
         * פעולה שמחברת את הרשימה (RecyclerView) אל האדפטר שיצרנו (MyWorkerAdapter).
         * בנוסף, מגדירה מה קורה כשלוחצים על עובד ברשימה (כרגע מציגה הודעה קצרה עם השם שלו).
         */
        public void connectionAdapter () {
            myWorkerAdapter = new MyWorkerAdapter(workers, item ->
                    Toast.makeText(factorymanager.this, item.getFirstName() + " " + item.getLastName(), Toast.LENGTH_SHORT).show()
            );
            rcShowWorker.setLayoutManager(new LinearLayoutManager(this));
            rcShowWorker.setAdapter(myWorkerAdapter);
            rcShowWorker.setHasFixedSize(true);
        }

        /**
         * פעולה לסינון הרשימה לפי מה שהמנהל מקליד בתיבת החיפוש.
         * בודקת האם הטקסט שהוקלד תואם לשם פרטי, שם משפחה או תעודת זהות של העובד.
         * * @param text הטקסט שהמנהל הקליד בתיבת החיפוש.
         */
        private void filter (String text){
            ArrayList<worker> filteredList = new ArrayList<>();

            // אם תיבת החיפוש ריקה, נציג את כל העובדים השייכים למפעל
            if (text == null || text.trim().isEmpty()) {
                if (myWorkerAdapter != null) {
                    myWorkerAdapter.filterList(workers);
                }
                return;
            }

            String searchText = text.trim();

            // עוברים על כל העובדים ברשימה ובודקים התאמה
            for (worker item : workers) {
                String firstName = item.getFirstName() != null ? item.getFirstName().trim() : "";
                String lastName = item.getLastName() != null ? item.getLastName().trim() : "";
                String idNumber = item.getId() != null ? item.getId().trim() : "";

                // אם הטקסט נמצא באחד מהשדות, נוסיף אותו לרשימה המסוננת
                if (firstName.contains(searchText) || lastName.contains(searchText) || idNumber.contains(searchText)) {
                    filteredList.add(item);
                }
            }

            // מעדכנים את האדפטר להציג רק את התוצאות המסוננות
            if (myWorkerAdapter != null) {
                myWorkerAdapter.filterList(filteredList);
            }
        }

        private void scheduleDailyAlarms (String factoryNumber){
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // בדיקה לאנדרואיד 12 ומעלה: האם יש לנו הרשאה לתזמן אזעקות מדויקות?
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // אם אין הרשאה, נשלח את המשתמש להגדרות
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                    return; // נעצור כאן עד שהמשתמש יאשר
                }
            }
            // 2. יצירת Intent - זהו "ההסכם" שאומר איזה קוד להריץ כשיגיע הזמן.
            Intent intent = new Intent(this, TimeTriggerReceiver.class);

            // כאן אנחנו "מדביקים" את מספר המפעל לתוך ה-Intent
            intent.putExtra("factoryNumber", factoryNumber);
            // 3. הגדרת שתי שעות (10 ו-18) באמצעות פונקציית עזר.
            setOneAlarm(alarmManager, intent, 10, 0, 10);
            setOneAlarm(alarmManager, intent, 18, 0, 18);
        }
    /**
     * מתזמנת התראה מדויקת בודדת לשעה ספציפית ביום.
     * הפונקציה מחשבת את זמן ההפעלה הקרוב ביותר; אם השעה המבוקשת כבר עברה היום,
     * היא תתזמן את ההתראה ליום המחרת.
     * השימוש ב-setExactAndAllowWhileIdle מבטיח שההתראה תופעל בדיוק בזמן,
     * גם אם המכשיר נמצא במצב שינה (Doze Mode).
     */
        private void setOneAlarm (AlarmManager am, Intent intent,int hour, int minute,
        int requestCode){
            // PendingIntent הוא "שלט רחוק". אנחנו לא מפעילים את הקוד עכשיו,
            // אלא מוסרים את השלט למערכת ההפעלה שתפעיל אותו מאוחר יותר.
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            // הגדרת הזמן בלוח השנה.
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // אם השעה עברה, נוסיף יום כדי שההתראה תכוון ליום הבא.
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            // RTC_WAKEUP: "תעיר את המכשיר אם הוא במצב שינה" (קריטי לדיוק).
            if (am != null) {
                try {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } catch (SecurityException e) {
                    // אם יש שגיאה, האפליקציה תתעלם ממנה במקום לקרוס
                    e.printStackTrace();
                }
            }

        }
        /**
         * פונקציית עזר להוספת "דלת אחורית" להתנתקות בלחיצה ארוכה.
         * הלחיצה הארוכה מבטיחה שהמשתמש לא יתנתק בטעות מפעולה רגילה בכפתור.
         *
         * @param view הרכיב (כפתור/אייקון) שעל גביו נפעיל את מנגנון ההתנתקות.
         */
        private void setupLogoutGesture (View view){
            // הגדרת מאזין ללחיצה ארוכה (Long Click) על הכפתור
            view.setOnLongClickListener(v -> {

                // 1. התנתקות מפיירבייס: מחיקת פרטי המשתמש הנוכחי מהזיכרון של המכשיר ומהחיבור לשרת
                FirebaseAuth.getInstance().signOut();

                // 2. יצירת Intent למעבר למסך ההתחברות (MainActivityLogin)
                Intent intent = new Intent(factorymanager.this, MainActivityLogin.class);

                // 3. הגדרת דגלים (Flags) למניעת חזרה לאחור:
                // FLAG_ACTIVITY_NEW_TASK: פותח את מסך ההתחברות כמשימה חדשה.
                // FLAG_ACTIVITY_CLEAR_TASK: מוחק את כל המסכים הקודמים (סטק הפעילויות) מהזיכרון.
                // זה קריטי כדי שבלחיצה על "חזור" בטלפון, המשתמש לא יחזור למסך הניהול.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // 4. הפעלת מסך ההתחברות וסגירת המסך הנוכחי (finish) מהזיכרון
                startActivity(intent);
                finish();

                // 5. חיווי ויזואלי למשתמש שהפעולה הצליחה
                Toast.makeText(factorymanager.this, "התנתקת בהצלחה!", Toast.LENGTH_SHORT).show();

                // 6. מחזירים true כדי לציין למערכת שהלחיצה הארוכה טופלה במלואה,
                // ובכך אנחנו מונעים הפעלה של לחיצה רגילה (OnClick) במקביל.
                return true;
            });
        }
    /**
     * פונקציה זו אחראית על שליפת התאריך הנוכחי מהמערכת,
     * עיצובו בפורמט קריא, והצגתו בתוך רכיב ה-TextView במסך.
     */
    private void displayCurrentDate() {
        // 1. יצירת אובייקט לפורמט תאריך (SimpleDateFormat)
        // אנחנו מגדירים לו את התבנית "יום/חודש/שנה" (dd/MM/yyyy)
        // ומשתמשים ב-Locale.getDefault() כדי שהפורמט יתאים להגדרות השפה של הטלפון
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());

        // 2. יצירת אובייקט Date שמייצג את הרגע הנוכחי ("עכשיו")
        java.util.Date now = new java.util.Date();

        // 3. המרת התאריך למחרוזת (String) לפי הפורמט שהגדרנו למעלה
        String currentDate = dateFormat.format(now);

        // 4. עדכון רכיב ה-TextView במסך עם הטקסט המעוצב
        // אנחנו מוסיפים את המילה "תאריך: " לפני הערך שקיבלנו
        tvdate.setText("תאריך: " + currentDate);
    }

}
