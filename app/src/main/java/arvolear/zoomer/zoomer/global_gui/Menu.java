package arvolear.zoomer.zoomer.global_gui;

import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

abstract public class Menu extends FrameLayout
{
    protected AppCompatActivity activity;
    protected OnClickListener controller;

    protected FrameLayout menuLayout;

    protected AssetsLoader assetsLoader;
    protected TreeMap<Integer, Bitmap> tree;

    protected String path;

    protected LinearLayout dummyMenu;

    public Menu(AppCompatActivity activity, OnClickListener controller)
    {
        super(activity);
        setOnClickListener(controller);
        setId(generateViewId());
        setSoundEffectsEnabled(false);

        this.activity = activity;
        this.controller = controller;

        menuLayout = activity.findViewById(R.id.menuLayout);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);
    }

    public int getDummyId()
    {
        return dummyMenu.getId();
    }

    abstract protected void configureFonts();

    abstract public void disableButtons();
    abstract public void enableButtons();

    public void show()
    {
        menuLayout.addView(this);

        Animation show = AnimationUtils.loadAnimation(activity, R.anim.game_menu_show);

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
        startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_menu_hide));
        menuLayout.removeView(this);
    }

    public void leave()
    {
        startAnimation(AnimationUtils.loadAnimation(activity, R.anim.game_menu_leave));
    }

    public boolean canPlayAnimation()
    {
        return getAnimation() == null;
    }
}
