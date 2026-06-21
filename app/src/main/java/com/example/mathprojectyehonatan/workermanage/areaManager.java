package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectyehonatan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class areaManager extends AppCompatActivity {


    private MyWorkerAdapter allWorkersAdapter;
    private RecyclerView allWorkersRecyclerView;
    private ArrayList<worker> workersList;
    private EditText etSearchW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_area_manager);

        initviews();
popAllworkersFromFiresroe();
    }

    private void initviews() {
        allWorkersRecyclerView = findViewById(R.id.recyclerViewAllWorkers);
        etSearchW = findViewById(R.id.etSearch1);
        // קובעים שהרשימה תופיע בצורה ישרה (טור אחד למטה)
        allWorkersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

// מייצרים רשימה ריקה בהתחלה (כדי שלא תקרוס לפני שהנתונים יורדים מהאינטרנט)
        workersList = new ArrayList<>();

// יוצרים את הגשר (Adapter) ושולחים לו את הרשימה הריקה
        allWorkersAdapter = new MyWorkerAdapter(workersList, new InterOnWorkerClickListener() {
            @Override
            public void OnWorkerClick(worker clickedWorker) {
            }
        });
        // מחברים את האדפטר ל-RecyclerView
        allWorkersRecyclerView.setAdapter(allWorkersAdapter);
        etSearchW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //משמש את התיבת חיפוש
                // פעולה זו מתבצעת שבריר שנייה *לפני* שהטקסט משתנה בפועל.
                // משתמשים בה בעיקר כאשר רוצים לשמור או לבדוק מה היה הטקסט המקורי רגע לפני ההקלדה או המחיקה
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //משמש את התיבת חיפוש
                // פעולה זו מתבצעת *בזמן* שהטקסט מתחלף (כל תו שנוסף או נמחק).
                // שימושי למקרים שבהם רוצים להציג חיווי או לבצע בדיקות בזמן אמת תוך כדי תנועה.
            }

            @Override
            public void afterTextChanged(Editable s) { // משמש את התיבת חיפוש
                filterWorkers(s.toString());
                // פעולה זו מתבצעת מיד *אחרי* שההקלדה או המחיקה הסתיימו והטקסט החדש מוצג על המסך.
                // זהו השלב הבטוח והמומלץ ביותר לקחת את הטקסט המלא שהוקלד ולשלוח אותו לפונקציית הסינון.
            }
        });
    }


    //פעולה שמביאה מהשרת את הנתונים
    private void popAllworkersFromFiresroe() {
        // קריאה לשרת ושליפה מהקלקציה הנכונה ומבקשים את הנתונים
        FirebaseFirestore.getInstance().collection("workers")
                .get()
                .addOnCompleteListener(task -> {
                    // בודקים שהשליפה מהאינטרנט הצליחה
                    if (task.isSuccessful() && task.getResult() != null) {
                        // מנקים את הרשימה מנתונים קודמים (מונע כפילויות)

                        workersList.clear();
                        // עוברים על כל מסמך שחזר מהשרת
                        for (DocumentSnapshot document : task.getResult()) {
                            // ממירים את המסמך לאובייקט מסוג worker
                            worker w = document.toObject(worker.class);

                            if (w != null) {
                                // מוסיפים את העובד לרשימה שלנו
                                workersList.add(w);
                            }
                        }
                        // מעדכנים את האדפטר שהרשימה התמלאה בנתונים החדשים
                        allWorkersAdapter.notifyDataSetChanged();
                    }
                });

    }
        private void filterWorkers(String textToSearch) {

// יוצרים רשימה זמנית שתחזיק רק את התוצאות שעונות על החיפוש
            ArrayList<worker> filteredList = new ArrayList<>();
// עוברים על כל העובדים ששמורים ברשימה המקורית שלנו
            for (worker w : workersList) {


// בודקים האם השם של העובד או תעודת הזהות שלו מכילים את מה שהוקלד
                // (אנחנו משתמשים ב-toLowerCase כדי שהחיפוש לא יהיה רגיש לאותיות גדולות/קטנות)
                if ((w.getFirstName() + " " + w.getLastName()).toLowerCase().contains(textToSearch.toLowerCase()) || w.getId().toLowerCase().contains(textToSearch.toLowerCase()) || w.getFactoryNumber() != null && w.getFactoryNumber().toLowerCase().contains(textToSearch.toLowerCase())) { //.toLowerCase().contains(...) – ממיר את מספר המפעל לאותיות קטנות ומשווה אותו לטקסט שחפשנו, כך שהחיפוש ימצא תוצאות ללא תלות באותיות קטנות או גדולות.
                    // אם כן, מוסיפים את העובד לרשימה המסוננת
                    filteredList.add(w);

                }
            }
            //שליחת הרשימה המסוננת לאדפטר כדי שיצייר אותה מחדש על המסך
                 allWorkersAdapter.filterList(filteredList);
                }
    /**
     * פונקציית עזר להוספת "דלת אחורית" להתנתקות בלחיצה ארוכה.
     * הלחיצה הארוכה מבטיחה שהמשתמש לא יתנתק בטעות מפעולה רגילה בכפתור.
     *
     * @param view הרכיב (כפתור/אייקון) שעל גביו נפעיל את מנגנון ההתנתקות.
     */
    private void setupLogoutGesture(View view) {
        // הגדרת מאזין ללחיצה ארוכה (Long Click) באמצעות Anonymous Class
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // 1. התנתקות מפיירבייס
                FirebaseAuth.getInstance().signOut();

                // 2. יצירת Intent למעבר למסך ההתחברות (MainActivityLogin)
                // שים לב: אנחנו משתמשים ב-EnterWorker.this כדי לציין את ההקשר (Context) של המחלקה
                Intent intent = new Intent(areaManager.this, MainActivityLogin.class);

                // 3. הגדרת דגלים (Flags) למניעת חזרה לאחור
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // 4. הפעלת מסך ההתחברות וסגירת המסך הנוכחי
                startActivity(intent);
                finish();

                // 5. חיווי ויזואלי למשתמש
                Toast.makeText(areaManager.this, "התנתקת בהצלחה!", Toast.LENGTH_SHORT).show();

                // 6. מחזירים true כדי לציין שהלחיצה טופלה בהצלחה
                return true;
            }
        });
    }
        }