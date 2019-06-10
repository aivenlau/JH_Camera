package com.joyhonest.syma_lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.joyhonest.jh_fpv.FpvActivity;
import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.PlayActivity;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        JH_App.init(getApplicationContext(),null,null,null,null);
        findViewById(R.id.Start_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StartActivity.this, PlayActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JH_App.checkDeviceHasNavigationBar(this);

    }
}
