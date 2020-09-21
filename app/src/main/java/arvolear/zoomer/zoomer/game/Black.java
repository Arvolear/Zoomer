package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class Black extends FrameLayout
{
    private AppCompatActivity activity;

    private Bitmap blackBitmap;

    private FrameLayout mainLayout;

    private String path;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private ImageView blackImage;

    public Black(AppCompatActivity activity, String path)
    {
        super(activity);

        this.activity = activity;
        this.path = path;

        mainLayout = activity.findViewById(R.id.mainLayout);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        init();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        blackBitmap = tree.get(0);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.CENTER;

        blackImage = new ImageView(activity);
        blackImage.setLayoutParams(LP0);
        blackImage.setImageBitmap(blackBitmap);
        blackImage.setScaleType(ImageView.ScaleType.FIT_XY);

        addView(blackImage);
    }

    public void show()
    {
        mainLayout.addView(this);
    }

    public void hide()
    {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(2000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
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
                mainLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainLayout.removeView(Black.this);
                    }
                });
            }
        });

        blackImage.startAnimation(fadeOut);
    }
}
