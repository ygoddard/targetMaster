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

package com.davemorrissey.labs.subscaleview.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.sample.R.id;
import com.davemorrissey.labs.subscaleview.sample.animation.AnimationActivity;
import com.davemorrissey.labs.subscaleview.sample.basicfeatures.BasicFeaturesActivity;
import com.davemorrissey.labs.subscaleview.sample.basicfeatures.ExcelWriter;
import com.davemorrissey.labs.subscaleview.sample.configuration.ConfigurationActivity;
import com.davemorrissey.labs.subscaleview.sample.eventhandling.EventHandlingActivity;
import com.davemorrissey.labs.subscaleview.sample.eventhandlingadvanced.AdvancedEventHandlingActivity;
import com.davemorrissey.labs.subscaleview.sample.extension.ExtensionActivity;
import com.davemorrissey.labs.subscaleview.sample.imagedisplay.ImageDisplayActivity;
import com.davemorrissey.labs.subscaleview.sample.viewpager.ViewPagerActivity;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class MainActivity extends Activity implements OnClickListener {

    private static final int REQUEST_CODE = 99;
    private Button scanButton;
    private Button cameraButton;
    private Button mediaButton;
    private ImageView scannedImageView;
    private Bitmap mBitmap;
    private Uri mUri;
    private boolean PicTaken = false;
    private String PROJ_NAME = "PROJ_NAME";
    private String FIRE_FILE_TYPE = ".xlsx";


    private FileDialog mFileDialog;
    private String mFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Project: "+PROJ_NAME);
        setContentView(R.layout.main);
        findViewById(id.animation).setOnClickListener(this);
        findViewById(id.libraryPic).setOnClickListener(this);
        findViewById(id.CameraPic).setOnClickListener(this);
        findViewById(id.self).setOnClickListener(this);
        findViewById(id.btnExcel).setOnClickListener(this);
        init();
        Toast toast =Toast.makeText(this, "select your target from camera or photo library and click on \"Mark Hits\"",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();


    }

    private void init() {
        cameraButton = (Button) findViewById(id.CameraPic);
        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        mediaButton = (Button) findViewById(id.libraryPic);
        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);


        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        mFileDialog = new FileDialog(this, mPath, FIRE_FILE_TYPE);
        mFileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                mFilePath = file.toString();
                //get project name without path
                String[] sArr = file.toString().split("_");
                sArr = sArr[sArr.length-1].split("\\.");
                setTitleProjName(sArr[0]);
            }
        });
        mFileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
          public void directorySelected(File directory) {
              Log.d(getClass().getName(), "selected dir " + directory.toString());
          }
        });
        mFileDialog.setSelectDirectoryOption(false);
//        mFileDialog.showDialog();


    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    protected void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //getContentResolver().delete(uri, null, null);
                scannedImageView.setImageBitmap(bitmap);
                scannedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mBitmap= bitmap;
                mUri= uri;
                PicTaken = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == id.animation) {
            if (PicTaken){
                EditText etSeries = (EditText) findViewById(id.etSeries);
                if (etSeries.getText().toString().equals("")){
                    Toast toast = Toast.makeText(MainActivity.this, "Please choose a series", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

//                EditText etProjName = (EditText)findViewById(id.projectName);
                String sProjName = "Project Name";
//                EditText etSeries = (EditText)findViewById(id.seriesNumber);
//                String sSeiries = etSeries.toString();
              String sSeiries = etSeries.getText().toString();
                Intent intent = new Intent(this, AnimationActivity.class);  //AnimationActivity
                intent.putExtra("UriSrc", mUri);
                intent.putExtra("projName", PROJ_NAME);
                intent.putExtra("seriesNum", sSeiries);
                intent.putExtra("filePath", mFilePath);
                startActivity(intent);
            } else{
                Toast toast =Toast.makeText(this, "first pick a picture", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        } else if (view.getId() == id.btnExcel){
            if (!mFilePath.matches("")){
                //TODO: check new ExcelWriter(mFilePath);
                File file = new File(mFilePath);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
                startActivity(intent);

            } else {
                // TODO: change it to default file
//                Toast.makeText(MainActivity.this, "first pick a file", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.target_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mFileDialog.showDialog();
//            mFileDialog.
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTitleProjName(String projName){
        PROJ_NAME= projName;
        getActionBar().setTitle("Project: "+PROJ_NAME);
    }

}// class ending
