package org.telegram.p009ui.Cells;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Components.LayoutHelper;

public class LetterSectionCell extends FrameLayout {
    private TextView textView;

    public LetterSectionCell(Context context) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.m34dp(54.0f), AndroidUtilities.m34dp(64.0f)));
        TextView textView = new TextView(getContext());
        this.textView = textView;
        textView.setTextSize(1, 22.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void setLetter(String str) {
        this.textView.setText(str.toUpperCase());
    }

    public void setCellHeight(int i) {
        setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.m34dp(54.0f), i));
    }
}
