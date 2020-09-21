package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class BoosterShower extends FrameLayout
{
    private AppCompatActivity activity;
    private GameConfigurator configurator;

    private Bitmap boosterBitmap;

    private String path;

    private FrameLayout notch2Layout;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private ImageView boosterImage;

    public BoosterShower(AppCompatActivity activity, GameConfigurator configurator, String path)
    {
        super(activity);

        this.activity = activity;
        this.configurator = configurator;
        this.path = path;

        notch2Layout = activity.findViewById(R.id.notch2Layout);
        notch2Layout.addView(this);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        init();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/" + configurator.getBoosterIndex() + ".png", false, true);
        boosterBitmap = tree.get(0);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.START;

        boosterImage = new ImageView(activity);
        boosterImage.setLayoutParams(LP0);
        boosterImage.setImageBitmap(boosterBitmap);
        boosterImage.setAdjustViewBounds(true);

        addView(boosterImage);
    }

    public void show()
    {
        boosterImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_show));
    }

    public void hide()
    {
        boosterImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_hide));
    }

    public void leave()
    {
        boosterImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.settings_leave));
    }
}
