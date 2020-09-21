package arvolear.zoomer.zoomer.market;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.game.GameController;
import arvolear.zoomer.zoomer.utility.ColorFilterGenerator;

public class MarketColoringGradient extends MarketColoring
{
    private static final int MAX_OFFSET = GameController.ASSETS_NUMBER / 4;

    public MarketColoringGradient(AppCompatActivity activity, int index)
    {
        super(activity, index);
    }

    private Paint configurePaint(int index)
    {
        ColorMatrix colorMatrix = new ColorMatrix();
        Paint paint = new Paint();

        double offset = index % MAX_OFFSET;
        int type = (index / MAX_OFFSET) % 2;
        offset -= type * MAX_OFFSET;

        ColorFilterGenerator.adjustHue(colorMatrix, (float) offset);

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);

        return paint;
    }

    @Override
    public Bitmap colorBitmap(Bitmap input, int index)
    {
        Bitmap output = Bitmap.createBitmap(input, 0, 0, input.getWidth() - 1, input.getHeight() - 1);

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input, 0, 0, configurePaint(index));

        return output;
    }
}
