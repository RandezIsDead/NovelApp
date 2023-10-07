package com.randez_trying.novel.Views.CardStack.Internal;

import android.view.animation.Interpolator;

import com.randez_trying.novel.Views.CardStack.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}