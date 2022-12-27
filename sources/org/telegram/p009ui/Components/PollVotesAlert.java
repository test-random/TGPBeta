package org.telegram.p009ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C1072R;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.p009ui.ActionBar.BottomSheet;
import org.telegram.p009ui.ActionBar.C1133ActionBar;
import org.telegram.p009ui.ActionBar.SimpleTextView;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.ActionBar.ThemeDescription;
import org.telegram.p009ui.Cells.TextCell;
import org.telegram.p009ui.ChatActivity;
import org.telegram.p009ui.Components.AnimationProperties;
import org.telegram.p009ui.Components.PollVotesAlert;
import org.telegram.p009ui.Components.RecyclerListView;
import org.telegram.p009ui.ProfileActivity;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$MessageUserVote;
import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageUserVoteInputOption;
import org.telegram.tgnet.TLRPC$TL_messages_getPollVotes;
import org.telegram.tgnet.TLRPC$TL_messages_votesList;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC$User;

public class PollVotesAlert extends BottomSheet {
    public static final Property<UserCell, Float> USER_CELL_PROPERTY = new AnimationProperties.FloatProperty<UserCell>("placeholderAlpha") {
        @Override
        public void setValue(UserCell userCell, float f) {
            userCell.setPlaceholderAlpha(f);
        }

        @Override
        public Float get(UserCell userCell) {
            return Float.valueOf(userCell.getPlaceholderAlpha());
        }
    };
    private C1133ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private ChatActivity chatActivity;
    private float gradientWidth;
    private Adapter listAdapter;
    private RecyclerListView listView;
    private HashSet<VotesList> loadingMore;
    private boolean loadingResults;
    private MessageObject messageObject;
    private TLRPC$InputPeer peer;
    private LinearGradient placeholderGradient;
    private Matrix placeholderMatrix;
    private Paint placeholderPaint;
    private TLRPC$Poll poll;
    private ArrayList<Integer> queries;
    private RectF rect;
    private int scrollOffsetY;
    private Drawable shadowDrawable;
    private TextView titleTextView;
    private float totalTranslation;
    private ArrayList<VotesList> voters;
    private HashMap<VotesList, Button> votesPercents;

    @Override
    protected boolean canDismissWithSwipe() {
        return false;
    }

    static float access$3016(PollVotesAlert pollVotesAlert, float f) {
        float f2 = pollVotesAlert.totalTranslation + f;
        pollVotesAlert.totalTranslation = f2;
        return f2;
    }

    static float access$3024(PollVotesAlert pollVotesAlert, float f) {
        float f2 = pollVotesAlert.totalTranslation - f;
        pollVotesAlert.totalTranslation = f2;
        return f2;
    }

    public static class VotesList {
        public boolean collapsed;
        public int collapsedCount = 10;
        public int count;
        public String next_offset;
        public byte[] option;
        public ArrayList<TLRPC$User> users;
        public ArrayList<TLRPC$MessageUserVote> votes;

        public VotesList(TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList, byte[] bArr) {
            this.count = tLRPC$TL_messages_votesList.count;
            this.votes = tLRPC$TL_messages_votesList.votes;
            this.users = tLRPC$TL_messages_votesList.users;
            this.next_offset = tLRPC$TL_messages_votesList.next_offset;
            this.option = bArr;
        }

        public int getCount() {
            if (this.collapsed) {
                return Math.min(this.collapsedCount, this.votes.size());
            }
            return this.votes.size();
        }

        public int getCollapsed() {
            if (this.votes.size() <= 15) {
                return 0;
            }
            return this.collapsed ? 1 : 2;
        }
    }

    public class SectionCell extends FrameLayout {
        private TextView middleTextView;
        private AnimatedTextView righTextView;
        private TextView textView;

        protected void onCollapseClick() {
        }

        public SectionCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("graySection"));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textView.setTextColor(Theme.getColor("key_graySectionText"));
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView2 = new TextView(getContext());
            this.middleTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.middleTextView.setTextColor(Theme.getColor("key_graySectionText"));
            this.middleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), PollVotesAlert.this) {
                @Override
                public boolean post(Runnable runnable) {
                    return ((BottomSheet) PollVotesAlert.this).containerView.post(runnable);
                }

                @Override
                public boolean postDelayed(Runnable runnable, long j) {
                    return ((BottomSheet) PollVotesAlert.this).containerView.postDelayed(runnable, j);
                }

                @Override
                public void invalidate() {
                    super.invalidate();
                    SectionCell sectionCell = SectionCell.this;
                    if (sectionCell == PollVotesAlert.this.listView.getPinnedHeader()) {
                        PollVotesAlert.this.listView.invalidate();
                    }
                }
            };
            this.righTextView = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.m36dp(14.0f));
            this.righTextView.setTextColor(Theme.getColor("key_graySectionText"));
            this.righTextView.setGravity(LocaleController.isRTL ? 3 : 5);
            this.righTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PollVotesAlert.SectionCell.this.lambda$new$0(view);
                }
            });
            TextView textView3 = this.textView;
            boolean z = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-2, -1.0f, (z ? 5 : 3) | 48, z ? 0 : 16, 0.0f, z ? 16 : 0, 0.0f));
            addView(this.middleTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
            addView(this.righTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        }

        public void lambda$new$0(View view) {
            onCollapseClick();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m36dp(32.0f), 1073741824);
            measureChildWithMargins(this.middleTextView, i, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.righTextView, i, 0, makeMeasureSpec, 0);
            measureChildWithMargins(this.textView, i, this.middleTextView.getMeasuredWidth() + this.righTextView.getMeasuredWidth() + AndroidUtilities.m36dp(32.0f), makeMeasureSpec, 0);
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.m36dp(32.0f));
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (LocaleController.isRTL) {
                int left = this.textView.getLeft() - this.middleTextView.getMeasuredWidth();
                TextView textView = this.middleTextView;
                textView.layout(left, textView.getTop(), this.middleTextView.getMeasuredWidth() + left, this.middleTextView.getBottom());
                return;
            }
            int right = this.textView.getRight();
            TextView textView2 = this.middleTextView;
            textView2.layout(right, textView2.getTop(), this.middleTextView.getMeasuredWidth() + right, this.middleTextView.getBottom());
        }

        public void setText(String str, int i, int i2, int i3, boolean z) {
            SpannableStringBuilder spannableStringBuilder;
            TextView textView = this.textView;
            textView.setText(Emoji.replaceEmoji(str, textView.getPaint().getFontMetricsInt(), AndroidUtilities.m36dp(14.0f), false));
            String format = String.format("%d", Integer.valueOf(i));
            if (LocaleController.isRTL) {
                spannableStringBuilder = new SpannableStringBuilder(String.format("%s%% – ", Integer.valueOf(i)));
            } else {
                spannableStringBuilder = new SpannableStringBuilder(String.format(" – %s%%", Integer.valueOf(i)));
            }
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 3, format.length() + 3, 33);
            this.middleTextView.setText(spannableStringBuilder);
            if (i3 == 0) {
                if (PollVotesAlert.this.poll.quiz) {
                    this.righTextView.setText(LocaleController.formatPluralString("Answer", i2, new Object[0]), z);
                } else {
                    this.righTextView.setText(LocaleController.formatPluralString("Vote", i2, new Object[0]), z);
                }
            } else if (i3 == 1) {
                this.righTextView.setText(LocaleController.getString("PollExpand", C1072R.string.PollExpand), z);
            } else {
                this.righTextView.setText(LocaleController.getString("PollCollapse", C1072R.string.PollCollapse), z);
            }
        }
    }

    public class UserCell extends FrameLayout {
        private ArrayList<Animator> animators;
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private TLRPC$User currentUser;
        private boolean drawPlaceholder;
        private TLRPC$FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private float placeholderAlpha;
        private int placeholderNum;

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        public UserCell(Context context) {
            super(context);
            int i = UserConfig.selectedAccount;
            this.placeholderAlpha = 1.0f;
            setWillNotDraw(false);
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.m36dp(18.0f));
            BackupImageView backupImageView2 = this.avatarImageView;
            boolean z = LocaleController.isRTL;
            addView(backupImageView2, LayoutHelper.createFrame(36, 36.0f, (z ? 5 : 3) | 48, z ? 0.0f : 14.0f, 6.0f, z ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView2 = this.nameTextView;
            boolean z2 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 28.0f : 65.0f, 14.0f, z2 ? 65.0f : 28.0f, 0.0f));
        }

        public void setData(TLRPC$User tLRPC$User, int i, boolean z) {
            this.currentUser = tLRPC$User;
            this.needDivider = z;
            this.drawPlaceholder = tLRPC$User == null;
            this.placeholderNum = i;
            if (tLRPC$User == null) {
                this.nameTextView.setText("");
                this.avatarImageView.setImageDrawable(null);
            } else {
                update(0);
            }
            ArrayList<Animator> arrayList = this.animators;
            if (arrayList != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.avatarImageView, View.ALPHA, 0.0f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, 0.0f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(this, PollVotesAlert.USER_CELL_PROPERTY, 1.0f, 0.0f));
            } else if (this.drawPlaceholder) {
            } else {
                this.placeholderAlpha = 0.0f;
            }
        }

        @Keep
        public void setPlaceholderAlpha(float f) {
            this.placeholderAlpha = f;
            invalidate();
        }

        @Keep
        public float getPlaceholderAlpha() {
            return this.placeholderAlpha;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m36dp(48.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void update(int r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.PollVotesAlert.UserCell.update(int):void");
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int m36dp;
            int m36dp2;
            int m36dp3;
            int m36dp4;
            if (this.drawPlaceholder || this.placeholderAlpha != 0.0f) {
                PollVotesAlert.this.placeholderPaint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                int left = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
                int top = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
                canvas.drawCircle(left, top, this.avatarImageView.getMeasuredWidth() / 2, PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    m36dp = AndroidUtilities.m36dp(65.0f);
                    m36dp2 = AndroidUtilities.m36dp(48.0f);
                } else {
                    m36dp = AndroidUtilities.m36dp(65.0f);
                    m36dp2 = AndroidUtilities.m36dp(60.0f);
                }
                if (LocaleController.isRTL) {
                    m36dp = (getMeasuredWidth() - m36dp) - m36dp2;
                }
                PollVotesAlert.this.rect.set(m36dp, top - AndroidUtilities.m36dp(4.0f), m36dp + m36dp2, AndroidUtilities.m36dp(4.0f) + top);
                canvas.drawRoundRect(PollVotesAlert.this.rect, AndroidUtilities.m36dp(4.0f), AndroidUtilities.m36dp(4.0f), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    m36dp3 = AndroidUtilities.m36dp(119.0f);
                    m36dp4 = AndroidUtilities.m36dp(60.0f);
                } else {
                    m36dp3 = AndroidUtilities.m36dp(131.0f);
                    m36dp4 = AndroidUtilities.m36dp(80.0f);
                }
                if (LocaleController.isRTL) {
                    m36dp3 = (getMeasuredWidth() - m36dp3) - m36dp4;
                }
                PollVotesAlert.this.rect.set(m36dp3, top - AndroidUtilities.m36dp(4.0f), m36dp3 + m36dp4, top + AndroidUtilities.m36dp(4.0f));
                canvas.drawRoundRect(PollVotesAlert.this.rect, AndroidUtilities.m36dp(4.0f), AndroidUtilities.m36dp(4.0f), PollVotesAlert.this.placeholderPaint);
            }
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.m36dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.m36dp(64.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public static void showForPoll(ChatActivity chatActivity, MessageObject messageObject) {
        if (chatActivity == null || chatActivity.getParentActivity() == null) {
            return;
        }
        chatActivity.showDialog(new PollVotesAlert(chatActivity, messageObject));
    }

    public static class Button {
        private float decimal;
        private int percent;
        private int votesCount;

        private Button() {
        }

        static float access$3924(Button button, float f) {
            float f2 = button.decimal - f;
            button.decimal = f2;
            return f2;
        }

        static int access$4012(Button button, int i) {
            int i2 = button.percent + i;
            button.percent = i2;
            return i2;
        }
    }

    public PollVotesAlert(final ChatActivity chatActivity, MessageObject messageObject) {
        super(chatActivity.getParentActivity(), true);
        int i;
        int i2;
        int i3 = 1;
        this.loadingMore = new HashSet<>();
        this.votesPercents = new HashMap<>();
        this.voters = new ArrayList<>();
        this.queries = new ArrayList<>();
        this.placeholderPaint = new Paint(1);
        this.loadingResults = true;
        this.rect = new RectF();
        fixNavigationBar();
        this.messageObject = messageObject;
        this.chatActivity = chatActivity;
        TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) messageObject.messageOwner.media;
        this.poll = tLRPC$TL_messageMediaPoll.poll;
        Activity parentActivity = chatActivity.getParentActivity();
        this.peer = chatActivity.getMessagesController().getInputPeer((int) messageObject.getDialogId());
        final ArrayList arrayList = new ArrayList();
        int size = tLRPC$TL_messageMediaPoll.results.results.size();
        final Integer[] numArr = new Integer[size];
        int i4 = 0;
        while (i4 < size) {
            final TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters = tLRPC$TL_messageMediaPoll.results.results.get(i4);
            if (tLRPC$TL_pollAnswerVoters.voters == 0) {
                i2 = i4;
                i = size;
            } else {
                TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = new TLRPC$TL_messages_votesList();
                int i5 = tLRPC$TL_pollAnswerVoters.voters;
                i5 = i5 > 15 ? 10 : i5;
                for (int i6 = 0; i6 < i5; i6++) {
                    tLRPC$TL_messages_votesList.votes.add(new TLRPC$TL_messageUserVoteInputOption());
                }
                int i7 = tLRPC$TL_pollAnswerVoters.voters;
                tLRPC$TL_messages_votesList.next_offset = i5 < i7 ? "empty" : null;
                tLRPC$TL_messages_votesList.count = i7;
                this.voters.add(new VotesList(tLRPC$TL_messages_votesList, tLRPC$TL_pollAnswerVoters.option));
                TLRPC$TL_messages_getPollVotes tLRPC$TL_messages_getPollVotes = new TLRPC$TL_messages_getPollVotes();
                tLRPC$TL_messages_getPollVotes.peer = this.peer;
                tLRPC$TL_messages_getPollVotes.f951id = this.messageObject.getId();
                tLRPC$TL_messages_getPollVotes.limit = tLRPC$TL_pollAnswerVoters.voters <= 15 ? 15 : 10;
                tLRPC$TL_messages_getPollVotes.flags |= i3;
                tLRPC$TL_messages_getPollVotes.option = tLRPC$TL_pollAnswerVoters.option;
                final int i8 = i4;
                i = size;
                i2 = i4;
                numArr[i2] = Integer.valueOf(chatActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_getPollVotes, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        PollVotesAlert.this.lambda$new$1(numArr, i8, chatActivity, arrayList, tLRPC$TL_pollAnswerVoters, tLObject, tLRPC$TL_error);
                    }
                }));
                this.queries.add(numArr[i2]);
            }
            i4 = i2 + 1;
            size = i;
            i3 = 1;
        }
        updateButtons();
        Collections.sort(this.voters, new Comparator<VotesList>() {
            private int getIndex(VotesList votesList) {
                int size2 = PollVotesAlert.this.poll.answers.size();
                for (int i9 = 0; i9 < size2; i9++) {
                    if (Arrays.equals(PollVotesAlert.this.poll.answers.get(i9).option, votesList.option)) {
                        return i9;
                    }
                }
                return 0;
            }

            @Override
            public int compare(VotesList votesList, VotesList votesList2) {
                int index = getIndex(votesList);
                int index2 = getIndex(votesList2);
                if (index > index2) {
                    return 1;
                }
                return index < index2 ? -1 : 0;
            }
        });
        updatePlaceholder();
        Drawable mutate = parentActivity.getResources().getDrawable(C1072R.C1073drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        FrameLayout frameLayout = new FrameLayout(parentActivity) {
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            @Override
            protected void onMeasure(int i9, int i10) {
                int size2 = View.MeasureSpec.getSize(i10);
                if (Build.VERSION.SDK_INT >= 21 && !((BottomSheet) PollVotesAlert.this).isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size2 - getPaddingTop();
                ((FrameLayout.LayoutParams) PollVotesAlert.this.listView.getLayoutParams()).topMargin = C1133ActionBar.getCurrentActionBarHeight();
                ((FrameLayout.LayoutParams) PollVotesAlert.this.actionBarShadow.getLayoutParams()).topMargin = C1133ActionBar.getCurrentActionBarHeight();
                int m36dp = ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop + AndroidUtilities.m36dp(15.0f) + AndroidUtilities.statusBarHeight;
                int sectionCount = PollVotesAlert.this.listAdapter.getSectionCount();
                for (int i11 = 0; i11 < sectionCount; i11++) {
                    if (i11 == 0) {
                        PollVotesAlert.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i9 - (((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft * 2)), 1073741824), i10);
                        m36dp += PollVotesAlert.this.titleTextView.getMeasuredHeight();
                    } else {
                        m36dp += AndroidUtilities.m36dp(32.0f) + (AndroidUtilities.m36dp(50.0f) * (PollVotesAlert.this.listAdapter.getCountForSection(i11) - 1));
                    }
                }
                int m36dp2 = (m36dp < paddingTop ? paddingTop - m36dp : paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.m36dp(8.0f);
                if (PollVotesAlert.this.listView.getPaddingTop() != m36dp2) {
                    this.ignoreLayout = true;
                    PollVotesAlert.this.listView.setPinnedSectionOffsetY(-m36dp2);
                    PollVotesAlert.this.listView.setPadding(0, m36dp2, 0, 0);
                    this.ignoreLayout = false;
                }
                super.onMeasure(i9, View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
            }

            @Override
            protected void onLayout(boolean z, int i9, int i10, int i11, int i12) {
                super.onLayout(z, i9, i10, i11, i12);
                PollVotesAlert.this.updateLayout(false);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && PollVotesAlert.this.scrollOffsetY != 0 && motionEvent.getY() < PollVotesAlert.this.scrollOffsetY + AndroidUtilities.m36dp(12.0f) && PollVotesAlert.this.actionBar.getAlpha() == 0.0f) {
                    PollVotesAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !PollVotesAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                float f;
                int m36dp = AndroidUtilities.m36dp(13.0f);
                int i9 = (PollVotesAlert.this.scrollOffsetY - ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop) - m36dp;
                if (((BottomSheet) PollVotesAlert.this).currentSheetAnimationType == 1) {
                    i9 = (int) (i9 + PollVotesAlert.this.listView.getTranslationY());
                }
                int m36dp2 = AndroidUtilities.m36dp(20.0f) + i9;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.m36dp(15.0f) + ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop;
                if (((BottomSheet) PollVotesAlert.this).backgroundPaddingTop + i9 < C1133ActionBar.getCurrentActionBarHeight()) {
                    float m36dp3 = m36dp + AndroidUtilities.m36dp(4.0f);
                    float min = Math.min(1.0f, ((C1133ActionBar.getCurrentActionBarHeight() - i9) - ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop) / m36dp3);
                    int currentActionBarHeight = (int) ((C1133ActionBar.getCurrentActionBarHeight() - m36dp3) * min);
                    i9 -= currentActionBarHeight;
                    m36dp2 -= currentActionBarHeight;
                    measuredHeight += currentActionBarHeight;
                    f = 1.0f - min;
                } else {
                    f = 1.0f;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int i10 = AndroidUtilities.statusBarHeight;
                    i9 += i10;
                    m36dp2 += i10;
                }
                PollVotesAlert.this.shadowDrawable.setBounds(0, i9, getMeasuredWidth(), measuredHeight);
                PollVotesAlert.this.shadowDrawable.draw(canvas);
                if (f != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("dialogBackground"));
                    this.rect.set(((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft, ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop + i9, getMeasuredWidth() - ((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft, ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop + i9 + AndroidUtilities.m36dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.m36dp(12.0f) * f, AndroidUtilities.m36dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                }
                if (f != 0.0f) {
                    int m36dp4 = AndroidUtilities.m36dp(36.0f);
                    this.rect.set((getMeasuredWidth() - m36dp4) / 2, m36dp2, (getMeasuredWidth() + m36dp4) / 2, m36dp2 + AndroidUtilities.m36dp(4.0f));
                    int color = Theme.getColor("key_sheet_scrollUp");
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * 1.0f * f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.m36dp(2.0f), AndroidUtilities.m36dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                int color2 = Theme.getColor("dialogBackground");
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (PollVotesAlert.this.actionBar.getAlpha() * 255.0f), (int) (Color.red(color2) * 0.8f), (int) (Color.green(color2) * 0.8f), (int) (Color.blue(color2) * 0.8f)));
                canvas.drawRect(((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft, 0.0f, getMeasuredWidth() - ((BottomSheet) PollVotesAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView = frameLayout;
        frameLayout.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i9 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i9, 0, i9, 0);
        this.listView = new RecyclerListView(parentActivity) {
            long lastUpdateTime;

            @Override
            protected boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (PollVotesAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                if (PollVotesAlert.this.loadingResults) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
                    if (abs > 17) {
                        abs = 16;
                    }
                    this.lastUpdateTime = elapsedRealtime;
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                    PollVotesAlert.access$3016(pollVotesAlert, (((float) abs) * pollVotesAlert.gradientWidth) / 1800.0f);
                    while (PollVotesAlert.this.totalTranslation >= PollVotesAlert.this.gradientWidth * 2.0f) {
                        PollVotesAlert pollVotesAlert2 = PollVotesAlert.this;
                        PollVotesAlert.access$3024(pollVotesAlert2, pollVotesAlert2.gradientWidth * 2.0f);
                    }
                    PollVotesAlert.this.placeholderMatrix.setTranslate(PollVotesAlert.this.totalTranslation, 0.0f);
                    PollVotesAlert.this.placeholderGradient.setLocalMatrix(PollVotesAlert.this.placeholderMatrix);
                    invalidateViews();
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(150L);
        defaultItemAnimator.setMoveDuration(350L);
        defaultItemAnimator.setChangeDuration(0L);
        defaultItemAnimator.setRemoveDuration(0L);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setMoveInterpolator(new OvershootInterpolator(1.1f));
        defaultItemAnimator.setTranslationInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setClipToPadding(false);
        this.listView.setLayoutManager(new LinearLayoutManager(this, getContext(), 1, false) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return AndroidUtilities.m36dp(4000.0f);
            }
        });
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setSectionsType(2);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = new Adapter(parentActivity);
        this.listAdapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i10) {
                PollVotesAlert.this.lambda$new$4(chatActivity, view, i10);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i10, int i11) {
                if (PollVotesAlert.this.listView.getChildCount() <= 0) {
                    return;
                }
                PollVotesAlert.this.updateLayout(true);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i10) {
                if (i10 == 0) {
                    if (((PollVotesAlert.this.scrollOffsetY - ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop) - AndroidUtilities.m36dp(13.0f)) + ((BottomSheet) PollVotesAlert.this).backgroundPaddingTop >= C1133ActionBar.getCurrentActionBarHeight() || !PollVotesAlert.this.listView.canScrollVertically(1)) {
                        return;
                    }
                    PollVotesAlert.this.listView.getChildAt(0);
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) PollVotesAlert.this.listView.findViewHolderForAdapterPosition(0);
                    if (holder == null || holder.itemView.getTop() <= AndroidUtilities.m36dp(7.0f)) {
                        return;
                    }
                    PollVotesAlert.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.m36dp(7.0f));
                }
            }
        });
        TextView textView = new TextView(parentActivity);
        this.titleTextView = textView;
        textView.setTextSize(1, 18.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleTextView.setPadding(AndroidUtilities.m36dp(21.0f), AndroidUtilities.m36dp(5.0f), AndroidUtilities.m36dp(14.0f), AndroidUtilities.m36dp(21.0f));
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        TextView textView2 = this.titleTextView;
        textView2.setText(Emoji.replaceEmoji(this.poll.question, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.m36dp(18.0f), false));
        C1133ActionBar c1133ActionBar = new C1133ActionBar(parentActivity) {
            @Override
            public void setAlpha(float f) {
                super.setAlpha(f);
                ((BottomSheet) PollVotesAlert.this).containerView.invalidate();
            }
        };
        this.actionBar = c1133ActionBar;
        c1133ActionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setBackButtonImage(C1072R.C1073drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle(LocaleController.getString("PollResults", C1072R.string.PollResults));
        if (this.poll.quiz) {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Answer", tLRPC$TL_messageMediaPoll.results.total_voters, new Object[0]));
        } else {
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Vote", tLRPC$TL_messageMediaPoll.results.total_voters, new Object[0]));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new C1133ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i10) {
                if (i10 == -1) {
                    PollVotesAlert.this.dismiss();
                }
            }
        });
        View view = new View(parentActivity);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
    }

    public void lambda$new$1(final Integer[] numArr, final int i, final ChatActivity chatActivity, final ArrayList arrayList, final TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PollVotesAlert.this.lambda$new$0(numArr, i, tLObject, chatActivity, arrayList, tLRPC$TL_pollAnswerVoters);
            }
        });
    }

    public void lambda$new$0(Integer[] numArr, int i, TLObject tLObject, ChatActivity chatActivity, ArrayList arrayList, TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters) {
        RecyclerView.ViewHolder findContainingViewHolder;
        this.queries.remove(numArr[i]);
        if (tLObject != null) {
            TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = (TLRPC$TL_messages_votesList) tLObject;
            chatActivity.getMessagesController().putUsers(tLRPC$TL_messages_votesList.users, false);
            if (!tLRPC$TL_messages_votesList.votes.isEmpty()) {
                arrayList.add(new VotesList(tLRPC$TL_messages_votesList, tLRPC$TL_pollAnswerVoters.option));
            }
            if (this.queries.isEmpty()) {
                int size = arrayList.size();
                boolean z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    VotesList votesList = (VotesList) arrayList.get(i2);
                    int size2 = this.voters.size();
                    int i3 = 0;
                    while (true) {
                        if (i3 < size2) {
                            VotesList votesList2 = this.voters.get(i3);
                            if (Arrays.equals(votesList.option, votesList2.option)) {
                                votesList2.next_offset = votesList.next_offset;
                                z = (votesList2.count == votesList.count && votesList2.votes.size() == votesList.votes.size()) ? true : true;
                                votesList2.count = votesList.count;
                                votesList2.users = votesList.users;
                                votesList2.votes = votesList.votes;
                            } else {
                                i3++;
                            }
                        }
                    }
                }
                this.loadingResults = false;
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null) {
                    if (this.currentSheetAnimationType != 0 || this.startAnimationRunnable != null || z) {
                        if (z) {
                            updateButtons();
                        }
                        this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    int childCount = recyclerListView.getChildCount();
                    ArrayList arrayList2 = new ArrayList();
                    for (int i4 = 0; i4 < childCount; i4++) {
                        View childAt = this.listView.getChildAt(i4);
                        if ((childAt instanceof UserCell) && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null) {
                            UserCell userCell = (UserCell) childAt;
                            userCell.animators = arrayList2;
                            userCell.setEnabled(true);
                            this.listAdapter.onViewAttachedToWindow(findContainingViewHolder);
                            userCell.animators = null;
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(arrayList2);
                        animatorSet.setDuration(180L);
                        animatorSet.start();
                    }
                    this.loadingResults = false;
                    return;
                }
                return;
            }
            return;
        }
        dismiss();
    }

    public void lambda$new$4(final ChatActivity chatActivity, View view, int i) {
        if (chatActivity == null || chatActivity.getParentActivity() == null) {
            return;
        }
        ArrayList<Integer> arrayList = this.queries;
        if (arrayList == null || arrayList.isEmpty()) {
            int i2 = 0;
            if (view instanceof TextCell) {
                int sectionForPosition = this.listAdapter.getSectionForPosition(i) - 1;
                int positionInSectionForPosition = this.listAdapter.getPositionInSectionForPosition(i) - 1;
                if (positionInSectionForPosition <= 0 || sectionForPosition < 0) {
                    return;
                }
                final VotesList votesList = this.voters.get(sectionForPosition);
                if (positionInSectionForPosition != votesList.getCount() || this.loadingMore.contains(votesList)) {
                    return;
                }
                if (votesList.collapsed && votesList.collapsedCount < votesList.votes.size()) {
                    int min = Math.min(votesList.collapsedCount + 50, votesList.votes.size());
                    votesList.collapsedCount = min;
                    if (min == votesList.votes.size()) {
                        votesList.collapsed = false;
                    }
                    animateSectionUpdates(null);
                    this.listAdapter.update(true);
                    return;
                }
                this.loadingMore.add(votesList);
                TLRPC$TL_messages_getPollVotes tLRPC$TL_messages_getPollVotes = new TLRPC$TL_messages_getPollVotes();
                tLRPC$TL_messages_getPollVotes.peer = this.peer;
                tLRPC$TL_messages_getPollVotes.f951id = this.messageObject.getId();
                tLRPC$TL_messages_getPollVotes.limit = 50;
                int i3 = tLRPC$TL_messages_getPollVotes.flags | 1;
                tLRPC$TL_messages_getPollVotes.flags = i3;
                tLRPC$TL_messages_getPollVotes.option = votesList.option;
                tLRPC$TL_messages_getPollVotes.flags = i3 | 2;
                tLRPC$TL_messages_getPollVotes.offset = votesList.next_offset;
                this.chatActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_getPollVotes, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        PollVotesAlert.this.lambda$new$3(votesList, chatActivity, tLObject, tLRPC$TL_error);
                    }
                });
            } else if (view instanceof UserCell) {
                UserCell userCell = (UserCell) view;
                if (userCell.currentUser == null) {
                    return;
                }
                TLRPC$User currentUser = chatActivity.getCurrentUser();
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", userCell.currentUser.f995id);
                dismiss();
                ProfileActivity profileActivity = new ProfileActivity(bundle);
                if (currentUser != null && currentUser.f995id == userCell.currentUser.f995id) {
                    i2 = 1;
                }
                profileActivity.setPlayProfileAnimation(i2);
                chatActivity.presentFragment(profileActivity);
            }
        }
    }

    public void lambda$new$3(final VotesList votesList, final ChatActivity chatActivity, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PollVotesAlert.this.lambda$new$2(votesList, tLObject, chatActivity);
            }
        });
    }

    public void lambda$new$2(VotesList votesList, TLObject tLObject, ChatActivity chatActivity) {
        if (isShowing()) {
            this.loadingMore.remove(votesList);
            if (tLObject != null) {
                TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = (TLRPC$TL_messages_votesList) tLObject;
                chatActivity.getMessagesController().putUsers(tLRPC$TL_messages_votesList.users, false);
                votesList.votes.addAll(tLRPC$TL_messages_votesList.votes);
                votesList.next_offset = tLRPC$TL_messages_votesList.next_offset;
                animateSectionUpdates(null);
                this.listAdapter.update(true);
            }
        }
    }

    private void updateButtons() {
        this.votesPercents.clear();
        TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) this.messageObject.messageOwner.media;
        ArrayList arrayList = new ArrayList();
        int size = this.voters.size();
        int i = 100;
        boolean z = false;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            VotesList votesList = this.voters.get(i4);
            Button button = new Button();
            arrayList.add(button);
            this.votesPercents.put(votesList, button);
            if (!tLRPC$TL_messageMediaPoll.results.results.isEmpty()) {
                int size2 = tLRPC$TL_messageMediaPoll.results.results.size();
                int i5 = 0;
                while (true) {
                    if (i5 < size2) {
                        TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters = tLRPC$TL_messageMediaPoll.results.results.get(i5);
                        if (Arrays.equals(votesList.option, tLRPC$TL_pollAnswerVoters.option)) {
                            button.votesCount = tLRPC$TL_pollAnswerVoters.voters;
                            button.decimal = (tLRPC$TL_pollAnswerVoters.voters / tLRPC$TL_messageMediaPoll.results.total_voters) * 100.0f;
                            button.percent = (int) button.decimal;
                            Button.access$3924(button, button.percent);
                            if (i2 == 0) {
                                i2 = button.percent;
                            } else if (button.percent != 0 && i2 != button.percent) {
                                z = true;
                            }
                            i -= button.percent;
                            i3 = Math.max(button.percent, i3);
                        } else {
                            i5++;
                        }
                    }
                }
            }
        }
        if (!z || i == 0) {
            return;
        }
        Collections.sort(arrayList, PollVotesAlert$$ExternalSyntheticLambda2.INSTANCE);
        int min = Math.min(i, arrayList.size());
        for (int i6 = 0; i6 < min; i6++) {
            Button.access$4012((Button) arrayList.get(i6), 1);
        }
    }

    public static int lambda$updateButtons$5(Button button, Button button2) {
        if (button.decimal > button2.decimal) {
            return -1;
        }
        return button.decimal < button2.decimal ? 1 : 0;
    }

    @Override
    public void dismissInternal() {
        int size = this.queries.size();
        for (int i = 0; i < size; i++) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.queries.get(i).intValue(), true);
        }
        super.dismissInternal();
    }

    @SuppressLint({"NewApi"})
    public void updateLayout(boolean z) {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int m36dp = AndroidUtilities.m36dp(7.0f);
        if (top < AndroidUtilities.m36dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = m36dp;
        }
        boolean z2 = top <= AndroidUtilities.m36dp(12.0f);
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(180L);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            C1133ActionBar c1133ActionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z2 ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(c1133ActionBar, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = z2 ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    PollVotesAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int m36dp2 = top + (layoutParams.topMargin - AndroidUtilities.m36dp(11.0f));
        if (this.scrollOffsetY != m36dp2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = m36dp2;
            recyclerListView2.setTopGlowOffset(m36dp2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    public void updatePlaceholder() {
        if (this.placeholderPaint == null) {
            return;
        }
        int color = Theme.getColor("dialogBackground");
        int color2 = Theme.getColor("dialogBackgroundGray");
        int averageColor = AndroidUtilities.getAverageColor(color2, color);
        this.placeholderPaint.setColor(color2);
        float m36dp = AndroidUtilities.m36dp(500.0f);
        this.gradientWidth = m36dp;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, m36dp, 0.0f, new int[]{color2, averageColor, color2}, new float[]{0.0f, 0.18f, 0.36f}, Shader.TileMode.REPEAT);
        this.placeholderGradient = linearGradient;
        this.placeholderPaint.setShader(linearGradient);
        Matrix matrix = new Matrix();
        this.placeholderMatrix = matrix;
        this.placeholderGradient.setLocalMatrix(matrix);
    }

    public class Adapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        @Override
        public String getLetter(int i) {
            return null;
        }

        public Adapter(Context context) {
            int i = UserConfig.selectedAccount;
            this.mContext = context;
        }

        @Override
        public Object getItem(int i, int i2) {
            int i3;
            if (i == 0) {
                return 293145;
            }
            int i4 = i - 1;
            if (i2 != 0) {
                if (i4 < 0 || i4 >= PollVotesAlert.this.voters.size() || (i3 = i2 - 1) >= ((VotesList) PollVotesAlert.this.voters.get(i4)).getCount()) {
                    return -182734;
                }
                return Integer.valueOf(Objects.hash(Long.valueOf(((VotesList) PollVotesAlert.this.voters.get(i4)).votes.get(i3).user_id)));
            }
            return -928312;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (i == 0 || i2 == 0) {
                return false;
            }
            return PollVotesAlert.this.queries == null || PollVotesAlert.this.queries.isEmpty();
        }

        @Override
        public int getSectionCount() {
            return PollVotesAlert.this.voters.size() + 1;
        }

        @Override
        public int getCountForSection(int i) {
            int i2 = 1;
            if (i == 0) {
                return 1;
            }
            VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
            int count = votesList.getCount() + 1;
            if (TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                i2 = 0;
            }
            return count + i2;
        }

        private SectionCell createSectionCell() {
            return new SectionCell(this.mContext) {
                {
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                }

                @Override
                protected void onCollapseClick() {
                    VotesList votesList = (VotesList) getTag(C1072R.C1074id.object_tag);
                    if (votesList.votes.size() <= 15) {
                        return;
                    }
                    boolean z = !votesList.collapsed;
                    votesList.collapsed = z;
                    if (z) {
                        votesList.collapsedCount = 10;
                    }
                    PollVotesAlert.this.animateSectionUpdates(this);
                    PollVotesAlert.this.listAdapter.update(true);
                }
            };
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = createSectionCell();
            }
            SectionCell sectionCell = (SectionCell) view;
            if (i == 0) {
                sectionCell.setAlpha(0.0f);
            } else {
                view.setAlpha(1.0f);
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                int i2 = 0;
                int size = PollVotesAlert.this.poll.answers.size();
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = PollVotesAlert.this.poll.answers.get(i2);
                    if (Arrays.equals(tLRPC$TL_pollAnswer.option, votesList.option) && ((Button) PollVotesAlert.this.votesPercents.get(votesList)) != null) {
                        sectionCell.setText(tLRPC$TL_pollAnswer.text, PollVotesAlert.this.calcPercent(votesList.option), votesList.count, votesList.getCollapsed(), false);
                        sectionCell.setTag(C1072R.C1074id.object_tag, votesList);
                        break;
                    }
                    i2++;
                }
            }
            return view;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextCell textCell;
            if (i == 0) {
                textCell = new UserCell(this.mContext);
            } else if (i == 1) {
                if (PollVotesAlert.this.titleTextView.getParent() != null) {
                    ((ViewGroup) PollVotesAlert.this.titleTextView.getParent()).removeView(PollVotesAlert.this.titleTextView);
                }
                textCell = PollVotesAlert.this.titleTextView;
            } else if (i == 2) {
                textCell = createSectionCell();
            } else {
                TextCell textCell2 = new TextCell(this.mContext, 23, true);
                textCell2.setOffsetFromImage(65);
                textCell2.setBackgroundColor(PollVotesAlert.this.getThemedColor("dialogBackground"));
                textCell2.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                textCell = textCell2;
            }
            return new RecyclerListView.Holder(textCell);
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 2) {
                if (itemViewType != 3) {
                    return;
                }
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                ((TextCell) viewHolder.itemView).setTextAndIcon(LocaleController.formatPluralString("ShowVotes", votesList.count - votesList.getCount(), new Object[0]), C1072R.C1073drawable.arrow_more, false);
                return;
            }
            SectionCell sectionCell = (SectionCell) viewHolder.itemView;
            VotesList votesList2 = (VotesList) PollVotesAlert.this.voters.get(i - 1);
            votesList2.votes.get(0);
            int size = PollVotesAlert.this.poll.answers.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = PollVotesAlert.this.poll.answers.get(i3);
                if (Arrays.equals(tLRPC$TL_pollAnswer.option, votesList2.option) && ((Button) PollVotesAlert.this.votesPercents.get(votesList2)) != null) {
                    sectionCell.setText(tLRPC$TL_pollAnswer.text, PollVotesAlert.this.calcPercent(votesList2.option), votesList2.count, votesList2.getCollapsed(), false);
                    sectionCell.setTag(C1072R.C1074id.object_tag, votesList2);
                    return;
                }
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                int adapterPosition = viewHolder.getAdapterPosition();
                int sectionForPosition = getSectionForPosition(adapterPosition);
                int positionInSectionForPosition = getPositionInSectionForPosition(adapterPosition) - 1;
                UserCell userCell = (UserCell) viewHolder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(sectionForPosition - 1);
                TLRPC$MessageUserVote tLRPC$MessageUserVote = votesList.votes.get(positionInSectionForPosition);
                TLRPC$User user = tLRPC$MessageUserVote.user_id != 0 ? PollVotesAlert.this.chatActivity.getMessagesController().getUser(Long.valueOf(tLRPC$MessageUserVote.user_id)) : null;
                boolean z = true;
                if (positionInSectionForPosition == votesList.getCount() - 1 && TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    z = false;
                }
                userCell.setData(user, positionInSectionForPosition, z);
            }
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            if (i2 == 0) {
                return 2;
            }
            return i2 + (-1) < ((VotesList) PollVotesAlert.this.voters.get(i + (-1))).getCount() ? 0 : 3;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }
    }

    public int calcPercent(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.voters.size(); i3++) {
            VotesList votesList = this.voters.get(i3);
            if (votesList != null) {
                i += votesList.count;
                if (Arrays.equals(votesList.option, bArr)) {
                    i2 += votesList.count;
                }
            }
        }
        if (i <= 0) {
            return 0;
        }
        return Math.round((i2 / i) * 100.0f);
    }

    public void animateSectionUpdates(View view) {
        View pinnedHeader;
        int i = -2;
        while (i < this.listView.getChildCount()) {
            if (i == -2) {
                pinnedHeader = view;
            } else {
                pinnedHeader = i == -1 ? this.listView.getPinnedHeader() : this.listView.getChildAt(i);
            }
            if (pinnedHeader instanceof SectionCell) {
                int i2 = C1072R.C1074id.object_tag;
                if (pinnedHeader.getTag(i2) instanceof VotesList) {
                    SectionCell sectionCell = (SectionCell) pinnedHeader;
                    VotesList votesList = (VotesList) pinnedHeader.getTag(i2);
                    int i3 = 0;
                    int size = this.poll.answers.size();
                    while (true) {
                        if (i3 < size) {
                            TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = this.poll.answers.get(i3);
                            if (Arrays.equals(tLRPC$TL_pollAnswer.option, votesList.option) && this.votesPercents.get(votesList) != null) {
                                sectionCell.setText(tLRPC$TL_pollAnswer.text, calcPercent(votesList.option), votesList.count, votesList.getCollapsed(), true);
                                sectionCell.setTag(C1072R.C1074id.object_tag, votesList);
                                break;
                            }
                            i3++;
                        }
                    }
                }
            }
            i++;
        }
        this.listView.relayoutPinnedHeader();
        this.listView.invalidate();
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                PollVotesAlert.this.updatePlaceholder();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.containerView, 0, null, null, null, null, "key_sheet_scrollUp"));
        arrayList.add(new ThemeDescription(this.containerView, 0, null, null, new Drawable[]{this.shadowDrawable}, null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "player_actionBarSubtitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBarShadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "dialogBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"middleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"righTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        return arrayList;
    }
}
