package arvolear.zoomer.zoomer.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.InputStream;
import java.util.TreeMap;

import arvolear.zoomer.zoomer.db.GameConfigurator;
import arvolear.zoomer.zoomer.expansions.ExpansionController;

public class AssetsLoader
{
    private AppCompatActivity activity;
    private GameConfigurator configurator;

    private ZipResourceFile expansionFile;
    private boolean loadFromLocalAssets = false;

    private TreeMap < Integer, Bitmap > tree;

    public AssetsLoader(AppCompatActivity activity, TreeMap < Integer, Bitmap > tree)
    {
        this.activity = activity;
        this.configurator = new GameConfigurator(activity);
        this.tree = tree;

        try
        {
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(activity, ExpansionController.EXP_VERSION, ExpansionController.EXP_VERSION);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void loadBitmapFromAssets(final int index, final String path, final boolean applyFilter, boolean join)
    {
        Thread loader = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                InputStream stream = null;

                try
                {
                    if (loadFromLocalAssets)
                    {
                        stream = activity.getAssets().open(path);
                    }
                    else
                    {
                        stream = expansionFile.getInputStream(path);
                    }

                    Bitmap newBitmap = BitmapFactory.decodeStream(stream);

                    if (applyFilter)
                    {
                        newBitmap = configurator.colorBitmap(newBitmap, index);
                    }

                    synchronized (AssetsLoader.this)
                    {
                        tree.put(index, newBitmap);
                    }
                }
                catch (Exception ex)
                {
                }
                finally
                {
                    try
                    {
                        if (stream != null)
                        {
                            stream.close();
                        }
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        });

        loader.start();

        if (join)
        {
            try
            {
                loader.join();
            }
            catch (Exception ex)
            {
            }
        }
    }

    public void setLoadFromLocalAssets(boolean loadFromLocalAssets)
    {
        this.loadFromLocalAssets = loadFromLocalAssets;
    }
}
