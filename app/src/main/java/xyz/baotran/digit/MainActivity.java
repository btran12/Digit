package xyz.baotran.digit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide the Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void onStartGame(View view) {
        startActivity(new Intent(this, GameOnActivity.class));
    }

    public void openSettingsIntent(View view){startActivity(new Intent(this, SettingsActivity.class)); }
}
