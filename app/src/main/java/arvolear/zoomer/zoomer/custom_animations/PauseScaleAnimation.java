package arvolear.zoomer.zoomer.custom_animations;

import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

public class PauseScaleAnimation extends ScaleAnimation
{
    private long elapsed;
    private boolean paused = false;

    public PauseScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue)
    {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation)
    {
        if (paused && elapsed == 0)
        {
            elapsed = currentTime - getStartTime();
        }

        if (paused)
        {
            setStartTime(currentTime - elapsed);
        }

        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause()
    {
        elapsed = 0;
        paused = true;
    }

    public void resume()
    {
        paused = false;
    }
}
