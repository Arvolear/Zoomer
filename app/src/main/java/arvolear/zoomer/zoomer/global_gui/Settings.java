package arvolear.zoomer.zoomer.global_gui;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class Settings extends FrameLayout
{
    private AppCompatActivity activity;
    private OnClickListener controller;

    private boolean displayNoOffset = false;
    private boolean attention = false;

    private Bitmap settingsBitmap;

    private String path;

    private FrameLayout notchLayout;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private ImageView settingsImage;

    public Settings(AppCompatActivity activity, OnClickListener controller, String path)
    {
        super(activity);

        this.activity = activity;
        this.controller = controller;
        this.path = path;

        notchLayout = activity.findViewById(R.id.notchLayout);
        notchLayout.addView(this);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        init();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        settingsBitmap = tree.get(0);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.END;

        settingsImage = new ImageView(activity);
        settingsImage.setLayoutParams(LP0);
        settingsImage.setImageBitmap(settingsBitmap);
        settingsImage.setAdjustViewBounds(true);
        settingsImage.setId(generateViewId());
        settingsImage.setOnClickListener(controller);
        settingsImage.setSoundEffectsEnabled(false);

        addView(settingsImage);
    }

    public void displayNoOffset()
    {
        displayNoOffset = true;
    }

    public void setAttention()
    {
        attention = true;

        if (!displayNoOffset)
        {
            Animation dummyOffset = new AlphaAnimation(1.0f, 1.0f);
            dummyOffset.setDuration(1400);

            dummyOffset.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    settingsImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_attention));
                }
            });

            settingsImage.startAnimation(dummyOffset);
        }
        else
        {
            settingsImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_attention));
        }
    }

    public void setNormal()
    {
        if (settingsImage.getAnimation() != null && attention)
        {
            settingsImage.getAnimation().setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {
                    attention = false;
                    settingsImage.clearAnimation();
                }
            });
        }
        else
        {
            attention = false;
        }
    }

    public void clear()
    {
        setNormal();

        removeView(settingsImage);
        addView(settingsImage);

        notchLayout.removeView(this);
        notchLayout.addView(this);
    }

    public void show()
    {
        if (!attention)
        {
            settingsImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_show));
        }
    }

    public void hide()
    {
        if (!attention)
        {
            settingsImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_hide));
        }
    }

    public void leave()
    {
        settingsImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_leave));
    }

    public int getGearId()
    {
        return settingsImage.getId();
    }
}
