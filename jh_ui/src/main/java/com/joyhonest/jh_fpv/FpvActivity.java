package com.joyhonest.jh_fpv;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.MyControl;
import com.joyhonest.jh_ui.PermissionAsker;
import com.joyhonest.jh_ui.PlayActivity;
import com.joyhonest.jh_ui.R;
import com.joyhonest.wifination.JH_GLSurfaceView;
import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class FpvActivity extends AppCompatActivity implements View.OnClickListener{

    private PermissionAsker mAsker;

    private Button  btn_photo;
    private Button  btn_video;
    private Button  btn_brow;
    private Button  btn_wifi;
    private Button  btn_back;
    private TextView  RectimeView;


    private LinearLayout  flashView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpv);
        wifination.appContext = getApplicationContext();
        wifination.naSetVrBackground(false);
        JH_App.F_InitMusic();



        mAsker = new PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                F_Init();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FpvActivity.this, "The necessary permission denied, the application exit",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }).askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        RssiHander.post(RssiRunable);


    }


    private HandlerThread thread1;
    private Handler openHandler;
    private Runnable openRunnable = new Runnable() {
        @Override
        public void run() {
            {

                JH_App.nSdStatus = 0;
                JH_App.F_OpenStream();

            }
        }
    };


    Handler RssiHander = new Handler();
    Runnable RssiRunable = new Runnable() {
        @Override
        public void run() {
            int nrssi = JH_App.F_GetWifiRssi();
            if(nrssi<0)
                nrssi = 0;
            if(nrssi>4)
                nrssi = 4;

            if(nrssi==1)
            {
                btn_wifi.setBackgroundResource(R.mipmap.wifi_01_jh_fpv);
            }
            else if(nrssi==2)
            {
                btn_wifi.setBackgroundResource(R.mipmap.wifi_02_jh_fpv);
            }
            else if(nrssi==3)
            {
                btn_wifi.setBackgroundResource(R.mipmap.wifi_03_jh_fpv);
            }
            else if(nrssi==4)
            {
                btn_wifi.setBackgroundResource(R.mipmap.wifi_04_jh_fpv);
            }
            else if(nrssi==0)
            {
                btn_wifi.setBackgroundResource(R.mipmap.wifi_00_jh_fpv);
            }

            //wifi_00_jh_fpv



                /*
                if (flyPlayFragment != null) {
                    F_DispRssi(flyPlayFragment.WifiSingle, nrssi);
                }
                if (flyPathFragment != null) {
                    F_DispRssi(flyPathFragment.WifiSingle, nrssi);
                }
                */

            RssiHander.postDelayed(this, 1000);

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        JH_App.checkDeviceHasNavigationBar(this);
    }


    private  void TackPhoto()
    {
        if(!isConnected())
            return;
        final String str = JH_App.F_GetSaveName(true);
        flashView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flashView.setVisibility(View.GONE);
            }
        }, 120);
        wifination.naSnapPhoto(str, wifination.TYPE_ONLY_PHONE);
        JH_App.F_PlayPhoto();

    }


    private  void StartOrStopRecord()
    {
        if(isConnected())
        {
            if((JH_App.nSdStatus & JH_App.LocalRecording) ==0)
            {
                String str = JH_App.F_GetSaveName(false);
                wifination.naStartRecord(str, wifination.TYPE_ONLY_PHONE);
            }
            else
            {
                wifination.naStopRecord_All();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v== btn_back)
        {
            finish();
        }
        if(v == btn_photo)
        {
            TackPhoto();
        }
        if(v==btn_video)
        {
            StartOrStopRecord();
        }
        if(v ==btn_brow)
        {
            Intent mainIntent = new Intent(FpvActivity.this, BrowActivity.class);
            startActivity(mainIntent);
        }


    }





        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Exit();
        RssiHander.removeCallbacksAndMessages(null);
        openHandler.removeCallbacksAndMessages(null);
        thread1.quit();
        EventBus.getDefault().unregister(this);

    }



    private  void Exit()
    {
        wifination.naStopRecord_All();
        wifination.naStop();

    }

    private void F_Init() {
        MyControl.bFlyType = false;
        wifination.F_AdjBackGround(this, R.mipmap.loginbackground_jh);
        JH_App.checkDeviceHasNavigationBar(this);
        JH_App.F_CreateLocalFpvDefalutDir();
        JH_App.F_Clear_not_videoFiles();



        flashView = (LinearLayout)findViewById(R.id.flashView);
        flashView.setVisibility(View.GONE);


        RectimeView = (TextView)findViewById(R.id.RectimeView);
        RectimeView.setVisibility(View.INVISIBLE);

        btn_photo = (Button)findViewById(R.id.btn_photp);
        btn_video = (Button)findViewById(R.id.btn_video);
        btn_brow = (Button)findViewById(R.id.btn_brow);
        btn_wifi = (Button)findViewById(R.id.btn_wifi);
        btn_back = (Button)findViewById(R.id.btn_back);

        btn_photo.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_brow.setOnClickListener(this);
        btn_wifi.setOnClickListener(this);
        btn_back.setOnClickListener(this);


        thread1 = new HandlerThread("MyHandlerThread_FPV");
        thread1.start(); //创建一个HandlerThread并启动它
        openHandler = new Handler(thread1.getLooper());
        openHandler.postDelayed(openRunnable,100);
        EventBus.getDefault().register(this);
        //RssiHander.postDelayed(RssiRunable, 100);


        /*
        glSurfaceView = (JH_GLSurfaceView) findViewById(R.id.surfaceView_gl);
        mFragmentMan = getSupportFragmentManager();//  getFragmentManager();// getSupportFragmentManager();
        Fragment_Layout = (RelativeLayout) findViewById(R.id.Fragment_Layout);
        RssiHander = new Handler();
        RssiRunable = new Runnable() {
            @Override
            public void run() {
                int nrssi = JH_App.F_GetWifiRssi();
                {
                    if (main_fragment != null) {
                        F_DispRssi(main_fragment.imageViewRssi, nrssi);
                    }
                    if (path_fragment != null) {
                        F_DispRssi(path_fragment.imageViewRssi, nrssi);
                    }
                }
                RssiHander.postDelayed(this, 1000);
            }
        };


        // surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        //surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.addCallback(this);

        thread1 = new HandlerThread("MyHandlerThread");
        thread1.start(); //创建一个HandlerThread并启动它
        openHandler = new Handler(thread1.getLooper());

        F_InitFragment();

        //openHandler.postDelayed(initmusic,100);

        EventBus.getDefault().register(this);
        RssiHander.postDelayed(RssiRunable, 100);
        */
    }


    private  boolean  isConnected()
    {
        return ((JH_App.nSdStatus & JH_App.Status_Connected ) !=0);
    }






    private Handler  DispRecTimeHander = new Handler();
    private Runnable DispRecTimeRunnable = new Runnable() {
        @Override
        public void run() {
              if((JH_App.nSdStatus & JH_App.LocalRecording) !=0)
              {
                    int rec = wifination.naGetRecordTime()/1000;
                    int nMin = rec/60;
                    int nSec = rec %60;
                    String str = String.format("%02d:%02d",nMin,nSec);
                    RectimeView.setText(str);
                  DispRecTimeHander.postDelayed(this,250);
              }
        }
    };

    private void DispRecTimeView(boolean b)
    {
        if(b)
        {
            RectimeView.setText("00:00");
            RectimeView.setVisibility(View.VISIBLE);
            DispRecTimeHander.removeCallbacksAndMessages(null);
            DispRecTimeHander.post(DispRecTimeRunnable);

        }
        else
        {
            DispRecTimeHander.removeCallbacksAndMessages(null);
            RectimeView.setVisibility(View.INVISIBLE);
            RectimeView.setText("00:00");
        }
    }

    @Subscriber(tag = "SDStatus_Changed")
    private void _SDStatus_Changed(Integer nStatus) {
        int backStatus = JH_App.nSdStatus;
        if ((nStatus & 0x01) != 0) {
            JH_App.nSdStatus |= JH_App.Status_Connected;
        } else {
            JH_App.nSdStatus &= ((JH_App.Status_Connected ^ 0xFFFF) & 0xFFFF);
        }


        if ((nStatus & JH_App.LocalRecording) != 0)
        {

            if((JH_App.nSdStatus & JH_App.LocalRecording) == 0)
            {
                JH_App.nSdStatus |= JH_App.LocalRecording;   //Recording . . .
                DispRecTimeView(true);
            }
        } else {
            JH_App.nSdStatus &= ((JH_App.LocalRecording ^ 0xFFFF) & 0xFFFF);   //StopRecording ....
            DispRecTimeView(false);
        }

    }

    @Subscriber(tag = "SavePhotoOK")
    private void SavePhotoOK(String Sn) {
        if (Sn.length() < 5) {
            return;
        }
        String sType = Sn.substring(0, 2);
        String sName = Sn.substring(2, Sn.length());
        int nPhoto = Integer.parseInt(sType);
        if (nPhoto == 0) {
            JH_App.F_Save2ToGallery(sName, true);
        } else {
            JH_App.F_Save2ToGallery(sName, false);
        }
    }
    @Subscriber(tag = "key_Press")
    private void key_Press(Integer nKey) {
        Log.e("TAG","Key = "+nKey);
    }




}
