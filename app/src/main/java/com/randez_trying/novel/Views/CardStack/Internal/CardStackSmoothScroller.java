package com.randez_trying.novel.Views.CardStack.Internal;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randez_trying.novel.Views.CardStack.CardStackLayoutManager;
import com.randez_trying.novel.Views.CardStack.CardStackListener;
import com.randez_trying.novel.Views.CardStack.RewindAnimationSetting;

public class CardStackSmoothScroller extends RecyclerView.SmoothScroller {

    public enum ScrollType {
        AutomaticSwipe,
        AutomaticRewind,
        ManualSwipe,
        ManualCancel
    }

    private final ScrollType type;
    private final CardStackLayoutManager manager;

    public CardStackSmoothScroller(
            ScrollType type,
            CardStackLayoutManager manager
    ) {
        this.type = type;
        this.manager = manager;
    }

    @Override
    protected void onSeekTargetStep(
            int dx,
            int dy,
            @NonNull RecyclerView.State state,
            @NonNull Action action
    ) {
        if (type == ScrollType.AutomaticRewind) {
            RewindAnimationSetting setting = manager.getCardStackSetting().rewindAnimationSetting;
            action.update(
                    -getDx(setting),
                    -getDy(setting),
                    setting.getDuration(),
                    setting.getInterpolator()
            );
        }
    }

    @Override
    protected void onTargetFound(
            @NonNull View targetView,
            @NonNull RecyclerView.State state,
            @NonNull Action action
    ) {
        int x = (int) targetView.getTranslationX();
        int y = (int) targetView.getTranslationY();
        AnimationSetting setting;
        switch (type) {
            case AutomaticSwipe:
                setting = manager.getCardStackSetting().swipeAnimationSetting;
                action.update(
                        -getDx(setting),
                        -getDy(setting),
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case AutomaticRewind:
            case ManualCancel:
                setting = manager.getCardStackSetting().rewindAnimationSetting;
                action.update(
                        x,
                        y,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case ManualSwipe:
                int dx = -x * 10;
                int dy = -y * 10;
                setting = manager.getCardStackSetting().swipeAnimationSetting;
                action.update(
                        dx,
                        dy,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
        }
    }

    @Override
    protected void onStart() {
        CardStackListener listener = manager.getCardStackListener();
        CardStackState state = manager.getCardStackState();
        switch (type) {
            case AutomaticSwipe:
                state.next(CardStackState.Status.AutomaticSwipeAnimating);
                listener.onCardDisappeared(manager.getTopView(), manager.getTopPosition());
                break;
            case AutomaticRewind:
            case ManualCancel:
                state.next(CardStackState.Status.RewindAnimating);
                break;
            case ManualSwipe:
                state.next(CardStackState.Status.ManualSwipeAnimating);
                listener.onCardDisappeared(manager.getTopView(), manager.getTopPosition());
                break;
        }
    }

    @Override
    protected void onStop() {
        CardStackListener listener = manager.getCardStackListener();
        switch (type) {
            case AutomaticSwipe:
                // Notify callback from CardStackLayoutManager
                break;
            case AutomaticRewind:
                listener.onCardRewound();
                listener.onCardAppeared(manager.getTopView(), manager.getTopPosition());
                break;
            case ManualSwipe:
                // Notify callback from CardStackLayoutManager
                break;
            case ManualCancel:
                listener.onCardCanceled();
                break;
        }
    }

    private int getDx(AnimationSetting setting) {
        CardStackState state = manager.getCardStackState();
        int dx = 0;
        switch (setting.getDirection()) {
            case Left:
                dx = -state.width * 2;
                break;
            case Right:
                dx = state.width * 2;
                break;
            case Top:
            case Bottom:
                break;
        }
        return dx;
    }

    private int getDy(AnimationSetting setting) {
        CardStackState state = manager.getCardStackState();
        int dy = 0;
        switch (setting.getDirection()) {
            case Left:
            case Right:
                dy = state.height / 4;
                break;
            case Top:
                dy = -state.height * 2;
                break;
            case Bottom:
                dy = state.height * 2;
                break;
        }
        return dy;
    }

}