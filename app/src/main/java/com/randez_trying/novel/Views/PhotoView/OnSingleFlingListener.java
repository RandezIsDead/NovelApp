package com.randez_trying.novel.Views.PhotoView;

import android.view.MotionEvent;

public interface OnSingleFlingListener {
    boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}