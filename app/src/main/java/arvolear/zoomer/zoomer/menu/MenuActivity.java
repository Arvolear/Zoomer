package arvolear.zoomer.zoomer.menu;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.expansions.ExpansionController;

public class MenuActivity extends AppCompatActivity
{
    private ExpansionController expansionController;
    private MenuController menuController = null;

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ExpansionController.PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                menuController = new MenuController(this);
                return;
            }

            Toast.makeText(this, "Unable to launch the game without permission", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        expansionController = new ExpansionController(this);

        /* check for expansions accessibility */
        if (!expansionController.downloadContent())
        {
            if (expansionController.checkPermission())
            {
                menuController = new MenuController(this);
            }
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();

        if (menuController != null)
        {
            menuController.restart();
        }
    }

    @Override
    protected void onStart()
    {
        expansionController.start();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideNavigationBar();

        if (menuController != null)
        {
            menuController.resume();
        }
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
        if (menuController != null)
        {
            menuController.pause();
        }

        super.onPause();
    }

    @Override
    protected void onStop()
    {
        expansionController.stop();

        if (menuController != null)
        {
            menuController.stop();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        if (menuController != null)
        {
            menuController.destroy();
        }

        super.onDestroy();
    }
}
