package com.joyhonest.jh_fpv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class BrowActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_Exit_Brow;
    private Button btn_Photos;
    private Button btn_Videos;
    private WaitView WaitView;

    private TextView TotalPhotosView;
    private TextView TotalVideosView;
    int nTotalPhotos = 0;
    int nTotalVideos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brow);
        btn_Exit_Brow =(Button)findViewById(R.id.btn_Exit_Brow);
        btn_Photos = (Button)findViewById(R.id.btn_Photos);
        btn_Videos = (Button)findViewById(R.id.btn_Videos);
        WaitView = (WaitView)findViewById(R.id.WaitView);
        TotalPhotosView = (TextView)findViewById(R.id.TotalPhotosView);
        TotalVideosView = (TextView)findViewById(R.id.TotalVideosView);

        WaitView.setVisibility(View.INVISIBLE);

        btn_Exit_Brow.setOnClickListener(this);
        btn_Photos.setOnClickListener(this);
        btn_Videos.setOnClickListener(this);
        EventBus.getDefault().register(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _Init_Theard init = new _Init_Theard();
        init.start();
        JH_App.checkDeviceHasNavigationBar(this);

    }



    @Override
    public void onClick(View v) {
        if(v == btn_Exit_Brow)
        {
            finish();
        }
        if(v == btn_Photos)
        {
            if(nTotalPhotos>0) {
                JH_App.bBrowPhoto = true;
                Intent mainIntent = new Intent(BrowActivity.this, grid_fpv.class);
                startActivity(mainIntent);
            }
        }
        if(v == btn_Videos)
        {
            if(nTotalVideos>0) {
                JH_App.bBrowPhoto = false;
                Intent mainIntent = new Intent(BrowActivity.this, grid_fpv.class);
                startActivity(mainIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    private class _Init_Theard extends Thread {
        @Override
        public void run() {
            F_Init();
        }
    }

    private String getFileType(String filename){
        if(filename==null)
            return null;
        filename.toLowerCase();
        int pos = filename.lastIndexOf(".");
        if(pos == -1){
            return null;
        }
        return filename.substring(pos+1);
    }


    private int F_GetAllPhotoLocalCount() {
        String sPath = JH_App.sLocalPhoto;
        int nCount = 0;
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();
            String sExt;
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                sExt = getFileType(f.getPath());
                if(sExt.equalsIgnoreCase("jpg") ||
                    sExt.equalsIgnoreCase("png") ||
                        sExt.equalsIgnoreCase("bmp"))
                {
                     nCount++;
                }

            }
        }
        return nCount;

    }

    private int F_GetAllVideoLocalCount() {
        String sPath = JH_App.sLocalVideo;
        int nCount = 0;
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();
            String sExt;
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                sExt = getFileType(f.getPath());
                if(sExt.equalsIgnoreCase("mp4"))
                {
                    nCount++;
                }

            }
        }
        return nCount;
    }




    private  void F_Init()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WaitView.setVisibility(View.VISIBLE);
            }
        });

        nTotalPhotos = F_GetAllPhotoLocalCount();
        nTotalVideos = F_GetAllVideoLocalCount();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WaitView.setVisibility(View.INVISIBLE);
                TotalPhotosView.setText(""+nTotalPhotos);
                TotalVideosView.setText(""+nTotalVideos);
            }
        });
    }
}
