package org.telegram.p009ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class CombinedDrawable extends Drawable implements Drawable.Callback {
    private int backHeight;
    private int backWidth;
    private Drawable background;
    private boolean fullSize;
    private Drawable icon;
    private int iconHeight;
    private int iconWidth;
    private int left;
    private int offsetX;
    private int offsetY;
    private int top;

    @Override
    protected boolean onStateChange(int[] iArr) {
        return true;
    }

    public CombinedDrawable(Drawable drawable, Drawable drawable2, int i, int i2) {
        this.background = drawable;
        this.icon = drawable2;
        this.left = i;
        this.top = i2;
        if (drawable2 != null) {
            drawable2.setCallback(this);
        }
    }

    public void setIconSize(int i, int i2) {
        this.iconWidth = i;
        this.iconHeight = i2;
    }

    public CombinedDrawable(Drawable drawable, Drawable drawable2) {
        this.background = drawable;
        this.icon = drawable2;
        if (drawable2 != null) {
            drawable2.setCallback(this);
        }
    }

    public void setCustomSize(int i, int i2) {
        this.backWidth = i;
        this.backHeight = i2;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public void setFullsize(boolean z) {
        this.fullSize = z;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.icon.setColorFilter(colorFilter);
    }

    @Override
    public boolean isStateful() {
        return this.icon.isStateful();
    }

    @Override
    public boolean setState(int[] iArr) {
        this.icon.setState(iArr);
        return true;
    }

    @Override
    public int[] getState() {
        return this.icon.getState();
    }

    @Override
    public void jumpToCurrentState() {
        this.icon.jumpToCurrentState();
    }

    @Override
    public Drawable.ConstantState getConstantState() {
        return this.icon.getConstantState();
    }

    @Override
    public void draw(Canvas canvas) {
        this.background.setBounds(getBounds());
        this.background.draw(canvas);
        if (this.icon != null) {
            if (this.fullSize) {
                Rect bounds = getBounds();
                int i = this.left;
                if (i != 0) {
                    int i2 = bounds.top;
                    int i3 = this.top;
                    this.icon.setBounds(bounds.left + i, i2 + i3, bounds.right - i, bounds.bottom - i3);
                } else {
                    this.icon.setBounds(bounds);
                }
            } else if (this.iconWidth != 0) {
                int centerX = (getBounds().centerX() - (this.iconWidth / 2)) + this.left + this.offsetX;
                int centerY = getBounds().centerY();
                int i4 = this.iconHeight;
                int i5 = (centerY - (i4 / 2)) + this.top + this.offsetY;
                this.icon.setBounds(centerX, i5, this.iconWidth + centerX, i4 + i5);
            } else {
                int centerX2 = (getBounds().centerX() - (this.icon.getIntrinsicWidth() / 2)) + this.left;
                int centerY2 = (getBounds().centerY() - (this.icon.getIntrinsicHeight() / 2)) + this.top;
                Drawable drawable = this.icon;
                drawable.setBounds(centerX2, centerY2, drawable.getIntrinsicWidth() + centerX2, this.icon.getIntrinsicHeight() + centerY2);
            }
            this.icon.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int i) {
        this.icon.setAlpha(i);
        this.background.setAlpha(i);
    }

    @Override
    public int getIntrinsicWidth() {
        int i = this.backWidth;
        return i != 0 ? i : this.background.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        int i = this.backHeight;
        return i != 0 ? i : this.background.getIntrinsicHeight();
    }

    @Override
    public int getMinimumWidth() {
        int i = this.backWidth;
        return i != 0 ? i : this.background.getMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        int i = this.backHeight;
        return i != 0 ? i : this.background.getMinimumHeight();
    }

    @Override
    public int getOpacity() {
        return this.icon.getOpacity();
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    @Override
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    @Override
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }
}
