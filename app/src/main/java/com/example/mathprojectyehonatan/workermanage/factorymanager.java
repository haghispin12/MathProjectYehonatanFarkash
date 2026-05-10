package com.example.mathprojectyehonatan.workermanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
                getSupportFragmentManager().beginTransaction().replace(R.id.FrAddWorker, fragment, "anyTagName").commit(); //פ
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
        MyWorkerAdapter myWorkerAdapter = new MyWorkerAdapter(workers, new InterOnWorkerClickListener() {
            @Override
            public void OnWorkerClick(worker item) {
                Toast.makeText(factorymanager.this, item.getFirstName()+" "+item.getLastName(), Toast.LENGTH_SHORT).show();
            }
        });
        rcShowWorker.setLayoutManager(new LinearLayoutManager(this));
        rcShowWorker.setAdapter(myWorkerAdapter);
        rcShowWorker.setHasFixedSize(true);
    }
}