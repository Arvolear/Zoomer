package arvolear.zoomer.zoomer.utility;

import android.graphics.ColorMatrix;

public class ColorFilterGenerator
{
    public static void adjustHue(ColorMatrix cm, float value)
    {
        value = cleanValue(value, 180.0f) / 180.0f * (float) Math.PI;

        if (value == 0)
        {
            return;
        }

        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);

        float lumR = 0.333f;
        float lumG = 0.333f;
        float lumB = 0.333f;

        float[] mat = new float[]
                {
                        lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0.0f, 0.0f,
                        lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0.0f, 0.0f,
                        lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f, 1.0f // needed for proper concatenation
                };

        cm.postConcat(new ColorMatrix(mat));
    }

    public static void adjustBrightness(ColorMatrix cm, float value)
    {
        value = cleanValue(value, 100.0f);

        if (value == 0.0f)
        {
            return;
        }

        float[] mat = new float[]
                {
                        1.0f, 0.0f, 0.0f, 0.0f, value,
                        0.0f, 1.0f, 0.0f, 0.0f, value,
                        0.0f, 0.0f, 1.0f, 0.0f, value,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f, 1.0f // needed for proper concatenation
                };

        cm.postConcat(new ColorMatrix(mat));
    }

    public static void invert(ColorMatrix cm)
    {
        float[] mat = new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f, 1.0f // needed for proper concatenation
                };

        cm.postConcat(new ColorMatrix(mat));
    }

    public static void adjustSaturation(ColorMatrix cm, float value)
    {
        value = cleanValue(value, 100.0f);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(value);

        cm.postConcat(matrix);
    }

    private static float cleanValue(float val, float limit)
    {
        return Math.min(limit, Math.max(-limit, val));
    }
}
