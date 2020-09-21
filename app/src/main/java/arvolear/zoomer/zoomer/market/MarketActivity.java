package arvolear.zoomer.zoomer.market;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

public class MarketActivity extends AppCompatActivity
{
    private MarketController marketController;

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
        setContentView(R.layout.activity_market);

        marketController = new MarketController(this);
    }

    @Override
    protected void onPause()
    {
        marketController.pause();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideNavigationBar();
        marketController.resume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        marketController.start();
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
    protected void onStop()
    {
        marketController.stop();
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        marketController.leave();
    }
}
