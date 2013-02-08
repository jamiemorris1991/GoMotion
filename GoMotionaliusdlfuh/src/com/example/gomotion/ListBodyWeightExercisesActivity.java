package com.example.gomotion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.gomotion.BodyWeightExcercise.BodyweightType;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
public class ListBodyWeightExercisesActivity extends ListActivity 
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        OfflineDatabase db = new OfflineDatabase(this);

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        
        // Add each exercise as a map with the key being first line and value the second
        for(BodyWeightExcercise exercise : db.getAllBodyWeightExercises())
        {
        	Map<String, String> datum = new HashMap<String, String>(2);
        	
        	char[] charArray = exercise.getType().toString().toLowerCase().toCharArray();
        	charArray[0] = Character.toUpperCase(charArray[0]);
        	String title = new String(charArray);
        	
        	datum.put("First Line", title);
        	
        	String date = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(new Date(exercise.getTimeStamp()));
        	datum.put("Second Line", "Completed: " + date + "\nSets: " + exercise.getSets() + " Reps: " + exercise.getReps());

        	data.add(datum);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
        			new String[]{ "First Line", "Second Line"}, new int[] {android.R.id.text1, android.R.id.text2 });
        
        setListAdapter(adapter);
    }
}
