package com.exmple.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;


public class BookingSlotActivity extends AppCompatActivity {

    ImageView logoutButton;
    RadioGroup allShifts;
    Button confirmButton;
    TextView slots;

    //firebase stuff
    FirebaseAuth auth;
    DatabaseReference reference;
    static Long oneDayMillis = 60*1000*60*24l;
    int shiftInt=0;
    Integer slot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_slot);

        logoutButton=findViewById(R.id.logout_button);
        allShifts=findViewById(R.id.all_shifts);
        confirmButton=findViewById(R.id.confirm_button);
        slots=findViewById(R.id.slots);

        // cons
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        //end

        //firebase stuff
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();

        // if user have already booked the slot, then move to BookedSlotInfo activity
        reference.child("book_slot_info/booked_slots").child(BookingSlotActivity.getMonthString(String.valueOf(System.currentTimeMillis())))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnap: snapshot.getChildren()){
                    if (auth.getCurrentUser().getUid().equals(childSnap.getKey())){
                        Intent intent=new Intent(BookingSlotActivity.this,BookedSlotInfo.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get booking slots value from realtime db
        reference.child("all_users_info").child(auth.getCurrentUser().getUid()).child("city").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Toast.makeText(BookingSlotActivity.this, snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                String city=snapshot.getValue(String.class);
                reference.child("book_slot_info/available_slots/").child(getMonthString(String.valueOf(System.currentTimeMillis())))
                        .child(city).child("1").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        slots.setText(String.valueOf(snap.getValue(Integer.class)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingSlotActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingSlotActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        //confirm booking button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // fresh start
//======================
                switch(allShifts.getCheckedRadioButtonId()){
                    case R.id.morning_6:
                        shiftInt=1;
                        break;
                    case R.id.morning_7:
                        shiftInt=2;
                        break;
                    case R.id.morning_8:
                        shiftInt=3;
                        break;
                    case R.id.evening_5:
                        shiftInt=4;
                        break;
                    case R.id.evening_6:
                        shiftInt=5;
                        break;
                    case R.id.evening_7:
                        shiftInt=6;
                        break;
                }

                reference.child("all_users_info").child(auth.getCurrentUser().getUid()).child("city").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Toast.makeText(BookingSlotActivity.this, snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                        String city=snapshot.getValue(String.class);
                        reference.child("book_slot_info/available_slots/").child(getMonthString(String.valueOf(System.currentTimeMillis())))
                                .child(city).child(String.valueOf(shiftInt)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                // update slot textview with updated value
                                //slots.setText(String.valueOf(snap.getValue(Integer.class)));
                                slot=snap.getValue(Integer.class);
                                if (slot>0){
                                   //===================================================
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("uid",auth.getCurrentUser().getUid());
                                    Long nextDayTime = System.currentTimeMillis()+oneDayMillis;
                                    map.put("start_date",getDate(String.valueOf(nextDayTime)));
                                    map.put("shift", String.valueOf(shiftInt));
                                    map.put("city",city);

                                    //write in db
                                    reference.child("book_slot_info").child("booked_slots").child(getMonthString(String.valueOf(System.currentTimeMillis()))).child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                slot=slot-1;
                                                reference.child("book_slot_info/available_slots").child(getMonthString(String.valueOf(System.currentTimeMillis())))
                                                        .child(city).child(String.valueOf(shiftInt)).setValue(slot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(BookingSlotActivity.this, "Booked Successful", Toast.LENGTH_SHORT).show();

                                                        }
                                                        else{
                                                            Toast.makeText(BookingSlotActivity.this, "Unable to book,Try again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(BookingSlotActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(BookingSlotActivity.this, "Sorry no slots left in this time slot", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(BookingSlotActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingSlotActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //logout the user when he/she clicks logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent=new Intent(BookingSlotActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        allShifts.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.morning_6:
                        shiftInt=1;
                        break;
                    case R.id.morning_7:
                        shiftInt=2;
                        break;
                    case R.id.morning_8:
                        shiftInt=3;
                        break;
                    case R.id.evening_5:
                        shiftInt=4;
                        break;
                    case R.id.evening_6:
                        shiftInt=5;
                        break;
                    case R.id.evening_7:
                        shiftInt=6;
                        break;
                }

                //whenever radio button is clicked we have to tell available slots
                reference.child("all_users_info").child(auth.getCurrentUser().getUid()).child("city").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Toast.makeText(BookingSlotActivity.this, snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                        String city=snapshot.getValue(String.class);
                        reference.child("book_slot_info/available_slots/").child(getMonthString(String.valueOf(System.currentTimeMillis())))
                                .child(city).child(String.valueOf(shiftInt)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                // update slot textview with updated value
                                slots.setText(String.valueOf(snap.getValue(Integer.class)));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(BookingSlotActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingSlotActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Method to get formatted date string from date-ime stamp
    public String getDate(String user_created_time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        return formatter.format(Long.valueOf(user_created_time)).toUpperCase();
    }

    public static String getMonthString(String user_created_time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM_yyyy");
        return formatter.format(Long.valueOf(user_created_time)).toUpperCase();
    }
}