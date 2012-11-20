package com.example.gomotion;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HomeScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
}
