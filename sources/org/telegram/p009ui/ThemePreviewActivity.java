package org.telegram.p009ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0952R;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.p009ui.ActionBar.ActionBarMenuItem;
import org.telegram.p009ui.ActionBar.AlertDialog;
import org.telegram.p009ui.ActionBar.BaseFragment;
import org.telegram.p009ui.ActionBar.C1006ActionBar;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.ActionBar.ThemeDescription;
import org.telegram.p009ui.Cells.ChatActionCell;
import org.telegram.p009ui.Cells.ChatMessageCell;
import org.telegram.p009ui.Cells.DialogCell;
import org.telegram.p009ui.Cells.HeaderCell;
import org.telegram.p009ui.Cells.LoadingCell;
import org.telegram.p009ui.Cells.PatternCell;
import org.telegram.p009ui.Cells.TextSelectionHelper;
import org.telegram.p009ui.Components.AlertsCreator;
import org.telegram.p009ui.Components.BackgroundGradientDrawable;
import org.telegram.p009ui.Components.BackupImageView;
import org.telegram.p009ui.Components.ColorPicker;
import org.telegram.p009ui.Components.CubicBezierInterpolator;
import org.telegram.p009ui.Components.HintView;
import org.telegram.p009ui.Components.LayoutHelper;
import org.telegram.p009ui.Components.MotionBackgroundDrawable;
import org.telegram.p009ui.Components.RadialProgress2;
import org.telegram.p009ui.Components.RecyclerListView;
import org.telegram.p009ui.Components.SeekBarView;
import org.telegram.p009ui.Components.ShareAlert;
import org.telegram.p009ui.Components.UndoView;
import org.telegram.p009ui.Components.WallpaperCheckBoxView;
import org.telegram.p009ui.Components.WallpaperParallaxEffect;
import org.telegram.p009ui.ThemePreviewActivity;
import org.telegram.p009ui.WallpapersListActivity;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC$TL_account_wallPapers;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WallPaper;

public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    private Theme.ThemeAccent accent;
    private C1006ActionBar actionBar2;
    private HintView animationHint;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    private Theme.ThemeInfo applyingTheme;
    private FrameLayout backgroundButtonsContainer;
    private WallpaperCheckBoxView[] backgroundCheckBoxView;
    private int backgroundColor;
    private int backgroundGradientColor1;
    private int backgroundGradientColor2;
    private int backgroundGradientColor3;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private BackupImageView backgroundImage;
    private ImageView backgroundPlayAnimationImageView;
    private FrameLayout backgroundPlayAnimationView;
    private AnimatorSet backgroundPlayViewAnimator;
    private int backgroundRotation;
    private int backupAccentColor;
    private int backupAccentColor2;
    private long backupBackgroundGradientOverrideColor1;
    private long backupBackgroundGradientOverrideColor2;
    private long backupBackgroundGradientOverrideColor3;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private float backupIntensity;
    private int backupMyMessagesAccentColor;
    private boolean backupMyMessagesAnimated;
    private int backupMyMessagesGradientAccentColor1;
    private int backupMyMessagesGradientAccentColor2;
    private int backupMyMessagesGradientAccentColor3;
    private String backupSlug;
    private final PorterDuff.Mode blendMode;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private TextView cancelButton;
    private int checkColor;
    private ColorPicker colorPicker;
    private int colorType;
    private float currentIntensity;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private boolean deleteOnCancel;
    private DialogsAdapter dialogsAdapter;
    private TextView doneButton;
    private View dotsContainer;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private boolean editingTheme;
    private ImageView floatingButton;
    private FrameLayout frameLayout;
    private String imageFilter;
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    private boolean isBlurred;
    private boolean isMotion;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private TLRPC$TL_wallPaper lastSelectedPattern;
    private RecyclerListView listView;
    private RecyclerListView listView2;
    private int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    private FrameLayout messagesButtonsContainer;
    private WallpaperCheckBoxView[] messagesCheckBoxView;
    private ImageView messagesPlayAnimationImageView;
    private FrameLayout messagesPlayAnimationView;
    private AnimatorSet messagesPlayViewAnimator;
    private AnimatorSet motionAnimation;
    Theme.MessageDrawable msgOutDrawable;
    Theme.MessageDrawable msgOutDrawableSelected;
    Theme.MessageDrawable msgOutMediaDrawable;
    Theme.MessageDrawable msgOutMediaDrawableSelected;
    private boolean nightTheme;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Bitmap originalBitmap;
    private FrameLayout page1;
    private FrameLayout page2;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private int patternColor;
    private FrameLayout[] patternLayout;
    private AnimatorSet patternViewAnimation;
    private ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer;
    private TextView[] patternsCancelButton;
    private HashMap<Long, Object> patternsDict;
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor1;
    private int previousBackgroundGradientColor2;
    private int previousBackgroundGradientColor3;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC$TL_wallPaper previousSelectedPattern;
    private boolean progressVisible;
    private RadialProgress2 radialProgress;
    private boolean removeBackgroundOverride;
    private boolean rotatePreview;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    private final int screenType;
    private TLRPC$TL_wallPaper selectedPattern;
    private Drawable sheetDrawable;
    private boolean showColor;
    private List<ThemeDescription> themeDescriptions;
    private UndoView undoView;
    public boolean useDefaultThemeForButtons;
    private ViewPager viewPager;
    private boolean wasScroll;
    private long watchForKeyboardEndTime;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    public static void lambda$createView$1(View view, int i) {
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public void lambda$new$0() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
        this.lastPickedColorNum = -1;
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap) {
        this(obj, bitmap, false, false);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap, boolean z, boolean z2) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new Theme.MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new Theme.MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new Theme.MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.showColor = z2;
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        this.rotatePreview = z;
        if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
            this.isMotion = colorWallpaper.motion;
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = colorWallpaper.pattern;
            this.selectedPattern = tLRPC$TL_wallPaper;
            if (tLRPC$TL_wallPaper != null) {
                float f = colorWallpaper.intensity;
                this.currentIntensity = f;
                if (f < 0.0f && !Theme.getActiveTheme().isDark()) {
                    this.currentIntensity *= -1.0f;
                }
            }
        }
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean z, int i, boolean z2, boolean z3) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new Theme.MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new Theme.MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new Theme.MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = i;
        this.nightTheme = z3;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        this.editingTheme = z2;
        if (i == 1) {
            Theme.ThemeAccent accent = themeInfo.getAccent(!z2);
            this.accent = accent;
            if (accent != null) {
                this.useDefaultThemeForButtons = false;
                this.backupAccentColor = accent.accentColor;
                this.backupAccentColor2 = accent.accentColor2;
                this.backupMyMessagesAccentColor = accent.myMessagesAccentColor;
                this.backupMyMessagesGradientAccentColor1 = accent.myMessagesGradientAccentColor1;
                this.backupMyMessagesGradientAccentColor2 = accent.myMessagesGradientAccentColor2;
                this.backupMyMessagesGradientAccentColor3 = accent.myMessagesGradientAccentColor3;
                this.backupMyMessagesAnimated = accent.myMessagesAnimated;
                this.backupBackgroundOverrideColor = accent.backgroundOverrideColor;
                this.backupBackgroundGradientOverrideColor1 = accent.backgroundGradientOverrideColor1;
                this.backupBackgroundGradientOverrideColor2 = accent.backgroundGradientOverrideColor2;
                this.backupBackgroundGradientOverrideColor3 = accent.backgroundGradientOverrideColor3;
                this.backupIntensity = accent.patternIntensity;
                this.backupSlug = accent.patternSlug;
                this.backupBackgroundRotation = accent.backgroundRotation;
            }
        } else {
            if (i == 0) {
                this.useDefaultThemeForButtons = false;
            }
            Theme.ThemeAccent accent2 = themeInfo.getAccent(false);
            this.accent = accent2;
            if (accent2 != null) {
                this.selectedPattern = accent2.pattern;
            }
        }
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public void setInitialModes(boolean z, boolean z2) {
        this.isBlurred = z;
        this.isMotion = z2;
    }

    @Override
    public int getNavigationBarColor() {
        return super.getNavigationBarColor();
    }

    @Override
    @android.annotation.SuppressLint({"Recycle"})
    public android.view.View createView(android.content.Context r38) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.ThemePreviewActivity.createView(android.content.Context):android.view.View");
    }

    public void lambda$createView$2(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (z && drawable != null) {
                if (!Theme.hasThemeKey("chat_serviceBackground") || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                    Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable), drawable);
                }
                this.listView2.invalidateViews();
                FrameLayout frameLayout = this.backgroundButtonsContainer;
                if (frameLayout != null) {
                    int childCount = frameLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        this.backgroundButtonsContainer.getChildAt(i).invalidate();
                    }
                }
                FrameLayout frameLayout2 = this.messagesButtonsContainer;
                if (frameLayout2 != null) {
                    int childCount2 = frameLayout2.getChildCount();
                    for (int i2 = 0; i2 < childCount2; i2++) {
                        this.messagesButtonsContainer.getChildAt(i2).invalidate();
                    }
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
                }
                if (!z2 && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    public void lambda$createView$3(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public void lambda$createView$4(View view, int i, float f, float f2) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            if (!chatMessageCell.isInsideBackground(f, f2)) {
                selectColorType(2);
            } else if (chatMessageCell.getMessageObject().isOutOwner()) {
                selectColorType(3);
            } else {
                selectColorType(1);
            }
        }
    }

    public void lambda$createView$5(int i, int i2, float f) {
        if (this.isMotion) {
            this.backgroundImage.getBackground();
            float f2 = 1.0f;
            if (this.motionAnimation != null) {
                f2 = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            }
            this.backgroundImage.setTranslationX(i * f2);
            this.backgroundImage.setTranslationY(i2 * f2);
        }
    }

    public void lambda$createView$6(android.view.View r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.ThemePreviewActivity.lambda$createView$6(android.view.View):void");
    }

    public void lambda$createView$7(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.backgroundButtonsContainer.getAlpha() == 1.0f && this.patternViewAnimation == null) {
            int i2 = this.screenType;
            if ((i2 == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) && i == 2) {
                wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
                boolean isChecked = wallpaperCheckBoxView.isChecked();
                this.isMotion = isChecked;
                this.parallaxEffect.setEnabled(isChecked);
                animateMotionChange();
                return;
            }
            boolean z = false;
            if (i == 1 && (i2 == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper))) {
                if (this.backgroundCheckBoxView[1].isChecked()) {
                    this.lastSelectedPattern = this.selectedPattern;
                    this.backgroundImage.setImageDrawable(null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(false, true);
                    animateMotionChange();
                    if (this.patternLayout[1].getVisibility() == 0) {
                        if (this.screenType == 1) {
                            showPatternsView(0, true, true);
                        } else {
                            showPatternsView(i, this.patternLayout[i].getVisibility() != 0, true);
                        }
                    }
                } else {
                    selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                    if (this.screenType == 1) {
                        showPatternsView(1, true, true);
                    } else {
                        showPatternsView(i, this.patternLayout[i].getVisibility() != 0, true);
                    }
                }
                WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[1];
                if (this.selectedPattern != null) {
                    z = true;
                }
                wallpaperCheckBoxView2.setChecked(z, true);
                updateSelectedPattern(true);
                this.patternsListView.invalidateViews();
                updateMotionButton();
            } else if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                if (this.patternLayout[i].getVisibility() != 0) {
                    z = true;
                }
                showPatternsView(i, z, true);
            } else if (i2 != 1) {
                wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
                if (i == 0) {
                    boolean isChecked2 = wallpaperCheckBoxView.isChecked();
                    this.isBlurred = isChecked2;
                    if (isChecked2) {
                        this.backgroundImage.getImageReceiver().setForceCrossfade(true);
                    }
                    updateBlurred();
                    return;
                }
                boolean isChecked3 = wallpaperCheckBoxView.isChecked();
                this.isMotion = isChecked3;
                this.parallaxEffect.setEnabled(isChecked3);
                animateMotionChange();
            }
        }
    }

    public void lambda$createView$8(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.messagesButtonsContainer.getAlpha() == 1.0f && i == 0) {
            wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
            this.accent.myMessagesAnimated = wallpaperCheckBoxView.isChecked();
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
        }
    }

    public void lambda$createView$9(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (i == 0) {
                this.backgroundRotation = this.previousBackgroundRotation;
                setBackgroundColor(this.previousBackgroundGradientColor3, 3, true, true);
                setBackgroundColor(this.previousBackgroundGradientColor2, 2, true, true);
                setBackgroundColor(this.previousBackgroundGradientColor1, 1, true, true);
                setBackgroundColor(this.previousBackgroundColor, 0, true, true);
            } else {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.previousSelectedPattern;
                this.selectedPattern = tLRPC$TL_wallPaper;
                if (tLRPC$TL_wallPaper == null) {
                    this.backgroundImage.setImageDrawable(null);
                } else {
                    BackupImageView backupImageView = this.backgroundImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_wallPaper.document);
                    String str = this.imageFilter;
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = this.selectedPattern;
                    backupImageView.setImage(forDocument, str, null, null, "jpg", tLRPC$TL_wallPaper2.document.size, 1, tLRPC$TL_wallPaper2);
                }
                this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, false);
                float f = this.previousIntensity;
                this.currentIntensity = f;
                this.intensitySeekBar.setProgress(f);
                this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
                updateButtonState(false, true);
                updateSelectedPattern(true);
            }
            if (this.screenType == 2) {
                showPatternsView(i, false, true);
                return;
            }
            if (this.selectedPattern == null) {
                if (this.isMotion) {
                    this.isMotion = false;
                    this.backgroundCheckBoxView[0].setChecked(false, true);
                    animateMotionChange();
                }
                updateMotionButton();
            }
            showPatternsView(0, true, true);
        }
    }

    public void lambda$createView$10(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (this.screenType == 2) {
                showPatternsView(i, false, true);
            } else {
                showPatternsView(0, true, true);
            }
        }
    }

    public void lambda$createView$11(View view, int i) {
        boolean z = this.selectedPattern != null;
        selectPattern(i);
        if (z == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        int left = view.getLeft();
        int right = view.getRight();
        int dp = AndroidUtilities.m34dp(52.0f);
        int i2 = left - dp;
        if (i2 < 0) {
            this.patternsListView.smoothScrollBy(i2, 0);
            return;
        }
        int i3 = right + dp;
        if (i3 > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy(i3 - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    public class C354622 implements ColorPicker.ColorPickerDelegate {
        C354622() {
        }

        @Override
        public void setColor(int i, int i2, boolean z) {
            if (ThemePreviewActivity.this.screenType == 2) {
                ThemePreviewActivity.this.setBackgroundColor(i, i2, z, true);
            } else {
                ThemePreviewActivity.this.scheduleApplyColor(i, i2, z);
            }
        }

        @Override
        public void openThemeCreate(boolean z) {
            if (!z) {
                AlertsCreator.createThemeCreateDialog(ThemePreviewActivity.this, 1, null, null);
            } else if (ThemePreviewActivity.this.accent.info == null) {
                ThemePreviewActivity.this.finishFragment();
                MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).saveThemeToServer(ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
            } else {
                String str = "https://" + MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).linkPrefix + "/addtheme/" + ThemePreviewActivity.this.accent.info.slug;
                ThemePreviewActivity.this.showDialog(new ShareAlert(ThemePreviewActivity.this.getParentActivity(), null, str, false, str, false));
            }
        }

        @Override
        public void deleteTheme() {
            if (ThemePreviewActivity.this.getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ThemePreviewActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteThemeTitle", C0952R.string.DeleteThemeTitle));
                builder.setMessage(LocaleController.getString("DeleteThemeAlert", C0952R.string.DeleteThemeAlert));
                builder.setPositiveButton(LocaleController.getString("Delete", C0952R.string.Delete), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemePreviewActivity.C354622.this.lambda$deleteTheme$0(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", C0952R.string.Cancel), null);
                AlertDialog create = builder.create();
                ThemePreviewActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }

        public void lambda$deleteTheme$0(DialogInterface dialogInterface, int i) {
            Theme.deleteThemeAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.accent, true);
            Theme.applyPreviousTheme();
            Theme.refreshThemeColors();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, -1);
            ThemePreviewActivity.this.finishFragment();
        }

        @Override
        public int getDefaultColor(int i) {
            Theme.ThemeAccent themeAccent;
            if (ThemePreviewActivity.this.colorType != 3 || !ThemePreviewActivity.this.applyingTheme.firstAccentIsDefault || i != 0 || (themeAccent = ThemePreviewActivity.this.applyingTheme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID)) == null) {
                return 0;
            }
            return themeAccent.myMessagesAccentColor;
        }
    }

    public void lambda$createView$12() {
        this.watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
        this.frameLayout.invalidate();
    }

    public void lambda$createView$13(View view) {
        cancelThemeApply(false);
    }

    public void lambda$createView$14(View view) {
        Theme.ThemeAccent themeAccent;
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme != null) {
            int i = previousTheme.prevAccentId;
            if (i >= 0) {
                themeAccent = previousTheme.themeAccentsMap.get(i);
            } else {
                themeAccent = previousTheme.getAccent(false);
            }
            if (this.accent != null) {
                saveAccentWallpaper();
                Theme.saveThemeAccents(this.applyingTheme, true, false, false, false);
                Theme.clearPreviousTheme();
                Theme.applyTheme(this.applyingTheme, this.nightTheme);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            } else {
                this.parentLayout.rebuildAllFragmentViews(false, false);
                File file = new File(this.applyingTheme.pathToFile);
                Theme.ThemeInfo themeInfo = this.applyingTheme;
                Theme.applyThemeFile(file, themeInfo.name, themeInfo.info, false);
                MessagesController.getInstance(this.applyingTheme.account).saveTheme(this.applyingTheme, null, false, false);
                SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                edit.putString("lastDayTheme", this.applyingTheme.getKey());
                edit.commit();
            }
            finishFragment();
            if (this.screenType == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, themeAccent, Boolean.valueOf(this.deleteOnCancel));
            }
        }
    }

    public void onColorsRotate() {
        if (this.screenType == 2) {
            this.backgroundRotation += 45;
            while (true) {
                int i = this.backgroundRotation;
                if (i >= 360) {
                    this.backgroundRotation = i - 360;
                } else {
                    setBackgroundColor(this.backgroundColor, 0, true, true);
                    return;
                }
            }
        } else {
            this.accent.backgroundRotation += 45;
            while (true) {
                Theme.ThemeAccent themeAccent = this.accent;
                int i2 = themeAccent.backgroundRotation;
                if (i2 >= 360) {
                    themeAccent.backgroundRotation = i2 - 360;
                } else {
                    Theme.refreshThemeColors();
                    return;
                }
            }
        }
    }

    public void selectColorType(int i) {
        selectColorType(i, true);
    }

    private void selectColorType(int i, boolean z) {
        int i2;
        if (getParentActivity() != null && this.colorType != i && this.patternViewAnimation == null) {
            if (!z || i != 2 || (!Theme.hasCustomWallpaper() && this.accent.backgroundOverrideColor != 4294967296L)) {
                int i3 = this.colorType;
                this.colorType = i;
                if (i == 1) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", C0952R.string.ColorPickerMainColor));
                    this.colorPicker.setType(1, hasChanges(1), 2, this.accent.accentColor2 != 0 ? 2 : 1, false, 0, false);
                    this.colorPicker.setColor(this.accent.accentColor, 0);
                    int i4 = this.accent.accentColor2;
                    if (i4 != 0) {
                        this.colorPicker.setColor(i4, 1);
                    }
                    if (i3 == 2 || (i3 == 3 && this.accent.myMessagesGradientAccentColor2 != 0)) {
                        this.messagesAdapter.notifyItemRemoved(0);
                    }
                } else if (i == 2) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerBackground", C0952R.string.ColorPickerBackground));
                    int color = Theme.getColor("chat_wallpaper");
                    int color2 = Theme.hasThemeKey("chat_wallpaper_gradient_to") ? Theme.getColor("chat_wallpaper_gradient_to") : 0;
                    int color3 = Theme.hasThemeKey("key_chat_wallpaper_gradient_to2") ? Theme.getColor("key_chat_wallpaper_gradient_to2") : 0;
                    int color4 = Theme.hasThemeKey("key_chat_wallpaper_gradient_to3") ? Theme.getColor("key_chat_wallpaper_gradient_to3") : 0;
                    Theme.ThemeAccent themeAccent = this.accent;
                    long j = themeAccent.backgroundGradientOverrideColor1;
                    int i5 = (int) j;
                    if (i5 == 0 && j != 0) {
                        color2 = 0;
                    }
                    long j2 = themeAccent.backgroundGradientOverrideColor2;
                    int i6 = (int) j2;
                    if (i6 == 0 && j2 != 0) {
                        color3 = 0;
                    }
                    long j3 = themeAccent.backgroundGradientOverrideColor3;
                    int i7 = (int) j3;
                    if (i7 == 0 && j3 != 0) {
                        color4 = 0;
                    }
                    int i8 = (int) themeAccent.backgroundOverrideColor;
                    this.colorPicker.setType(2, hasChanges(2), 4, (i5 == 0 && color2 == 0) ? 1 : (i7 == 0 && color4 == 0) ? (i6 == 0 && color3 == 0) ? 2 : 3 : 4, false, this.accent.backgroundRotation, false);
                    ColorPicker colorPicker = this.colorPicker;
                    if (i7 == 0) {
                        i7 = color4;
                    }
                    colorPicker.setColor(i7, 3);
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i6 == 0) {
                        i6 = color3;
                    }
                    colorPicker2.setColor(i6, 2);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i5 == 0) {
                        i5 = color2;
                    }
                    colorPicker3.setColor(i5, 1);
                    ColorPicker colorPicker4 = this.colorPicker;
                    if (i8 != 0) {
                        color = i8;
                    }
                    colorPicker4.setColor(color, 0);
                    if (i3 == 1 || this.accent.myMessagesGradientAccentColor2 == 0) {
                        this.messagesAdapter.notifyItemInserted(0);
                    } else {
                        this.messagesAdapter.notifyItemChanged(0);
                    }
                    this.listView2.smoothScrollBy(0, AndroidUtilities.m34dp(60.0f));
                } else if (i == 3) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", C0952R.string.ColorPickerMyMessages));
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    if (themeAccent2.myMessagesGradientAccentColor1 == 0) {
                        i2 = 1;
                    } else if (themeAccent2.myMessagesGradientAccentColor3 != 0) {
                        i2 = 4;
                    } else {
                        i2 = themeAccent2.myMessagesGradientAccentColor2 != 0 ? 3 : 2;
                    }
                    this.colorPicker.setType(2, hasChanges(3), 4, i2, true, 0, false);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                    ColorPicker colorPicker5 = this.colorPicker;
                    Theme.ThemeAccent themeAccent3 = this.accent;
                    int i9 = themeAccent3.myMessagesAccentColor;
                    if (i9 == 0) {
                        i9 = themeAccent3.accentColor;
                    }
                    colorPicker5.setColor(i9, 0);
                    this.messagesCheckBoxView[1].setColor(0, this.accent.myMessagesAccentColor);
                    this.messagesCheckBoxView[1].setColor(1, this.accent.myMessagesGradientAccentColor1);
                    this.messagesCheckBoxView[1].setColor(2, this.accent.myMessagesGradientAccentColor2);
                    this.messagesCheckBoxView[1].setColor(3, this.accent.myMessagesGradientAccentColor3);
                    if (this.accent.myMessagesGradientAccentColor2 != 0) {
                        if (i3 == 1) {
                            this.messagesAdapter.notifyItemInserted(0);
                        } else {
                            this.messagesAdapter.notifyItemChanged(0);
                        }
                    } else if (i3 == 2) {
                        this.messagesAdapter.notifyItemRemoved(0);
                    }
                    this.listView2.smoothScrollBy(0, AndroidUtilities.m34dp(60.0f));
                    showAnimationHint();
                }
                if (i == 1 || i == 3) {
                    if (i3 == 2 && this.patternLayout[1].getVisibility() == 0) {
                        showPatternsView(0, true, true);
                    }
                    if (i != 1) {
                        this.colorPicker.setMinBrightness(0.0f);
                        this.colorPicker.setMaxBrightness(1.0f);
                    } else if (this.applyingTheme.isDark()) {
                        this.colorPicker.setMinBrightness(0.2f);
                    } else {
                        this.colorPicker.setMinBrightness(0.05f);
                        this.colorPicker.setMaxBrightness(0.8f);
                    }
                } else {
                    this.colorPicker.setMinBrightness(0.0f);
                    this.colorPicker.setMaxBrightness(1.0f);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ChangeChatBackground", C0952R.string.ChangeChatBackground));
                if (!Theme.hasCustomWallpaper() || Theme.isCustomWallpaperColor()) {
                    builder.setMessage(LocaleController.getString("ChangeColorToColor", C0952R.string.ChangeColorToColor));
                    builder.setPositiveButton(LocaleController.getString("Reset", C0952R.string.Reset), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i10) {
                            ThemePreviewActivity.this.lambda$selectColorType$15(dialogInterface, i10);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Continue", C0952R.string.Continue), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i10) {
                            ThemePreviewActivity.this.lambda$selectColorType$16(dialogInterface, i10);
                        }
                    });
                } else {
                    builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", C0952R.string.ChangeWallpaperToColor));
                    builder.setPositiveButton(LocaleController.getString("Change", C0952R.string.Change), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i10) {
                            ThemePreviewActivity.this.lambda$selectColorType$17(dialogInterface, i10);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0952R.string.Cancel), null);
                }
                showDialog(builder.create());
            }
        }
    }

    public void lambda$selectColorType$15(DialogInterface dialogInterface, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0L;
            themeAccent.backgroundGradientOverrideColor1 = 0L;
            themeAccent.backgroundGradientOverrideColor2 = 0L;
            themeAccent.backgroundGradientOverrideColor3 = 0L;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    public void lambda$selectColorType$16(DialogInterface dialogInterface, int i) {
        if (Theme.isCustomWallpaperColor()) {
            Theme.ThemeAccent themeAccent = this.accent;
            Theme.OverrideWallpaperInfo overrideWallpaperInfo = themeAccent.overrideWallpaper;
            themeAccent.backgroundOverrideColor = overrideWallpaperInfo.color;
            themeAccent.backgroundGradientOverrideColor1 = overrideWallpaperInfo.gradientColor1;
            themeAccent.backgroundGradientOverrideColor2 = overrideWallpaperInfo.gradientColor2;
            themeAccent.backgroundGradientOverrideColor3 = overrideWallpaperInfo.gradientColor3;
            themeAccent.backgroundRotation = overrideWallpaperInfo.rotation;
            String str = overrideWallpaperInfo.slug;
            themeAccent.patternSlug = str;
            float f = overrideWallpaperInfo.intensity;
            themeAccent.patternIntensity = f;
            this.currentIntensity = f;
            if (str != null && !"c".equals(str)) {
                int size = this.patterns.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i2);
                    if (tLRPC$TL_wallPaper.pattern && this.accent.patternSlug.equals(tLRPC$TL_wallPaper.slug)) {
                        this.selectedPattern = tLRPC$TL_wallPaper;
                        break;
                    }
                    i2++;
                }
            } else {
                this.selectedPattern = null;
            }
            this.removeBackgroundOverride = true;
            this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        Drawable background = this.backgroundImage.getBackground();
        if (background instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) background;
            motionBackgroundDrawable.setPatternBitmap(100, null);
            if (Theme.getActiveTheme().isDark()) {
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                }
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
            } else {
                float f2 = this.currentIntensity;
                if (f2 < 0.0f) {
                    this.currentIntensity = -f2;
                }
            }
        }
        SeekBarView seekBarView2 = this.intensitySeekBar;
        if (seekBarView2 != null) {
            seekBarView2.setProgress(this.currentIntensity);
        }
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    public void lambda$selectColorType$17(DialogInterface dialogInterface, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0L;
            themeAccent.backgroundGradientOverrideColor1 = 0L;
            themeAccent.backgroundGradientOverrideColor2 = 0L;
            themeAccent.backgroundGradientOverrideColor3 = 0L;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    private void selectPattern(int i) {
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (i < 0 || i >= this.patterns.size()) {
            tLRPC$TL_wallPaper = this.lastSelectedPattern;
        } else {
            tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i);
        }
        if (tLRPC$TL_wallPaper != null) {
            this.backgroundImage.setImage(ImageLocation.getForDocument(tLRPC$TL_wallPaper.document), this.imageFilter, null, null, "jpg", tLRPC$TL_wallPaper.document.size, 1, tLRPC$TL_wallPaper);
            this.selectedPattern = tLRPC$TL_wallPaper;
            this.isMotion = this.backgroundCheckBoxView[2].isChecked();
            updateButtonState(false, true);
        }
    }

    public void saveAccentWallpaper() {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            try {
                File pathToWallpaper = this.accent.getPathToWallpaper();
                Drawable background = this.backgroundImage.getBackground();
                Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                if (background instanceof MotionBackgroundDrawable) {
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 87, fileOutputStream);
                    fileOutputStream.close();
                } else {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    background.draw(canvas);
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                    paint.setAlpha((int) (this.currentIntensity * 255.0f));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                    FileOutputStream fileOutputStream2 = new FileOutputStream(pathToWallpaper);
                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream2);
                    fileOutputStream2.close();
                }
            } catch (Throwable th) {
                FileLog.m30e(th);
            }
        }
    }

    private boolean hasChanges(int i) {
        long j;
        int i2;
        if (this.editingTheme) {
            return false;
        }
        if (i == 1 || i == 2) {
            long j2 = this.backupBackgroundOverrideColor;
            if (j2 == 0) {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                int i3 = (int) this.accent.backgroundOverrideColor;
                if (i3 == 0) {
                    i3 = defaultAccentColor;
                }
                if (i3 != defaultAccentColor) {
                    return true;
                }
            } else if (j2 != this.accent.backgroundOverrideColor) {
                return true;
            }
            long j3 = this.backupBackgroundGradientOverrideColor1;
            if (j3 == 0 && this.backupBackgroundGradientOverrideColor2 == 0 && this.backupBackgroundGradientOverrideColor3 == 0) {
                for (int i4 = 0; i4 < 3; i4++) {
                    if (i4 == 0) {
                        i2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                        j = this.accent.backgroundGradientOverrideColor1;
                    } else if (i4 == 1) {
                        i2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                        j = this.accent.backgroundGradientOverrideColor2;
                    } else {
                        i2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                        j = this.accent.backgroundGradientOverrideColor3;
                    }
                    int i5 = (int) j;
                    if (i5 == 0 && j != 0) {
                        i5 = 0;
                    } else if (i5 == 0) {
                        i5 = i2;
                    }
                    if (i5 != i2) {
                        return true;
                    }
                }
            } else {
                Theme.ThemeAccent themeAccent = this.accent;
                if (!(j3 == themeAccent.backgroundGradientOverrideColor1 && this.backupBackgroundGradientOverrideColor2 == themeAccent.backgroundGradientOverrideColor2 && this.backupBackgroundGradientOverrideColor3 == themeAccent.backgroundGradientOverrideColor3)) {
                    return true;
                }
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (i == 1 || i == 3) {
            int i6 = this.backupAccentColor;
            Theme.ThemeAccent themeAccent2 = this.accent;
            if (i6 != themeAccent2.accentColor2) {
                return true;
            }
            int i7 = this.backupMyMessagesAccentColor;
            if (i7 == 0) {
                int i8 = themeAccent2.myMessagesAccentColor;
                if (!(i8 == 0 || i8 == themeAccent2.accentColor)) {
                    return true;
                }
            } else if (i7 != themeAccent2.myMessagesAccentColor) {
                return true;
            }
            int i9 = this.backupMyMessagesGradientAccentColor1;
            if (i9 != 0) {
                if (i9 != themeAccent2.myMessagesGradientAccentColor1) {
                    return true;
                }
            } else if (themeAccent2.myMessagesGradientAccentColor1 != 0) {
                return true;
            }
            int i10 = this.backupMyMessagesGradientAccentColor2;
            if (i10 != 0) {
                if (i10 != themeAccent2.myMessagesGradientAccentColor2) {
                    return true;
                }
            } else if (themeAccent2.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            int i11 = this.backupMyMessagesGradientAccentColor3;
            if (i11 != 0) {
                if (i11 != themeAccent2.myMessagesGradientAccentColor3) {
                    return true;
                }
            } else if (themeAccent2.myMessagesGradientAccentColor3 != 0) {
                return true;
            }
            if (this.backupMyMessagesAnimated != themeAccent2.myMessagesAnimated) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDiscard() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    public void lambda$checkDiscard$18(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    public void lambda$checkDiscard$19(DialogInterface dialogInterface, int i) {
        cancelThemeApply(false);
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.invalidateMotionBackground);
        int i = this.screenType;
        if (i == 1 || i == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        int i2 = this.screenType;
        if (i2 == 2 || i2 == 1) {
            Theme.setChangingWallpaper(true);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            if (SharedConfig.getDevicePerformanceClass() == 0) {
                Point point = AndroidUtilities.displaySize;
                int min = Math.min(point.x, point.y);
                Point point2 = AndroidUtilities.displaySize;
                int max = Math.max(point2.x, point2.y);
                this.imageFilter = ((int) (min / AndroidUtilities.density)) + "_" + ((int) (max / AndroidUtilities.density)) + "_f";
            } else {
                this.imageFilter = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
            }
            Point point3 = AndroidUtilities.displaySize;
            this.maxWallpaperSize = Math.min(1920, Math.max(point3.x, point3.y));
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
            if (this.patterns == null) {
                this.patterns = new ArrayList<>();
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.invalidateMotionBackground);
        FrameLayout frameLayout = this.frameLayout;
        if (!(frameLayout == null || this.onGlobalLayoutListener == null)) {
            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2 || i == 1) {
            AndroidUtilities.runOnUIThread(ThemePreviewActivity$$ExternalSyntheticLambda18.INSTANCE);
        }
        int i2 = this.screenType;
        if (i2 == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        } else if (i2 == 1 || i2 == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (!(this.screenType == 0 && this.accent == null)) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
    }

    @Override
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (!z && this.screenType == 2) {
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        MessagesAdapter messagesAdapter = this.messagesAdapter;
        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    @Override
    public void onSuccessDownload(String str) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(1.0f, this.progressVisible);
        }
        updateButtonState(false, true);
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                updateButtonState(false, true);
            }
        }
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.originalBitmap = bitmap;
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.originalBitmap = imageReceiver.getBitmap();
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            Bitmap bitmap2 = this.blurredBitmap;
            if (bitmap2 != null) {
                this.backgroundImage.setImageBitmap(bitmap2);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    @Override
    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return false;
        }
        cancelThemeApply(true);
        return true;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                int childCount = recyclerListView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        } else if (i == NotificationCenter.invalidateMotionBackground) {
            RecyclerListView recyclerListView2 = this.listView2;
            if (recyclerListView2 != null) {
                recyclerListView2.invalidateViews();
            }
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
            }
        } else if (i == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = (String) objArr[0];
                }
            }
        } else if (i == NotificationCenter.wallpapersDidLoad) {
            ArrayList arrayList = (ArrayList) objArr[0];
            this.patterns.clear();
            this.patternsDict.clear();
            int size = arrayList.size();
            boolean z = false;
            for (int i4 = 0; i4 < size; i4++) {
                TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) arrayList.get(i4);
                if ((tLRPC$WallPaper instanceof TLRPC$TL_wallPaper) && tLRPC$WallPaper.pattern) {
                    TLRPC$Document tLRPC$Document = tLRPC$WallPaper.document;
                    if (tLRPC$Document != null && !this.patternsDict.containsKey(Long.valueOf(tLRPC$Document.f861id))) {
                        this.patterns.add(tLRPC$WallPaper);
                        this.patternsDict.put(Long.valueOf(tLRPC$WallPaper.document.f861id), tLRPC$WallPaper);
                    }
                    Theme.ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(tLRPC$WallPaper.slug)) {
                        this.selectedPattern = (TLRPC$TL_wallPaper) tLRPC$WallPaper;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                    } else if (this.accent == null) {
                        TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = this.selectedPattern;
                        if (tLRPC$TL_wallPaper2 != null) {
                            if (!tLRPC$TL_wallPaper2.slug.equals(tLRPC$WallPaper.slug)) {
                            }
                        }
                    }
                    z = true;
                }
            }
            if (!z && (tLRPC$TL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tLRPC$TL_wallPaper);
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            long j = 0;
            int size2 = arrayList.size();
            for (int i5 = 0; i5 < size2; i5++) {
                TLRPC$WallPaper tLRPC$WallPaper2 = (TLRPC$WallPaper) arrayList.get(i5);
                if (tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaper) {
                    j = MediaDataController.calcHash(j, tLRPC$WallPaper2.f993id);
                }
            }
            TLRPC$TL_account_getWallPapers tLRPC$TL_account_getWallPapers = new TLRPC$TL_account_getWallPapers();
            tLRPC$TL_account_getWallPapers.hash = j;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getWallPapers, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ThemePreviewActivity.this.lambda$didReceivedNotification$24(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    public void lambda$didReceivedNotification$24(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$didReceivedNotification$23(tLObject);
            }
        });
    }

    public void lambda$didReceivedNotification$23(TLObject tLObject) {
        Theme.ThemeAccent themeAccent;
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (tLObject instanceof TLRPC$TL_account_wallPapers) {
            TLRPC$TL_account_wallPapers tLRPC$TL_account_wallPapers = (TLRPC$TL_account_wallPapers) tLObject;
            this.patterns.clear();
            this.patternsDict.clear();
            int size = tLRPC$TL_account_wallPapers.wallpapers.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                if (tLRPC$TL_account_wallPapers.wallpapers.get(i) instanceof TLRPC$TL_wallPaper) {
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = (TLRPC$TL_wallPaper) tLRPC$TL_account_wallPapers.wallpapers.get(i);
                    if (tLRPC$TL_wallPaper2.pattern) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_wallPaper2.document;
                        if (tLRPC$Document != null && !this.patternsDict.containsKey(Long.valueOf(tLRPC$Document.f861id))) {
                            this.patterns.add(tLRPC$TL_wallPaper2);
                            this.patternsDict.put(Long.valueOf(tLRPC$TL_wallPaper2.document.f861id), tLRPC$TL_wallPaper2);
                        }
                        Theme.ThemeAccent themeAccent2 = this.accent;
                        if (themeAccent2 != null && themeAccent2.patternSlug.equals(tLRPC$TL_wallPaper2.slug)) {
                            this.selectedPattern = tLRPC$TL_wallPaper2;
                            setCurrentImage(false);
                            updateButtonState(false, false);
                        } else if (this.accent == null) {
                            TLRPC$TL_wallPaper tLRPC$TL_wallPaper3 = this.selectedPattern;
                            if (tLRPC$TL_wallPaper3 != null) {
                                if (!tLRPC$TL_wallPaper3.slug.equals(tLRPC$TL_wallPaper2.slug)) {
                                }
                            }
                        }
                        z = true;
                    }
                }
            }
            if (!z && (tLRPC$TL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tLRPC$TL_wallPaper);
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tLRPC$TL_account_wallPapers.wallpapers, 1);
        }
        if (!(this.selectedPattern != null || (themeAccent = this.accent) == null || TextUtils.isEmpty(themeAccent.patternSlug))) {
            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
            TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
            tLRPC$TL_inputWallPaperSlug.slug = this.accent.patternSlug;
            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaperSlug;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_account_getWallPaper, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                    ThemePreviewActivity.this.lambda$didReceivedNotification$22(tLObject2, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    public void lambda$didReceivedNotification$22(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$didReceivedNotification$21(tLObject);
            }
        });
    }

    public void lambda$didReceivedNotification$21(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLObject;
            if (tLRPC$TL_wallPaper.pattern) {
                this.selectedPattern = tLRPC$TL_wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter = this.patternsAdapter;
                if (patternsAdapter != null) {
                    patternsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void cancelThemeApply(boolean z) {
        if (this.screenType != 2) {
            Theme.applyPreviousTheme();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            if (this.screenType == 1) {
                if (this.editingTheme) {
                    Theme.ThemeAccent themeAccent = this.accent;
                    themeAccent.accentColor = this.backupAccentColor;
                    themeAccent.accentColor2 = this.backupAccentColor2;
                    themeAccent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                    themeAccent.myMessagesGradientAccentColor1 = this.backupMyMessagesGradientAccentColor1;
                    themeAccent.myMessagesGradientAccentColor2 = this.backupMyMessagesGradientAccentColor2;
                    themeAccent.myMessagesGradientAccentColor3 = this.backupMyMessagesGradientAccentColor3;
                    themeAccent.myMessagesAnimated = this.backupMyMessagesAnimated;
                    themeAccent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                    themeAccent.backgroundGradientOverrideColor1 = this.backupBackgroundGradientOverrideColor1;
                    themeAccent.backgroundGradientOverrideColor2 = this.backupBackgroundGradientOverrideColor2;
                    themeAccent.backgroundGradientOverrideColor3 = this.backupBackgroundGradientOverrideColor3;
                    themeAccent.backgroundRotation = this.backupBackgroundRotation;
                    themeAccent.patternSlug = this.backupSlug;
                    themeAccent.patternIntensity = this.backupIntensity;
                }
                Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
            } else {
                if (this.accent != null) {
                    Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
                }
                this.parentLayout.rebuildAllFragmentViews(false, false);
                if (this.deleteOnCancel) {
                    Theme.ThemeInfo themeInfo = this.applyingTheme;
                    if (themeInfo.pathToFile != null && !Theme.isThemeInstalled(themeInfo)) {
                        new File(this.applyingTheme.pathToFile).delete();
                    }
                }
            }
            if (!z) {
                finishFragment();
            }
        } else if (!z) {
            finishFragment();
        }
    }

    public int getButtonsColor(String str) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(str) : Theme.getColor(str);
    }

    public void scheduleApplyColor(int i, int i2, boolean z) {
        if (i2 == -1) {
            int i3 = this.colorType;
            if (i3 == 1 || i3 == 2) {
                long j = this.backupBackgroundOverrideColor;
                if (j != 0) {
                    this.accent.backgroundOverrideColor = j;
                } else {
                    this.accent.backgroundOverrideColor = 0L;
                }
                long j2 = this.backupBackgroundGradientOverrideColor1;
                if (j2 != 0) {
                    this.accent.backgroundGradientOverrideColor1 = j2;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 0L;
                }
                long j3 = this.backupBackgroundGradientOverrideColor2;
                if (j3 != 0) {
                    this.accent.backgroundGradientOverrideColor2 = j3;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 0L;
                }
                long j4 = this.backupBackgroundGradientOverrideColor3;
                if (j4 != 0) {
                    this.accent.backgroundGradientOverrideColor3 = j4;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 0L;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (i3 == 2) {
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int defaultAccentColor3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int defaultAccentColor4 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                    Theme.ThemeAccent themeAccent = this.accent;
                    int i4 = (int) themeAccent.backgroundGradientOverrideColor1;
                    int i5 = (int) themeAccent.backgroundGradientOverrideColor2;
                    int i6 = (int) themeAccent.backgroundGradientOverrideColor3;
                    int i7 = (int) themeAccent.backgroundOverrideColor;
                    ColorPicker colorPicker = this.colorPicker;
                    if (i6 != 0) {
                        defaultAccentColor4 = i6;
                    }
                    colorPicker.setColor(defaultAccentColor4, 3);
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i5 != 0) {
                        defaultAccentColor3 = i5;
                    }
                    colorPicker2.setColor(defaultAccentColor3, 2);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i4 != 0) {
                        defaultAccentColor2 = i4;
                    }
                    colorPicker3.setColor(defaultAccentColor2, 1);
                    ColorPicker colorPicker4 = this.colorPicker;
                    if (i7 != 0) {
                        defaultAccentColor = i7;
                    }
                    colorPicker4.setColor(defaultAccentColor, 0);
                }
            }
            int i8 = this.colorType;
            if (i8 == 1 || i8 == 3) {
                int i9 = this.backupMyMessagesAccentColor;
                if (i9 != 0) {
                    this.accent.myMessagesAccentColor = i9;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                int i10 = this.backupMyMessagesGradientAccentColor1;
                if (i10 != 0) {
                    this.accent.myMessagesGradientAccentColor1 = i10;
                } else {
                    this.accent.myMessagesGradientAccentColor1 = 0;
                }
                int i11 = this.backupMyMessagesGradientAccentColor2;
                if (i11 != 0) {
                    this.accent.myMessagesGradientAccentColor2 = i11;
                } else {
                    this.accent.myMessagesGradientAccentColor2 = 0;
                }
                int i12 = this.backupMyMessagesGradientAccentColor3;
                if (i12 != 0) {
                    this.accent.myMessagesGradientAccentColor3 = i12;
                } else {
                    this.accent.myMessagesGradientAccentColor3 = 0;
                }
                if (i8 == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                    ColorPicker colorPicker5 = this.colorPicker;
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    int i13 = themeAccent2.myMessagesAccentColor;
                    if (i13 == 0) {
                        i13 = themeAccent2.accentColor;
                    }
                    colorPicker5.setColor(i13, 0);
                }
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            return;
        }
        int i14 = this.lastPickedColorNum;
        if (!(i14 == -1 || i14 == i2)) {
            this.applyColorAction.run();
        }
        this.lastPickedColor = i;
        this.lastPickedColorNum = i2;
        if (z) {
            this.applyColorAction.run();
        } else if (!this.applyColorScheduled) {
            this.applyColorScheduled = true;
            this.fragmentView.postDelayed(this.applyColorAction, 16L);
        }
    }

    private void applyColor(int i, int i2) {
        int i3 = this.colorType;
        if (i3 == 1) {
            if (i2 == 0) {
                this.accent.accentColor = i;
                Theme.refreshThemeColors();
            } else if (i2 == 1) {
                this.accent.accentColor2 = i;
                Theme.refreshThemeColors(true, true);
                this.listView2.invalidateViews();
                this.colorPicker.setHasChanges(hasChanges(this.colorType));
                updatePlayAnimationView(true);
            }
        } else if (i3 == 2) {
            if (this.lastPickedColorNum == 0) {
                this.accent.backgroundOverrideColor = i;
            } else if (i2 == 1) {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                if (i != 0 || defaultAccentColor == 0) {
                    this.accent.backgroundGradientOverrideColor1 = i;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 4294967296L;
                }
            } else if (i2 == 2) {
                int defaultAccentColor2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                if (i != 0 || defaultAccentColor2 == 0) {
                    this.accent.backgroundGradientOverrideColor2 = i;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 4294967296L;
                }
            } else if (i2 == 3) {
                int defaultAccentColor3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                if (i != 0 || defaultAccentColor3 == 0) {
                    this.accent.backgroundGradientOverrideColor3 = i;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 4294967296L;
                }
            }
            Theme.refreshThemeColors(true, false);
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
        } else if (i3 == 3) {
            int i4 = this.lastPickedColorNum;
            if (i4 == 0) {
                this.accent.myMessagesAccentColor = i;
            } else if (i4 == 1) {
                this.accent.myMessagesGradientAccentColor1 = i;
            } else if (i4 == 2) {
                Theme.ThemeAccent themeAccent = this.accent;
                int i5 = themeAccent.myMessagesGradientAccentColor2;
                themeAccent.myMessagesGradientAccentColor2 = i;
                if (i5 != 0 && i == 0) {
                    this.messagesAdapter.notifyItemRemoved(0);
                } else if (i5 == 0 && i != 0) {
                    this.messagesAdapter.notifyItemInserted(0);
                    showAnimationHint();
                }
            } else {
                this.accent.myMessagesGradientAccentColor3 = i;
            }
            int i6 = this.lastPickedColorNum;
            if (i6 >= 0) {
                this.messagesCheckBoxView[1].setColor(i6, i);
            }
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
        }
        int size = this.themeDescriptions.size();
        for (int i7 = 0; i7 < size; i7++) {
            ThemeDescription themeDescription = this.themeDescriptions.get(i7);
            themeDescription.setColor(Theme.getColor(themeDescription.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private void updateButtonState(boolean z, boolean z2) {
        File file;
        String str;
        int i;
        FrameLayout frameLayout;
        String str2;
        File file2;
        Object obj = this.selectedPattern;
        if (obj == null) {
            obj = this.currentWallpaper;
        }
        boolean z3 = obj instanceof TLRPC$TL_wallPaper;
        if (z3 || (obj instanceof MediaController.SearchImage)) {
            if (z2 && !this.progressVisible) {
                z2 = false;
            }
            if (z3) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
                str = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
                if (!TextUtils.isEmpty(str)) {
                    file = FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$TL_wallPaper.document, true);
                    i = tLRPC$TL_wallPaper.document.size;
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC$Photo tLRPC$Photo = searchImage.photo;
                if (tLRPC$Photo != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, this.maxWallpaperSize, true);
                    file2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true);
                    str2 = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    i = closestPhotoSizeWithSize.size;
                } else {
                    file2 = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                    str2 = file2.getName();
                    i = searchImage.size;
                }
                str = str2;
                file = file2;
                if (TextUtils.isEmpty(str)) {
                    return;
                }
            }
            boolean exists = file.exists();
            float f = 1.0f;
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setProgress(1.0f, z2);
                    this.radialProgress.setIcon(4, z, z2);
                }
                this.backgroundImage.invalidate();
                if (this.screenType == 2) {
                    if (i != 0) {
                        this.actionBar2.setSubtitle(AndroidUtilities.formatFileSize(i));
                    } else {
                        this.actionBar2.setSubtitle(null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, null, this);
                if (this.radialProgress != null) {
                    FileLoader.getInstance(this.currentAccount).isLoadingFile(str);
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(str);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                    this.radialProgress.setIcon(10, z, z2);
                }
                if (this.screenType == 2) {
                    this.actionBar2.setSubtitle(LocaleController.getString("LoadingFullImage", C0952R.string.LoadingFullImage));
                }
                this.backgroundImage.invalidate();
            }
            if (this.selectedPattern == null && (frameLayout = this.backgroundButtonsContainer) != null) {
                frameLayout.setAlpha(exists ? 1.0f : 0.5f);
            }
            int i2 = this.screenType;
            if (i2 == 0) {
                this.doneButton.setEnabled(exists);
                TextView textView = this.doneButton;
                if (!exists) {
                    f = 0.5f;
                }
                textView.setAlpha(f);
            } else if (i2 == 2) {
                this.bottomOverlayChat.setEnabled(exists);
                TextView textView2 = this.bottomOverlayChatText;
                if (!exists) {
                    f = 0.5f;
                }
                textView2.setAlpha(f);
            } else {
                this.saveItem.setEnabled(exists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (!exists) {
                    f = 0.5f;
                }
                actionBarMenuItem.setAlpha(f);
            }
        } else {
            RadialProgress2 radialProgress22 = this.radialProgress;
            if (radialProgress22 != null) {
                radialProgress22.setIcon(4, z, z2);
            }
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) this.currentWallpaper;
            if (colorWallpaper.patternId != 0) {
                int i = 0;
                int size = arrayList.size();
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i);
                    if (tLRPC$TL_wallPaper.f993id == colorWallpaper.patternId) {
                        this.selectedPattern = tLRPC$TL_wallPaper;
                        break;
                    }
                    i++;
                }
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    private void showAnimationHint() {
        if (this.page2 != null && this.messagesCheckBoxView != null && this.accent.myMessagesGradientAccentColor2 != 0) {
            final SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("bganimationhint", false)) {
                if (this.animationHint == null) {
                    HintView hintView = new HintView(getParentActivity(), 8);
                    this.animationHint = hintView;
                    hintView.setShowingDuration(5000L);
                    this.animationHint.setAlpha(0.0f);
                    this.animationHint.setVisibility(4);
                    this.animationHint.setText(LocaleController.getString("BackgroundAnimateInfo", C0952R.string.BackgroundAnimateInfo));
                    this.animationHint.setExtraTranslationY(AndroidUtilities.m34dp(6.0f));
                    this.frameLayout.addView(this.animationHint, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ThemePreviewActivity.this.lambda$showAnimationHint$25(globalMainSettings);
                    }
                }, 500L);
            }
        }
    }

    public void lambda$showAnimationHint$25(SharedPreferences sharedPreferences) {
        if (this.colorType == 3) {
            sharedPreferences.edit().putBoolean("bganimationhint", true).commit();
            this.animationHint.showForView(this.messagesCheckBoxView[0], true);
        }
    }

    private void updateSelectedPattern(boolean z) {
        int childCount = this.patternsListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.patternsListView.getChildAt(i);
            if (childAt instanceof PatternCell) {
                ((PatternCell) childAt).updateSelected(z);
            }
        }
    }

    private void updateMotionButton() {
        int i = this.screenType;
        float f = 1.0f;
        float f2 = 0.0f;
        if (i == 1 || i == 2) {
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.backgroundCheckBoxView[2].setChecked(false, true);
            }
            this.backgroundCheckBoxView[this.selectedPattern != null ? (char) 2 : (char) 0].setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr);
            WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (this.selectedPattern != null) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ThemePreviewActivity.this.backgroundCheckBoxView[ThemePreviewActivity.this.selectedPattern != null ? (char) 0 : (char) 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200L);
            animatorSet.start();
            return;
        }
        boolean isEnabled = this.backgroundCheckBoxView[0].isEnabled();
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.selectedPattern;
        if (isEnabled != (tLRPC$TL_wallPaper != null)) {
            if (tLRPC$TL_wallPaper == null) {
                this.backgroundCheckBoxView[0].setChecked(false, true);
            }
            this.backgroundCheckBoxView[0].setEnabled(this.selectedPattern != null);
            if (this.selectedPattern != null) {
                this.backgroundCheckBoxView[0].setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            int dp = (((FrameLayout.LayoutParams) this.backgroundCheckBoxView[1].getLayoutParams()).width + AndroidUtilities.m34dp(9.0f)) / 2;
            Animator[] animatorArr2 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[0];
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            if (this.selectedPattern == null) {
                f = 0.0f;
            }
            fArr3[0] = f;
            animatorArr2[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property3, fArr3);
            animatorSet2.playTogether(animatorArr2);
            Animator[] animatorArr3 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView4 = this.backgroundCheckBoxView[0];
            Property property4 = View.TRANSLATION_X;
            float[] fArr4 = new float[1];
            fArr4[0] = this.selectedPattern != null ? 0.0f : dp;
            animatorArr3[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property4, fArr4);
            animatorSet2.playTogether(animatorArr3);
            Animator[] animatorArr4 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView5 = this.backgroundCheckBoxView[1];
            Property property5 = View.TRANSLATION_X;
            float[] fArr5 = new float[1];
            if (this.selectedPattern == null) {
                f2 = -dp;
            }
            fArr5[0] = f2;
            animatorArr4[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property5, fArr5);
            animatorSet2.playTogether(animatorArr4);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.setDuration(200L);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (ThemePreviewActivity.this.selectedPattern == null) {
                        ThemePreviewActivity.this.backgroundCheckBoxView[0].setVisibility(4);
                    }
                }
            });
            animatorSet2.start();
        }
    }

    public void showPatternsView(final int i, final boolean z, boolean z2) {
        int i2;
        char c = 0;
        final boolean z3 = z && i == 1 && this.selectedPattern != null;
        if (z) {
            if (i != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.selectedPattern;
                    if (tLRPC$TL_wallPaper == null) {
                        i2 = 0;
                    } else {
                        i2 = arrayList.indexOf(tLRPC$TL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(i2, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.m34dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                int i3 = this.backgroundGradientColor1;
                this.previousBackgroundGradientColor1 = i3;
                int i4 = this.backgroundGradientColor2;
                this.previousBackgroundGradientColor2 = i4;
                int i5 = this.backgroundGradientColor3;
                this.previousBackgroundGradientColor3 = i5;
                int i6 = this.backupBackgroundRotation;
                this.previousBackgroundRotation = i6;
                this.colorPicker.setType(0, false, 4, i5 != 0 ? 4 : i4 != 0 ? 3 : i3 != 0 ? 2 : 1, false, i6, false);
                this.colorPicker.setColor(this.backgroundGradientColor3, 3);
                this.colorPicker.setColor(this.backgroundGradientColor2, 2);
                this.colorPicker.setColor(this.backgroundGradientColor1, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        int i7 = this.screenType;
        if (i7 == 1 || i7 == 2) {
            this.backgroundCheckBoxView[z3 ? (char) 2 : (char) 0].setVisibility(0);
        }
        if (i == 1 && !this.intensitySeekBar.isTwoSided()) {
            float f = this.currentIntensity;
            if (f < 0.0f) {
                float f2 = -f;
                this.currentIntensity = f2;
                this.intensitySeekBar.setProgress(f2);
            }
        }
        float f3 = 1.0f;
        if (z2) {
            this.patternViewAnimation = new AnimatorSet();
            ArrayList arrayList2 = new ArrayList();
            int i8 = i == 0 ? 1 : 0;
            if (z) {
                this.patternLayout[i].setVisibility(0);
                int i9 = this.screenType;
                if (i9 == 1) {
                    RecyclerListView recyclerListView = this.listView2;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = i == 1 ? -AndroidUtilities.m34dp(21.0f) : 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(recyclerListView, property, fArr));
                    WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    fArr2[0] = z3 ? 1.0f : 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView, property2, fArr2));
                    WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = z3 ? 0.0f : 1.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property3, fArr3));
                    if (i == 1) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, 0.0f, 1.0f));
                    } else {
                        this.patternLayout[i].setAlpha(1.0f);
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i8], View.ALPHA, 0.0f));
                    }
                    this.colorPicker.hideKeyboard();
                } else if (i9 == 2) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, (-this.patternLayout[i].getMeasuredHeight()) + AndroidUtilities.m34dp(48.0f)));
                    WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[2];
                    Property property4 = View.ALPHA;
                    float[] fArr4 = new float[1];
                    fArr4[0] = z3 ? 1.0f : 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property4, fArr4));
                    WallpaperCheckBoxView wallpaperCheckBoxView4 = this.backgroundCheckBoxView[0];
                    Property property5 = View.ALPHA;
                    float[] fArr5 = new float[1];
                    if (z3) {
                        f3 = 0.0f;
                    }
                    fArr5[0] = f3;
                    arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property5, fArr5));
                    arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, 0.0f));
                    if (this.patternLayout[i8].getVisibility() == 0) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i8], View.ALPHA, 0.0f));
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, 0.0f, 1.0f));
                        this.patternLayout[i].setTranslationY(0.0f);
                    } else {
                        FrameLayout[] frameLayoutArr = this.patternLayout;
                        arrayList2.add(ObjectAnimator.ofFloat(frameLayoutArr[i], View.TRANSLATION_Y, frameLayoutArr[i].getMeasuredHeight(), 0.0f));
                    }
                } else {
                    if (i == 1) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, 0.0f, 1.0f));
                    } else {
                        this.patternLayout[i].setAlpha(1.0f);
                        arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i8], View.ALPHA, 0.0f));
                    }
                    this.colorPicker.hideKeyboard();
                }
            } else {
                arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, 0.0f));
                FrameLayout[] frameLayoutArr2 = this.patternLayout;
                arrayList2.add(ObjectAnimator.ofFloat(frameLayoutArr2[i], View.TRANSLATION_Y, frameLayoutArr2[i].getMeasuredHeight()));
                arrayList2.add(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[0], View.ALPHA, 1.0f));
                arrayList2.add(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[2], View.ALPHA, 0.0f));
                arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, 1.0f));
            }
            this.patternViewAnimation.playTogether(arrayList2);
            final int i10 = i8;
            this.patternViewAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ThemePreviewActivity.this.patternViewAnimation = null;
                    if (z && ThemePreviewActivity.this.patternLayout[i10].getVisibility() == 0) {
                        ThemePreviewActivity.this.patternLayout[i10].setAlpha(1.0f);
                        ThemePreviewActivity.this.patternLayout[i10].setVisibility(4);
                    } else if (!z) {
                        ThemePreviewActivity.this.patternLayout[i].setVisibility(4);
                    }
                    char c2 = 2;
                    if (ThemePreviewActivity.this.screenType == 1 || ThemePreviewActivity.this.screenType == 2) {
                        WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = ThemePreviewActivity.this.backgroundCheckBoxView;
                        if (z3) {
                            c2 = 0;
                        }
                        wallpaperCheckBoxViewArr[c2].setVisibility(4);
                    } else if (i == 1) {
                        ThemePreviewActivity.this.patternLayout[i10].setAlpha(0.0f);
                    }
                }
            });
            this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.patternViewAnimation.setDuration(200L);
            this.patternViewAnimation.start();
            return;
        }
        char c2 = i == 0 ? (char) 1 : (char) 0;
        if (z) {
            this.patternLayout[i].setVisibility(0);
            int i11 = this.screenType;
            if (i11 == 1) {
                this.listView2.setTranslationY(i == 1 ? -AndroidUtilities.m34dp(21.0f) : 0.0f);
                this.backgroundCheckBoxView[2].setAlpha(z3 ? 1.0f : 0.0f);
                this.backgroundCheckBoxView[0].setAlpha(z3 ? 0.0f : 1.0f);
                if (i == 1) {
                    this.patternLayout[i].setAlpha(1.0f);
                } else {
                    this.patternLayout[i].setAlpha(1.0f);
                    this.patternLayout[c2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            } else if (i11 == 2) {
                this.listView2.setTranslationY((-AndroidUtilities.m34dp(i == 0 ? 343.0f : 316.0f)) + AndroidUtilities.m34dp(48.0f));
                this.backgroundCheckBoxView[2].setAlpha(z3 ? 1.0f : 0.0f);
                this.backgroundCheckBoxView[0].setAlpha(z3 ? 0.0f : 1.0f);
                this.backgroundImage.setAlpha(0.0f);
                if (this.patternLayout[c2].getVisibility() == 0) {
                    this.patternLayout[c2].setAlpha(0.0f);
                    this.patternLayout[i].setAlpha(1.0f);
                    this.patternLayout[i].setTranslationY(0.0f);
                } else {
                    this.patternLayout[i].setTranslationY(0.0f);
                }
            } else {
                if (i == 1) {
                    this.patternLayout[i].setAlpha(1.0f);
                } else {
                    this.patternLayout[i].setAlpha(1.0f);
                    this.patternLayout[c2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            }
        } else {
            this.listView2.setTranslationY(0.0f);
            FrameLayout[] frameLayoutArr3 = this.patternLayout;
            frameLayoutArr3[i].setTranslationY(frameLayoutArr3[i].getMeasuredHeight());
            this.backgroundCheckBoxView[0].setAlpha(1.0f);
            this.backgroundCheckBoxView[2].setAlpha(1.0f);
            this.backgroundImage.setAlpha(1.0f);
        }
        if (z && this.patternLayout[c2].getVisibility() == 0) {
            this.patternLayout[c2].setAlpha(1.0f);
            this.patternLayout[c2].setVisibility(4);
        } else if (!z) {
            this.patternLayout[i].setVisibility(4);
        }
        int i12 = this.screenType;
        if (i12 == 1 || i12 == 2) {
            WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
            if (!z3) {
                c = 2;
            }
            wallpaperCheckBoxViewArr[c].setVisibility(4);
        } else if (i == 1) {
            this.patternLayout[c2].setAlpha(0.0f);
        }
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.motionAnimation = animatorSet2;
        if (this.isMotion) {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, this.parallaxScale), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, this.parallaxScale));
        } else {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, 0.0f));
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void updatePlayAnimationView(boolean r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.ThemePreviewActivity.updatePlayAnimationView(boolean):void");
    }

    public void setBackgroundColor(int i, int i2, boolean z, boolean z2) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (i2 == 0) {
            this.backgroundColor = i;
        } else if (i2 == 1) {
            this.backgroundGradientColor1 = i;
        } else if (i2 == 2) {
            this.backgroundGradientColor2 = i;
        } else if (i2 == 3) {
            this.backgroundGradientColor3 = i;
        }
        updatePlayAnimationView(z2);
        if (this.backgroundCheckBoxView != null) {
            int i3 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                if (i3 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[i3] != null) {
                    wallpaperCheckBoxViewArr[i3].setColor(i2, i);
                }
                i3++;
            }
        }
        if (this.backgroundGradientColor2 != 0) {
            if (this.intensitySeekBar != null && Theme.getActiveTheme().isDark()) {
                this.intensitySeekBar.setTwoSided(true);
            }
            Drawable background = this.backgroundImage.getBackground();
            if (background instanceof MotionBackgroundDrawable) {
                motionBackgroundDrawable = (MotionBackgroundDrawable) background;
            } else {
                motionBackgroundDrawable = new MotionBackgroundDrawable();
                motionBackgroundDrawable.setParentView(this.backgroundImage);
                if (this.rotatePreview) {
                    motionBackgroundDrawable.rotatePreview(false);
                }
            }
            motionBackgroundDrawable.setColors(this.backgroundColor, this.backgroundGradientColor1, this.backgroundGradientColor2, this.backgroundGradientColor3);
            this.backgroundImage.setBackground(motionBackgroundDrawable);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
            this.checkColor = 754974720;
        } else if (this.backgroundGradientColor1 != 0) {
            this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1}));
            int patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor1));
            this.checkColor = patternColor;
            this.patternColor = patternColor;
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            int patternColor2 = AndroidUtilities.getPatternColor(this.backgroundColor);
            this.checkColor = patternColor2;
            this.patternColor = patternColor2;
        }
        if (!Theme.hasThemeKey("chat_serviceBackground") || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
            int i4 = this.checkColor;
            Theme.applyChatServiceMessageColor(new int[]{i4, i4, i4, i4}, this.backgroundImage.getBackground());
        } else if (Theme.getCachedWallpaperNonBlocking() instanceof MotionBackgroundDrawable) {
            int color = Theme.getColor("chat_serviceBackground");
            Theme.applyChatServiceMessageColor(new int[]{color, color, color, color}, this.backgroundImage.getBackground());
        }
        ImageView imageView = this.backgroundPlayAnimationImageView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.messagesPlayAnimationImageView;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
        }
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
            this.backgroundImage.invalidate();
            if (!Theme.getActiveTheme().isDark() || !(this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                this.backgroundImage.getImageReceiver().setGradientBitmap(null);
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(false);
                }
            } else {
                SeekBarView seekBarView2 = this.intensitySeekBar;
                if (seekBarView2 != null) {
                    seekBarView2.setTwoSided(true);
                }
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
                }
            }
            SeekBarView seekBarView3 = this.intensitySeekBar;
            if (seekBarView3 != null) {
                seekBarView3.setProgress(this.currentIntensity);
            }
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout = this.backgroundButtonsContainer;
        if (frameLayout != null) {
            int childCount = frameLayout.getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                this.backgroundButtonsContainer.getChildAt(i5).invalidate();
            }
        }
        FrameLayout frameLayout2 = this.messagesButtonsContainer;
        if (frameLayout2 != null) {
            int childCount2 = frameLayout2.getChildCount();
            for (int i6 = 0; i6 < childCount2; i6++) {
                this.messagesButtonsContainer.getChildAt(i6).invalidate();
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        }
    }

    private void setCurrentImage(boolean z) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        MotionBackgroundDrawable motionBackgroundDrawable2;
        int i = this.screenType;
        if (i == 0 && this.accent == null) {
            this.backgroundImage.setBackground(Theme.getCachedWallpaperNonBlocking());
        } else {
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            if (i == 2) {
                Object obj = this.currentWallpaper;
                if (obj instanceof TLRPC$TL_wallPaper) {
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
                    if (z) {
                        tLRPC$PhotoSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_wallPaper.document.thumbs, 100);
                    }
                    this.backgroundImage.setImage(ImageLocation.getForDocument(tLRPC$TL_wallPaper.document), this.imageFilter, ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$TL_wallPaper.document), "100_100_b", "jpg", tLRPC$TL_wallPaper.document.size, 1, tLRPC$TL_wallPaper);
                } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                    WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                    this.backgroundRotation = colorWallpaper.gradientRotation;
                    setBackgroundColor(colorWallpaper.color, 0, true, false);
                    int i2 = colorWallpaper.gradientColor1;
                    if (i2 != 0) {
                        setBackgroundColor(i2, 1, true, false);
                    }
                    setBackgroundColor(colorWallpaper.gradientColor2, 2, true, false);
                    setBackgroundColor(colorWallpaper.gradientColor3, 3, true, false);
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = this.selectedPattern;
                    if (tLRPC$TL_wallPaper2 != null) {
                        BackupImageView backupImageView = this.backgroundImage;
                        ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_wallPaper2.document);
                        String str = this.imageFilter;
                        TLRPC$TL_wallPaper tLRPC$TL_wallPaper3 = this.selectedPattern;
                        backupImageView.setImage(forDocument, str, null, null, "jpg", tLRPC$TL_wallPaper3.document.size, 1, tLRPC$TL_wallPaper3);
                    } else if ("d".equals(colorWallpaper.slug)) {
                        Point point = AndroidUtilities.displaySize;
                        int min = Math.min(point.x, point.y);
                        Point point2 = AndroidUtilities.displaySize;
                        this.backgroundImage.setImageBitmap(SvgHelper.getBitmap((int) C0952R.raw.default_pattern, min, Math.max(point2.x, point2.y), Build.VERSION.SDK_INT >= 29 ? 1459617792 : MotionBackgroundDrawable.getPatternColor(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3)));
                    }
                } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
                    Bitmap bitmap = this.currentWallpaperBitmap;
                    if (bitmap != null) {
                        this.backgroundImage.setImageBitmap(bitmap);
                    } else {
                        WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                        File file = fileWallpaper.originalPath;
                        if (file != null) {
                            this.backgroundImage.setImage(file.getAbsolutePath(), this.imageFilter, null);
                        } else {
                            File file2 = fileWallpaper.path;
                            if (file2 != null) {
                                this.backgroundImage.setImage(file2.getAbsolutePath(), this.imageFilter, null);
                            } else if ("t".equals(fileWallpaper.slug)) {
                                BackupImageView backupImageView2 = this.backgroundImage;
                                backupImageView2.setImageDrawable(Theme.getThemedWallpaper(false, backupImageView2));
                            } else {
                                int i3 = fileWallpaper.resId;
                                if (i3 != 0) {
                                    this.backgroundImage.setImageResource(i3);
                                }
                            }
                        }
                    }
                } else if (obj instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                    TLRPC$Photo tLRPC$Photo = searchImage.photo;
                    if (tLRPC$Photo != null) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, 100);
                        TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(searchImage.photo.sizes, this.maxWallpaperSize, true);
                        if (closestPhotoSizeWithSize2 != closestPhotoSizeWithSize) {
                            tLRPC$PhotoSize = closestPhotoSizeWithSize2;
                        }
                        this.backgroundImage.setImage(ImageLocation.getForPhoto(tLRPC$PhotoSize, searchImage.photo), this.imageFilter, ImageLocation.getForPhoto(closestPhotoSizeWithSize, searchImage.photo), "100_100_b", "jpg", tLRPC$PhotoSize != null ? tLRPC$PhotoSize.size : 0, 1, searchImage);
                    } else {
                        this.backgroundImage.setImage(searchImage.imageUrl, this.imageFilter, searchImage.thumbUrl, "100_100_b");
                    }
                }
            } else {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                int i4 = (int) this.accent.backgroundOverrideColor;
                if (i4 != 0) {
                    defaultAccentColor = i4;
                }
                int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                long j = this.accent.backgroundGradientOverrideColor1;
                int i5 = (int) j;
                if (i5 == 0 && j != 0) {
                    defaultAccentColor2 = 0;
                } else if (i5 != 0) {
                    defaultAccentColor2 = i5;
                }
                int defaultAccentColor3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                long j2 = this.accent.backgroundGradientOverrideColor2;
                int i6 = (int) j2;
                if (i6 == 0 && j2 != 0) {
                    defaultAccentColor3 = 0;
                } else if (i6 != 0) {
                    defaultAccentColor3 = i6;
                }
                int defaultAccentColor4 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                Theme.ThemeAccent themeAccent = this.accent;
                long j3 = themeAccent.backgroundGradientOverrideColor3;
                int i7 = (int) j3;
                if (i7 == 0 && j3 != 0) {
                    defaultAccentColor4 = 0;
                } else if (i7 != 0) {
                    defaultAccentColor4 = i7;
                }
                if (TextUtils.isEmpty(themeAccent.patternSlug) || Theme.hasCustomWallpaper()) {
                    Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
                    if (cachedWallpaperNonBlocking != null) {
                        if (cachedWallpaperNonBlocking instanceof MotionBackgroundDrawable) {
                            ((MotionBackgroundDrawable) cachedWallpaperNonBlocking).setParentView(this.backgroundImage);
                        }
                        this.backgroundImage.setBackground(cachedWallpaperNonBlocking);
                    }
                } else {
                    if (defaultAccentColor3 != 0) {
                        Drawable background = this.backgroundImage.getBackground();
                        if (background instanceof MotionBackgroundDrawable) {
                            motionBackgroundDrawable2 = (MotionBackgroundDrawable) background;
                        } else {
                            MotionBackgroundDrawable motionBackgroundDrawable3 = new MotionBackgroundDrawable();
                            motionBackgroundDrawable3.setParentView(this.backgroundImage);
                            motionBackgroundDrawable2 = motionBackgroundDrawable3;
                            if (this.rotatePreview) {
                                motionBackgroundDrawable3.rotatePreview(false);
                                motionBackgroundDrawable2 = motionBackgroundDrawable3;
                            }
                        }
                        motionBackgroundDrawable2.setColors(defaultAccentColor, defaultAccentColor2, defaultAccentColor3, defaultAccentColor4);
                        motionBackgroundDrawable = motionBackgroundDrawable2;
                    } else if (defaultAccentColor2 != 0) {
                        BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.accent.backgroundRotation), new int[]{defaultAccentColor, defaultAccentColor2});
                        this.backgroundGradientDisposable = backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(), new BackgroundGradientDrawable.ListenerAdapter() {
                            @Override
                            public void onSizeReady(int i8, int i9) {
                                Point point3 = AndroidUtilities.displaySize;
                                boolean z2 = true;
                                boolean z3 = point3.x <= point3.y;
                                if (i8 > i9) {
                                    z2 = false;
                                }
                                if (z3 == z2) {
                                    ThemePreviewActivity.this.backgroundImage.invalidate();
                                }
                            }
                        }, 100L);
                        motionBackgroundDrawable = backgroundGradientDrawable;
                    } else {
                        motionBackgroundDrawable = new ColorDrawable(defaultAccentColor);
                    }
                    this.backgroundImage.setBackground(motionBackgroundDrawable);
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper4 = this.selectedPattern;
                    if (tLRPC$TL_wallPaper4 != null) {
                        BackupImageView backupImageView3 = this.backgroundImage;
                        ImageLocation forDocument2 = ImageLocation.getForDocument(tLRPC$TL_wallPaper4.document);
                        String str2 = this.imageFilter;
                        TLRPC$TL_wallPaper tLRPC$TL_wallPaper5 = this.selectedPattern;
                        backupImageView3.setImage(forDocument2, str2, null, null, "jpg", tLRPC$TL_wallPaper5.document.size, 1, tLRPC$TL_wallPaper5);
                    }
                }
                if (defaultAccentColor2 == 0) {
                    int patternColor = AndroidUtilities.getPatternColor(defaultAccentColor);
                    this.checkColor = patternColor;
                    this.patternColor = patternColor;
                } else if (defaultAccentColor3 != 0) {
                    this.patternColor = MotionBackgroundDrawable.getPatternColor(defaultAccentColor, defaultAccentColor2, defaultAccentColor3, defaultAccentColor4);
                    this.checkColor = 754974720;
                } else {
                    int patternColor2 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(defaultAccentColor, defaultAccentColor2));
                    this.checkColor = patternColor2;
                    this.patternColor = patternColor2;
                }
                BackupImageView backupImageView4 = this.backgroundImage;
                if (backupImageView4 != null) {
                    backupImageView4.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                    this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
                    this.backgroundImage.invalidate();
                    if (!Theme.getActiveTheme().isDark() || !(this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                        this.backgroundImage.getImageReceiver().setGradientBitmap(null);
                        SeekBarView seekBarView = this.intensitySeekBar;
                        if (seekBarView != null) {
                            seekBarView.setTwoSided(false);
                        }
                    } else {
                        SeekBarView seekBarView2 = this.intensitySeekBar;
                        if (seekBarView2 != null) {
                            seekBarView2.setTwoSided(true);
                        }
                        if (this.currentIntensity < 0.0f) {
                            this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
                        }
                    }
                    SeekBarView seekBarView3 = this.intensitySeekBar;
                    if (seekBarView3 != null) {
                        seekBarView3.setProgress(this.currentIntensity);
                    }
                }
                if (this.backgroundCheckBoxView != null) {
                    int i8 = 0;
                    while (true) {
                        WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                        if (i8 >= wallpaperCheckBoxViewArr.length) {
                            break;
                        }
                        wallpaperCheckBoxViewArr[i8].setColor(0, defaultAccentColor);
                        this.backgroundCheckBoxView[i8].setColor(1, defaultAccentColor2);
                        this.backgroundCheckBoxView[i8].setColor(2, defaultAccentColor3);
                        this.backgroundCheckBoxView[i8].setColor(3, defaultAccentColor4);
                        i8++;
                    }
                }
                ImageView imageView = this.backgroundPlayAnimationImageView;
                if (imageView != null) {
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
                }
                ImageView imageView2 = this.messagesPlayAnimationImageView;
                if (imageView2 != null) {
                    imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
                }
                FrameLayout frameLayout = this.backgroundButtonsContainer;
                if (frameLayout != null) {
                    int childCount = frameLayout.getChildCount();
                    for (int i9 = 0; i9 < childCount; i9++) {
                        this.backgroundButtonsContainer.getChildAt(i9).invalidate();
                    }
                }
                FrameLayout frameLayout2 = this.messagesButtonsContainer;
                if (frameLayout2 != null) {
                    int childCount2 = frameLayout2.getChildCount();
                    for (int i10 = 0; i10 < childCount2; i10++) {
                        this.messagesButtonsContainer.getChildAt(i10).invalidate();
                    }
                }
            }
        }
        this.rotatePreview = false;
    }

    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<DialogCell.CustomDialog> dialogs = new ArrayList<>();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
            customDialog.name = LocaleController.getString("ThemePreviewDialog1", C0952R.string.ThemePreviewDialog1);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage1", C0952R.string.ThemePreviewDialogMessage1);
            customDialog.f1009id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            DialogCell.CustomDialog customDialog2 = new DialogCell.CustomDialog();
            customDialog2.name = LocaleController.getString("ThemePreviewDialog2", C0952R.string.ThemePreviewDialog2);
            customDialog2.message = LocaleController.getString("ThemePreviewDialogMessage2", C0952R.string.ThemePreviewDialogMessage2);
            customDialog2.f1009id = 1;
            customDialog2.unread_count = 2;
            customDialog2.pinned = false;
            customDialog2.muted = false;
            customDialog2.type = 0;
            customDialog2.date = currentTimeMillis - 3600;
            customDialog2.verified = false;
            customDialog2.isMedia = false;
            customDialog2.sent = false;
            this.dialogs.add(customDialog2);
            DialogCell.CustomDialog customDialog3 = new DialogCell.CustomDialog();
            customDialog3.name = LocaleController.getString("ThemePreviewDialog3", C0952R.string.ThemePreviewDialog3);
            customDialog3.message = LocaleController.getString("ThemePreviewDialogMessage3", C0952R.string.ThemePreviewDialogMessage3);
            customDialog3.f1009id = 2;
            customDialog3.unread_count = 3;
            customDialog3.pinned = false;
            customDialog3.muted = true;
            customDialog3.type = 0;
            customDialog3.date = currentTimeMillis - 7200;
            customDialog3.verified = false;
            customDialog3.isMedia = true;
            customDialog3.sent = false;
            this.dialogs.add(customDialog3);
            DialogCell.CustomDialog customDialog4 = new DialogCell.CustomDialog();
            customDialog4.name = LocaleController.getString("ThemePreviewDialog4", C0952R.string.ThemePreviewDialog4);
            customDialog4.message = LocaleController.getString("ThemePreviewDialogMessage4", C0952R.string.ThemePreviewDialogMessage4);
            customDialog4.f1009id = 3;
            customDialog4.unread_count = 0;
            customDialog4.pinned = false;
            customDialog4.muted = false;
            customDialog4.type = 2;
            customDialog4.date = currentTimeMillis - 10800;
            customDialog4.verified = false;
            customDialog4.isMedia = false;
            customDialog4.sent = false;
            this.dialogs.add(customDialog4);
            DialogCell.CustomDialog customDialog5 = new DialogCell.CustomDialog();
            customDialog5.name = LocaleController.getString("ThemePreviewDialog5", C0952R.string.ThemePreviewDialog5);
            customDialog5.message = LocaleController.getString("ThemePreviewDialogMessage5", C0952R.string.ThemePreviewDialogMessage5);
            customDialog5.f1009id = 4;
            customDialog5.unread_count = 0;
            customDialog5.pinned = false;
            customDialog5.muted = false;
            customDialog5.type = 1;
            customDialog5.date = currentTimeMillis - 14400;
            customDialog5.verified = false;
            customDialog5.isMedia = false;
            customDialog5.sent = true;
            this.dialogs.add(customDialog5);
            DialogCell.CustomDialog customDialog6 = new DialogCell.CustomDialog();
            customDialog6.name = LocaleController.getString("ThemePreviewDialog6", C0952R.string.ThemePreviewDialog6);
            customDialog6.message = LocaleController.getString("ThemePreviewDialogMessage6", C0952R.string.ThemePreviewDialogMessage6);
            customDialog6.f1009id = 5;
            customDialog6.unread_count = 0;
            customDialog6.pinned = false;
            customDialog6.muted = false;
            customDialog6.type = 0;
            customDialog6.date = currentTimeMillis - 18000;
            customDialog6.verified = false;
            customDialog6.isMedia = false;
            customDialog6.sent = false;
            this.dialogs.add(customDialog6);
            DialogCell.CustomDialog customDialog7 = new DialogCell.CustomDialog();
            customDialog7.name = LocaleController.getString("ThemePreviewDialog7", C0952R.string.ThemePreviewDialog7);
            customDialog7.message = LocaleController.getString("ThemePreviewDialogMessage7", C0952R.string.ThemePreviewDialogMessage7);
            customDialog7.f1009id = 6;
            customDialog7.unread_count = 0;
            customDialog7.pinned = false;
            customDialog7.muted = false;
            customDialog7.type = 0;
            customDialog7.date = currentTimeMillis - 21600;
            customDialog7.verified = true;
            customDialog7.isMedia = false;
            customDialog7.sent = false;
            this.dialogs.add(customDialog7);
            DialogCell.CustomDialog customDialog8 = new DialogCell.CustomDialog();
            customDialog8.name = LocaleController.getString("ThemePreviewDialog8", C0952R.string.ThemePreviewDialog8);
            customDialog8.message = LocaleController.getString("ThemePreviewDialogMessage8", C0952R.string.ThemePreviewDialogMessage8);
            customDialog8.f1009id = 0;
            customDialog8.unread_count = 0;
            customDialog8.pinned = false;
            customDialog8.muted = false;
            customDialog8.type = 0;
            customDialog8.date = currentTimeMillis - 25200;
            customDialog8.verified = true;
            customDialog8.isMedia = false;
            customDialog8.sent = false;
            this.dialogs.add(customDialog8);
        }

        @Override
        public int getItemCount() {
            return this.dialogs.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new DialogCell(null, this.mContext, false, false);
            } else {
                view = new LoadingCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                dialogCell.useSeparator = z;
                dialogCell.setDialog(this.dialogs.get(i));
            }
        }

        @Override
        public int getItemViewType(int i) {
            return i == this.dialogs.size() ? 1 : 0;
        }
    }

    public class MessagesAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages;
        private boolean showSecretMessages;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public MessagesAdapter(Context context) {
            this.showSecretMessages = ThemePreviewActivity.this.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList<>();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (ThemePreviewActivity.this.screenType == 2) {
                TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tLRPC$TL_message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", C0952R.string.BackgroundColorSinglePreviewLine2);
                } else {
                    tLRPC$TL_message.message = LocaleController.getString("BackgroundPreviewLine2", C0952R.string.BackgroundPreviewLine2);
                }
                int i = currentTimeMillis + 60;
                tLRPC$TL_message.date = i;
                tLRPC$TL_message.dialog_id = 1L;
                tLRPC$TL_message.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tLRPC$TL_message.f877id = 1;
                tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$TL_message.peer_id = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = 0L;
                MessageObject messageObject = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message, true, false);
                messageObject.eventId = 1L;
                messageObject.resetLayout();
                this.messages.add(messageObject);
                TLRPC$TL_message tLRPC$TL_message2 = new TLRPC$TL_message();
                if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tLRPC$TL_message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", C0952R.string.BackgroundColorSinglePreviewLine1);
                } else {
                    tLRPC$TL_message2.message = LocaleController.getString("BackgroundPreviewLine1", C0952R.string.BackgroundPreviewLine1);
                }
                tLRPC$TL_message2.date = i;
                tLRPC$TL_message2.dialog_id = 1L;
                tLRPC$TL_message2.flags = 265;
                tLRPC$TL_message2.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message2.f877id = 1;
                tLRPC$TL_message2.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message2.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
                tLRPC$TL_message2.peer_id = tLRPC$TL_peerUser3;
                tLRPC$TL_peerUser3.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                MessageObject messageObject2 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message2, true, false);
                messageObject2.eventId = 1L;
                messageObject2.resetLayout();
                this.messages.add(messageObject2);
            } else if (ThemePreviewActivity.this.screenType == 1) {
                TLRPC$TL_message tLRPC$TL_message3 = new TLRPC$TL_message();
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message3.media = tLRPC$TL_messageMediaDocument;
                tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document = tLRPC$TL_message3.media.document;
                tLRPC$Document.mime_type = "audio/mp3";
                tLRPC$Document.file_reference = new byte[0];
                tLRPC$Document.f861id = -2147483648L;
                tLRPC$Document.size = MediaController.VIDEO_BITRATE_720;
                tLRPC$Document.dc_id = Integer.MIN_VALUE;
                TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
                tLRPC$TL_documentAttributeFilename.file_name = LocaleController.getString("NewThemePreviewReply2", C0952R.string.NewThemePreviewReply2) + ".mp3";
                tLRPC$TL_message3.media.document.attributes.add(tLRPC$TL_documentAttributeFilename);
                int i2 = currentTimeMillis + 60;
                tLRPC$TL_message3.date = i2;
                tLRPC$TL_message3.dialog_id = 1L;
                tLRPC$TL_message3.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser4 = new TLRPC$TL_peerUser();
                tLRPC$TL_message3.from_id = tLRPC$TL_peerUser4;
                tLRPC$TL_peerUser4.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tLRPC$TL_message3.f877id = 1;
                tLRPC$TL_message3.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser5 = new TLRPC$TL_peerUser();
                tLRPC$TL_message3.peer_id = tLRPC$TL_peerUser5;
                tLRPC$TL_peerUser5.user_id = 0L;
                MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message3, true, false);
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    TLRPC$TL_message tLRPC$TL_message4 = new TLRPC$TL_message();
                    tLRPC$TL_message4.message = "this is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text";
                    tLRPC$TL_message4.date = currentTimeMillis + 960;
                    tLRPC$TL_message4.dialog_id = 1L;
                    tLRPC$TL_message4.flags = 259;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser6 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message4.from_id = tLRPC$TL_peerUser6;
                    tLRPC$TL_peerUser6.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    tLRPC$TL_message4.f877id = 1;
                    tLRPC$TL_message4.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message4.out = true;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser7 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message4.peer_id = tLRPC$TL_peerUser7;
                    tLRPC$TL_peerUser7.user_id = 0L;
                    MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message4, true, false);
                    messageObject4.resetLayout();
                    messageObject4.eventId = 1L;
                    this.messages.add(messageObject4);
                }
                TLRPC$TL_message tLRPC$TL_message5 = new TLRPC$TL_message();
                String string = LocaleController.getString("NewThemePreviewLine3", C0952R.string.NewThemePreviewLine3);
                StringBuilder sb = new StringBuilder(string);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                if (!(indexOf == -1 || lastIndexOf == -1)) {
                    sb.replace(lastIndexOf, lastIndexOf + 1, "");
                    sb.replace(indexOf, indexOf + 1, "");
                    TLRPC$TL_messageEntityTextUrl tLRPC$TL_messageEntityTextUrl = new TLRPC$TL_messageEntityTextUrl();
                    tLRPC$TL_messageEntityTextUrl.offset = indexOf;
                    tLRPC$TL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                    tLRPC$TL_messageEntityTextUrl.url = "https://telegram.org";
                    tLRPC$TL_message5.entities.add(tLRPC$TL_messageEntityTextUrl);
                }
                tLRPC$TL_message5.message = sb.toString();
                tLRPC$TL_message5.date = currentTimeMillis + 960;
                tLRPC$TL_message5.dialog_id = 1L;
                tLRPC$TL_message5.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser8 = new TLRPC$TL_peerUser();
                tLRPC$TL_message5.from_id = tLRPC$TL_peerUser8;
                tLRPC$TL_peerUser8.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tLRPC$TL_message5.f877id = 1;
                tLRPC$TL_message5.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message5.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser9 = new TLRPC$TL_peerUser();
                tLRPC$TL_message5.peer_id = tLRPC$TL_peerUser9;
                tLRPC$TL_peerUser9.user_id = 0L;
                MessageObject messageObject5 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message5, true, false);
                messageObject5.resetLayout();
                messageObject5.eventId = 1L;
                this.messages.add(messageObject5);
                TLRPC$TL_message tLRPC$TL_message6 = new TLRPC$TL_message();
                tLRPC$TL_message6.message = LocaleController.getString("NewThemePreviewLine1", C0952R.string.NewThemePreviewLine1);
                tLRPC$TL_message6.date = i2;
                tLRPC$TL_message6.dialog_id = 1L;
                tLRPC$TL_message6.flags = 265;
                tLRPC$TL_message6.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message6.f877id = 1;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
                tLRPC$TL_message6.reply_to = tLRPC$TL_messageReplyHeader;
                tLRPC$TL_messageReplyHeader.reply_to_msg_id = 5;
                tLRPC$TL_message6.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message6.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser10 = new TLRPC$TL_peerUser();
                tLRPC$TL_message6.peer_id = tLRPC$TL_peerUser10;
                tLRPC$TL_peerUser10.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                MessageObject messageObject6 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message6, true, false);
                messageObject6.customReplyName = LocaleController.getString("NewThemePreviewName", C0952R.string.NewThemePreviewName);
                messageObject5.customReplyName = "Test User";
                messageObject6.eventId = 1L;
                messageObject6.resetLayout();
                messageObject6.replyMessageObject = messageObject3;
                messageObject5.replyMessageObject = messageObject6;
                this.messages.add(messageObject6);
                this.messages.add(messageObject3);
                TLRPC$TL_message tLRPC$TL_message7 = new TLRPC$TL_message();
                tLRPC$TL_message7.date = currentTimeMillis + 120;
                tLRPC$TL_message7.dialog_id = 1L;
                tLRPC$TL_message7.flags = 259;
                tLRPC$TL_message7.out = false;
                tLRPC$TL_message7.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message7.f877id = 1;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument2 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message7.media = tLRPC$TL_messageMediaDocument2;
                tLRPC$TL_messageMediaDocument2.flags |= 3;
                tLRPC$TL_messageMediaDocument2.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_message7.media.document;
                tLRPC$Document2.mime_type = "audio/ogg";
                tLRPC$Document2.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio.flags = 1028;
                tLRPC$TL_documentAttributeAudio.duration = 3;
                tLRPC$TL_documentAttributeAudio.voice = true;
                tLRPC$TL_documentAttributeAudio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tLRPC$TL_message7.media.document.attributes.add(tLRPC$TL_documentAttributeAudio);
                tLRPC$TL_message7.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser11 = new TLRPC$TL_peerUser();
                tLRPC$TL_message7.peer_id = tLRPC$TL_peerUser11;
                tLRPC$TL_peerUser11.user_id = 0L;
                MessageObject messageObject7 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message7, true, false);
                messageObject7.audioProgressSec = 1;
                messageObject7.audioProgress = 0.3f;
                messageObject7.useCustomPhoto = true;
                this.messages.add(messageObject7);
            } else if (this.showSecretMessages) {
                TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
                tLRPC$TL_user.f985id = 2147483647L;
                tLRPC$TL_user.first_name = "Me";
                TLRPC$TL_user tLRPC$TL_user2 = new TLRPC$TL_user();
                tLRPC$TL_user2.f985id = 2147483646L;
                tLRPC$TL_user2.first_name = "Serj";
                ArrayList<TLRPC$User> arrayList = new ArrayList<>();
                arrayList.add(tLRPC$TL_user);
                arrayList.add(tLRPC$TL_user2);
                MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).putUsers(arrayList, true);
                TLRPC$TL_message tLRPC$TL_message8 = new TLRPC$TL_message();
                tLRPC$TL_message8.message = "Guess why Half-Life 3 was never released.";
                int i3 = currentTimeMillis + 960;
                tLRPC$TL_message8.date = i3;
                tLRPC$TL_message8.dialog_id = -1L;
                tLRPC$TL_message8.flags = 259;
                tLRPC$TL_message8.f877id = 2147483646;
                tLRPC$TL_message8.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message8.out = false;
                TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                tLRPC$TL_message8.peer_id = tLRPC$TL_peerChat;
                tLRPC$TL_peerChat.chat_id = 1L;
                TLRPC$TL_peerUser tLRPC$TL_peerUser12 = new TLRPC$TL_peerUser();
                tLRPC$TL_message8.from_id = tLRPC$TL_peerUser12;
                tLRPC$TL_peerUser12.user_id = tLRPC$TL_user2.f985id;
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message8, true, false));
                TLRPC$TL_message tLRPC$TL_message9 = new TLRPC$TL_message();
                tLRPC$TL_message9.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                tLRPC$TL_message9.date = i3;
                tLRPC$TL_message9.dialog_id = -1L;
                tLRPC$TL_message9.flags = 259;
                tLRPC$TL_message9.f877id = 1;
                tLRPC$TL_message9.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message9.out = false;
                TLRPC$TL_peerChat tLRPC$TL_peerChat2 = new TLRPC$TL_peerChat();
                tLRPC$TL_message9.peer_id = tLRPC$TL_peerChat2;
                tLRPC$TL_peerChat2.chat_id = 1L;
                TLRPC$TL_peerUser tLRPC$TL_peerUser13 = new TLRPC$TL_peerUser();
                tLRPC$TL_message9.from_id = tLRPC$TL_peerUser13;
                tLRPC$TL_peerUser13.user_id = tLRPC$TL_user2.f985id;
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message9, true, false));
                TLRPC$TL_message tLRPC$TL_message10 = new TLRPC$TL_message();
                tLRPC$TL_message10.message = "Is source code for Android coming anytime soon?";
                tLRPC$TL_message10.date = currentTimeMillis + 600;
                tLRPC$TL_message10.dialog_id = -1L;
                tLRPC$TL_message10.flags = 259;
                tLRPC$TL_message10.f877id = 1;
                tLRPC$TL_message10.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message10.out = false;
                TLRPC$TL_peerChat tLRPC$TL_peerChat3 = new TLRPC$TL_peerChat();
                tLRPC$TL_message10.peer_id = tLRPC$TL_peerChat3;
                tLRPC$TL_peerChat3.chat_id = 1L;
                TLRPC$TL_peerUser tLRPC$TL_peerUser14 = new TLRPC$TL_peerUser();
                tLRPC$TL_message10.from_id = tLRPC$TL_peerUser14;
                tLRPC$TL_peerUser14.user_id = tLRPC$TL_user.f985id;
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message10, true, false));
            } else {
                TLRPC$TL_message tLRPC$TL_message11 = new TLRPC$TL_message();
                tLRPC$TL_message11.message = LocaleController.getString("ThemePreviewLine1", C0952R.string.ThemePreviewLine1);
                int i4 = currentTimeMillis + 60;
                tLRPC$TL_message11.date = i4;
                tLRPC$TL_message11.dialog_id = 1L;
                tLRPC$TL_message11.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser15 = new TLRPC$TL_peerUser();
                tLRPC$TL_message11.from_id = tLRPC$TL_peerUser15;
                tLRPC$TL_peerUser15.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tLRPC$TL_message11.f877id = 1;
                tLRPC$TL_message11.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message11.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser16 = new TLRPC$TL_peerUser();
                tLRPC$TL_message11.peer_id = tLRPC$TL_peerUser16;
                tLRPC$TL_peerUser16.user_id = 0L;
                MessageObject messageObject8 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message11, true, false);
                TLRPC$TL_message tLRPC$TL_message12 = new TLRPC$TL_message();
                tLRPC$TL_message12.message = LocaleController.getString("ThemePreviewLine2", C0952R.string.ThemePreviewLine2);
                tLRPC$TL_message12.date = currentTimeMillis + 960;
                tLRPC$TL_message12.dialog_id = 1L;
                tLRPC$TL_message12.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser17 = new TLRPC$TL_peerUser();
                tLRPC$TL_message12.from_id = tLRPC$TL_peerUser17;
                tLRPC$TL_peerUser17.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tLRPC$TL_message12.f877id = 1;
                tLRPC$TL_message12.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message12.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser18 = new TLRPC$TL_peerUser();
                tLRPC$TL_message12.peer_id = tLRPC$TL_peerUser18;
                tLRPC$TL_peerUser18.user_id = 0L;
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message12, true, false));
                TLRPC$TL_message tLRPC$TL_message13 = new TLRPC$TL_message();
                tLRPC$TL_message13.date = currentTimeMillis + 130;
                tLRPC$TL_message13.dialog_id = 1L;
                tLRPC$TL_message13.flags = 259;
                tLRPC$TL_message13.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message13.f877id = 5;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument3 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message13.media = tLRPC$TL_messageMediaDocument3;
                tLRPC$TL_messageMediaDocument3.flags |= 3;
                tLRPC$TL_messageMediaDocument3.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document3 = tLRPC$TL_message13.media.document;
                tLRPC$Document3.mime_type = "audio/mp4";
                tLRPC$Document3.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio2 = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio2.duration = 243;
                tLRPC$TL_documentAttributeAudio2.performer = LocaleController.getString("ThemePreviewSongPerformer", C0952R.string.ThemePreviewSongPerformer);
                tLRPC$TL_documentAttributeAudio2.title = LocaleController.getString("ThemePreviewSongTitle", C0952R.string.ThemePreviewSongTitle);
                tLRPC$TL_message13.media.document.attributes.add(tLRPC$TL_documentAttributeAudio2);
                tLRPC$TL_message13.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser19 = new TLRPC$TL_peerUser();
                tLRPC$TL_message13.peer_id = tLRPC$TL_peerUser19;
                tLRPC$TL_peerUser19.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message13, true, false));
                TLRPC$TL_message tLRPC$TL_message14 = new TLRPC$TL_message();
                tLRPC$TL_message14.message = LocaleController.getString("ThemePreviewLine3", C0952R.string.ThemePreviewLine3);
                tLRPC$TL_message14.date = i4;
                tLRPC$TL_message14.dialog_id = 1L;
                tLRPC$TL_message14.flags = 265;
                tLRPC$TL_message14.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message14.f877id = 1;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = new TLRPC$TL_messageReplyHeader();
                tLRPC$TL_message14.reply_to = tLRPC$TL_messageReplyHeader2;
                tLRPC$TL_messageReplyHeader2.reply_to_msg_id = 5;
                tLRPC$TL_message14.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message14.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser20 = new TLRPC$TL_peerUser();
                tLRPC$TL_message14.peer_id = tLRPC$TL_peerUser20;
                tLRPC$TL_peerUser20.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                MessageObject messageObject9 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message14, true, false);
                messageObject9.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", C0952R.string.ThemePreviewLine3Reply);
                messageObject9.replyMessageObject = messageObject8;
                this.messages.add(messageObject9);
                TLRPC$TL_message tLRPC$TL_message15 = new TLRPC$TL_message();
                tLRPC$TL_message15.date = currentTimeMillis + 120;
                tLRPC$TL_message15.dialog_id = 1L;
                tLRPC$TL_message15.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser21 = new TLRPC$TL_peerUser();
                tLRPC$TL_message15.from_id = tLRPC$TL_peerUser21;
                tLRPC$TL_peerUser21.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tLRPC$TL_message15.f877id = 1;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument4 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message15.media = tLRPC$TL_messageMediaDocument4;
                tLRPC$TL_messageMediaDocument4.flags |= 3;
                tLRPC$TL_messageMediaDocument4.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document4 = tLRPC$TL_message15.media.document;
                tLRPC$Document4.mime_type = "audio/ogg";
                tLRPC$Document4.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio3 = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio3.flags = 1028;
                tLRPC$TL_documentAttributeAudio3.duration = 3;
                tLRPC$TL_documentAttributeAudio3.voice = true;
                tLRPC$TL_documentAttributeAudio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tLRPC$TL_message15.media.document.attributes.add(tLRPC$TL_documentAttributeAudio3);
                tLRPC$TL_message15.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser22 = new TLRPC$TL_peerUser();
                tLRPC$TL_message15.peer_id = tLRPC$TL_peerUser22;
                tLRPC$TL_peerUser22.user_id = 0L;
                MessageObject messageObject10 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message15, true, false);
                messageObject10.audioProgressSec = 1;
                messageObject10.audioProgress = 0.3f;
                messageObject10.useCustomPhoto = true;
                this.messages.add(messageObject10);
                this.messages.add(messageObject8);
                TLRPC$TL_message tLRPC$TL_message16 = new TLRPC$TL_message();
                tLRPC$TL_message16.date = currentTimeMillis + 10;
                tLRPC$TL_message16.dialog_id = 1L;
                tLRPC$TL_message16.flags = 257;
                tLRPC$TL_message16.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message16.f877id = 1;
                TLRPC$TL_messageMediaPhoto tLRPC$TL_messageMediaPhoto = new TLRPC$TL_messageMediaPhoto();
                tLRPC$TL_message16.media = tLRPC$TL_messageMediaPhoto;
                tLRPC$TL_messageMediaPhoto.flags |= 3;
                tLRPC$TL_messageMediaPhoto.photo = new TLRPC$TL_photo();
                TLRPC$Photo tLRPC$Photo = tLRPC$TL_message16.media.photo;
                tLRPC$Photo.file_reference = new byte[0];
                tLRPC$Photo.has_stickers = false;
                tLRPC$Photo.f882id = 1L;
                tLRPC$Photo.access_hash = 0L;
                tLRPC$Photo.date = currentTimeMillis;
                TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
                tLRPC$TL_photoSize.size = 0;
                tLRPC$TL_photoSize.f884w = 500;
                tLRPC$TL_photoSize.f883h = 302;
                tLRPC$TL_photoSize.type = "s";
                tLRPC$TL_photoSize.location = new TLRPC$TL_fileLocationUnavailable();
                tLRPC$TL_message16.media.photo.sizes.add(tLRPC$TL_photoSize);
                tLRPC$TL_message16.message = LocaleController.getString("ThemePreviewLine4", C0952R.string.ThemePreviewLine4);
                tLRPC$TL_message16.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser23 = new TLRPC$TL_peerUser();
                tLRPC$TL_message16.peer_id = tLRPC$TL_peerUser23;
                tLRPC$TL_peerUser23.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                MessageObject messageObject11 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tLRPC$TL_message16, true, false);
                messageObject11.useCustomPhoto = true;
                this.messages.add(messageObject11);
            }
        }

        private boolean hasButtons() {
            if (ThemePreviewActivity.this.messagesButtonsContainer != null && ThemePreviewActivity.this.screenType == 1 && ThemePreviewActivity.this.colorType == 3 && ThemePreviewActivity.this.accent.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            if (ThemePreviewActivity.this.backgroundButtonsContainer != null) {
                if (ThemePreviewActivity.this.screenType == 2) {
                    return true;
                }
                if (ThemePreviewActivity.this.screenType == 1 && ThemePreviewActivity.this.colorType == 2) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getItemCount() {
            int size = this.messages.size();
            return hasButtons() ? size + 1 : size;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 0) {
                ChatMessageCell chatMessageCell = new ChatMessageCell(this.mContext, false, new Theme.ResourcesProvider() {
                    @Override
                    public void applyServiceShaderMatrix(int i2, int i3, float f, float f2) {
                        Theme.applyServiceShaderMatrix(i2, i3, f, f2);
                    }

                    @Override
                    public int getColorOrDefault(String str) {
                        return getColor(str);
                    }

                    @Override
                    public Integer getCurrentColor(String str) {
                        Integer color;
                        color = getColor(str);
                        return color;
                    }

                    @Override
                    public Paint getPaint(String str) {
                        return Theme.ResourcesProvider.CC.$default$getPaint(this, str);
                    }

                    @Override
                    public boolean hasGradientService() {
                        return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
                    }

                    @Override
                    public void setAnimatedColor(String str, int i2) {
                        Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, str, i2);
                    }

                    @Override
                    public Integer getColor(String str) {
                        return Integer.valueOf(Theme.getColor(str));
                    }

                    @Override
                    public Drawable getDrawable(String str) {
                        if (str.equals("drawableMsgOut")) {
                            return ThemePreviewActivity.this.msgOutDrawable;
                        }
                        if (str.equals("drawableMsgOutSelected")) {
                            return ThemePreviewActivity.this.msgOutDrawableSelected;
                        }
                        if (str.equals("drawableMsgOutMedia")) {
                            return ThemePreviewActivity.this.msgOutMediaDrawable;
                        }
                        if (str.equals("drawableMsgOutMediaSelected")) {
                            return ThemePreviewActivity.this.msgOutMediaDrawableSelected;
                        }
                        return Theme.getThemeDrawable(str);
                    }
                });
                chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate(this) {
                    @Override
                    public boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    @Override
                    public boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    @Override
                    public void didLongPress(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC$Chat tLRPC$Chat, int i2, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, tLRPC$Chat, i2, f, f2);
                    }

                    @Override
                    public boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC$User tLRPC$User, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, tLRPC$User, f, f2);
                    }

                    @Override
                    public void didPressBotButton(ChatMessageCell chatMessageCell2, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell2, tLRPC$KeyboardButton);
                    }

                    @Override
                    public void didPressCancelSendButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC$Chat tLRPC$Chat, int i2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, tLRPC$Chat, i2, f, f2);
                    }

                    @Override
                    public void didPressCommentButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressHiddenForward(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressHint(ChatMessageCell chatMessageCell2, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i2);
                    }

                    @Override
                    public void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didPressInstantButton(ChatMessageCell chatMessageCell2, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i2);
                    }

                    @Override
                    public void didPressOther(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didPressReaction(ChatMessageCell chatMessageCell2, TLRPC$TL_reactionCount tLRPC$TL_reactionCount, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell2, tLRPC$TL_reactionCount, z);
                    }

                    @Override
                    public void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i2);
                    }

                    @Override
                    public void didPressSideButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressTime(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressUrl(ChatMessageCell chatMessageCell2, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell2, characterStyle, z);
                    }

                    @Override
                    public void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC$User tLRPC$User, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, tLRPC$User, f, f2);
                    }

                    @Override
                    public void didPressViaBot(ChatMessageCell chatMessageCell2, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell2, str);
                    }

                    @Override
                    public void didPressViaBotNotInline(ChatMessageCell chatMessageCell2, long j) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell2, j);
                    }

                    @Override
                    public void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i2, int i3, int i4) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i2, i3, i4);
                    }

                    @Override
                    public void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    @Override
                    public String getAdminRank(long j) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                    }

                    @Override
                    public PinchToZoomHelper getPinchToZoomHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                    }

                    @Override
                    public TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    @Override
                    public boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    @Override
                    public void invalidateBlur() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                    }

                    @Override
                    public boolean isLandscape() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                    }

                    @Override
                    public boolean keyboardIsOpened() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                    }

                    @Override
                    public void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i2, i3);
                    }

                    @Override
                    public boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                    }

                    @Override
                    public void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    @Override
                    public void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    @Override
                    public void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    @Override
                    public boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2);
                    }

                    @Override
                    public boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    @Override
                    public void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
                frameLayout = chatMessageCell;
            } else if (i == 1) {
                ChatActionCell chatActionCell = new ChatActionCell(this.mContext);
                chatActionCell.setDelegate(new ChatActionCell.ChatActionCellDelegate(this) {
                    @Override
                    public void didClickImage(ChatActionCell chatActionCell2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didClickImage(this, chatActionCell2);
                    }

                    @Override
                    public boolean didLongPress(ChatActionCell chatActionCell2, float f, float f2) {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$didLongPress(this, chatActionCell2, f, f2);
                    }

                    @Override
                    public void didPressReplyMessage(ChatActionCell chatActionCell2, int i2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReplyMessage(this, chatActionCell2, i2);
                    }

                    @Override
                    public void needOpenInviteLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenInviteLink(this, tLRPC$TL_chatInviteExported);
                    }

                    @Override
                    public void needOpenUserProfile(long j) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenUserProfile(this, j);
                    }
                });
                frameLayout = chatActionCell;
            } else if (i == 2) {
                if (ThemePreviewActivity.this.backgroundButtonsContainer.getParent() != null) {
                    ((ViewGroup) ThemePreviewActivity.this.backgroundButtonsContainer.getParent()).removeView(ThemePreviewActivity.this.backgroundButtonsContainer);
                }
                FrameLayout frameLayout2 = new FrameLayout(this, this.mContext) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m34dp(60.0f), 1073741824));
                    }
                };
                frameLayout2.addView(ThemePreviewActivity.this.backgroundButtonsContainer, LayoutHelper.createFrame(-1, 76, 17));
                frameLayout = frameLayout2;
            } else {
                if (ThemePreviewActivity.this.messagesButtonsContainer.getParent() != null) {
                    ((ViewGroup) ThemePreviewActivity.this.messagesButtonsContainer.getParent()).removeView(ThemePreviewActivity.this.messagesButtonsContainer);
                }
                FrameLayout frameLayout3 = new FrameLayout(this, this.mContext) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m34dp(60.0f), 1073741824));
                    }
                };
                frameLayout3.addView(ThemePreviewActivity.this.messagesButtonsContainer, LayoutHelper.createFrame(-1, 76, 17));
                frameLayout = frameLayout3;
            }
            frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(frameLayout);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            boolean z;
            MessageObject messageObject;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 2 && itemViewType != 3) {
                if (hasButtons()) {
                    i--;
                }
                MessageObject messageObject2 = this.messages.get(i);
                View view = viewHolder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    boolean z2 = false;
                    chatMessageCell.isChat = false;
                    int i2 = i - 1;
                    int itemViewType2 = getItemViewType(i2);
                    int i3 = i + 1;
                    int itemViewType3 = getItemViewType(i3);
                    if (!(messageObject2.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup) && itemViewType2 == viewHolder.getItemViewType()) {
                        MessageObject messageObject3 = this.messages.get(i2);
                        if (messageObject3.isOutOwner() == messageObject2.isOutOwner() && Math.abs(messageObject3.messageOwner.date - messageObject2.messageOwner.date) <= 300) {
                            z = true;
                            if (itemViewType3 == viewHolder.getItemViewType() && i3 < this.messages.size()) {
                                messageObject = this.messages.get(i3);
                                if (!(messageObject.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup) && messageObject.isOutOwner() == messageObject2.isOutOwner() && Math.abs(messageObject.messageOwner.date - messageObject2.messageOwner.date) <= 300) {
                                    z2 = true;
                                }
                            }
                            chatMessageCell.isChat = this.showSecretMessages;
                            chatMessageCell.setFullyDraw(true);
                            chatMessageCell.setMessageObject(messageObject2, null, z, z2);
                        }
                    }
                    z = false;
                    if (itemViewType3 == viewHolder.getItemViewType()) {
                        messageObject = this.messages.get(i3);
                        if (!(messageObject.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup)) {
                            z2 = true;
                        }
                    }
                    chatMessageCell.isChat = this.showSecretMessages;
                    chatMessageCell.setFullyDraw(true);
                    chatMessageCell.setMessageObject(messageObject2, null, z, z2);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) view;
                    chatActionCell.setMessageObject(messageObject2);
                    chatActionCell.setAlpha(1.0f);
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (hasButtons()) {
                if (i == 0) {
                    return ThemePreviewActivity.this.colorType == 3 ? 3 : 2;
                }
                i--;
            }
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return this.messages.get(i).contentType;
        }
    }

    public class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (ThemePreviewActivity.this.patterns != null) {
                return ThemePreviewActivity.this.patterns.size();
            }
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCell.PatternCellDelegate() {
                @Override
                public TLRPC$TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                @Override
                public int getCheckColor() {
                    return ThemePreviewActivity.this.checkColor;
                }

                @Override
                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientColor1() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor1;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientColor2() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor2;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientColor3() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor3;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientAngle() {
                    return ThemePreviewActivity.this.screenType == 2 ? ThemePreviewActivity.this.backgroundRotation : ThemePreviewActivity.this.accent.backgroundRotation;
                }

                @Override
                public float getIntensity() {
                    return ThemePreviewActivity.this.currentIntensity;
                }

                @Override
                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }
            }));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            patternCell.setPattern((TLRPC$TL_wallPaper) ThemePreviewActivity.this.patterns.get(i));
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
            if (Build.VERSION.SDK_INT >= 29) {
                int i2 = 0;
                if (ThemePreviewActivity.this.screenType == 1) {
                    int defaultAccentColor = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int i3 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    if (i3 != 0 || ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2 == 0) {
                        if (i3 != 0) {
                            defaultAccentColor = i3;
                        }
                        i2 = defaultAccentColor;
                    }
                } else if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    i2 = ThemePreviewActivity.this.backgroundGradientColor2;
                }
                if (i2 == 0 || ThemePreviewActivity.this.currentIntensity < 0.0f) {
                    patternCell.getImageReceiver().setBlendMode(null);
                } else {
                    ThemePreviewActivity.this.backgroundImage.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                }
            }
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        ThemeDescription.ThemeDescriptionDelegate themePreviewActivity$$ExternalSyntheticLambda22 = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ThemePreviewActivity.this.lambda$getThemeDescriptionsInternal$26();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.page1, ThemeDescription.FLAG_BACKGROUND, null, null, null, themePreviewActivity$$ExternalSyntheticLambda22, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, themePreviewActivity$$ExternalSyntheticLambda22, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, themePreviewActivity$$ExternalSyntheticLambda22, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
        if (!this.useDefaultThemeForButtons) {
            arrayList.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            arrayList.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.provideThemeDescriptions(arrayList);
        }
        if (this.patternLayout != null) {
            for (int i = 0; i < this.patternLayout.length; i++) {
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
            }
            for (int i2 = 0; i2 < this.patternsButtonsContainer.length; i2++) {
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
            }
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            for (int i3 = 0; i3 < this.patternsSaveButton.length; i3++) {
                arrayList.add(new ThemeDescription(this.patternsSaveButton[i3], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            }
            for (int i4 = 0; i4 < this.patternsCancelButton.length; i4++) {
                arrayList.add(new ThemeDescription(this.patternsCancelButton[i4], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            }
            arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
            arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
            arrayList.add(new ThemeDescription(this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, "chat_outBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, "chat_outBubbleGradient"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, "chat_outBubbleGradient2"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, "chat_outBubbleGradient3"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText"));
        }
        return arrayList;
    }

    public void lambda$getThemeDescriptionsInternal$26() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        }
        Drawable drawable = this.sheetDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        }
    }
}
