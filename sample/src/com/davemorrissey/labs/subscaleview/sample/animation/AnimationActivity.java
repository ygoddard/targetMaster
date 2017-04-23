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

package com.davemorrissey.labs.subscaleview.sample.animation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.sample.R.id;
import com.davemorrissey.labs.subscaleview.sample.R.layout;
import com.davemorrissey.labs.subscaleview.sample.basicfeatures.BasicFeaturesActivity;
import com.davemorrissey.labs.subscaleview.sample.extension.views.PinView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.sqrt;

public class AnimationActivity extends Activity implements OnClickListener {

    private static final String BUNDLE_POSITION = "position";
    private static final int LIMIT_OF_HITS = 20;
    private String projectName;
    private String seriesNumber;
    private String filePath;

    private boolean nextClicked = false;

    private int position;
    private int pinsCounter=0;
    private PinView pinView;
    private float rotationDegree = 0;


    private boolean centerSelected = false;
    private boolean centerAttached = false;
    private boolean doneRotateAndCenter  = false;
    private PointF pfCenterPt;

    private List<Note> notes;
    private List<PinView> pins;
    private ArrayList<PointF> MapPins;
    private ArrayList<PointF> CenterPins;
    private ArrayList<PointF> scaledMapPins;

    private enum markMode {MARK_CENTER, MARK_HITS};
    private markMode MarkMode = markMode.MARK_CENTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Mark Hits");
        setContentView(layout.animation_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(id.next).setOnClickListener(this);
        findViewById(id.previous).setOnClickListener(this);
        //findViewById(id.avgOfHits).setOnClickListener(this);
        findViewById(id.rotateLeft).setOnClickListener(this);
        findViewById(id.rotateRight).setOnClickListener(this);
        findViewById(id.setCenter).setOnClickListener(this);
        findViewById(id.centerDoneBtn).setOnClickListener(this);
//        findViewById(id.markHit).


        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_POSITION)) {
            position = savedInstanceState.getInt(BUNDLE_POSITION);
        }
        notes = Arrays.asList(
                new Note("", "Tap the play button. The image will scale and zoom to a random point, shown by a marker."),
                new Note("Limited pan", "If the target point is near the edge of the image, it will be moved as near to the center as possible."),
                new Note("Unlimited pan", "With unlimited or center-limited pan, the target point can always be animated to the center."),
                new Note("Customisation", "Duration and easing are configurable. You can also make animations non-interruptible.")
        );
        pinView= (PinView)(findViewById(id.imageView));
        MapPins= new ArrayList<PointF>();
        CenterPins= new ArrayList<PointF>();
        scaledMapPins = new ArrayList<PointF>();
        CenterPins.add(new PointF(0,0));
//        CenterPins.add(new PointF(-1,-1));  //fake value for later
        initialiseImage();
        updateNotes(0);

        Toast.makeText(this, "rotate the target, then click on the pen and mark the target's center. when you finish click done",Toast.LENGTH_LONG).show();

        findViewById(id.rotateLeft).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rotationDegree-=90;
                final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);
                imageView.setRotation(rotationDegree);
                return true;
            }
        });

        findViewById(id.rotateRight).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rotationDegree+=90;
                final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);
                imageView.setRotation(rotationDegree);
                return true;
            }
        });

        ToggleButton markHit = (ToggleButton) findViewById(id.markHit);
        markHit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    centerSelected = true;
                    if (!centerAttached){
                        MapPins.add(new PointF(999,999));
                    }
                    ToggleButton deleteHit = (ToggleButton) findViewById(id.deleteHit);
                    deleteHit.setChecked(false);
                }
            }
        });

        ToggleButton deleteHit = (ToggleButton) findViewById(id.deleteHit);
        deleteHit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ToggleButton markHit = (ToggleButton) findViewById(id.markHit);
                    markHit.setChecked(false);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_POSITION, position);
    }



    @Override
    public void onClick(View view) {
        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);
        final EditText  elvText = (EditText)findViewById(id.elvTxt);
        final EditText  trvText = (EditText)findViewById(id.trvTxt);
        if (view.getId() == id.next) {
            if (elvText.getText().toString().matches("") || trvText.getText().toString().matches("")){
                Toast.makeText(this, "please enter target height and width size first", Toast.LENGTH_SHORT).show();
                return;
            }
            double targetElvSize = Double.parseDouble(elvText.getText().toString());
            double targetTrvSize = Double.parseDouble(trvText.getText().toString());
            scaleHitsToCenter(targetElvSize,targetTrvSize);
            Intent intent = new Intent(this, BasicFeaturesActivity.class);
            intent.putParcelableArrayListExtra("ScaledPoints", scaledMapPins);
            intent.putExtra("projectName" ,projectName);
            intent.putExtra("seriesNumber" ,seriesNumber);
            intent.putExtra("filePath" ,filePath);
            startActivity(intent);
        } else if (view.getId() == id.rotateLeft) {
            rotationDegree-=0.5;
            imageView.setRotation(rotationDegree);
        } else if (view.getId() == id.rotateRight) {
            rotationDegree+=0.5;
            imageView.setRotation(rotationDegree);
        } else if (view.getId() == id.setCenter) {
            centerSelected = true;
            MapPins.add(new PointF(999,999));
        } else if (view.getId() == id.centerDoneBtn) {
            if (!centerSelected){
                Toast.makeText(AnimationActivity.this,"first select a center",Toast.LENGTH_SHORT).show();
                return;
            }
            MarkMode = markMode.MARK_HITS;
            if (!centerAttached){return;}
            doneRotateAndCenter = true;
            clearAllPins();
            MapPins.add(new PointF(999,999));
            MapPins.add(pfCenterPt);
            findViewById(id.rotateLeft).setVisibility(View.INVISIBLE);
            findViewById(id.rotateRight).setVisibility(View.INVISIBLE);
            findViewById(id.setCenter).setVisibility(View.INVISIBLE);
            findViewById(id.centerDoneBtn).setVisibility(View.INVISIBLE);
            findViewById(id.next).setVisibility(View.VISIBLE);
            findViewById(id.markHit).setVisibility(View.VISIBLE);
            findViewById(id.markHit).performClick();
            findViewById(id.deleteHit).setVisibility(View.VISIBLE);

            Toast.makeText(this, "mark the hits over the target, you can find the average and clear all marks, when finish click done button",
                    Toast.LENGTH_LONG).show();

        }

    }

    public void showPin(float x, float y){
        PinView pinView = (PinView)findViewById(id.imageView);
        Random random = new Random();
        if (pinView.isReady()) {
            float maxScale = pinView.getMaxScale();
            float minScale = pinView.getMinScale();
            float scale = (random.nextFloat() * (maxScale - minScale)) + minScale;
            PointF center = new PointF(x, y);
            pinView.setPin(center);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void initialiseImage() {
        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                if (!centerAttached && MarkMode == markMode.MARK_HITS){ return true;}
                ToggleButton markHit =   (ToggleButton) findViewById(id.markHit);
                ToggleButton deleteHit = (ToggleButton) findViewById(id.deleteHit);
//                here we on mark mode
                if (imageView.isReady() && ( MarkMode==markMode.MARK_HITS && markHit.isChecked() ||
                                        MarkMode==markMode.MARK_CENTER )) {
                    PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                    Toast.makeText(getApplicationContext(), "Single tap: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
//                    showPin(sCoord.x,sCoord.y);
                    if (MarkMode==markMode.MARK_CENTER){
                        CenterPins.remove(0);
                        CenterPins.add(new PointF(0,0));
                        CenterPins.add(sCoord);
                        pinView.setPins(CenterPins);
                        centerSelected = true;
                        pfCenterPt=sCoord;
                    } else {
                        MapPins.add(0,new PointF(999,999));
                        MapPins.add(sCoord);
                        pinView.setPins(MapPins);
                    }
                    pinView.post(new Runnable(){
                        public void run(){
                            pinView.getRootView().postInvalidate();
                        }
                    });
                    updateNotes(++pinsCounter);
                    centerAttached =true;
                } else if(imageView.isReady() && deleteHit.isChecked()){
                    // in case we want to delete points
                    if (pinsCounter == 0){ return true;}
                    PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
//                    Toast.makeText(getApplicationContext(), "Single tap: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
                    //find the nearest point to delete
                    double minDist = sqrt(Math.pow(sCoord.x-MapPins.get(1).x,2)+Math.pow(sCoord.y-MapPins.get(1).y,2));
                    int minIdx = 1;
                    //i=0 is the center point so dont check for it
                    for (int i=1; i<MapPins.size(); i++) {
                        double currDist = sqrt(Math.pow(sCoord.x - MapPins.get(i).x, 2) + Math.pow(sCoord.y - MapPins.get(i).y, 2));
                        if (minDist > currDist) {
                            minDist = currDist;
                            minIdx = i;
                        }
                    }
                    MapPins.remove(minIdx);
                    MapPins.add(0, new PointF(999,999));
                    pinView.setPins(MapPins);

                    pinView.post(new Runnable(){
                        public void run(){
                            pinView.getRootView().postInvalidate();
                        }
                    });
                    updateNotes(--pinsCounter);
                    centerAttached =true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Single tap: Image not ready", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
//
        });
        Intent intent = getIntent();
        Uri ScannedImage= intent.getParcelableExtra("UriSrc");
        projectName  = intent.getStringExtra("projName");
        seriesNumber = intent.getStringExtra("seriesNum");
        filePath = intent.getStringExtra("filePath");
        getActionBar().setTitle("Project: "+projectName+" / "+seriesNumber);
        getActionBar().setSubtitle("Series: "+seriesNumber);

        imageView.setImage(ImageSource.uri(ScannedImage));
        imageView.setMinimumDpi(25);
//        imageView.setImage(ImageSource.asset("target_test.png"));  //for testing
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void updateNotes(int nHit) {


        int limitOfHits = LIMIT_OF_HITS;
        getActionBar().setSubtitle(notes.get(position).subtitle);
        ((TextView)findViewById(id.note)).setText("marked: "+nHit);
        findViewById(id.next).setVisibility(position >= notes.size() - 1 ? View.INVISIBLE : View.VISIBLE);
        findViewById(id.previous).setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);
        if (position == 2) {
            imageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
        } else {
            imageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE);
        }

    }

    private static final class Note {
        private final String text;
        private final String subtitle;
        private Note(String subtitle, String text) {
            this.subtitle = subtitle;
            this.text = text;
        }
    }

    private void clearAllPins(){
        if (pinsCounter == 0){
            return;
        }
        MapPins.clear();
        pinView.setPins(MapPins);
        pinView.post(new Runnable(){
            public void run(){
                pinView.getRootView().postInvalidate();
            }
        });
        pinsCounter=0;
        updateNotes(pinsCounter);
    }

    private void undoLastAction(){
        if (pinsCounter==0){
            return;
        }
        MapPins.remove(MapPins.size()-1);
        pinView.setPins(MapPins);
        pinView.post(new Runnable(){
            public void run(){
                pinView.getRootView().postInvalidate();
            }
        });
        updateNotes(--pinsCounter);
    }


    private void showAvgOfHits(boolean showAvg){
        if (pinsCounter==0){
            return;
        }
        if (showAvg){
            float x = 0,y = 0;
            for (PointF pin: MapPins){
                x+= pin.x;
                y+=pin.y;
            }
            x= x/MapPins.size();
            y= y/MapPins.size();
            PointF AvgPt = new PointF(x,y);
            ArrayList<PointF> AvgMapPins = new ArrayList<PointF>();
            AvgMapPins.add(AvgPt);
            AvgMapPins.add(0,new PointF(0,0));
            pinView.setPins(AvgMapPins);
            pinView.post(new Runnable(){
                public void run(){
                    pinView.getRootView().postInvalidate();
                }
            });
        } else {
            pinView.setPins(MapPins);
            pinView.post(new Runnable(){
                public void run(){
                    pinView.getRootView().postInvalidate();
                }
            });
        }
    }

    void scaleHitsToCenter(double targetElvSize, double targetTrvSize){
        scaledMapPins.clear();
        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(id.imageView);
        int imageWidth = imageView.getSWidth();
        int imageHight = imageView.getSHeight();
//        imageView.
        for(int i=0; i<MapPins.size(); i++){
            float newX= (float)((MapPins.get(i).x- pfCenterPt.x)*targetTrvSize/imageWidth)*100;
            float newY= (float)((pfCenterPt.y-MapPins.get(i).y)*targetElvSize/imageHight)*100;

            PointF tempPF = new PointF(newX,newY);

            scaledMapPins.add(tempPF);
        }
    }


}

