package xyz.baotran.digit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Bao Tran on 6/26/2015.
 */
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

        //TODO Move this portion into a method
        //TODO Then remove the button along with its onClick method
        RelativeLayout gameOnLayout = (RelativeLayout) findViewById(R.id.gameActivityLayout);
        gameOnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

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

                    //Give the user a swift look at what they chose
                    //Pause the Thread
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
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

    //Obtained from stackoverflow
    public void rotateNumber(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(delayGap);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isPaused) {
                                    //Reset Only loop from 0-9
                                    if (rotatingNumber == 10) {
                                        rotatingNumber = 0;
                                    }
                                    rotatingNumberTextView.setText(rotatingNumber++ + "");
                                }
                            }
                        });
                    }catch (Exception e){
                        Log.e("Thread", "Unable to update rotating number text");
                    }
                }
            }
        }).start();
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
        int random = (int) Math.floor(Math.random()* 10);
        return random;
    }

    public void setMatchNumber(int randomNumber){
        matchNumberTextView.setText(randomNumber+"");
    }

}
