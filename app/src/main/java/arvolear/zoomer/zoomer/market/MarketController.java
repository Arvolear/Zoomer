package arvolear.zoomer.zoomer.market;

import android.content.Intent;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.tabs.TabLayout;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.buy.BuyDisplayer;
import arvolear.zoomer.zoomer.buy.BuyElement;
import arvolear.zoomer.zoomer.buy.Buyer;
import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.game.GameActivity;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.global_gui.LoadingWheel;
import arvolear.zoomer.zoomer.global_gui.Settings;
import arvolear.zoomer.zoomer.menu.MenuController;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

import static android.view.View.OVER_SCROLL_NEVER;

public class MarketController implements View.OnClickListener
{
    private AppCompatActivity activity;

    private SoundsPlayer soundsPlayer;

    private boolean pauseMusic = true;
    private boolean restartMusic = true;
    private boolean turnSoundOn;

    private RewardedAd coinsX2Add;

    private Buyer buyer;
    private BuyDisplayer buyDisplayer;

    private GameConfigurator configurator;

    private MarketBackgroundSetter marketBackgroundSetter;
    private CoinCollector coinCollector;
    private Settings settings;
    private MarketMenu marketMenu;
    private LoadingWheel loadingWheel;

    private boolean buyShown = false;
    private boolean marketMenuShown = false;

    public static final int BOOSTERS_AMOUNT = 8;
    public static final int COLORING_AMOUNT = 8;

    public static final int COLORING_BLACK_IND = 4;
    public static final int COLORING_WHITE_IND = 5;
    public static final int COLORING_INVERSE_IND = 6;
    public static final int COLORING_GRADIENT_IND = 7;

    private TabLayout pointsLayout;
    private ArrayList<MarketPage> pages;
    private ViewPager marketPager;

    public MarketController(final AppCompatActivity activity)
    {
        this.activity = activity;

        soundsPlayer = new SoundsPlayer(activity);

        this.configurator = new GameConfigurator(activity);

        turnSoundOn = configurator.getDataBaseHelper().isSound();

        marketPager = activity.findViewById(R.id.marketPager);
        marketPager.setOffscreenPageLimit(2);
        marketPager.setOverScrollMode(OVER_SCROLL_NEVER);

        pages = new ArrayList<>();

        pointsLayout = activity.findViewById(R.id.tabLayout);
        disablePointsClick();

        coinCollector = new CoinCollector(activity, this, "assets/textures/all/coin", configurator.getDataBaseHelper().getCoins());
        buyDisplayer = new BuyDisplayer(activity, this, "assets/textures/all/buy");

        buyer = new Buyer(activity, this, soundsPlayer, buyDisplayer, coinCollector);

        checkNewDay();
        createRewardedAdd();

        marketBackgroundSetter = new MarketBackgroundSetter(activity, "assets/textures/market", 6000, 20000);
        settings = new Settings(activity, this, "assets/textures/all/settings");
        marketMenu = new MarketMenu(activity, this, "assets/textures/all/settings/menu", turnSoundOn);
        loadingWheel = new LoadingWheel(activity, activity.getResources().getColor(R.color.light_text));

        updateSound();
        configurePages();

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

    private void disablePointsClick()
    {
        pointsLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                LinearLayout tabStrip = (LinearLayout)pointsLayout.getChildAt(0);

                for (int i = 0; i < tabStrip.getChildCount(); i++)
                {
                    tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener()
                    {
                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            return true;
                        }
                    });
                }
            }
        });
    }

    private void configurePages()
    {
        MarketPage page0 = new MarketPage(activity, this, soundsPlayer, coinCollector, "Boosters", BOOSTERS_AMOUNT);
        MarketPage page1 = new MarketPage(activity, this, soundsPlayer, coinCollector, "Coloring", COLORING_AMOUNT);

        configurePage(page0);
        configurePage(page1);

        // TODO MarketPage page2 = new MarketPage(activity, this, coinCollector, "Music", MUSIC_AMOUNT);

        marketPager.setAdapter(new MarketPagerAdapter(activity, this));
        pointsLayout.setupWithViewPager(marketPager);
    }

    private void configurePage(MarketPage page)
    {
        for (int j = 0; j < page.getAmount(); j++)
        {
            MarketElement element = new MarketElement(activity, "assets/textures/market", page.getFormattedName(), j, configurator.getDataBaseHelper().getStatus(page.getFormattedName(), j));
            page.addElement(element);
        }

        pages.add(page);
    }

    private void savePages()
    {
        for (int i = 0; i < pages.size(); i++)
        {
            pages.get(i).saveTo(configurator.getDataBaseHelper());
        }
    }

    private void updateElementsToBuy()
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < pages.size(); i++)
                {
                    pages.get(i).displayEnoughToBuy();
                }
            }
        });
    }

    public void update()
    {
        Thread updateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                savePages();
                marketBackgroundSetter.update();

                if (configurator.getDataBaseHelper().isFirstBuy())
                {
                    configurator.getDataBaseHelper().setExperiencedBuy();
                    configurator.getDataBaseHelper().setShowPopUp("coins_store_popup");
                }

                configurator.getDataBaseHelper().setCoins(coinCollector.getActualCoins());
                updateElementsToBuy();
            }
        });

        updateThread.start();
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

                    configurator.getDataBaseHelper().setCoinsAddsAmount(configurator.getDataBaseHelper().getCoinsAddsAmount() + 1);
                }
            };

            coinsX2Add.show(activity, coin2XRewardCallBack);
        }
    }

    public void stop()
    {
        marketBackgroundSetter.resume();
        marketBackgroundSetter.stop();

        soundsPlayer.stop();

        for (MarketPage page : pages)
        {
            page.stop();
        }
    }

    public void pause()
    {
        configurator.getDataBaseHelper().setCoins(coinCollector.getActualCoins());

        savePages();
        marketBackgroundSetter.pause();

        if (pauseMusic)
        {
            if (MenuController.getMusicPlayer() != null)
            {
                MenuController.getMusicPlayer().pause();
            }
        }
    }

    public void resume()
    {
        if (MenuController.getMusicPlayer() != null)
        {
            MenuController.getMusicPlayer().resume(false);
        }

        marketBackgroundSetter.start();
        loadingWheel.hide();

        updateElementsToBuy();
    }

    public void start()
    {
        Intent intent = activity.getIntent();

        if (restartMusic && intent.getBooleanExtra("restartPlayer", false))
        {
            if (MenuController.getMusicPlayer() != null)
            {
                MenuController.getMusicPlayer().playFadeIn("assets/music/all/recollection.m4a", true, 1000, 0.5f, 0.5f);
                MenuController.getMusicPlayer().setVolume(0.5f, 0.5f);

            }

            restartMusic = false;
        }
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

                    pages.get(marketPager.getCurrentItem()).showForCoins();
                    coinCollector.show();
                    buyDisplayer.hide();
                }
            }
        }
        else if (marketMenuShown)
        {
            if (v.getId() == marketMenu.getDummyId())
            {
                // do nothing
            }
            else if (v.getId() == marketMenu.getSoundId())
            {
                turnSoundOn = !turnSoundOn;

                updateSound();

                marketMenu.setTurnSoundOn(turnSoundOn);
                soundsPlayer.play("assets/sounds/all/sound_press.mp3", false, 0.5f, 0.5f);
            }
            else if (v.getId() == marketMenu.getHomeId())
            {
                marketMenu.disableButtons();

                soundsPlayer.play("assets/sounds/all/button_press.mp3", false);

                MenuController.setRestartMusic(false);
                pauseMusic = false;

                marketMenu.leave();
                settings.leave();
                coinCollector.leave();

                leave();
            }
            else if (v.getId() == marketMenu.getGameId())
            {
                marketMenu.disableButtons();

                soundsPlayer.play("assets/sounds/all/button_press.mp3", false);

                pauseMusic = false;

                loadingWheel.show();

                Intent gameIntent = new Intent(activity, GameActivity.class);
                activity.startActivity(gameIntent);
                activity.overridePendingTransition(R.anim.any_to_main_scale_down, R.anim.market_to_game_scale_up);

                activity.finish();
            }
            else
            {
                if (marketMenu.canPlayAnimation())
                {
                    marketMenuShown = false;

                    soundsPlayer.play("assets/sounds/all/hide_menu.mp3", false);

                    settings.hide();
                    pointsLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_points_show));
                    pages.get(marketPager.getCurrentItem()).showForMenu();

                    marketMenu.hide();
                }
            }
        }
        else if (v.getId() == settings.getGearId())
        {
            if (marketMenu.canPlayAnimation() && pages.get(marketPager.getCurrentItem()).canPlayAnimation() && buyDisplayer.canPlayAnimation())
            {
                marketMenuShown = true;

                soundsPlayer.play("assets/sounds/all/show_menu.mp3", false);

                settings.show();
                pointsLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_points_hide));
                pages.get(marketPager.getCurrentItem()).hideForMenu();

                marketMenu.show();
            }
        }
        else if (v.getId() == coinCollector.getCoinsId())
        {
            if (buyDisplayer.canPlayAnimation() && pages.get(marketPager.getCurrentItem()).canPlayAnimation() && marketMenu.canPlayAnimation())
            {
                buyShown = true;

                if (MenuController.getMusicPlayer() != null)
                {
                    MenuController.getMusicPlayer().setVolume(0.25f, 0.25f);
                }

                soundsPlayer.play("assets/sounds/all/show_menu.mp3", false);

                pages.get(marketPager.getCurrentItem()).hideForCoins();
                buyDisplayer.show();
                coinCollector.hide();
            }
        }
        else
        {
            if (buyDisplayer.canPlayAnimation() && marketMenu.canPlayAnimation())
            {
                pages.get(marketPager.getCurrentItem()).click(v);
            }
        }
    }

    public ArrayList<MarketPage> getPages()
    {
        return pages;
    }

    public void leave()
    {
        activity.finish();
        activity.overridePendingTransition(R.anim.menu_to_market_scale_down, R.anim.menu_to_any_alpha_down);
    }
}
