package com.example.tefillion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class prayer_menu extends AppCompatActivity implements View.OnClickListener {


    LinearLayout modaAni, semaIsrael, shmaMita, shachrit, food, mariv, road, shabat;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Prayers");
    ArrayList<Pray> prayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        intiView();

        readDBS();

        intiStart();

    }


    // פונקציה אשר הולכת לפייר ביס , מושכת את מערך התפילות , יוצרת מכול תפילה אובייקט ממחלקה "תפילה" ומוסיפה אותו לרשימה מהמחלקה
    private void readDBS() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue().toString();
                    String details = snapshot.child("details").getValue().toString();
                    Pray pray = new Pray(name, details);
                    prayList.add(pray);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(prayer_menu.this, "Fail to get database", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void intiStart() {
        modaAni.setOnClickListener(this);
        semaIsrael.setOnClickListener(this);
        shmaMita.setOnClickListener(this);
        shachrit.setOnClickListener(this);
        food.setOnClickListener(this);
        mariv.setOnClickListener(this);
        road.setOnClickListener(this);
        shabat.setOnClickListener(this);

    }

    private void intiView() {
        modaAni = findViewById(R.id.modaAni);
        semaIsrael = findViewById(R.id.semaIsrael);
        shmaMita = findViewById(R.id.shmaMita);
        shachrit = findViewById(R.id.shachrit);
        food = findViewById(R.id.food);
        mariv = findViewById(R.id.mariv);
        road = findViewById(R.id.road);
        shabat = findViewById(R.id.shabat);

    }

    // פונקציה אשר מחזירה את התפילה המתאימה בהתאם לשם שלה השווה לסטרינג שאנו שולחים כפרמטר
    public Pray GetPray(String name) {
        Pray pray = null;
        for (Pray p : prayList) {
            if (p.getName().equals(name))
                pray = p;
        }
        return pray;
    }


    public void onClick(@NonNull View v) {
        Pray pray = null;
        //  אנו קוראים לפונקציה המחזירה את התפילה הרצוייה בהתאם לתפילה המבוקשת
        switch (v.getId()) {
            case R.id.modaAni:
                pray = GetPray( "מודה אני");
                break;
            case R.id.semaIsrael:
                pray = GetPray("שמע ישראל");
                break;
            case R.id.shmaMita:
                pray = GetPray("ברכה לפני השינה");
                break;
            case R.id.shachrit:
                pray = GetPray("תפילת שחרית");
                break;
            case R.id.food:
                pray = GetPray("ברכת המזון");
                break;
            case R.id.mariv:
                pray = GetPray("תפילת ערבית");
                break;
            case R.id.road:
                pray = GetPray("תפילת הדרך");
                break;
            case R.id.shabat:
                pray = GetPray("תפילת שבת");
                break;
        }

        //  כשאר אנו נירצה לעבור לתפילה אנו נישמור את פרטי התפילה ב"אינטנט" כמטען ונעבור איתו לאקטיביטי המתאים
        Intent myIntent = new Intent(this,prayerPage.class);
        myIntent.putExtra("calling-activity", 2);
        myIntent.putExtra("name", pray.getName());
        myIntent.putExtra("details", pray.getDetails());
        startActivity(myIntent);

    }
}