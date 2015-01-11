package com.aaron.mapsapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/**
 * Created by wuyuan on 1/10/2015.
 */
public class Slug extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        Thread timer = new Thread() {
            public void run(){
                try{
                    sleep(1100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent openSlug = new Intent("com.aaron.mapsap.MAINACTIVITY");
                    startActivity(openSlug);
                }
            }
        };
        timer.start();
    }
}

