package arvolear.zoomer.zoomer.menu;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class SoundIcon extends FrameLayout
{
    private AppCompatActivity activity;
    private OnClickListener controller;

    private boolean turnSoundOn;

    private Bitmap soundOnBitmap;
    private Bitmap soundOffBitmap;

    private String path;

    private FrameLayout notchLayout;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private ImageView soundImage;

    public SoundIcon(AppCompatActivity activity, OnClickListener controller, String path, boolean turnSoundOn)
    {
        super(activity);

        this.activity = activity;
        this.controller = controller;
        this.turnSoundOn = turnSoundOn;
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
        assetsLoader.loadBitmapFromAssets(1, path + "/1.png", false, true);
        soundOnBitmap = tree.get(0);
        soundOffBitmap = tree.get(1);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.START;
        LP0.topMargin = 10;
        LP0.leftMargin = 10;

        soundImage = new ImageView(activity);
        soundImage.setLayoutParams(LP0);

        if (turnSoundOn)
        {
            soundImage.setImageBitmap(soundOnBitmap);
        }
        else
        {
            soundImage.setImageBitmap(soundOffBitmap);
        }

        soundImage.setAdjustViewBounds(true);
        soundImage.setOnClickListener(controller);
        soundImage.setId(generateViewId());
        soundImage.setSoundEffectsEnabled(false);

        addView(soundImage);
    }

    public void setTurnSoundOn(boolean turnSoundOn)
    {
        soundImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_show));

        this.turnSoundOn = turnSoundOn;

        if (turnSoundOn)
        {
            soundImage.setImageBitmap(soundOnBitmap);
        }
        else
        {
            soundImage.setImageBitmap(soundOffBitmap);
        }
    }

    public void disableButtons()
    {
        soundImage.setEnabled(false);
    }

    public void enableButtons()
    {
        soundImage.setEnabled(true);
    }

    public int getSoundId()
    {
        return soundImage.getId();
    }
}
