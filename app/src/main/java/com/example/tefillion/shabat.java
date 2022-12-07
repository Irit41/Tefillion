package com.example.tefillion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class shabat extends AppCompatActivity {

    ListView listCities;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Saturday_Hours");
    ArrayList<Shabbat_entry_times> shabbat_entry_times = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shabat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        listCities = findViewById(R.id.listCities);

        // פונקציה אשר מחזירה את מערך הערים מהפייר ביס
        readDBS();

    }

    // הפונקציה ניגשת לפייר ביס , מושכת את מערך הערים לרשימה מהמחלקה "זמני כניסת שבת" ו
    private void readDBS() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    String entry = snapshot.child("entry").getValue().toString();
                    String exit = snapshot.child("exit").getValue().toString();
                    Shabbat_entry_times temp = new Shabbat_entry_times(name, entry, exit);
                    shabbat_entry_times.add(temp);
                }
                // יצירת אובייקט של המחלקה אשר יוצרת את הלאווט המתאים
                ShabatTimesAdapter shabatTimesAdapter = new ShabatTimesAdapter(shabat.this, shabbat_entry_times);

                // השמת התבנית החדשה לרשימה המוצגת באקטיביטי
                listCities.setAdapter(shabatTimesAdapter);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(shabat.this, "Fail to get database", Toast.LENGTH_SHORT).show();
            }
        });


    }


    // מחלקה אשר משתמשת בטבנית הלאווט המתאימה , משנה את התבנית במקומות הרלוונטים ומחזירה אצ לתבנית לאחר השינוי
    public class ShabatTimesAdapter extends ArrayAdapter<Shabbat_entry_times> {

        public ArrayList<Shabbat_entry_times> listTimes;
        Context context;

        public ShabatTimesAdapter(@NonNull Context context, ArrayList<Shabbat_entry_times> objects) {
            super(context, R.layout.shabat_cards, objects);

            this.context = context;
            this.listTimes = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = LayoutInflater.from(context).inflate(R.layout.shabat_cards, parent, false);

            TextView city_name = view.findViewById(R.id.name);
            TextView EntryHour = view.findViewById(R.id.entry);
            TextView ExitHour = view.findViewById(R.id.exit);
            city_name.setText("שם העיר : " + getItem(position).getName());
            EntryHour.setText("כניסה: " + getItem(position).getEntry());
            ExitHour.setText("יציאה: " + getItem(position).getExit());

            return view;
        }

    }
}