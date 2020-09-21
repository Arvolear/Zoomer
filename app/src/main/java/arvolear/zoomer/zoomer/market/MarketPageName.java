package arvolear.zoomer.zoomer.market;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

public class MarketPageName extends FrameLayout
{
    private AppCompatActivity activity;

    private String name;

    private TextView pageText;

    public MarketPageName(AppCompatActivity activity, String name)
    {
        super(activity);
        setId(generateViewId());
        setSoundEffectsEnabled(false);

        this.activity = activity;
        this.name = name;

        init();
    }

    private void init()
    {
        post(new Runnable()
        {
            @Override
            public void run()
            {
                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                LP0.gravity = Gravity.CENTER;

                pageText = new TextView(activity);
                pageText.setLayoutParams(LP0);
                pageText.setBackgroundColor(Color.TRANSPARENT);
                pageText.setGravity(Gravity.CENTER);
                pageText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                pageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getHeight() / 2.0f);
                pageText.setTextColor(activity.getResources().getColor(R.color.light_text));
                pageText.setText(name);
                pageText.setTransformationMethod(null);

                configureFonts();

                addView(pageText);
            }
        });
    }

    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        pageText.setTypeface(face);
        pageText.setShadowLayer(3.0f, 7.0f, 7.0f, activity.getResources().getColor(R.color.dark_shadow));
    }
}
