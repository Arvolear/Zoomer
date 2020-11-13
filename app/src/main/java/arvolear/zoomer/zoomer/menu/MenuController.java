package arvolear.zoomer.zoomer.menu;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.game.GameActivity;
import arvolear.zoomer.zoomer.global_gui.LoadingWheel;
import arvolear.zoomer.zoomer.market.MarketActivity;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class MenuController implements View.OnClickListener
{
    private AppCompatActivity activity;

    private static SoundsPlayer musicPlayer;

    public static final int HOW_MANY_ADDS_PER_DAY = 2;

    private static boolean restartMusic = false;
    private boolean pauseMusic = true;
    private boolean turnSoundOn;

    private SoundsPlayer soundsPlayer;

    private GameConfigurator configurator;

    private boolean firstTime;
    private boolean helpShown = false;

    private MenuBackgroundSetter menuBackgroundSetter;

    private MenuPage menuPage;
    private SoundIcon soundIcon;
    private HelpIcon helpIcon;
    private HelpDisplayer helpDisplayer;
    private LoadingWheel loadingWheel;

    public static SoundsPlayer getMusicPlayer()
    {
        return musicPlayer;
    }

    public static void setRestartMusic(boolean restartMusic)
    {
        MenuController.restartMusic = restartMusic;
    }

    public MenuController(AppCompatActivity activity)
    {
        this.activity = activity;

        // FIXME
        configurator = new GameConfigurator(activity);

        menuBackgroundSetter = new MenuBackgroundSetter(activity, "assets/textures/menu", 6000, 12000);

        turnSoundOn = configurator.getDataBaseHelper().isSound();

        menuPage = new MenuPage(activity, this);
        soundIcon = new SoundIcon(activity, this, "assets/textures/menu/sound", turnSoundOn);
        helpIcon = new HelpIcon(activity, this, "assets/textures/menu/help");
        helpDisplayer = new HelpDisplayer(activity, this);
        loadingWheel = new LoadingWheel(activity, activity.getResources().getColor(R.color.light_text));

        configureSound();
        configureHelp();
    }

    private void disableButtons()
    {
        menuPage.disableButtons();
        soundIcon.disableButtons();
        helpIcon.disableButtons();
    }

    private void enableButtons()
    {
        menuPage.enableButtons();
        soundIcon.enableButtons();
        helpIcon.enableButtons();
    }

    private void configureHelp()
    {
        firstTime = configurator.getDataBaseHelper().isFirstTime();

        if (!firstTime)
        {
            return;
        }

        Thread helper = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1500);
                }
                catch (Exception ex)
                {
                }

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        helpShown = true;

                        soundsPlayer.play("assets/sounds/all/show_menu.mp3", false);

                        helpIcon.show();
                        helpDisplayer.show();
                        menuPage.hide();

                        configurator.getDataBaseHelper().setExperienced();
                        firstTime = false;
                    }
                });
            }
        });

        helper.start();
    }

    private void configureSound()
    {
        musicPlayer = new SoundsPlayer(activity, 0.5f, 0.5f);
        soundsPlayer = new SoundsPlayer(activity);

        musicPlayer.setTurnSoundOn(turnSoundOn);
        soundsPlayer.setTurnSoundOn(turnSoundOn);

        musicPlayer.playFadeIn("assets/music/all/recollection.m4a", true, 1000);
    }

    public void restart()
    {
        if (restartMusic)
        {
            musicPlayer.playFadeIn("assets/music/all/recollection.m4a", true, 1000, 0.5f, 0.5f);
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    public void resume()
    {
        enableButtons();

        turnSoundOn = configurator.getDataBaseHelper().isSound();
        updateSound();

        musicPlayer.resume(false);

        if (!pauseMusic)
        {
            soundsPlayer.play("assets/sounds/all/zoom_out.mp3", false);
        }

        pauseMusic = true;
        restartMusic = false;

        menuBackgroundSetter.update();
        menuBackgroundSetter.start();
        menuBackgroundSetter.resume();

        loadingWheel.hide();
    }

    public void stop()
    {
        menuBackgroundSetter.stop();
        soundsPlayer.stop();
    }

    public void pause()
    {
        menuBackgroundSetter.pause();

        if (pauseMusic)
        {
            musicPlayer.pause();
        }
    }

    private void updateSound()
    {
        configurator.getDataBaseHelper().setSound(turnSoundOn);

        musicPlayer.setTurnSoundOn(turnSoundOn);
        soundsPlayer.setTurnSoundOn(turnSoundOn);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == soundIcon.getSoundId())
        {
            turnSoundOn = !turnSoundOn;

            soundIcon.setTurnSoundOn(turnSoundOn);

            updateSound();

            soundsPlayer.play("assets/sounds/all/sound_press.mp3", false, 0.5f, 0.5f);
        }
        else if (helpShown)
        {
            if (v.getId() == helpDisplayer.getDummyId())
            {
                // do nothing
            }
            else
            {
                if (helpDisplayer.canPlayAnimation())
                {
                    helpShown = false;

                    soundsPlayer.play("assets/sounds/all/hide_menu.mp3", false);

                    helpIcon.hide();
                    helpDisplayer.hide();
                    menuPage.show();
                }
            }
        }
        else
        {
            if (firstTime)
            {
                return;
            }

            if (v.getId() == helpIcon.getHelpId())
            {
                if (helpDisplayer.canPlayAnimation())
                {
                    helpShown = true;

                    soundsPlayer.play("assets/sounds/all/show_menu.mp3", false);

                    helpIcon.show();
                    helpDisplayer.show();
                    menuPage.hide();
                }
            }
            else
            {
                soundsPlayer.play("assets/sounds/all/button_press.mp3", false);

                loadingWheel.show();
                pauseMusic = false;

                if (v.getId() == menuPage.getPlayId())
                {
                    disableButtons();

                    Intent playIntent = new Intent(activity, GameActivity.class);

                    activity.startActivity(playIntent);
                    activity.overridePendingTransition(R.anim.menu_to_any_alpha_up, R.anim.menu_to_any_scale_up);
                }
                else if (v.getId() == menuPage.getMarketId())
                {
                    disableButtons();

                    Intent marketIntent = new Intent(activity, MarketActivity.class);
                    marketIntent.putExtra("restartPlayer", false);

                    activity.startActivity(marketIntent);
                    activity.overridePendingTransition(R.anim.menu_to_any_alpha_up, R.anim.menu_to_market_scale_up);
                }
            }
        }
    }

    void destroy()
    {
        if (musicPlayer != null)
        {
            musicPlayer.stop();
        }

        musicPlayer = null;
    }
}
