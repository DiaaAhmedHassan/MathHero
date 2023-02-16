package com.example.mathhero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
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

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView tvResult, tvNumber1, tvNumber2, tvOpr,tvScore,tvTimer;
    ImageButton btnGo;
    MediaPlayer mp,mpw,tick,timesUp,gameOver;
    MediaPlayer[] sounds = new MediaPlayer[4];
    public static ImageView backgroundImg;
    Random rand = new Random();
    CountDownTimer time;
    int number1,number2;
    int oprNumber;
    int voice;
    int colorThemes;
    int length;
    public static int cnt = 0;
    public static int score = 0;
    public static int highScore = 0;
    public static boolean highScoreFlag = false;
    short voiceCounter = 0;
    char chosenOpr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNumber1 = findViewById(R.id.number1);
        tvNumber2 = findViewById(R.id.number2);
        tvResult = findViewById(R.id.result);
        tvTimer = findViewById(R.id.timer);
        btnGo = findViewById(R.id.btn_go);
        tvOpr = findViewById(R.id.opr);
        tvScore = findViewById(R.id.score);
        backgroundImg = findViewById(R.id.mainBackground);
        mp = MediaPlayer.create(this,R.raw.correct);
        mpw = MediaPlayer.create(this,R.raw.wrong);
        tick = MediaPlayer.create(this,R.raw.tick);
        timesUp = MediaPlayer.create(this,R.raw.timeup);
        gameOver = MediaPlayer.create(this,R.raw.gameover);
        //fill sounds array
        sounds[0] = MediaPlayer.create(this,R.raw.excellent);
        sounds[1] = MediaPlayer.create(this,R.raw.great);
        sounds[2] = MediaPlayer.create(this,R.raw.wow);
        sounds[3] = MediaPlayer.create(this,R.raw.awesome);



        //choose random number of operations to start voice
        voice = rand.nextInt(20)+5;
        System.out.println();
        System.out.println("number of operations = "+voice);
        //reset the current game score
        cnt = 0;

        //read the old score
        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        score = preferences.getInt("total score",0);
        backgroundImg.setImageResource(preferences.getInt("selected image",0));
        SharedPreferences langPrefs = getSharedPreferences("langPrefs",MODE_PRIVATE);
        tvScore.setText(langPrefs.getString("mainScore","Score: ")+cnt);







        //generate random theme
        colorThemes = rand.nextInt(3);
        switch (colorThemes){
            //orange and blue
            case 0:
                tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.orange));
                tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.blue));
                tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.orange));
                tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.blue));
                break;
            case 1:
                tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.green));
                tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.red));
                tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.green));
                tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.red));
                break;
            case 2:
                tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.yellow));
                tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.violet));
                tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.yellow));
                tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.violet));
                break;
            case 3:
                tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.ski));
                tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.or));
                tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.ski));
                tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.or));
                break;


        }



        //creating first random number
        number1 = rand.nextInt(10) + 1;

        //choosing an operation
        String opr = "+×-";
        oprNumber = rand.nextInt(3);
        chosenOpr = opr.charAt(oprNumber);
        tvOpr.setText(""+chosenOpr);

        //creating second random number
        number2 = rand.nextInt(10) + 1;




        //special case for minus that make the first number always bigger than the second on
        switch (chosenOpr) {
            case '-':
                //change the operation background color
                tvOpr.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.orange));
                //check if the first number is smaller than the second one
                if (number1 < number2) {
                    tvNumber1.setText("" + number2);
                    tvNumber2.setText("" + number1);
                } else {
                    tvNumber1.setText("" + number1);
                    tvNumber2.setText("" + number2);
                }
                break;
            case '+':
                tvOpr.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.ski));
                tvNumber1.setText("" + number1);
                tvNumber2.setText("" + number2);
                break;
            case '×':
                tvOpr.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.red));
                tvNumber1.setText("" + number1);
                tvNumber2.setText("" + number2);
                break;
        }
        time = new CountDownTimer(1000*11,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(langPrefs.getString("time","Time: ")+millisUntilFinished/1000);
                tick.start();

            }

            @Override
            public void onFinish() {
                tick.stop();
                gameOver.start();
                timesUp.start();
                correctWrong();
                Intent i = new Intent(MainActivity.this,overActivity.class);
                startActivity(i);
                MainActivity.this.finish();
                return;
            }
        }.start();








        //go button function
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time.cancel();

                System.out.println();
                System.out.println("number of operations = "+voice);
                //generate random theme for the next operation
                colorThemes = rand.nextInt(3);
                switch (colorThemes){
                    //orange and blue
                    case 0:
                        tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.orange));
                        tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.blue));
                        tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.orange));
                        tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.blue));
                        break;
                    case 1:
                        tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.green));
                        tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.red));
                        tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.green));
                        tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.red));
                        break;
                    case 2:
                        tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.yellow));
                        tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.violet));
                        tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.yellow));
                        tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.violet));
                        break;
                    case 3:
                        tvNumber1.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.ski));
                        tvNumber1.setTextColor(MainActivity.this.getColorStateList(R.color.or));
                        tvNumber2.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.ski));
                        tvNumber2.setTextColor(MainActivity.this.getColorStateList(R.color.or));
                        break;


                }


                //check if the result text is not empty
                if(tvResult.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "fill the text plz", Toast.LENGTH_SHORT).show();
                }else {
                    //generate different operations
                    switch (chosenOpr) {
                        case '×':
                            if (Integer.parseInt(tvResult.getText().toString()) == number1 * number2) {
                                //if the answer is correct
                                if (number1 < number2) {
                                    score += number1;
                                    cnt+= number1;
                                } else {
                                    score += number2;
                                    cnt+= number2;
                                }



                                //generate sound if the user answers correct answer
                                mp.start();
                                countForVoice();


                                tvScore.setText(langPrefs.getString("mainScore","Score: ") + cnt);
                                System.out.println("Correct");
                                saveScore();
                            } else {
                                mpw.start();
                                System.out.println("wrong");
                                tick.stop();
                                time.cancel();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;

                            }
                            break;
                        case '+':
                            if (Integer.parseInt(tvResult.getText().toString()) == number1 + number2) {
                                if (number1 < number2) {
                                    score += number1;
                                    cnt += number1;
                                } else {
                                    score += number2;
                                    cnt += number2;
                                }
                                saveScore();

                                //wining sound and show the score
                                mp.start();
                                countForVoice();
                                tvScore.setText(langPrefs.getString("mainScore","Score: ") + cnt);
                                System.out.println("Correct");
                            } else {
                                mpw.start();
                                System.out.println("wrong");
                                tick.stop();
                                time.cancel();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;
                            }
                            break;
                        case '-':
                            if (number1 < number2) {
                                tvNumber1.setText("" + number2);
                                tvNumber2.setText("" + number1);
                                if (Integer.parseInt(tvResult.getText().toString()) == number2 - number1) {
                                    if (number1 < number2) {
                                        score += number1;
                                        cnt+= number1;
                                    } else {
                                        score += number2;
                                        cnt+= number2;
                                    }
                                    saveScore();

                                //wining sound and show the score
                                    mp.start();
                                    countForVoice();
                                    tvScore.setText(langPrefs.getString("mainScore","Score: ") + cnt);
                                    System.out.println("Correct");
                                } else {
                                    mpw.start();
                                    System.out.println("Wrong");
                                    tick.stop();
                                    time.cancel();
                                    correctWrong();
                                    Intent i = new Intent(MainActivity.this,overActivity.class);
                                    startActivity(i);
                                    MainActivity.this.finish();
                                    return;
                                }
                            } else {
                                if (Integer.parseInt(tvResult.getText().toString()) == number1 - number2) {
                                    if (number1 < number2) {
                                        score += number1;
                                        cnt+= number1;
                                    } else {
                                        score += number2;
                                        cnt+= number2;
                                    }
                                    saveScore();

                                    mp.start();
                                    tvScore.setText(langPrefs.getString("mainScore","Score: ") + cnt);
                                    System.out.println("Correct");

                                    //wining sound and show the score

                                } else {
                                    mpw.start();
                                    System.out.println("Wrong");
                                    tick.stop();
                                    time.cancel();
                                    correctWrong();
                                    Intent i = new Intent(MainActivity.this,overActivity.class);
                                    startActivity(i);
                                    MainActivity.this.finish();
                                    return;
                                }
                            }



                    }




                    //reset the value of the result text and create new random numbers and new operation
                    tvResult.setText("");
                    //generate new random numbers according to current score.

                    //number by number
                    if(cnt<100) {
                        number1 = rand.nextInt(10) + 1;
                        oprNumber = rand.nextInt(3);
                        chosenOpr = opr.charAt(oprNumber);
                        number2 = rand.nextInt(10) + 1;
                        time = new CountDownTimer(1000*11,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvTimer.setText(langPrefs.getString("time","Time: ")+millisUntilFinished/1000);
                                tick.start();

                            }

                            @Override
                            public void onFinish() {
                                tick.stop();
                                gameOver.start();
                                timesUp.start();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;
                            }
                        }.start();
                        //timer 10s
                    }else if(cnt<200){ //number by two numbers
                        number1 = rand.nextInt(100) + 1;
                        oprNumber = rand.nextInt(3);
                        chosenOpr = opr.charAt(oprNumber);
                        number2 = rand.nextInt(10) + 1;
                        time = new CountDownTimer(1000*21,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvTimer.setText(langPrefs.getString("time","Time: ")+millisUntilFinished/1000);
                                tick.start();

                            }

                            @Override
                            public void onFinish() {
                                tick.stop();
                                gameOver.start();
                                timesUp.start();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;
                            }
                        }.start();
                        //timer 20s
                    }else if(cnt<300){//two numbers by two numbers
                        number1 = rand.nextInt(100) + 1;
                        oprNumber = rand.nextInt(3);
                        chosenOpr = opr.charAt(oprNumber);
                        number2 = rand.nextInt(100) + 1;
                        time = new CountDownTimer(1000*31,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvTimer.setText(langPrefs.getString("time","Time: ")+millisUntilFinished/1000);
                                tick.start();

                            }

                            @Override
                            public void onFinish() {
                                tick.stop();
                                gameOver.start();
                                timesUp.start();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;
                            }
                        }.start();
                        //timer 30s
                    }else if(cnt<400){//two numbers by three numbers
                        number1 = rand.nextInt(1000) + 1;
                        oprNumber = rand.nextInt(3);
                        chosenOpr = opr.charAt(oprNumber);
                        number2 = rand.nextInt(100) + 1;
                        time = new CountDownTimer(1000*51,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvTimer.setText(langPrefs.getString("time","Time: ")+millisUntilFinished/1000);
                                tick.start();

                            }

                            @Override
                            public void onFinish() {
                                tick.stop();
                                gameOver.start();
                                timesUp.start();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;
                            }
                        }.start();
                        //timer 50s
                    }else if(score<500){ //three numbers by three numbers
                        number1 = rand.nextInt(1000) + 1;
                        oprNumber = rand.nextInt(3);
                        chosenOpr = opr.charAt(oprNumber);
                        number2 = rand.nextInt(1000) + 1;
                        time = new CountDownTimer(1000*61,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvTimer.setText(langPrefs.getString("time","Time: ")+millisUntilFinished/1000);
                                tick.start();


                            }

                            @Override
                            public void onFinish() {
                                tick.stop();
                                gameOver.start();
                                timesUp.start();
                                correctWrong();
                                Intent i = new Intent(MainActivity.this,overActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                                return;
                            }
                        }.start();
                        //timer 60s
                    }

                    if (chosenOpr == '-') {
                        tvOpr.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.orange));
                        if (number1 < number2) {
                            tvNumber1.setText("" + number2);
                            tvOpr.setText("" + chosenOpr);
                            tvNumber2.setText("" + number1);
                        } else {
                            tvNumber1.setText("" + number1);
                            tvOpr.setText("" + chosenOpr);
                            tvNumber2.setText("" + number2);
                        }
                    }else if(chosenOpr == '+'){
                        tvOpr.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.ski));
                        tvNumber1.setText("" + number1);
                        tvOpr.setText("" + chosenOpr);
                        tvNumber2.setText("" + number2);
                    }else if(chosenOpr == '×'){
                        tvOpr.setBackgroundTintList(MainActivity.this.getColorStateList(R.color.red));
                        tvNumber1.setText("" + number1);
                        tvOpr.setText("" + chosenOpr);
                        tvNumber2.setText("" + number2);
                    }


                }




            }
        });









    }

    public void saveScore(){
        //read the new data

        SharedPreferences.Editor editor = getSharedPreferences("prefs",MODE_PRIVATE).edit();
        editor.putInt("total score",score);
        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);

        if(cnt>preferences.getInt("high score",0)){
            highScore = cnt;
            editor.putInt("high score",highScore);
            highScoreFlag = true;

        }
        editor.apply();

    }


    public void countForVoice(){
        voiceCounter++;
        if(voiceCounter == voice){
            //choose random index of voice
            int randVoice = rand.nextInt(4);
            sounds[randVoice].start();
            voiceCounter = 0;
            //choose new random number of operations
            voice = rand.nextInt(20)+10;
        }
    }

    //type the numbers by button
    public void type0(View v){
        tvResult.setText(tvResult.getText().toString()+"0");

    }
    public void type1(View v){
        tvResult.setText(tvResult.getText().toString()+"1");
    }
    public void type2(View v){
        tvResult.setText(tvResult.getText().toString()+"2");
    }
    public void type3(View v){
        tvResult.setText(tvResult.getText().toString()+"3");

    }
    public void type4(View v){
        tvResult.setText(tvResult.getText().toString()+"4");

    }
    public void type5(View v){
        tvResult.setText(tvResult.getText().toString()+"5");

    }
    public void type6(View v){
        tvResult.setText(tvResult.getText().toString()+"6");

    }
    public void type7(View v){
        tvResult.setText(tvResult.getText().toString()+"7");

    }
    public void type8(View v){
        tvResult.setText(tvResult.getText().toString()+"8");

    }
    public void type9(View v){
        tvResult.setText(tvResult.getText().toString()+"9");

    }

    //backspace
    public void remove(View v){
        if(!tvResult.getText().toString().isEmpty()) {
            String str = tvResult.getText().toString();
            str = str.substring(0, str.length() - 1);
            tvResult.setText(str);
        }
    }
    public void correctWrong(){

        char operation = tvOpr.getText().toString().charAt(0);
        switch(operation){
            case '×':
                Toast.makeText(this, ""+number1+" × "+number2+" = "+(number1*number2), Toast.LENGTH_SHORT).show();
                break;
            case '+':
                Toast.makeText(this, ""+number1+" + "+number2+" = "+(number1+number2), Toast.LENGTH_SHORT).show();
                break;
            case '-':
                Toast.makeText(this,""+tvNumber1.getText().toString()+" - "+tvNumber2.getText().toString()+" = "+(Integer.parseInt(tvNumber1.getText().toString())-Integer.parseInt(tvNumber2.getText().toString())),Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        saveScore();
        time.cancel();
        tick.stop();
        Intent i = new Intent(MainActivity.this,LandingActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();

    }

    @Override
    public void onUserLeaveHint() {

        time.cancel();
        tick.pause();
        length = tick.getCurrentPosition();
        super.onUserLeaveHint();
    }

    @Override
    public void onStart() {
        time.start();
        tick.seekTo(length);
        tick.start();
        super.onStart();
    }
}