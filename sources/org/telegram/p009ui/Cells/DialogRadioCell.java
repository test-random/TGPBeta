package org.telegram.p009ui.Cells;

import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Components.RadioButton;

public class DialogRadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.m34dp(50.0f) + (this.needDivider ? 1 : 0));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.m34dp(34.0f);
        this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m34dp(22.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m34dp(22.0f), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            float f = 0.0f;
            float dp = AndroidUtilities.m34dp(LocaleController.isRTL ? 0.0f : 60.0f);
            float height = getHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = 60.0f;
            }
            canvas.drawLine(dp, height, measuredWidth - AndroidUtilities.m34dp(f), getHeight() - 1, Theme.dividerPaint);
        }
    }
}
