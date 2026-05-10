package com.example.mathprojectyehonatan.workermanage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mathprojectyehonatan.R;
import com.example.mathprojectyehonatan.mathproject.MyUserAdapter;

import java.util.ArrayList;

public class MyWorkerAdapter extends RecyclerView.Adapter<MyWorkerAdapter.MyViewHolder> {
    private ArrayList<worker> workers;
    private InterOnWorkerClickListener listener;
    private TextView TvFullName;
    private TextView tvId;


    public MyWorkerAdapter(ArrayList<worker> workers,InterOnWorkerClickListener listener) {
        this.workers = workers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyWorkerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemworker, parent, false);

        return new MyWorkerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWorkerAdapter.MyViewHolder holder, int position) { //לוקחת אובייקט מהמערך בהתאם לגלילה של המשתמש
            holder.bind(workers.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{ //מייצר את המפגש בין המערך לתבנית שבנינו(item)

       private TextView TvFullName;
       private TextView TvId;
       private   ImageView IvWorkerImg;

        public MyViewHolder(@NonNull android.view.View itemView) {//מייבא את התבנית
            super(itemView);
            TvFullName = itemView.findViewById(R.id.fullName);
            TvId = itemView.findViewById(R.id.id);
            IvWorkerImg = itemView.findViewById(R.id.pictre);
        }
        public void bind(final worker item, final InterOnWorkerClickListener listener) { //מייצרת את המפגש בין המערך לתבניות
            TvFullName.setText(item.getFirstName()+ " " + item.getLastName());
            TvId.setText(item.getId());
         //   IvWorkerImg.setImageResource(item.);
            itemView.setOnClickListener(new android.view.View.OnClickListener() { //מה קורה כשלוחצים על אחד מהפריטים ברשימה
                @Override
                public void onClick(android.view.View v) {
                    listener.OnWorkerClick(item);
                }
            });
        }
    }

}
