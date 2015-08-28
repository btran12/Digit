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
            scoreTextView,
            bonusTextView;

    int score,
            rotatingNumber,
            delayGap, //Initial Delay (ms)
            incorrect, //# of times the user missed the matched number, deduct score
            delayDecreaseBy, //Initial Decrease is 50
            levelsPassed, //Decrease the delayBy by 10 for every 2 levels
            bonusTime,
            level;

    long timeOfLastClick; //To disallow the user from repeatedly clicking

    Handler mHandler,   //Main number thread
            mHandler2;  //Secondary bonus number thread
    boolean isPaused;
    boolean gameEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use the activity_game_on layout
        setContentView(R.layout.activity_game_on);

        initializeVariables();

        //Assign a random number to match
        setMatchNumber(randomNumber());

        //Continuously change rotateNumberTextView
        rotateNumber();

        decreaseBonus();

        updateDisplay();

    }

    public void initializeVariables(){
        score = rotatingNumber = incorrect = levelsPassed = 0;
        delayGap = 400;
        delayDecreaseBy = 50;
        bonusTime = 20;
        level = 1;

        isPaused = gameEnd = false;

        matchNumberTextView = (TextView) findViewById(R.id.matchNumber);
        rotatingNumberTextView = (TextView) findViewById(R.id.rotatingNumber);
        scoreTextView = (TextView) findViewById(R.id.score);
        bonusTextView = (TextView) findViewById(R.id.bonusTime);

        mHandler = new Handler();
        mHandler2 = new Handler();
    }
    //TODO Background music, and tap fx
    public void updateDisplay(){
        final RelativeLayout gameOnLayout = (RelativeLayout) findViewById(R.id.gameActivityLayout);
        gameOnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Register the click only after every 250ms, Disallow repeated clicks
                if (System.currentTimeMillis() > (timeOfLastClick + 250)) {
                    long time = System.currentTimeMillis();

                    //Go into a loop for 500ms; delay between each pick
                    while ((System.currentTimeMillis() - time) < 500) {
                        pauseRotation();
                    }

                    //Get values from the TextViews
                    int stoppedNumber = Integer.valueOf(rotatingNumberTextView.getText().toString());
                    int matchNumber = Integer.valueOf(matchNumberTextView.getText().toString());
                    bonusTime = Integer.valueOf(bonusTextView.getText().toString());

                    //Compare; Increase Score if Matches
                    if (stoppedNumber == matchNumber) {

                        incorrect = 0;

                        decreaseDelay();

                        increaseScore();

                        //Set a new Match number
                        setMatchNumber(randomNumber());

                        //Reset Bonus
                        bonusTextView.setText((bonusTime = 20) + "");

                        //New layout on top.
                        //TODO Animations
                        if (level > 5) {    //TODO Change back to 10 when done with testing
                            endGame();
                        } else {
                            level++;
                        }
                    } else {
                        //Incorrect pick, deduct 5 from bonus
                        if (bonusTime > 5) {
                            bonusTime -= 5;
                            bonusTextView.setText(bonusTime + "");
                        } else { // < 5
                            bonusTime = 0;
                            bonusTextView.setText(bonusTime + "");
                        }

                        incorrect++;

                        //End game if the player get 4 incorrect picks
                        if (incorrect > 4){ endGame(); }
                    }

                    //Resume
                    resumeRotation();

                    timeOfLastClick = System.currentTimeMillis();   //Register the current time
                }
            }
        });
    }

    public void endGame(){
        pauseRotation();
        bonusTextView.setText("0");  //Can no longer decrease when clicked
        gameEnd = true;

        //open end screen
        startEndScreenActivity();
    }

    public void startEndScreenActivity(){
        Intent endIntent = new Intent(this, EndGameActivity.class);
        endIntent.putExtra("finalScore", score);

        finish(); //Kill current activity
        startActivity(endIntent);
    }

    public void increaseScore() {
        int currentScore = Integer.valueOf(scoreTextView.getText().toString());
        score = currentScore + (1340 * bonusTime);
        scoreTextView.setText(score + "");
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

    public void decreaseBonus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (gameEnd == false) {
                    try {
                        Thread.sleep(1000);
                        if (!isPaused) {
                            mHandler2.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (bonusTime > 0) {
                                        bonusTextView.setText(bonusTime-- + "");
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("Thread", "Unable to update bonusTimeTextView");
                    }
                }
            }
        }).start();
    }

    public void rotateNumber(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while(gameEnd == false){
                    try{
                        //Determine the rotation's speed
                        Thread.sleep(delayGap);
                        if(!isPaused){  //To toggle the rotation
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
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

    //Pause thread, stop rotation
    public void pauseRotation(){ isPaused = true;}

    public void resumeRotation(){ isPaused = false;}

    public int randomNumber(){ return (int) Math.floor(Math.random()* 10); }

    public void setMatchNumber(int randomNumber){ matchNumberTextView.setText(randomNumber+""); }

}
