package com.crittercism;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

public class MainActivity extends Activity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                GoToWorkActivity();
            }
        }, 3000);
    }

    public void GoToWorkActivity(){
        Intent intent = new Intent();
        intent.setClass(this, WorkActivity.class);
        startActivity(intent);
        super.finish();
    }
}
