package com.example.tefillion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;


public class prayerPage extends AppCompatActivity {


    String name, details;
    TextView namePray ,detailsPray ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        int callingActivity = getIntent().getIntExtra("calling-activity", 0);

        switch (callingActivity) {
            case 1:
                intiGet_weekly_Torah_Portion();
                break;
            case 2:
                intiGetBundle();
                break;
        }


        intiView();

        intiStart();

    }

    private void intiStart() {
        if (!name.isEmpty() && !details.isEmpty())
        {
            namePray.setText(name);
            detailsPray.setText(details);
        }
    }

    private void intiView() {
        namePray = findViewById(R.id.namePray);
        detailsPray = findViewById(R.id.detailsPray);
        detailsPray.setMovementMethod(new ScrollingMovementMethod());

    }

    private void intiGetBundle() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        details = intent.getStringExtra("details");


    }


    private void intiGet_weekly_Torah_Portion() {

        Intent intent = getIntent();
        name = intent.getStringExtra("weekly_torah_portion_name");
        details = intent.getStringExtra("weekly_torah_portion_details");


    }











}