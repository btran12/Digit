package xyz.baotran.digit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by quybao95 on 8/27/2015.
 */
public class EndGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end_screen);

        //Get Score from GameActivity
        Intent gameOn = getIntent();
        int score = gameOn.getExtras().getInt("finalScore");

        TextView scoreTextView = (TextView) findViewById(R.id.final_score);
        scoreTextView.setText(score+"");
    }

    // Restart Button Click Listener
    public void restartGame(View view){
        finish(); //Kill this activity; no history
        startActivity(new Intent(this, GameActivity.class));
    }
}
