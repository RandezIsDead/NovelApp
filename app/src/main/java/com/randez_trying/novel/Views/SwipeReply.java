package com.randez_trying.novel.Views;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.randez_trying.novel.R;

import org.jetbrains.annotations.NotNull;

public class SwipeReply extends ItemTouchHelper.Callback {

    private Drawable imageDrawable;
    private Drawable shareRound;
    private RecyclerView.ViewHolder currentItemViewHolder;
    private View mView;
    private float replyButtonProgress = 0f;
    private long lastReplyButtonAnimationTime = 0;
    private boolean swipeBack = false;
    private boolean isVibrate = false;
    private boolean startTracking = false;
    private float density;
    private final Context context;
    private final SwipeControllerActions swipeControllerActions;
    public SwipeReply(@NotNull Context context, @NotNull SwipeControllerActions swipeControllerActions) {
        super();
        this.context = context;
        this.swipeControllerActions = swipeControllerActions;
        this.density = 1.0F;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        mView = viewHolder.itemView;
        imageDrawable = context.getDrawable(R.drawable.baseline_reply_24);
        shareRound = context.getDrawable(R.drawable.grey_circle);
        return ItemTouchHelper.Callback.makeMovementFlags(ACTION_STATE_IDLE, ItemTouchHelper.LEFT);
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) setTouchListener(recyclerView, viewHolder);
        super.onChildDraw(c, recyclerView, viewHolder, dX/2, 0, actionState, isCurrentlyActive);
        startTracking = true;
        currentItemViewHolder = viewHolder;
        drawReplyButton(c);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
        recyclerView.setOnTouchListener((view, motionEvent) -> {
            swipeBack = motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_UP;
            if (swipeBack) {
                if (Math.abs(mView.getTranslationX()) >= convertToDp(50)) {
                    swipeControllerActions.showReplyUI(viewHolder.getAdapterPosition());
                }
            }
            return false;
        });
    }
    private void drawReplyButton(Canvas canvas) {
        if (currentItemViewHolder == null) {
            return;
        }
        float translationX = mView.getTranslationX();
        long newTime = System.currentTimeMillis();
        long dt = Math.min(17, newTime - lastReplyButtonAnimationTime)/2;
        lastReplyButtonAnimationTime = newTime;
        boolean showing = translationX >= convertToDp(30);
        boolean showing1 = translationX <=-convertToDp(30);
        if (showing|showing1) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f;
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f;
                } else {
                    mView.invalidate();
                }
            }
        } else if (translationX == 0.0f) {
            replyButtonProgress = 0f;
            startTracking = false;
            isVibrate = false;
        }else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 180.0f;
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f;
                } else {
                    mView.invalidate();
                }
            }
        }
        int alpha;
        float scale;
        if (showing||showing1) {
            scale = this.replyButtonProgress <= 0.8F ? 1.2F * (this.replyButtonProgress / 0.8F) : 1.2F - 0.2F * ((this.replyButtonProgress - 0.8F) / 0.2F);
            alpha = (int) Math.min(255.0F, (float) 255 * (this.replyButtonProgress / 0.8F));
        } else {
            scale = this.replyButtonProgress;
            alpha = (int) Math.min(255.0F, (float) 255 * this.replyButtonProgress);
        }
        shareRound.setAlpha(alpha);
        imageDrawable.setAlpha(alpha);
        if (startTracking) {
            if (!isVibrate && (mView.getTranslationX() >= convertToDp(50)||mView.getTranslationX() <= -convertToDp(50))) {
                mView.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                );
                isVibrate = true;
            }
        }

        int x;
        float y;
        y = (float) ((mView.getTop() + mView.getMeasuredHeight() / 2));
        if(mView.getTranslationX()>0){
            if (mView.getTranslationX() > (float) this.convertToDp(130)) {
                x = this.convertToDp(130) / 2;
            }else {
                x = (int) (mView.getTranslationX() / (float) 2);
            }
            shareRound.setBounds((int) ((float) x - (float) this.convertToDp(16) * scale), (int) (y - (float) this.convertToDp(16) * scale), (int) ((float) x + (float) this.convertToDp(16) * scale), (int) (y + (float) this.convertToDp(16) * scale));
            shareRound.draw(canvas);
            imageDrawable.setBounds((int) ((float) x - (float) this.convertToDp(10) * scale), (int) (y - (float) this.convertToDp(10) * scale), (int) ((float) x + (float) this.convertToDp(10) * scale), (int) (y + (float) this.convertToDp(8) * scale));
            imageDrawable.draw(canvas);
            shareRound.setAlpha(255);
            imageDrawable.setAlpha(255);
        } else if(0 > mView.getTranslationX()){
            x = mView.getRight() + (int) (mView.getTranslationX() / 2);
            shareRound.setBounds((int) ((float) x - (float) this.convertToDp(16) * scale), (int) (y - (float) this.convertToDp(16) * scale), (int) ((float) x + (float) this.convertToDp(16) * scale), (int) (y + (float) this.convertToDp(16) * scale));
            shareRound.draw(canvas);
            imageDrawable.setBounds((int) ((float) x - (float) this.convertToDp(10) * scale), (int) (y - (float) this.convertToDp(10) * scale), (int) ((float) x + (float) this.convertToDp(10) * scale), (int) (y + (float) this.convertToDp(8) * scale));
            imageDrawable.draw(canvas);
            shareRound.setAlpha(255);
            imageDrawable.setAlpha(255);
        }
    }


    private int convertToDp(int pixel) {
        return this.dp((float) pixel, this.context);
    }


    public int dp(Float value, Context context) {
        if (this.density == 1.0F) this.checkDisplaySize(context);
        return value == 0.0F ? 0 : (int) Math.ceil(this.density * value);
    }

    private void checkDisplaySize(Context context) {
        try {
            this.density = context.getResources().getDisplayMetrics().density;
        } catch (Exception ignored) {}
    }

    public interface SwipeControllerActions {
        void showReplyUI(int position);
    }
}