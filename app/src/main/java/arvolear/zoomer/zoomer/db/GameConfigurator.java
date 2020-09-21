package arvolear.zoomer.zoomer.db;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.market.MarketBooster;
import arvolear.zoomer.zoomer.market.MarketColoring;
import arvolear.zoomer.zoomer.market.MarketColoringBlack;
import arvolear.zoomer.zoomer.market.MarketColoringGradient;
import arvolear.zoomer.zoomer.market.MarketColoringHue;
import arvolear.zoomer.zoomer.market.MarketColoringInverse;
import arvolear.zoomer.zoomer.market.MarketColoringWhite;
import arvolear.zoomer.zoomer.market.MarketController;

public class GameConfigurator
{
    private AppCompatActivity activity;

    private DataBaseHelper dataBaseHelper;

    private int boosterIndex = -1;
    private int coloringIndex = -1;

    private MarketBooster booster;
    private MarketColoring coloring;

    private double maxZoom;
    private String coins;

    public GameConfigurator(AppCompatActivity activity)
    {
        this.activity = activity;
        this.dataBaseHelper = DataBaseHelper.getDatabaseHelper(activity);

        init();
    }

    public void init()
    {
        booster = null;
        coloring = null;

        maxZoom = dataBaseHelper.getMaxZoom();
        coins = dataBaseHelper.getCoins();

        String index = dataBaseHelper.getEquippedIndex("boosters");

        if (!index.equals(""))
        {
            boosterIndex = Integer.parseInt(index);
            booster = new MarketBooster(activity, boosterIndex);
        }

        index = dataBaseHelper.getEquippedIndex("coloring");

        if (!index.equals(""))
        {
            coloringIndex = Integer.parseInt(index);

            switch (coloringIndex)
            {
                case MarketController.COLORING_BLACK_IND:
                {
                    coloring = new MarketColoringBlack(activity, Integer.parseInt(index));
                    break;
                }
                case MarketController.COLORING_WHITE_IND:
                {
                    coloring = new MarketColoringWhite(activity, Integer.parseInt(index));
                    break;
                }
                case MarketController.COLORING_INVERSE_IND:
                {
                    coloring = new MarketColoringInverse(activity, Integer.parseInt(index));
                    break;
                }
                case MarketController.COLORING_GRADIENT_IND:
                {
                    coloring = new MarketColoringGradient(activity, Integer.parseInt(index));
                    break;
                }
                default:
                {
                    coloring = new MarketColoringHue(activity, Integer.parseInt(index));
                    break;
                }
            }
        }
    }

    public double reduceDifficulty(double zoom)
    {
        if (booster != null)
        {
            return booster.reduceDifficulty(zoom);
        }

        return zoom;
    }

    public Bitmap colorBitmap(Bitmap input, int index)
    {
        if (coloring != null)
        {
            return coloring.colorBitmap(input, index);
        }

        return input;
    }

    public double getMaxZoom()
    {
        return maxZoom;
    }

    public String getCoins()
    {
        return coins;
    }

    public int getBoosterIndex()
    {
        return boosterIndex;
    }

    public int getColoringIndex()
    {
        return coloringIndex;
    }

    public DataBaseHelper getDataBaseHelper()
    {
        return dataBaseHelper;
    }
}
