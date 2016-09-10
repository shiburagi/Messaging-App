package com.app.infideap.readcontact.util;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

/**
 * Created by Shiburagi on 11/09/2016.
 */
public class SetLineOverlap implements LineHeightSpan {
    private int originalBottom = 15;        // init value ignored
    private int originalDescent = 13;       // init value ignored
    private Boolean overlap;                // saved state
    private Boolean overlapSaved = false;   // ensure saved values only happen once

    public SetLineOverlap(Boolean overlap) {
        this.overlap = overlap;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v,
                             Paint.FontMetricsInt fm) {
        if (overlap) {
            if (!overlapSaved) {
                originalBottom = fm.bottom;
                originalDescent = fm.descent;
                overlapSaved = true;
            }
            fm.bottom += fm.top;
            fm.descent += fm.top;
        } else {
            // restore saved values
            fm.bottom = originalBottom;
            fm.descent = originalDescent;
            overlapSaved = false;
        }
    }
}
