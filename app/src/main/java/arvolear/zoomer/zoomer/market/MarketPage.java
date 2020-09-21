package arvolear.zoomer.zoomer.market;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.db.DataBaseHelper;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class MarketPage extends LinearLayout
{
    private AppCompatActivity activity;
    private MarketController controller;

    private SoundsPlayer soundsPlayer;

    private CoinCollector coinCollector;
    private MarketPageName pageName;

    private FrameLayout elementsLayout;

    private MarketElementMenu elementMenu;
    private boolean elementMenuShown = false;

    private String name;
    private int amount;

    private static final int MAX_PER_ROW = 4;
    private int currentRow = 0;

    private ArrayList<ArrayList<MarketElement>> elements;

    public MarketPage(AppCompatActivity activity, MarketController controller, SoundsPlayer soundsPlayer, CoinCollector coinCollector, String name, int amount)
    {
        super(activity);

        this.activity = activity;
        this.controller = controller;
        this.soundsPlayer = soundsPlayer;
        this.coinCollector = coinCollector;
        this.name = name;
        this.amount = amount;

        elements = new ArrayList<>();

        init();
    }

    private void init()
    {
        setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LP0 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        LP0.weight = 0.3f;

        pageName = new MarketPageName(activity, name);
        pageName.setLayoutParams(LP0);
        pageName.setOnClickListener(controller);

        LinearLayout.LayoutParams LP1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        LP1.weight = 0.9f;

        elementsLayout = new FrameLayout(activity);
        elementsLayout.setLayoutParams(LP1);

        addView(pageName);
        addView(elementsLayout);

        elementMenu = new MarketElementMenu(activity, controller, this);
    }

    public void addElement(final MarketElement element)
    {
        elementsLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (currentRow == elements.size())
                {
                    elements.add(new ArrayList<MarketElement>());
                }

                int elemWidth = elementsLayout.getWidth() / 4;
                int elemHeight = elementsLayout.getHeight() / 2;

                FrameLayout.LayoutParams LP0 = new FrameLayout.LayoutParams(elemWidth, elemHeight);
                LP0.leftMargin = elemWidth * elements.get(currentRow).size();
                LP0.topMargin = elemHeight * currentRow;

                int padding = (int) (elementsLayout.getHeight() / 27.0f);

                element.setLayoutParams(LP0);
                element.setPadding(padding, padding, padding, padding);

                element.setOnClickListener(controller);

                elements.get(currentRow).add(element);
                elementsLayout.addView(element);

                if (elements.get(currentRow).size() == MAX_PER_ROW)
                {
                    currentRow++;
                }

                String elementStatus = element.getStatus();

                if (elementStatus.equals("locked"))
                {
                    if (coinCollector.compareTo(element.getPrice()) >= 0)
                    {
                        element.displayEnoughToBuy(true);
                    }
                    else
                    {
                        element.displayEnoughToBuy(false);
                    }
                }
            }
        });
    }

    private void showElementMenuFor(MarketElement cur, MarketElement.Type type)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            for (int j = 0; j < elements.get(i).size(); j++)
            {
                MarketElement element = elements.get(i).get(j);

                if (element == cur)
                {
                    float centerX = (float) elementsLayout.getWidth() / 4;
                    float centerY = (float) elementsLayout.getHeight() / 2;

                    if (j >= MAX_PER_ROW / 2)
                    {
                        centerX += (float) elementsLayout.getWidth() / 2;
                    }

                    float elemPosX = element.getX() + (float) element.getWidth() / 2;
                    float elemPosY = element.getY() + (float) element.getHeight() / 2;

                    AnimationSet set = new AnimationSet(false);
                    set.setFillAfter(true);

                    Animation toCenterAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.ABSOLUTE, centerX - elemPosX,
                            Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.ABSOLUTE, centerY - elemPosY);
                    toCenterAnim.setDuration(700);

                    Animation scaleAnim = new ScaleAnimation(1.0f, 1.6f,
                            1.0f, 1.6f,
                            Animation.ABSOLUTE, centerX - element.getX(),
                            Animation.ABSOLUTE, centerY - element.getY());
                    scaleAnim.setDuration(600);
                    scaleAnim.setStartOffset(600);

                    set.addAnimation(toCenterAnim);
                    set.addAnimation(scaleAnim);

                    element.show(set);
                }
                else if (type == MarketElement.Type.LEFT)
                {
                    if (j < MAX_PER_ROW / 2)
                    {
                        element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_scale_down));
                    }
                    else
                    {
                        element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_right_hide));
                    }
                }
                else
                {
                    if (j < MAX_PER_ROW / 2)
                    {
                        element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_left_hide));
                    }
                    else
                    {
                        element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_scale_down));
                    }
                }
            }
        }
    }

    public void hideElementMenuFor(MarketElement cur, MarketElement.Type type)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            for (int j = 0; j < elements.get(i).size(); j++)
            {
                MarketElement element = elements.get(i).get(j);

                if (element == cur)
                {
                    float centerX = (float) elementsLayout.getWidth() / 4;
                    float centerY = (float) elementsLayout.getHeight() / 2;

                    if (j >= MAX_PER_ROW / 2)
                    {
                        centerX += (float) elementsLayout.getWidth() / 2;
                    }

                    float elemPosX = element.getX() + (float) element.getWidth() / 2;
                    float elemPosY = element.getY() + (float) element.getHeight() / 2;

                    AnimationSet set = new AnimationSet(false);
                    set.setFillAfter(true);

                    Animation scaleAnim = new ScaleAnimation(1.6f, 1.0f,
                            1.6f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnim.setDuration(700);

                    Animation fromCenterAnim = new TranslateAnimation(Animation.ABSOLUTE, centerX - elemPosX,
                            Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.ABSOLUTE, centerY - elemPosY,
                            Animation.RELATIVE_TO_SELF, 0.0f);
                    fromCenterAnim.setDuration(700);
                    fromCenterAnim.setStartOffset(500);

                    set.addAnimation(scaleAnim);
                    set.addAnimation(fromCenterAnim);

                    element.hide(set);
                }
                else if (type == MarketElement.Type.LEFT)
                {
                    if (j < MAX_PER_ROW / 2)
                    {
                        element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_scale_up));
                    }
                    else
                    {
                        element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_right_show));
                    }
                }
                else
                {
                    if (j < MAX_PER_ROW / 2)
                    {
                        element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_left_show));
                    }
                    else
                    {
                        element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_scale_up));
                    }
                }
            }
        }
    }

    public void showForMenu()
    {
        pageName.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_name_show_left));

        if (elementMenuShown)
        {
            Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(450);
            fadeIn.setFillAfter(true);

            MarketElement element = elementMenu.getDisplayedElement();
            MarketElement.Type type = elementMenu.getDisplayedElementType();

            float centerX = (float) elementsLayout.getWidth() / 4;
            float centerY = (float) elementsLayout.getHeight() / 2;

            if (type == MarketElement.Type.RIGHT)
            {
                centerX += (float) elementsLayout.getWidth() / 2;
            }

            float elemPosX = element.getX() + (float) element.getWidth() / 2;
            float elemPosY = element.getY() + (float) element.getHeight() / 2;

            if (type == MarketElement.Type.LEFT)
            {
                fadeIn.setStartOffset(250);
            }
            else
            {
                fadeIn.setStartOffset(150);
            }

            fadeIn.setAnimationListener(new Animation.AnimationListener()
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
                    elementMenu.clearAnimation();
                }
            });

            AnimationSet set = new AnimationSet(false);
            set.setFillAfter(true);

            Animation scaleAnim = new ScaleAnimation(1.6f, 1.6f,
                    1.6f, 1.6f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(700);

            Animation fromCenterAnim = new TranslateAnimation(Animation.ABSOLUTE, centerX - elemPosX,
                    Animation.ABSOLUTE, centerX - elemPosX,
                    Animation.ABSOLUTE, elementsLayout.getHeight() + (float) element.getHeight() / 2,
                    Animation.ABSOLUTE, centerY - elemPosY);
            fromCenterAnim.setDuration(700);

            set.addAnimation(scaleAnim);
            set.addAnimation(fromCenterAnim);

            elementMenu.startAnimation(fadeIn);

            element.show(set);
        }
        else
        {
            for (int i = 0; i < elements.size(); i++)
            {
                for (int j = MAX_PER_ROW / 2; j < elements.get(i).size(); j++)
                {
                    MarketElement element = elements.get(i).get(j);
                    element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_bottom_show_slow));
                }
            }

            for (int i = 0; i < elements.size(); i++)
            {
                for (int j = 0; j < Math.min(elements.get(i).size(), MAX_PER_ROW / 2); j++)
                {
                    MarketElement element = elements.get(i).get(j);
                    element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_left_show));
                }
            }
        }
    }

    public void hideForMenu()
    {
        pageName.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_name_hide_left));

        if (elementMenuShown)
        {
            Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(450);
            fadeOut.setFillAfter(true);

            MarketElement element = elementMenu.getDisplayedElement();
            MarketElement.Type type = elementMenu.getDisplayedElementType();

            float centerX = (float) elementsLayout.getWidth() / 4;
            float centerY = (float) elementsLayout.getHeight() / 2;

            if (type == MarketElement.Type.RIGHT)
            {
                centerX += (float) elementsLayout.getWidth() / 2;
            }

            float elemPosX = element.getX() + (float) element.getWidth() / 2;
            float elemPosY = element.getY() + (float) element.getHeight() / 2;

            AnimationSet set = new AnimationSet(false);
            set.setFillAfter(true);

            Animation scaleAnim = new ScaleAnimation(1.6f, 1.6f,
                    1.6f, 1.6f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(700);

            Animation fromCenterAnim = new TranslateAnimation(Animation.ABSOLUTE, centerX - elemPosX,
                    Animation.ABSOLUTE, centerX - elemPosX,
                    Animation.ABSOLUTE, centerY - elemPosY,
                    Animation.ABSOLUTE, elementsLayout.getHeight() + (float) element.getHeight() / 2);
            fromCenterAnim.setDuration(700);

            if (type == MarketElement.Type.LEFT)
            {
                scaleAnim.setStartOffset(100);
                fromCenterAnim.setStartOffset(100);
            }
            else
            {
                fadeOut.setStartOffset(100);
            }

            elementMenu.startAnimation(fadeOut);

            set.addAnimation(scaleAnim);
            set.addAnimation(fromCenterAnim);

            element.hide(set);
        }
        else
        {
            for (int i = 0; i < elements.size(); i++)
            {
                for (int j = MAX_PER_ROW / 2; j < elements.get(i).size(); j++)
                {
                    MarketElement element = elements.get(i).get(j);
                    element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_bottom_hide));
                }
            }

            for (int i = 0; i < elements.size(); i++)
            {
                for (int j = 0; j < Math.min(elements.get(i).size(), MAX_PER_ROW / 2); j++)
                {
                    MarketElement element = elements.get(i).get(j);
                    element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_left_hide_now));
                }
            }
        }
    }

    public void showForCoins()
    {
        pageName.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_name_show_right));

        if (elementMenuShown)
        {
            MarketElement element = elementMenu.getDisplayedElement();
            MarketElement.Type type = elementMenu.getDisplayedElementType();

            if (type == MarketElement.Type.LEFT)
            {
                float centerX = (float) elementsLayout.getWidth() / 4;
                float centerY = (float) elementsLayout.getHeight() / 2;

                float elemPosX = element.getX() + (float) element.getWidth() / 2;
                float elemPosY = element.getY() + (float) element.getHeight() / 2;

                AnimationSet set = new AnimationSet(false);
                set.setFillAfter(true);

                Animation scaleAnim = new ScaleAnimation(1.6f, 1.6f,
                        1.6f, 1.6f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnim.setDuration(700);

                Animation fromCenterAnim = new TranslateAnimation(Animation.ABSOLUTE, centerX - elemPosX,
                        Animation.ABSOLUTE, centerX - elemPosX,
                        Animation.ABSOLUTE, elementsLayout.getHeight() + (float) element.getHeight() / 2,
                        Animation.ABSOLUTE, centerY - elemPosY);
                fromCenterAnim.setDuration(700);

                set.addAnimation(scaleAnim);
                set.addAnimation(fromCenterAnim);

                element.show(set);
            }

            if (type == MarketElement.Type.RIGHT)
            {
                Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(450);
                fadeIn.setStartOffset(300);
                fadeIn.setFillAfter(true);

                fadeIn.setAnimationListener(new Animation.AnimationListener()
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
                        elementMenu.clearAnimation();
                    }
                });

                elementMenu.startAnimation(fadeIn);
            }
        }
        else
        {
            for (int i = 0; i < elements.size(); i++)
            {
                for (int j = 0; j < Math.min(elements.get(i).size(), MAX_PER_ROW / 2); j++)
                {
                    MarketElement element = elements.get(i).get(j);
                    element.show(AnimationUtils.loadAnimation(activity, R.anim.market_element_bottom_show_fast));
                }
            }
        }
    }

    public void hideForCoins()
    {
        pageName.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.market_name_hide_right));

        if (elementMenuShown)
        {
            MarketElement element = elementMenu.getDisplayedElement();
            MarketElement.Type type = elementMenu.getDisplayedElementType();

            if (type == MarketElement.Type.LEFT)
            {
                float centerX = (float) elementsLayout.getWidth() / 4;
                float centerY = (float) elementsLayout.getHeight() / 2;

                float elemPosX = element.getX() + (float) element.getWidth() / 2;
                float elemPosY = element.getY() + (float) element.getHeight() / 2;

                AnimationSet set = new AnimationSet(false);
                set.setFillAfter(true);
                set.setStartOffset(50);

                Animation scaleAnim = new ScaleAnimation(1.6f, 1.6f,
                        1.6f, 1.6f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnim.setDuration(700);

                Animation fromCenterAnim = new TranslateAnimation(Animation.ABSOLUTE, centerX - elemPosX,
                        Animation.ABSOLUTE, centerX - elemPosX,
                        Animation.ABSOLUTE, centerY - elemPosY,
                        Animation.ABSOLUTE, elementsLayout.getHeight() + (float) element.getHeight() / 2);
                fromCenterAnim.setDuration(700);

                set.addAnimation(scaleAnim);
                set.addAnimation(fromCenterAnim);

                element.hide(set);
            }

            if (type == MarketElement.Type.RIGHT)
            {
                Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(450);
                fadeOut.setFillAfter(true);

                elementMenu.startAnimation(fadeOut);
            }
        }
        else
        {
            for (int i = 0; i < elements.size(); i++)
            {
                for (int j = 0; j < Math.min(elements.get(i).size(), MAX_PER_ROW / 2); j++)
                {
                    MarketElement element = elements.get(i).get(j);
                    element.hide(AnimationUtils.loadAnimation(activity, R.anim.market_element_bottom_hide));
                }
            }
        }
    }

    public void click(View v)
    {
        if (elementMenuShown)
        {
            if (v.getId() == elementMenu.getDummyId())
            {
                // do nothing
            }
            else if (v.getId() == elementMenu.getActionButtonId())
            {
                String elementStatus = elementMenu.getDisplayedElement().getStatus();

                if (elementStatus.equals("locked"))
                {
                    if (coinCollector.compareTo(elementMenu.getPrice()) >= 0)
                    {
                        soundsPlayer.play("assets/sounds/market/buy_item.mp3", false);

                        coinCollector.subCoins(elementMenu.getPrice());
                        elementMenu.buy();
                        controller.update();
                    }
                    else
                    {
                        soundsPlayer.play("assets/sounds/market/not_enough_money.mp3", false);

                        coinCollector.showNotEnough();
                        elementMenu.showNotEnough();
                    }
                }
                else if (elementStatus.equals("bought"))
                {
                    for (int i = 0; i < elements.size(); i++)
                    {
                        for (int j = 0; j < elements.get(i).size(); j++)
                        {
                            if (elements.get(i).get(j).getStatus().equals("equipped"))
                            {
                                elements.get(i).get(j).drop();
                            }
                        }
                    }

                    soundsPlayer.play("assets/sounds/market/equip_item.mp3", false);

                    elementMenu.equip();
                    controller.update();
                }
                else if (elementStatus.equals("equipped"))
                {
                    soundsPlayer.play("assets/sounds/market/drop_item.mp3", false);

                    elementMenu.drop();
                    controller.update();
                }
            }
            else
            {
                if (elementMenu.canPlayAnimation())
                {
                    elementMenuShown = false;

                    soundsPlayer.play("assets/sounds/market/hide_item.mp3", false);

                    hideElementMenuFor(elementMenu.getDisplayedElement(), elementMenu.getDisplayedElementType());

                    elementMenu.hide();
                }
            }
        }
        else
        {
            if (canPlayAnimation())
            {
                for (int i = 0; i < elements.size(); i++)
                {
                    for (int j = 0; j < elements.get(i).size(); j++)
                    {
                        MarketElement element = elements.get(i).get(j);

                        if (v.getId() == element.getId())
                        {
                            elementMenuShown = true;

                            soundsPlayer.play("assets/sounds/market/show_item.mp3", false);

                            MarketElement.Type type = j < MAX_PER_ROW / 2 ? MarketElement.Type.LEFT : MarketElement.Type.RIGHT;

                            showElementMenuFor(elements.get(i).get(j), type);

                            elementMenu.setDisplayedElement(elements.get(i).get(j), type);
                            elementMenu.show();

                            break;
                        }
                    }
                }
            }
        }
    }

    public void saveTo(DataBaseHelper dataBaseHelper)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            for (int j = 0; j < elements.get(i).size(); j++)
            {
                elements.get(i).get(j).saveTo(dataBaseHelper);
            }
        }
    }

    public void displayEnoughToBuy()
    {
        for (int i = 0; i < elements.size(); i++)
        {
            for (int j = 0; j < elements.get(i).size(); j++)
            {
                String elementStatus = elements.get(i).get(j).getStatus();

                if (elementStatus.equals("locked"))
                {
                    MarketElement element = elements.get(i).get(j);

                    if (coinCollector.compareTo(element.getPrice()) >= 0)
                    {
                        element.displayEnoughToBuy(true);
                    }
                    else
                    {
                        element.displayEnoughToBuy(false);
                    }
                }
            }
        }
    }

    public void stop()
    {
        soundsPlayer.stop();
    }

    public FrameLayout getMenuLayout()
    {
        return elementsLayout;
    }

    public String getFormattedName()
    {
        return name.toLowerCase();
    }

    public int getAmount()
    {
        return amount;
    }

    public boolean canPlayAnimation()
    {
        MarketElement disElement = elementMenu.getDisplayedElement();

        return (elementMenu.canPlayAnimation() && (disElement == null || disElement.canPlayAnimation()));
    }
}
