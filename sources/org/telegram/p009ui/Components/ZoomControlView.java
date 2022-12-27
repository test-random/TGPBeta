package org.telegram.p009ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C1072R;
import org.telegram.p009ui.Components.AnimationProperties;

public class ZoomControlView extends View {
    public final Property<ZoomControlView, Float> ZOOM_PROPERTY;
    private float animatingToZoom;
    private AnimatorSet animatorSet;
    private ZoomControlViewDelegate delegate;
    private Drawable filledProgressDrawable;
    private Drawable knobDrawable;
    private boolean knobPressed;
    private float knobStartX;
    private float knobStartY;
    private int minusCx;
    private int minusCy;
    private Drawable minusDrawable;
    private int plusCx;
    private int plusCy;
    private Drawable plusDrawable;
    private boolean pressed;
    private Drawable pressedKnobDrawable;
    private Drawable progressDrawable;
    private int progressEndX;
    private int progressEndY;
    private int progressStartX;
    private int progressStartY;
    private float zoom;

    public interface ZoomControlViewDelegate {
        void didSetZoom(float f);
    }

    public ZoomControlView(Context context) {
        super(context);
        this.ZOOM_PROPERTY = new AnimationProperties.FloatProperty<ZoomControlView>("clipProgress") {
            @Override
            public void setValue(ZoomControlView zoomControlView, float f) {
                ZoomControlView.this.zoom = f;
                if (ZoomControlView.this.delegate != null) {
                    ZoomControlView.this.delegate.didSetZoom(ZoomControlView.this.zoom);
                }
                ZoomControlView.this.invalidate();
            }

            @Override
            public Float get(ZoomControlView zoomControlView) {
                return Float.valueOf(ZoomControlView.this.zoom);
            }
        };
        this.minusDrawable = context.getResources().getDrawable(C1072R.C1073drawable.zoom_minus);
        this.plusDrawable = context.getResources().getDrawable(C1072R.C1073drawable.zoom_plus);
        this.progressDrawable = context.getResources().getDrawable(C1072R.C1073drawable.zoom_slide);
        this.filledProgressDrawable = context.getResources().getDrawable(C1072R.C1073drawable.zoom_slide_a);
        this.knobDrawable = context.getResources().getDrawable(C1072R.C1073drawable.zoom_round);
        this.pressedKnobDrawable = context.getResources().getDrawable(C1072R.C1073drawable.zoom_round_b);
    }

    public float getZoom() {
        if (this.animatorSet != null) {
            return this.animatingToZoom;
        }
        return this.zoom;
    }

    public void setZoom(float f, boolean z) {
        ZoomControlViewDelegate zoomControlViewDelegate;
        if (f == this.zoom) {
            return;
        }
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        this.zoom = f;
        if (z && (zoomControlViewDelegate = this.delegate) != null) {
            zoomControlViewDelegate.didSetZoom(f);
        }
        invalidate();
    }

    public void setDelegate(ZoomControlViewDelegate zoomControlViewDelegate) {
        this.delegate = zoomControlViewDelegate;
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.ZoomControlView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean animateToZoom(float f) {
        if (f < 0.0f || f > 1.0f) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.animatingToZoom = f;
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, this.ZOOM_PROPERTY, f));
        this.animatorSet.setDuration(180L);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ZoomControlView.this.animatorSet = null;
            }
        });
        this.animatorSet.start();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        boolean z = getMeasuredWidth() > getMeasuredHeight();
        if (z) {
            this.minusCx = AndroidUtilities.m36dp(41.0f);
            this.minusCy = measuredHeight;
            this.plusCx = getMeasuredWidth() - AndroidUtilities.m36dp(41.0f);
            this.plusCy = measuredHeight;
            this.progressStartX = this.minusCx + AndroidUtilities.m36dp(18.0f);
            this.progressStartY = measuredHeight;
            this.progressEndX = this.plusCx - AndroidUtilities.m36dp(18.0f);
            this.progressEndY = measuredHeight;
        } else {
            this.minusCx = measuredWidth;
            this.minusCy = AndroidUtilities.m36dp(41.0f);
            this.plusCx = measuredWidth;
            this.plusCy = getMeasuredHeight() - AndroidUtilities.m36dp(41.0f);
            this.progressStartX = measuredWidth;
            this.progressStartY = this.minusCy + AndroidUtilities.m36dp(18.0f);
            this.progressEndX = measuredWidth;
            this.progressEndY = this.plusCy - AndroidUtilities.m36dp(18.0f);
        }
        this.minusDrawable.setBounds(this.minusCx - AndroidUtilities.m36dp(7.0f), this.minusCy - AndroidUtilities.m36dp(7.0f), this.minusCx + AndroidUtilities.m36dp(7.0f), this.minusCy + AndroidUtilities.m36dp(7.0f));
        this.minusDrawable.draw(canvas);
        this.plusDrawable.setBounds(this.plusCx - AndroidUtilities.m36dp(7.0f), this.plusCy - AndroidUtilities.m36dp(7.0f), this.plusCx + AndroidUtilities.m36dp(7.0f), this.plusCy + AndroidUtilities.m36dp(7.0f));
        this.plusDrawable.draw(canvas);
        int i = this.progressEndX;
        int i2 = this.progressStartX;
        int i3 = this.progressEndY;
        int i4 = this.progressStartY;
        float f = this.zoom;
        int i5 = (int) (i2 + ((i - i2) * f));
        int i6 = (int) (i4 + ((i3 - i4) * f));
        if (z) {
            this.progressDrawable.setBounds(i2, i4 - AndroidUtilities.m36dp(3.0f), this.progressEndX, this.progressStartY + AndroidUtilities.m36dp(3.0f));
            this.filledProgressDrawable.setBounds(this.progressStartX, this.progressStartY - AndroidUtilities.m36dp(3.0f), i5, this.progressStartY + AndroidUtilities.m36dp(3.0f));
        } else {
            this.progressDrawable.setBounds(i4, 0, i3, AndroidUtilities.m36dp(6.0f));
            this.filledProgressDrawable.setBounds(this.progressStartY, 0, i6, AndroidUtilities.m36dp(6.0f));
            canvas.save();
            canvas.rotate(90.0f);
            canvas.translate(0.0f, (-this.progressStartX) - AndroidUtilities.m36dp(3.0f));
        }
        this.progressDrawable.draw(canvas);
        this.filledProgressDrawable.draw(canvas);
        if (!z) {
            canvas.restore();
        }
        Drawable drawable = this.knobPressed ? this.pressedKnobDrawable : this.knobDrawable;
        int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
        drawable.setBounds(i5 - intrinsicWidth, i6 - intrinsicWidth, i5 + intrinsicWidth, i6 + intrinsicWidth);
        drawable.draw(canvas);
    }
}
