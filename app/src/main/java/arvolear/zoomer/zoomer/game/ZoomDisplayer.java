package arvolear.zoomer.zoomer.game;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

public class ZoomDisplayer extends FrameLayout
{
    private AppCompatActivity activity;

    private FrameLayout notchLayout;

    private double currentZoom;

    private TextView zoomText;

    ZoomDisplayer(AppCompatActivity activity, double currentZoom)
    {
        super(activity);
        setId(generateViewId());

        this.activity = activity;
        this.currentZoom = currentZoom;

        notchLayout = activity.findViewById(R.id.notchLayout);
        notchLayout.addView(this);

        init();
    }

    private void init()
    {
        setClipChildren(false);

        post(new Runnable()
        {
            @Override
            public void run()
            {
                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                LP0.gravity = Gravity.CENTER;

                zoomText = new Button(activity);
                zoomText.setLayoutParams(LP0);
                zoomText.setBackgroundColor(Color.TRANSPARENT);
                zoomText.setGravity(Gravity.CENTER);
                zoomText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                zoomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getHeight() / 2.0f);
                zoomText.setTextColor(activity.getResources().getColor(R.color.dark_text));
                setZoomText(currentZoom);

                configureFonts();

                addView(zoomText);
            }
        });
    }

    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        zoomText.setTypeface(face, Typeface.BOLD);
        zoomText.setShadowLayer(8.0f, 0.0f, 0.0f, activity.getResources().getColor(R.color.light_shadow));
    }

    public void setZoomText(double zoom)
    {
        int zoomI = (int)(zoom * 10);
        currentZoom = zoomI / 10.0;

        zoomText.setText(String.valueOf(currentZoom));
    }

    public void clear()
    {
        currentZoom = 0.0;
        zoomText.setText("0.0");

        removeView(zoomText);
        addView(zoomText);

        notchLayout.removeView(this);
        notchLayout.addView(this);
    }

    public void show()
    {
        zoomText.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_text_show));
    }

    public void hide()
    {
        zoomText.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_text_hide));
    }

    public void leave()
    {
        zoomText.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_text_leave));
    }
}
