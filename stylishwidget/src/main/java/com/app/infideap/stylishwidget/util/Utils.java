package com.app.infideap.stylishwidget.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.List;

/**
 * Created by Shiburagi on 14/07/2016.
 */
public class Utils {
    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static List<Integer> getIds(String textValue, List<Integer> integers) {
        String delimiter = ";";
        String subdelimiter = "\\|";

        String[] temp = textValue.split(delimiter);

        for (String aTemp : temp) {
            String[] split = aTemp.split(subdelimiter);
            try {
                integers.add(Integer.parseInt(split[0]));
            } catch (NumberFormatException e) {

            }
        }

        return integers;
    }
}
