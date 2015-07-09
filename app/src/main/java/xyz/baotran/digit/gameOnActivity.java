package xyz.baotran.digit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class gameOnActivity extends Activity{
    TextView matchNumberTextView,
            rotatingNumberTextView,
            scoreTextView;

    int score,
            rotatingNumber,
            delayGap, //Initial Delay (ms)
            incorrect; //# of times the user missed the matched number, deduct score

    Handler mHandler = new Handler();
    boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use the activity_game_on layout
        setContentView(R.layout.activity_game_on);

        initializeVariables();

        //Get whatever is passed from the previous intent
        Intent fromMain = getIntent();
        String userName = fromMain.getExtras().getString("userName");

        //Assign a random number to match
        setMatchNumber(randomNumber());

        //Continuously change rotateNumberTextView
        rotateNumber();

        updateDisplay();

//        RelativeLayout gameOnLayout = (RelativeLayout) findViewById(R.id.gameActivityLayout);
//        gameOnLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
    }

    public void initializeVariables(){
        score = 0;
        rotatingNumber = 0;
        delayGap = 400;
        incorrect = 0;

        matchNumberTextView = (TextView) findViewById(R.id.matchNumber);
        rotatingNumberTextView = (TextView) findViewById(R.id.rotatingNumber);
        scoreTextView = (TextView) findViewById(R.id.score);
    }

    public void updateDisplay(){
        RelativeLayout gameOnLayout = (RelativeLayout) findViewById(R.id.gameActivityLayout);
        gameOnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //Give the user a swift look at what they chose
                    //Pause the Thread
                    //Find another method to pause.
                    //Reinitialize rotateNumber/ timer
                    //Thread.currentThread().sleep(1000);

                    //Get values from the TextViews
                    int stoppedNumber = Integer.valueOf(rotatingNumberTextView.getText().toString());
                    int matchNumber = Integer.valueOf(matchNumberTextView.getText().toString());
                    int currentScore = Integer.valueOf(scoreTextView.getText().toString());

                    //Compare; Increase Score if Matches
                    if (stoppedNumber == matchNumber){
                        //Decrease Delay
                        if (delayGap > 100){
                            delayGap -= 50;
                        }

                        //Increase Score
                        scoreTextView.setText(currentScore + 1340 + "");

                        //Set a new Match number
                        setMatchNumber(randomNumber());
                    }

            }
        });
    }

    public void rotateNumber(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("Current Number: ", rotatingNumber+"");
                //Reset, only loop from 0-9
                if (rotatingNumber > 9) {
                    rotatingNumber = 0;
                }
                rotatingNumberTextView.setText(rotatingNumber + "");
                increment();
            }
            //Replace with delayGap
        }, 0, 1000);    //Update every second
    }

    public synchronized void increment(){
        rotatingNumber++;
    }

    /**
     * Stop Updating the rotatingNumberTextView
     */
    public void pauseRotation(){ isPaused = true;}

    /**
     * Resume Updating the rotatingNumberTextView
     */
    public void resumeRotation(){ isPaused = false;}

    public int randomNumber(){
        //Random number between 0 and 9
        return (int) Math.floor(Math.random()* 10);
    }

    public void setMatchNumber(int randomNumber){
        matchNumberTextView.setText(randomNumber+"");
    }

}
