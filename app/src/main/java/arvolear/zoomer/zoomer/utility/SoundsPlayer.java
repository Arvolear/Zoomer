package arvolear.zoomer.zoomer.utility;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import arvolear.zoomer.zoomer.expansions.ExpansionController;

public class SoundsPlayer
{
    private AppCompatActivity activity;

    private ZipResourceFile expansionFile;
    private boolean loadFromLocalAssets = false;

    private MediaPlayer mediaPlayer;
    private int curPos;
    private float leftVolume;
    private float rightVolume;
    private boolean turnSoundOn;

    public SoundsPlayer(AppCompatActivity activity)
    {
        this(activity, 1.0f, 1.0f);
    }

    public SoundsPlayer(AppCompatActivity activity, float leftVolume, float rightVolume)
    {
        this.activity = activity;

        mediaPlayer = new MediaPlayer();
        curPos = 0;

        this.turnSoundOn = true;

        this.leftVolume = leftVolume;
        this.rightVolume = rightVolume;

        try
        {
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(activity, ExpansionController.EXP_VERSION, ExpansionController.EXP_VERSION);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void play(String path, boolean loop, float left, float right)
    {
        try
        {
            if (mediaPlayer == null)
            {
                mediaPlayer = new MediaPlayer();
            }

            mediaPlayer.reset();
            curPos = 0;
            AssetFileDescriptor descriptor;

            if (loadFromLocalAssets)
            {
                descriptor = activity.getAssets().openFd(path);
            }
            else
            {
                descriptor = expansionFile.getAssetFileDescriptor(path);
            }

            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setLooping(loop);

            if (turnSoundOn)
            {
                mediaPlayer.setVolume(left, right);
            }
            else
            {
                mediaPlayer.setVolume(0.0f, 0.0f);
            }

            mediaPlayer.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void play(String path, boolean loop)
    {
        play(path, loop, leftVolume, rightVolume);
    }

    public void playFadeIn(String path, boolean loop, int time, float left, float right)
    {
        play(path, loop, 0.0f, 0.0f);

        if (!turnSoundOn)
        {
            return;
        }

        time = Math.max(time, 100);
        final int steps = 100;
        final int toSleep = time / steps;

        final float incLeft = left / (float) steps;
        final float incRight = right / (float) steps;

        final Thread fadeIn = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                float curLeft = incLeft;
                float curRight = incRight;

                for (int i = 0; i < steps; i++)
                {
                    try
                    {
                        Thread.sleep(toSleep);

                        mediaPlayer.setVolume(Math.min(leftVolume, curLeft), Math.min(rightVolume, curRight));
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    curLeft += incLeft;
                    curRight += incRight;
                }
            }
        });

        fadeIn.start();
    }

    public void playFadeIn(String path, boolean loop, int time)
    {
        playFadeIn(path, loop, time, leftVolume, rightVolume);
    }

    public void pause()
    {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            curPos = mediaPlayer.getCurrentPosition();
        }
    }

    public void resume(boolean reset)
    {
        if (mediaPlayer != null && !mediaPlayer.isPlaying())
        {
            if (!reset)
            {
                mediaPlayer.seekTo(curPos);
                curPos = 0;
            }

            mediaPlayer.start();
        }
    }

    public void stop()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setTurnSoundOn(boolean turnSoundOn)
    {
        this.turnSoundOn = turnSoundOn;

        if (mediaPlayer != null)
        {
            if (turnSoundOn)
            {
                mediaPlayer.setVolume(leftVolume, rightVolume);
            }
            else
            {
                mediaPlayer.setVolume(0.0f, 0.0f);
            }
        }
    }

    public void setVolume(float leftVolume, float rightVolume)
    {
        this.leftVolume = leftVolume;
        this.rightVolume = rightVolume;

        if (mediaPlayer != null && turnSoundOn)
        {
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    public void setLoadFromLocalAssets(boolean loadFromLocalAssets)
    {
        this.loadFromLocalAssets = loadFromLocalAssets;
    }

    public void setOnEndListener(MediaPlayer.OnCompletionListener listener)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.setOnCompletionListener(listener);
        }
    }

    public void clearEndListener()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.setOnCompletionListener(null);
        }
    }

    public boolean canPlay()
    {
        return mediaPlayer != null && !mediaPlayer.isPlaying();
    }
}
