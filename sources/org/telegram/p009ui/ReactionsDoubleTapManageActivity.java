package org.telegram.p009ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C1010R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.p009ui.ActionBar.BaseFragment;
import org.telegram.p009ui.ActionBar.C1069ActionBar;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.ActionBar.ThemeDescription;
import org.telegram.p009ui.Cells.AvailableReactionCell;
import org.telegram.p009ui.Cells.TextInfoPrivacyCell;
import org.telegram.p009ui.Cells.ThemePreviewMessagesCell;
import org.telegram.p009ui.Components.AnimatedEmojiDrawable;
import org.telegram.p009ui.Components.LayoutHelper;
import org.telegram.p009ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.p009ui.Components.RecyclerListView;
import org.telegram.p009ui.Components.SimpleThemeDescription;
import org.telegram.p009ui.SelectAnimatedEmojiDialog;
import org.telegram.tgnet.TLRPC$TL_availableReaction;

public class ReactionsDoubleTapManageActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private LinearLayout contentView;
    int infoRow;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    int premiumReactionRow;
    int previewRow;
    int reactionsStartRow = -1;
    int rowCount;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        return super.onFragmentCreate();
    }

    @Override
    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", C1010R.string.Reactions));
        this.actionBar.setBackButtonImage(C1010R.C1011drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C1069ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    ReactionsDoubleTapManageActivity.this.finishFragment();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setSupportsChangeAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        RecyclerListView.SelectionAdapter selectionAdapter = new RecyclerListView.SelectionAdapter() {
            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 2;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                SetDefaultReactionCell setDefaultReactionCell;
                if (i == 0) {
                    ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, ((BaseFragment) ReactionsDoubleTapManageActivity.this).parentLayout, 2);
                    if (Build.VERSION.SDK_INT >= 19) {
                        themePreviewMessagesCell.setImportantForAccessibility(4);
                    }
                    themePreviewMessagesCell.fragment = ReactionsDoubleTapManageActivity.this;
                    setDefaultReactionCell = themePreviewMessagesCell;
                } else if (i == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
                    textInfoPrivacyCell.setText(LocaleController.getString("DoubleTapPreviewRational", C1010R.string.DoubleTapPreviewRational));
                    textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(context, C1010R.C1011drawable.greydivider, "windowBackgroundGrayShadow"));
                    setDefaultReactionCell = textInfoPrivacyCell;
                } else if (i == 3) {
                    SetDefaultReactionCell setDefaultReactionCell2 = new SetDefaultReactionCell(context);
                    setDefaultReactionCell2.update(false);
                    setDefaultReactionCell = setDefaultReactionCell2;
                } else if (i == 4) {
                    View view = new View(this, context) {
                        @Override
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m35dp(16.0f), 1073741824));
                        }
                    };
                    view.setBackground(Theme.getThemedDrawable(context, C1010R.C1011drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    setDefaultReactionCell = view;
                } else {
                    setDefaultReactionCell = new AvailableReactionCell(context, true, true);
                }
                return new RecyclerListView.Holder(setDefaultReactionCell);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (getItemViewType(i) != 1) {
                    return;
                }
                TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) ReactionsDoubleTapManageActivity.this.getAvailableReactions().get(i - ReactionsDoubleTapManageActivity.this.reactionsStartRow);
                ((AvailableReactionCell) viewHolder.itemView).bind(tLRPC$TL_availableReaction, tLRPC$TL_availableReaction.reaction.contains(MediaDataController.getInstance(((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount).getDoubleTapReaction()), ((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount);
            }

            @Override
            public int getItemCount() {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                return reactionsDoubleTapManageActivity.rowCount + (reactionsDoubleTapManageActivity.premiumReactionRow < 0 ? reactionsDoubleTapManageActivity.getAvailableReactions().size() : 0) + 1;
            }

            @Override
            public int getItemViewType(int i) {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                if (i == reactionsDoubleTapManageActivity.previewRow) {
                    return 0;
                }
                if (i == reactionsDoubleTapManageActivity.infoRow) {
                    return 2;
                }
                if (i == reactionsDoubleTapManageActivity.premiumReactionRow) {
                    return 3;
                }
                return i == getItemCount() - 1 ? 4 : 1;
            }
        };
        this.listAdapter = selectionAdapter;
        recyclerListView2.setAdapter(selectionAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                ReactionsDoubleTapManageActivity.this.lambda$createView$0(view, i);
            }
        });
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.contentView = linearLayout;
        this.fragmentView = linearLayout;
        updateColors();
        updateRows();
        return this.contentView;
    }

    public void lambda$createView$0(View view, int i) {
        if (view instanceof AvailableReactionCell) {
            AvailableReactionCell availableReactionCell = (AvailableReactionCell) view;
            if (availableReactionCell.locked && !getUserConfig().isPremium()) {
                showDialog(new PremiumFeatureBottomSheet(this, 4, true));
                return;
            }
            MediaDataController.getInstance(this.currentAccount).setDoubleTapReaction(availableReactionCell.react.reaction);
            this.listView.getAdapter().notifyItemRangeChanged(0, this.listView.getAdapter().getItemCount());
        } else if (view instanceof SetDefaultReactionCell) {
            showSelectStatusDialog((SetDefaultReactionCell) view);
        }
    }

    public class SetDefaultReactionCell extends FrameLayout {
        private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable imageDrawable;
        private TextView textView;

        public SetDefaultReactionCell(Context context) {
            super(context);
            setBackgroundColor(ReactionsDoubleTapManageActivity.this.getThemedColor("windowBackgroundWhite"));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 16.0f);
            this.textView.setTextColor(ReactionsDoubleTapManageActivity.this.getThemedColor("windowBackgroundWhiteBlackText"));
            this.textView.setText(LocaleController.getString("DoubleTapSetting", C1010R.string.DoubleTapSetting));
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 23, 20.0f, 0.0f, 48.0f, 0.0f));
            this.imageDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.m35dp(24.0f));
        }

        public void update(boolean z) {
            String doubleTapReaction = MediaDataController.getInstance(((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount).getDoubleTapReaction();
            if (doubleTapReaction != null && doubleTapReaction.startsWith("animated_")) {
                try {
                    this.imageDrawable.set(Long.parseLong(doubleTapReaction.substring(9)), z);
                    return;
                } catch (Exception unused) {
                }
            }
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount).getReactionsMap().get(doubleTapReaction);
            if (tLRPC$TL_availableReaction != null) {
                this.imageDrawable.set(tLRPC$TL_availableReaction.static_icon, z);
            }
        }

        public void updateImageBounds() {
            this.imageDrawable.setBounds((getWidth() - this.imageDrawable.getIntrinsicWidth()) - AndroidUtilities.m35dp(21.0f), (getHeight() - this.imageDrawable.getIntrinsicHeight()) / 2, getWidth() - AndroidUtilities.m35dp(21.0f), (getHeight() + this.imageDrawable.getIntrinsicHeight()) / 2);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            updateImageBounds();
            this.imageDrawable.draw(canvas);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m35dp(50.0f), 1073741824));
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageDrawable.detach();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageDrawable.attach();
        }
    }

    public void showSelectStatusDialog(final org.telegram.p009ui.ReactionsDoubleTapManageActivity.SetDefaultReactionCell r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.ReactionsDoubleTapManageActivity.showSelectStatusDialog(org.telegram.ui.ReactionsDoubleTapManageActivity$SetDefaultReactionCell):void");
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.previewRow = 0;
        this.rowCount = i + 1;
        this.infoRow = i;
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.reactionsStartRow = -1;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.premiumReactionRow = i2;
            return;
        }
        this.premiumReactionRow = -1;
        this.reactionsStartRow = this.rowCount;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public List<TLRPC$TL_availableReaction> getAvailableReactions() {
        return getMediaDataController().getReactionsList();
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ReactionsDoubleTapManageActivity.this.updateColors();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listAdapter.notifyDataSetChanged();
    }

    @Override
    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 != this.currentAccount) {
            return;
        }
        if (i == NotificationCenter.reactionsDidLoad) {
            this.listAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            updateRows();
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
