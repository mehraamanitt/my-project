package com.exmple.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookedSlotInfo extends AppCompatActivity {

    ImageView logoutButton;
    TextView nameText, slotText;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_slot_info);

        logoutButton=findViewById(R.id.logout_button);
        nameText=findViewById(R.id.name_text);
        slotText=findViewById(R.id.slot_text);

        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();

        //logout the user when he/she clicks logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent=new Intent(BookedSlotInfo.this,LoginActivity.class);
                startActivity(intent);
                Toast.makeText(BookedSlotInfo.this, "Logout successful", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //get value from data base to show his/her slot info
        reference.child("book_slot_info/booked_slots").child(BookingSlotActivity.getMonthString(String.valueOf(System.currentTimeMillis())))
                .child(auth.getCurrentUser().getUid()).child("shift").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String slot="";
                switch(snapshot.getValue(String.class)){
                    case "1":
                        slot="6 AM to 7 AM";
                        break;
                    case "2":
                        slot="7 AM to 8 AM";
                        break;
                    case "3":
                        slot="8 AM to 9 AM";
                        break;
                    case "4":
                        slot="5 PM to 6 PM";
                        break;
                    case "5":
                        slot="6 PM to 7 PM";
                        break;
                    case "6":
                        slot="7 PM to 8 PM";
                        break;
                }
                slotText.setText("Your Slot "+slot+" is booked till the end of this month");

                reference.child("all_users_info").child(auth.getCurrentUser().getUid())
                        .child("fullName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        nameText.setText("Thank you "+snapshot.getValue(String.class)+" for booking the slot");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}