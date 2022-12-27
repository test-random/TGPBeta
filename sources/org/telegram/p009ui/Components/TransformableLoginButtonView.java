package org.telegram.p009ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p009ui.ActionBar.Theme;

public class TransformableLoginButtonView extends View {
    private Paint backgroundPaint;
    private String buttonText;
    private float buttonWidth;
    private boolean drawBackground;
    private Paint outlinePaint;
    private float progress;
    private RectF rect;
    private Drawable rippleDrawable;
    private TextPaint textPaint;
    private int transformType;

    public TransformableLoginButtonView(Context context) {
        super(context);
        this.backgroundPaint = new Paint(1);
        this.outlinePaint = new Paint(1);
        this.drawBackground = true;
        this.transformType = 0;
        this.rect = new RectF();
        this.backgroundPaint.setColor(Theme.getColor("chats_actionBackground"));
        this.outlinePaint.setStrokeWidth(AndroidUtilities.m36dp(2.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setRippleDrawable(Drawable drawable) {
        this.rippleDrawable = drawable;
        invalidate();
    }

    public void setTransformType(int i) {
        this.transformType = i;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int i) {
        this.backgroundPaint.setColor(i);
        invalidate();
    }

    public void setColor(int i) {
        this.outlinePaint.setColor(i);
        invalidate();
    }

    public void setButtonText(TextPaint textPaint, String str) {
        this.textPaint = textPaint;
        this.buttonText = str;
        this.outlinePaint.setColor(textPaint.getColor());
        this.buttonWidth = textPaint.measureText(str);
    }

    public void setProgress(float f) {
        this.progress = f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.drawBackground) {
            float m36dp = AndroidUtilities.m36dp(((this.transformType == 0 ? this.progress : 1.0f) * 26.0f) + 6.0f);
            this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRoundRect(this.rect, m36dp, m36dp, this.backgroundPaint);
        }
        int i = this.transformType;
        if (i == 0) {
            TextPaint textPaint = this.textPaint;
            if (textPaint != null && this.buttonText != null) {
                int alpha = textPaint.getAlpha();
                this.textPaint.setAlpha((int) (alpha * (1.0f - (Math.min(0.6f, this.progress) / 0.6f))));
                canvas.drawText(this.buttonText, (getWidth() - this.buttonWidth) / 2.0f, ((getHeight() / 2.0f) + (this.textPaint.getTextSize() / 2.0f)) - AndroidUtilities.m36dp(1.75f), this.textPaint);
                this.textPaint.setAlpha(alpha);
            }
            float max = (Math.max(0.4f, this.progress) - 0.4f) / 0.6f;
            if (max != 0.0f) {
                float m36dp2 = AndroidUtilities.m36dp(21.0f) + ((getWidth() - (AndroidUtilities.m36dp(21.0f) * 2)) * max);
                float height = getHeight() / 2.0f;
                canvas.drawLine(AndroidUtilities.m36dp(21.0f), height, m36dp2, height, this.outlinePaint);
                double d = m36dp2;
                double cos = Math.cos(0.7853981633974483d);
                double m36dp3 = AndroidUtilities.m36dp(9.0f) * max;
                Double.isNaN(m36dp3);
                Double.isNaN(d);
                float f = (float) (d - (cos * m36dp3));
                double sin = Math.sin(0.7853981633974483d);
                Double.isNaN(m36dp3);
                float f2 = (float) (sin * m36dp3);
                canvas.drawLine(m36dp2, height, f, height - f2, this.outlinePaint);
                canvas.drawLine(m36dp2, height, f, height + f2, this.outlinePaint);
            }
        } else if (i == 1) {
            float m36dp4 = AndroidUtilities.m36dp(21.0f);
            float width = getWidth() - AndroidUtilities.m36dp(21.0f);
            float height2 = getHeight() / 2.0f;
            canvas.save();
            canvas.translate((-AndroidUtilities.m36dp(2.0f)) * this.progress, 0.0f);
            canvas.rotate(this.progress * 90.0f, getWidth() / 2.0f, getHeight() / 2.0f);
            canvas.drawLine(((width - m36dp4) * this.progress) + m36dp4, height2, width, height2, this.outlinePaint);
            int m36dp5 = AndroidUtilities.m36dp((this.progress * (-1.0f)) + 9.0f);
            int m36dp6 = AndroidUtilities.m36dp((this.progress * 7.0f) + 9.0f);
            double d2 = width;
            double d3 = m36dp5;
            double cos2 = Math.cos(0.7853981633974483d);
            Double.isNaN(d3);
            Double.isNaN(d2);
            double d4 = height2;
            double sin2 = Math.sin(0.7853981633974483d);
            Double.isNaN(d3);
            Double.isNaN(d4);
            canvas.drawLine(width, height2, (float) (d2 - (cos2 * d3)), (float) ((d3 * sin2) + d4), this.outlinePaint);
            double d5 = m36dp6;
            double cos3 = Math.cos(0.7853981633974483d);
            Double.isNaN(d5);
            Double.isNaN(d2);
            double sin3 = Math.sin(0.7853981633974483d);
            Double.isNaN(d5);
            Double.isNaN(d4);
            canvas.drawLine(width, height2, (float) (d2 - (cos3 * d5)), (float) (d4 - (d5 * sin3)), this.outlinePaint);
            canvas.restore();
        }
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
            if (Build.VERSION.SDK_INT >= 21) {
                this.rippleDrawable.setHotspotBounds(0, 0, getWidth(), getHeight());
            }
            this.rippleDrawable.draw(canvas);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        Drawable drawable = this.rippleDrawable;
        if (drawable == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        drawable.setHotspot(f, f2);
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        Drawable drawable2;
        return super.verifyDrawable(drawable) || ((drawable2 = this.rippleDrawable) != null && drawable == drawable2);
    }
}
