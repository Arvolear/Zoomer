package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TreeMap;

import arvolear.zoomer.zoomer.utility.AssetsLoader;

public class FractalLoader extends AssetsLoader
{
    private String directory;
    private int assetsNumber;

    public FractalLoader(AppCompatActivity activity, TreeMap < Integer, Bitmap > fractalTree, String directory, int assetsNumber)
    {
        super(activity, fractalTree);

        this.directory = directory;
        this.assetsNumber = assetsNumber;
    }

    public void loadNBitmapsFromAssets(int index, int toLoad, final boolean join)
    {
        index = (index + assetsNumber) % assetsNumber;

        if (toLoad > 0)
        {
            for (int i = index; i < index + toLoad; i++)
            {
                int newIndex = i % assetsNumber;
                String path = directory + "/" + newIndex + ".jpg";

                loadBitmapFromAssets(newIndex, path, true, join);
            }
        }
        else if (toLoad < 0)
        {
            for (int i = index; i > index + toLoad; i--)
            {
                int newIndex = i % assetsNumber;
                String path = directory + "/" + newIndex + ".jpg";

                loadBitmapFromAssets(newIndex, path, true, join);
            }
        }
    }
}