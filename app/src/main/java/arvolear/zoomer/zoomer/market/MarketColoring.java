package arvolear.zoomer.zoomer.market;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

abstract public class MarketColoring
{
    protected AppCompatActivity activity;

    protected double offset;

    protected String type;
    protected int index;

    public MarketColoring(AppCompatActivity activity, int index)
    {
        this.activity = activity;
        this.type = "coloring";
        this.index = index;

        offset = Double.parseDouble(activity.getResources().getString(activity.getResources().getIdentifier(
                type + "_" + index + "_offset", "string", activity.getPackageName())));

    }

    abstract public Bitmap colorBitmap(Bitmap input, int index);

    public double getOffset()
    {
        return offset;
    }
}
