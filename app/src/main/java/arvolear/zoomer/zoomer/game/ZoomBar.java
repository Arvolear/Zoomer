package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class ZoomBar extends FrameLayout
{
    private AppCompatActivity activity;

    private Bitmap barBoundsBitmap;
    private Bitmap barProgressBitmap;

    private FrameLayout progressBarLayout;

    private String path;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private double maxProgress;
    private int currentProgress;

    private ImageView barBounds;
    private ImageView barProgress;

    private ClipDrawable barCurrent;

    public ZoomBar(AppCompatActivity activity, String path, double currentZoom)
    {
        super(activity);

        this.activity = activity;
        this.path = path;

        progressBarLayout = activity.findViewById(R.id.progressBarLayout);
        progressBarLayout.addView(this);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        this.maxProgress = currentZoom;
        this.currentProgress = (int) ((maxProgress - (int) maxProgress) * 10000.0);

        init();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        assetsLoader.loadBitmapFromAssets(1, path + "/1.png", false, true);
        barBoundsBitmap = tree.get(0);
        barProgressBitmap = tree.get(1);

        progressBarLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                LP0.gravity = Gravity.CENTER;

                barBounds = new ImageView(activity);
                barBounds.setLayoutParams(LP0);
                barBounds.setImageBitmap(barBoundsBitmap);
                barBounds.setPadding(progressBarLayout.getWidth() / 4, 0, progressBarLayout.getWidth() / 4, 0);
                barBounds.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                barBounds.setAdjustViewBounds(true);

                barCurrent = new ClipDrawable(new BitmapDrawable(activity.getResources(), barProgressBitmap), Gravity.START, ClipDrawable.HORIZONTAL);
                barCurrent.setLevel(currentProgress);

                barProgress = new ImageView(activity);
                barProgress.setLayoutParams(LP0);
                barProgress.setImageDrawable(barCurrent);
                barProgress.setPadding(progressBarLayout.getWidth() / 4, 0, progressBarLayout.getWidth() / 4, 0);
                barProgress.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                barProgress.setAdjustViewBounds(true);

                addView(barBounds);
                addView(barProgress);

                barCurrent.setLevel(currentProgress);
            }
        });
    }

    public void increaseProgress(double maxZoom)
    {
        if ((int) maxZoom - (int) maxProgress >= 1)
        {
            if (barProgress.getAnimation() == null || barProgress.getAnimation().hasEnded())
            {
                barProgress.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.little_jump));
                barBounds.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.little_jump));
            }
        }

        maxProgress = maxZoom;
        currentProgress = (int) ((maxProgress - (int) maxProgress) * 10000.0);

        barCurrent.setLevel(currentProgress);
    }

    public void clear()
    {
        maxProgress = 0.0;
        currentProgress = 0;
        barCurrent.setLevel(currentProgress);

        progressBarLayout.removeView(this);
        progressBarLayout.addView(this);
    }

    public void show()
    {
        barProgress.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_bar_show));
        barBounds.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_bar_show));
    }

    public void hide()
    {
        barProgress.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_bar_hide));
        barBounds.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_bar_hide));
    }

    public void leave()
    {
        barProgress.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_bar_leave));
        barBounds.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_zoom_bar_leave));
    }
}
