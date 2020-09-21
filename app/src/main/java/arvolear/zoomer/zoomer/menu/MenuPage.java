package arvolear.zoomer.zoomer.menu;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

import static android.view.View.generateViewId;

public class MenuPage
{
    private AppCompatActivity activity;
    private View.OnClickListener controller;

    private LinearLayout centralLayout;
    private LinearLayout signLayout;
    private FrameLayout buttonsLayout;

    private TextView sign;
    private Button playButton;
    private Button marketButton;

    public MenuPage(AppCompatActivity activity, View.OnClickListener controller)
    {
        this.activity = activity;
        this.controller = controller;

        centralLayout = activity.findViewById(R.id.centralLayout);
        signLayout = activity.findViewById(R.id.signsLayout);
        buttonsLayout = activity.findViewById(R.id.buttonsLayout);

        init();
    }

    private void init()
    {
        centralLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                LinearLayout.LayoutParams LP0 = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP0.gravity = Gravity.CENTER;

                sign = new TextView(activity);
                sign.setLayoutParams(LP0);
                sign.setGravity(Gravity.CENTER);
                sign.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                sign.setText("Zoomer");
                sign.setTextSize(TypedValue.COMPLEX_UNIT_PX, centralLayout.getWidth() / 7.35f);
                sign.setTextColor(activity.getResources().getColor(R.color.light_text));
                sign.setTransformationMethod(null);

                FrameLayout.LayoutParams LP1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP1.gravity = Gravity.CENTER | Gravity.TOP;

                playButton = new Button(activity);
                playButton.setLayoutParams(LP1);
                playButton.setText("START ZOOMING");
                playButton.setBackgroundColor(Color.TRANSPARENT);
                playButton.setTextColor(activity.getResources().getColor(R.color.white_text));
                playButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, centralLayout.getHeight() / 27.5f);
                playButton.setOnClickListener(controller);
                playButton.setId(generateViewId());
                playButton.setSoundEffectsEnabled(false);

                FrameLayout.LayoutParams LP2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                LP2.gravity = Gravity.CENTER;

                marketButton = new Button(activity);
                marketButton.setLayoutParams(LP2);
                marketButton.setText("MARKET");
                marketButton.setBackgroundColor(Color.TRANSPARENT);
                marketButton.setTextColor(activity.getResources().getColor(R.color.white_text));
                marketButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, centralLayout.getHeight() / 27.5f);
                marketButton.setOnClickListener(controller);
                marketButton.setId(generateViewId());
                marketButton.setSoundEffectsEnabled(false);

                configureFonts();
                configureAnimations();

                signLayout.addView(sign);
                buttonsLayout.addView(playButton);
                buttonsLayout.addView(marketButton);
            }
        });
    }

    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        playButton.setTypeface(face, Typeface.BOLD);
        marketButton.setTypeface(face, Typeface.BOLD);

        sign.setTypeface(face);
        sign.setShadowLayer(3.0f, 7.0f, 7.0f, activity.getResources().getColor(R.color.dark_shadow));
    }

    private void configureAnimations()
    {
        playButton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
        marketButton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
    }

    public void disableButtons()
    {
        playButton.setEnabled(false);
        marketButton.setEnabled(false);
    }

    public void enableButtons()
    {
        if (playButton != null && marketButton != null)
        {
            playButton.setEnabled(true);
            marketButton.setEnabled(true);
        }
    }

    public int getPlayId()
    {
        return playButton.getId();
    }

    public int getMarketId()
    {
        return marketButton.getId();
    }

    public void show()
    {
        centralLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.menu_central_show));
    }

    public void hide()
    {
        centralLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.menu_central_hide));
    }
}
