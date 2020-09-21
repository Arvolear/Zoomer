package arvolear.zoomer.zoomer.buy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.Menu;

public class BuyDisplayer extends Menu
{
    private String path;
    private ImageView menuImage;

    private TextView title;
    private TextView disconnected;

    private boolean canShowVideo;
    private TextView video;

    private ArrayList<BuyElement> elements;

    public BuyDisplayer(AppCompatActivity activity, OnClickListener controller, String path)
    {
        super(activity, controller);

        elements = new ArrayList<>();

        this.path = path;

        init();
    }

    private Bitmap configureBitmap(int size)
    {
        Bitmap menuBitmap = tree.get(0);
        Paint paint = new Paint();

        float aspectRatio = (float)size / (float)menuLayout.getHeight();

        int bitmapWidth = (int)(menuBitmap.getHeight() * aspectRatio);
        bitmapWidth = Math.min(bitmapWidth, menuBitmap.getWidth());

        int bitmapWidthOffset = (menuBitmap.getWidth() - bitmapWidth);

        Rect frameToRender = new Rect(bitmapWidthOffset, 0, menuBitmap.getWidth(), menuBitmap.getHeight());
        RectF whereToRender = new RectF(0, 0, size, menuLayout.getHeight());

        Bitmap output = Bitmap.createBitmap(menuLayout.getWidth(), menuLayout.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(menuBitmap, frameToRender, whereToRender, paint);

        return output;
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);

        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                while (menuLayout.getWidth() == 0)
                {
                    try
                    {
                        wait(50);
                    }
                    catch (Exception ex)
                    {
                    }
                }

                int size = menuLayout.getWidth() / 3 + menuLayout.getWidth() / 50;
                int bitmapSize = menuLayout.getWidth() / 3 + menuLayout.getWidth() / 12;

                Bitmap menuBitmap = configureBitmap(bitmapSize);

                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                LP0.gravity = Gravity.START;

                menuImage = new ImageView(activity);
                menuImage.setLayoutParams(LP0);
                menuImage.setImageBitmap(menuBitmap);

                FrameLayout.LayoutParams LP1 = new FrameLayout.LayoutParams(size, LayoutParams.MATCH_PARENT);
                LP1.gravity = Gravity.START;

                dummyMenu = new LinearLayout(activity);
                dummyMenu.setLayoutParams(LP1);
                dummyMenu.setOrientation(LinearLayout.VERTICAL);
                dummyMenu.setBackgroundColor(Color.TRANSPARENT);
                dummyMenu.setOnClickListener(controller);
                dummyMenu.setId(generateViewId());
                dummyMenu.setSoundEffectsEnabled(false);

                LinearLayout.LayoutParams LP2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LP2.gravity = Gravity.CENTER;
                LP2.topMargin = menuLayout.getHeight() / 60;

                title = new TextView(activity);
                title.setLayoutParams(LP2);
                title.setGravity(Gravity.CENTER);
                title.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 12.0f);
                title.setText("Coins Store");
                title.setTextColor(activity.getResources().getColor(R.color.light_text));
                title.setTransformationMethod(null);

                int paddingTop = (int)(menuLayout.getHeight() / 15.4f);

                disconnected = new TextView(activity);
                disconnected.setLayoutParams(LP2);
                disconnected.setPadding(0, paddingTop, 0, 0);
                disconnected.setGravity(Gravity.CENTER);
                disconnected.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                disconnected.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 22.0f);
                disconnected.setText("Temporarily unavailable\nPlease try again later");
                disconnected.setTextColor(activity.getResources().getColor(R.color.light_text));
                disconnected.setTransformationMethod(null);

                LinearLayout.LayoutParams LP3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LP3.gravity = Gravity.START;
                LP3.leftMargin = menuLayout.getWidth() / 15;

                video = new TextView(activity);
                video.setLayoutParams(LP3);
                video.setPadding(0, paddingTop, 0, 0);
                video.setGravity(Gravity.CENTER);
                video.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                video.setTextColor(activity.getResources().getColor(R.color.white_text));
                video.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 25.0f);
                video.setOnClickListener(controller);
                video.setId(generateViewId());
                video.setSoundEffectsEnabled(false);
                video.setTransformationMethod(null);

                configureFonts();
                setDisconnectedAdd();

                dummyMenu.addView(title);
                dummyMenu.addView(disconnected);
                dummyMenu.addView(video);

                addView(menuImage);
                addView(dummyMenu);
            }
        });
    }

    @Override
    protected void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        title.setTypeface(face);
        disconnected.setTypeface(face);
        video.setTypeface(face);
    }

    @Override
    public void disableButtons()
    {
        video.setEnabled(false);
    }

    @Override
    public void enableButtons()
    {
        video.setEnabled(true);
    }

    public void addBuyElement(final BuyElement element)
    {
        elements.add(element);

        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                dummyMenu.removeView(disconnected);
                dummyMenu.addView(element, 1);
            }
        });
    }

    public void setDisconnectedStore()
    {
        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                for (BuyElement element : elements)
                {
                    dummyMenu.removeView(element);
                }

                dummyMenu.removeView(disconnected);
                dummyMenu.addView(disconnected);
            }
        });
    }

    public void setConnectedAdd()
    {
        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                canShowVideo = true;

                video.setText(Html.fromHtml("<font color=#" +
                        Integer.toHexString(activity.getResources().getColor(R.color.orange_text) & 0x00ffffff) + ">2x multiplier</font><br>Watch a video"));
                video.setEnabled(canShowVideo);
                video.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
            }
        });
    }

    public void setDisconnectedAdd()
    {
        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                canShowVideo = false;

                video.setText(Html.fromHtml("Video " + "<font color=#" +
                        Integer.toHexString(activity.getResources().getColor(R.color.orange_text) & 0x00ffffff) + ">reward</font><br>is unavailable"));
                video.setEnabled(canShowVideo);
                video.clearAnimation();
            }
        });
    }

    @Override
    public void show()
    {
        menuLayout.addView(this);

        Animation show = AnimationUtils.loadAnimation(activity, R.anim.buy_displayer_show);

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

        if (canShowVideo)
        {
            video.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
        }

        for (BuyElement element : elements)
        {
            element.show();
        }

        startAnimation(show);
    }

    @Override
    public void hide()
    {
        startAnimation(AnimationUtils.loadAnimation(activity, R.anim.buy_displayer_hide));
        menuLayout.removeView(this);
    }

    public int getVideoId()
    {
        return video.getId();
    }

    public ArrayList<BuyElement> getElements()
    {
        return elements;
    }
}
