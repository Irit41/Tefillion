package com.example.tefillion;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
 private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
        {
            HashMap<String, String> googlePlaceMap = new HashMap<>();
            String placeName = "--NA--";
            String vicinity=  "--NA--";




            try {
                if (!googlePlaceJson.isNull("name")) {
                    placeName = googlePlaceJson.getString("name");
                }
                if (!googlePlaceJson.isNull("vicinity")) {
                    vicinity = googlePlaceJson.getString("vicinity");
                    Log.i("DataParser","jsonobject ="+vicinity);

                }
                googlePlaceMap.put("place_name", placeName);
                googlePlaceMap.put("vicinity", vicinity);


            }
            catch (JSONException e) {
                e.printStackTrace();
            }


            return googlePlaceMap;

        }
        private List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
        {
            int count = jsonArray.length();
            List<HashMap<String, String>> placelist = new ArrayList<>();
            HashMap<String, String> placeMap = null;

            for(int i = 0; i<count;i++)
            {
                try {
                    placeMap = getPlace((JSONObject) jsonArray.get(i));
                    placelist.add(placeMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placelist;
        }


    public List<HashMap<String, String>> parse(JSONObject object) {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    return getPlaces(jsonArray);
    }

}
