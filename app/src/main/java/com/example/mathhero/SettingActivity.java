package com.example.mathhero;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    TextView tvLang, tvReset;
    String[] lang = {"اللغة العربية","English"};
    String selectedLang = "English";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tvLang = findViewById(R.id.lang);
        tvReset = findViewById(R.id.reset);

        SharedPreferences sharedPreferences = getSharedPreferences("langPrefs",MODE_PRIVATE);
        selectedLang = sharedPreferences.getString("Lang","Language");
        tvLang.setText(sharedPreferences.getString("Lang","Language"));
        tvReset.setText(sharedPreferences.getString("Reset","Reset"));

        tvLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("Languages")
                        .setSingleChoiceItems(lang, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedLang = lang[which];

                                SharedPreferences.Editor editor = getSharedPreferences("langPrefs", MODE_PRIVATE).edit();
                                if(selectedLang.equals("اللغة العربية")) {
                                    //Landing activity
                                    editor.putInt("Start", R.drawable.ar_start);
                                    editor.putInt("Background", R.drawable.ar_enviro);
                                    editor.putInt("Setting", R.drawable.ar_setting);
                                    //over activity
                                    editor.putInt("Again", R.drawable.ar_again);
                                    editor.putInt("Menu", R.drawable.ar_menu);
                                    editor.putString("Total", "النتيجة الكلية: ");
                                    editor.putString("High", "أعلى نتيجة: ");
                                    //Main activity
                                    editor.putString("mainScore","النتيجة: ");
                                    editor.putString("time","الوقت: ");
                                    //Setting activity
                                    editor.putString("Lang","اللغات");
                                    editor.putString("Reset","إعادة ضبط");
                                    //Background activity
                                    editor.putString("totalCoins","المجموع: ");
                                    //reset message
                                    editor.putString("msgTitle","إعادة الضبط");
                                    editor.putString("msgText","هل تريد إعادة ضبط تقدمك!");
                                    editor.putString("positive","حسنا");
                                    editor.putString("negative","تجاهل");
                                    //buying message
                                    editor.putString("buyTitle","شراء خلفية جديدة");
                                    editor.putString("buyMsg","شراء هذه الخلفية");
                                    editor.putString("buyOp","شراء");
                                    editor.putString("def","الخلفية الرئيسة");
                                }else{
                                    //Landing activity
                                    editor.putInt("Start",R.drawable.start);
                                    editor.putInt("Background",R.drawable.environment);
                                    editor.putInt("Setting",R.drawable.settings_btn);
                                    //Main activity
                                    editor.putString("mainScore","Score: ");
                                    editor.putString("time","Time: ");
                                    //over activity
                                    editor.putInt("Again",R.drawable.again);
                                    editor.putInt("Menu",R.drawable.menu);
                                    editor.putString("Total","Total score: ");
                                    editor.putString("High","Last high Score: ");
                                    editor.putString("Lang","Languages");
                                    editor.putString("Reset","Reset");
                                    //Background activity
                                    editor.putString("totalCoins","Total: ");
                                    //reset message
                                    editor.putString("msgTitle","Reset");
                                    editor.putString("msgText","Do you want to reset your progress!");
                                    editor.putString("positive","Ok");
                                    editor.putString("negative","Dismiss");
                                    //buying message
                                    editor.putString("buyTitle","Buy background");
                                    editor.putString("buyMsg","Buy this background");
                                    editor.putString("buyOp","Buy");
                                    //btnDef
                                    editor.putString("def","Default Background");
                                }
                                editor.apply();
                                tvLang.setText(sharedPreferences.getString("Lang","English"));
                                tvReset.setText(sharedPreferences.getString("Reset","Reset"));

                            }
                        }).setPositiveButton(sharedPreferences.getString("positive","Ok"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle(sharedPreferences.getString("msgTitle","Reset"))
                        .setMessage(sharedPreferences.getString("msgText","Do you want to reset your progress!"))
                        .setPositiveButton(sharedPreferences.getString("positive", "Ok"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
                                prefs.edit().putInt("total score",0).apply();
                                prefs.edit().putInt("high score",0).apply();
                                prefs.edit().putInt("selected image",0).apply();
                                for(int i = 0; i<10; i++){
                                    prefs.edit().putBoolean("buy".concat(String.valueOf(i)),false).apply();
                                }
                                Toast.makeText(SettingActivity.this,"All your progress have been reset",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton(sharedPreferences.getString("negative", "Dismiss"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SettingActivity.this,LandingActivity.class);
        startActivity(i);
        finish();
    }
}