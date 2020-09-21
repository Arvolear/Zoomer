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

public class HelpIcon extends FrameLayout
{
    private AppCompatActivity activity;
    private OnClickListener controller;

    private Bitmap helpBitmap;

    private String path;

    private FrameLayout notchLayout;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private ImageView helpImage;

    public HelpIcon(AppCompatActivity activity, OnClickListener controller, String path)
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
        helpBitmap = tree.get(0);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.END;
        LP0.topMargin = 10;
        LP0.rightMargin = 10;

        helpImage = new ImageView(activity);
        helpImage.setLayoutParams(LP0);
        helpImage.setImageBitmap(helpBitmap);
        helpImage.setAdjustViewBounds(true);
        helpImage.setId(generateViewId());
        helpImage.setOnClickListener(controller);
        helpImage.setSoundEffectsEnabled(false);

        addView(helpImage);
    }

    public void show()
    {
        helpImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.menu_help_show));
    }

    public void hide()
    {
        helpImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.menu_help_hide));
    }

    public void disableButtons()
    {
        helpImage.setEnabled(false);
    }

    public void enableButtons()
    {
        helpImage.setEnabled(true);
    }

    public int getHelpId()
    {
        return helpImage.getId();
    }
}
