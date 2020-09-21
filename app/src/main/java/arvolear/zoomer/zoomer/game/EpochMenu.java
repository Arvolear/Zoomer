package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.Menu;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class EpochMenu extends Menu
{
    private String epochGain;

    private boolean attention = false;

    private String path;
    private ImageView menuImage;

    private TextView info;
    private Button epoch;

    EpochMenu(AppCompatActivity activity, OnClickListener controller, String path)
    {
        super(activity, controller);

        this.path = path;
        epochGain = "";

        init();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        Bitmap menuBitmap = tree.get(0);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.CENTER;

        menuImage = new ImageView(activity);
        menuImage.setLayoutParams(LP0);
        menuImage.setImageBitmap(menuBitmap);
        menuImage.setAdjustViewBounds(true);

        addView(menuImage);

        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                int size = menuLayout.getHeight() / 2 + menuLayout.getHeight() / 4;

                FrameLayout.LayoutParams LP1 = new FrameLayout.LayoutParams(size, size - menuLayout.getHeight() / 8);
                LP1.gravity = Gravity.CENTER;

                dummyMenu = new LinearLayout(activity);
                dummyMenu.setLayoutParams(LP1);
                dummyMenu.setOrientation(LinearLayout.VERTICAL);
                dummyMenu.setGravity(Gravity.CENTER);
                dummyMenu.setBackgroundColor(Color.TRANSPARENT);
                dummyMenu.setOnClickListener(controller);
                dummyMenu.setId(generateViewId());
                dummyMenu.setSoundEffectsEnabled(false);

                LinearLayout.LayoutParams LP2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LP2.gravity = Gravity.CENTER;
                LP2.topMargin = menuLayout.getHeight() / 8;

                info = new TextView(activity);
                info.setLayoutParams(LP2);
                info.setGravity(Gravity.CENTER);
                info.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                info.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 22.0f);
                info.setTextColor(activity.getResources().getColor(R.color.light_text));
                info.setTransformationMethod(null);

                LinearLayout.LayoutParams LP3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LP3.gravity = Gravity.CENTER;

                epoch = new Button(activity);
                epoch.setLayoutParams(LP3);
                epoch.setText("EPOCH");
                epoch.setBackgroundColor(Color.TRANSPARENT);
                epoch.setTextColor(activity.getResources().getColor(R.color.white_text));
                epoch.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 24.85f);
                epoch.setOnClickListener(controller);
                epoch.setId(generateViewId());
                epoch.setSoundEffectsEnabled(false);

                configureFonts();

                dummyMenu.addView(info);
                dummyMenu.addView(epoch);

                addView(dummyMenu);
            }
        });
    }

    public void setEpochGain(String epochGain)
    {
        this.epochGain = epochGain;

        info.setText(Html.fromHtml("Current progress will be lost<br><br>" +
                "You will receive " + "<font color=#" +
                Integer.toHexString(activity.getResources().getColor(R.color.orange_text) & 0x00ffffff) + ">" + this.epochGain + "</font> coins<br>"));
    }

    @Override
    protected void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        info.setTypeface(face);
        epoch.setTypeface(face, Typeface.BOLD);
    }

    public int getEpochId()
    {
        return epoch.getId();
    }

    public Animation epoch(final SoundsPlayer soundsPlayer)
    {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(800);
        fadeOut.setFillAfter(true);

        final float centerY = (float) dummyMenu.getHeight() / 2;
        final float posY = epoch.getY() + (float) epoch.getHeight() / 2;

        Animation toCenter = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.ABSOLUTE, centerY - posY);
        toCenter.setDuration(1000);
        toCenter.setFillAfter(true);

        final AnimationSet set = new AnimationSet(false);
        set.setFillAfter(true);
        set.setStartOffset(400);

        final Animation toCenterHelp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.ABSOLUTE, centerY - posY,
                Animation.ABSOLUTE, centerY - posY);

        final Animation scaleUp = new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f,
                Animation.RELATIVE_TO_SELF, 0.4885f,
                Animation.ABSOLUTE, centerY - epoch.getY());
        scaleUp.setInterpolator(new OvershootInterpolator());
        scaleUp.setDuration(1000);

        set.addAnimation(toCenterHelp);
        set.addAnimation(scaleUp);

        toCenter.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                soundsPlayer.play("assets/sounds/game/epoch_move.mp3", false);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                epoch.startAnimation(set);
            }
        });

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
                            Thread.sleep(400);
                        }
                        catch (Exception ex)
                        {
                        }

                        soundsPlayer.play("assets/sounds/game/epoch_bounce.mp3", false);
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
                soundsPlayer.play("assets/sounds/all/long_zoom_in.mp3", false);

                AnimationSet set = new AnimationSet(false);
                set.setFillAfter(true);

                Animation scaleUp1 = new ScaleAnimation(3.0f, 10.0f, 3.0f, 10.0f,
                        Animation.RELATIVE_TO_SELF, 0.4885f,
                        Animation.ABSOLUTE, centerY - epoch.getY());
                scaleUp1.setDuration(1000);
                scaleUp1.setStartOffset(400);

                set.addAnimation(toCenterHelp);
                set.addAnimation(scaleUp1);

                epoch.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                epoch.startAnimation(set);
            }
        });

        Animation bigScaleUp = new ScaleAnimation(1.0f, 60.0f, 1.0f, 60.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        bigScaleUp.setFillAfter(true);
        bigScaleUp.setDuration(1500);
        bigScaleUp.setStartOffset(2950);

        info.startAnimation(fadeOut);
        epoch.startAnimation(toCenter);

        startAnimation(bigScaleUp);

        return bigScaleUp;
    }

    public void setAttention()
    {
        this.attention = true;
    }

    @Override
    public void show()
    {
        super.show();

        if (!attention)
        {
            epoch.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
        }
        else
        {
            epoch.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath_active));
        }
    }

    @Override
    public void disableButtons()
    {
        epoch.setEnabled(false);
    }

    @Override
    public void enableButtons()
    {
        epoch.setEnabled(true);
    }

    public void clear()
    {
        enableButtons();

        attention = false;

        dummyMenu.removeAllViews();
        dummyMenu.addView(info);
        dummyMenu.addView(epoch);
        epoch.setLayerType(View.LAYER_TYPE_NONE, null);

        removeAllViews();
        addView(menuImage);
        addView(dummyMenu);

        menuLayout.removeView(this);
    }
}
