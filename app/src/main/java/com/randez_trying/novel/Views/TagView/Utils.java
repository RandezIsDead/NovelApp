package com.randez_trying.novel.Views.TagView;

import android.content.Context;
import android.graphics.Color;

public class Utils {

    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int manipulateColorBrightness(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        if (r > 127) r = 255 - Math.round((255 - r) * factor);
        if (g > 127) g = 255 - Math.round((255 - g) * factor);
        if (b > 127) b = 255 - Math.round((255 - b) * factor);

        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255)
        );
    }
}