package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messages_chatFull;
import org.telegram.tgnet.TLRPC$TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReactionsList;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReadParticipants;
import org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList;
import org.telegram.tgnet.TLRPC$TL_readParticipantDate;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.Theme;

public class ReactedHeaderView extends FrameLayout {
    private AvatarsImageView avatarsImageView;
    private int currentAccount;
    private long dialogId;
    private int fixedWidth;
    private FlickerLoadingView flickerLoadingView;
    private ImageView iconView;
    private boolean ignoreLayout;
    private boolean isLoaded;
    private MessageObject message;
    private BackupImageView reactView;
    private Consumer seenCallback;
    private List seenUsers;
    private TextView titleView;
    private List users;

    public static class UserSeen {
        public int date;
        long dialogId;
        public TLObject user;

        public UserSeen(TLObject tLObject, int i) {
            long j;
            this.user = tLObject;
            this.date = i;
            if (tLObject instanceof TLRPC$User) {
                j = ((TLRPC$User) tLObject).id;
            } else if (!(tLObject instanceof TLRPC$Chat)) {
                return;
            } else {
                j = -((TLRPC$Chat) tLObject).id;
            }
            this.dialogId = j;
        }
    }

    public ReactedHeaderView(Context context, int i, MessageObject messageObject, long j) {
        super(context);
        this.seenUsers = new ArrayList();
        this.users = new ArrayList();
        this.currentAccount = i;
        this.message = messageObject;
        this.dialogId = j;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setColors(Theme.key_actionBarDefaultSubmenuBackground, Theme.key_listSelector, -1);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        this.titleView.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView;
        avatarsImageView.setStyle(11);
        this.avatarsImageView.setAvatarsTextSize(AndroidUtilities.dp(22.0f));
        addView(this.avatarsImageView, LayoutHelper.createFrameRelatively(56.0f, -1.0f, 8388629, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.msg_reactions).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(mutate);
        this.iconView.setVisibility(8);
        BackupImageView backupImageView = new BackupImageView(context);
        this.reactView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setAlpha(0.0f);
        this.avatarsImageView.setAlpha(0.0f);
        setBackground(Theme.getSelectorDrawable(false));
    }

    public void lambda$loadReactions$6(int i, TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList) {
        String formatPluralString;
        if (this.seenUsers.isEmpty() || this.seenUsers.size() < i) {
            formatPluralString = LocaleController.formatPluralString("ReactionsCount", i, new Object[0]);
        } else {
            formatPluralString = String.format(LocaleController.getPluralString("Reacted", i), i == this.seenUsers.size() ? String.valueOf(i) : i + "/" + this.seenUsers.size());
        }
        if (getMeasuredWidth() > 0) {
            this.fixedWidth = getMeasuredWidth();
        }
        this.titleView.setText(formatPluralString);
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions = this.message.messageOwner.reactions;
        if (tLRPC$TL_messageReactions != null && tLRPC$TL_messageReactions.results.size() == 1 && !tLRPC$TL_messages_messageReactionsList.reactions.isEmpty()) {
            for (TLRPC$TL_availableReaction tLRPC$TL_availableReaction : MediaDataController.getInstance(this.currentAccount).getReactionsList()) {
                if (tLRPC$TL_availableReaction.reaction.equals(((TLRPC$MessagePeerReaction) tLRPC$TL_messages_messageReactionsList.reactions.get(0)).reaction)) {
                    this.reactView.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), "40_40_lastreactframe", "webp", (Drawable) null, tLRPC$TL_availableReaction);
                    this.reactView.setVisibility(0);
                    this.reactView.setAlpha(0.0f);
                    this.reactView.animate().alpha(1.0f).start();
                    this.iconView.setVisibility(8);
                    break;
                }
            }
        }
        this.iconView.setVisibility(0);
        this.iconView.setAlpha(0.0f);
        this.iconView.animate().alpha(1.0f).start();
        Iterator it = tLRPC$TL_messages_messageReactionsList.users.iterator();
        while (it.hasNext()) {
            TLRPC$User tLRPC$User = (TLRPC$User) it.next();
            TLRPC$Peer tLRPC$Peer = this.message.messageOwner.from_id;
            if (tLRPC$Peer != null && tLRPC$User.id != tLRPC$Peer.user_id) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.users.size()) {
                        this.users.add(new UserSeen(tLRPC$User, 0));
                        break;
                    } else if (((UserSeen) this.users.get(i2)).dialogId == tLRPC$User.id) {
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }
        Iterator it2 = tLRPC$TL_messages_messageReactionsList.chats.iterator();
        while (it2.hasNext()) {
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) it2.next();
            TLRPC$Peer tLRPC$Peer2 = this.message.messageOwner.from_id;
            if (tLRPC$Peer2 != null && tLRPC$Chat.id != tLRPC$Peer2.user_id) {
                int i3 = 0;
                while (true) {
                    if (i3 >= this.users.size()) {
                        this.users.add(new UserSeen(tLRPC$Chat, 0));
                        break;
                    } else if (((UserSeen) this.users.get(i3)).dialogId == (-tLRPC$Chat.id)) {
                        break;
                    } else {
                        i3++;
                    }
                }
            }
        }
        updateView();
    }

    public void lambda$loadReactions$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_messages_messageReactionsList) {
            final TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList = (TLRPC$TL_messages_messageReactionsList) tLObject;
            final int i = tLRPC$TL_messages_messageReactionsList.count;
            tLRPC$TL_messages_messageReactionsList.users.size();
            post(new Runnable() {
                @Override
                public final void run() {
                    ReactedHeaderView.this.lambda$loadReactions$6(i, tLRPC$TL_messages_messageReactionsList);
                }
            });
        }
    }

    public void lambda$onAttachedToWindow$0(List list) {
        this.seenUsers.addAll(list);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            UserSeen userSeen = (UserSeen) it.next();
            int i = 0;
            while (true) {
                if (i >= this.users.size()) {
                    this.users.add(userSeen);
                    break;
                } else if (MessageObject.getObjectPeerId(((UserSeen) this.users.get(i)).user) != MessageObject.getObjectPeerId(userSeen.user)) {
                    i++;
                } else if (userSeen.date > 0) {
                    ((UserSeen) this.users.get(i)).date = userSeen.date;
                }
            }
        }
        Consumer consumer = this.seenCallback;
        if (consumer != null) {
            consumer.accept(list);
        }
        loadReactions();
    }

    public void lambda$onAttachedToWindow$1(TLObject tLObject, List list, List list2, List list3, Runnable runnable) {
        if (tLObject != null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            for (int i = 0; i < tLRPC$TL_channels_channelParticipants.users.size(); i++) {
                TLRPC$User tLRPC$User = (TLRPC$User) tLRPC$TL_channels_channelParticipants.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(tLRPC$User, false);
                int indexOf = list.indexOf(Long.valueOf(tLRPC$User.id));
                if (!tLRPC$User.self && indexOf >= 0) {
                    list2.add(new UserSeen(tLRPC$User, ((Integer) list3.get(indexOf)).intValue()));
                }
            }
        }
        runnable.run();
    }

    public void lambda$onAttachedToWindow$2(final List list, final List list2, final List list3, final Runnable runnable, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ReactedHeaderView.this.lambda$onAttachedToWindow$1(tLObject, list, list2, list3, runnable);
            }
        });
    }

    public void lambda$onAttachedToWindow$3(TLObject tLObject, List list, List list2, List list3, Runnable runnable) {
        if (tLObject != null) {
            TLRPC$TL_messages_chatFull tLRPC$TL_messages_chatFull = (TLRPC$TL_messages_chatFull) tLObject;
            for (int i = 0; i < tLRPC$TL_messages_chatFull.users.size(); i++) {
                TLRPC$User tLRPC$User = (TLRPC$User) tLRPC$TL_messages_chatFull.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(tLRPC$User, false);
                int indexOf = list.indexOf(Long.valueOf(tLRPC$User.id));
                if (!tLRPC$User.self && indexOf >= 0) {
                    list2.add(new UserSeen(tLRPC$User, ((Integer) list3.get(indexOf)).intValue()));
                }
            }
        }
        runnable.run();
    }

    public void lambda$onAttachedToWindow$4(final List list, final List list2, final List list3, final Runnable runnable, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ReactedHeaderView.this.lambda$onAttachedToWindow$3(tLObject, list, list2, list3, runnable);
            }
        });
    }

    public void lambda$onAttachedToWindow$5(long j, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC$TL_messages_getFullChat tLRPC$TL_messages_getFullChat;
        int i;
        if (tLObject instanceof TLRPC$Vector) {
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            Iterator it = ((TLRPC$Vector) tLObject).objects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof Long) {
                    Long l = (Long) next;
                    if (j != l.longValue()) {
                        arrayList.add(l);
                        i = 0;
                        arrayList2.add(i);
                    }
                } else if (next instanceof TLRPC$TL_readParticipantDate) {
                    TLRPC$TL_readParticipantDate tLRPC$TL_readParticipantDate = (TLRPC$TL_readParticipantDate) next;
                    long j2 = tLRPC$TL_readParticipantDate.user_id;
                    int i2 = tLRPC$TL_readParticipantDate.date;
                    if (j != j2) {
                        arrayList.add(Long.valueOf(j2));
                        i = Integer.valueOf(i2);
                        arrayList2.add(i);
                    }
                }
            }
            arrayList.add(Long.valueOf(j));
            arrayList2.add(0);
            final ArrayList arrayList3 = new ArrayList();
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    ReactedHeaderView.this.lambda$onAttachedToWindow$0(arrayList3);
                }
            };
            if (ChatObject.isChannel(tLRPC$Chat)) {
                TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
                tLRPC$TL_channels_getParticipants.limit = MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold;
                tLRPC$TL_channels_getParticipants.offset = 0;
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
                tLRPC$TL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(tLRPC$Chat.id);
                ConnectionsManager connectionsManager2 = ConnectionsManager.getInstance(this.currentAccount);
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                        ReactedHeaderView.this.lambda$onAttachedToWindow$2(arrayList, arrayList3, arrayList2, runnable, tLObject2, tLRPC$TL_error2);
                    }
                };
                tLRPC$TL_messages_getFullChat = tLRPC$TL_channels_getParticipants;
                connectionsManager = connectionsManager2;
            } else {
                TLRPC$TL_messages_getFullChat tLRPC$TL_messages_getFullChat2 = new TLRPC$TL_messages_getFullChat();
                tLRPC$TL_messages_getFullChat2.chat_id = tLRPC$Chat.id;
                ConnectionsManager connectionsManager3 = ConnectionsManager.getInstance(this.currentAccount);
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error2) {
                        ReactedHeaderView.this.lambda$onAttachedToWindow$4(arrayList, arrayList3, arrayList2, runnable, tLObject2, tLRPC$TL_error2);
                    }
                };
                tLRPC$TL_messages_getFullChat = tLRPC$TL_messages_getFullChat2;
                connectionsManager = connectionsManager3;
            }
            connectionsManager.sendRequest(tLRPC$TL_messages_getFullChat, requestDelegate);
        }
    }

    private void loadReactions() {
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC$TL_messages_getMessageReactionsList tLRPC$TL_messages_getMessageReactionsList = new TLRPC$TL_messages_getMessageReactionsList();
        tLRPC$TL_messages_getMessageReactionsList.peer = messagesController.getInputPeer(this.message.getDialogId());
        tLRPC$TL_messages_getMessageReactionsList.id = this.message.getId();
        tLRPC$TL_messages_getMessageReactionsList.limit = 3;
        tLRPC$TL_messages_getMessageReactionsList.reaction = null;
        tLRPC$TL_messages_getMessageReactionsList.offset = null;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getMessageReactionsList, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ReactedHeaderView.this.lambda$loadReactions$7(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    private void updateView() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactedHeaderView.updateView():void");
    }

    public List<UserSeen> getSeenUsers() {
        return this.seenUsers;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLoaded) {
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        final TLRPC$Chat chat = messagesController.getChat(Long.valueOf(this.message.getChatId()));
        TLRPC$ChatFull chatFull = messagesController.getChatFull(this.message.getChatId());
        if (chat == null || !this.message.isOutOwner() || !this.message.isSent() || this.message.isEditing() || this.message.isSending() || this.message.isSendError() || this.message.isContentUnread() || this.message.isUnread() || ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.message.messageOwner.date >= 604800 || ((!ChatObject.isMegagroup(chat) && ChatObject.isChannel(chat)) || chatFull == null || chatFull.participants_count > MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold || (this.message.messageOwner.action instanceof TLRPC$TL_messageActionChatJoinedByRequest))) {
            loadReactions();
            return;
        }
        TLRPC$TL_messages_getMessageReadParticipants tLRPC$TL_messages_getMessageReadParticipants = new TLRPC$TL_messages_getMessageReadParticipants();
        tLRPC$TL_messages_getMessageReadParticipants.msg_id = this.message.getId();
        tLRPC$TL_messages_getMessageReadParticipants.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.message.getDialogId());
        TLRPC$Peer tLRPC$Peer = this.message.messageOwner.from_id;
        final long j = tLRPC$Peer != null ? tLRPC$Peer.user_id : 0L;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getMessageReadParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ReactedHeaderView.this.lambda$onAttachedToWindow$5(j, chat, tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3 = this.fixedWidth;
        if (i3 > 0) {
            i = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
        }
        if (this.flickerLoadingView.getVisibility() == 0) {
            this.ignoreLayout = true;
            this.flickerLoadingView.setVisibility(8);
            super.onMeasure(i, i2);
            this.flickerLoadingView.getLayoutParams().width = getMeasuredWidth();
            this.flickerLoadingView.setVisibility(0);
            this.ignoreLayout = false;
        }
        super.onMeasure(i, i2);
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setSeenCallback(Consumer consumer) {
        this.seenCallback = consumer;
    }
}
