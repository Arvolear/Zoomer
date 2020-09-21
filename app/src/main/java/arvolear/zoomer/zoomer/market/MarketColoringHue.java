package arvolear.zoomer.zoomer.market;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.utility.ColorFilterGenerator;

public class MarketColoringHue extends MarketColoring
{
    private ColorFilter colorFilter;
    private Paint paint;

    public MarketColoringHue(AppCompatActivity activity, int index)
    {
        super(activity, index);

        paint = new Paint();

        init();
    }

    private void init()
    {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorFilterGenerator.adjustHue(colorMatrix, (float)offset);

        colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
    }

    @Override
    public Bitmap colorBitmap(Bitmap input, int index)
    {
        Bitmap output = Bitmap.createBitmap(input, 0, 0, input.getWidth() - 1, input.getHeight() - 1);

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }
}
