package com.example.mathhero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;

public class BackgroundsActivity extends AppCompatActivity {

    ImageView[] images = new ImageView[10];
    int[] res = new int[10];
    int totalScore;
    boolean[] isBought = new boolean[10];
    String[] buyKey = new String[10];
    String[] resKey = new String[10];
    TextView[] tvCoins = new TextView[10];
    TextView tvTotal;
    SharedPreferences preferences;
    SharedPreferences sharedPreferences;
    Button btnDef;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgrounds);

        tvTotal = findViewById(R.id.total);

        res[0] = R.drawable.cubes;
        res[1] = R.drawable.numbers;
        res[2] = R.drawable.army;
        res[3] = R.drawable.office;
        res[4] = R.drawable.places;
        res[5] = R.drawable.space;
        res[6] = R.drawable.woods;
        res[7] = R.drawable.home;
        res[8] = R.drawable.egypt;
        res[9] = R.drawable.palestine;


        //declare images array
        images[0] = findViewById(R.id.cube);
        images[1] = findViewById(R.id.number);
        images[2] = findViewById(R.id.army);
        images[3] = findViewById(R.id.office);
        images[4] = findViewById(R.id.places);
        images[5] = findViewById(R.id.space);
        images[6] = findViewById(R.id.woods);
        images[7] = findViewById(R.id.city);
        images[8] = findViewById(R.id.egypt);
        images[9] = findViewById(R.id.palestine);
        //declare coins texts
        tvCoins[0] = findViewById(R.id.cubesCoins);
        tvCoins[1] = findViewById(R.id.numberCoins);
        tvCoins[2] = findViewById(R.id.armyCoins);
        tvCoins[3] = findViewById(R.id.officeCoin);
        tvCoins[4] = findViewById(R.id.placesCoins);
        tvCoins[5] = findViewById(R.id.spaceCoins);
        tvCoins[6] = findViewById(R.id.woodsCoins);
        tvCoins[7] = findViewById(R.id.cityCoins);
        tvCoins[8] = findViewById(R.id.egyptCoins);
        tvCoins[9] = findViewById(R.id.palestineCoin);

        btnDef = findViewById(R.id.def_btn);

        //adds
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //...

        //read the total score
        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        totalScore = preferences.getInt("total score",0);
        sharedPreferences = getSharedPreferences("langPrefs",MODE_PRIVATE);
        tvTotal.setText(sharedPreferences.getString("totalCoins","Total: ")+totalScore);
        btnDef.setText(sharedPreferences.getString("def","Default Background"));
        for(int i= 0; i<10; i++){
            isBought[i] = false;
        }

        for(int i = 0; i<10; i++){
            if(totalScore>=Integer.parseInt(tvCoins[i].getText().toString())){
                tvCoins[i].setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.coin,0);
            }
            if(preferences.getBoolean("buy".concat(String.valueOf(i)),false)){
                tvCoins[i].setVisibility(View.INVISIBLE);
            }
        }


        //clicking images
        for(int i = 0; i<10; i++){
            int finalI = i;
            images[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectBackground(finalI,res[finalI]);
                }
            });
        }

        for(int i = 0; i<10;i++){
            buyKey[i] = "buy".concat(String.valueOf(i));
            resKey[i] = "res".concat(String.valueOf(i));
        }

        btnDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putInt("selected image",0).apply();
                Toast.makeText(BackgroundsActivity.this,"Background set to it's default",Toast.LENGTH_SHORT).show();

            }
        });


    }
    public void SelectBackground(int i,int img){
        System.out.println(preferences.getBoolean("buy".concat(String.valueOf(i)),false));
        if(preferences.getBoolean("buy".concat(String.valueOf(i)), false)){
            img = res[i];
            preferences.edit().putInt("selected image",img).apply();
            MediaPlayer.create(BackgroundsActivity.this,R.raw.open).start();
            Toast.makeText(BackgroundsActivity.this,"Selected",Toast.LENGTH_SHORT).show();
        }else{
            BuyBackground(i);
        }
    }

    public void BuyBackground(int i) {
        if(totalScore>=Integer.parseInt(tvCoins[i].getText().toString())){
            new AlertDialog.Builder(BackgroundsActivity.this)
                    .setTitle(sharedPreferences.getString("buyTitle","Buy background"))
                    .setMessage(sharedPreferences.getString("buyMsg","Buy this background"))
                    .setPositiveButton(sharedPreferences.getString("buyOp","Buy"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isBought[i] = true;
                            totalScore -= Integer.parseInt(tvCoins[i].getText().toString());
                            preferences.edit().putInt("total score", totalScore).apply();
                            tvTotal.setText("Total: "+preferences.getInt("total score",0));
                            preferences.edit().putBoolean(buyKey[i],isBought[i]).apply();
                            preferences.edit().putInt((resKey[i]),res[i]).apply();
                            tvCoins[i].setVisibility(View.INVISIBLE);
                            SelectBackground(i,res[i]);
                            MediaPlayer.create(BackgroundsActivity.this,R.raw.change).start();


                        }
                    }).setNegativeButton(sharedPreferences.getString("negative","Dismiss"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }else{
            MediaPlayer.create(BackgroundsActivity.this,R.raw.buzzer).start();
            Toast.makeText(BackgroundsActivity.this,"Coins not enough",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(BackgroundsActivity.this,LandingActivity.class);
        startActivity(i);
        finish();
    }
}