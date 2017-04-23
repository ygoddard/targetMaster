package com.scanner.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataActivity extends AppCompatActivity {

    private static final String[] PROJECTS = new String[] {
            "UT_Spain Demo", "UT_Botawana", "Land400", "UT_Phill"
    };

    private static final String[] FIRE_TYPE = new String[] {
            "Single", "Burst"
    };

    private static final String[] TURRET_MODE = new String[] {
            "GTS", "STG", "ATT", "CTG", "POW", "MECH"
    };

    private static final String[] AMMUNITION = new String[] {
            "Nemmo TP-T 30mm", "TP-T 25mm", "7.62mm"
    };

    private static final String[] AIMING_CAMERA = new String[] {
            "G.Day", "G. Night", "C. Day", "C. Night", "G. Day + Night", "C. Day + Night", "LRF"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        InitValues();
    }

    private void
    InitValues()
    {
        // Init date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        EditText dateTxt = (EditText) findViewById(R.id.dateTxt);
        dateTxt.setText(currentDate);

        //Init time
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        EditText timeTxt = (EditText) findViewById(R.id.TimeTxt);
        timeTxt.setText(hourofday + ":" + minute);

        //Init project name options
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, PROJECTS);
        final AutoCompleteTextView textView0 = (AutoCompleteTextView) findViewById(R.id.ProjectTxt);
        textView0.setAdapter(adapter);
        textView0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView0.showDropDown();
                return false;
            }
        });


        //Init Fire type options
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, FIRE_TYPE);
        final AutoCompleteTextView textView1 = (AutoCompleteTextView) findViewById(R.id.FireTypeTxt);
        textView1.setAdapter(adapter);
        textView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView1.showDropDown();
                return false;
            }
        });

        //Init Turret mode options
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TURRET_MODE);
        final AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.TurretTxt);
        textView2.setAdapter(adapter);
        textView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView2.showDropDown();
                return false;
            }
        });

        //Init Aiming Camera options
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, AIMING_CAMERA);
        final AutoCompleteTextView textView3 = (AutoCompleteTextView) findViewById(R.id.AimingTxt);
        textView3.setAdapter(adapter);
        textView3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView3.showDropDown();
                return false;
            }
        });

        //Init Ammunition options
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, AMMUNITION);
        final AutoCompleteTextView textView4 = (AutoCompleteTextView) findViewById(R.id.AmmunitionTxt);
        textView4.setAdapter(adapter);
        textView4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView4.showDropDown();
                return false;
            }
        });
    }


}// class DataActivity
