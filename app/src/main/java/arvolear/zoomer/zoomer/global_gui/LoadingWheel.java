package arvolear.zoomer.zoomer.global_gui;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

public class LoadingWheel extends FrameLayout
{
    private AppCompatActivity activity;

    private int color;

    private FrameLayout loadLayout;

    private ProgressBar progressBar;

    public LoadingWheel(AppCompatActivity activity, int color)
    {
        super(activity);

        this.activity = activity;
        this.loadLayout = activity.findViewById(R.id.loadLayout);
        this.color = color;

        init();
    }

    @SuppressWarnings("deprecation")
    private void init()
    {
        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.END;

        progressBar = new ProgressBar(activity);
        progressBar.setLayoutParams(LP0);
        progressBar.setPadding(0, 0, 0, 20);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.MODULATE));
        }
        else
        {
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }

        progressBar.setIndeterminate(true);

        addView(progressBar);
    }

    public void show()
    {
        loadLayout.addView(this);
    }

    public void hide()
    {
        loadLayout.removeView(this);
    }
}
