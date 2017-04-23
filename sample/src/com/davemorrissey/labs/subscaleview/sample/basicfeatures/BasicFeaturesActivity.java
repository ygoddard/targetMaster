/*
Copyright 2014 David Morrissey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.davemorrissey.labs.subscaleview.sample.basicfeatures;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.sample.R.id;
import com.davemorrissey.labs.subscaleview.sample.R.layout;
import com.davemorrissey.labs.subscaleview.sample.imagedisplay.decoders.RapidImageRegionDecoder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicFeaturesActivity extends Activity implements OnClickListener {
    private ArrayList<PointF> scaledMapPins;

    private static final String BUNDLE_POSITION = "position";
    private String projectName;
    private String serieNumber;
    private String filePath;


//    private int position;

//    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.notes_activity);
        getActionBar().setTitle("Data");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(this, "enter your data",
                Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectName");
        serieNumber = intent.getStringExtra("seriesNumber");
        filePath = intent.getStringExtra("filePath");

        getActionBar().setSubtitle(projectName+":  #"+serieNumber);
        scaledMapPins= new ArrayList<PointF>();
        scaledMapPins= intent.getParcelableArrayListExtra("ScaledPoints");
        String s = new String();
        s = String.format("%1$-20s %2$-15s %3$10s", "No.","TRV [cm]", "ELV [cm]") + "\n\n";
        DecimalFormat df = new DecimalFormat("#.#");
        // i=0 is the center so we are not caulating it
        for (int i=1; i<scaledMapPins.size(); i++){
            String x = df.format(scaledMapPins.get(i).x);
            String y = df.format(scaledMapPins.get(i).y);
            String value = String.format("%1$-20s %2$-10s %3$10s",i, x, y);
            s+= value +"\n";
        }
        TextView tv = (TextView)findViewById(id.scaledHitsText);
        tv.setText(s);


        NumberPicker np = (NumberPicker) findViewById(id.numberPicker);

        np.setMinValue(0);
        np.setMaxValue(40);
        np.setValue(20);
        np.setWrapSelectorWheel(false);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                TextView tv = (TextView)findViewById(id.scaledHitsText);
                NumberPicker np = (NumberPicker) findViewById(id.numberPicker);
                tv.setTextSize(np.getValue());
            }
        });

    }

//        findViewById(id.next).setOnClickListener(this);
//        findViewById(id.previous).setOnClickListener(this);
//        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_POSITION)) {
//            position = savedInstanceState.getInt(BUNDLE_POSITION);
//        }
//        notes = Arrays.asList(
//                new Note("Pinch to zoom", "Use a two finger pinch to zoom in and out. The zoom is centred on the pinch gesture, and you can pan at the same time."),
//                new Note("Quick scale", "Double tap and swipe up or down to zoom in or out. The zoom is centred where you tapped."),
//                new Note("Drag", "Use one finger to drag the image around."),
//                new Note("Fling", "If you drag quickly and let go, fling momentum keeps the image moving."),
//                new Note("Double tap", "Double tap the image to zoom in to that spot. Double tap again to zoom out.")
//        );
//
//        initialiseImage();
//        updateNotes();
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(BUNDLE_POSITION, position);
//    }

    @Override
    public void onClick(View view) {
        if (view.getId() == id.next) {
//            position++;
//            updateNotes();
        } else if (view.getId() == id.previous) {
//            position--;
//            updateNotes();
        }
    }

    @Override
    public void onBackPressed()
    {
        scaledMapPins.clear();
        super.onBackPressed();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        finish();
//        return true;
//    }

//    private void initialiseImage() {
//        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);
//        imageView.setImage(ImageSource.asset("squirrel.jpg"));
//    }

//    private void updateNotes() {
//        if (position > notes.size() - 1) {
//            return;
//        }
//        getActionBar().setSubtitle(notes.get(position).subtitle);
//        ((TextView)findViewById(id.note)).setText(notes.get(position).text);
//        findViewById(id.next).setVisibility(position >= notes.size() - 1 ? View.INVISIBLE : View.VISIBLE);
//        findViewById(id.previous).setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
//    }

//    private static final class Note {
//        private final String text;
//        private final String subtitle;
//        private Note(String subtitle, String text) {
//            this.subtitle = subtitle;
//            this.text = text;
//        }
//    }

}
