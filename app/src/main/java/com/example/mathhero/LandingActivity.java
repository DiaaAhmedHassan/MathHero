package com.example.mathhero;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class LandingActivity extends AppCompatActivity {

   public static ImageButton btnStart, btnBackground, btnSetting;
   public static TextView totalScore, highScore;
   int total;
    int highSc;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        btnStart = findViewById(R.id.start_btn);
        btnBackground = findViewById(R.id.background_btn);
        btnSetting = findViewById(R.id.settings_btn);
        totalScore = findViewById(R.id.total_tv);
        highScore = findViewById(R.id.high_tv);

        //adds
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //..


        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        total = prefs.getInt("total score",0);
        highSc = prefs.getInt("high score",0);
//        totalScore.setText("Total score: "+total);
//        highScore.setText("Last high score: "+highSc);

        SharedPreferences langPrefs = getSharedPreferences("langPrefs",MODE_PRIVATE);
        totalScore.setText(""+langPrefs.getString("Total","total score: ")+total);
        highScore.setText(""+langPrefs.getString("High","Last high score: ")+highSc);
        btnStart.setBackgroundResource(langPrefs.getInt("Start",R.drawable.start));
        btnBackground.setBackgroundResource(langPrefs.getInt("Background",R.drawable.environment));
        btnSetting.setBackgroundResource(langPrefs.getInt("Setting",R.drawable.settings_btn));



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        btnBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this,BackgroundsActivity.class);
                startActivity(i);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LandingActivity.this)
                        .setTitle("Reset")
                        .setMessage("Do you want reset all your progress")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(LandingActivity.this)
                                        .setTitle("Reset")
                                        .setMessage("This will reset all your scores to 0")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                prefs.edit().putInt("total score",0).apply();
                                                prefs.edit().putInt("high score",0).apply();
                                                for(int i = 0; i<10;i++){
                                                    prefs.edit().putBoolean("buy".concat(String.valueOf(i)),false).apply();
                                                    prefs.edit().putInt("selected image",0).apply();
                                                }
                                                totalScore.setText("Total score: 0");
                                                highScore.setText("Last high score: 0");

                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this,SettingActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        onDestroy();
    }
}