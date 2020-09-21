package arvolear.zoomer.zoomer.game;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

public class GameActivity extends AppCompatActivity
{
    private GameController gameController;

    private void hideNavigationBar()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameController = new GameController(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideNavigationBar();
        gameController.resume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        gameController.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
        {
            hideNavigationBar();
        }
    }

    @Override
    protected void onPause()
    {
        gameController.pause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        gameController.stop();
        super.onStop();
    }

    @Override
    public void onEnterAnimationComplete()
    {
        gameController.fullIn();
    }

    @Override
    public void onBackPressed()
    {
        gameController.leave();
    }
}
