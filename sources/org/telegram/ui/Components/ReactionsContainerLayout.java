package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatReactions;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$ReactionCount;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_chatReactionsAll;
import org.telegram.tgnet.TLRPC$TL_chatReactionsNone;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messages_savedReactionsTags;
import org.telegram.tgnet.TLRPC$TL_reactionCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.tgnet.TLRPC$messages_AvailableEffects;
import org.telegram.tgnet.tl.TL_stories$StoryItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow;
import org.telegram.ui.Components.Reactions.HwEmojis;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.HintView2;

public class ReactionsContainerLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final Property<ReactionsContainerLayout, Float> TRANSITION_PROGRESS_VALUE = new Property<ReactionsContainerLayout, Float>(Float.class, "transitionProgress") {
        AnonymousClass1(Class cls, String str) {
            super(cls, str);
        }

        @Override
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        @Override
        public void set(ReactionsContainerLayout reactionsContainerLayout, Float f) {
            reactionsContainerLayout.setTransitionProgress(f.floatValue());
        }
    };
    private boolean allReactionsAvailable;
    private boolean allReactionsIsDefault;
    public List<ReactionsLayoutInBubble.VisibleReaction> allReactionsList;
    HashSet<ReactionsLayoutInBubble.VisibleReaction> alwaysSelectedReactions;
    private boolean animatePopup;
    private final boolean animationEnabled;
    private Paint bgPaint;
    public int bigCircleOffset;
    private float bigCircleRadius;
    public float bubblesOffset;
    ValueAnimator cancelPressedAnimation;
    private float cancelPressedProgress;
    ChatScrimPopupContainerLayout chatScrimPopupContainerLayout;
    private boolean clicked;
    private int currentAccount;
    private float customEmojiReactionsEnterProgress;
    private InternalImageView customEmojiReactionsIconView;
    FrameLayout customReactionsContainer;
    private ReactionsContainerDelegate delegate;
    public final float durationScale;
    private float flipVerticalProgress;
    BaseFragment fragment;
    public boolean hasHint;
    private boolean hintMeasured;
    public TextView hintView;
    public int hintViewHeight;
    public int hintViewWidth;
    public boolean hitLimit;
    private boolean isFlippedVertically;
    public boolean isHiddenNextReaction;
    private boolean isTop;
    public ArrayList<InnerItem> items;
    long lastReactionSentTime;
    private long lastUpdate;
    HashSet<View> lastVisibleViews;
    HashSet<View> lastVisibleViewsTmp;
    private float leftAlpha;
    private Paint leftShadowPaint;
    private LinearLayoutManager linearLayoutManager;
    private Adapter listAdapter;
    private int[] location;
    private Path mPath;
    public MessageObject messageObject;
    private float miniBubblesOffset;
    private boolean mirrorX;
    public ReactionHolderView nextRecentReaction;
    public final AnimationNotificationsLocker notificationsLocker;
    public ArrayList<InnerItem> oldItems;
    private Runnable onSwitchedToLoopView;
    private float otherViewsScale;
    ChatScrimPopupContainerLayout parentLayout;
    public boolean paused;
    public boolean pausedExceptSelected;
    FrameLayout premiumLockContainer;
    private PremiumLockIconView premiumLockIconView;
    private List<TLRPC$TL_availableReaction> premiumLockedReactions;
    private boolean prepareAnimation;
    private float pressedProgress;
    private ReactionsLayoutInBubble.VisibleReaction pressedReaction;
    private int pressedReactionPosition;
    private float pressedViewScale;
    ValueAnimator pullingDownBackAnimator;
    float pullingLeftOffset;
    public float radius;
    CustomEmojiReactionsWindow reactionsWindow;
    public RectF rect;
    RectF rectF;
    public final RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    private float rightAlpha;
    private Paint rightShadowPaint;
    private Paint selectedPaint;
    HashSet<ReactionsLayoutInBubble.VisibleReaction> selectedReactions;
    private Drawable shadow;
    private android.graphics.Rect shadowPad;
    private boolean showExpandableReactions;
    boolean skipDraw;
    public boolean skipEnterAnimation;
    private float smallCircleRadius;
    private float transitionProgress;
    private final int type;
    private List<ReactionsLayoutInBubble.VisibleReaction> visibleReactionsList;
    private long waitingLoadingChatId;

    public interface ReactionsContainerDelegate {

        public final class CC {
            public static boolean $default$drawBackground(ReactionsContainerDelegate reactionsContainerDelegate) {
                return false;
            }

            public static void $default$drawRoundRect(ReactionsContainerDelegate reactionsContainerDelegate, Canvas canvas, RectF rectF, float f, float f2, float f3, int i, boolean z) {
            }

            public static boolean $default$needEnterText(ReactionsContainerDelegate reactionsContainerDelegate) {
                return false;
            }

            public static void $default$onEmojiWindowDismissed(ReactionsContainerDelegate reactionsContainerDelegate) {
            }
        }

        boolean drawBackground();

        void drawRoundRect(Canvas canvas, RectF rectF, float f, float f2, float f3, int i, boolean z);

        boolean needEnterText();

        void onEmojiWindowDismissed();

        void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2);
    }

    protected void onShownCustomEmojiReactionDialog() {
    }

    public class AnonymousClass1 extends Property<ReactionsContainerLayout, Float> {
        AnonymousClass1(Class cls, String str) {
            super(cls, str);
        }

        @Override
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        @Override
        public void set(ReactionsContainerLayout reactionsContainerLayout, Float f) {
            reactionsContainerLayout.setTransitionProgress(f.floatValue());
        }
    }

    public class InnerItem extends AdapterWithDiffUtils.Item {
        ReactionsLayoutInBubble.VisibleReaction reaction;

        public InnerItem(ReactionsContainerLayout reactionsContainerLayout, int i, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            super(i, false);
            this.reaction = visibleReaction;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || InnerItem.class != obj.getClass()) {
                return false;
            }
            InnerItem innerItem = (InnerItem) obj;
            int i = this.viewType;
            int i2 = innerItem.viewType;
            if (i != i2 || (i != 0 && i != 3)) {
                return i == i2;
            }
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.reaction;
            return visibleReaction != null && visibleReaction.equals(innerItem.reaction);
        }
    }

    public ReactionsContainerLayout(final int i, BaseFragment baseFragment, Context context, int i2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.items = new ArrayList<>();
        this.oldItems = new ArrayList<>();
        this.bgPaint = new Paint(1);
        this.leftShadowPaint = new Paint(1);
        this.rightShadowPaint = new Paint(1);
        this.transitionProgress = 1.0f;
        this.rect = new RectF();
        this.mPath = new Path();
        this.radius = AndroidUtilities.dp(72.0f);
        float dp = AndroidUtilities.dp(8.0f);
        this.bigCircleRadius = dp;
        this.smallCircleRadius = dp / 2.0f;
        this.bigCircleOffset = AndroidUtilities.dp(36.0f);
        this.visibleReactionsList = new ArrayList(20);
        this.premiumLockedReactions = new ArrayList(10);
        this.allReactionsList = new ArrayList(20);
        this.rectF = new RectF();
        this.selectedReactions = new HashSet<>();
        this.alwaysSelectedReactions = new HashSet<>();
        this.location = new int[2];
        this.shadowPad = new android.graphics.Rect();
        new ArrayList();
        this.lastVisibleViews = new HashSet<>();
        this.lastVisibleViewsTmp = new HashSet<>();
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.isHiddenNextReaction = true;
        this.paused = false;
        this.type = i;
        this.durationScale = Settings.Global.getFloat(context.getContentResolver(), "animator_duration_scale", 1.0f);
        Paint paint = new Paint(1);
        this.selectedPaint = paint;
        paint.setColor(Theme.getColor(Theme.key_listSelector, resourcesProvider));
        this.resourcesProvider = resourcesProvider;
        this.currentAccount = i2;
        this.fragment = baseFragment;
        ReactionHolderView reactionHolderView = new ReactionHolderView(context, false);
        this.nextRecentReaction = reactionHolderView;
        reactionHolderView.setVisibility(8);
        ReactionHolderView reactionHolderView2 = this.nextRecentReaction;
        reactionHolderView2.touchable = false;
        reactionHolderView2.pressedBackupImageView.setVisibility(8);
        addView(this.nextRecentReaction);
        this.animationEnabled = SharedConfig.animationsEnabled() && SharedConfig.getDevicePerformanceClass() != 0;
        this.shadow = ContextCompat.getDrawable(context, R.drawable.reactions_bubble_shadow).mutate();
        android.graphics.Rect rect = this.shadowPad;
        int dp2 = AndroidUtilities.dp(7.0f);
        rect.bottom = dp2;
        rect.right = dp2;
        rect.top = dp2;
        rect.left = dp2;
        this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelShadow), PorterDuff.Mode.MULTIPLY));
        AnonymousClass2 anonymousClass2 = new RecyclerListView(context) {
            AnonymousClass2(Context context2) {
                super(context2);
            }

            @Override
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (ReactionsContainerLayout.this.pressedReaction != null && (view instanceof ReactionHolderView) && ((ReactionHolderView) view).currentReaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (motionEvent.getAction() != 1 || ReactionsContainerLayout.this.getPullingLeftProgress() <= 0.95f) {
                        ReactionsContainerLayout.this.animatePullingBack();
                    } else {
                        ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
                    }
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.recyclerListView = anonymousClass2;
        anonymousClass2.setClipChildren(false);
        anonymousClass2.setClipToPadding(false);
        this.linearLayoutManager = new LinearLayoutManager(context2, 0, false) {
            AnonymousClass3(Context context2, int i3, boolean z) {
                super(context2, i3, z);
            }

            @Override
            public int scrollHorizontallyBy(int i3, RecyclerView.Recycler recycler, RecyclerView.State state) {
                int i4;
                if (i3 < 0) {
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    if (reactionsContainerLayout.pullingLeftOffset != 0.0f) {
                        float pullingLeftProgress = reactionsContainerLayout.getPullingLeftProgress();
                        ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                        reactionsContainerLayout2.pullingLeftOffset += i3;
                        if ((pullingLeftProgress > 1.0f) != (reactionsContainerLayout2.getPullingLeftProgress() > 1.0f)) {
                            ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                        }
                        ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                        float f = reactionsContainerLayout3.pullingLeftOffset;
                        if (f < 0.0f) {
                            i4 = (int) f;
                            reactionsContainerLayout3.pullingLeftOffset = 0.0f;
                        } else {
                            i4 = 0;
                        }
                        FrameLayout frameLayout = reactionsContainerLayout3.customReactionsContainer;
                        if (frameLayout != null) {
                            frameLayout.invalidate();
                        }
                        ReactionsContainerLayout.this.recyclerListView.invalidate();
                        i3 = i4;
                    }
                }
                int scrollHorizontallyBy = super.scrollHorizontallyBy(i3, recycler, state);
                if (i3 > 0 && scrollHorizontallyBy == 0 && ReactionsContainerLayout.this.recyclerListView.getScrollState() == 1 && ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    ValueAnimator valueAnimator = ReactionsContainerLayout.this.pullingDownBackAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        ReactionsContainerLayout.this.pullingDownBackAnimator.cancel();
                    }
                    float pullingLeftProgress2 = ReactionsContainerLayout.this.getPullingLeftProgress();
                    float f2 = pullingLeftProgress2 > 1.0f ? 0.05f : 0.6f;
                    ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                    reactionsContainerLayout4.pullingLeftOffset += i3 * f2;
                    if ((pullingLeftProgress2 > 1.0f) != (reactionsContainerLayout4.getPullingLeftProgress() > 1.0f)) {
                        ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                    }
                    FrameLayout frameLayout2 = ReactionsContainerLayout.this.customReactionsContainer;
                    if (frameLayout2 != null) {
                        frameLayout2.invalidate();
                    }
                    ReactionsContainerLayout.this.recyclerListView.invalidate();
                }
                return scrollHorizontallyBy;
            }
        };
        anonymousClass2.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass4() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect2, View view, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect2, view, recyclerView, state);
                if (!ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                    if (childAdapterPosition == 0) {
                        rect2.left = AndroidUtilities.dp(6.0f);
                    }
                    rect2.right = AndroidUtilities.dp(4.0f);
                    if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                        if (ReactionsContainerLayout.this.showUnlockPremiumButton() || ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                            rect2.right = AndroidUtilities.dp(2.0f);
                            return;
                        } else {
                            rect2.right = AndroidUtilities.dp(6.0f);
                            return;
                        }
                    }
                    return;
                }
                rect2.left = 0;
                rect2.right = 0;
            }
        });
        anonymousClass2.setLayoutManager(this.linearLayoutManager);
        anonymousClass2.setOverScrollMode(2);
        Adapter adapter = new Adapter();
        this.listAdapter = adapter;
        anonymousClass2.setAdapter(adapter);
        anonymousClass2.addOnScrollListener(new LeftRightShadowsListener());
        anonymousClass2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass5() {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                if (recyclerView.getChildCount() > 2) {
                    recyclerView.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int i5 = ReactionsContainerLayout.this.location[0];
                    View childAt = recyclerView.getChildAt(0);
                    childAt.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min = ((1.0f - Math.min(1.0f, (-Math.min(ReactionsContainerLayout.this.location[0] - i5, 0.0f)) / childAt.getWidth())) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min)) {
                        min = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(childAt, min);
                    View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min2 = ((1.0f - Math.min(1.0f, (-Math.min((i5 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth()), 0.0f)) / childAt2.getWidth())) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min2)) {
                        min2 = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(childAt2, min2);
                }
                for (int i6 = 1; i6 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i6++) {
                    ReactionsContainerLayout.this.setChildScale(ReactionsContainerLayout.this.recyclerListView.getChildAt(i6), 1.0f);
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        anonymousClass2.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass6() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect2, View view, RecyclerView recyclerView, RecyclerView.State state) {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect2.left = AndroidUtilities.dp(8.0f);
                }
                if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    rect2.right = AndroidUtilities.dp(8.0f);
                }
            }
        });
        anonymousClass2.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                ReactionsContainerLayout.this.lambda$new$0(view, i3);
            }
        });
        anonymousClass2.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i3) {
                boolean lambda$new$1;
                lambda$new$1 = ReactionsContainerLayout.this.lambda$new$1(i, view, i3);
                return lambda$new$1;
            }
        });
        addView(anonymousClass2, LayoutHelper.createFrame(-1, -1.0f));
        setClipChildren(false);
        setClipToPadding(false);
        invalidateShaders();
        int paddingTop = (anonymousClass2.getLayoutParams().height - anonymousClass2.getPaddingTop()) - anonymousClass2.getPaddingBottom();
        this.nextRecentReaction.getLayoutParams().width = paddingTop - AndroidUtilities.dp(12.0f);
        this.nextRecentReaction.getLayoutParams().height = paddingTop;
        if (i == 2 || i == 4) {
            this.bgPaint.setColor(ColorUtils.blendARGB(-16777216, -1, 0.13f));
        } else {
            this.bgPaint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
        }
        MediaDataController.getInstance(i2).preloadDefaultReactions();
    }

    public class AnonymousClass2 extends RecyclerListView {
        AnonymousClass2(Context context2) {
            super(context2);
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (ReactionsContainerLayout.this.pressedReaction != null && (view instanceof ReactionHolderView) && ((ReactionHolderView) view).currentReaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() != 1 || ReactionsContainerLayout.this.getPullingLeftProgress() <= 0.95f) {
                    ReactionsContainerLayout.this.animatePullingBack();
                } else {
                    ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }
    }

    public class AnonymousClass3 extends LinearLayoutManager {
        AnonymousClass3(Context context2, int i3, boolean z) {
            super(context2, i3, z);
        }

        @Override
        public int scrollHorizontallyBy(int i3, RecyclerView.Recycler recycler, RecyclerView.State state) {
            int i4;
            if (i3 < 0) {
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                if (reactionsContainerLayout.pullingLeftOffset != 0.0f) {
                    float pullingLeftProgress = reactionsContainerLayout.getPullingLeftProgress();
                    ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                    reactionsContainerLayout2.pullingLeftOffset += i3;
                    if ((pullingLeftProgress > 1.0f) != (reactionsContainerLayout2.getPullingLeftProgress() > 1.0f)) {
                        ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                    }
                    ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                    float f = reactionsContainerLayout3.pullingLeftOffset;
                    if (f < 0.0f) {
                        i4 = (int) f;
                        reactionsContainerLayout3.pullingLeftOffset = 0.0f;
                    } else {
                        i4 = 0;
                    }
                    FrameLayout frameLayout = reactionsContainerLayout3.customReactionsContainer;
                    if (frameLayout != null) {
                        frameLayout.invalidate();
                    }
                    ReactionsContainerLayout.this.recyclerListView.invalidate();
                    i3 = i4;
                }
            }
            int scrollHorizontallyBy = super.scrollHorizontallyBy(i3, recycler, state);
            if (i3 > 0 && scrollHorizontallyBy == 0 && ReactionsContainerLayout.this.recyclerListView.getScrollState() == 1 && ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                ValueAnimator valueAnimator = ReactionsContainerLayout.this.pullingDownBackAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    ReactionsContainerLayout.this.pullingDownBackAnimator.cancel();
                }
                float pullingLeftProgress2 = ReactionsContainerLayout.this.getPullingLeftProgress();
                float f2 = pullingLeftProgress2 > 1.0f ? 0.05f : 0.6f;
                ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                reactionsContainerLayout4.pullingLeftOffset += i3 * f2;
                if ((pullingLeftProgress2 > 1.0f) != (reactionsContainerLayout4.getPullingLeftProgress() > 1.0f)) {
                    ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                }
                FrameLayout frameLayout2 = ReactionsContainerLayout.this.customReactionsContainer;
                if (frameLayout2 != null) {
                    frameLayout2.invalidate();
                }
                ReactionsContainerLayout.this.recyclerListView.invalidate();
            }
            return scrollHorizontallyBy;
        }
    }

    public class AnonymousClass4 extends RecyclerView.ItemDecoration {
        AnonymousClass4() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect2, View view, RecyclerView recyclerView, RecyclerView.State state) {
            super.getItemOffsets(rect2, view, recyclerView, state);
            if (!ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect2.left = AndroidUtilities.dp(6.0f);
                }
                rect2.right = AndroidUtilities.dp(4.0f);
                if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    if (ReactionsContainerLayout.this.showUnlockPremiumButton() || ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                        rect2.right = AndroidUtilities.dp(2.0f);
                        return;
                    } else {
                        rect2.right = AndroidUtilities.dp(6.0f);
                        return;
                    }
                }
                return;
            }
            rect2.left = 0;
            rect2.right = 0;
        }
    }

    public class AnonymousClass5 extends RecyclerView.OnScrollListener {
        AnonymousClass5() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
            if (recyclerView.getChildCount() > 2) {
                recyclerView.getLocationInWindow(ReactionsContainerLayout.this.location);
                int i5 = ReactionsContainerLayout.this.location[0];
                View childAt = recyclerView.getChildAt(0);
                childAt.getLocationInWindow(ReactionsContainerLayout.this.location);
                float min = ((1.0f - Math.min(1.0f, (-Math.min(ReactionsContainerLayout.this.location[0] - i5, 0.0f)) / childAt.getWidth())) * 0.39999998f) + 0.6f;
                if (Float.isNaN(min)) {
                    min = 1.0f;
                }
                ReactionsContainerLayout.this.setChildScale(childAt, min);
                View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                float min2 = ((1.0f - Math.min(1.0f, (-Math.min((i5 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth()), 0.0f)) / childAt2.getWidth())) * 0.39999998f) + 0.6f;
                if (Float.isNaN(min2)) {
                    min2 = 1.0f;
                }
                ReactionsContainerLayout.this.setChildScale(childAt2, min2);
            }
            for (int i6 = 1; i6 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i6++) {
                ReactionsContainerLayout.this.setChildScale(ReactionsContainerLayout.this.recyclerListView.getChildAt(i6), 1.0f);
            }
            ReactionsContainerLayout.this.invalidate();
        }
    }

    public class AnonymousClass6 extends RecyclerView.ItemDecoration {
        AnonymousClass6() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect2, View view, RecyclerView recyclerView, RecyclerView.State state) {
            int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            if (childAdapterPosition == 0) {
                rect2.left = AndroidUtilities.dp(8.0f);
            }
            if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                rect2.right = AndroidUtilities.dp(8.0f);
            }
        }
    }

    public void lambda$new$0(View view, int i) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate == null || !(view instanceof ReactionHolderView)) {
            return;
        }
        reactionsContainerDelegate.onReactionClicked(this, ((ReactionHolderView) view).currentReaction, false, false);
    }

    public boolean lambda$new$1(int i, View view, int i2) {
        ReactionsContainerDelegate reactionsContainerDelegate;
        if (i == 5 || (reactionsContainerDelegate = this.delegate) == null || !(view instanceof ReactionHolderView)) {
            return false;
        }
        reactionsContainerDelegate.onReactionClicked(this, ((ReactionHolderView) view).currentReaction, true, false);
        return true;
    }

    public boolean showExpandableReactions() {
        return this.showExpandableReactions;
    }

    public List<ReactionsLayoutInBubble.VisibleReaction> getVisibleReactionsList() {
        return this.visibleReactionsList;
    }

    public int getWindowType() {
        int i = this.type;
        if (i == 4) {
            return 13;
        }
        if (i == 3) {
            return 11;
        }
        if (i == 5) {
            return 14;
        }
        return this.showExpandableReactions ? 8 : 1;
    }

    public void animatePullingBack() {
        if (this.pullingLeftOffset != 0.0f) {
            ValueAnimator valueAnimator = this.pullingDownBackAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.pullingLeftOffset, 0.0f);
            this.pullingDownBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ReactionsContainerLayout.this.lambda$animatePullingBack$2(valueAnimator2);
                }
            });
            this.pullingDownBackAnimator.setDuration(150L);
            this.pullingDownBackAnimator.start();
        }
    }

    public void lambda$animatePullingBack$2(ValueAnimator valueAnimator) {
        this.pullingLeftOffset = ((Float) this.pullingDownBackAnimator.getAnimatedValue()).floatValue();
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    public void setOnSwitchedToLoopView(Runnable runnable) {
        this.onSwitchedToLoopView = runnable;
    }

    public void dismissWindow() {
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow != null) {
            customEmojiReactionsWindow.dismiss();
        }
    }

    public CustomEmojiReactionsWindow getReactionsWindow() {
        return this.reactionsWindow;
    }

    public void showCustomEmojiReactionDialog() {
        if (this.reactionsWindow != null) {
            return;
        }
        this.reactionsWindow = new CustomEmojiReactionsWindow(this.type, this.fragment, this.allReactionsList, this.selectedReactions, this, this.resourcesProvider);
        invalidateLoopViews();
        this.reactionsWindow.onDismissListener(new Runnable() {
            @Override
            public final void run() {
                ReactionsContainerLayout.this.lambda$showCustomEmojiReactionDialog$3();
            }
        });
        onShownCustomEmojiReactionDialog();
    }

    public void lambda$showCustomEmojiReactionDialog$3() {
        this.reactionsWindow = null;
        invalidateLoopViews();
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null) {
            reactionsContainerDelegate.onEmojiWindowDismissed();
        }
    }

    public void invalidateLoopViews() {
        for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
            View childAt = this.recyclerListView.getChildAt(i);
            if (childAt instanceof ReactionHolderView) {
                ((ReactionHolderView) childAt).loopImageView.invalidate();
            }
        }
    }

    public boolean showCustomEmojiReaction() {
        return this.allReactionsAvailable || this.showExpandableReactions;
    }

    public boolean showUnlockPremiumButton() {
        return (this.premiumLockedReactions.isEmpty() || MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()) ? false : true;
    }

    public void showUnlockPremium(float f, float f2) {
        new PremiumFeatureBottomSheet(this.fragment, 4, true).show();
    }

    public void setChildScale(View view, float f) {
        if (view instanceof ReactionHolderView) {
            ((ReactionHolderView) view).sideScale = f;
        } else {
            view.setScaleX(f);
            view.setScaleY(f);
        }
    }

    public void setDelegate(ReactionsContainerDelegate reactionsContainerDelegate) {
        this.delegate = reactionsContainerDelegate;
    }

    public boolean isFlippedVertically() {
        return this.isFlippedVertically;
    }

    public void setFlippedVertically(boolean z) {
        this.isFlippedVertically = z;
        invalidate();
    }

    public void setMirrorX(boolean z) {
        this.mirrorX = z;
        invalidate();
    }

    @SuppressLint({"NotifyDataSetChanged"})
    private void setVisibleReactionsList(List<ReactionsLayoutInBubble.VisibleReaction> list, boolean z) {
        this.visibleReactionsList.clear();
        if (showCustomEmojiReaction()) {
            int dp = (AndroidUtilities.displaySize.x - AndroidUtilities.dp(36.0f)) / AndroidUtilities.dp(34.0f);
            if (dp > 7) {
                dp = 7;
            }
            if (dp < 1) {
                dp = 1;
            }
            int i = 0;
            while (i < Math.min(list.size(), dp)) {
                this.visibleReactionsList.add(list.get(i));
                i++;
            }
            if (i < list.size()) {
                this.nextRecentReaction.setReaction(list.get(i), -1);
            }
        } else {
            this.visibleReactionsList.addAll(list);
        }
        this.allReactionsIsDefault = true;
        for (int i2 = 0; i2 < this.visibleReactionsList.size(); i2++) {
            if (this.visibleReactionsList.get(i2).documentId != 0) {
                this.allReactionsIsDefault = false;
            }
        }
        this.allReactionsList.clear();
        this.allReactionsList.addAll(list);
        if ((((getLayoutParams().height - ((int) getTopOffset())) - getPaddingTop()) - getPaddingBottom()) * list.size() < AndroidUtilities.dp(200.0f)) {
            getLayoutParams().width = -2;
        }
        this.listAdapter.updateItems(z);
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactionsContainerLayout.dispatchDraw(android.graphics.Canvas):void");
    }

    public void drawBubbles(Canvas canvas) {
        float max = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        drawBubbles(canvas, this.bigCircleRadius * max, max, this.smallCircleRadius * max, this.type == 5 ? 255 : (int) (Utilities.clamp(this.customEmojiReactionsEnterProgress / 0.2f, 1.0f, 0.0f) * (1.0f - this.customEmojiReactionsEnterProgress) * 255.0f));
    }

    private void drawBubbles(Canvas canvas, float f, float f2, float f3, int i) {
        if (this.type == 1) {
            return;
        }
        canvas.save();
        if (this.isTop) {
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), (AndroidUtilities.lerp(this.rect.top, getMeasuredHeight(), CubicBezierInterpolator.DEFAULT.getInterpolation(this.flipVerticalProgress)) - ((int) Math.ceil((this.rect.height() / 2.0f) * (1.0f - this.transitionProgress)))) + 1.0f);
        } else {
            float f4 = this.rect.bottom;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            canvas.clipRect(0.0f, (AndroidUtilities.lerp(f4, 0.0f, cubicBezierInterpolator.getInterpolation(this.flipVerticalProgress)) - ((int) Math.ceil((this.rect.height() / 2.0f) * (1.0f - this.transitionProgress)))) - 1.0f, getMeasuredWidth(), AndroidUtilities.lerp(getMeasuredHeight() + AndroidUtilities.dp(8.0f), getPaddingTop() - expandSize(), cubicBezierInterpolator.getInterpolation(this.flipVerticalProgress)));
        }
        float width = ((LocaleController.isRTL || this.mirrorX) ? this.bigCircleOffset : getWidth() - this.bigCircleOffset) + this.bubblesOffset;
        float paddingTop = this.isTop ? getPaddingTop() - expandSize() : (getHeight() - getPaddingBottom()) + expandSize();
        int dp = AndroidUtilities.dp(3.0f);
        this.shadow.setAlpha(i);
        this.bgPaint.setAlpha(i);
        float f5 = width - f;
        float f6 = dp;
        float f7 = f6 * f2;
        float f8 = paddingTop - f;
        float f9 = width + f;
        float f10 = paddingTop + f;
        this.shadow.setBounds((int) (f5 - f7), (int) (f8 - f7), (int) (f9 + f7), (int) (f7 + f10));
        this.shadow.draw(canvas);
        if (this.delegate.drawBackground()) {
            this.rectF.set(f5, f8, f9, f10);
            this.delegate.drawRoundRect(canvas, this.rectF, f, getX(), getY(), i, false);
        } else {
            canvas.drawCircle(width, paddingTop, f, this.bgPaint);
        }
        float width2 = ((LocaleController.isRTL || this.mirrorX) ? this.bigCircleOffset - this.bigCircleRadius : (getWidth() - this.bigCircleOffset) + this.bigCircleRadius) + this.bubblesOffset + this.miniBubblesOffset;
        float lerp = AndroidUtilities.lerp(this.isTop ? (getPaddingTop() - expandSize()) - AndroidUtilities.dp(16.0f) : ((getHeight() - this.smallCircleRadius) - f6) + expandSize(), (this.smallCircleRadius + f6) - expandSize(), CubicBezierInterpolator.DEFAULT.getInterpolation(this.flipVerticalProgress));
        float f11 = (-AndroidUtilities.dp(1.0f)) * f2;
        this.shadow.setBounds((int) ((width2 - f) - f11), (int) ((lerp - f) - f11), (int) (width2 + f + f11), (int) (lerp + f + f11));
        this.shadow.draw(canvas);
        if (this.delegate.drawBackground()) {
            this.rectF.set(width2 - f3, lerp - f3, width2 + f3, lerp + f3);
            this.delegate.drawRoundRect(canvas, this.rectF, f3, getX(), getY(), i, false);
        } else {
            canvas.drawCircle(width2, lerp, f3, this.bgPaint);
        }
        canvas.restore();
        this.shadow.setAlpha(255);
        this.bgPaint.setAlpha(255);
    }

    public void setMiniBubblesOffset(float f) {
        this.miniBubblesOffset = f;
    }

    private void checkPressedProgressForOtherViews(View view) {
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(view);
        float measuredWidth = ((view.getMeasuredWidth() * (this.pressedViewScale - 1.0f)) / 3.0f) - ((view.getMeasuredWidth() * (1.0f - this.otherViewsScale)) * (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1));
        if (childAdapterPosition < this.pressedReactionPosition) {
            view.setPivotX(0.0f);
            view.setTranslationX(-measuredWidth);
        } else {
            view.setPivotX(view.getMeasuredWidth());
            view.setTranslationX(measuredWidth);
        }
        view.setScaleX(this.otherViewsScale);
        view.setScaleY(this.otherViewsScale);
    }

    private void invalidateEmojis() {
        if (this.type != 4) {
            return;
        }
        invalidate();
        this.recyclerListView.invalidate();
        this.recyclerListView.invalidateViews();
        for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
            View childAt = this.recyclerListView.getChildAt(i);
            if (childAt instanceof ReactionHolderView) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) childAt;
                reactionHolderView.enterImageView.invalidate();
                reactionHolderView.loopImageView.invalidate();
            } else {
                childAt.invalidate();
            }
        }
    }

    private void checkPressedProgress(Canvas canvas, ReactionHolderView reactionHolderView) {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        float clamp = this.pullingLeftOffset != 0.0f ? Utilities.clamp(reactionHolderView.getLeft() / (getMeasuredWidth() - AndroidUtilities.dp(34.0f)), 1.0f, 0.0f) * getPullingLeftProgress() * AndroidUtilities.dp(46.0f) : 0.0f;
        if (reactionHolderView.currentReaction.equals(this.pressedReaction)) {
            BackupImageView backupImageView = reactionHolderView.loopImageView.getVisibility() == 0 ? reactionHolderView.loopImageView : reactionHolderView.enterImageView;
            reactionHolderView.setPivotX(reactionHolderView.getMeasuredWidth() >> 1);
            reactionHolderView.setPivotY(backupImageView.getY() + backupImageView.getMeasuredHeight());
            reactionHolderView.setScaleX(this.pressedViewScale);
            reactionHolderView.setScaleY(this.pressedViewScale);
            if (!this.clicked) {
                if (this.cancelPressedAnimation == null) {
                    reactionHolderView.pressedBackupImageView.setVisibility(0);
                    reactionHolderView.pressedBackupImageView.setAlpha(1.0f);
                    if (reactionHolderView.pressedBackupImageView.getImageReceiver().hasBitmapImage() || ((animatedEmojiDrawable = reactionHolderView.pressedBackupImageView.animatedEmojiDrawable) != null && animatedEmojiDrawable.getImageReceiver() != null && reactionHolderView.pressedBackupImageView.animatedEmojiDrawable.getImageReceiver().hasBitmapImage())) {
                        backupImageView.setAlpha(0.0f);
                    }
                } else {
                    reactionHolderView.pressedBackupImageView.setAlpha(1.0f - this.cancelPressedProgress);
                    backupImageView.setAlpha(this.cancelPressedProgress);
                }
                if (this.pressedProgress == 1.0f) {
                    this.clicked = true;
                    if (System.currentTimeMillis() - this.lastReactionSentTime > 300) {
                        this.lastReactionSentTime = System.currentTimeMillis();
                        this.delegate.onReactionClicked(reactionHolderView, reactionHolderView.currentReaction, true, false);
                    }
                }
            }
            canvas.save();
            float x = this.recyclerListView.getX() + reactionHolderView.getX();
            float measuredWidth = ((reactionHolderView.getMeasuredWidth() * reactionHolderView.getScaleX()) - reactionHolderView.getMeasuredWidth()) / 2.0f;
            float f = x - measuredWidth;
            if (f < 0.0f && reactionHolderView.getTranslationX() >= 0.0f) {
                reactionHolderView.setTranslationX((-f) - clamp);
            } else if (reactionHolderView.getMeasuredWidth() + x + measuredWidth > getMeasuredWidth() && reactionHolderView.getTranslationX() <= 0.0f) {
                reactionHolderView.setTranslationX((((getMeasuredWidth() - x) - reactionHolderView.getMeasuredWidth()) - measuredWidth) - clamp);
            } else {
                reactionHolderView.setTranslationX(0.0f - clamp);
            }
            canvas.translate(this.recyclerListView.getX() + reactionHolderView.getX(), this.recyclerListView.getY() + reactionHolderView.getY());
            canvas.scale(reactionHolderView.getScaleX(), reactionHolderView.getScaleY(), reactionHolderView.getPivotX(), reactionHolderView.getPivotY());
            reactionHolderView.draw(canvas);
            canvas.restore();
            return;
        }
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(reactionHolderView);
        float measuredWidth2 = ((reactionHolderView.getMeasuredWidth() * (this.pressedViewScale - 1.0f)) / 3.0f) - ((reactionHolderView.getMeasuredWidth() * (1.0f - this.otherViewsScale)) * (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1));
        if (childAdapterPosition < this.pressedReactionPosition) {
            reactionHolderView.setPivotX(0.0f);
            reactionHolderView.setTranslationX(-measuredWidth2);
        } else {
            reactionHolderView.setPivotX(reactionHolderView.getMeasuredWidth() - clamp);
            reactionHolderView.setTranslationX(measuredWidth2 - clamp);
        }
        reactionHolderView.setPivotY(reactionHolderView.enterImageView.getY() + reactionHolderView.enterImageView.getMeasuredHeight());
        reactionHolderView.setScaleX(this.otherViewsScale);
        reactionHolderView.setScaleY(this.otherViewsScale);
        reactionHolderView.pressedBackupImageView.setVisibility(4);
        reactionHolderView.enterImageView.setAlpha(1.0f);
    }

    public float getPullingLeftProgress() {
        return Utilities.clamp(this.pullingLeftOffset / AndroidUtilities.dp(42.0f), 2.0f, 0.0f);
    }

    @Override
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateShaders();
    }

    private void invalidateShaders() {
        int dp = AndroidUtilities.dp(24.0f);
        float height = getHeight() / 2.0f;
        int color = Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground);
        this.leftShadowPaint.setShader(new LinearGradient(0.0f, height, dp, height, color, 0, Shader.TileMode.CLAMP));
        this.rightShadowPaint.setShader(new LinearGradient(getWidth(), height, getWidth() - dp, height, color, 0, Shader.TileMode.CLAMP));
        invalidate();
    }

    public void setTransitionProgress(float f) {
        this.transitionProgress = f;
        ChatScrimPopupContainerLayout chatScrimPopupContainerLayout = this.parentLayout;
        if (chatScrimPopupContainerLayout != null) {
            if (!this.animatePopup || !allowSmoothEnterTransition()) {
                f = 1.0f;
            }
            chatScrimPopupContainerLayout.setReactionsTransitionProgress(f);
        }
        invalidate();
    }

    public void setMessage(MessageObject messageObject, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions;
        TLRPC$Message tLRPC$Message;
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions2;
        this.messageObject = messageObject;
        this.hitLimit = (this.type != 0 || messageObject == null || (tLRPC$Message = messageObject.messageOwner) == null || (tLRPC$TL_messageReactions2 = tLRPC$Message.reactions) == null || tLRPC$TL_messageReactions2.results.size() < MessagesController.getInstance(this.currentAccount).getChatMaxUniqReactions(this.messageObject.getDialogId())) ? false : true;
        ArrayList arrayList = new ArrayList();
        if (messageObject != null && messageObject.isForwardedChannelPost() && (tLRPC$ChatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-messageObject.getFromChatId())) == null) {
            this.waitingLoadingChatId = -messageObject.getFromChatId();
            MessagesController.getInstance(this.currentAccount).loadFullChat(-messageObject.getFromChatId(), 0, true);
            setVisibility(4);
            return;
        }
        int i = this.type;
        if (i == 3) {
            this.allReactionsAvailable = UserConfig.getInstance(this.currentAccount).isPremium();
            fillRecentReactionsList(arrayList);
        } else if (i == 5) {
            this.allReactionsAvailable = true;
            fillRecentReactionsList(arrayList);
        } else if (this.hitLimit) {
            this.allReactionsAvailable = false;
            Iterator<TLRPC$ReactionCount> it = this.messageObject.messageOwner.reactions.results.iterator();
            while (it.hasNext()) {
                arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(it.next().reaction));
            }
        } else if (tLRPC$ChatFull != null) {
            TLRPC$ChatReactions tLRPC$ChatReactions = tLRPC$ChatFull.available_reactions;
            if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsAll) {
                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$ChatFull.id));
                if (chat != null && !ChatObject.isChannelAndNotMegaGroup(chat)) {
                    this.allReactionsAvailable = true;
                } else {
                    this.allReactionsAvailable = false;
                }
                fillRecentReactionsList(arrayList);
            } else if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsSome) {
                Iterator<TLRPC$Reaction> it2 = ((TLRPC$TL_chatReactionsSome) tLRPC$ChatReactions).reactions.iterator();
                while (it2.hasNext()) {
                    TLRPC$Reaction next = it2.next();
                    Iterator<TLRPC$TL_availableReaction> it3 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList().iterator();
                    while (true) {
                        if (it3.hasNext()) {
                            TLRPC$TL_availableReaction next2 = it3.next();
                            if ((next instanceof TLRPC$TL_reactionEmoji) && next2.reaction.equals(((TLRPC$TL_reactionEmoji) next).emoticon)) {
                                arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(next));
                                break;
                            } else if (next instanceof TLRPC$TL_reactionCustomEmoji) {
                                arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(next));
                                break;
                            }
                        }
                    }
                }
            } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                throw new RuntimeException("Unknown chat reactions type: " + tLRPC$ChatFull.available_reactions);
            }
        } else {
            this.allReactionsAvailable = true;
            fillRecentReactionsList(arrayList);
        }
        filterReactions(arrayList);
        this.showExpandableReactions = !this.hitLimit && ((!this.allReactionsAvailable && arrayList.size() > 16) || (this.allReactionsAvailable && !UserConfig.getInstance(this.currentAccount).isPremium() && MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()));
        if (this.type == 3 && !UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.showExpandableReactions = false;
        }
        if (this.type == 4) {
            this.showExpandableReactions = true;
        }
        setVisibleReactionsList(arrayList, z);
        if (messageObject == null || (tLRPC$TL_messageReactions = messageObject.messageOwner.reactions) == null || tLRPC$TL_messageReactions.results == null) {
            return;
        }
        for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
            if (messageObject.messageOwner.reactions.results.get(i2).chosen) {
                this.selectedReactions.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(messageObject.messageOwner.reactions.results.get(i2).reaction));
            }
        }
    }

    public void setStoryItem(TL_stories$StoryItem tL_stories$StoryItem) {
        TLRPC$Reaction tLRPC$Reaction;
        this.selectedReactions.clear();
        if (tL_stories$StoryItem != null && (tLRPC$Reaction = tL_stories$StoryItem.sent_reaction) != null) {
            this.selectedReactions.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(tLRPC$Reaction));
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void setSelectedReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        this.selectedReactions.clear();
        if (visibleReaction != null) {
            this.selectedReactions.add(visibleReaction);
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void setSelectedReactionAnimated(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        this.selectedReactions.clear();
        if (visibleReaction != null) {
            this.selectedReactions.add(visibleReaction);
        }
        updateSelected(true);
    }

    public void setSelectedReactions(ArrayList<MessageObject> arrayList) {
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions;
        this.selectedReactions.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = arrayList.get(i);
            if (messageObject != null && (tLRPC$TL_messageReactions = messageObject.messageOwner.reactions) != null && tLRPC$TL_messageReactions.results != null) {
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    if (messageObject.messageOwner.reactions.results.get(i2).chosen) {
                        this.selectedReactions.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(messageObject.messageOwner.reactions.results.get(i2).reaction));
                    }
                }
            }
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public HashSet<ReactionsLayoutInBubble.VisibleReaction> getSelectedReactions() {
        return this.selectedReactions;
    }

    public String getSelectedEmoji() {
        TLRPC$Document findDocument;
        if (this.selectedReactions.isEmpty()) {
            return "";
        }
        ReactionsLayoutInBubble.VisibleReaction next = this.selectedReactions.iterator().next();
        long j = next.documentId;
        String str = null;
        if (j != 0 && (findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, j)) != null) {
            str = MessageObject.findAnimatedEmojiEmoticon(findDocument, null);
        }
        if (TextUtils.isEmpty(str)) {
            str = next.emojicon;
        }
        return TextUtils.isEmpty(str) ? "👍" : str;
    }

    public static HashSet<ReactionsLayoutInBubble.VisibleReaction> getInclusiveReactions(ArrayList<MessageObject> arrayList) {
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions;
        LongSparseArray longSparseArray = new LongSparseArray();
        HashSet hashSet = new HashSet();
        int i = 0;
        boolean z = true;
        while (i < arrayList.size()) {
            MessageObject messageObject = arrayList.get(i);
            hashSet.clear();
            if (messageObject != null && (tLRPC$TL_messageReactions = messageObject.messageOwner.reactions) != null && tLRPC$TL_messageReactions.results != null) {
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    if (messageObject.messageOwner.reactions.results.get(i2).chosen) {
                        ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(messageObject.messageOwner.reactions.results.get(i2).reaction);
                        if (z || longSparseArray.indexOfKey(fromTL.hash) >= 0) {
                            hashSet.add(Long.valueOf(fromTL.hash));
                            longSparseArray.put(fromTL.hash, fromTL);
                        }
                    }
                }
            }
            int i3 = 0;
            while (i3 < longSparseArray.size()) {
                if (!hashSet.contains(Long.valueOf(longSparseArray.keyAt(i3)))) {
                    longSparseArray.removeAt(i3);
                    i3--;
                }
                i3++;
            }
            i++;
            z = false;
        }
        HashSet<ReactionsLayoutInBubble.VisibleReaction> hashSet2 = new HashSet<>();
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            if (longSparseArray.valueAt(i4) != null) {
                hashSet2.add((ReactionsLayoutInBubble.VisibleReaction) longSparseArray.valueAt(i4));
            }
        }
        return hashSet2;
    }

    public void setSelectedReactionsInclusive(ArrayList<MessageObject> arrayList) {
        this.selectedReactions.clear();
        this.selectedReactions.addAll(getInclusiveReactions(arrayList));
        updateSelected(true);
    }

    public void setSelectedReactionInclusive(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        this.selectedReactions.clear();
        if (visibleReaction != null) {
            this.selectedReactions.add(visibleReaction);
        }
        updateSelected(true);
    }

    public void setSelectedEmojis(ArrayList<String> arrayList) {
        this.selectedReactions.clear();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            ReactionsLayoutInBubble.VisibleReaction fromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(it.next());
            if (fromEmojicon != null) {
                this.selectedReactions.add(fromEmojicon);
                this.alwaysSelectedReactions.add(fromEmojicon);
            }
        }
        updateSelected(true);
    }

    private void updateSelected(final boolean z) {
        AndroidUtilities.forEachViews((RecyclerView) this.recyclerListView, (Consumer<View>) new Consumer() {
            @Override
            public final void accept(Object obj) {
                ReactionsContainerLayout.this.lambda$updateSelected$4(z, (View) obj);
            }
        });
    }

    public void lambda$updateSelected$4(boolean z, View view) {
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(view);
        if (childAdapterPosition < 0 || childAdapterPosition >= this.items.size() || !(view instanceof ReactionHolderView)) {
            return;
        }
        ((ReactionHolderView) view).updateSelected(this.items.get(childAdapterPosition).reaction, z);
    }

    private void filterReactions(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        HashSet hashSet = new HashSet();
        int i = 0;
        while (i < list.size()) {
            if (hashSet.contains(list.get(i))) {
                i--;
                list.remove(i);
            } else {
                hashSet.add(list.get(i));
            }
            i++;
        }
    }

    private void fillRecentReactionsList(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        int i;
        ArrayList<TLRPC$Reaction> topReactions;
        HashSet hashSet = new HashSet();
        int i2 = 0;
        if (this.type == 0) {
            ReactionsLayoutInBubble.VisibleReaction asStar = ReactionsLayoutInBubble.VisibleReaction.asStar();
            hashSet.add(asStar);
            list.add(asStar);
            i = 1;
        } else {
            i = 0;
        }
        int i3 = this.type;
        if (i3 == 4) {
            Iterator<ReactionsLayoutInBubble.VisibleReaction> it = this.selectedReactions.iterator();
            while (it.hasNext()) {
                ReactionsLayoutInBubble.VisibleReaction next = it.next();
                if (!hashSet.contains(next)) {
                    hashSet.add(next);
                    list.add(next);
                    i++;
                    if (i >= 8) {
                        return;
                    }
                }
            }
            List<TLRPC$TL_availableReaction> enabledReactionsList = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            while (i2 < enabledReactionsList.size()) {
                ReactionsLayoutInBubble.VisibleReaction fromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList.get(i2));
                if (!hashSet.contains(fromEmojicon)) {
                    hashSet.add(fromEmojicon);
                    list.add(fromEmojicon);
                    i++;
                    if (i >= 8) {
                        return;
                    }
                }
                i2++;
            }
            return;
        }
        if (!this.allReactionsAvailable || i3 == 4) {
            if (i3 == 3) {
                ArrayList<TLRPC$Reaction> savedReactions = MediaDataController.getInstance(this.currentAccount).getSavedReactions();
                while (i2 < savedReactions.size()) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(savedReactions.get(i2));
                    if (!hashSet.contains(fromTL)) {
                        hashSet.add(fromTL);
                        list.add(fromTL);
                        i++;
                    }
                    if (i == 16) {
                        return;
                    } else {
                        i2++;
                    }
                }
                return;
            }
            List<TLRPC$TL_availableReaction> enabledReactionsList2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            while (i2 < enabledReactionsList2.size()) {
                list.add(ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList2.get(i2)));
                i2++;
            }
            return;
        }
        if (i3 == 5) {
            TLRPC$messages_AvailableEffects availableEffects = MessagesController.getInstance(this.currentAccount).getAvailableEffects();
            if (availableEffects != null) {
                while (i2 < availableEffects.effects.size()) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL2 = ReactionsLayoutInBubble.VisibleReaction.fromTL(availableEffects.effects.get(i2));
                    if (!hashSet.contains(fromTL2)) {
                        hashSet.add(fromTL2);
                        list.add(fromTL2);
                    }
                    i2++;
                }
                return;
            }
            return;
        }
        if (i3 == 3) {
            topReactions = MediaDataController.getInstance(this.currentAccount).getSavedReactions();
        } else {
            topReactions = MediaDataController.getInstance(this.currentAccount).getTopReactions();
        }
        if (this.type == 3) {
            TLRPC$TL_messages_savedReactionsTags savedReactionTags = MessagesController.getInstance(this.currentAccount).getSavedReactionTags(0L);
            if (savedReactionTags != null) {
                for (int i4 = 0; i4 < savedReactionTags.tags.size(); i4++) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL3 = ReactionsLayoutInBubble.VisibleReaction.fromTL(savedReactionTags.tags.get(i4).reaction);
                    if (!hashSet.contains(fromTL3)) {
                        hashSet.add(fromTL3);
                        list.add(fromTL3);
                    }
                }
            }
            for (int i5 = 0; i5 < topReactions.size(); i5++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL4 = ReactionsLayoutInBubble.VisibleReaction.fromTL(topReactions.get(i5));
                if (!hashSet.contains(fromTL4)) {
                    hashSet.add(fromTL4);
                    list.add(fromTL4);
                }
            }
        } else {
            for (int i6 = 0; i6 < topReactions.size(); i6++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL5 = ReactionsLayoutInBubble.VisibleReaction.fromTL(topReactions.get(i6));
                if (!hashSet.contains(fromTL5) && (this.type == 3 || UserConfig.getInstance(this.currentAccount).isPremium() || fromTL5.documentId == 0)) {
                    hashSet.add(fromTL5);
                    list.add(fromTL5);
                }
            }
        }
        if (this.type != 3 || UserConfig.getInstance(this.currentAccount).isPremium()) {
            ArrayList<TLRPC$Reaction> recentReactions = MediaDataController.getInstance(this.currentAccount).getRecentReactions();
            for (int i7 = 0; i7 < recentReactions.size(); i7++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL6 = ReactionsLayoutInBubble.VisibleReaction.fromTL(recentReactions.get(i7));
                if (!hashSet.contains(fromTL6)) {
                    hashSet.add(fromTL6);
                    list.add(fromTL6);
                }
            }
            List<TLRPC$TL_availableReaction> enabledReactionsList3 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            while (i2 < enabledReactionsList3.size()) {
                ReactionsLayoutInBubble.VisibleReaction fromEmojicon2 = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList3.get(i2));
                if (!hashSet.contains(fromEmojicon2)) {
                    hashSet.add(fromEmojicon2);
                    list.add(fromEmojicon2);
                }
                i2++;
            }
        }
    }

    public void startEnterAnimation(boolean z) {
        ObjectAnimator duration;
        this.animatePopup = z;
        setTransitionProgress(0.0f);
        setAlpha(1.0f);
        this.notificationsLocker.lock();
        if (allowSmoothEnterTransition()) {
            duration = ObjectAnimator.ofFloat(this, TRANSITION_PROGRESS_VALUE, 0.0f, 1.0f).setDuration(250L);
            duration.setInterpolator(new OvershootInterpolator(0.5f));
        } else {
            duration = ObjectAnimator.ofFloat(this, TRANSITION_PROGRESS_VALUE, 0.0f, 1.0f).setDuration(250L);
            duration.setInterpolator(new OvershootInterpolator(0.5f));
        }
        duration.addListener(new AnimatorListenerAdapter() {
            AnonymousClass7() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                ReactionsContainerLayout.this.notificationsLocker.unlock();
            }
        });
        duration.start();
    }

    public class AnonymousClass7 extends AnimatorListenerAdapter {
        AnonymousClass7() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            ReactionsContainerLayout.this.notificationsLocker.unlock();
        }
    }

    public int getTotalWidth() {
        int itemsCount = getItemsCount();
        if (!showCustomEmojiReaction()) {
            return (AndroidUtilities.dp(36.0f) * itemsCount) + (AndroidUtilities.dp(2.0f) * (itemsCount - 1)) + AndroidUtilities.dp(16.0f);
        }
        return (AndroidUtilities.dp(36.0f) * itemsCount) - AndroidUtilities.dp(4.0f);
    }

    public int getHintTextWidth() {
        return this.hintViewWidth;
    }

    public int getItemsCount() {
        return this.visibleReactionsList.size() + (showCustomEmojiReaction() ? 1 : 0) + 1;
    }

    public void setCustomEmojiEnterProgress(float f) {
        this.customEmojiReactionsEnterProgress = f;
        ChatScrimPopupContainerLayout chatScrimPopupContainerLayout = this.chatScrimPopupContainerLayout;
        if (chatScrimPopupContainerLayout != null) {
            chatScrimPopupContainerLayout.setPopupAlpha(1.0f - f);
        }
        invalidate();
    }

    public void dismissParent(boolean z) {
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow != null) {
            customEmojiReactionsWindow.dismiss(z);
            this.reactionsWindow = null;
        }
    }

    public void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null) {
            reactionsContainerDelegate.onReactionClicked(view, visibleReaction, z, true);
        }
        if (this.type == 5) {
            try {
                performHapticFeedback(3, 1);
            } catch (Exception unused) {
            }
        }
    }

    public void prepareAnimation(boolean z) {
        this.prepareAnimation = z;
        invalidate();
    }

    public void setCustomEmojiReactionsBackground(boolean z) {
        if (z) {
            this.customEmojiReactionsIconView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(28.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_listSelector), 40)));
        } else {
            this.customEmojiReactionsIconView.setBackground(null);
        }
    }

    public void setSkipDraw(boolean z) {
        if (this.skipDraw != z) {
            this.skipDraw = z;
            if (!z) {
                for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                    if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                        ReactionHolderView reactionHolderView = (ReactionHolderView) this.recyclerListView.getChildAt(i);
                        if (reactionHolderView.hasEnterAnimation && (reactionHolderView.loopImageView.getImageReceiver().getLottieAnimation() != null || reactionHolderView.loopImageView.getImageReceiver().getAnimation() != null)) {
                            reactionHolderView.loopImageView.setVisibility(0);
                            reactionHolderView.enterImageView.setVisibility(4);
                            if (reactionHolderView.shouldSwitchToLoopView) {
                                reactionHolderView.switchedToLoopView = true;
                            }
                        }
                        reactionHolderView.invalidate();
                    }
                }
            }
            invalidate();
        }
    }

    public void onCustomEmojiWindowOpened() {
        ValueAnimator valueAnimator = this.pullingDownBackAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.pullingLeftOffset = 0.0f;
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    public void onCustomEmojiWindowClosing() {
        ValueAnimator valueAnimator = this.pullingDownBackAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.pullingLeftOffset = 0.0f;
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    public void setChatScrimView(ChatScrimPopupContainerLayout chatScrimPopupContainerLayout) {
        this.chatScrimPopupContainerLayout = chatScrimPopupContainerLayout;
    }

    public ReactionsContainerDelegate getDelegate() {
        return this.delegate;
    }

    public void setCurrentAccount(int i) {
        this.currentAccount = i;
    }

    public void setFragment(BaseFragment baseFragment) {
        this.fragment = baseFragment;
    }

    public void reset() {
        this.isHiddenNextReaction = true;
        this.pressedReactionPosition = 0;
        this.pressedProgress = 0.0f;
        this.pullingLeftOffset = 0.0f;
        this.pressedReaction = null;
        this.clicked = false;
        AndroidUtilities.forEachViews((RecyclerView) this.recyclerListView, (Consumer<View>) new Consumer() {
            @Override
            public final void accept(Object obj) {
                ReactionsContainerLayout.this.lambda$reset$6((View) obj);
            }
        });
        this.lastVisibleViews.clear();
        this.recyclerListView.invalidate();
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    public void lambda$reset$6(View view) {
        if (view instanceof ReactionHolderView) {
            ReactionHolderView reactionHolderView = (ReactionHolderView) view;
            reactionHolderView.pressed = false;
            reactionHolderView.loopImageView.setAlpha(1.0f);
            if (this.skipEnterAnimation) {
                reactionHolderView.loopImageView.setScaleX(reactionHolderView.enterScale * (reactionHolderView.selected ? 0.76f : 1.0f));
                reactionHolderView.loopImageView.setScaleY(reactionHolderView.enterScale * (reactionHolderView.selected ? 0.76f : 1.0f));
            } else {
                reactionHolderView.resetAnimation();
            }
        }
    }

    public void setHint(CharSequence charSequence) {
        this.hasHint = true;
        if (this.hintView == null) {
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(getContext(), this.resourcesProvider);
            this.hintView = linksTextView;
            linksTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
            this.hintView.setClickable(true);
            this.hintView.setTextSize(1, 12.0f);
            int i = this.type;
            if (i == 1 || i == 2 || i == 4) {
                this.hintView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
                this.hintView.setAlpha(0.5f);
            } else {
                this.hintView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider));
            }
            this.hintView.setGravity(1);
            addView(this.hintView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 6.0f, 0.0f, 0.0f));
        }
        this.hintView.setText(charSequence);
        this.hintMeasured = false;
        ((FrameLayout.LayoutParams) this.nextRecentReaction.getLayoutParams()).topMargin = AndroidUtilities.dp(20.0f);
        ((FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams()).topMargin = AndroidUtilities.dp(20.0f);
    }

    public void measureHint() {
        if (this.hintMeasured || !this.hasHint || getMeasuredWidth() <= 0) {
            return;
        }
        int min = Math.min(AndroidUtilities.dp(320.0f), getMeasuredWidth() - AndroidUtilities.dp(16.0f));
        StaticLayout staticLayout = new StaticLayout(this.hintView.getText(), this.hintView.getPaint(), min, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.hintViewHeight = staticLayout.getHeight();
        this.hintViewWidth = 0;
        for (int i = 0; i < staticLayout.getLineCount(); i++) {
            this.hintViewWidth = Math.max(this.hintViewWidth, (int) Math.ceil(staticLayout.getLineWidth(i)));
        }
        if (staticLayout.getLineCount() > 1 && !this.hintView.getText().toString().contains("\n")) {
            int cutInFancyHalf = HintView2.cutInFancyHalf(this.hintView.getText(), this.hintView.getPaint());
            StaticLayout staticLayout2 = new StaticLayout(this.hintView.getText(), this.hintView.getPaint(), cutInFancyHalf, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.hintViewHeight = staticLayout2.getHeight();
            this.hintViewWidth = 0;
            for (int i2 = 0; i2 < staticLayout2.getLineCount(); i2++) {
                this.hintViewWidth = Math.max(this.hintViewWidth, (int) Math.ceil(staticLayout2.getLineWidth(i2)));
            }
            this.hintView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
            this.hintView.setWidth(AndroidUtilities.dp(48.0f) + cutInFancyHalf);
        } else {
            this.hintView.setWidth(AndroidUtilities.dp(16.0f) + min);
        }
        int max = Math.max(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(7.0f) + this.hintViewHeight);
        int i3 = this.type;
        if (i3 == 1 || i3 == 2) {
            max = AndroidUtilities.dp(20.0f);
        } else {
            getLayoutParams().height = AndroidUtilities.dp(52.0f) + max + AndroidUtilities.dp(22.0f);
        }
        ((FrameLayout.LayoutParams) this.nextRecentReaction.getLayoutParams()).topMargin = max;
        ((FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams()).topMargin = max;
        this.hintMeasured = true;
    }

    public void setTop(boolean z) {
        this.isTop = z;
    }

    public float getTopOffset() {
        if (this.hasHint) {
            return ((FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams()).topMargin;
        }
        return 0.0f;
    }

    public void setBubbleOffset(float f) {
        this.bubblesOffset = f;
    }

    public final class LeftRightShadowsListener extends RecyclerView.OnScrollListener {
        private ValueAnimator leftAnimator;
        private boolean leftVisible;
        private ValueAnimator rightAnimator;
        private boolean rightVisible;

        private LeftRightShadowsListener() {
        }

        LeftRightShadowsListener(ReactionsContainerLayout reactionsContainerLayout, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            boolean z = ReactionsContainerLayout.this.linearLayoutManager.findFirstVisibleItemPosition() != 0;
            if (z != this.leftVisible) {
                ValueAnimator valueAnimator = this.leftAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.leftAnimator = startAnimator(ReactionsContainerLayout.this.leftAlpha, z ? 1.0f : 0.0f, new androidx.core.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$0((Float) obj);
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$1();
                    }
                });
                this.leftVisible = z;
            }
            boolean z2 = ReactionsContainerLayout.this.linearLayoutManager.findLastVisibleItemPosition() != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1;
            if (z2 != this.rightVisible) {
                ValueAnimator valueAnimator2 = this.rightAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.rightAnimator = startAnimator(ReactionsContainerLayout.this.rightAlpha, z2 ? 1.0f : 0.0f, new androidx.core.util.Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$2((Float) obj);
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$3();
                    }
                });
                this.rightVisible = z2;
            }
        }

        public void lambda$onScrolled$0(Float f) {
            ReactionsContainerLayout.this.leftShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.leftAlpha = f.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        public void lambda$onScrolled$1() {
            this.leftAnimator = null;
        }

        public void lambda$onScrolled$2(Float f) {
            ReactionsContainerLayout.this.rightShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.rightAlpha = f.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        public void lambda$onScrolled$3() {
            this.rightAnimator = null;
        }

        private ValueAnimator startAnimator(float f, float f2, final androidx.core.util.Consumer<Float> consumer, Runnable runnable) {
            ValueAnimator duration = ValueAnimator.ofFloat(f, f2).setDuration(Math.abs(f2 - f) * 150.0f);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ReactionsContainerLayout.LeftRightShadowsListener.lambda$startAnimator$4(androidx.core.util.Consumer.this, valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter(this) {
                final Runnable val$onEnd;

                AnonymousClass1(LeftRightShadowsListener this, Runnable runnable2) {
                    r2 = runnable2;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    r2.run();
                }
            });
            duration.start();
            return duration;
        }

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final Runnable val$onEnd;

            AnonymousClass1(LeftRightShadowsListener this, Runnable runnable2) {
                r2 = runnable2;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                r2.run();
            }
        }

        public static void lambda$startAnimator$4(androidx.core.util.Consumer consumer, ValueAnimator valueAnimator) {
            consumer.accept((Float) valueAnimator.getAnimatedValue());
        }
    }

    public final class ReactionHolderView extends FrameLayout {
        public ReactionsLayoutInBubble.VisibleReaction currentReaction;
        public boolean drawSelected;
        public ValueAnimator enterAnimator;
        public BackupImageView enterImageView;
        public float enterScale;
        public boolean hasEnterAnimation;
        private boolean isEnter;
        public boolean isLocked;
        public PremiumLockIconView lockIconView;
        Runnable longPressRunnable;
        public BackupImageView loopImageView;
        Runnable playRunnable;
        public int position;
        private ImageReceiver preloadImageReceiver;
        boolean pressed;
        public BackupImageView pressedBackupImageView;
        float pressedX;
        float pressedY;
        public boolean selected;
        public boolean shouldSwitchToLoopView;
        public float sideScale;
        public boolean switchedToLoopView;
        boolean touchable;
        public boolean waitingAnimation;

        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                if (ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().start();
                }
                ReactionHolderView.this.waitingAnimation = false;
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.currentReaction;
            if (visibleReaction != null) {
                String str = visibleReaction.emojicon;
                if (str != null) {
                    accessibilityNodeInfo.setText(str);
                    accessibilityNodeInfo.setEnabled(true);
                } else {
                    accessibilityNodeInfo.setText(LocaleController.getString(R.string.AccDescrCustomEmoji));
                    accessibilityNodeInfo.setEnabled(true);
                }
            }
        }

        ReactionHolderView(Context context, boolean z) {
            super(context);
            this.preloadImageReceiver = new ImageReceiver();
            this.sideScale = 1.0f;
            this.drawSelected = true;
            this.playRunnable = new Runnable() {
                AnonymousClass1() {
                }

                @Override
                public void run() {
                    if (ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                        ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().start();
                    }
                    ReactionHolderView.this.waitingAnimation = false;
                }
            };
            this.enterScale = 1.0f;
            this.longPressRunnable = new Runnable() {
                AnonymousClass5() {
                }

                @Override
                public void run() {
                    ReactionHolderView.this.performHapticFeedback(0);
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.pressedReactionPosition = reactionsContainerLayout.visibleReactionsList.indexOf(ReactionHolderView.this.currentReaction);
                    ReactionHolderView reactionHolderView = ReactionHolderView.this;
                    ReactionsContainerLayout.this.pressedReaction = reactionHolderView.currentReaction;
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            this.touchable = true;
            this.enterImageView = new AnonymousClass2(context, ReactionsContainerLayout.this);
            this.loopImageView = new BackupImageView(context, ReactionsContainerLayout.this) {
                AnonymousClass3(Context context2, ReactionsContainerLayout reactionsContainerLayout) {
                    super(context2);
                }

                @Override
                public void onDraw(Canvas canvas) {
                    ReactionHolderView.this.checkPlayLoopImage();
                    super.onDraw(canvas);
                }

                class AnonymousClass1 extends ImageReceiver {
                    AnonymousClass1(AnonymousClass3 anonymousClass3, View view) {
                        super(view);
                    }

                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                        boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z, i2);
                        if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                            rLottieDrawable.setCurrentFrame(0, false, true);
                            rLottieDrawable.stop();
                        }
                        return imageBitmapByKey;
                    }
                }

                @Override
                protected ImageReceiver createImageReciever() {
                    return new ImageReceiver(this, this) {
                        AnonymousClass1(View this, View this) {
                            super(this);
                        }

                        @Override
                        public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z2, int i2) {
                            boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z2, i2);
                            if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                                rLottieDrawable.setCurrentFrame(0, false, true);
                                rLottieDrawable.stop();
                            }
                            return imageBitmapByKey;
                        }
                    };
                }

                @Override
                public void invalidate() {
                    if (HwEmojis.grabIfWeakDevice(this)) {
                        return;
                    }
                    super.invalidate();
                }

                @Override
                public void invalidate(int i, int i2, int i3, int i4) {
                    if (HwEmojis.grabIfWeakDevice(this)) {
                        return;
                    }
                    super.invalidate(i, i2, i3, i4);
                }
            };
            this.enterImageView.getImageReceiver().setAutoRepeat(0);
            this.enterImageView.getImageReceiver().setAllowStartLottieAnimation(false);
            this.pressedBackupImageView = new BackupImageView(context2, ReactionsContainerLayout.this) {
                AnonymousClass4(Context context2, ReactionsContainerLayout reactionsContainerLayout) {
                    super(context2);
                }

                @Override
                public void onDraw(Canvas canvas) {
                    AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
                    ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
                    if (imageReceiver != null && imageReceiver.getLottieAnimation() != null) {
                        imageReceiver.getLottieAnimation().start();
                    }
                    super.onDraw(canvas);
                }

                @Override
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            addView(this.enterImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.pressedBackupImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.loopImageView, LayoutHelper.createFrame(34, 34, 17));
            if (ReactionsContainerLayout.this.type == 4) {
                LayoutTransition layoutTransition = new LayoutTransition();
                layoutTransition.setDuration(100L);
                layoutTransition.enableTransitionType(4);
                setLayoutTransition(layoutTransition);
            }
            this.enterImageView.setLayerNum(ConnectionsManager.DEFAULT_DATACENTER_ID);
            this.loopImageView.setLayerNum(ConnectionsManager.DEFAULT_DATACENTER_ID);
            this.loopImageView.imageReceiver.setAutoRepeat(0);
            this.loopImageView.imageReceiver.setAllowStartAnimation(false);
            this.loopImageView.imageReceiver.setAllowStartLottieAnimation(false);
            this.pressedBackupImageView.setLayerNum(ConnectionsManager.DEFAULT_DATACENTER_ID);
        }

        public class AnonymousClass2 extends BackupImageView {
            AnonymousClass2(Context context, ReactionsContainerLayout reactionsContainerLayout) {
                super(context);
            }

            class AnonymousClass1 extends ImageReceiver {
                AnonymousClass1(AnonymousClass2 anonymousClass2, View view) {
                    super(view);
                }

                @Override
                public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                    if (drawable instanceof RLottieDrawable) {
                        ((RLottieDrawable) drawable).setCurrentFrame(0, false, true);
                    }
                    return super.setImageBitmapByKey(drawable, str, i, z, i2);
                }
            }

            @Override
            protected ImageReceiver createImageReciever() {
                return new ImageReceiver(this, this) {
                    AnonymousClass1(View this, View this) {
                        super(this);
                    }

                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                        if (drawable instanceof RLottieDrawable) {
                            ((RLottieDrawable) drawable).setCurrentFrame(0, false, true);
                        }
                        return super.setImageBitmapByKey(drawable, str, i, z, i2);
                    }
                };
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (this.imageReceiver.getLottieAnimation() != null && !ReactionHolderView.this.waitingAnimation) {
                    this.imageReceiver.getLottieAnimation().start();
                }
                ReactionHolderView reactionHolderView = ReactionHolderView.this;
                if (reactionHolderView.shouldSwitchToLoopView && !reactionHolderView.switchedToLoopView && this.imageReceiver.getLottieAnimation() != null && this.imageReceiver.getLottieAnimation().isLastFrame() && ReactionHolderView.this.loopImageView.imageReceiver.getLottieAnimation() != null && ReactionHolderView.this.loopImageView.imageReceiver.getLottieAnimation().hasBitmap()) {
                    ReactionHolderView reactionHolderView2 = ReactionHolderView.this;
                    reactionHolderView2.switchedToLoopView = true;
                    reactionHolderView2.loopImageView.imageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
                    ReactionHolderView.this.loopImageView.setVisibility(0);
                    if (ReactionsContainerLayout.this.onSwitchedToLoopView != null) {
                        ReactionsContainerLayout.this.onSwitchedToLoopView.run();
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            ReactionsContainerLayout.ReactionHolderView.AnonymousClass2.this.lambda$dispatchDraw$0();
                        }
                    });
                }
                invalidate();
            }

            public void lambda$dispatchDraw$0() {
                ReactionHolderView.this.enterImageView.setVisibility(4);
            }

            @Override
            public void invalidate() {
                if (HwEmojis.grabIfWeakDevice(this, ReactionsContainerLayout.this)) {
                    return;
                }
                super.invalidate();
                ReactionsContainerLayout.this.invalidate();
            }

            @Override
            public void invalidate(android.graphics.Rect rect) {
                if (HwEmojis.grabIfWeakDevice(this, ReactionsContainerLayout.this)) {
                    return;
                }
                super.invalidate(rect);
                ReactionsContainerLayout.this.invalidate();
            }

            @Override
            public void invalidate(int i, int i2, int i3, int i4) {
                if (HwEmojis.grabIfWeakDevice(this)) {
                    return;
                }
                super.invalidate(i, i2, i3, i4);
            }
        }

        public class AnonymousClass3 extends BackupImageView {
            AnonymousClass3(Context context2, ReactionsContainerLayout reactionsContainerLayout) {
                super(context2);
            }

            @Override
            public void onDraw(Canvas canvas) {
                ReactionHolderView.this.checkPlayLoopImage();
                super.onDraw(canvas);
            }

            class AnonymousClass1 extends ImageReceiver {
                AnonymousClass1(View this, View this) {
                    super(this);
                }

                @Override
                public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z2, int i2) {
                    boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z2, i2);
                    if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                        RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                        rLottieDrawable.setCurrentFrame(0, false, true);
                        rLottieDrawable.stop();
                    }
                    return imageBitmapByKey;
                }
            }

            @Override
            protected ImageReceiver createImageReciever() {
                return new ImageReceiver(this, this) {
                    AnonymousClass1(View this, View this) {
                        super(this);
                    }

                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z2, int i2) {
                        boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z2, i2);
                        if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                            rLottieDrawable.setCurrentFrame(0, false, true);
                            rLottieDrawable.stop();
                        }
                        return imageBitmapByKey;
                    }
                };
            }

            @Override
            public void invalidate() {
                if (HwEmojis.grabIfWeakDevice(this)) {
                    return;
                }
                super.invalidate();
            }

            @Override
            public void invalidate(int i, int i2, int i3, int i4) {
                if (HwEmojis.grabIfWeakDevice(this)) {
                    return;
                }
                super.invalidate(i, i2, i3, i4);
            }
        }

        public class AnonymousClass4 extends BackupImageView {
            AnonymousClass4(Context context2, ReactionsContainerLayout reactionsContainerLayout) {
                super(context2);
            }

            @Override
            public void onDraw(Canvas canvas) {
                AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
                ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
                if (imageReceiver != null && imageReceiver.getLottieAnimation() != null) {
                    imageReceiver.getLottieAnimation().start();
                }
                super.onDraw(canvas);
            }

            @Override
            public void invalidate() {
                super.invalidate();
                ReactionsContainerLayout.this.invalidate();
            }
        }

        public void updateSelected(ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z) {
            boolean z2 = this.selected;
            boolean contains = ReactionsContainerLayout.this.selectedReactions.contains(visibleReaction);
            this.selected = contains;
            if (contains != z2) {
                if (z) {
                    ViewPropertyAnimator duration = this.loopImageView.animate().scaleX(this.enterScale * (this.selected ? 0.76f : 1.0f)).scaleY(this.enterScale * (this.selected ? 0.76f : 1.0f)).setDuration(240L);
                    CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                    duration.setInterpolator(cubicBezierInterpolator).start();
                    this.enterImageView.animate().scaleX(this.enterScale * (this.selected ? 0.76f : 1.0f)).scaleY(this.enterScale * (this.selected ? 0.76f : 1.0f)).setDuration(240L).setInterpolator(cubicBezierInterpolator).start();
                } else {
                    this.loopImageView.setScaleX(this.enterScale * (contains ? 0.76f : 1.0f));
                    this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.enterImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.enterImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                }
                requestLayout();
                invalidate();
            }
        }

        public void setReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction, int i) {
            updateSelected(visibleReaction, false);
            ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = this.currentReaction;
            if (visibleReaction2 == null || !visibleReaction2.equals(visibleReaction)) {
                boolean isPremium = UserConfig.getInstance(ReactionsContainerLayout.this.currentAccount).isPremium();
                boolean z = (ReactionsContainerLayout.this.type == 3 && !isPremium) || (ReactionsContainerLayout.this.type == 5 && visibleReaction.premium && !isPremium);
                this.isLocked = z;
                if (z && this.lockIconView == null) {
                    PremiumLockIconView premiumLockIconView = new PremiumLockIconView(getContext(), PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
                    this.lockIconView = premiumLockIconView;
                    premiumLockIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    this.lockIconView.setImageReceiver(this.loopImageView.getImageReceiver());
                    addView(this.lockIconView, LayoutHelper.createFrame(18, 18.0f, 17, 8.0f, 8.0f, 0.0f, 0.0f));
                }
                PremiumLockIconView premiumLockIconView2 = this.lockIconView;
                if (premiumLockIconView2 != null) {
                    premiumLockIconView2.setVisibility(this.isLocked ? 0 : 8);
                }
                resetAnimation();
                this.currentReaction = visibleReaction;
                this.hasEnterAnimation = visibleReaction.emojicon != null && (ReactionsContainerLayout.this.showCustomEmojiReaction() || ReactionsContainerLayout.this.allReactionsIsDefault) && LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_EMOJI_REACTIONS);
                if (ReactionsContainerLayout.this.type == 4 || this.currentReaction.isEffect) {
                    this.hasEnterAnimation = false;
                }
                ReactionsLayoutInBubble.VisibleReaction visibleReaction3 = this.currentReaction;
                if (visibleReaction3.isStar || visibleReaction3.emojicon != null) {
                    updateImage(visibleReaction);
                    this.pressedBackupImageView.setAnimatedEmojiDrawable(null);
                    if (this.enterImageView.getImageReceiver().getLottieAnimation() != null) {
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    }
                    PremiumLockIconView premiumLockIconView3 = this.lockIconView;
                    if (premiumLockIconView3 != null) {
                        premiumLockIconView3.setAnimatedEmojiDrawable(null);
                    }
                } else {
                    this.pressedBackupImageView.getImageReceiver().clearImage();
                    this.loopImageView.getImageReceiver().clearImage();
                    AnimatedEmojiDrawable animatedEmojiDrawable = new AnimatedEmojiDrawable(4, ReactionsContainerLayout.this.currentAccount, this.currentReaction.documentId);
                    AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(3, ReactionsContainerLayout.this.currentAccount, this.currentReaction.documentId);
                    if (ReactionsContainerLayout.this.type == 1 || ReactionsContainerLayout.this.type == 2 || ReactionsContainerLayout.this.type == 4) {
                        animatedEmojiDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                        animatedEmojiDrawable2.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                    } else {
                        int i2 = Theme.key_windowBackgroundWhiteBlueIcon;
                        animatedEmojiDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, ReactionsContainerLayout.this.resourcesProvider), PorterDuff.Mode.SRC_IN));
                        animatedEmojiDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, ReactionsContainerLayout.this.resourcesProvider), PorterDuff.Mode.SRC_IN));
                    }
                    this.pressedBackupImageView.setAnimatedEmojiDrawable(animatedEmojiDrawable);
                    this.loopImageView.setAnimatedEmojiDrawable(animatedEmojiDrawable2);
                    PremiumLockIconView premiumLockIconView4 = this.lockIconView;
                    if (premiumLockIconView4 != null) {
                        premiumLockIconView4.setAnimatedEmojiDrawable(animatedEmojiDrawable2);
                    }
                }
                setFocusable(true);
                boolean z2 = this.hasEnterAnimation;
                this.shouldSwitchToLoopView = z2;
                if (!z2) {
                    this.enterImageView.setVisibility(8);
                    this.loopImageView.setVisibility(0);
                    this.switchedToLoopView = true;
                } else {
                    this.switchedToLoopView = false;
                    this.enterImageView.setVisibility(0);
                    this.loopImageView.setVisibility(8);
                }
                ViewGroup.LayoutParams layoutParams = this.loopImageView.getLayoutParams();
                ViewGroup.LayoutParams layoutParams2 = this.loopImageView.getLayoutParams();
                int dp = AndroidUtilities.dp(34.0f);
                layoutParams2.height = dp;
                layoutParams.width = dp;
                ViewGroup.LayoutParams layoutParams3 = this.enterImageView.getLayoutParams();
                ViewGroup.LayoutParams layoutParams4 = this.enterImageView.getLayoutParams();
                int dp2 = AndroidUtilities.dp(34.0f);
                layoutParams4.height = dp2;
                layoutParams3.width = dp2;
                return;
            }
            this.position = i;
            updateImage(visibleReaction);
        }

        private void updateImage(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            if (visibleReaction == null || !visibleReaction.isStar) {
                if (ReactionsContainerLayout.this.type == 4 && visibleReaction != null && visibleReaction.emojicon != null) {
                    this.enterImageView.getImageReceiver().setImageBitmap(Emoji.getEmojiDrawable(visibleReaction.emojicon));
                    this.loopImageView.getImageReceiver().setImageBitmap(Emoji.getEmojiDrawable(visibleReaction.emojicon));
                    return;
                }
                ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = this.currentReaction;
                if (visibleReaction2.isEffect) {
                    TLRPC$Document effectDocument = MessagesController.getInstance(ReactionsContainerLayout.this.currentAccount).getEffectDocument(this.currentReaction.documentId);
                    this.loopImageView.getImageReceiver().setImage(ImageLocation.getForDocument(effectDocument), "60_60_firstframe", null, null, this.hasEnterAnimation ? null : DocumentObject.getSvgThumb(effectDocument, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f), 0L, "tgs", this.currentReaction, 0);
                    return;
                }
                if (visibleReaction2.emojicon != null) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(ReactionsContainerLayout.this.currentAccount).getReactionsMap().get(this.currentReaction.emojicon);
                    if (tLRPC$TL_availableReaction != null) {
                        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.activate_animation, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f);
                        if (!LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_EMOJI_REACTIONS) || ReactionsContainerLayout.this.type == 4) {
                            if (SharedConfig.getDevicePerformanceClass() <= 0 || ReactionsContainerLayout.this.type == 4) {
                                this.loopImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_firstframe", null, null, this.hasEnterAnimation ? null : svgThumb, 0L, "tgs", this.currentReaction, 0);
                            } else {
                                this.enterImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.appear_animation), "30_30_nolimit", null, null, svgThumb, 0L, "tgs", visibleReaction, 0);
                                this.loopImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_firstframe", null, null, this.hasEnterAnimation ? null : svgThumb, 0L, "tgs", this.currentReaction, 0);
                            }
                        } else {
                            this.enterImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.appear_animation), "30_30_nolimit", null, null, svgThumb, 0L, "tgs", visibleReaction, 0);
                            this.loopImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_pcache", null, null, this.hasEnterAnimation ? null : svgThumb, 0L, "tgs", this.currentReaction, 0);
                        }
                        if (this.enterImageView.getImageReceiver().getLottieAnimation() != null) {
                            this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false, true);
                        }
                        this.pressedBackupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_pcache", null, null, svgThumb, 0L, "tgs", visibleReaction, 0);
                        this.preloadImageReceiver.setAllowStartLottieAnimation(false);
                        MediaDataController.getInstance(ReactionsContainerLayout.this.currentAccount).preloadImage(this.preloadImageReceiver, ImageLocation.getForDocument(tLRPC$TL_availableReaction.around_animation), ReactionsEffectOverlay.getFilterForAroundAnimation());
                    }
                    PremiumLockIconView premiumLockIconView = this.lockIconView;
                    if (premiumLockIconView != null) {
                        premiumLockIconView.setImageReceiver(this.loopImageView.getImageReceiver());
                        return;
                    }
                    return;
                }
                return;
            }
            Drawable mutate = getContext().getResources().getDrawable(R.drawable.star_small_inner).mutate();
            this.enterImageView.getImageReceiver().setImageBitmap(mutate);
            this.loopImageView.getImageReceiver().setImageBitmap(mutate);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            resetAnimation();
            this.preloadImageReceiver.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.preloadImageReceiver.onDetachedFromWindow();
        }

        public boolean play(int i) {
            if (!ReactionsContainerLayout.this.animationEnabled) {
                resetAnimation();
                this.isEnter = true;
                if (!this.hasEnterAnimation) {
                    this.loopImageView.setVisibility(0);
                    this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
                }
                return false;
            }
            AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
            if (this.hasEnterAnimation) {
                if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache() && !this.isEnter) {
                    this.isEnter = true;
                    if (i == 0) {
                        this.waitingAnimation = false;
                        this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        this.playRunnable.run();
                    } else {
                        this.waitingAnimation = true;
                        this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        AndroidUtilities.runOnUIThread(this.playRunnable, i);
                    }
                    return true;
                }
                if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && this.isEnter && !this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.enterImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                }
                this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
            } else if (!this.isEnter) {
                this.enterScale = 0.0f;
                this.loopImageView.setScaleX(0.0f);
                this.loopImageView.setScaleY(0.0f);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.enterAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ReactionsContainerLayout.ReactionHolderView.this.lambda$play$0(valueAnimator);
                    }
                });
                this.enterAnimator.setDuration(150L);
                this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.enterAnimator.setStartDelay(i * ReactionsContainerLayout.this.durationScale);
                this.enterAnimator.start();
                this.isEnter = true;
            }
            return false;
        }

        public void lambda$play$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.enterScale = floatValue;
            this.loopImageView.setScaleY(floatValue * (this.selected ? 0.76f : 1.0f));
            this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
        }

        public void resetAnimation() {
            if (this.hasEnterAnimation) {
                AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
                if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                    if (ReactionsContainerLayout.this.animationEnabled) {
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false, true);
                    } else {
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.enterImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false, true);
                    }
                }
                this.loopImageView.setVisibility(4);
                this.enterImageView.setVisibility(0);
                this.switchedToLoopView = false;
                this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
            } else {
                this.loopImageView.animate().cancel();
                if (ReactionsContainerLayout.this.skipEnterAnimation) {
                    this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
                } else {
                    this.loopImageView.setScaleY(0.0f);
                    this.loopImageView.setScaleX(0.0f);
                }
            }
            this.isEnter = false;
        }

        public class AnonymousClass5 implements Runnable {
            AnonymousClass5() {
            }

            @Override
            public void run() {
                ReactionHolderView.this.performHapticFeedback(0);
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                reactionsContainerLayout.pressedReactionPosition = reactionsContainerLayout.visibleReactionsList.indexOf(ReactionHolderView.this.currentReaction);
                ReactionHolderView reactionHolderView = ReactionHolderView.this;
                ReactionsContainerLayout.this.pressedReaction = reactionHolderView.currentReaction;
                ReactionsContainerLayout.this.invalidate();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!this.touchable || ReactionsContainerLayout.this.cancelPressedAnimation != null) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                this.pressed = true;
                this.pressedX = motionEvent.getX();
                this.pressedY = motionEvent.getY();
                if (this.sideScale == 1.0f && !this.isLocked && ReactionsContainerLayout.this.type != 3 && ReactionsContainerLayout.this.type != 4 && ReactionsContainerLayout.this.type != 5) {
                    AndroidUtilities.runOnUIThread(this.longPressRunnable, ViewConfiguration.getLongPressTimeout());
                }
            }
            float scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2.0f;
            if ((motionEvent.getAction() == 2 && (Math.abs(this.pressedX - motionEvent.getX()) > scaledTouchSlop || Math.abs(this.pressedY - motionEvent.getY()) > scaledTouchSlop)) || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 1 && this.pressed && ((ReactionsContainerLayout.this.pressedReaction == null || ReactionsContainerLayout.this.pressedProgress > 0.8f) && ReactionsContainerLayout.this.delegate != null)) {
                    ReactionsContainerLayout.this.clicked = true;
                    long currentTimeMillis = System.currentTimeMillis();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    if (currentTimeMillis - reactionsContainerLayout.lastReactionSentTime > 300) {
                        reactionsContainerLayout.lastReactionSentTime = System.currentTimeMillis();
                        ReactionsContainerLayout.this.delegate.onReactionClicked(this, this.currentReaction, ReactionsContainerLayout.this.pressedProgress > 0.8f, false);
                    }
                }
                if (!ReactionsContainerLayout.this.clicked) {
                    ReactionsContainerLayout.this.cancelPressed();
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                this.pressed = false;
            }
            return true;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.selected && this.drawSelected) {
                canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, (getMeasuredWidth() >> 1) - AndroidUtilities.dp(1.0f), ReactionsContainerLayout.this.selectedPaint);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.loopImageView.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                if (this.position == 0) {
                    this.loopImageView.animatedEmojiDrawable.getImageReceiver().setRoundRadius(AndroidUtilities.dp(6.0f), 0, 0, AndroidUtilities.dp(6.0f));
                } else {
                    this.loopImageView.animatedEmojiDrawable.getImageReceiver().setRoundRadius(this.selected ? AndroidUtilities.dp(6.0f) : 0);
                }
            }
            super.dispatchDraw(canvas);
        }

        public void checkPlayLoopImage() {
            BackupImageView backupImageView = this.loopImageView;
            AnimatedEmojiDrawable animatedEmojiDrawable = backupImageView.animatedEmojiDrawable;
            ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : backupImageView.imageReceiver;
            if (imageReceiver == null || imageReceiver.getLottieAnimation() == null) {
                return;
            }
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            if (reactionsContainerLayout.reactionsWindow != null || this.pressed || !reactionsContainerLayout.allReactionsIsDefault) {
                imageReceiver.getLottieAnimation().start();
            } else if (imageReceiver.getLottieAnimation().getCurrentFrame() <= 2) {
                imageReceiver.getLottieAnimation().stop();
            }
        }
    }

    public void cancelPressed() {
        if (this.pressedReaction != null) {
            this.cancelPressedProgress = 0.0f;
            float f = this.pressedProgress;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.cancelPressedAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                final float val$fromProgress;

                AnonymousClass8(float f2) {
                    r2 = f2;
                }

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.pressedProgress = r2 * (1.0f - reactionsContainerLayout.cancelPressedProgress);
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.addListener(new AnimatorListenerAdapter() {
                AnonymousClass9() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.cancelPressedAnimation = null;
                    reactionsContainerLayout.pressedProgress = 0.0f;
                    ReactionsContainerLayout.this.pressedReaction = null;
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.setDuration(150L);
            this.cancelPressedAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.cancelPressedAnimation.start();
        }
    }

    public class AnonymousClass8 implements ValueAnimator.AnimatorUpdateListener {
        final float val$fromProgress;

        AnonymousClass8(float f2) {
            r2 = f2;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            reactionsContainerLayout.pressedProgress = r2 * (1.0f - reactionsContainerLayout.cancelPressedProgress);
            ReactionsContainerLayout.this.invalidate();
        }
    }

    public class AnonymousClass9 extends AnimatorListenerAdapter {
        AnonymousClass9() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            reactionsContainerLayout.cancelPressedAnimation = null;
            reactionsContainerLayout.pressedProgress = 0.0f;
            ReactionsContainerLayout.this.pressedReaction = null;
            ReactionsContainerLayout.this.invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        if (this.type == 5) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.availableEffectsUpdate);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        if (this.type == 5) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.availableEffectsUpdate);
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = (TLRPC$ChatFull) objArr[0];
            if (tLRPC$ChatFull.id != this.waitingLoadingChatId || getVisibility() == 0 || (tLRPC$ChatFull.available_reactions instanceof TLRPC$TL_chatReactionsNone)) {
                return;
            }
            setMessage(this.messageObject, null, true);
            setVisibility(0);
            startEnterAnimation(false);
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            invalidateEmojis();
        } else if (i == NotificationCenter.availableEffectsUpdate) {
            setMessage(this.messageObject, null, true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (getAlpha() < 0.5f) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void setAlpha(float f) {
        if (getAlpha() != f && f == 0.0f) {
            this.lastVisibleViews.clear();
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                    ((ReactionHolderView) this.recyclerListView.getChildAt(i)).resetAnimation();
                }
            }
        }
        super.setAlpha(f);
    }

    @Override
    public void setTranslationX(float f) {
        if (f != getTranslationX()) {
            super.setTranslationX(f);
        }
    }

    public class InternalImageView extends ImageView {
        ValueAnimator valueAnimator;

        public InternalImageView(Context context) {
            super(context);
        }

        public void play(int i, boolean z) {
            invalidate();
            ValueAnimator valueAnimator = this.valueAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.valueAnimator.cancel();
            }
            if (z) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(getScaleX(), 1.0f);
                this.valueAnimator = ofFloat;
                ofFloat.setInterpolator(AndroidUtilities.overshootInterpolator);
                this.valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ReactionsContainerLayout.InternalImageView.this.lambda$play$0(valueAnimator2);
                    }
                });
                this.valueAnimator.setStartDelay(i * ReactionsContainerLayout.this.durationScale);
                this.valueAnimator.setDuration(300L);
                this.valueAnimator.start();
                return;
            }
            setScaleX(1.0f);
            setScaleY(1.0f);
        }

        public void lambda$play$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setScaleX(floatValue);
            setScaleY(floatValue);
            ReactionsContainerLayout.this.customReactionsContainer.invalidate();
        }

        public void resetAnimation() {
            setScaleX(0.0f);
            setScaleY(0.0f);
            ReactionsContainerLayout.this.customReactionsContainer.invalidate();
            ValueAnimator valueAnimator = this.valueAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }
    }

    private class CustomReactionsContainer extends FrameLayout {
        Paint backgroundPaint;

        public CustomReactionsContainer(Context context) {
            super(context);
            this.backgroundPaint = new Paint(1);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            int alphaComponent;
            if (ReactionsContainerLayout.this.type == 1 || ReactionsContainerLayout.this.type == 2 || ReactionsContainerLayout.this.type == 4) {
                alphaComponent = ColorUtils.setAlphaComponent(-1, 30);
            } else {
                alphaComponent = ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, ReactionsContainerLayout.this.resourcesProvider), Theme.getColor(Theme.key_dialogBackground, ReactionsContainerLayout.this.resourcesProvider), 0.7f);
            }
            this.backgroundPaint.setColor(alphaComponent);
            float measuredHeight = getMeasuredHeight() / 2.0f;
            float measuredWidth = getMeasuredWidth() / 2.0f;
            View childAt = getChildAt(0);
            float measuredWidth2 = (getMeasuredWidth() - AndroidUtilities.dpf2(6.0f)) / 2.0f;
            float expandSize = ReactionsContainerLayout.this.expandSize();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(measuredWidth - measuredWidth2, (measuredHeight - measuredWidth2) - expandSize, measuredWidth + measuredWidth2, measuredHeight + measuredWidth2 + expandSize);
            canvas.save();
            canvas.scale(childAt.getScaleX(), childAt.getScaleY(), measuredWidth, measuredHeight);
            canvas.drawRoundRect(rectF, measuredWidth2, measuredWidth2, this.backgroundPaint);
            canvas.restore();
            canvas.save();
            canvas.translate(0.0f, expandSize);
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }

    public float expandSize() {
        return (int) (getPullingLeftProgress() * AndroidUtilities.dp(6.0f));
    }

    public void setParentLayout(ChatScrimPopupContainerLayout chatScrimPopupContainerLayout) {
        this.parentLayout = chatScrimPopupContainerLayout;
    }

    public static boolean allowSmoothEnterTransition() {
        return SharedConfig.deviceIsHigh();
    }

    public class Adapter extends AdapterWithDiffUtils {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public Adapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 1) {
                ReactionsContainerLayout.this.premiumLockContainer = new FrameLayout(ReactionsContainerLayout.this.getContext());
                ReactionsContainerLayout.this.premiumLockIconView = new PremiumLockIconView(ReactionsContainerLayout.this.getContext(), PremiumLockIconView.TYPE_REACTIONS);
                PremiumLockIconView premiumLockIconView = ReactionsContainerLayout.this.premiumLockIconView;
                int color = Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon);
                int i2 = Theme.key_dialogBackground;
                premiumLockIconView.setColor(ColorUtils.blendARGB(color, Theme.getColor(i2), 0.7f));
                ReactionsContainerLayout.this.premiumLockIconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), PorterDuff.Mode.MULTIPLY));
                ReactionsContainerLayout.this.premiumLockIconView.setScaleX(0.0f);
                ReactionsContainerLayout.this.premiumLockIconView.setScaleY(0.0f);
                ReactionsContainerLayout.this.premiumLockIconView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                reactionsContainerLayout.premiumLockContainer.addView(reactionsContainerLayout.premiumLockIconView, LayoutHelper.createFrame(26, 26, 17));
                ReactionsContainerLayout.this.premiumLockIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ReactionsContainerLayout.Adapter.this.lambda$onCreateViewHolder$0(view2);
                    }
                });
                view = ReactionsContainerLayout.this.premiumLockContainer;
            } else if (i != 2) {
                ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                view = new ReactionHolderView(reactionsContainerLayout2.getContext(), true);
            } else {
                ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                reactionsContainerLayout3.customReactionsContainer = new CustomReactionsContainer(reactionsContainerLayout4.getContext());
                ReactionsContainerLayout reactionsContainerLayout5 = ReactionsContainerLayout.this;
                ReactionsContainerLayout reactionsContainerLayout6 = ReactionsContainerLayout.this;
                reactionsContainerLayout5.customEmojiReactionsIconView = new InternalImageView(reactionsContainerLayout6.getContext());
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setImageResource(R.drawable.msg_reactions_expand);
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if (ReactionsContainerLayout.this.type == 1 || ReactionsContainerLayout.this.type == 2 || ReactionsContainerLayout.this.type == 4) {
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
                } else {
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
                }
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(28.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_listSelector), 40)));
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setContentDescription(LocaleController.getString(R.string.AccDescrExpandPanel));
                ReactionsContainerLayout reactionsContainerLayout7 = ReactionsContainerLayout.this;
                reactionsContainerLayout7.customReactionsContainer.addView(reactionsContainerLayout7.customEmojiReactionsIconView, LayoutHelper.createFrame(30, 30, 17));
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ReactionsContainerLayout.Adapter.this.lambda$onCreateViewHolder$1(view2);
                    }
                });
                view = ReactionsContainerLayout.this.customReactionsContainer;
            }
            int topOffset = ((ReactionsContainerLayout.this.getLayoutParams().height - ((int) ReactionsContainerLayout.this.getTopOffset())) - ReactionsContainerLayout.this.getPaddingTop()) - ReactionsContainerLayout.this.getPaddingBottom();
            view.setLayoutParams(new RecyclerView.LayoutParams(topOffset - AndroidUtilities.dp(12.0f), topOffset));
            return new RecyclerListView.Holder(view);
        }

        public void lambda$onCreateViewHolder$0(View view) {
            view.getLocationOnScreen(new int[2]);
            ReactionsContainerLayout.this.showUnlockPremium(r0[0] + (view.getMeasuredWidth() / 2.0f), r0[1] + (view.getMeasuredHeight() / 2.0f));
        }

        public void lambda$onCreateViewHolder$1(View view) {
            ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 3) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) viewHolder.itemView;
                reactionHolderView.setScaleX(1.0f);
                reactionHolderView.setScaleY(1.0f);
                reactionHolderView.setReaction(ReactionsContainerLayout.this.items.get(i).reaction, i);
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition;
            if ((viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 3) && (adapterPosition = viewHolder.getAdapterPosition()) >= 0 && adapterPosition < ReactionsContainerLayout.this.items.size()) {
                ((ReactionHolderView) viewHolder.itemView).updateSelected(ReactionsContainerLayout.this.items.get(adapterPosition).reaction, false);
            }
            super.onViewAttachedToWindow(viewHolder);
        }

        @Override
        public int getItemCount() {
            return ReactionsContainerLayout.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            return ReactionsContainerLayout.this.items.get(i).viewType;
        }

        public void updateItems(boolean z) {
            ReactionsContainerLayout.this.oldItems.clear();
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            reactionsContainerLayout.oldItems.addAll(reactionsContainerLayout.items);
            ReactionsContainerLayout.this.items.clear();
            for (int i = 0; i < ReactionsContainerLayout.this.visibleReactionsList.size(); i++) {
                ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) ReactionsContainerLayout.this.visibleReactionsList.get(i);
                ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                reactionsContainerLayout2.items.add(new InnerItem(reactionsContainerLayout2, visibleReaction.emojicon == null ? 3 : 0, visibleReaction));
            }
            if (ReactionsContainerLayout.this.showUnlockPremiumButton()) {
                ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                reactionsContainerLayout3.items.add(new InnerItem(reactionsContainerLayout3, 1, null));
            }
            if (ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                reactionsContainerLayout4.items.add(new InnerItem(reactionsContainerLayout4, 2, null));
            }
            if (z) {
                ReactionsContainerLayout reactionsContainerLayout5 = ReactionsContainerLayout.this;
                setItems(reactionsContainerLayout5.oldItems, reactionsContainerLayout5.items);
            } else {
                super.notifyDataSetChanged();
            }
        }
    }

    public void setPaused(boolean z, boolean z2) {
        if (this.paused == z) {
            return;
        }
        this.paused = z;
        this.pausedExceptSelected = z2;
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow == null || customEmojiReactionsWindow.getSelectAnimatedEmojiDialog() == null) {
            return;
        }
        this.reactionsWindow.getSelectAnimatedEmojiDialog().setPaused(this.paused, this.pausedExceptSelected);
    }
}
