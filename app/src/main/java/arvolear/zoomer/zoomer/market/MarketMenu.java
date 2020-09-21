package arvolear.zoomer.zoomer.market;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.Menu;

public class MarketMenu extends Menu
{
    boolean turnSoundOn;

    private String path;
    private ImageView menuImage;

    private Button sound;
    private Button home;
    private Button game;

    public MarketMenu(AppCompatActivity activity, OnClickListener controller, String path, boolean turnSoundOn)
    {
        super(activity, controller);

        this.path = path;
        this.turnSoundOn = turnSoundOn;

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

                sound = new Button(activity);
                sound.setLayoutParams(LP2);
                sound.setText("SOUND");
                sound.setBackgroundColor(Color.TRANSPARENT);
                sound.setTextColor(activity.getResources().getColor(R.color.white_text));
                sound.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 27.5f);
                sound.setOnClickListener(controller);
                sound.setId(generateViewId());
                sound.setSoundEffectsEnabled(false);

                int padding = (int)(menuLayout.getHeight() / 9.8f);

                home = new Button(activity);
                home.setLayoutParams(LP2);
                home.setPadding(0, padding, 0, padding);
                home.setText("HOME");
                home.setBackgroundColor(Color.TRANSPARENT);
                home.setTextColor(activity.getResources().getColor(R.color.white_text));
                home.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 27.5f);
                home.setOnClickListener(controller);
                home.setId(generateViewId());
                home.setSoundEffectsEnabled(false);

                game = new Button(activity);
                game.setLayoutParams(LP2);
                game.setText("GAME");
                game.setBackgroundColor(Color.TRANSPARENT);
                game.setTextColor(activity.getResources().getColor(R.color.white_text));
                game.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 27.5f);
                game.setOnClickListener(controller);
                game.setId(generateViewId());
                game.setSoundEffectsEnabled(false);

                configureFonts();
                setTurnSoundOn(turnSoundOn);

                dummyMenu.addView(sound);
                dummyMenu.addView(home);
                dummyMenu.addView(game);

                addView(dummyMenu);
            }
        });
    }

    protected void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        sound.setTypeface(face, Typeface.BOLD);
        home.setTypeface(face, Typeface.BOLD);
        game.setTypeface(face, Typeface.BOLD);
    }

    public void setTurnSoundOn(boolean turnSoundOn)
    {
        this.turnSoundOn = turnSoundOn;

        if (turnSoundOn)
        {
            sound.setPaintFlags(sound.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            sound.setPaintFlags(sound.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void disableButtons()
    {
        home.setEnabled(false);
        game.setEnabled(false);
    }

    @Override
    public void enableButtons()
    {
        home.setEnabled(true);
        game.setEnabled(true);
    }

    public int getSoundId()
    {
        return sound.getId();
    }

    public int getHomeId()
    {
        return home.getId();
    }

    public int getGameId()
    {
        return game.getId();
    }

    @Override
    public void show()
    {
        super.show();

        sound.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
        home.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
        game.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
    }
}
