package arvolear.zoomer.zoomer.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.TreeMap;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.utility.Blurer;
import arvolear.zoomer.zoomer.utility.ColorFilterGenerator;

public class Fractal extends View
{
    private AppCompatActivity activity;
    private GameController controller;

    private FractalLoader fractalLoader;
    private Blurer blurer;

    private int assetsNumber;
    private static final int SUPPLY = 5;

    private ArrayList < Bitmap > blurredFractal;
    private TreeMap < Integer, Bitmap > fractalTree;
    private int currentIndex;
    private Bitmap currentBitmap;

    private FrameLayout fractalLayout;
    private Rect frameToRender;
    private RectF whereToRender;

    private Paint paint;
    private Matrix matrix;
    private ColorMatrix colorMatrix;
    private ColorFilter colorFilter;

    public Fractal(AppCompatActivity activity, GameController controller, String directory, int currentIndex, int assetsNumber)
    {
        super(activity);

        this.activity = activity;
        this.controller = controller;
        this.assetsNumber = assetsNumber;

        blurer = new Blurer(activity);

        fractalLayout = activity.findViewById(R.id.fractalLayout);
        fractalLayout.addView(this);

        blurredFractal = new ArrayList<>();
        fractalTree = new TreeMap<>();

        fractalLoader = new FractalLoader(activity, fractalTree, directory, assetsNumber);

        paint = new Paint();
        matrix = new Matrix();
        colorMatrix = new ColorMatrix();

        this.currentIndex = currentIndex;

        init();
    }

    private void init()
    {
        currentIndex %= assetsNumber;

        if (SUPPLY > 1)
        {
            fractalLoader.loadNBitmapsFromAssets(currentIndex + 1, SUPPLY, false);
            fractalLoader.loadNBitmapsFromAssets(currentIndex - 1, -SUPPLY, false);
        }

        ColorFilterGenerator.adjustSaturation(colorMatrix, 1.1f);
        colorFilter = new ColorMatrixColorFilter(colorMatrix);

        fractalLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                fractalLoader.loadNBitmapsFromAssets(currentIndex, 1, true);
                currentBitmap = fractalTree.get(currentIndex);

                float aspectRatio = (float)fractalLayout.getWidth() / (float)fractalLayout.getHeight();

                int bitmapWidth = (int)(currentBitmap.getHeight() * aspectRatio);
                bitmapWidth = Math.min(bitmapWidth, currentBitmap.getWidth());

                int bitmapWidthOffset = (currentBitmap.getWidth() - bitmapWidth) / 2;

                frameToRender = new Rect(bitmapWidthOffset, 0, bitmapWidth + bitmapWidthOffset, currentBitmap.getHeight());
                whereToRender = new RectF(0, 0, fractalLayout.getWidth(), fractalLayout.getHeight());

                fractalLayout.setOnTouchListener(controller);
                fractalLayout.setOnClickListener(controller);

                invalidate();
            }
        });
    }

    public void show(int offset)
    {
        if (offset > 0)
        {
            fractalLoader.loadNBitmapsFromAssets(currentIndex + SUPPLY + 1, offset, false);

            for (int i = currentIndex + 1; i <= currentIndex + offset; i++)
            {
                int newIndex = (i + assetsNumber) % assetsNumber;

                currentBitmap = fractalTree.get(newIndex);
                invalidate();

                int toRemoveIndex = (i - SUPPLY - 1 + assetsNumber) % assetsNumber;
                fractalTree.remove(toRemoveIndex);
            }
        }
        else if (offset < 0)
        {
            fractalLoader.loadNBitmapsFromAssets(currentIndex - SUPPLY - 1, offset, false);

            for (int i = currentIndex - 1; i >= currentIndex + offset; i--)
            {
                int newIndex = (i + assetsNumber) % assetsNumber;

                currentBitmap = fractalTree.get(newIndex);
                invalidate();

                int toRemoveIndex = (i + SUPPLY + 1 + assetsNumber) % assetsNumber;
                fractalTree.remove(toRemoveIndex);
            }
        }

        currentIndex = (currentIndex + offset + assetsNumber) % assetsNumber;
    }

    public boolean canShow(int offset)
    {
        if (offset > 0)
        {
            for (int i = currentIndex + 1; i <= currentIndex + offset; i++)
            {
                int newIndex = (i + assetsNumber) % assetsNumber;

                if (!fractalTree.containsKey(newIndex))
                {
                    return false;
                }
            }
        }
        else if (offset < 0)
        {
            for (int i = currentIndex - 1; i >= currentIndex + offset; i--)
            {
                int newIndex = (i + assetsNumber) % assetsNumber;

                if (!fractalTree.containsKey(newIndex))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void deBlur(final int time)
    {
        Thread deBlurThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = blurredFractal.size() - 1; i >= 0; i--)
                {
                    try
                    {
                        currentBitmap = blurredFractal.get(i);
                    }
                    catch (Exception ex)
                    {
                        return;
                    }

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            invalidate();
                        }
                    });

                    try
                    {
                        Thread.sleep(time / blurredFractal.size());
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        });

        deBlurThread.start();
    }

    public void blur(final float radius, final int time)
    {
        blurredFractal.clear();
        currentBitmap = fractalTree.get(currentIndex);

        Thread blurThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < 10; i++)
                {
                    blurredFractal.add(currentBitmap);

                    currentBitmap = blurer.blur(currentBitmap, radius * (i + 1));

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            invalidate();
                        }
                    });

                    try
                    {
                        Thread.sleep(time / 10);
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        });

        blurThread.start();
    }

    public boolean isDeBlurred()
    {
        // FIXME pointer comparison?
        return currentBitmap == fractalTree.get(currentIndex);
    }

    public void clear()
    {
        blurredFractal.clear();
        fractalTree.clear();

        currentIndex = 0;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        matrix.reset();
        paint.setColorFilter(colorFilter);

        if (currentBitmap != null)
        {
            canvas.drawBitmap(currentBitmap, frameToRender, whereToRender, paint);
        }
    }
}