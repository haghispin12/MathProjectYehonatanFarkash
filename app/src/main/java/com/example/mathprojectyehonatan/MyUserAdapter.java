package com.example.mathprojectyehonatan;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.MyViewHolder> {
    private ArrayList<User> userr ;
    private MyInterOnItemClickListener listener;
    public MyUserAdapter(ArrayList<User> user, MyInterOnItemClickListener listener) {
        this.userr = user;
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemusers,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserAdapter.MyViewHolder holder, int position) {
    holder.bind(userr.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return userr.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvUserName;
        ImageView ivUserImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivUserImg = itemView.findViewById(R.id.ivUserImg);
        }

        public void bind (final )
    }

}
