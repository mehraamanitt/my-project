package com.exmple.gymapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    //firebase stuff
    DatabaseReference reference;

    ArrayList<String> uidList;
    ArrayList<String> shiftList;
    ArrayList<String> cityList;

    public CustomAdapter(ArrayList<String> uidList,ArrayList<String> shiftList, ArrayList<String> cityList){
        this.uidList=uidList;
        this.shiftList=shiftList;
        this.cityList = cityList;
        reference= FirebaseDatabase.getInstance().getReference();

    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        reference.child("all_users_info").child(uidList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("fullName").getValue(String.class);
                holder.nameText.setText(name);
                holder.cityText.setText(cityList.get(position));
                holder.phoneText.setText("Phone: "+snapshot.child("phone").getValue(String.class));
                switch(shiftList.get(position)){
                    case "1":
                        holder.timeSlot.setText("6 AM - 7 AM");
                        break;
                    case "2":
                        holder.timeSlot.setText("7 AM - 8 AM");
                        break;
                    case "3":
                        holder.timeSlot.setText("8 AM - 9 AM");
                        break;
                    case "4":
                        holder.timeSlot.setText("5 PM - 6 PM");
                        break;
                    case "5":
                        holder.timeSlot.setText("6 PM - 7 PM");
                        break;
                    case "6":
                        holder.timeSlot.setText("7 PM - 8 PM");
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, timeSlot, cityText, phoneText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_text);
            timeSlot = itemView.findViewById(R.id.time_slot);
            cityText = itemView.findViewById(R.id.city_text);
            phoneText = itemView.findViewById(R.id.phone_text);
        }
    }
}
