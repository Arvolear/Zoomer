package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.market.MarketController;
import arvolear.zoomer.zoomer.menu.MenuController;
import arvolear.zoomer.zoomer.utility.AssetsLoader;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class PopUpItem extends FrameLayout
{
    private AppCompatActivity activity;

    private GameConfigurator configurator;

    private TreeSet<BigInteger> elementsValues;

    private Bitmap firstPopUpBitmap;
    private Bitmap firstZoomPopUpBitmap;
    private Bitmap boosterPopUpBitmap;
    private Bitmap epochPopUpBitmap;
    private Bitmap coinsStoreBitmap;
    private Bitmap horizonBitmap;
    private Bitmap easterBitmap;
    private Bitmap popUpBitmap;

    private boolean firstPopUp;
    private boolean firstZoomPopUp;
    private boolean boosterPopUp;
    private boolean epochPopUp;
    private boolean coinsStorePopUp;
    private boolean horizonPopUp;
    private boolean easterPopUp;

    private boolean displayNoOffset = false;
    private String path;

    private FrameLayout popUpLayout;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private ImageView popUpImage;

    public PopUpItem(AppCompatActivity activity, String path)
    {
        super(activity);

        this.activity = activity;
        this.configurator = new GameConfigurator(activity);
        this.firstPopUp = configurator.getDataBaseHelper().isPopUp("first_popup");
        this.firstZoomPopUp = configurator.getDataBaseHelper().isPopUp("first_zoom_popup");
        this.boosterPopUp = configurator.getDataBaseHelper().isPopUp("booster_popup");
        this.epochPopUp = configurator.getDataBaseHelper().isPopUp("epoch_popup");
        this.coinsStorePopUp = configurator.getDataBaseHelper().isPopUp("coins_store_popup");
        this.horizonPopUp = configurator.getDataBaseHelper().isPopUp("horizon_popup");
        this.easterPopUp = configurator.getDataBaseHelper().isPopUp("easter_popup");
        this.path = path;

        elementsValues = new TreeSet<>();

        popUpLayout = activity.findViewById(R.id.popUpLayout);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        init();
        configureElements();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        assetsLoader.loadBitmapFromAssets(1, path + "/1.png", false, true);
        assetsLoader.loadBitmapFromAssets(2, path + "/2.png", false, true);
        assetsLoader.loadBitmapFromAssets(3, path + "/3.png", false, true);
        assetsLoader.loadBitmapFromAssets(4, path + "/4.png", false, true);
        assetsLoader.loadBitmapFromAssets(5, path + "/5.png", false, true);
        assetsLoader.loadBitmapFromAssets(6, path + "/6.png", false, true);
        assetsLoader.loadBitmapFromAssets(7, path + "/7.png", false, true);

        firstPopUpBitmap = tree.get(0);
        firstZoomPopUpBitmap = tree.get(1);
        boosterPopUpBitmap = tree.get(2);
        epochPopUpBitmap = tree.get(3);
        coinsStoreBitmap = tree.get(4);
        horizonBitmap = tree.get(5);
        easterBitmap = tree.get(6);
        popUpBitmap = tree.get(7);

        popUpImage = new ImageView(activity);
        popUpImage.setAdjustViewBounds(true);

        addView(popUpImage);
    }

    private void configureElements()
    {
        configureSpecific("boosters", MarketController.BOOSTERS_AMOUNT);
        configureSpecific("coloring", MarketController.COLORING_AMOUNT);
        //configureSpecific("music");
    }

    private void configureSpecific(String type, int size)
    {
        for (int i = 0; i < size; i++)
        {
            if (configurator.getDataBaseHelper().getStatus(type, i).equals("locked"))
            {
                String price = getResources().getString(getResources().getIdentifier(type + "_" + i + "_price", "string", activity.getPackageName()));
                BigInteger value = CoinCollector.parseStringCoins(price);

                elementsValues.add(value);
            }
        }
    }

    public boolean enough(BigInteger potentialCoins)
    {
        SortedSet<BigInteger> tail = elementsValues.tailSet(potentialCoins, false);

        if (tail.size() < elementsValues.size())
        {
            elementsValues = new TreeSet<>(tail);

            return true;
        }

        return false;
    }

    public void clear()
    {
        displayNoOffset = false;
        configureElements();

        removeAllViews();
        addView(popUpImage);

        popUpLayout.removeView(this);
    }

    public void show(final boolean showTutorial, final SoundsPlayer soundsPlayer)
    {
        Thread showThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (getAnimation() != null)
                {
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (Exception ex)
                    {
                    }
                }

                popUpLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, popUpLayout.getHeight() / 2 + popUpLayout.getHeight() / 6);

                        if (showTutorial)
                        {
                            MenuController.getMusicPlayer().setVolume(0.25f, 0.25f);
                        }

                        if (showTutorial && firstPopUp)
                        {
                            popUpImage.setImageBitmap(firstPopUpBitmap);
                        }
                        else if (showTutorial && firstZoomPopUp)
                        {
                            popUpImage.setImageBitmap(firstZoomPopUpBitmap);
                        }
                        else if (showTutorial && boosterPopUp)
                        {
                            popUpImage.setImageBitmap(boosterPopUpBitmap);
                        }
                        else if (showTutorial && epochPopUp)
                        {
                            popUpImage.setImageBitmap(epochPopUpBitmap);
                        }
                        else if (showTutorial && coinsStorePopUp)
                        {
                            popUpImage.setImageBitmap(coinsStoreBitmap);
                        }
                        else if (showTutorial && horizonPopUp)
                        {
                            popUpImage.setImageBitmap(horizonBitmap);
                        }
                        else if (showTutorial && easterPopUp)
                        {
                            popUpImage.setImageBitmap(easterBitmap);
                        }
                        else
                        {
                            LP0 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, popUpLayout.getHeight() / 4);

                            popUpImage.setImageBitmap(popUpBitmap);
                        }

                        LP0.gravity = Gravity.CENTER;
                        popUpImage.setLayoutParams(LP0);
                    }
                });

                final AnimationSet set = new AnimationSet(false);
                set.setFillAfter(true);

                Animation scaleUp = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleUp.setDuration(500);

                Animation rotate = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(500);
                rotate.setStartOffset(400);

                if (!displayNoOffset)
                {
                    set.setStartOffset(1400);
                }

                set.addAnimation(scaleUp);
                set.addAnimation(rotate);

                set.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                        Thread offset = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    Thread.sleep(!displayNoOffset ? 1400 : 400);
                                }
                                catch (Exception ex)
                                {
                                }

                                soundsPlayer.play("assets/sounds/game/show_pop_up.mp3", false);
                            }
                        });

                        offset.start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                setAnimation(null);

                                if (!showTutorial || (!firstPopUp && !firstZoomPopUp && !boosterPopUp && !epochPopUp && !coinsStorePopUp && !horizonPopUp && !easterPopUp))
                                {
                                    startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_up_item_hide));
                                    popUpLayout.removeView(PopUpItem.this);
                                }
                                else
                                {
                                    popUpImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_up_item_float));
                                }
                            }
                        });
                    }
                });

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        popUpLayout.addView(PopUpItem.this);
                        startAnimation(set);
                    }
                });
            }
        });

        showThread.start();
    }

    public void hide(final SoundsPlayer soundsPlayer)
    {
        MenuController.getMusicPlayer().setVolume(0.5f, 0.5f);
        soundsPlayer.play("assets/sounds/game/hide_pop_up.mp3", false);

        if (firstPopUp)
        {
            firstPopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("first_popup");
        }
        else if (firstZoomPopUp)
        {
            firstZoomPopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("first_zoom_popup");
        }
        else if (boosterPopUp)
        {
            boosterPopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("booster_popup");
        }
        else if (epochPopUp)
        {
            epochPopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("epoch_popup");
        }
        else if (coinsStorePopUp)
        {
            coinsStorePopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("coins_store_popup");
        }
        else if (horizonPopUp)
        {
            horizonPopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("horizon_popup");
        }
        else if (easterPopUp)
        {
            easterPopUp = false;
            configurator.getDataBaseHelper().setExperiencedPopUp("easter_popup");
        }

        Animation hide = AnimationUtils.loadAnimation(activity, R.anim.pop_up_item_hide);
        hide.setFillAfter(true);

        hide.setAnimationListener(new Animation.AnimationListener()
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
                popUpImage.setAnimation(null);
                popUpLayout.removeView(PopUpItem.this);
            }
        });

        startAnimation(hide);
    }

    public boolean isFirstPopUp()
    {
        return firstPopUp;
    }

    public boolean isFirstZoomPopUp()
    {
        return firstZoomPopUp;
    }

    public boolean isBoosterPopUp()
    {
        return boosterPopUp;
    }

    public boolean isEpochPopUp()
    {
        return epochPopUp;
    }

    public boolean isCoinsStorePopUp()
    {
        return coinsStorePopUp;
    }

    public boolean isHorizonPopUp()
    {
        return horizonPopUp;
    }

    public boolean isEasterPopUp()
    {
        return easterPopUp;
    }

    public void displayNoOffset()
    {
        displayNoOffset = true;
    }

    public boolean canPlayAnimation()
    {
        return getAnimation() == null;
    }
}
