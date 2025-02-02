package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public class SlideIntChooseView extends FrameLayout {
    private final AnimatedTextView maxText;
    private float maxTextEmojiSaturation;
    private ValueAnimator maxTextEmojiSaturationAnimator;
    private final AnimatedTextView minText;
    private int minValueAllowed;
    private Options options;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SeekBarView seekBarView;
    private int stepsCount;
    private float toMaxTextEmojiSaturation;
    private int value;
    private final AnimatedTextView valueText;
    private Utilities.Callback whenChanged;

    public static class Options {
        public int max;
        public int min;
        public int style;
        public Utilities.Callback2Return toString;

        public static String lambda$make$0(Utilities.CallbackReturn callbackReturn, Integer num, Integer num2) {
            return (String) callbackReturn.run(num2);
        }

        public static String lambda$make$1(String str, Integer num, Integer num2) {
            if (num.intValue() == 0) {
                return LocaleController.formatPluralString(str, num2.intValue(), new Object[0]);
            }
            return "" + num2;
        }

        public static Options make(int i, int i2, int i3, final Utilities.CallbackReturn callbackReturn) {
            Options options = new Options();
            options.style = i;
            options.min = i2;
            options.max = i3;
            options.toString = new Utilities.Callback2Return() {
                @Override
                public final Object run(Object obj, Object obj2) {
                    String lambda$make$0;
                    lambda$make$0 = SlideIntChooseView.Options.lambda$make$0(Utilities.CallbackReturn.this, (Integer) obj, (Integer) obj2);
                    return lambda$make$0;
                }
            };
            return options;
        }

        public static Options make(int i, final String str, int i2, int i3) {
            Options options = new Options();
            options.style = i;
            options.min = i2;
            options.max = i3;
            options.toString = new Utilities.Callback2Return() {
                @Override
                public final Object run(Object obj, Object obj2) {
                    String lambda$make$1;
                    lambda$make$1 = SlideIntChooseView.Options.lambda$make$1(str, (Integer) obj, (Integer) obj2);
                    return lambda$make$1;
                }
            };
            return options;
        }
    }

    public SlideIntChooseView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.minValueAllowed = Integer.MIN_VALUE;
        this.toMaxTextEmojiSaturation = -1.0f;
        this.resourcesProvider = resourcesProvider;
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.minText = animatedTextView;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        animatedTextView.setAnimationProperties(0.3f, 0L, 220L, cubicBezierInterpolator);
        animatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
        int i = Theme.key_windowBackgroundWhiteGrayText;
        animatedTextView.setTextColor(Theme.getColor(i, resourcesProvider));
        animatedTextView.setGravity(3);
        animatedTextView.setEmojiCacheType(19);
        animatedTextView.setEmojiColor(-1);
        addView(animatedTextView, LayoutHelper.createFrame(-1, 25.0f, 48, 22.0f, 13.0f, 22.0f, 0.0f));
        AnimatedTextView animatedTextView2 = new AnimatedTextView(context, false, true, true);
        this.valueText = animatedTextView2;
        animatedTextView2.setAnimationProperties(0.3f, 0L, 220L, cubicBezierInterpolator);
        animatedTextView2.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextView2.setGravity(17);
        animatedTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText, resourcesProvider));
        animatedTextView2.setEmojiColor(-1);
        animatedTextView2.setEmojiCacheType(19);
        addView(animatedTextView2, LayoutHelper.createFrame(-1, 25.0f, 48, 22.0f, 13.0f, 22.0f, 0.0f));
        AnimatedTextView animatedTextView3 = new AnimatedTextView(context, true, true, true);
        this.maxText = animatedTextView3;
        animatedTextView3.setAnimationProperties(0.3f, 0L, 220L, cubicBezierInterpolator);
        animatedTextView3.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextView3.setGravity(5);
        animatedTextView3.setTextColor(Theme.getColor(i, resourcesProvider));
        animatedTextView3.setEmojiColor(-1);
        animatedTextView3.setEmojiCacheType(19);
        addView(animatedTextView3, LayoutHelper.createFrame(-1, 25.0f, 48, 22.0f, 13.0f, 22.0f, 0.0f));
        SeekBarView seekBarView = new SeekBarView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.seekBarView = seekBarView;
        seekBarView.setReportChanges(true);
        seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            @Override
            public int getStepsCount() {
                return SlideIntChooseView.this.stepsCount;
            }

            @Override
            public void onSeekBarDrag(boolean z, float f) {
                if (SlideIntChooseView.this.options == null || SlideIntChooseView.this.whenChanged == null) {
                    return;
                }
                int round = Math.round(SlideIntChooseView.this.options.min + (SlideIntChooseView.this.stepsCount * f));
                if (SlideIntChooseView.this.minValueAllowed != Integer.MIN_VALUE) {
                    round = Math.max(round, SlideIntChooseView.this.minValueAllowed);
                }
                if (SlideIntChooseView.this.value != round) {
                    SlideIntChooseView.this.value = round;
                    AndroidUtilities.vibrateCursor(SlideIntChooseView.this.seekBarView);
                    SlideIntChooseView slideIntChooseView = SlideIntChooseView.this;
                    slideIntChooseView.updateTexts(slideIntChooseView.value, true);
                    if (SlideIntChooseView.this.whenChanged != null) {
                        SlideIntChooseView.this.whenChanged.run(Integer.valueOf(SlideIntChooseView.this.value));
                    }
                }
            }

            @Override
            public void onSeekBarPressed(boolean z) {
                SeekBarView.SeekBarViewDelegate.CC.$default$onSeekBarPressed(this, z);
            }
        });
        addView(seekBarView, LayoutHelper.createFrame(-1, 38.0f, 55, 6.0f, 30.0f, 6.0f, 0.0f));
    }

    public void lambda$setMaxTextEmojiSaturation$0(ValueAnimator valueAnimator) {
        ColorMatrix colorMatrix = new ColorMatrix();
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.maxTextEmojiSaturation = floatValue;
        colorMatrix.setSaturation(floatValue);
        if (Theme.isCurrentThemeDark()) {
            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, (1.0f - this.maxTextEmojiSaturation) * (-0.3f));
        }
        this.maxText.setEmojiColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    private void setMaxTextEmojiSaturation(final float f, boolean z) {
        if (Math.abs(this.toMaxTextEmojiSaturation - f) < 0.01f) {
            return;
        }
        ValueAnimator valueAnimator = this.maxTextEmojiSaturationAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.maxTextEmojiSaturationAnimator = null;
        }
        this.toMaxTextEmojiSaturation = f;
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.maxTextEmojiSaturation, f);
            this.maxTextEmojiSaturationAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SlideIntChooseView.this.lambda$setMaxTextEmojiSaturation$0(valueAnimator2);
                }
            });
            this.maxTextEmojiSaturationAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(SlideIntChooseView.this.maxTextEmojiSaturation = f);
                    if (Theme.isCurrentThemeDark()) {
                        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, (1.0f - SlideIntChooseView.this.maxTextEmojiSaturation) * (-0.3f));
                    }
                    SlideIntChooseView.this.maxText.setEmojiColorFilter(new ColorMatrixColorFilter(colorMatrix));
                }
            });
            this.maxTextEmojiSaturationAnimator.setDuration(240L);
            this.maxTextEmojiSaturationAnimator.start();
            return;
        }
        ColorMatrix colorMatrix = new ColorMatrix();
        this.maxTextEmojiSaturation = f;
        colorMatrix.setSaturation(f);
        if (Theme.isCurrentThemeDark()) {
            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, (1.0f - this.maxTextEmojiSaturation) * (-0.3f));
        }
        this.maxText.setEmojiColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(75.0f), 1073741824));
        if (Build.VERSION.SDK_INT >= 29) {
            setSystemGestureExclusionRects(Arrays.asList(new Rect(0, 0, AndroidUtilities.dp(80.0f), getMeasuredHeight()), new Rect(getMeasuredWidth() - AndroidUtilities.dp(80.0f), 0, getMeasuredWidth(), getMeasuredHeight())));
        }
    }

    public void set(int i, Options options, Utilities.Callback callback) {
        this.value = i;
        this.options = options;
        this.whenChanged = callback;
        int i2 = options.max - options.min;
        this.stepsCount = i2;
        this.seekBarView.setProgress((i - r3) / i2, false);
        updateTexts(i, false);
    }

    public void setMinValueAllowed(int i) {
        this.minValueAllowed = i;
        if (this.value < i) {
            this.value = i;
        }
        this.seekBarView.setMinProgress(Utilities.clamp01((i - this.options.min) / this.stepsCount));
        updateTexts(this.value, false);
        invalidate();
    }

    public void updateTexts(int i, boolean z) {
        this.minText.cancelAnimation();
        this.maxText.cancelAnimation();
        this.valueText.cancelAnimation();
        this.valueText.setText((CharSequence) this.options.toString.run(0, Integer.valueOf(i)), z);
        this.minText.setText((CharSequence) this.options.toString.run(-1, Integer.valueOf(this.options.min)), z);
        this.maxText.setText((CharSequence) this.options.toString.run(1, Integer.valueOf(this.options.max)), z);
        this.maxText.setTextColor(Theme.getColor(i >= this.options.max ? Theme.key_windowBackgroundWhiteValueText : Theme.key_windowBackgroundWhiteGrayText, this.resourcesProvider), z);
        setMaxTextEmojiSaturation(i >= this.options.max ? 1.0f : 0.0f, z);
    }
}
