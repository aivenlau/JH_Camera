package com.joyhonest.syma_lib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
/*
        JH_App.init(getApplicationContext(),null,null,null,null);
        JH_App.checkDeviceHasNavigationBar(this);
        findViewById(R.id.Start_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StartActivity.this, PlayActivity.class);
                startActivity(mainIntent);
            }
        });
*/
    }
}
