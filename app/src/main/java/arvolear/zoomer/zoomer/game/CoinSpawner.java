package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.utility.AssetsLoader;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class CoinSpawner extends FrameLayout
{
    private AppCompatActivity activity;
    private CoinCollector collector;

    private Bitmap coinBitmap;

    private FrameLayout contentLayout;
    private FrameLayout notchLayout;

    private AssetsLoader assetLoader;
    private TreeMap<Integer, Bitmap> tree;

    private Random generator;
    private static final int RANDOM_OFFSET = 150;

    private boolean spawnEnded;

    private String path;

    private int imageHeight;

    private int centerX;
    private int centerY;

    public CoinSpawner(AppCompatActivity activity, CoinCollector collector, String path)
    {
        super(activity);
        setId(generateViewId());

        this.activity = activity;
        this.collector = collector;

        this.path = path;

        notchLayout = activity.findViewById(R.id.notchLayout);
        contentLayout = activity.findViewById(R.id.contentLayout);
        contentLayout.addView(this, 0);

        generator = new Random();

        spawnEnded = true;

        tree = new TreeMap<>();
        assetLoader = new AssetsLoader(activity, tree);

        init();
    }

    private void init()
    {
        assetLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        coinBitmap = tree.get(0);

        notchLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                imageHeight = notchLayout.getHeight();
            }
        });

        contentLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                centerX = contentLayout.getWidth() / 2;
                centerY = contentLayout.getHeight() / 2;
            }
        });
    }

    public void spawn(final int amount, final int time, final SoundsPlayer soundsPlayer)
    {
        Thread spawner = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                spawnEnded = false;

                for (int i = 0; i < amount; i++)
                {
                    int offsetX = generator.nextInt(RANDOM_OFFSET + RANDOM_OFFSET + 1) - RANDOM_OFFSET;
                    int offsetY = generator.nextInt(RANDOM_OFFSET + RANDOM_OFFSET + 1) - RANDOM_OFFSET;

                    FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, imageHeight);
                    LP0.leftMargin = centerX + offsetX - imageHeight / 2;
                    LP0.topMargin = centerY + offsetY - imageHeight / 2;

                    final ImageView coinImage = new ImageView(activity);
                    coinImage.setLayoutParams(LP0);
                    coinImage.setImageBitmap(coinBitmap);
                    coinImage.setAdjustViewBounds(true);

                    final AnimationSet set = new AnimationSet(true);
                    set.setFillAfter(true);

                    set.setAnimationListener(new Animation.AnimationListener()
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
                            if (soundsPlayer.canPlay())
                            {
                                soundsPlayer.play("assets/sounds/game/gain_coin.mp3", false);
                            }

                            post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    removeView(coinImage);
                                    collector.addCoins("0");
                                }
                            });
                        }
                    });

                    Animation showAnim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    showAnim.setDuration(700);

                    Animation transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.ABSOLUTE, -LP0.leftMargin,
                            Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.ABSOLUTE, -LP0.topMargin);
                    transAnim.setDuration(700);
                    transAnim.setStartOffset(700);

                    set.addAnimation(showAnim);
                    set.addAnimation(transAnim);

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            addView(coinImage);
                            coinImage.startAnimation(set);
                        }
                    });

                    try
                    {
                        Thread.sleep(time);
                    }
                    catch (Exception ex)
                    {
                    }
                }

                spawnEnded = true;
            }
        });

        spawner.start();
    }

    public boolean canPlayAnimation()
    {
        return spawnEnded;
    }
}
