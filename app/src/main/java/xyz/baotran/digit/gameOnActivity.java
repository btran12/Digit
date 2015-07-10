package xyz.baotran.digit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

                long time = System.currentTimeMillis();
                //Pause the rotation to give the user a quick look at their chosen number
                //Go into a loop for 500ms
                while((System.currentTimeMillis() - time) < 500){
                    pauseRotation();
                }

                //Get values from the TextViews
                int stoppedNumber = Integer.valueOf(rotatingNumberTextView.getText().toString());
                int matchNumber = Integer.valueOf(matchNumberTextView.getText().toString());
                int currentScore = Integer.valueOf(scoreTextView.getText().toString());

                //Compare; Increase Score if Matches
                if (stoppedNumber == matchNumber) {
                    //Decrease Delay
                    if (delayGap > 100) {
                        delayGap -= 50;
                    }

                    //Increase Score
                    scoreTextView.setText(currentScore + 1340 + "");

                    //Set a new Match number
                    setMatchNumber(randomNumber());
                }

                //Resume
                resumeRotation();

            }
        });
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
