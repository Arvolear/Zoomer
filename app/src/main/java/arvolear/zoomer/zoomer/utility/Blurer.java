package arvolear.zoomer.zoomer.utility;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.appcompat.app.AppCompatActivity;

public class Blurer
{
    private AppCompatActivity activity;

    private RenderScript renderScript;
    private ScriptIntrinsicBlur blurScript;

    public Blurer(AppCompatActivity activity)
    {
        this.activity = activity;

        renderScript = RenderScript.create(activity);
        blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
    }

    public Bitmap blur(Bitmap toBlur, float radius)
    {
        Bitmap blurred = Bitmap.createBitmap(toBlur.getWidth(), toBlur.getHeight(), Bitmap.Config.ARGB_8888);

        Allocation allIn = Allocation.createFromBitmap(renderScript, toBlur);
        Allocation allOut = Allocation.createFromBitmap(renderScript, blurred);

        blurScript.setRadius(radius);

        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        allOut.copyTo(blurred);

        return blurred;
    }
}
