package com.example.mathhero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class overActivity extends AppCompatActivity {
    LinearLayout highScoreView;
  public static   ImageButton btnAgain, btnMenu;
    Handler handler = new Handler();
    MediaPlayer wowSound;
    ImageView imgScore;
    TextView tvHighScore;
    int high;

    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        MobileAds.setAppVolume(0.0F);

        InterstitialAd.load(this,getString(R.string.interId), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                            LoadAd();

                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("TAG", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });



        highScoreView = findViewById(R.id.high_score_view);
        btnAgain = findViewById(R.id.play_again);
        btnMenu = findViewById(R.id.main_menu);
        tvHighScore = findViewById(R.id.over_score);
        imgScore = findViewById(R.id.score_img);
        wowSound = MediaPlayer.create(overActivity.this,R.raw.wow);

        SharedPreferences preferences1 = getSharedPreferences("langPrefs",MODE_PRIVATE);
        btnAgain.setBackgroundResource(preferences1.getInt("Again",R.drawable.again));
        btnMenu.setBackgroundResource(preferences1.getInt("Menu",R.drawable.menu));




        //return to main menu
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(overActivity.this,LandingActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.cnt = 0;
                Intent i = new Intent(overActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });


        if(MainActivity.highScoreFlag){
            highScoreView.setVisibility(View.VISIBLE);
            SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
            high = preferences.getInt("high score",0);
            int i = 0;
            while (i<=high){
                final String score_string = ""+ i++;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        MediaPlayer.create(overActivity.this,R.raw.collect).start();
                        tvHighScore.setText(score_string);
                    }
                },++i *100);
            }
            wowSound.start();
            MainActivity.highScoreFlag = false;
        }

    }

    void LoadAd(){

            if (mInterstitialAd != null) {
                mInterstitialAd.show(overActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

    }

    }
