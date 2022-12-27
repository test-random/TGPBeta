package org.telegram.p009ui.Cells;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C1072R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Components.AnimatedFloat;
import org.telegram.p009ui.Components.AvatarDrawable;
import org.telegram.p009ui.Components.BackupImageView;
import org.telegram.p009ui.Components.CombinedDrawable;
import org.telegram.p009ui.Components.DotDividerSpan;
import org.telegram.p009ui.Components.FlickerLoadingView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_webAuthorization;
import org.telegram.tgnet.TLRPC$User;

public class SessionCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private int currentAccount;
    private int currentType;
    private TextView detailExTextView;
    private TextView detailTextView;
    FlickerLoadingView globalGradient;
    private BackupImageView imageView;
    LinearLayout linearLayout;
    private TextView nameTextView;
    private boolean needDivider;
    private TextView onlineTextView;
    private BackupImageView placeholderImageView;
    private boolean showStub;
    private AnimatedFloat showStubValue;

    public SessionCell(android.content.Context r22, int r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Cells.SessionCell.<init>(android.content.Context, int):void");
    }

    private void setContentAlpha(float f) {
        TextView textView = this.detailExTextView;
        if (textView != null) {
            textView.setAlpha(f);
        }
        TextView textView2 = this.detailTextView;
        if (textView2 != null) {
            textView2.setAlpha(f);
        }
        TextView textView3 = this.nameTextView;
        if (textView3 != null) {
            textView3.setAlpha(f);
        }
        TextView textView4 = this.onlineTextView;
        if (textView4 != null) {
            textView4.setAlpha(f);
        }
        BackupImageView backupImageView = this.imageView;
        if (backupImageView != null) {
            backupImageView.setAlpha(f);
        }
        BackupImageView backupImageView2 = this.placeholderImageView;
        if (backupImageView2 != null) {
            backupImageView2.setAlpha(1.0f - f);
        }
        LinearLayout linearLayout = this.linearLayout;
        if (linearLayout != null) {
            linearLayout.setAlpha(f);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m36dp(this.currentType == 0 ? 70.0f : 90.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setSession(TLObject tLObject, boolean z) {
        String str;
        String stringForMessageListDate;
        this.needDivider = z;
        if (tLObject instanceof TLRPC$TL_authorization) {
            TLRPC$TL_authorization tLRPC$TL_authorization = (TLRPC$TL_authorization) tLObject;
            this.imageView.setImageDrawable(createDrawable(tLRPC$TL_authorization));
            StringBuilder sb = new StringBuilder();
            if (tLRPC$TL_authorization.device_model.length() != 0) {
                sb.append(tLRPC$TL_authorization.device_model);
            }
            if (sb.length() == 0) {
                if (tLRPC$TL_authorization.platform.length() != 0) {
                    sb.append(tLRPC$TL_authorization.platform);
                }
                if (tLRPC$TL_authorization.system_version.length() != 0) {
                    if (tLRPC$TL_authorization.platform.length() != 0) {
                        sb.append(" ");
                    }
                    sb.append(tLRPC$TL_authorization.system_version);
                }
            }
            this.nameTextView.setText(sb);
            if ((tLRPC$TL_authorization.flags & 1) != 0) {
                setTag("windowBackgroundWhiteValueText");
                stringForMessageListDate = LocaleController.getString("Online", C1072R.string.Online);
            } else {
                setTag("windowBackgroundWhiteGrayText3");
                stringForMessageListDate = LocaleController.stringForMessageListDate(tLRPC$TL_authorization.date_active);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (tLRPC$TL_authorization.country.length() != 0) {
                spannableStringBuilder.append((CharSequence) tLRPC$TL_authorization.country);
            }
            if (spannableStringBuilder.length() != 0) {
                DotDividerSpan dotDividerSpan = new DotDividerSpan();
                dotDividerSpan.setTopPadding(AndroidUtilities.m36dp(1.5f));
                spannableStringBuilder.append((CharSequence) " . ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 0);
            }
            spannableStringBuilder.append((CharSequence) stringForMessageListDate);
            this.detailExTextView.setText(spannableStringBuilder);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tLRPC$TL_authorization.app_name);
            sb2.append(" ");
            sb2.append(tLRPC$TL_authorization.app_version);
            this.detailTextView.setText(sb2);
        } else if (tLObject instanceof TLRPC$TL_webAuthorization) {
            TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) tLObject;
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$TL_webAuthorization.bot_id));
            this.nameTextView.setText(tLRPC$TL_webAuthorization.domain);
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                str = UserObject.getFirstName(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
            } else {
                str = "";
            }
            setTag("windowBackgroundWhiteGrayText3");
            this.onlineTextView.setText(LocaleController.stringForMessageListDate(tLRPC$TL_webAuthorization.date_active));
            this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            StringBuilder sb3 = new StringBuilder();
            if (tLRPC$TL_webAuthorization.f993ip.length() != 0) {
                sb3.append(tLRPC$TL_webAuthorization.f993ip);
            }
            if (tLRPC$TL_webAuthorization.region.length() != 0) {
                if (sb3.length() != 0) {
                    sb3.append(" ");
                }
                sb3.append("— ");
                sb3.append(tLRPC$TL_webAuthorization.region);
            }
            this.detailExTextView.setText(sb3);
            StringBuilder sb4 = new StringBuilder();
            if (!TextUtils.isEmpty(str)) {
                sb4.append(str);
            }
            if (tLRPC$TL_webAuthorization.browser.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tLRPC$TL_webAuthorization.browser);
            }
            if (tLRPC$TL_webAuthorization.platform.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tLRPC$TL_webAuthorization.platform);
            }
            this.detailTextView.setText(sb4);
        }
        if (this.showStub) {
            this.showStub = false;
            invalidate();
        }
    }

    public static Drawable createDrawable(TLRPC$TL_authorization tLRPC$TL_authorization) {
        int i;
        String lowerCase = tLRPC$TL_authorization.platform.toLowerCase();
        if (lowerCase.isEmpty()) {
            lowerCase = tLRPC$TL_authorization.system_version.toLowerCase();
        }
        String lowerCase2 = tLRPC$TL_authorization.device_model.toLowerCase();
        String str = "avatar_background2Cyan";
        String str2 = "avatar_backgroundCyan";
        if (lowerCase2.contains("safari")) {
            i = C1072R.C1073drawable.device_web_safari;
        } else if (lowerCase2.contains("edge")) {
            i = C1072R.C1073drawable.device_web_edge;
        } else if (lowerCase2.contains("chrome")) {
            i = C1072R.C1073drawable.device_web_chrome;
        } else if (lowerCase2.contains("opera")) {
            i = C1072R.C1073drawable.device_web_opera;
        } else if (lowerCase2.contains("firefox")) {
            i = C1072R.C1073drawable.device_web_firefox;
        } else if (lowerCase2.contains("vivaldi")) {
            i = C1072R.C1073drawable.device_web_other;
        } else {
            if (lowerCase.contains("ios")) {
                i = lowerCase2.contains("ipad") ? C1072R.C1073drawable.device_tablet_ios : C1072R.C1073drawable.device_phone_ios;
                str2 = "avatar_backgroundBlue";
                str = "avatar_background2Blue";
            } else if (lowerCase.contains("windows")) {
                i = C1072R.C1073drawable.device_desktop_win;
            } else if (lowerCase.contains("macos")) {
                i = C1072R.C1073drawable.device_desktop_osx;
            } else if (lowerCase.contains("android")) {
                i = lowerCase2.contains("tab") ? C1072R.C1073drawable.device_tablet_android : C1072R.C1073drawable.device_phone_android;
                str2 = "avatar_backgroundGreen";
                str = "avatar_background2Green";
            } else if (tLRPC$TL_authorization.app_name.toLowerCase().contains("desktop")) {
                i = C1072R.C1073drawable.device_desktop_other;
            } else {
                i = C1072R.C1073drawable.device_web_other;
            }
            Drawable mutate = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
            return new CombinedDrawable(new CircleGradientDrawable(AndroidUtilities.m36dp(42.0f), Theme.getColor(str2), Theme.getColor(str)), mutate);
        }
        str = "avatar_background2Pink";
        str2 = "avatar_backgroundPink";
        Drawable mutate2 = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        return new CombinedDrawable(new CircleGradientDrawable(AndroidUtilities.m36dp(42.0f), Theme.getColor(str2), Theme.getColor(str)), mutate2);
    }

    public static class CircleGradientDrawable extends Drawable {
        private Paint paint;
        private int size;

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public CircleGradientDrawable(int i, int i2, int i3) {
            this.size = i;
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, i, new int[]{i2, i3}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), Math.min(getBounds().width(), getBounds().height()) / 2.0f, this.paint);
        }

        @Override
        public void setAlpha(int i) {
            this.paint.setAlpha(i);
        }

        @Override
        public int getIntrinsicHeight() {
            return this.size;
        }

        @Override
        public int getIntrinsicWidth() {
            return this.size;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float f = this.showStubValue.set(this.showStub ? 1.0f : 0.0f);
        setContentAlpha(1.0f - f);
        if (f > 0.0f && this.globalGradient != null) {
            if (f < 1.0f) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                canvas.saveLayerAlpha(rectF, (int) (255.0f * f), 31);
            }
            this.globalGradient.updateColors();
            this.globalGradient.updateGradient();
            if (getParent() != null) {
                View view = (View) getParent();
                this.globalGradient.setParentSize(view.getMeasuredWidth(), view.getMeasuredHeight(), -getX());
            }
            float top = this.linearLayout.getTop() + this.nameTextView.getTop() + AndroidUtilities.m36dp(12.0f);
            float x = this.linearLayout.getX();
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(x, top - AndroidUtilities.m36dp(4.0f), (getMeasuredWidth() * 0.2f) + x, top + AndroidUtilities.m36dp(4.0f));
            canvas.drawRoundRect(rectF2, AndroidUtilities.m36dp(4.0f), AndroidUtilities.m36dp(4.0f), this.globalGradient.getPaint());
            float top2 = (this.linearLayout.getTop() + this.detailTextView.getTop()) - AndroidUtilities.m36dp(1.0f);
            float x2 = this.linearLayout.getX();
            rectF2.set(x2, top2 - AndroidUtilities.m36dp(4.0f), (getMeasuredWidth() * 0.4f) + x2, top2 + AndroidUtilities.m36dp(4.0f));
            canvas.drawRoundRect(rectF2, AndroidUtilities.m36dp(4.0f), AndroidUtilities.m36dp(4.0f), this.globalGradient.getPaint());
            float top3 = (this.linearLayout.getTop() + this.detailExTextView.getTop()) - AndroidUtilities.m36dp(1.0f);
            float x3 = this.linearLayout.getX();
            rectF2.set(x3, top3 - AndroidUtilities.m36dp(4.0f), (getMeasuredWidth() * 0.3f) + x3, top3 + AndroidUtilities.m36dp(4.0f));
            canvas.drawRoundRect(rectF2, AndroidUtilities.m36dp(4.0f), AndroidUtilities.m36dp(4.0f), this.globalGradient.getPaint());
            invalidate();
            if (f < 1.0f) {
                canvas.restore();
            }
        }
        if (this.needDivider) {
            int i = this.currentType == 1 ? 49 : 72;
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.m36dp(i), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.m36dp(i) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void showStub(FlickerLoadingView flickerLoadingView) {
        this.globalGradient = flickerLoadingView;
        this.showStub = true;
        Drawable mutate = ContextCompat.getDrawable(ApplicationLoader.applicationContext, AndroidUtilities.isTablet() ? C1072R.C1073drawable.device_tablet_android : C1072R.C1073drawable.device_phone_android).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.m36dp(42.0f), Theme.getColor("avatar_backgroundGreen")), mutate);
        BackupImageView backupImageView = this.placeholderImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(combinedDrawable);
        } else {
            this.imageView.setImageDrawable(combinedDrawable);
        }
        invalidate();
    }
}
