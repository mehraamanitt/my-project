package com.exmple.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminScreenActivity extends AppCompatActivity {
    TextView monthText;
    RecyclerView recyclerView;
    ImageView logoutButton;

    //firebase stuff
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        //firebase========
        auth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        //===========

        monthText=findViewById(R.id.month_text);
        recyclerView=findViewById(R.id.recycler_view);
        logoutButton=findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent=new Intent(AdminScreenActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // get the current month and create string for it
        String monthString = "";
        String month = BookingSlotActivity.getMonthString(String.valueOf(System.currentTimeMillis())).split("_")[0];
        switch (month){
            case "01":
                monthString="JANUARY";
                break;
            case "02":
                monthString="FEBRUARY";
                break;
            case "03":
                monthString="MARCH";
                break;
            case "04":
                monthString="APRIL";
                break;
            case "05":
                monthString="MAY";
                break;
            case "06":
                monthString="JUNE";
                break;
            case "07":
                monthString="JULY";
                break;
            case "08":
                monthString="AUGUST";
                break;
            case "09":
                monthString="SEPTEMBER";
                break;
            case "10":
                monthString="OCTOBER";
                break;
            case "11":
                monthString="NOVEMBER";
                break;
            case "12":
                monthString="DECEMBER";
                break;
        }
        monthText.setText(monthString);

        reference.child("book_slot_info/booked_slots").child(BookingSlotActivity.getMonthString(String.valueOf(System.currentTimeMillis())))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> uidList=new ArrayList<>();
                        ArrayList<String> shiftList=new ArrayList<>();
                        ArrayList<String> cityList = new ArrayList<>();

                        for(DataSnapshot snap:snapshot.getChildren()){
                            shiftList.add(snap.child("shift").getValue(String.class));
                            uidList.add(snap.child("uid").getValue(String.class));
                            cityList.add(snap.child("city").getValue(String.class).toUpperCase());
                        }

                        // create and attach adapter to the recycler_view
                        CustomAdapter obj=new CustomAdapter(uidList,shiftList, cityList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AdminScreenActivity.this));
                        recyclerView.setAdapter(obj);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}