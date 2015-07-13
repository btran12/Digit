package xyz.baotran.digit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameOnActivity extends Activity {
    TextView matchNumberTextView,
            rotatingNumberTextView,
            scoreTextView;

    int score,
            rotatingNumber,
            delayGap, //Initial Delay (ms)
            incorrect, //# of times the user missed the matched number, deduct score
            delayDecreaseBy, //Initial Decrease is 50
            levelsPassed, //Decrease the delayBy by 10 for every 2 levels
            bonusTime;

    long timeOfLastClick; //To disallow the user from repeatedly clicking

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

    }

    public void initializeVariables(){
        score = 0;
        rotatingNumber = 0;
        delayGap = 400;
        delayDecreaseBy = 50;
        incorrect = 0;
        levelsPassed = 0;
        bonusTime = 20;

        matchNumberTextView = (TextView) findViewById(R.id.matchNumber);
        rotatingNumberTextView = (TextView) findViewById(R.id.rotatingNumber);
        scoreTextView = (TextView) findViewById(R.id.score);
    }

    //TODO If wrong deduct bonus multiply by 5
    public void updateDisplay(){
        RelativeLayout gameOnLayout = (RelativeLayout) findViewById(R.id.gameActivityLayout);
        gameOnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Register the click only after every 500ms, Disallow repeated clicks
                if (System.currentTimeMillis() > (timeOfLastClick + 500)) {
                    long time = System.currentTimeMillis();
                    //Pause the rotation to give the user a quick look at their chosen number
                    //Go into a loop for 500ms
                    while ((System.currentTimeMillis() - time) < 500) {
                        pauseRotation();
                    }

                    //Get values from the TextViews
                    int stoppedNumber = Integer.valueOf(rotatingNumberTextView.getText().toString());
                    int matchNumber = Integer.valueOf(matchNumberTextView.getText().toString());

                    //Compare; Increase Score if Matches
                    if (stoppedNumber == matchNumber) {

                        decreaseDelay();

                        increaseScore();

                        //Set a new Match number
                        setMatchNumber(randomNumber());
                    }

                    //Resume
                    resumeRotation();

                    timeOfLastClick = System.currentTimeMillis();
                }
            }
        });
    }

    //TODO Have a 20 seconds countdown that multiply with 1340, the longer the player takes, the less the multiple, final score when the user gets it correct.
    //TODO Wrong answer also penalizes some how.
    public void increaseScore() {
        int currentScore = Integer.valueOf(scoreTextView.getText().toString());

        scoreTextView.setText(currentScore + 1340 + "");
    }

    public void decreaseDelay() {
        if (delayGap > 100) {//Max Level
            //Increase Speed Per 2 levels
            if (levelsPassed == 4) {
                delayDecreaseBy -= 25;
                levelsPassed = 0;
            }
            //Keep track of the 2 levels passed
            levelsPassed++;
            delayGap -= delayDecreaseBy;
        }
    }

    public void rotateNumber(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while(true){
                    try{
                        //Determine the rotation's speed
                        Thread.sleep(delayGap);
                        if(!isPaused){  //To toggle the rotation
                            mHandler.post(new Runnable(){
                               @Override
                                public void run(){
                                   //Reset, only loop from 0-9
                                   if (rotatingNumber > 9) {
                                       rotatingNumber = 0;
                                   }
                                   rotatingNumberTextView.setText(rotatingNumber++ + "");
                               }
                            });
                        }
                    }catch(Exception e){
                        Log.e("Thread", "Unable to update rotatingNumber TextView");
                    }
                }
            }
        }).start();
    }

    public void pauseRotation(){ isPaused = true;}

    public void resumeRotation(){ isPaused = false;}

    public int randomNumber(){ return (int) Math.floor(Math.random()* 10); }

    public void setMatchNumber(int randomNumber){ matchNumberTextView.setText(randomNumber+""); }

}
