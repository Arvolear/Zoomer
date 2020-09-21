package arvolear.zoomer.zoomer.game;

import android.content.Intent;
import android.media.MediaPlayer;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.buy.BuyDisplayer;
import arvolear.zoomer.zoomer.buy.BuyElement;
import arvolear.zoomer.zoomer.buy.Buyer;
import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.global_gui.LoadingWheel;
import arvolear.zoomer.zoomer.global_gui.Settings;
import arvolear.zoomer.zoomer.market.MarketActivity;
import arvolear.zoomer.zoomer.menu.MenuController;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class GameController extends ScaleGestureDetector.SimpleOnScaleGestureListener implements View.OnTouchListener, View.OnClickListener, MediaPlayer.OnCompletionListener
{
    private AppCompatActivity activity;

    private SoundsPlayer soundsPlayer;

    private boolean pauseMusic = true;
    private boolean initMusic = true;
    private boolean turnSoundOn;
    private int curMusicIndex;

    private InterstitialAd afterEpochAdd;
    private RewardedAd coinsX2Add;

    private GameConfigurator configurator;

    private Buyer buyer;
    private BuyDisplayer buyDisplayer;

    private Fractal fractal;
    private CoinCollector coinCollector;
    private BoosterShower boosterShower;
    private CoinSpawner coinSpawner;
    private ZoomDisplayer zoomDisplayer;
    private Settings settings;
    private ZoomBar zoomBar;
    private GameMenu gameMenu;
    private EpochMenu epochMenu;
    private PopUpItem popUpItem;
    private Black blackScreen;
    private LoadingWheel loadingWheel;

    private boolean buyShown = false;
    private boolean popUpShown = false;
    private boolean gameMenuShown = false;
    private boolean epochMenuShown = false;

    private ArrayList<String> musicPaths = new ArrayList<>(Arrays.asList(
            "assets/music/game/crescent.m4a",
            "assets/music/game/blooming.m4a",
            "assets/music/game/entropy.m4a"
    ));

    private boolean epoch = false;

    private ScaleGestureDetector scaleDetector;

    public static final int ASSETS_NUMBER = 925;

    private static final double SCALE_FACTOR_BOUND = 1.75;
    private static final double SENSITIVITY = 8.0;

    private static final double INIT_SENSITIVITY_DECAY = 0.45;
    private static final double DIFFICULTY = 1.8;

    private static final int START_OFFSET = 20;

    private static final int INC_DIFFICULTY_ON = 10;
    private static final int INC_COINS_ON = 50;

    private double depthSensitivity;

    private double maxZoom;
    private double currentZoom;
    private double prevZoom;

    GameController(AppCompatActivity activity)
    {
        this.activity = activity;

        soundsPlayer = new SoundsPlayer(activity);

        configurator = new GameConfigurator(activity);

        turnSoundOn = configurator.getDataBaseHelper().isSound();

        maxZoom = configurator.getDataBaseHelper().getMaxZoom();
        initZoom();

        scaleDetector = new ScaleGestureDetector(activity, this);

        coinCollector = new CoinCollector(activity, this, "assets/textures/all/coin", configurator.getDataBaseHelper().getCoins());
        buyDisplayer = new BuyDisplayer(activity, this, "assets/textures/all/buy");

        buyer = new Buyer(activity, this, soundsPlayer, buyDisplayer, coinCollector);

        checkNewDay();
        initAdds();

        fractal = new Fractal(activity, this, "assets/textures/game/mandelbrot", (int) maxZoom, ASSETS_NUMBER);
        boosterShower = new BoosterShower(activity, configurator, "assets/textures/market/boosters/elements");
        coinSpawner = new CoinSpawner(activity, coinCollector, "assets/textures/all/coin");
        zoomDisplayer = new ZoomDisplayer(activity, maxZoom);
        settings = new Settings(activity, this, "assets/textures/all/settings");
        zoomBar = new ZoomBar(activity, "assets/textures/game/progress_bar", maxZoom);
        gameMenu = new GameMenu(activity, this, "assets/textures/all/settings/menu", turnSoundOn);
        epochMenu = new EpochMenu(activity, this, "assets/textures/game/epoch");
        popUpItem = new PopUpItem(activity, "assets/textures/game/pop_up_item");
        blackScreen = new Black(activity, "assets/textures/game/black");
        loadingWheel = new LoadingWheel(activity, activity.getResources().getColor(R.color.dark_text));

        updateSound();
        displayPopUpItem();

        soundsPlayer.play("assets/sounds/all/short_zoom_in.mp3", false);
    }

    private void checkNewDay()
    {
        String day = (String) DateFormat.format("dd.MM.yyyy", new Date());

        if (!day.equals(configurator.getDataBaseHelper().getCurrentDay()))
        {
            configurator.getDataBaseHelper().setCurrentDay(day);
            configurator.getDataBaseHelper().setCoinsAddsAmount(0);
        }
    }

    private void initAdds()
    {
        afterEpochAdd = new InterstitialAd(activity);
        // test afterEpochAdd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        afterEpochAdd.setAdUnitId("ca-app-pub-4757202430610617/4753853521");

        createInterAdd();
        createRewardedAdd();
    }

    private void createRewardedAdd()
    {
        if (configurator.getDataBaseHelper().getCoinsAddsAmount() < MenuController.HOW_MANY_ADDS_PER_DAY)
        {
            // test coinsX2Add = new RewardedAd(activity, "ca-app-pub-3940256099942544/5224354917");
            coinsX2Add = new RewardedAd(activity, "ca-app-pub-4757202430610617/9353639164");

            RewardedAdLoadCallback coinsX2LoadCallback = new RewardedAdLoadCallback()
            {
                @Override
                public void onRewardedAdLoaded()
                {
                    buyDisplayer.setConnectedAdd();
                }

                @Override
                public void onRewardedAdFailedToLoad(int errorCode)
                {
                    buyDisplayer.setDisconnectedAdd();
                }
            };

            coinsX2Add.loadAd(new AdRequest.Builder().build(), coinsX2LoadCallback);
        }
        else
        {
            buyDisplayer.setDisconnectedAdd();
        }
    }

    private void createInterAdd()
    {
        afterEpochAdd.loadAd(new AdRequest.Builder().build());
    }

    private void initZoom()
    {
        prevZoom = maxZoom;
        currentZoom = maxZoom;
        depthSensitivity = getDepthSensitivity(maxZoom);
    }

    private double getDepthSensitivity(double maxZoom)
    {
        maxZoom -= START_OFFSET;
        maxZoom = Math.max(1.0, maxZoom);

        double inc = (int) (maxZoom / INC_DIFFICULTY_ON);
        double base = (double) ((int) (maxZoom) / INC_DIFFICULTY_ON) * INC_DIFFICULTY_ON + (double) (((int) (maxZoom) % INC_DIFFICULTY_ON) / (INC_DIFFICULTY_ON / 2));

        double sensitivityDecay = INIT_SENSITIVITY_DECAY * inc;

        base = configurator.reduceDifficulty(base);

        double decay = Math.pow(base, sensitivityDecay);
        decay = Math.max(decay, 1.0);

        return SENSITIVITY / (decay * DIFFICULTY);
    }

    private void showTutorialPopup()
    {
        popUpShown = true;
        popUpItem.show(true, soundsPlayer);
    }

    public void displayPopUpItem()
    {
        BigInteger actualCoins = new BigInteger(coinCollector.getActualCoins());
        BigInteger potentialCoins = CoinCollector.parseStringCoins(getEpochGain(maxZoom)).add(actualCoins);

        if (popUpItem.isFirstPopUp() || popUpItem.isFirstZoomPopUp() || popUpItem.isCoinsStorePopUp())
        {
            showTutorialPopup();
        }

        if (maxZoom >= 1000.0 && popUpItem.isHorizonPopUp())
        {
            showTutorialPopup();
        }

        if (maxZoom >= 1050.0 && popUpItem.isEasterPopUp())
        {
            showTutorialPopup();
        }

        if (popUpItem.enough(actualCoins))
        {
            if (popUpItem.isEpochPopUp())
            {
                showTutorialPopup();
            }

            settings.setAttention();
            gameMenu.setMarketAttention();
        }

        if (popUpItem.enough(potentialCoins))
        {
            if (popUpItem.isBoosterPopUp())
            {
                showTutorialPopup();
            }
            else if (popUpItem.canPlayAnimation())
            {
                popUpItem.show(false, soundsPlayer);
            }

            settings.setAttention();
            gameMenu.setEpochAttention();

            epochMenu.setAttention();
        }
    }

    private String getEpochGain(double maxZoom)
    {
        BigInteger coins = new BigInteger(String.valueOf((int) (maxZoom * 10)));
        BigInteger letterMul = BigInteger.TEN;

        int power = (int) (maxZoom / INC_COINS_ON) * 2;

        letterMul = letterMul.pow(power);
        coins = coins.multiply(letterMul);

        return CoinCollector.parseCoins(coins);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {
        double scaleFactor = 0.0;
        boolean toMaxZoom = false;

        if (detector.getScaleFactor() > 1.0)
        {
            if ((int) currentZoom < (int) maxZoom)
            {
                scaleFactor = (detector.getScaleFactor() - 1.0) * SENSITIVITY;
                scaleFactor = Math.min(Math.min(SCALE_FACTOR_BOUND, scaleFactor), maxZoom - currentZoom);
            }
            else
            {
                scaleFactor = (detector.getScaleFactor() - 1.0) * depthSensitivity;
                scaleFactor = Math.min(SCALE_FACTOR_BOUND, scaleFactor);

                toMaxZoom = true;
            }
        }
        else if (detector.getScaleFactor() < 1.0)
        {
            scaleFactor = (-(detector.getPreviousSpan() / detector.getCurrentSpan()) + 1.0) * SENSITIVITY;
            scaleFactor = Math.max(Math.max(-SCALE_FACTOR_BOUND, scaleFactor), 0.0 - currentZoom);
        }

        if (fractal.canShow((int) (currentZoom + scaleFactor) - (int) prevZoom))
        {
            currentZoom += scaleFactor;

            if (Math.abs((int) currentZoom - (int) prevZoom) >= 1)
            {
                fractal.show((int) currentZoom - (int) prevZoom);
                prevZoom = currentZoom;
            }

            if (toMaxZoom)
            {
                maxZoom += scaleFactor;
                currentZoom = maxZoom;

                zoomDisplayer.setZoomText(maxZoom);
                zoomBar.increaseProgress(maxZoom);

                depthSensitivity = getDepthSensitivity(maxZoom);
                displayPopUpItem();
            }
        }

        return true;
    }

    private void showRewardVideo()
    {
        if (coinsX2Add != null && coinsX2Add.isLoaded())
        {
            RewardedAdCallback coin2XRewardCallBack = new RewardedAdCallback()
            {
                @Override
                public void onRewardedAdClosed()
                {
                    createRewardedAdd();
                    buyDisplayer.enableButtons();
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward)
                {
                    Animation jump = AnimationUtils.loadAnimation(activity, R.anim.coin_collector_hidden_add);

                    BigInteger coins = new BigInteger(coinCollector.getActualCoins());
                    coinCollector.setActualCoins(coins.multiply(new BigInteger(String.valueOf(reward.getAmount()))).toString(), jump);
                }
            };

            coinsX2Add.show(activity, coin2XRewardCallBack);
        }
    }

    private void startAgain()
    {
        blackScreen.hide();

        epochMenu.clear();
        gameMenu.clear();
        fractal.clear();
        zoomDisplayer.clear();
        settings.clear();
        zoomBar.clear();
        popUpItem.clear();

        String coins = getEpochGain(maxZoom);
        String toAddStr = "";

        for (int i = 0; i < Math.min(2, coins.length()); i++)
        {
            if (coins.charAt(i) == '.')
            {
                break;
            }

            toAddStr += coins.charAt(i); // 2 times only
        }

        int toAdd = Integer.parseInt(toAddStr);
        toAdd = Math.min(20, toAdd);

        coinSpawner.spawn(toAdd, 100, soundsPlayer);
        coinCollector.addCoins(coins);

        maxZoom = 0.0;
        initZoom();

        Animation scale = new ScaleAnimation(10.0f, 1.0f, 10.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setFillAfter(true);
        scale.setDuration(1500);

        scale.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                soundsPlayer.play("assets/sounds/game/epoch_new.mp3", false);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                epoch = false;
                epochMenuShown = false;

                if (MenuController.getMusicPlayer() != null)
                {
                    MenuController.getMusicPlayer().setVolume(0.5f, 0.5f);
                }

                displayPopUpItem();
            }
        });

        fractal.startAnimation(scale);
    }

    private void epoch()
    {
        epochMenu.epoch(soundsPlayer).setAnimationListener(new Animation.AnimationListener()
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
                blackScreen.show();

                afterEpochAdd.setAdListener(new AdListener()
                {
                    @Override
                    public void onAdClosed()
                    {
                        createInterAdd();
                        startAgain();
                    }
                });

                if (afterEpochAdd.isLoaded())
                {
                    afterEpochAdd.show();
                }
                else
                {
                    startAgain();
                }
            }
        });
    }

    private void updateSound()
    {
        configurator.getDataBaseHelper().setSound(turnSoundOn);

        if (MenuController.getMusicPlayer() != null)
        {
            MenuController.getMusicPlayer().setTurnSoundOn(turnSoundOn);
        }

        soundsPlayer.setTurnSoundOn(turnSoundOn);
    }

    @Override
    public void onClick(View v)
    {
        if (epoch)
        {
            return;
        }

        if (buyShown)
        {
            if (v.getId() == buyDisplayer.getDummyId())
            {
                // do nothing
            }
            else if (v.getId() == buyDisplayer.getVideoId())
            {
                String actualCoins = coinCollector.getActualCoins();

                if (actualCoins.equals("0"))
                {
                    soundsPlayer.play("assets/sounds/market/not_enough_money.mp3", false);
                    Toast.makeText(activity, "You have 0 coins", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    buyDisplayer.disableButtons();
                    showRewardVideo();
                }
            }
            else
            {
                boolean buy = false;

                ArrayList<BuyElement> buyElements = buyDisplayer.getElements();

                for (BuyElement element : buyElements)
                {
                    if (v.getId() == element.getBuyId())
                    {
                        buy = true;

                        String actualCoins = coinCollector.getActualCoins();

                        if (actualCoins.equals("0"))
                        {
                            soundsPlayer.play("assets/sounds/market/not_enough_money.mp3", false);
                            Toast.makeText(activity, "You have 0 coins", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            soundsPlayer.play("assets/sounds/all/button_press.mp3", false);
                            buyer.purchase(element.getSku());
                        }

                        break;
                    }
                }

                if (!buy && buyDisplayer.canPlayAnimation())
                {
                    buyShown = false;

                    if (MenuController.getMusicPlayer() != null)
                    {
                        MenuController.getMusicPlayer().setVolume(0.5f, 0.5f);
                    }

                    soundsPlayer.play("assets/sounds/all/hide_menu.mp3", false);

                    fractal.deBlur(700);

                    coinCollector.show();
                    boosterShower.show();
                    zoomDisplayer.show();
                    buyDisplayer.hide();

                    displayPopUpItem();
                }
            }
        }
        else if (popUpShown)
        {
            if (popUpItem.canPlayAnimation())
            {
                popUpShown = false;

                popUpItem.hide(soundsPlayer);
                displayPopUpItem();
            }
        }
        else if (epochMenuShown)
        {
            if (v.getId() == epochMenu.getDummyId())
            {
                // do nothing
            }
            else if (v.getId() == epochMenu.getEpochId())
            {
                if (epochMenu.canPlayAnimation())
                {
                    epoch = true;

                    epochMenu.disableButtons();

                    epoch();
                }
            }
            else
            {
                if (epochMenu.canPlayAnimation())
                {
                    epochMenuShown = false;

                    if (MenuController.getMusicPlayer() != null)
                    {
                        MenuController.getMusicPlayer().setVolume(0.5f, 0.5f);
                    }

                    soundsPlayer.play("assets/sounds/all/hide_menu.mp3", false);

                    settings.hide();
                    zoomBar.show();
                    zoomDisplayer.show();

                    fractal.deBlur(700);

                    epochMenu.hide();
                }
            }
        }
        else if (gameMenuShown)
        {
            if (v.getId() == gameMenu.getDummyId())
            {
                // do nothing
            }
            else if (v.getId() == gameMenu.getSoundId())
            {
                turnSoundOn = !turnSoundOn;

                if (MenuController.getMusicPlayer() != null)
                {
                    MenuController.getMusicPlayer().setVolume(0.25f, 0.25f);
                }

                updateSound();

                gameMenu.setTurnSoundOn(turnSoundOn);
                soundsPlayer.play("assets/sounds/all/sound_press.mp3", false, 0.5f, 0.5f);
            }
            else if (v.getId() == gameMenu.getHomeId())
            {
                gameMenu.disableButtons();

                soundsPlayer.play("assets/sounds/all/button_press.mp3", false);

                pauseMusic = false;

                gameMenu.leave();
                settings.leave();
                zoomBar.leave();
                zoomDisplayer.leave();
                coinCollector.leave();
                boosterShower.leave();

                leave();
            }
            else if (v.getId() == gameMenu.getMarketId())
            {
                gameMenu.disableButtons();

                soundsPlayer.play("assets/sounds/all/button_press.mp3", false);

                pauseMusic = false;

                loadingWheel.show();

                Intent marketIntent = new Intent(activity, MarketActivity.class);
                marketIntent.putExtra("restartPlayer", true);

                activity.startActivity(marketIntent);
                activity.overridePendingTransition(R.anim.any_to_main_scale_down, R.anim.game_to_market_scale_up);

                activity.finish();
            }
            else if (v.getId() == gameMenu.getEpochId())
            {
                if (epochMenu.canPlayAnimation())
                {
                    epochMenuShown = true;
                    gameMenuShown = false;

                    soundsPlayer.play("assets/sounds/all/next_menu.mp3", false);

                    settings.show();
                    gameMenu.hide();

                    epochMenu.setEpochGain(getEpochGain(maxZoom));
                    epochMenu.show();
                }
            }
            else
            {
                if (gameMenu.canPlayAnimation())
                {
                    gameMenuShown = false;

                    if (MenuController.getMusicPlayer() != null)
                    {
                        MenuController.getMusicPlayer().setVolume(0.5f, 0.5f);
                    }

                    soundsPlayer.play("assets/sounds/all/hide_menu.mp3", false);

                    settings.hide();
                    zoomBar.show();
                    zoomDisplayer.show();

                    fractal.deBlur(700);

                    gameMenu.hide();
                }
            }
        }
        else if (v.getId() == settings.getGearId())
        {
            if (gameMenu.canPlayAnimation() && epochMenu.canPlayAnimation() && buyDisplayer.canPlayAnimation())
            {
                gameMenuShown = true;

                if (MenuController.getMusicPlayer() != null)
                {
                    MenuController.getMusicPlayer().setVolume(0.25f, 0.25f);
                }

                soundsPlayer.play("assets/sounds/all/show_menu.mp3", false);

                settings.show();
                settings.setNormal();
                zoomBar.hide();
                zoomDisplayer.hide();

                fractal.blur(1.5f, 450);

                gameMenu.show();
            }
        }
        else if (v.getId() == coinCollector.getCoinsId())
        {
            if (buyDisplayer.canPlayAnimation() && gameMenu.canPlayAnimation() && coinSpawner.canPlayAnimation())
            {
                buyShown = true;

                if (MenuController.getMusicPlayer() != null)
                {
                    MenuController.getMusicPlayer().setVolume(0.25f, 0.25f);
                }

                soundsPlayer.play("assets/sounds/all/show_menu.mp3", false);

                fractal.blur(1.5f, 450);

                buyDisplayer.show();
                coinCollector.hide();
                boosterShower.hide();
                zoomDisplayer.hide();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (epoch)
        {
            return false;
        }

        if (!popUpShown && !gameMenuShown && !epochMenuShown && !buyShown && fractal.isDeBlurred())
        {
//            if (fractal.canShow(1))
//            {
//                maxZoom += 1.0;
//                fractal.show(1);
//                zoomDisplayer.setZoomText(maxZoom);
//                zoomBar.increaseProgress(0.1234);
//                displayPopUpItem();
//            }

            return scaleDetector.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        curMusicIndex = (curMusicIndex + 1) % musicPaths.size();

        if (MenuController.getMusicPlayer() != null)
        {
            MenuController.getMusicPlayer().playFadeIn(musicPaths.get(curMusicIndex), false, 1000);
        }
    }

    public void leave()
    {
        MenuController.setRestartMusic(true);

        activity.finish();
        activity.overridePendingTransition(R.anim.menu_to_any_scale_down, R.anim.menu_to_any_alpha_down);
    }

    public void resume()
    {
        if (MenuController.getMusicPlayer() != null)
        {
            MenuController.getMusicPlayer().resume(false);
        }

        loadingWheel.hide();
    }

    public void start()
    {
        SoundsPlayer player = MenuController.getMusicPlayer();

        if (initMusic)
        {
            curMusicIndex = 0;
            Collections.shuffle(musicPaths);

            if (player != null)
            {
                player.playFadeIn(musicPaths.get(curMusicIndex), false, 2000);
            }

            initMusic = false;
        }

        if (player != null)
        {
            player.setOnEndListener(this);
        }
    }

    public void pause()
    {
        configurator.getDataBaseHelper().setMaxZoom(maxZoom);
        configurator.getDataBaseHelper().setCoins(coinCollector.getActualCoins());

        if (pauseMusic)
        {
            if (MenuController.getMusicPlayer() != null)
            {
                MenuController.getMusicPlayer().pause();
            }
        }
    }

    public void stop()
    {
        if (MenuController.getMusicPlayer() != null)
        {
            MenuController.getMusicPlayer().clearEndListener();
        }

        soundsPlayer.stop();
    }

    public void fullIn()
    {
        popUpItem.displayNoOffset();
        settings.displayNoOffset();
    }
}
