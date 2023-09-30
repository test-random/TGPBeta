package org.telegram.ui.Stories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.OvershootInterpolator;
import androidx.annotation.Keep;
import com.google.zxing.common.detector.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$PeerStories;
import org.telegram.tgnet.TLRPC$StoryItem;
import org.telegram.tgnet.TLRPC$TL_storyItemDeleted;
import org.telegram.tgnet.TLRPC$TL_storyItemSkipped;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.ProfileStoriesView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoryViewer;
public class ProfileStoriesView extends View implements NotificationCenter.NotificationCenterDelegate {
    private float actionBarProgress;
    private boolean attached;
    private final View avatarContainer;
    private final ProfileActivity.AvatarImageView avatarImage;
    private float bounceScale;
    private final ArrayList<StoryCircle> circles;
    private final Paint clipOutAvatar;
    private final Path clipPath;
    private int count;
    private final int currentAccount;
    private float cy;
    private final long dialogId;
    private float expandProgress;
    private float expandRight;
    private boolean expandRightPad;
    private final AnimatedFloat expandRightPadAnimated;
    private float expandY;
    private float fragmentTransitionProgress;
    private StoriesController.UploadingStory lastUploadingStory;
    private float left;
    private StoryCircle mainCircle;
    private ValueAnimator newStoryBounce;
    private float newStoryBounceT;
    private Runnable onLongPressRunnable;
    Paint paint;
    private boolean progressIsDone;
    private float progressToInsets;
    private final AnimatedFloat progressToUploading;
    private boolean progressWasDrawn;
    private final StoryViewer.PlaceProvider provider;
    private RadialProgress radialProgress;
    private final Paint readPaint;
    private int readPaintAlpha;
    private final RectF rect1;
    private final RectF rect2;
    private final RectF rect3;
    private float right;
    private final AnimatedFloat rightAnimated;
    private final AnimatedFloat segmentsCountAnimated;
    private final AnimatedFloat segmentsUnreadCountAnimated;
    StoriesController storiesController;
    private long tapTime;
    private float tapX;
    private float tapY;
    private final AnimatedTextView.AnimatedTextDrawable titleDrawable;
    private int unreadCount;
    private int uploadingStoriesCount;
    float w;
    private final Paint whitePaint;

    public void lambda$new$4() {
    }

    protected void onTap(StoryViewer.PlaceProvider placeProvider) {
    }

    public void setProgressToStoriesInsets(float f) {
        if (this.progressToInsets == f) {
            return;
        }
        this.progressToInsets = f;
        invalidate();
    }

    public class StoryCircle {
        final RectF borderRect;
        float cachedIndex;
        float cachedRead;
        final RectF cachedRect;
        float cachedScale;
        final AnimatedFloat indexAnimated;
        final AnimatedFloat readAnimated;
        final AnimatedFloat scaleAnimated;
        int storyId;
        ImageReceiver imageReceiver = new ImageReceiver();
        int index = 0;
        boolean read = false;
        float scale = 1.0f;

        public StoryCircle(TLRPC$StoryItem tLRPC$StoryItem) {
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.readAnimated = new AnimatedFloat(ProfileStoriesView.this, 420L, cubicBezierInterpolator);
            this.indexAnimated = new AnimatedFloat(ProfileStoriesView.this, 420L, cubicBezierInterpolator);
            this.scaleAnimated = new AnimatedFloat(ProfileStoriesView.this, 420L, cubicBezierInterpolator);
            this.cachedRect = new RectF();
            this.borderRect = new RectF();
            this.storyId = tLRPC$StoryItem.id;
            this.imageReceiver.setRoundRadius(AndroidUtilities.dp(200.0f));
            this.imageReceiver.setParentView(ProfileStoriesView.this);
            if (ProfileStoriesView.this.attached) {
                this.imageReceiver.onAttachedToWindow();
            }
            StoriesUtilities.setThumbImage(this.imageReceiver, tLRPC$StoryItem, 25, 25);
        }

        public void destroy() {
            this.imageReceiver.onDetachedFromWindow();
        }

        public void apply() {
            this.readAnimated.set(this.read, true);
            this.indexAnimated.set(this.index, true);
            this.scaleAnimated.set(this.scale, true);
        }
    }

    public ProfileStoriesView(Context context, int i, long j, View view, ProfileActivity.AvatarImageView avatarImageView, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        Paint paint = new Paint(1);
        this.readPaint = paint;
        Paint paint2 = new Paint(1);
        this.whitePaint = paint2;
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.titleDrawable = animatedTextDrawable;
        Paint paint3 = new Paint(1);
        this.clipOutAvatar = paint3;
        this.circles = new ArrayList<>();
        this.paint = new Paint(1);
        this.bounceScale = 1.0f;
        this.progressToInsets = 1.0f;
        this.rect1 = new RectF();
        this.rect2 = new RectF();
        this.rect3 = new RectF();
        this.clipPath = new Path();
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.segmentsCountAnimated = new AnimatedFloat(this, 0L, 480L, cubicBezierInterpolator);
        this.segmentsUnreadCountAnimated = new AnimatedFloat(this, 0L, 240L, cubicBezierInterpolator);
        this.progressToUploading = new AnimatedFloat(this, 0L, 150L, CubicBezierInterpolator.DEFAULT);
        this.newStoryBounceT = 1.0f;
        this.expandRightPadAnimated = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.rightAnimated = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.provider = new AnonymousClass3();
        this.onLongPressRunnable = new Runnable() {
            @Override
            public final void run() {
                ProfileStoriesView.this.lambda$new$4();
            }
        };
        this.currentAccount = i;
        this.dialogId = j;
        this.avatarContainer = view;
        this.avatarImage = avatarImageView;
        this.storiesController = MessagesController.getInstance(i).getStoriesController();
        paint.setColor(1526726655);
        this.readPaintAlpha = paint.getAlpha();
        paint.setStrokeWidth(AndroidUtilities.dpf2(1.5f));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint2.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(18.0f));
        animatedTextDrawable.setAnimationProperties(0.4f, 0L, 320L, cubicBezierInterpolator);
        animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        animatedTextDrawable.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle, resourcesProvider));
        animatedTextDrawable.setEllipsizeByGradient(true);
        animatedTextDrawable.setCallback(this);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.paint.setStrokeWidth(AndroidUtilities.dpf2(2.33f));
        this.paint.setStyle(Paint.Style.STROKE);
        updateStories(false, false);
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.titleDrawable || super.verifyDrawable(drawable);
    }

    public void setStories(TLRPC$PeerStories tLRPC$PeerStories) {
        updateStories(true, false);
    }

    public void updateStories(boolean z, boolean z2) {
        ArrayList<TLRPC$StoryItem> arrayList;
        int i;
        TLRPC$StoryItem tLRPC$StoryItem;
        boolean z3 = this.dialogId == UserConfig.getInstance(this.currentAccount).getClientUserId();
        TLRPC$PeerStories storiesFromFullPeer = MessagesController.getInstance(this.currentAccount).getStoriesController().getStoriesFromFullPeer(this.dialogId);
        TLRPC$PeerStories stories = MessagesController.getInstance(this.currentAccount).getStoriesController().getStories(this.dialogId);
        TLRPC$PeerStories tLRPC$PeerStories = this.dialogId == 0 ? null : storiesFromFullPeer;
        int max = storiesFromFullPeer != null ? Math.max(0, storiesFromFullPeer.max_read_id) : 0;
        if (stories != null) {
            max = Math.max(max, stories.max_read_id);
        }
        if (tLRPC$PeerStories == null || (arrayList = tLRPC$PeerStories.stories) == null) {
            arrayList = new ArrayList<>();
        }
        ArrayList arrayList2 = new ArrayList();
        int i2 = this.unreadCount;
        this.unreadCount = 0;
        if (arrayList != null) {
            i = 0;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                TLRPC$StoryItem tLRPC$StoryItem2 = arrayList.get(i3);
                if (!(tLRPC$StoryItem2 instanceof TLRPC$TL_storyItemDeleted)) {
                    if (tLRPC$StoryItem2.id > max) {
                        this.unreadCount++;
                    }
                    i++;
                }
            }
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                TLRPC$StoryItem tLRPC$StoryItem3 = arrayList.get(i4);
                if (!(tLRPC$StoryItem3 instanceof TLRPC$TL_storyItemDeleted)) {
                    if (tLRPC$StoryItem3 instanceof TLRPC$TL_storyItemSkipped) {
                        int i5 = tLRPC$StoryItem3.id;
                        if (stories != null) {
                            int i6 = 0;
                            while (true) {
                                if (i6 >= stories.stories.size()) {
                                    break;
                                } else if (stories.stories.get(i6).id == i5) {
                                    tLRPC$StoryItem3 = stories.stories.get(i6);
                                    break;
                                } else {
                                    i6++;
                                }
                            }
                        }
                        boolean z4 = tLRPC$StoryItem3 instanceof TLRPC$TL_storyItemSkipped;
                        if (z4) {
                            if (storiesFromFullPeer != null) {
                                int i7 = 0;
                                while (true) {
                                    if (i7 >= storiesFromFullPeer.stories.size()) {
                                        break;
                                    } else if (storiesFromFullPeer.stories.get(i7).id == i5) {
                                        storiesFromFullPeer.stories.get(i7);
                                        break;
                                    } else {
                                        i7++;
                                    }
                                }
                            }
                        } else if (z4) {
                            continue;
                        }
                    }
                    if (z3 || tLRPC$StoryItem3.id > max) {
                        arrayList2.add(tLRPC$StoryItem3);
                        if (arrayList2.size() >= 3) {
                            break;
                        }
                    }
                }
            }
        } else {
            i = 0;
        }
        if (arrayList2.size() < 3) {
            for (int i8 = 0; i8 < arrayList.size(); i8++) {
                TLRPC$StoryItem tLRPC$StoryItem4 = arrayList.get(i8);
                if (tLRPC$StoryItem4 instanceof TLRPC$TL_storyItemSkipped) {
                    int i9 = tLRPC$StoryItem4.id;
                    if (stories != null) {
                        int i10 = 0;
                        while (true) {
                            if (i10 >= stories.stories.size()) {
                                break;
                            } else if (stories.stories.get(i10).id == i9) {
                                tLRPC$StoryItem4 = stories.stories.get(i10);
                                break;
                            } else {
                                i10++;
                            }
                        }
                    }
                    boolean z5 = tLRPC$StoryItem4 instanceof TLRPC$TL_storyItemSkipped;
                    if (z5) {
                        if (storiesFromFullPeer != null) {
                            int i11 = 0;
                            while (true) {
                                if (i11 >= storiesFromFullPeer.stories.size()) {
                                    break;
                                } else if (storiesFromFullPeer.stories.get(i11).id == i9) {
                                    storiesFromFullPeer.stories.get(i11);
                                    break;
                                } else {
                                    i11++;
                                }
                            }
                        }
                    } else if (z5) {
                        continue;
                    }
                }
                if (!(tLRPC$StoryItem4 instanceof TLRPC$TL_storyItemDeleted) && !arrayList2.contains(tLRPC$StoryItem4)) {
                    arrayList2.add(tLRPC$StoryItem4);
                    if (arrayList2.size() >= 3) {
                        break;
                    }
                }
            }
        }
        for (int i12 = 0; i12 < this.circles.size(); i12++) {
            StoryCircle storyCircle = this.circles.get(i12);
            int i13 = 0;
            while (true) {
                if (i13 >= arrayList2.size()) {
                    i13 = -1;
                    tLRPC$StoryItem = null;
                    break;
                }
                tLRPC$StoryItem = (TLRPC$StoryItem) arrayList2.get(i13);
                if (tLRPC$StoryItem.id == storyCircle.storyId) {
                    break;
                }
                i13++;
            }
            if (i13 == -1) {
                storyCircle.scale = 0.0f;
            } else {
                storyCircle.index = i13;
                storyCircle.read = z3 || !(tLRPC$PeerStories == null || tLRPC$StoryItem == null || tLRPC$StoryItem.id > this.storiesController.getMaxStoriesReadId(this.dialogId));
            }
            if (!z) {
                storyCircle.apply();
            }
        }
        for (int i14 = 0; i14 < arrayList2.size(); i14++) {
            TLRPC$StoryItem tLRPC$StoryItem5 = (TLRPC$StoryItem) arrayList2.get(i14);
            int i15 = 0;
            while (true) {
                if (i15 >= this.circles.size()) {
                    i15 = -1;
                    break;
                } else if (this.circles.get(i15).storyId == tLRPC$StoryItem5.id) {
                    break;
                } else {
                    i15++;
                }
            }
            if (i15 == -1) {
                tLRPC$StoryItem5.dialogId = this.dialogId;
                StoryCircle storyCircle2 = new StoryCircle(tLRPC$StoryItem5);
                storyCircle2.index = i14;
                storyCircle2.scale = 1.0f;
                storyCircle2.scaleAnimated.set(0.0f, true);
                storyCircle2.read = z3 || (tLRPC$PeerStories != null && tLRPC$StoryItem5.id <= tLRPC$PeerStories.max_read_id);
                if (!z) {
                    storyCircle2.apply();
                }
                this.circles.add(storyCircle2);
            }
        }
        this.mainCircle = null;
        int i16 = 0;
        while (true) {
            if (i16 >= this.circles.size()) {
                break;
            }
            StoryCircle storyCircle3 = this.circles.get(i16);
            if (storyCircle3.scale > 0.0f) {
                this.mainCircle = storyCircle3;
                break;
            }
            i16++;
        }
        ArrayList<StoriesController.UploadingStory> uploadingStories = this.storiesController.getUploadingStories(this.dialogId);
        this.uploadingStoriesCount = uploadingStories == null ? 0 : uploadingStories.size();
        int max2 = Math.max(arrayList2.size(), i);
        if (max2 == 0 && this.uploadingStoriesCount != 0) {
            max2 = 1;
        }
        if (z2 && z) {
            if (max2 == this.count + 1 && this.unreadCount == i2 + 1) {
                animateNewStory();
            }
        }
        this.count = max2;
        this.titleDrawable.setText(max2 > 0 ? LocaleController.formatPluralString("Stories", max2, new Object[0]) : "", z && !LocaleController.isRTL);
        invalidate();
    }

    public void setExpandProgress(float f) {
        if (this.expandProgress != f) {
            this.expandProgress = f;
            invalidate();
        }
    }

    public void setActionBarActionMode(float f) {
        if (Theme.isCurrentThemeDark()) {
            return;
        }
        this.actionBarProgress = f;
        invalidate();
    }

    public void vibrateNewStory() {
        if (SharedConfig.getDevicePerformanceClass() <= 0) {
            return;
        }
        try {
            performHapticFeedback(9, 1);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileStoriesView.this.lambda$vibrateNewStory$0();
            }
        }, 180L);
    }

    public void lambda$vibrateNewStory$0() {
        try {
            performHapticFeedback(9, 1);
        } catch (Exception unused) {
        }
    }

    public void animateNewStory() {
        ValueAnimator valueAnimator = this.newStoryBounce;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final boolean[] zArr = {false};
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.newStoryBounce = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ProfileStoriesView.this.lambda$animateNewStory$1(zArr, valueAnimator2);
            }
        });
        this.newStoryBounce.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                boolean[] zArr2 = zArr;
                if (!zArr2[0]) {
                    zArr2[0] = true;
                    ProfileStoriesView.this.vibrateNewStory();
                }
                ProfileStoriesView.this.newStoryBounceT = 1.0f;
                ProfileStoriesView.this.invalidate();
            }
        });
        this.newStoryBounce.setInterpolator(new OvershootInterpolator(3.0f));
        this.newStoryBounce.setDuration(400L);
        this.newStoryBounce.setStartDelay(120L);
        this.newStoryBounce.start();
    }

    public void lambda$animateNewStory$1(boolean[] zArr, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (!zArr[0] && floatValue > 0.2f) {
            zArr[0] = true;
            vibrateNewStory();
        }
        this.newStoryBounceT = Math.max(1.0f, floatValue);
        invalidate();
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.ProfileStoriesView.dispatchDraw(android.graphics.Canvas):void");
    }

    public static int lambda$dispatchDraw$2(StoryCircle storyCircle, StoryCircle storyCircle2) {
        return (int) (storyCircle2.cachedIndex - storyCircle.cachedIndex);
    }

    private void animateBounce() {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 1.05f);
        ofFloat.setDuration(100L);
        ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(1.05f, 1.0f);
        ofFloat2.setDuration(250L);
        ofFloat2.setInterpolator(new OvershootInterpolator());
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileStoriesView.this.lambda$animateBounce$3(valueAnimator);
            }
        };
        ofFloat.addUpdateListener(animatorUpdateListener);
        ofFloat2.addUpdateListener(animatorUpdateListener);
        animatorSet.playSequentially(ofFloat, ofFloat2);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ProfileStoriesView.this.avatarImage.bounceScale = ProfileStoriesView.this.bounceScale = 1.0f;
                ProfileStoriesView.this.avatarImage.invalidate();
                ProfileStoriesView.this.invalidate();
            }
        });
        animatorSet.start();
    }

    public void lambda$animateBounce$3(ValueAnimator valueAnimator) {
        ProfileActivity.AvatarImageView avatarImageView = this.avatarImage;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.bounceScale = floatValue;
        avatarImageView.bounceScale = floatValue;
        this.avatarImage.invalidate();
        invalidate();
    }

    public void clipCircle(Canvas canvas, StoryCircle storyCircle, StoryCircle storyCircle2) {
        if (storyCircle2 == null) {
            return;
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(storyCircle2.cachedRect);
        float f = -(AndroidUtilities.dpf2(1.66f) * storyCircle2.cachedScale);
        rectF.inset(f, f);
        float centerX = storyCircle2.cachedRect.centerX();
        float width = storyCircle2.cachedRect.width() / 2.0f;
        float centerX2 = storyCircle.cachedRect.centerX();
        float width2 = storyCircle.cachedRect.width() / 2.0f;
        this.clipPath.rewind();
        if (centerX > centerX2) {
            float degrees = (float) Math.toDegrees(Math.acos(Math.abs((((centerX - width) + (centerX2 + width2)) / 2.0f) - centerX2) / width2));
            this.clipPath.arcTo(rectF, 180.0f + degrees, (-degrees) * 2.0f);
            this.clipPath.arcTo(storyCircle.cachedRect, degrees, 360.0f - (2.0f * degrees));
        } else {
            float degrees2 = (float) Math.toDegrees(Math.acos(Math.abs((((centerX + width) + (centerX2 - width2)) / 2.0f) - centerX2) / width2));
            float f2 = 2.0f * degrees2;
            this.clipPath.arcTo(rectF, -degrees2, f2);
            this.clipPath.arcTo(storyCircle.cachedRect, 180.0f - degrees2, -(360.0f - f2));
        }
        this.clipPath.close();
        canvas.save();
        canvas.clipPath(this.clipPath);
    }

    public StoryCircle nearest(StoryCircle storyCircle, StoryCircle storyCircle2, StoryCircle storyCircle3) {
        if (storyCircle3 != null) {
            if (storyCircle == null && storyCircle2 == null) {
                return null;
            }
            return (storyCircle == null || storyCircle2 == null) ? storyCircle != null ? storyCircle : storyCircle2 : Math.min(Math.abs(storyCircle.borderRect.left - storyCircle3.borderRect.right), Math.abs(storyCircle.borderRect.right - storyCircle3.borderRect.left)) > Math.min(Math.abs(storyCircle2.borderRect.left - storyCircle3.borderRect.right), Math.abs(storyCircle2.borderRect.right - storyCircle3.borderRect.left)) ? storyCircle : storyCircle2;
        }
        return null;
    }

    private void drawArcs(Canvas canvas, StoryCircle storyCircle, StoryCircle storyCircle2, StoryCircle storyCircle3, Paint paint) {
        double degrees;
        double degrees2;
        StoryCircle storyCircle4 = storyCircle;
        if (storyCircle4 == null && storyCircle3 == null) {
            canvas.drawArc(storyCircle2.borderRect, 0.0f, 360.0f, false, paint);
        } else if (storyCircle4 == null || storyCircle3 == null) {
            if (storyCircle4 == null && storyCircle3 == null) {
                return;
            }
            if (storyCircle4 == null) {
                storyCircle4 = storyCircle3;
            }
            float centerX = storyCircle4.borderRect.centerX();
            float width = storyCircle4.borderRect.width() / 2.0f;
            float centerX2 = storyCircle2.borderRect.centerX();
            float width2 = storyCircle2.borderRect.width() / 2.0f;
            if (Math.abs(centerX - centerX2) > width + width2) {
                canvas.drawArc(storyCircle2.borderRect, 0.0f, 360.0f, false, paint);
            } else if (centerX > centerX2) {
                float degrees3 = (float) Math.toDegrees(Math.acos(Math.abs((((centerX - width) + (centerX2 + width2)) / 2.0f) - centerX2) / width2));
                canvas.drawArc(storyCircle2.borderRect, degrees3, 360.0f - (2.0f * degrees3), false, paint);
            } else {
                float degrees4 = (float) Math.toDegrees(Math.acos(Math.abs((((centerX + width) + (centerX2 - width2)) / 2.0f) - centerX2) / width2));
                canvas.drawArc(storyCircle2.borderRect, degrees4 + 180.0f, 360.0f - (degrees4 * 2.0f), false, paint);
            }
        } else {
            float centerX3 = storyCircle4.borderRect.centerX();
            float width3 = storyCircle4.borderRect.width() / 2.0f;
            float centerX4 = storyCircle2.borderRect.centerX();
            float width4 = storyCircle2.borderRect.width() / 2.0f;
            float centerX5 = storyCircle3.borderRect.centerX();
            float width5 = storyCircle3.borderRect.width() / 2.0f;
            boolean z = centerX3 > centerX4;
            if (z) {
                degrees = Math.toDegrees(Math.acos(Math.abs((((centerX3 - width3) + (centerX4 + width4)) / 2.0f) - centerX4) / width4));
            } else {
                degrees = Math.toDegrees(Math.acos(Math.abs((((centerX3 + width3) + (centerX4 - width4)) / 2.0f) - centerX4) / width4));
            }
            float f = (float) degrees;
            boolean z2 = centerX5 > centerX4;
            if (z2) {
                degrees2 = Math.toDegrees(Math.acos(Math.abs((((centerX5 - width5) + (centerX4 + width4)) / 2.0f) - centerX4) / width4));
            } else {
                degrees2 = Math.toDegrees(Math.acos(Math.abs((((centerX5 + width5) + (centerX4 - width4)) / 2.0f) - centerX4) / width4));
            }
            float f2 = (float) degrees2;
            if (z && z2) {
                float max = Math.max(f, f2);
                canvas.drawArc(storyCircle2.borderRect, max, 360.0f - (2.0f * max), false, paint);
            } else if (z) {
                canvas.drawArc(storyCircle2.borderRect, f2 + 180.0f, 180.0f - (f + f2), false, paint);
                canvas.drawArc(storyCircle2.borderRect, f, (180.0f - f2) - f, false, paint);
            } else if (z2) {
                canvas.drawArc(storyCircle2.borderRect, f + 180.0f, 180.0f - (f2 + f), false, paint);
                canvas.drawArc(storyCircle2.borderRect, f2, (180.0f - f2) - f, false, paint);
            } else {
                float max2 = Math.max(f, f2);
                canvas.drawArc(storyCircle2.borderRect, max2 + 180.0f, 360.0f - (max2 * 2.0f), false, paint);
            }
        }
    }

    private void lerpCentered(RectF rectF, RectF rectF2, float f, RectF rectF3) {
        float lerp = AndroidUtilities.lerp(rectF.centerX(), rectF2.centerX(), f);
        float lerp2 = AndroidUtilities.lerp(rectF.centerY(), rectF2.centerY(), f);
        float lerp3 = AndroidUtilities.lerp(Math.min(rectF.width(), rectF.height()), Math.min(rectF2.width(), rectF2.height()), f) / 2.0f;
        rectF3.set(lerp - lerp3, lerp2 - lerp3, lerp + lerp3, lerp2 + lerp3);
    }

    public void setBounds(float f, float f2, float f3, boolean z) {
        boolean z2 = Math.abs(f - this.left) > 0.1f || Math.abs(f2 - this.right) > 0.1f || Math.abs(f3 - this.cy) > 0.1f;
        this.left = f;
        this.right = f2;
        if (!z) {
            this.rightAnimated.set(f2, true);
        }
        this.cy = f3;
        if (z2) {
            invalidate();
        }
    }

    public void setExpandCoords(float f, boolean z, float f2) {
        this.expandRight = f;
        this.expandRightPad = z;
        this.expandY = f2;
        invalidate();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.storiesUpdated) {
            updateStories(true, true);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        for (int i = 0; i < this.circles.size(); i++) {
            this.circles.get(i).imageReceiver.onAttachedToWindow();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesUpdated);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        for (int i = 0; i < this.circles.size(); i++) {
            this.circles.get(i).imageReceiver.onDetachedFromWindow();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesUpdated);
    }

    public class AnonymousClass3 implements StoryViewer.PlaceProvider {
        @Override
        public void loadNext(boolean z) {
            StoryViewer.PlaceProvider.CC.$default$loadNext(this, z);
        }

        AnonymousClass3() {
        }

        @Override
        public boolean findView(long j, int i, int i2, int i3, StoryViewer.TransitionViewHolder transitionViewHolder) {
            ImageReceiver imageReceiver;
            final StoryCircle storyCircle;
            final StoryCircle storyCircle2;
            transitionViewHolder.avatarImage = null;
            transitionViewHolder.storyImage = null;
            if (ProfileStoriesView.this.expandProgress < 0.2f) {
                transitionViewHolder.avatarImage = ProfileStoriesView.this.avatarImage.getImageReceiver();
                transitionViewHolder.storyImage = null;
                transitionViewHolder.view = ProfileStoriesView.this.avatarImage;
                transitionViewHolder.clipTop = 0.0f;
                transitionViewHolder.clipBottom = AndroidUtilities.displaySize.y;
                transitionViewHolder.clipParent = (View) ProfileStoriesView.this.getParent();
                transitionViewHolder.radialProgressUpload = ProfileStoriesView.this.radialProgress;
                return true;
            }
            int i4 = 0;
            while (true) {
                if (i4 >= ProfileStoriesView.this.circles.size()) {
                    imageReceiver = null;
                    storyCircle = null;
                    storyCircle2 = null;
                    break;
                }
                StoryCircle storyCircle3 = (StoryCircle) ProfileStoriesView.this.circles.get(i4);
                if (storyCircle3.scale < 1.0f || storyCircle3.storyId != i2) {
                    i4++;
                } else {
                    ProfileStoriesView profileStoriesView = ProfileStoriesView.this;
                    int i5 = i4 - 1;
                    int i6 = i4 - 2;
                    StoryCircle nearest = profileStoriesView.nearest(i5 >= 0 ? (StoryCircle) profileStoriesView.circles.get(i5) : null, i6 >= 0 ? (StoryCircle) ProfileStoriesView.this.circles.get(i6) : null, storyCircle3);
                    imageReceiver = storyCircle3.imageReceiver;
                    storyCircle2 = nearest;
                    storyCircle = storyCircle3;
                }
            }
            if (imageReceiver == null) {
                return false;
            }
            transitionViewHolder.storyImage = imageReceiver;
            transitionViewHolder.avatarImage = null;
            ProfileStoriesView profileStoriesView2 = ProfileStoriesView.this;
            transitionViewHolder.view = profileStoriesView2;
            transitionViewHolder.clipTop = 0.0f;
            transitionViewHolder.clipBottom = AndroidUtilities.displaySize.y;
            transitionViewHolder.clipParent = (View) profileStoriesView2.getParent();
            if (storyCircle != null && storyCircle2 != null) {
                final RectF rectF = new RectF(storyCircle.cachedRect);
                final RectF rectF2 = new RectF(storyCircle2.cachedRect);
                transitionViewHolder.drawClip = new StoryViewer.HolderClip() {
                    @Override
                    public final void clip(Canvas canvas, RectF rectF3, float f, boolean z) {
                        ProfileStoriesView.AnonymousClass3.this.lambda$findView$0(rectF, storyCircle, rectF2, storyCircle2, canvas, rectF3, f, z);
                    }
                };
            } else {
                transitionViewHolder.drawClip = null;
            }
            return true;
        }

        public void lambda$findView$0(RectF rectF, StoryCircle storyCircle, RectF rectF2, StoryCircle storyCircle2, Canvas canvas, RectF rectF3, float f, boolean z) {
            rectF.set(storyCircle.cachedRect);
            rectF2.set(storyCircle2.cachedRect);
            storyCircle.cachedRect.set(rectF3);
            try {
                float width = rectF3.width() / rectF.width();
                float centerX = rectF3.centerX() - ((rectF.centerX() - rectF2.centerX()) * (((1.0f - f) * 2.0f) + width));
                float centerY = rectF3.centerY();
                float width2 = (rectF2.width() / 2.0f) * width;
                float height = (rectF2.height() / 2.0f) * width;
                storyCircle2.cachedRect.set(centerX - width2, centerY - height, centerX + width2, centerY + height);
            } catch (Exception unused) {
            }
            ProfileStoriesView.this.clipCircle(canvas, storyCircle, storyCircle2);
            storyCircle.cachedRect.set(rectF);
            storyCircle2.cachedRect.set(rectF2);
        }

        @Override
        public void preLayout(long j, int i, Runnable runnable) {
            ProfileStoriesView.this.updateStories(true, false);
            runnable.run();
        }
    }

    public boolean isEmpty() {
        return this.circles.isEmpty();
    }

    private float getExpandRight() {
        return this.expandRight - (this.expandRightPadAnimated.set(this.expandRightPad) * AndroidUtilities.dp(71.0f));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (this.expandProgress < 0.9f) {
            z = this.rect2.contains(motionEvent.getX(), motionEvent.getY());
        } else {
            z = motionEvent.getX() >= (getExpandRight() - this.w) - ((float) AndroidUtilities.dp(32.0f)) && motionEvent.getX() <= getExpandRight() + ((float) AndroidUtilities.dp(32.0f)) && Math.abs(motionEvent.getY() - this.expandY) < ((float) AndroidUtilities.dp(32.0f));
        }
        if (z && motionEvent.getAction() == 0) {
            this.tapTime = System.currentTimeMillis();
            this.tapX = motionEvent.getX();
            this.tapY = motionEvent.getY();
            AndroidUtilities.cancelRunOnUIThread(this.onLongPressRunnable);
            AndroidUtilities.runOnUIThread(this.onLongPressRunnable, ViewConfiguration.getLongPressTimeout());
            return true;
        }
        if (motionEvent.getAction() == 1) {
            AndroidUtilities.cancelRunOnUIThread(this.onLongPressRunnable);
            if (z && System.currentTimeMillis() - this.tapTime <= ViewConfiguration.getTapTimeout() && MathUtils.distance(this.tapX, this.tapY, motionEvent.getX(), motionEvent.getY()) <= AndroidUtilities.dp(12.0f) && (this.storiesController.hasUploadingStories(this.dialogId) || this.storiesController.hasStories(this.dialogId) || !this.circles.isEmpty())) {
                onTap(this.provider);
                return true;
            }
        } else if (motionEvent.getAction() == 3) {
            this.tapTime = -1L;
            AndroidUtilities.cancelRunOnUIThread(this.onLongPressRunnable);
        }
        return super.onTouchEvent(motionEvent);
    }

    @Keep
    public void setFragmentTransitionProgress(float f) {
        if (this.fragmentTransitionProgress == f) {
            return;
        }
        this.fragmentTransitionProgress = f;
        invalidate();
    }

    @Keep
    public float getFragmentTransitionProgress() {
        return this.fragmentTransitionProgress;
    }
}
