package arvolear.zoomer.zoomer.market;

import androidx.appcompat.app.AppCompatActivity;

public class MarketBooster
{
    private AppCompatActivity activity;

    private String type;
    private int index;

    private double difficultyReduction;

    public MarketBooster(AppCompatActivity activity, int index)
    {
        this.activity = activity;
        this.type = "boosters";
        this.index = index;

        init();
    }

    private void init()
    {
        difficultyReduction = Double.parseDouble(activity.getResources().getString(activity.getResources().getIdentifier(
                type + "_" + index + "_reduction", "string", activity.getPackageName())));
    }

    public double reduceDifficulty(double zoom)
    {
        return zoom / difficultyReduction;
    }
}
