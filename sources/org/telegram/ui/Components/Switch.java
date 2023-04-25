package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.StateSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
public class Switch extends View {
    private boolean attachedToWindow;
    private boolean bitmapsCreated;
    private ObjectAnimator checkAnimator;
    private int colorSet;
    private int drawIconType;
    private boolean drawRipple;
    private ObjectAnimator iconAnimator;
    private Drawable iconDrawable;
    private float iconProgress;
    private boolean isChecked;
    private int lastIconColor;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Bitmap[] overlayBitmap;
    private Canvas[] overlayCanvas;
    private float overlayCx;
    private float overlayCy;
    private Paint overlayEraserPaint;
    private Bitmap overlayMaskBitmap;
    private Canvas overlayMaskCanvas;
    private Paint overlayMaskPaint;
    private float overlayRad;
    private int overrideColorProgress;
    private Paint paint;
    private Paint paint2;
    private int[] pressedState;
    private float progress;
    private RectF rectF;
    private Theme.ResourcesProvider resourcesProvider;
    private RippleDrawable rippleDrawable;
    private Paint ripplePaint;
    private boolean semHaptics;
    private int thumbCheckedColorKey;
    private int thumbColorKey;
    private int trackCheckedColorKey;
    private int trackColorKey;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch r1, boolean z);
    }

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.iconProgress = 1.0f;
        this.trackColorKey = Theme.key_fill_RedNormal;
        this.trackCheckedColorKey = Theme.key_switch2TrackChecked;
        int i = Theme.key_windowBackgroundWhite;
        this.thumbColorKey = i;
        this.thumbCheckedColorKey = i;
        this.pressedState = new int[]{16842910, 16842919};
        this.semHaptics = false;
        this.resourcesProvider = resourcesProvider;
        this.rectF = new RectF();
        this.paint = new Paint(1);
        Paint paint = new Paint(1);
        this.paint2 = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paint2.setStrokeCap(Paint.Cap.ROUND);
        this.paint2.setStrokeWidth(AndroidUtilities.dp(2.0f));
        setHapticFeedbackEnabled(true);
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress == f) {
            return;
        }
        this.progress = f;
        invalidate();
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    @Keep
    public void setIconProgress(float f) {
        if (this.iconProgress == f) {
            return;
        }
        this.iconProgress = f;
        invalidate();
    }

    @Keep
    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        ObjectAnimator objectAnimator = this.iconAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int i) {
        this.drawIconType = i;
    }

    public void setDrawRipple(boolean z) {
        int i = Build.VERSION.SDK_INT;
        if (i < 21 || z == this.drawRipple) {
            return;
        }
        this.drawRipple = z;
        if (this.rippleDrawable == null) {
            Paint paint = new Paint(1);
            this.ripplePaint = paint;
            paint.setColor(-1);
            RippleDrawable rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{0}), null, i >= 23 ? null : new Drawable() {
                @Override
                public int getOpacity() {
                    return 0;
                }

                @Override
                public void setAlpha(int i2) {
                }

                @Override
                public void setColorFilter(ColorFilter colorFilter) {
                }

                @Override
                public void draw(Canvas canvas) {
                    android.graphics.Rect bounds = getBounds();
                    canvas.drawCircle(bounds.centerX(), bounds.centerY(), AndroidUtilities.dp(18.0f), Switch.this.ripplePaint);
                }
            });
            this.rippleDrawable = rippleDrawable;
            if (i >= 23) {
                rippleDrawable.setRadius(AndroidUtilities.dp(18.0f));
            }
            this.rippleDrawable.setCallback(this);
        }
        boolean z2 = this.isChecked;
        if ((z2 && this.colorSet != 2) || (!z2 && this.colorSet != 1)) {
            this.rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor(z2 ? Theme.key_switchTrackBlueSelectorChecked : Theme.key_switchTrackBlueSelector, this.resourcesProvider)}));
            this.colorSet = this.isChecked ? 2 : 1;
        }
        if (i >= 28 && z) {
            this.rippleDrawable.setHotspot(this.isChecked ? 0.0f : AndroidUtilities.dp(100.0f), AndroidUtilities.dp(18.0f));
        }
        this.rippleDrawable.setState(z ? this.pressedState : StateSet.NOTHING);
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        RippleDrawable rippleDrawable;
        return super.verifyDrawable(drawable) || ((rippleDrawable = this.rippleDrawable) != null && drawable == rippleDrawable);
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.trackColorKey = i;
        this.trackCheckedColorKey = i2;
        this.thumbColorKey = i3;
        this.thumbCheckedColorKey = i4;
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(this.semHaptics ? 150L : 250L);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Switch.this.checkAnimator = null;
            }
        });
        this.checkAnimator.start();
    }

    private void animateIcon(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "iconProgress", fArr);
        this.iconAnimator = ofFloat;
        ofFloat.setDuration(this.semHaptics ? 150L : 250L);
        this.iconAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Switch.this.iconAnimator = null;
            }
        });
        this.iconAnimator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(z, this.drawIconType, z2);
    }

    public void setChecked(boolean z, int i, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                vibrateChecked(z);
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
            }
            OnCheckedChangeListener onCheckedChangeListener = this.onCheckedChangeListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, z);
            }
        }
        setDrawIconType(i, z2);
    }

    public void setIcon(int i) {
        if (i != 0) {
            Drawable mutate = getResources().getDrawable(i).mutate();
            this.iconDrawable = mutate;
            if (mutate != null) {
                int color = Theme.getColor(this.isChecked ? this.trackCheckedColorKey : this.trackColorKey, this.resourcesProvider);
                this.lastIconColor = color;
                mutate.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        } else {
            this.iconDrawable = null;
        }
        invalidate();
    }

    public void setDrawIconType(int i, boolean z) {
        if (this.drawIconType != i) {
            this.drawIconType = i;
            if (this.attachedToWindow && z) {
                animateIcon(i == 0);
                return;
            }
            cancelIconAnimator();
            setIconProgress(i == 0 ? 1.0f : 0.0f);
        }
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setOverrideColor(int i) {
        if (this.overrideColorProgress == i) {
            return;
        }
        if (this.overlayBitmap == null) {
            try {
                this.overlayBitmap = new Bitmap[2];
                this.overlayCanvas = new Canvas[2];
                for (int i2 = 0; i2 < 2; i2++) {
                    this.overlayBitmap[i2] = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    this.overlayCanvas[i2] = new Canvas(this.overlayBitmap[i2]);
                }
                this.overlayMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                this.overlayMaskCanvas = new Canvas(this.overlayMaskBitmap);
                Paint paint = new Paint(1);
                this.overlayEraserPaint = paint;
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                Paint paint2 = new Paint(1);
                this.overlayMaskPaint = paint2;
                paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                this.bitmapsCreated = true;
            } catch (Throwable unused) {
                return;
            }
        }
        if (this.bitmapsCreated) {
            this.overrideColorProgress = i;
            this.overlayCx = 0.0f;
            this.overlayCy = 0.0f;
            this.overlayRad = 0.0f;
            invalidate();
        }
    }

    public void setOverrideColorProgress(float f, float f2, float f3) {
        this.overlayCx = f;
        this.overlayCy = f2;
        this.overlayRad = f3;
        invalidate();
    }

    @Override
    protected void onDraw(android.graphics.Canvas r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Switch.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.isChecked);
    }

    private void vibrateChecked(boolean z) {
        try {
            if (!isHapticFeedbackEnabled() || Build.VERSION.SDK_INT < 28) {
                return;
            }
            Vibrator vibrator = AndroidUtilities.getVibrator();
            VibrationEffect createWaveform = VibrationEffect.createWaveform(new long[]{75, 10, 5, 10}, new int[]{5, 20, 110, 20}, -1);
            vibrator.cancel();
            vibrator.vibrate(createWaveform);
            this.semHaptics = true;
        } catch (Exception unused) {
        }
    }
}
