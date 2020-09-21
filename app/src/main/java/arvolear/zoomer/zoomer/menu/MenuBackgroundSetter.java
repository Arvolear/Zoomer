package arvolear.zoomer.zoomer.menu;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.custom_animations.PauseScaleAnimation;
import arvolear.zoomer.zoomer.global_gui.BackgroundSetter;

public class MenuBackgroundSetter extends BackgroundSetter
{
    public MenuBackgroundSetter(AppCompatActivity activity, String directory, int offset, int duration)
    {
        super(activity, directory, offset, duration);
        init();

        configureAnimations();
    }

    private PauseScaleAnimation genScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY)
    {
        PauseScaleAnimation anim = new PauseScaleAnimation(fromX, toX, fromY, toY,
                Animation.RELATIVE_TO_SELF, pivotX,
                Animation.RELATIVE_TO_SELF, pivotY);
        anim.setStartOffset(offset);
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setFillAfter(true);

        return anim;
    }

    @Override
    protected void configureAnimations()
    {
        float fromX = 1.0f;
        float toX = 10.0f;
        float fromY = 1.0f;
        float toY = 10.0f;

        PauseScaleAnimation anim1In = genScaleAnimation(fromX, toX, fromY, toY, 0.27f, 0.55f);
        PauseScaleAnimation anim1Out = genScaleAnimation(toX, fromX, toY, fromY, 0.27f, 0.55f);

        PauseScaleAnimation anim2In = genScaleAnimation(fromX, toX, fromY, toY, 0.69f, 0.77f);
        PauseScaleAnimation anim2Out = genScaleAnimation(toX, fromX, toY, fromY, 0.69f, 0.77f);

        PauseScaleAnimation anim3In = genScaleAnimation(fromX, toX, fromY, toY, 0.52f, 0.25f);
        PauseScaleAnimation anim3Out = genScaleAnimation(toX, fromX, toY, fromY, 0.52f, 0.25f);

        PauseScaleAnimation anim4In = genScaleAnimation(fromX, toX, fromY, toY, 0.42f, 0.69f);
        PauseScaleAnimation anim4Out = genScaleAnimation(toX, fromX, toY, fromY, 0.42f, 0.69f);

        PauseScaleAnimation anim5In = genScaleAnimation(fromX, toX, fromY, toY, 0.725f, 0.44f);
        PauseScaleAnimation anim5Out = genScaleAnimation(toX, fromX, toY, fromY, 0.725f, 0.44f);

        animations.add(anim1In);
        animations.add(anim1Out);
        animations.add(anim2In);
        animations.add(anim2Out);
        animations.add(anim3In);
        animations.add(anim3Out);
        animations.add(anim4In);
        animations.add(anim4Out);
        animations.add(anim5In);
        animations.add(anim5Out);

        super.configureAnimations();
    }
}
