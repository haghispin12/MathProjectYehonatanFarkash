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

    public MyWorkerAdapter(ArrayList<worker> workers, InterOnWorkerClickListener listener) {
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
    public void onBindViewHolder(@NonNull MyWorkerAdapter.MyViewHolder holder, int position) {
        // לוקחת אובייקט מהמערך בהתאם לגלילה של המשתמש ומעבירה אותו ל-ViewHolder
        holder.bind(workers.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }


    // --- המחלקה הפנימית שמנהלת את רכיבי התצוגה של פריט בודד ---
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView TvFullName;
        private TextView TvId;
        private ImageView IvWorkerImg;
        private TextView TvStatus;
        private TextView TvEntryTime;
        private TextView TvExitTime;
        private TextView TvFactoryNumber;

        public MyViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            TvFullName = itemView.findViewById(R.id.fullName);
            TvId = itemView.findViewById(R.id.id);
            TvStatus = itemView.findViewById(R.id.tvStatus);
            TvEntryTime = itemView.findViewById(R.id.tvEntryTime);
            TvExitTime = itemView.findViewById(R.id.tvExitTime);
            TvFactoryNumber = itemView.findViewById(R.id.tvFactoryNumber);
        }

        public void bind(final worker item, final InterOnWorkerClickListener listener) {
            TvFullName.setText(item.getFirstName() + " " + item.getLastName());
            TvId.setText(item.getId());

            // הצגת שעת כניסה
            TvEntryTime.setText("שעת כניסה ראשונה: " + (item.getEntryTime() != null ? item.getEntryTime() : "--:--"));

            // הצגת שעת היציאה (אם קיימת בענן, תוצג השעה. אם לא, יופיעו קווים)
            TvExitTime.setText("שעת יציאה אחרונה: " + (item.getExitTime() != null ? item.getExitTime() : "--:--"));
            TvFactoryNumber.setText("מספר מפעל: " + (item.getFactoryNumber()!= null ? item.getFactoryNumber() : "--")) ;
            if (item.isEntered()) {
                TvStatus.setText("סטטוס: במפעל");
                TvStatus.setTextColor(android.graphics.Color.parseColor("#25D366"));
            } else {
                TvStatus.setText("סטטוס: מחוץ למפעל");
                TvStatus.setTextColor(android.graphics.Color.RED);
            }

            itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    listener.OnWorkerClick(item);
                }
            });
        }
    }

    // פונקציה לעדכון הרשימה לאחר סינון החיפוש
    public void filterList(ArrayList<worker> filteredList) {
        this.workers = filteredList;
        notifyDataSetChanged();
    }
}