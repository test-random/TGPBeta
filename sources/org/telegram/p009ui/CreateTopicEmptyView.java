package org.telegram.p009ui;

import android.content.Context;
import android.graphics.Paint;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C1010R;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Components.BackupImageView;
import org.telegram.p009ui.Components.LayoutHelper;
import org.telegram.tgnet.TLRPC$Document;

public class CreateTopicEmptyView extends LinearLayout {
    BackupImageView backupImageView;
    private final Theme.ResourcesProvider resourcesProvider;

    public CreateTopicEmptyView(Context context, FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setBackground(Theme.createServiceDrawable(AndroidUtilities.m35dp(18.0f), this, frameLayout, getThemedPaint("paintChatActionBackground")));
        setPadding(AndroidUtilities.m35dp(16.0f), AndroidUtilities.m35dp(12.0f), AndroidUtilities.m35dp(16.0f), AndroidUtilities.m35dp(12.0f));
        setOrientation(1);
        this.backupImageView = new BackupImageView(context);
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextSize(1, 15.0f);
        textView.setTextColor(getThemedColor("chat_serviceText"));
        textView.setGravity(1);
        textView.setMaxWidth(AndroidUtilities.m35dp(210.0f));
        textView.setText(LocaleController.getString(C1010R.string.AlmostDone));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 13.0f);
        textView2.setTextColor(getThemedColor("chat_serviceText"));
        textView2.setGravity(1);
        textView2.setMaxWidth(AndroidUtilities.m35dp(160.0f));
        textView2.setText(LocaleController.getString(C1010R.string.TopicEmptyViewDescription));
        addView(this.backupImageView, LayoutHelper.createLinear(58, 58, 1, 0, 8, 0, 8));
        addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 2, 0));
        addView(textView2, LayoutHelper.createLinear(-2, -2, 1));
        setSticker();
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private void setSticker() {
        TLRPC$Document emojiAnimatedSticker = MediaDataController.getInstance(UserConfig.selectedAccount).getEmojiAnimatedSticker("\u1f973");
        if (emojiAnimatedSticker != null) {
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(emojiAnimatedSticker.thumbs, "emptyListPlaceholder", 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            this.backupImageView.setImage(ImageLocation.getForDocument(emojiAnimatedSticker), (String) null, "tgs", svgThumb, (Object) null);
        }
    }
}
