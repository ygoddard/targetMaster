package com.scanner.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.scanner.demo.photoview.photoview.PhotoViewAttacher;

public class HitsActivity extends AppCompatActivity {

    private Uri mCapturdImage;

    final private int NUMBER_OF_SHOOTS= 100;
    public int mCounter = NUMBER_OF_SHOOTS;
    private boolean mStopAttachHits = false;
    private boolean mTargetClicked = false;

    private float mPrimStartTouchEventX = -1;
    private float mPrimStartTouchEventY = -1;
    private float mSecStartTouchEventX = -1;
    private float mSecStartTouchEventY = -1;
    private float mPrimSecStartTouchDistance = 0;

    int clickCount = 0;
    long startTime;
    long duration;
    static final int MAX_DURATION = 500;


    private float mXSum=0;
    private float mYSum=0;
    private float mXAvg;
    private float mYAvg;
    private ImageView mTargetView;

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    public List<int[]> dataList;
    public List<int[]> dataRedoList;

    public List<ImageView> viewList;
    public List<ImageView> viewRedoList;
    public View mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hits);

        insertCapturedImage();
        initValues();
        enableHitsAttach();
        //mAttacher = new PhotoViewAttacher(mImageView);


    }

    private void insertCapturedImage(){

        Intent i = getIntent();
        mCapturdImage= i.getParcelableExtra("UriSrc");
        ImageView iv = (ImageView)findViewById(R.id.imageView);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mCapturdImage);
            getContentResolver().delete(mCapturdImage, null, null);
            iv.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initValues(){
//        init image view
        mImageView= (ImageView) findViewById(R.id.imageView);
//        mImageView.getLayoutParams().height = mImageView.getMeasuredWidth();

//        init counter fab
        FloatingActionButton counterFab= (FloatingActionButton)findViewById(R.id.counterFab);

        FloatingActionButton doneFab = (FloatingActionButton) findViewById(R.id.doneFab);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "calculating Hits statistics", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(getApplicationContext(), DataActivity.class);
                view.getContext().startActivity(i);
            }
        });

        FloatingActionButton hitFab = (FloatingActionButton) findViewById(R.id.hitFab);
        hitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTargetClicked == false) {
                    mTargetClicked = true;
                    updateAvgHit();

                } else
                {
                    mTargetClicked = false;
                    updateAvgHit();
                }

            }
        });

        FloatingActionButton undoFab = (FloatingActionButton) findViewById(R.id.undoFab);
        undoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewList.size() == 0){
                    return;
                }
                //update counter
                mCounter++;
                updateCounterFab();
                //get iv and move it between lists
                ImageView iv = viewList.get(viewList.size()-1);
                viewRedoList.add(iv);
                viewList.remove(viewList.size()-1);
                ((ViewGroup) mView).removeView(iv);

                float x = dataList.get(dataList.size()-1)[0];
                float y = dataList.get(dataList.size()-1)[1];
                dataRedoList.add(dataList.get(dataList.size()-1));
                dataList.remove(dataList.size()-1);
                mXSum-=x;
                mYSum-=y;
                mXAvg = mXSum/viewList.size();
                mYAvg = mYSum/viewList.size();
                updateAvgHit();
            }
        });

        FloatingActionButton redoFab = (FloatingActionButton) findViewById(R.id.redoFab);
        redoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewRedoList.size() == 0){
                    return;
                }
                mCounter--;
                updateCounterFab();
                ImageView iv = viewRedoList.get(viewRedoList.size()-1);
                viewList.add(iv);
                viewRedoList.remove(viewRedoList.size()-1);
                ((ViewGroup) mView).addView(iv);

                float x = dataRedoList.get(dataList.size()-1)[0];
                float y = dataRedoList.get(dataList.size()-1)[1];
                dataList.add(dataRedoList.get(dataRedoList.size()-1));
                dataRedoList.remove(dataRedoList.size()-1);
                mXSum-=x;
                mYSum-=y;
                mXAvg = mXSum/viewList.size();
                mYAvg = mYSum/viewList.size();
                updateAvgHit();
            }
        });

        FloatingActionButton clrFab = (FloatingActionButton) findViewById(R.id.clearFab);
        clrFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton undo = (FloatingActionButton) findViewById(R.id.undoFab);
                while (viewList.size()>0){
                    undo.performClick();
                }
            }
        });

        dataList     =  new ArrayList<int[]>();
        dataRedoList =  new ArrayList<int[]>();
        viewList     =  new ArrayList<ImageView>();
        viewRedoList =  new ArrayList<ImageView>();
    }

    private void updateCounterFab(){
//        TextView tv = (TextView)findViewById(R.id.mCounterText);
//        tv.setText(String.valueOf(mCounter));
//        if (mCounter == 0){
//            mStopAttachHits= true;
//        }
    }

    private void enableHitsAttach(){
        final FrameLayout rr = (FrameLayout) findViewById(R.id.rr);
        rr.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return handleSingleTap(v, event);
//                switch(event.getAction() & MotionEvent.ACTION_MASK)
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        startTime = System.currentTimeMillis();
//                        clickCount++;
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        long time = System.currentTimeMillis() - startTime;
//                        duration=  duration + time;
//                        if(clickCount == 2)
//                        {
//                            if(duration<= MAX_DURATION)
//                            {
//                                // return handleDoubleTap(v, event);
//                                Toast.makeText(HitsActivity.this, "double click", Toast.LENGTH_SHORT).show();
//                            }
//                            clickCount = 0;
//                            duration = 0;
//                            break;
//                        }
//                        //Toast.makeText(HitsActivity.this, "single click", Toast.LENGTH_SHORT).show();
//                }
//                return true;
            }

        });
    }

    private void updateAvgHit(){
        if (mTargetClicked == false || viewList.size()==0){
            ((ViewGroup) mView).removeView(mTargetView);
            return;
        }
        ((ViewGroup) mView).removeView(mTargetView);
        int x = (int) mXAvg;
        int y = (int) mYAvg;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ImageView iv = new ImageView(getApplicationContext());
        Drawable d = getResources().getDrawable(R.drawable.daimond);
        int h = d.getIntrinsicHeight();
        int w = d.getIntrinsicWidth();
        lp.setMargins(x - h/2, y - w/2, 0, 0);
        iv.setLayoutParams(lp);
        iv.setImageDrawable(getResources().getDrawable(
                R.drawable.daimond));
        ((ViewGroup) mView).addView(iv);
        mTargetView = iv;
    }

    private void updateAvgValues(float x, float y, boolean add){
        if (add){
            mXAvg = (mXAvg+x)/viewList.size();
            mYAvg = (mYAvg+y)/viewList.size();
        } else {
            mXAvg = (mXAvg-x)/viewList.size();
            mYAvg = (mYAvg-y)/viewList.size();
        }
    }

    public float distance(MotionEvent event, int first, int second) {
        if (event.getPointerCount() >= 2) {
            final float x = event.getX(first) - event.getX(second);
            final float y = event.getY(first) - event.getY(second);

            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    private boolean isPinchGesture(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            final float distanceCurrent = distance(event, 0, 1);
            final float diffPrimX = mPrimStartTouchEventX - event.getX(0);
            final float diffPrimY = mPrimStartTouchEventY - event.getY(0);
            final float diffSecX = mSecStartTouchEventX - event.getX(1);
            final float diffSecY = mSecStartTouchEventY - event.getY(1);

            if (// if the distance between the two fingers has increased past
                // and the fingers are moving in opposing directions
                    (diffPrimY * diffSecY) <= 0
                            && (diffPrimX * diffSecX) <= 0) {
                return true;
            }
        }

        return false;
    }

    private boolean handleSingleTap(View v, MotionEvent event){
        int gest = event.getPointerCount();
        // ignore attaching a hit if its double tap/ pinch/ out of bounds
        if (mStopAttachHits){
            return false;
        }
        mView = v;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (clickCount != 1){
                startTime = System.currentTimeMillis();
                clickCount++;
            } else {
                long time = System.currentTimeMillis() - startTime;
                if (time <= MAX_DURATION){
                    findViewById(R.id.undoFab).performClick();
                    startTime = clickCount=0;
                    float Sx = event.getX();
                    float Sy = event.getY();
                    findViewById(R.id.rr).setScaleX(Sx);
                    findViewById(R.id.rr).setScaleY(Sy);
                    findViewById(R.id.rr).setScaleY(Sy);

                    return false;
                } else {
                    startTime = clickCount=0;

                }
            }

            int x = (int) event.getX();
            int y = (int) event.getY();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            ImageView iv = new ImageView(getApplicationContext());
            Drawable d = getResources().getDrawable(R.drawable.check4);
            int h = d.getIntrinsicHeight();
            int w = d.getIntrinsicWidth();
            lp.setMargins(x - h/2, y - w/2, 0, 0);
            iv.setLayoutParams(lp);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.check4));
            ((ViewGroup) v).addView(iv);
            mCounter--;
            updateCounterFab();
            int[] xyData = {x, y};
            dataList.add(xyData);
            viewList.add(iv);
            //updateAvgValues(x,y,true);
            mXSum+=x;
            mYSum+=y;
            mXAvg = mXSum/viewList.size();
            mYAvg = mYSum/viewList.size();
            updateAvgHit();
        }
        return false;
    }

    public void setLayoutScale(float Sx, float Sy)
    {
        findViewById(R.id.rr).setScaleX(Sx);
        findViewById(R.id.rr).setScaleY(Sy);

    }


}
