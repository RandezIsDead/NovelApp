package com.randez_trying.novel.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.ActionMode;
import androidx.appcompat.widget.AppCompatEditText;
import com.randez_trying.novel.R;

import java.util.Objects;

public class VerificationEditText extends AppCompatEditText {
    private OnClickListener mClickListener;
    private float mLineSpacing = 8.0f;
    private float mLineStroke = 2.0f;
    private Paint mLinesPaint;
    private float mNumChars = 4.0f;
    private float mSpace = 16.0f;

    public VerificationEditText(Context context) {
        super(context);
    }

    public VerificationEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public VerificationEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        float f = context.getResources().getDisplayMetrics().density;
        this.mLineStroke *= f;
        Paint paint = new Paint(getPaint());
        this.mLinesPaint = paint;
        paint.setStrokeWidth(this.mLineStroke);
        this.mLinesPaint.setColor(getResources().getColor(R.color.grey));
        setBackgroundResource(0);
        this.mSpace *= f;
        this.mLineSpacing = f * this.mLineSpacing;
        this.mNumChars = 4.0f;
        super.setOnClickListener(v -> {
            setSelection(Objects.requireNonNull(getText()).length());
            OnClickListener onClickListener = this.mClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    public void onDraw(Canvas canvas) {
        float f;
        int i;
        int width = (getWidth() - getPaddingRight()) - getPaddingLeft();
        float f2 = this.mSpace;
        if (f2 < 0.0f) {
            f = ((float) width) / ((this.mNumChars * 2.0f) - 1.0f);
        } else {
            float f3 = this.mNumChars;
            f = (((float) width) - (f2 * (f3 - 1.0f))) / f3;
        }
        int paddingLeft = getPaddingLeft();
        int height = getHeight() - getPaddingBottom();
        Editable text = getText();
        int length = text.length();
        float[] fArr = new float[length];
        getPaint().getTextWidths(getText(), 0, length, fArr);
        int i2 = 0;
        while (((float) i2) < this.mNumChars) {
            float f4 = (float) paddingLeft;
            float f5 = (float) height;
            canvas.drawLine(f4, f5, f4 + f, f5, this.mLinesPaint);
            if (getText().length() > i2) {
                i = i2;
                canvas.drawText(text, i2, i2 + 1, ((f / 2.0f) + f4) - (fArr[0] / 2.0f), f5 - this.mLineSpacing, getPaint());
            } else {
                i = i2;
            }
            float f6 = this.mSpace;
            paddingLeft = (int) (f4 + (f6 < 0.0f ? f * 2.0f : f6 + f));
            i2 = i + 1;
        }
    }
}
