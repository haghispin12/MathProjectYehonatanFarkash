package com.example.mathprojectyehonatan.workermanage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    // רשימה ששומרת את העובדים שנמצא כרגע במסך
    ArrayList<worker> workers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factorymanager);

        // שלב 1: חיבור הכפתורים והעיצוב לקוד
        initviews();

        // שלב 2: זיהוי אוטומטי של המנהל המחובר ושליפת העובדים שלו
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

        // לחיצה על כפתור הוספת עובד - פותחת את ה-Fragment המתאים
        btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWorker fragment = new AddWorker();
                getSupportFragmentManager().beginTransaction().replace(R.id.FrAddWorker, fragment, "anyTagName").addToBackStack(null).commit();
            }
        });

        etSearch = findViewById(R.id.etSearch);

        // מאזין לתיבת החיפוש - מסנן את הרשימה בכל פעם שהמנהל מקליד אות
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                filter(s.toString()); // קריאה לפעולת הסינון
            }
        });
    }

    /**
     * פעולה 1 מתוך 2 לשליפת נתונים:
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
     * פעולה 2 מתוך 2 לשליפת נתונים:
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
                    if (queryDocumentSnapshots != null) {
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
     * פעולה שמחברת את הרשימה (RecyclerView) אל האדפטר שיצרנו (MyWorkerAdapter).
     * בנוסף, מגדירה מה קורה כשלוחצים על עובד ברשימה (כרגע מציגה הודעה קצרה עם השם שלו).
     */
    public void connectionAdapter() {
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
    private void filter(String text) {
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
}