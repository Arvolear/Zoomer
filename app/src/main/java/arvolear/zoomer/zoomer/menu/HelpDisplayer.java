package arvolear.zoomer.zoomer.menu;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;

public class HelpDisplayer extends FrameLayout
{
    private AppCompatActivity activity;
    private OnClickListener controller;

    private FrameLayout helpLayout;

    private LinearLayout dummyMenu;
    private ScrollView scrollMenu;
    private LinearLayout content;
    private TextView title;
    private TextView sign;
    private TextView info;

    public HelpDisplayer(AppCompatActivity activity, OnClickListener controller)
    {
        super(activity);
        setOnClickListener(controller);
        setId(generateViewId());
        setSoundEffectsEnabled(false);

        this.activity = activity;
        this.controller = controller;

        helpLayout = activity.findViewById(R.id.helpLayout);

        init();
    }

    private void init()
    {
        helpLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                int sizeW = helpLayout.getWidth() / 2;
                int sizeH = helpLayout.getHeight() / 2 + helpLayout.getHeight() / 4;

                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(sizeW + helpLayout.getWidth() / 10, LayoutParams.MATCH_PARENT);
                LP0.gravity = Gravity.CENTER;

                dummyMenu = new LinearLayout(activity);
                dummyMenu.setLayoutParams(LP0);
                dummyMenu.setOrientation(LinearLayout.VERTICAL);
                dummyMenu.setGravity(Gravity.CENTER);
                dummyMenu.setBackgroundColor(Color.TRANSPARENT);
                dummyMenu.setOnClickListener(controller);
                dummyMenu.setId(generateViewId());
                dummyMenu.setSoundEffectsEnabled(false);

                LinearLayout.LayoutParams LP1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, sizeH);
                LP1.gravity = Gravity.CENTER;

                scrollMenu = new ScrollView(activity);
                scrollMenu.setLayoutParams(LP1);
                scrollMenu.setOverScrollMode(OVER_SCROLL_NEVER);
                scrollMenu.setScrollbarFadingEnabled(false);
                scrollMenu.setBackgroundColor(Color.TRANSPARENT);

                LinearLayout.LayoutParams LP2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                LP2.gravity = Gravity.CENTER;

                content = new LinearLayout(activity);
                content.setLayoutParams(LP2);
                content.setGravity(Gravity.CENTER);
                content.setOrientation(LinearLayout.VERTICAL);
                content.setBackgroundColor(Color.TRANSPARENT);

                LinearLayout.LayoutParams LP3 = new LinearLayout.LayoutParams(sizeW, LayoutParams.WRAP_CONTENT);
                LP3.gravity = Gravity.CENTER;

                int padding1 = helpLayout.getHeight() / 27;

                title = new TextView(activity);
                title.setLayoutParams(LP3);
                title.setPadding(0, 0, 0, padding1);
                title.setGravity(Gravity.CENTER);
                title.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, helpLayout.getHeight() / 12.0f);
                title.setTextColor(activity.getResources().getColor(R.color.light_text));
                title.setTransformationMethod(null);

                int padding2 = helpLayout.getHeight() / 18;

                sign = new TextView(activity);
                sign.setLayoutParams(LP3);
                sign.setPadding(0, 0, 0, padding2);
                sign.setGravity(Gravity.CENTER);
                sign.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                sign.setTextSize(TypedValue.COMPLEX_UNIT_PX, helpLayout.getHeight() / 30.0f);
                sign.setTextColor(activity.getResources().getColor(R.color.light_text));
                sign.setTransformationMethod(null);

                info = new TextView(activity);
                info.setLayoutParams(LP3);
                info.setGravity(Gravity.CENTER_VERTICAL);
                info.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                info.setTextSize(TypedValue.COMPLEX_UNIT_PX, helpLayout.getHeight() / 22.0f);
                info.setTextColor(activity.getResources().getColor(R.color.light_text));
                info.setLinkTextColor(activity.getResources().getColor(R.color.light_text));
                info.setMovementMethod(LinkMovementMethod.getInstance());
                info.setTransformationMethod(null);

                configureText();
                configureFonts();

                content.addView(title);
                content.addView(sign);
                content.addView(info);

                scrollMenu.addView(content);
                dummyMenu.addView(scrollMenu);

                addView(dummyMenu);
            }
        });
    }

    private void configureText()
    {
        title.setText("How To Play");

        sign.setText("*this page might help you get familiar with the game");

        info.setText(Html.fromHtml(
                "<h2>1. Gameplay</h2>" +
                "<p>Play the game just as you would zoom in or zoom out of an image or a map. " +
                        "The most efficient technique is scaling via thumbs. " +
                        "Be prepared, the deeper you get the more difficult the game becomes.</p>" +
                "<h2>2. Coins</h2>" +
                "<p>Coins are needed to buy equipment in the market. " +
                        "To earn them just navigate to in-game menu and click \"EPOCH\". " +
                        "The received amount is calculated from your zooming progress. " +
                        "Don't hesitate to press the button - you won't lose anything except the current zoom.</p>" +
                "<h2>3. Market</h2>" +
                "<p>Market is the place where you can buy unique boosters to zoom further and disparate coloring to customize the game. " +
                        "The game popup will automatically notify you when something new is available.</p>" +
                "<h2>4. Developer's Note</h2>" +
                "<p>What you are about to witness is a true masterpiece of 20'th century math. Named after its founder, Mandelbrot fractal is an elaborate " +
                        "yet simple figure, that is constructed through squaring complex numbers and tracking the ones that converge.</p>" +
                "<h2>5. Credits</h2>" +
                "<p>Huge kudos to <a href=\"http://instagram.com/sokol.art_/\">Maria</a> for making the game possible." + "</p>" +
                "<p>All the sounds used in the game are taken from <a href=\"http://zapsplat.com\">Zapsplat.com</a>." + "</p>"
                ));
    }

    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        title.setTypeface(face);
        sign.setTypeface(face);
        info.setTypeface(face);
    }

    public void show()
    {
        helpLayout.addView(this);

        scrollMenu.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollMenu.setScrollY(scrollMenu.getBottom());
                ObjectAnimator.ofInt(scrollMenu, "scrollY",  0).setDuration(900).start();
            }
        });

        Animation show = AnimationUtils.loadAnimation(activity, R.anim.menu_help_displayer_show);

        show.setAnimationListener(new Animation.AnimationListener()
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
                clearAnimation();
            }
        });

        startAnimation(show);
    }

    public void hide()
    {
        startAnimation(AnimationUtils.loadAnimation(activity, R.anim.menu_help_displayer_hide));
        helpLayout.removeView(this);
    }

    public int getDummyId()
    {
        return dummyMenu.getId();
    }

    public boolean canPlayAnimation()
    {
        return getAnimation() == null;
    }
}
