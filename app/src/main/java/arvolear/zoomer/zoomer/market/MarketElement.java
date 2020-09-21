package arvolear.zoomer.zoomer.market;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.db.DataBaseHelper;
import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class MarketElement extends FrameLayout
{
    public enum Type
    {
        LEFT,
        RIGHT;
    }

    private AppCompatActivity activity;

    private String status;

    private String lockedPath;
    private String lockedAvailablePath;
    private String selectedPath;
    private String elementPath;
    private String type;
    private int index;

    private Bitmap lockedBitmap;
    private Bitmap lockedAvailableBitmap;
    private Bitmap elementBitmap;
    private Bitmap selectedBitmap;

    private TreeMap<Integer, Bitmap> tree;
    private AssetsLoader assetsLoader;

    private String price;
    private String description;

    private ImageView elementImage;
    private ImageView selectedImage;

    public MarketElement(AppCompatActivity activity, String path, String type, int index, String status)
    {
        super(activity);
        setId(generateViewId());
        setSoundEffectsEnabled(false);

        this.activity = activity;

        this.lockedPath = path + "/locked/" + "0.png";
        this.lockedAvailablePath = path + "/locked/" + "1.png";
        this.selectedPath = path + "/selected/" + "0.png";
        this.elementPath = path + "/" + type + "/elements/" + index + ".png";

        this.status = status;
        this.type = type;
        this.index = index;

        tree = new TreeMap<>();
        assetsLoader = new AssetsLoader(activity, tree);
        
        init();
    }

    private void init()
    {
        assetsLoader.loadBitmapFromAssets(0, lockedPath, false, true);
        assetsLoader.loadBitmapFromAssets(1, lockedAvailablePath, false, true);
        assetsLoader.loadBitmapFromAssets(2, elementPath, false, true);
        assetsLoader.loadBitmapFromAssets(3, selectedPath, false, true);

        lockedBitmap = tree.get(0);
        lockedAvailableBitmap = tree.get(1);
        elementBitmap = tree.get(2);
        selectedBitmap = tree.get(3);

        FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LP0.gravity = Gravity.CENTER;

        elementImage = new ImageView(activity);
        elementImage.setLayoutParams(LP0);
        elementImage.setAdjustViewBounds(true);
        elementImage.setImageBitmap(elementBitmap);

        selectedImage = new ImageView(activity);
        selectedImage.setLayoutParams(LP0);
        selectedImage.setAdjustViewBounds(true);
        selectedImage.setImageBitmap(selectedBitmap);

        if (status.equals("locked"))
        {
            elementImage.setImageBitmap(lockedBitmap);
        }
        else if (status.equals("equipped"))
        {
            addView(selectedImage);
        }

        addView(elementImage);

        description = getResources().getString(getResources().getIdentifier(type + "_" + index + "_info", "string", activity.getPackageName()));
        price = getResources().getString(getResources().getIdentifier(type + "_" + index + "_price", "string", activity.getPackageName()));
    }

    public void buy()
    {
        status = "bought";

        Animation down = AnimationUtils.loadAnimation(activity, R.anim.market_lock_down);

        down.setAnimationListener(new Animation.AnimationListener()
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
                elementImage.setImageBitmap(elementBitmap);
                elementImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_lock_up));
            }
        });

        elementImage.startAnimation(down);
    }

    public void equip()
    {
        status = "equipped";
        addView(selectedImage, 0);
    }

    public void drop()
    {
        status = "bought";
        removeView(selectedImage);
    }

    public void saveTo(DataBaseHelper dataBaseHelper)
    {
        dataBaseHelper.setStatus(type, index, status);
    }

    public void displayEnoughToBuy(boolean action)
    {
        if (action)
        {
            elementImage.setImageBitmap(lockedAvailableBitmap);
            elementImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_lock_breath));
        }
        else
        {
            elementImage.setImageBitmap(lockedBitmap);
            elementImage.clearAnimation();
        }
    }

    public String getDescription()
    {
        return description;
    }

    public String getPrice()
    {
        return price;
    }

    public String getStatus()
    {
        return status;
    }

    public void show(Animation animation)
    {
        startAnimation(animation);
    }

    public void hide(Animation animation)
    {
        startAnimation(animation);
    }

    public boolean canPlayAnimation()
    {
        return getAnimation() == null || getAnimation().hasEnded();
    }
}
