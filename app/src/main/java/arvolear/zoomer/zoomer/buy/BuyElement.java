package arvolear.zoomer.zoomer.buy;

import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class BuyElement extends FrameLayout
{
    private AppCompatActivity activity;
    private OnClickListener controller;

    private FrameLayout menuLayout;

    private Bitmap coinBitmap;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private String path;

    private String sku;
    private boolean inProgress = false;

    private LinearLayout all;
    private LinearLayout info;

    private ImageView coinImage;
    private TextView title;
    private TextView price;
    private Button buy;

    private ProgressBar progressBar;

    public BuyElement(AppCompatActivity activity, OnClickListener controller, String path, String sku)
    {
        super(activity);

        this.activity = activity;
        this.controller = controller;
        this.path = path;

        menuLayout = activity.findViewById(R.id.menuLayout);

        this.sku = sku;

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        init();
    }

    @SuppressWarnings("deprecation")
    private void init()
    {
        setClipChildren(false);

        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        coinBitmap = tree.get(0);

        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                int size = menuLayout.getHeight() / 8;

                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, size);
                LP0.topMargin = (int)(menuLayout.getHeight() / 15.4f);
                LP0.leftMargin = 10;

                setLayoutParams(LP0);

                FrameLayout.LayoutParams LP1 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

                all = new LinearLayout(activity);
                all.setLayoutParams(LP1);
                all.setGravity(Gravity.START);

                LinearLayout.LayoutParams LP2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

                coinImage = new ImageView(activity);
                coinImage.setLayoutParams(LP2);
                coinImage.setImageBitmap(coinBitmap);
                coinImage.setAdjustViewBounds(true);

                info = new LinearLayout(activity);
                info.setLayoutParams(LP2);
                info.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams LP3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1.0f);

                title = new TextView(activity);
                title.setLayoutParams(LP3);
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 3.0f);
                title.setGravity(Gravity.CENTER_VERTICAL);
                title.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                title.setTextColor(activity.getResources().getColor(R.color.orange_text));
                title.setTransformationMethod(null);

                price = new TextView(activity);
                price.setLayoutParams(LP3);
                price.setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 3.0f);
                price.setGravity(Gravity.CENTER_VERTICAL);
                price.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                price.setTextColor(activity.getResources().getColor(R.color.light_text));
                price.setTransformationMethod(null);

                FrameLayout.LayoutParams LP4 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                LP4.gravity = Gravity.END;
                LP4.rightMargin = (int)(menuLayout.getWidth() / 64.0f);

                buy = new Button(activity);
                buy.setLayoutParams(LP4);
                buy.setGravity(Gravity.CENTER);
                buy.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                buy.setText("BUY");
                buy.setBackgroundColor(Color.TRANSPARENT);
                buy.setTextColor(activity.getResources().getColor(R.color.white_text));
                buy.setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 3.0f);
                buy.setOnClickListener(controller);
                buy.setId(generateViewId());
                buy.setSoundEffectsEnabled(false);

                FrameLayout.LayoutParams LP5 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                LP5.gravity = Gravity.END;
                LP5.rightMargin = (int)(menuLayout.getWidth() / 22.6f);

                progressBar = new ProgressBar(activity);
                progressBar.setLayoutParams(LP5);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(activity.getResources().getColor(R.color.light_text), BlendMode.MODULATE));
                }
                else
                {
                    progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.light_text), PorterDuff.Mode.MULTIPLY);
                }

                progressBar.setIndeterminate(true);

                info.addView(title);
                info.addView(price);

                all.addView(coinImage);
                all.addView(info);

                addView(all);
                addView(buy);

                configureFonts();
            }
        });
    }

    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        title.setTypeface(face);
        price.setTypeface(face);
        buy.setTypeface(face, Typeface.BOLD);
    }

    public int getBuyId()
    {
        return buy.getId();
    }

    public String getSku()
    {
        return sku;
    }

    public void setTitle(final String title)
    {
        post(new Runnable()
        {
            @Override
            public void run()
            {
                BuyElement.this.title.setText(title);
            }
        });
    }

    public void setPrice(final String price)
    {
        post(new Runnable()
        {
            @Override
            public void run()
            {
                BuyElement.this.price.setText(price);
            }
        });
    }

    public void disable()
    {
        if (inProgress)
        {
            return;
        }

        post(new Runnable()
        {
            @Override
            public void run()
            {
                buy.setEnabled(false);
            }
        });
    }

    public void enable()
    {
        if (inProgress)
        {
            return;
        }

        post(new Runnable()
        {
            @Override
            public void run()
            {
                buy.setEnabled(true);
            }
        });
    }

    public void setInProgress()
    {
        if (inProgress)
        {
            return;
        }

        post(new Runnable()
        {
            @Override
            public void run()
            {
                inProgress = true;

                buy.clearAnimation();
                removeView(buy);
                addView(progressBar);
            }
        });
    }

    public void setNormal()
    {
        post(new Runnable()
        {
            @Override
            public void run()
            {
                if (inProgress)
                {
                    inProgress = false;

                    removeView(progressBar);
                    addView(buy);

                    show();
                }

                buy.setEnabled(true);
            }
        });
    }

    public void show()
    {
        buy.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
    }
}
