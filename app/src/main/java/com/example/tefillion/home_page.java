package com.example.tefillion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class home_page extends AppCompatActivity implements View.OnClickListener {

    LinearLayout alter,prayer_places,prayer_menu,shabat,weekly_Torah_Portion;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Weekly_Torah_Portion");
    Pray weekly_torah_portion = new Pray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // פונקציה אשר קוראת מהפיירביס
        readDBS();

        intiView();
        btnStart();

    }



    //  הפונקציה הולכת לפייר ביס , מביאה את פרשת השבוע ושומרת אותו באובייקט ממחלקה "תפילות" ל
    private void readDBS() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                    String name = datasnapshot.child("name").getValue().toString();
                    String details = datasnapshot.child("details").getValue().toString();
                    weekly_torah_portion = new Pray(name, details);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(home_page.this, "Fail to get database", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void intiView() {
        alter = findViewById(R.id.alert);
        prayer_places = findViewById(R.id.prayer_places);
        prayer_menu = findViewById(R.id.prayer_menu);
        shabat = findViewById(R.id.shabat);
        weekly_Torah_Portion = findViewById(R.id.weekly_Torah_Portion);
    }

    private void btnStart() {
        alter.setOnClickListener(this);
        prayer_places.setOnClickListener(this);
        prayer_menu.setOnClickListener(this);
        shabat.setOnClickListener(this);
        weekly_Torah_Portion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //  עבור כול לחיצה אנו מבצאים את הפעולה בהתאם
        switch (v.getId()) {
            case R.id.alert:
                startActivity(new Intent(home_page.this, alert.class));
                break;
            case R.id.prayer_places:
                startActivity(new Intent(home_page.this, prayer_places.class));
                break;
            case R.id.prayer_menu:
                startActivity(new Intent(home_page.this, prayer_menu.class));
                break;
            case R.id.shabat:
                startActivity(new Intent(home_page.this, shabat.class));
                break;
            case R.id.weekly_Torah_Portion:
                //  כשאר אנו נירצה לעבור לפרשת השבוע אנו נישמור את פרטי הפרשה ב"אינטנט" כמטען ונעבור איתו לאקטיביטי המתאים

                Intent myIntent = new Intent(this,prayerPage.class);
                myIntent.putExtra("calling-activity",1);
                myIntent.putExtra("weekly_torah_portion_name", weekly_torah_portion.getName());
                myIntent.putExtra("weekly_torah_portion_details", weekly_torah_portion.getDetails());
                startActivity(myIntent);
                break;
        }
    }
}