package arvolear.zoomer.zoomer.global_gui;

import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import java.math.BigInteger;
import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class CoinCollector extends LinearLayout
{
    private AppCompatActivity activity;
    private OnClickListener controller;

    private Bitmap coinBitmap;

    private FrameLayout notchLayout;

    private AssetsLoader assetsLoader;
    private TreeMap<Integer, Bitmap> tree;

    private String path;

    private BigInteger actualCoins;
    private String displayedCoins;

    private ImageView coinImage;
    private TextView coinsAmount;

    public CoinCollector(AppCompatActivity activity, OnClickListener controller, String path, String actualCoinsStr)
    {
        super(activity);

        this.activity = activity;
        this.controller = controller;
        this.path = path;

        notchLayout = activity.findViewById(R.id.notchLayout);
        notchLayout.addView(this);

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);

        actualCoins = new BigInteger(actualCoinsStr);
        displayedCoins = parseCoins(actualCoins);

        init();
    }

    private void init()
    {
        setClipChildren(false);

        assetsLoader.loadBitmapFromAssets(0, path + "/0.png", false, true);
        coinBitmap = tree.get(0);

        post(new Runnable()
        {
            @Override
            public void run()
            {
                LinearLayout.LayoutParams LP0 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

                coinImage = new ImageView(activity);
                coinImage.setLayoutParams(LP0);
                coinImage.setImageBitmap(coinBitmap);
                coinImage.setAdjustViewBounds(true);
                coinImage.setId(generateViewId());
                coinImage.setOnClickListener(controller);
                coinImage.setSoundEffectsEnabled(false);

                coinsAmount = new TextView(activity);
                coinsAmount.setLayoutParams(LP0);
                coinsAmount.setText(String.valueOf(displayedCoins));
                coinsAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getHeight() / 2.0f);
                coinsAmount.setGravity(Gravity.CENTER_VERTICAL);
                coinsAmount.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                coinsAmount.setTextColor(activity.getResources().getColor(R.color.dark_text));

                addView(coinImage);
                addView(coinsAmount);

                configureFonts();
            }
        });
    }

    public static BigInteger parseStringCoins(String coins)
    {
        StringBuilder ans = new StringBuilder();

        StringBuilder letters = new StringBuilder();
        StringBuilder numbers = new StringBuilder(coins);

        while (!String.valueOf(numbers.charAt(numbers.length() - 1)).matches("[0-9]"))
        {
            letters.append(numbers.charAt(numbers.length() - 1));
            numbers.deleteCharAt(numbers.length() - 1);
        }

        letters.reverse();

        int numLen = letters.length();
        int zeroAmount = 0;

        while (numLen > 0)
        {
            int letter = letters.charAt(numLen - 1) - 64;
            zeroAmount += Math.pow(26, letters.length() - numLen) * letter * 2;
            numLen--;
        }

        boolean isDot = false;

        for (int i = numbers.length() - 1; i >= 0; i--)
        {
            if (numbers.charAt(i) == '.')
            {
                isDot = true;
                break;
            }
        }

        double prefixNumD = Double.parseDouble(numbers.toString());

        if (isDot)
        {
            prefixNumD *= 10;
            zeroAmount--;
        }

        ans.append((int) prefixNumD);

        for (int i = 0; i < zeroAmount; i++)
        {
            ans.append("0");
        }

        return new BigInteger(ans.toString());
    }

    public static String parseCoins(BigInteger coins)
    {
        String coinsStr = coins.toString();
        int numLen = coinsStr.length() - 1;

        int addDotAfter = 1 + numLen % 2;

        numLen /= 2;

        StringBuilder ans = new StringBuilder();

        boolean letterAdded = false;

        while (numLen > 0)
        {
            int letterAscii = (numLen - 1) % 26;
            numLen /= 27;
            char letter = (char) (65 + letterAscii);

            ans.append(letter);
            letterAdded = true;
        }

        int addIndex = 1;

        if (letterAdded)
        {
            addIndex += addDotAfter - 1;
        }

        for (int i = Math.min(addIndex, coinsStr.length() - 1); i >= 0; i--)
        {
            ans.append(coinsStr.charAt(i));
        }

        ans.reverse();

        if (letterAdded)
        {
            ans.insert(addDotAfter, ".");
        }

        return ans.toString();
    }

    private void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        coinsAmount.setTypeface(face, Typeface.BOLD);
        coinsAmount.setShadowLayer(8.0f, 0.0f, 0.0f, activity.getResources().getColor(R.color.light_shadow));
    }

    public int compareTo(String coins)
    {
        BigInteger tmp = parseStringCoins(coins);

        return actualCoins.compareTo(tmp);
    }

    public void subCoins(final String coins)
    {
        if (coinImage.getAnimation() == null || coinImage.getAnimation().hasEnded())
        {
            coinImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.little_jump));
        }

        coinsAmount.post(new Runnable()
        {
            @Override
            public void run()
            {
                BigInteger tmp = parseStringCoins(coins);

                actualCoins = actualCoins.subtract(tmp);
                displayedCoins = parseCoins(actualCoins);

                coinsAmount.setText(displayedCoins);
            }
        });
    }

    public void addCoins(final String coins)
    {
        if (coinImage.getAnimation() == null || coinImage.getAnimation().hasEnded())
        {
            coinImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.little_jump));
        }

        coinsAmount.post(new Runnable()
        {
            @Override
            public void run()
            {
                BigInteger tmp = parseStringCoins(coins);

                actualCoins = actualCoins.add(tmp);
                displayedCoins = parseCoins(actualCoins);

                coinsAmount.setText(displayedCoins);
            }
        });
    }

    public void setActualCoins(final String coins, Animation animation)
    {
        if (animation != null && coinImage.getAnimation() == null || coinImage.getAnimation().hasEnded())
        {
            coinImage.startAnimation(animation);
        }

        coinsAmount.post(new Runnable()
        {
            @Override
            public void run()
            {
                actualCoins = new BigInteger(coins);
                displayedCoins = parseCoins(actualCoins);

                coinsAmount.setText(displayedCoins);
            }
        });
    }

    public String getActualCoins()
    {
        return actualCoins.toString();
    }

    public void showNotEnough()
    {
        Animation rattle = AnimationUtils.loadAnimation(activity, R.anim.market_menu_not_enough);

        rattle.setAnimationListener(new Animation.AnimationListener()
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
                coinsAmount.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_menu_not_enough));
            }
        });

        coinsAmount.startAnimation(rattle);
    }

    public void show()
    {
        coinImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_show));
        coinsAmount.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_show));
    }

    public void hide()
    {
        coinImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_hide));
        coinsAmount.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_hide));
    }

    public void leave()
    {
        coinImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_img_leave));
        coinsAmount.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.coin_collector_img_leave));
    }

    public int getCoinsId()
    {
        return coinImage.getId();
    }
}
