package arvolear.zoomer.zoomer.market;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.Menu;

public class MarketElementMenu extends Menu
{
    private int size;

    private MarketElement displayedElement;
    private MarketElement.Type type;

    private TextView elementInfo;
    private TextView elementPrice;
    private Button actionButton;

    public MarketElementMenu(AppCompatActivity activity, OnClickListener controller, MarketPage page)
    {
        super(activity, controller);

        menuLayout = page.getMenuLayout();

        init();
    }

    private void init()
    {
        menuLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                size = menuLayout.getHeight();

                dummyMenu = new LinearLayout(activity);
                dummyMenu.setBackgroundColor(Color.TRANSPARENT);
                dummyMenu.setOrientation(LinearLayout.VERTICAL);
                dummyMenu.setGravity(Gravity.CENTER);
                dummyMenu.setOnClickListener(controller);
                dummyMenu.setId(generateViewId());
                dummyMenu.setSoundEffectsEnabled(false);

                LinearLayout.LayoutParams LP1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                LP1.gravity = Gravity.CENTER;

                elementInfo = new TextView(activity);
                elementInfo.setLayoutParams(LP1);
                elementInfo.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                elementInfo.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                elementInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 14.0f);
                elementInfo.setTextColor(activity.getResources().getColor(R.color.light_text));
                elementInfo.setTransformationMethod(null);

                int paddingOne = (int)(menuLayout.getHeight() / 21.6f);
                int paddingTwo = (int)(menuLayout.getHeight() / 43.2f);

                elementPrice = new TextView(activity);
                elementPrice.setLayoutParams(LP1);
                elementPrice.setPadding(0, paddingOne, 0, paddingTwo);
                elementPrice.setGravity(Gravity.CENTER);
                elementPrice.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                elementPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 14.0f);
                elementPrice.setTextColor(activity.getResources().getColor(R.color.orange_text));
                elementPrice.setTransformationMethod(null);

                actionButton = new Button(activity);
                actionButton.setLayoutParams(LP1);
                actionButton.setBackgroundColor(Color.TRANSPARENT);
                actionButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuLayout.getHeight() / 14.0f);
                actionButton.setTextColor(activity.getResources().getColor(R.color.white_text));
                actionButton.setOnClickListener(controller);
                actionButton.setId(generateViewId());
                actionButton.setSoundEffectsEnabled(false);

                configureFonts();

                dummyMenu.addView(elementInfo);
                dummyMenu.addView(elementPrice);
                dummyMenu.addView(actionButton);

                addView(dummyMenu);
            }
        });
    }

    @Override
    protected void configureFonts()
    {
        Typeface face = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.font_path));

        elementInfo.setTypeface(face);
        elementPrice.setTypeface(face);
        actionButton.setTypeface(face, Typeface.BOLD);
    }

    @Override
    public void show()
    {
        if (type == null)
        {
            return;
        }

        menuLayout.addView(this);

        Animation show;

        if (type == MarketElement.Type.LEFT)
        {
            FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(size, size - menuLayout.getHeight() / 3);
            LP0.leftMargin = menuLayout.getWidth() / 6 + menuLayout.getWidth() / 25;
            LP0.gravity = Gravity.CENTER;

            dummyMenu.setLayoutParams(LP0);

            show = AnimationUtils.loadAnimation(activity, R.anim.market_left_menu_show);
        }
        else
        {
            FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(size, size - menuLayout.getHeight() / 3);
            LP0.rightMargin = menuLayout.getWidth() / 6 + menuLayout.getWidth() / 25;
            LP0.gravity = Gravity.CENTER;

            dummyMenu.setLayoutParams(LP0);

            show = AnimationUtils.loadAnimation(activity, R.anim.market_right_menu_show);
        }

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

        actionButton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.button_breath));
    }

    @Override
    public void hide()
    {
        if (type == null)
        {
            return;
        }

        if (type == MarketElement.Type.LEFT)
        {
            startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_left_menu_hide));
        }
        else
        {
            startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_right_menu_hide));
        }

        menuLayout.removeView(this);
    }

    public void showNotEnough()
    {
        if (type == null)
        {
            return;
        }

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.market_menu_not_enough);

        anim.setAnimationListener(new Animation.AnimationListener()
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
                elementPrice.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_menu_not_enough));
            }
        });

        elementPrice.startAnimation(anim);
    }

    public void setDisplayedElement(MarketElement displayedElement, MarketElement.Type type)
    {
        this.displayedElement = displayedElement;
        this.type = type;

        elementInfo.setText(displayedElement.getDescription());
        elementPrice.setText(displayedElement.getPrice());

        String status = displayedElement.getStatus();
        String text = "";

        if (status.equals("locked"))
        {
            text = "BUY";
        }
        else if (status.equals("bought"))
        {
            text = "EQUIP";
        }
        else if (status.equals("equipped"))
        {
            text = "DROP";
        }

        actionButton.setText(text);
    }

    public void buy()
    {
        displayedElement.buy();
        actionButton.setText("EQUIP");
    }

    public void equip()
    {
        displayedElement.equip();
        actionButton.setText("DROP");
    }

    public void drop()
    {
        displayedElement.drop();
        actionButton.setText("EQUIP");
    }

    @Override
    public void disableButtons()
    {
        actionButton.setEnabled(false);
    }

    @Override
    public void enableButtons()
    {
        actionButton.setEnabled(true);
    }

    public MarketElement getDisplayedElement()
    {
        return displayedElement;
    }

    public MarketElement.Type getDisplayedElementType()
    {
        return type;
    }

    public int getActionButtonId()
    {
        return actionButton.getId();
    }

    public String getPrice()
    {
        return elementPrice.getText().toString();
    }
}
