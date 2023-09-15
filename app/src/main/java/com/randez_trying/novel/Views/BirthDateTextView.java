package com.randez_trying.novel.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.randez_trying.novel.R;

public class BirthDateTextView extends AppCompatTextView {

    private float mLineSpacing = 8.0f;
    private float mLineStroke = 1.0f;
    public Paint[] paints = new Paint[8];
    private float mNumChars = 8.0f;
    private float mSpace = 8f;
    public BirthDateTextView(@NonNull Context context) {
        super(context);
    }

    public BirthDateTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BirthDateTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float f = context.getResources().getDisplayMetrics().density;
        this.mLineStroke *= f;
        for (int i = 0; i < paints.length; i++) {
            paints[i] = new Paint(getPaint());
            paints[i].setStrokeWidth(this.mLineStroke);
            paints[i].setColor(getResources().getColor(R.color.other_grey));
        }
        this.mSpace *= f;
        this.mLineSpacing = f * this.mLineSpacing;
        this.mNumChars = 8.0f;
    }

    public void onDraw(Canvas canvas) {
        float f;
        int i;
        int width = (getWidth() - getPaddingRight()) - getPaddingLeft();
        float f2 = this.mSpace;
        if (f2 < 0.0f) {
            f = (((float) width) / ((this.mNumChars * 2.0f) - 1.0f)) - 4f;
        } else {
            float f3 = this.mNumChars;
            f = ((((float) width) - (f2 * (f3 - 1.0f))) / f3) - 4f;
        }
        int height = getHeight() - getPaddingBottom();
        CharSequence text = getText();
        int length = text.length();
        @SuppressLint("DrawAllocation")
        float[] fArr = new float[length];
        getPaint().getTextWidths(getText(), 0, length, fArr);
        int i2 = 0;

        float f5 = (float) height;
        int paddingLeft = getPaddingLeft();
        int counter = 0;
        int sp = 0;
        while (((float) i2) < this.mNumChars) {
            if (counter == 2 || counter == 4) sp += (int) (this.mSpace);
            float f4 = (float) paddingLeft;
            if (getText().length() > i2) {
                i = i2;
                canvas.drawText(text, i2, i2 + 1, ((f / 2.0f) + f4) - (fArr[0] / 2.0f) + sp, f5 - this.mLineSpacing, paints[counter]);
            } else {
                i = i2;
            }
            float f6 = this.mSpace;
            paddingLeft = (int) (f4 + (f6 < 0.0f ? f * 2.0f : f6 + f));
            i2 = i + 1;
            counter++;
        }
    }
}
