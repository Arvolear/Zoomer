package arvolear.zoomer.zoomer.global_gui;

import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.custom_animations.PauseScaleAnimation;
import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

abstract public class BackgroundSetter
{
    protected AppCompatActivity activity;

    protected GameConfigurator configurator;

    protected String directory;

    protected ImageView backgroundImage;

    protected TreeMap<Integer, Bitmap> backgroundTree;
    protected ArrayList<PauseScaleAnimation> animations;

    protected boolean stopped;

    protected int desiredWidth;
    protected int widthOffset;

    protected final int duration;
    protected final int offset;
    protected int currentAnimation;

    protected Random random;

    protected AssetsLoader assetsLoader;

    public BackgroundSetter(AppCompatActivity activity, String directory, int offset, int duration)
    {
        this.activity = activity;
        this.configurator = new GameConfigurator(activity);
        this.directory = directory;
        this.duration = duration;
        this.offset = offset;

        stopped = false;

        backgroundTree = new TreeMap<>();
        animations = new ArrayList<>();

        assetsLoader = new AssetsLoader(activity, backgroundTree);

        backgroundImage = activity.findViewById(R.id.backgroundImage);

        random = new Random();
    }

    protected void init()
    {
        String path = directory + "/" + 0 + ".jpg";

        assetsLoader.loadBitmapFromAssets(0, path, false, true);
        final Bitmap currentBitmap = configurator.colorBitmap(backgroundTree.get(0), (int)configurator.getMaxZoom());

        desiredWidth = currentBitmap.getWidth();
        widthOffset = 0;

        backgroundImage.post(new Runnable()
        {
            @Override
            public void run()
            {
                while (backgroundImage.getWidth() == 0)
                {
                    try
                    {
                        wait(50);
                    }
                    catch (Exception ex)
                    {
                    }
                }

                float aspectRatio = (float) backgroundImage.getWidth() / (float) backgroundImage.getHeight();

                desiredWidth = (int)(currentBitmap.getHeight() * aspectRatio);
                desiredWidth = Math.min(desiredWidth, currentBitmap.getWidth());

                widthOffset = (currentBitmap.getWidth() - desiredWidth) / 2;

                setBackground(currentBitmap);
            }
        });
    }

    protected void configureAnimations()
    {
        currentAnimation = 0;

        for (int i = 0; i < animations.size(); i++)
        {
            final int index = i;
            Animation anim = animations.get(i);

            anim.setAnimationListener(new Animation.AnimationListener()
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
                    if (!stopped)
                    {
                        backgroundImage.startAnimation(animations.get((index + 1) % animations.size()));
                        currentAnimation = (currentAnimation + 1) % animations.size();
                    }
                }
            });
        }
    }

    private void setBackground(Bitmap newBitmap)
    {
        if (newBitmap != null)
        {
            while (desiredWidth == 0)
            {
                try
                {
                    wait(50);
                }
                catch (Exception ex)
                {
                }
            }

            final Bitmap backgroundBitmap = Bitmap.createBitmap(newBitmap, widthOffset, 0, desiredWidth, newBitmap.getHeight());

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    backgroundImage.setImageBitmap(backgroundBitmap);
                }
            });
        }
    }

    public void pause()
    {
        if (currentAnimation >= 0)
        {
            animations.get(currentAnimation).pause();
        }
    }

    public void resume()
    {
        if (currentAnimation >= 0)
        {
            animations.get(currentAnimation).resume();
        }
    }

    public void update()
    {
        configurator.init();

        Bitmap currentBitmap = configurator.colorBitmap(backgroundTree.get(0), (int)configurator.getMaxZoom());
        setBackground(currentBitmap);
    }

    public void start()
    {
        stopped = false;

        int randomAnim = 1 + random.nextInt(animations.size() - 1);

        if (randomAnim % 2 == 1)
        {
            randomAnim--;
        }

        currentAnimation = randomAnim;
        backgroundImage.startAnimation(animations.get(currentAnimation));
    }

    public void stop()
    {
        stopped = true;

        resume();
        backgroundImage.clearAnimation();
    }
}
