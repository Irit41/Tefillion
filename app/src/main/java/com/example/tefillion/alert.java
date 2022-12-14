package com.example.tefillion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class alert extends AppCompatActivity implements View.OnClickListener{


    Switch[] arr_of_switch =new Switch[7];
    Alert_Class[] alert_classes = new Alert_Class[7];
    LocalTime time = LocalTime.now();
    SharedPreferences sharedPreferences;

    ListView listView;
    ArrayList<String> tutorialList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        intiView();

        intiStart();

        intiGetAll();

        //  התרד מתבצע כול שניה ובוא הוא מעדכן את השעה הנוכחית ומפעיל את הפונקציה הרצה על מערך ההתראות מבלי להקריס את התוכנית
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    //  כול עוד אין משהו שמפריע לתרד לרוץ תריץ את הקוד הבאה
                    while (!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time = LocalTime.now();
                                intiAlerts(time);
                            }
                        });
                }}
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        String URL = "https://www.hebcal.com/zmanim?cfg=json&geonameid=293397&date="+dtf.format(now);
        new FetchDataTask().execute(URL);


    }



    // פונקציה אשר יוצרת התראה בעזרת הסטרינג שאנו שולחים לה כפרמטר
    private void notification(String name)
    {
        //  אנו בודקים את גרסת האנדרואיד של המכשיר שהתוכנית רצה עליו
        //  אם מוחזר "אמת" אז פירוש הדבר שהמכשיר המריץ את האפליקציה יש אנדרואיד SDK 26 ומעלה
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //  אנו יוצרים ערוץ התראות עבור התוכנית שבעזרתה אנו נשלח את האתראות
            NotificationChannel channel = new NotificationChannel("myCh1","myCh1",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //  יוצרים את ההתראה שאנו רוצים להציג
        NotificationCompat.Builder builder = new NotificationCompat.Builder(alert.this,"myCh1")
                .setContentTitle("Tefillion Alert")
                .setContentText(name)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true);
        //  הפעלת האתראה עצמה
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1,builder.build());
    }


    // פונקציה אשר משווה את השעה הנוכחית לאובייקט ממחלקה "התראות" במערך של המחלקה
    private void intiAlerts(LocalTime time) {
        //  יצירת סטרינג לצורך השוואה
        String time_format = time.getHour() + ":" + time.getMinute();

        //  ריצה על מערך המתגים , אם המתג מופעל וגם השעה תואמת את השעה הנוכחתי
        //  תשלח את הפרטים לפונקציה הבאה ותכבה את המתג
        for(int i = 0; i < arr_of_switch.length ; i++){
            if(arr_of_switch[i].isChecked() && alert_classes[i].getTime().equals(time_format)) {
                notification(alert_classes[i].getName());
                arr_of_switch[i].setChecked(false);
            }
        }
    }
    private void intiStart() {
        arr_of_switch[0].setOnClickListener(this);
        arr_of_switch[1].setOnClickListener(this);
        arr_of_switch[2].setOnClickListener(this);
        arr_of_switch[3].setOnClickListener(this);
        arr_of_switch[4].setOnClickListener(this);
        arr_of_switch[5].setOnClickListener(this);
        arr_of_switch[6].setOnClickListener(this);


    }


    private void intiView() {
        //  מערך סטטי של הסוויצים
        arr_of_switch[0] = findViewById(R.id.tefillin);
        arr_of_switch[1]  = findViewById(R.id.Great_facilitator);
        arr_of_switch[2]  = findViewById(R.id.Small_facilitator);
        arr_of_switch[3]  = findViewById(R.id.Facilitator);
        arr_of_switch[4]  = findViewById(R.id.Sunrise);
        arr_of_switch[5]  = findViewById(R.id.rising_of_the_stars);
        arr_of_switch[6]  = findViewById(R.id.all_alerts);

        //  מערך סטטי של פרטי ההאתראות
        alert_classes[0] = new Alert_Class("זמן טלית ותפילין", "04:24");
        alert_classes[1] = new Alert_Class("מנחה גדולה", "13:16");
        alert_classes[2] = new Alert_Class("מנחה קטנה", "16:53");
        alert_classes[3] = new Alert_Class("פלג המנחה", "18:37");
        alert_classes[4] = new Alert_Class("זריחה", "05:43");
        alert_classes[5] = new Alert_Class("צאת הכוכבים", "20:06");
        alert_classes[6] = new Alert_Class("all_alerts", "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_alerts:
                // פונקציה אשר תתדליק את כול הסוויצים במערך
                Turn_on_all_alerts();
                // פונקציה אשר תשמור את המצב של הסוויץ
                Save_Switch(arr_of_switch[6], alert_classes[6].getName());
                break;
            case R.id.tefillin:
                Save_Switch(arr_of_switch[0], alert_classes[0].getName());
                break;
            case R.id.Great_facilitator:
                Save_Switch(arr_of_switch[1], alert_classes[1].getName());
                break;
            case R.id.Small_facilitator:
                Save_Switch(arr_of_switch[2], alert_classes[2].getName());
                break;
            case R.id.Facilitator:
                Save_Switch(arr_of_switch[3], alert_classes[3].getName());
                break;
            case R.id.Sunrise:
                Save_Switch(arr_of_switch[4], alert_classes[4].getName());
                break;
            case R.id.rising_of_the_stars:
                Save_Switch(arr_of_switch[5], alert_classes[5].getName());
                break;

        }
    }


    // עבור כול סוויץ שמצבו שמור בזיכרון אנו עוברים על הזיכרון, מקבלים את המצב שהוא שמור בזיכרון ומדליקים את הסווויצים ששמורים
    //  כדלוקים
    private void intiGetAll()
    {
        sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        for (int i =0 ; i< arr_of_switch.length; i++) {

            boolean flag = sharedPreferences.getBoolean(alert_classes[i].getName(), true);

            arr_of_switch[i].setChecked(flag);
            if ( arr_of_switch[i].isChecked()) {
                // When switch checked
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean(alert_classes[i].getName(), true);
                editor.apply();
                arr_of_switch[i].setChecked(true);
            } else {
                // When switch unchecked
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean(alert_classes[i].getName(), false);
                editor.apply();
                arr_of_switch[i].setChecked(false);
            }
        }
    }



    //  פונקציה אשר שומרת בזיכרון את מצב הסוויץ
    private void Save_Switch(Switch s, String name) {
        sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putBoolean(name, s.isChecked());
        editor.apply();

    }


    //  פונקציה אשר עוברת על מערך הסוויצים מדליקה או מכבה את כולם וקוראת לפונקציה אשר שומרת אותם בזיכרון
    private void Turn_on_all_alerts() {
        if(arr_of_switch[6].isChecked()){
            for (int i = 0; i < arr_of_switch.length - 1; i++)
            {
                arr_of_switch[i].setChecked(true);
                Save_Switch(arr_of_switch[i],alert_classes[i].getName());
            }
        }
        else{
            for (int i = 0; i < arr_of_switch.length - 1; i++) {
                arr_of_switch[i].setChecked(false);
                Save_Switch(arr_of_switch[i], alert_classes[i].getName());
            }
        }

    }

















    private class FetchDataTask extends AsyncTask<String, Void, String> {
        String data = null;
        DownloadURL downloadURL = new DownloadURL();
        @Override
        protected String doInBackground(String... params) {

            try {
                data = downloadURL.readUrl(params[0]);






                // for (String a : originalString){
                //      System.out.println(a);  System.out.println("a");
                // }

                System.out.println(time);

               } catch (IOException e) {
                e.printStackTrace();
            }
//            Log.d("TAG", "11111111111111111111111 : "+jsonArray);
            return data;

        }

        @Override
        protected void onPostExecute(String dataFetched) {
            //parse the JSON data and then display
            parseJSON(dataFetched);
        }


      

        }

        private void parseJSON(String data){
            JSONObject object = null;
            JSONObject jsonArray = null;
            try {
                object = new JSONObject(data);
                jsonArray = object.getJSONObject("times");
                JSONArray keys = jsonArray.names ();
                String[] originalString=null;
                List<String> times = new ArrayList<>();

                for (int i = 0; i < keys.length (); ++i) {
                    String key = keys.getString (i);
                    String value = jsonArray.getString (key);
                    originalString = value.split("T");
                    String time = originalString[1].substring(0,5);
                    times.add(time);
            }

//                yourJSonObject.getJSONObject("cover").getJSONObject("content").getJSONObject("article");

        Log.d("TAG", "11111111111111111111111 : "+times);

//            Log.d("App", "Erdfgdfgdfgdfg34555555555555555555 : " +data);
//            JSONArray jsonArray = null;
//             jsonArray = data.getJSONArray("results");
//            private String convertInputStreamToString(InputStream InputStream inputStream;
//            inputStream) throws IOException{
//                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
//                String line = "";
//                String result = "";
//                while((line = bufferedReader.readLine()) != null)
//                    result += line;
//
//                inputStream.close();
//
//            try{
//                JSONArray jsonMainNode = new JSONArray(data);
//
//                int jsonArrLength = jsonMainNode.length();
//
//                for(int i=0; i < jsonArrLength; i++) {
//                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                    String postTitle = jsonChildNode.getString("post_title");
//                    tutorialList.add(postTitle);
//                }
//
//                // Get ListView object from xml
//                listView = (ListView) findViewById(R.id.list);
//
//                // Define a new Adapter
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, tutorialList);
//
//                // Assign adapter to ListView
//                listView.setAdapter(adapter);

//            }catch(Exception e){
//                Log.i("App", "Error parsing data" +e.getMessage());
//
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        }
    }



































}

