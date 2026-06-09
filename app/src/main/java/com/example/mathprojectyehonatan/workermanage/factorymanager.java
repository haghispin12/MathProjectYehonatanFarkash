package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.mathprojectyehonatan.mathproject.MainActivity;
import com.example.mathprojectyehonatan.mathproject.RateActivity;
import com.example.mathprojectyehonatan.mathproject.ShowAllUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;


public class factorymanager extends AppCompatActivity {
    private Button btnAddWorker;
    private worker wrk1;
    private RecyclerView rcShowWorker;
    // שדה טקסט שייצג את תיבת החיפוש שהוספנו ב-XML
    private EditText etSearch;

    // משתנה שיחזיק את האדפטר של הרשימה כדי שנוכל לעדכן אותה בזמן החיפוש
    private MyWorkerAdapter myWorkerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_factorymanager);
        initviews();
        DataRetrievalFirestore();
    }
    private void initviews() {
        btnAddWorker = findViewById(R.id.addWorker);
        rcShowWorker =findViewById(R.id.recycle);

        btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWorker fragment = new AddWorker(); //
                getSupportFragmentManager().beginTransaction().replace(R.id.FrAddWorker, fragment, "anyTagName").addToBackStack(null).commit(); //פ
            }
        });
        // מקשר את משתנה החיפוש לתיבה האמיתית שנמצאת ב-XML לפי ה-ID שלה
        etSearch = findViewById(R.id.etSearch);

// פקודה שמקשיבה למה שהמשתמש מקליד בתיבת החיפוש בכל רגע נתון
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // ברגע שהטקסט משתנה (אות מתווספת או נמחקת), אנחנו שולחים את המילה לפונקציית הסינון
                filter(s.toString());
            }
        });

    }
    ArrayList<worker> workers = new ArrayList<>();
    public void DataRetrievalFirestore() {
        FirebaseFirestore.getInstance().collection("workers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String id = documentSnapshot.getString("id");
                        worker worker1 = new worker(firstName, lastName, id);
                        workers.add(worker1);

                    }
                }
                connectionAdapter();
            }
        });
    }
     public void connectionAdapter() {
         // משתמשים במשתנה הכללי שהגדרנו בראש המחלקה במקום ליצור אחד מקומי חדש
         myWorkerAdapter = new MyWorkerAdapter(workers, new InterOnWorkerClickListener() {

             @Override
            public void OnWorkerClick(worker item) {
                Toast.makeText(factorymanager.this, item.getFirstName()+" "+item.getLastName(), Toast.LENGTH_SHORT).show();
            }
        });
        rcShowWorker.setLayoutManager(new LinearLayoutManager(this));
        rcShowWorker.setAdapter(myWorkerAdapter);
        rcShowWorker.setHasFixedSize(true);

    }
    // פונקציה שמקבלת את הטקסט שהוקלד ומסננת את רשימת העובדים
    // פונקציית סינון מעודכנת שתומכת בעברית ובאנגלית במקביל
    private void filter(String text) {
        ArrayList<worker> filteredList = new ArrayList<>();

        // אם תיבת החיפוש ריקה, נציג מיד את כל הרשימה המקורית
        if (text == null || text.trim().isEmpty()) {
            if (myWorkerAdapter != null) {
                myWorkerAdapter.filterList(workers);
            }
            return;
        }

        // ניקוי רווחים מיותרים מהחיפוש של המשתמש
        String searchText = text.trim();

        for (worker item : workers) {
            // שליפת הנתונים מהעובד (אם הם ריקים, נשים טקסט ריק כדי למנוע קריסה)
            String firstName = item.getFirstName() != null ? item.getFirstName().trim() : "";
            String lastName = item.getLastName() != null ? item.getLastName().trim() : "";
            String idNumber = item.getId() != null ? item.getId().trim() : "";

            // בדיקת התאמה מדויקת (עובד מעולה גם בעברית וגם במספרים)
            if (firstName.contains(searchText) || lastName.contains(searchText) || idNumber.contains(searchText)) {
                filteredList.add(item);
            }
        }

        // עדכון האדפטר עם הרשימה המסוננת
        if (myWorkerAdapter != null) {
            myWorkerAdapter.filterList(filteredList);
        }
    }


}