package org.telegram.messenger;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$ChatReactions;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_channel;
import org.telegram.tgnet.TLRPC$TL_channelForbidden;
import org.telegram.tgnet.TLRPC$TL_channel_layer48;
import org.telegram.tgnet.TLRPC$TL_channel_layer67;
import org.telegram.tgnet.TLRPC$TL_channel_layer72;
import org.telegram.tgnet.TLRPC$TL_channel_layer77;
import org.telegram.tgnet.TLRPC$TL_channel_layer92;
import org.telegram.tgnet.TLRPC$TL_channel_old;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatEmpty;
import org.telegram.tgnet.TLRPC$TL_chatForbidden;
import org.telegram.tgnet.TLRPC$TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC$TL_chatReactionsAll;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_chat_layer92;
import org.telegram.tgnet.TLRPC$TL_chat_old;
import org.telegram.tgnet.TLRPC$TL_chat_old2;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideoSourceGroup;
import org.telegram.tgnet.TLRPC$TL_groupCallStreamChannel;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerColor;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallTitle;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_groupCall;
import org.telegram.tgnet.TLRPC$TL_phone_groupParticipants;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallRecord;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
import org.telegram.tgnet.TLRPC$TL_username;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.GroupCallActivity;

public class ChatObject {
    public static final int ACTION_ADD_ADMINS = 4;
    public static final int ACTION_BLOCK_USERS = 2;
    public static final int ACTION_CHANGE_INFO = 1;
    public static final int ACTION_DELETE_MESSAGES = 13;
    public static final int ACTION_EDIT_MESSAGES = 12;
    public static final int ACTION_EMBED_LINKS = 9;
    public static final int ACTION_INVITE = 3;
    public static final int ACTION_MANAGE_CALLS = 14;
    public static final int ACTION_MANAGE_TOPICS = 15;
    public static final int ACTION_PIN = 0;
    public static final int ACTION_POST = 5;
    public static final int ACTION_SEND = 6;
    public static final int ACTION_SEND_DOCUMENTS = 19;
    public static final int ACTION_SEND_GIFS = 23;
    public static final int ACTION_SEND_MEDIA = 7;
    public static final int ACTION_SEND_MUSIC = 18;
    public static final int ACTION_SEND_PHOTO = 16;
    public static final int ACTION_SEND_PLAIN = 22;
    public static final int ACTION_SEND_POLLS = 10;
    public static final int ACTION_SEND_ROUND = 21;
    public static final int ACTION_SEND_STICKERS = 8;
    public static final int ACTION_SEND_TEXT = 22;
    public static final int ACTION_SEND_VIDEO = 17;
    public static final int ACTION_SEND_VOICE = 20;
    public static final int ACTION_VIEW = 11;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_FORUM = 5;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;
    private static final int MAX_PARTICIPANTS_COUNT = 5000;
    public static final int VIDEO_FRAME_HAS_FRAME = 2;
    public static final int VIDEO_FRAME_NO_FRAME = 0;
    public static final int VIDEO_FRAME_REQUESTING = 1;

    public static class Call {
        public static final int RECORD_TYPE_AUDIO = 0;
        public static final int RECORD_TYPE_VIDEO_LANDSCAPE = 2;
        public static final int RECORD_TYPE_VIDEO_PORTAIT = 1;
        private static int videoPointer;
        public int activeVideos;
        public TLRPC$GroupCall call;
        public boolean canStreamVideo;
        public long chatId;
        private Runnable checkQueueRunnable;
        public AccountInstance currentAccount;
        private long lastGroupCallReloadTime;
        private int lastLoadGuid;
        public boolean loadedRtmpStreamParticipant;
        private boolean loadingGroupCall;
        public boolean loadingMembers;
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public boolean recording;
        public boolean reloadingMembers;
        public VideoParticipant rtmpStreamParticipant;
        public TLRPC$Peer selfPeer;
        public int speakingMembersCount;
        private boolean typingUpdateRunnableScheduled;
        private long updatesStartWaitTime;
        public VideoParticipant videoNotAvailableParticipant;
        public LongSparseArray participants = new LongSparseArray();
        public final ArrayList<TLRPC$TL_groupCallParticipant> sortedParticipants = new ArrayList<>();
        public final ArrayList<VideoParticipant> visibleVideoParticipants = new ArrayList<>();
        public final ArrayList<TLRPC$TL_groupCallParticipant> visibleParticipants = new ArrayList<>();
        public final HashMap<String, Bitmap> thumbs = new HashMap<>();
        private final HashMap<String, VideoParticipant> videoParticipantsCache = new HashMap<>();
        public ArrayList<Long> invitedUsers = new ArrayList<>();
        public HashSet<Long> invitedUsersMap = new HashSet<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsByVideoSources = new SparseArray<>();
        public SparseArray<TLRPC$TL_groupCallParticipant> participantsByPresentationSources = new SparseArray<>();
        private Runnable typingUpdateRunnable = new Runnable() {
            @Override
            public final void run() {
                ChatObject.Call.this.lambda$new$0();
            }
        };
        private HashSet<Integer> loadingGuids = new HashSet<>();
        private ArrayList<TLRPC$TL_updateGroupCallParticipants> updatesQueue = new ArrayList<>();
        private HashSet<Long> loadingUids = new HashSet<>();
        private HashSet<Long> loadingSsrcs = new HashSet<>();
        public final LongSparseArray currentSpeakingPeers = new LongSparseArray();
        private final Runnable updateCurrentSpeakingRunnable = new Runnable() {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                StringBuilder sb;
                String str;
                long uptimeMillis = SystemClock.uptimeMillis();
                int i = 0;
                boolean z = false;
                while (i < Call.this.currentSpeakingPeers.size()) {
                    long keyAt = Call.this.currentSpeakingPeers.keyAt(i);
                    if (uptimeMillis - ((TLRPC$TL_groupCallParticipant) Call.this.currentSpeakingPeers.get(keyAt)).lastSpeakTime >= 500) {
                        Call.this.currentSpeakingPeers.remove(keyAt);
                        MessagesController messagesController = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount());
                        if (keyAt > 0) {
                            TLRPC$User user = messagesController.getUser(Long.valueOf(keyAt));
                            sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(keyAt);
                            sb.append(" ");
                            if (user != null) {
                                str = user.first_name;
                                sb.append(str);
                                Log.d("GroupCall", sb.toString());
                                i--;
                                z = true;
                            }
                            str = null;
                            sb.append(str);
                            Log.d("GroupCall", sb.toString());
                            i--;
                            z = true;
                        } else {
                            TLRPC$Chat chat = messagesController.getChat(Long.valueOf(-keyAt));
                            sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(keyAt);
                            sb.append(" ");
                            if (chat != null) {
                                str = chat.title;
                                sb.append(str);
                                Log.d("GroupCall", sb.toString());
                                i--;
                                z = true;
                            }
                            str = null;
                            sb.append(str);
                            Log.d("GroupCall", sb.toString());
                            i--;
                            z = true;
                        }
                    }
                    i++;
                }
                if (Call.this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.runOnUIThread(Call.this.updateCurrentSpeakingRunnable, 550L);
                }
                if (z) {
                    Call.this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallSpeakingUsersUpdated, Long.valueOf(Call.this.chatId), Long.valueOf(Call.this.call.id), Boolean.FALSE);
                }
            }
        };

        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                StringBuilder sb;
                String str;
                long uptimeMillis = SystemClock.uptimeMillis();
                int i = 0;
                boolean z = false;
                while (i < Call.this.currentSpeakingPeers.size()) {
                    long keyAt = Call.this.currentSpeakingPeers.keyAt(i);
                    if (uptimeMillis - ((TLRPC$TL_groupCallParticipant) Call.this.currentSpeakingPeers.get(keyAt)).lastSpeakTime >= 500) {
                        Call.this.currentSpeakingPeers.remove(keyAt);
                        MessagesController messagesController = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount());
                        if (keyAt > 0) {
                            TLRPC$User user = messagesController.getUser(Long.valueOf(keyAt));
                            sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(keyAt);
                            sb.append(" ");
                            if (user != null) {
                                str = user.first_name;
                                sb.append(str);
                                Log.d("GroupCall", sb.toString());
                                i--;
                                z = true;
                            }
                            str = null;
                            sb.append(str);
                            Log.d("GroupCall", sb.toString());
                            i--;
                            z = true;
                        } else {
                            TLRPC$Chat chat = messagesController.getChat(Long.valueOf(-keyAt));
                            sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(keyAt);
                            sb.append(" ");
                            if (chat != null) {
                                str = chat.title;
                                sb.append(str);
                                Log.d("GroupCall", sb.toString());
                                i--;
                                z = true;
                            }
                            str = null;
                            sb.append(str);
                            Log.d("GroupCall", sb.toString());
                            i--;
                            z = true;
                        }
                    }
                    i++;
                }
                if (Call.this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.runOnUIThread(Call.this.updateCurrentSpeakingRunnable, 550L);
                }
                if (z) {
                    Call.this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallSpeakingUsersUpdated, Long.valueOf(Call.this.chatId), Long.valueOf(Call.this.call.id), Boolean.FALSE);
                }
            }
        }

        public interface OnParticipantsLoad {
            void onLoad(ArrayList<Long> arrayList);
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface RecordType {
        }

        private void checkOnlineParticipants() {
            if (this.typingUpdateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.typingUpdateRunnable);
                this.typingUpdateRunnableScheduled = false;
            }
            this.speakingMembersCount = 0;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            int size = this.sortedParticipants.size();
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.sortedParticipants.get(i2);
                int i3 = currentTime - tLRPC$TL_groupCallParticipant.active_date;
                if (i3 < 5) {
                    this.speakingMembersCount++;
                    i = Math.min(i3, i);
                }
                if (Math.max(tLRPC$TL_groupCallParticipant.date, tLRPC$TL_groupCallParticipant.active_date) <= currentTime - 5) {
                    break;
                }
            }
            if (i != Integer.MAX_VALUE) {
                AndroidUtilities.runOnUIThread(this.typingUpdateRunnable, i * 1000);
                this.typingUpdateRunnableScheduled = true;
            }
        }

        public void checkQueue() {
            this.checkQueueRunnable = null;
            if (this.updatesStartWaitTime != 0 && System.currentTimeMillis() - this.updatesStartWaitTime >= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("QUEUE GROUP CALL UPDATES WAIT TIMEOUT - CHECK QUEUE");
                }
                processUpdatesQueue();
            }
            if (this.updatesQueue.isEmpty()) {
                return;
            }
            ChatObject$Call$$ExternalSyntheticLambda0 chatObject$Call$$ExternalSyntheticLambda0 = new ChatObject$Call$$ExternalSyntheticLambda0(this);
            this.checkQueueRunnable = chatObject$Call$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(chatObject$Call$$ExternalSyntheticLambda0, 1000L);
        }

        private long getSelfId() {
            TLRPC$Peer tLRPC$Peer = this.selfPeer;
            return tLRPC$Peer != null ? MessageObject.getPeerId(tLRPC$Peer) : this.currentAccount.getUserConfig().getClientUserId();
        }

        private boolean isSameVideo(TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo, TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo2) {
            if ((tLRPC$TL_groupCallParticipantVideo == null && tLRPC$TL_groupCallParticipantVideo2 != null) || (tLRPC$TL_groupCallParticipantVideo != null && tLRPC$TL_groupCallParticipantVideo2 == null)) {
                return false;
            }
            if (tLRPC$TL_groupCallParticipantVideo != null && tLRPC$TL_groupCallParticipantVideo2 != null) {
                if (!TextUtils.equals(tLRPC$TL_groupCallParticipantVideo.endpoint, tLRPC$TL_groupCallParticipantVideo2.endpoint) || tLRPC$TL_groupCallParticipantVideo.source_groups.size() != tLRPC$TL_groupCallParticipantVideo2.source_groups.size()) {
                    return false;
                }
                int size = tLRPC$TL_groupCallParticipantVideo.source_groups.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = (TLRPC$TL_groupCallParticipantVideoSourceGroup) tLRPC$TL_groupCallParticipantVideo.source_groups.get(i);
                    TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup2 = (TLRPC$TL_groupCallParticipantVideoSourceGroup) tLRPC$TL_groupCallParticipantVideo2.source_groups.get(i);
                    if (!TextUtils.equals(tLRPC$TL_groupCallParticipantVideoSourceGroup.semantics, tLRPC$TL_groupCallParticipantVideoSourceGroup2.semantics) || tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size() != tLRPC$TL_groupCallParticipantVideoSourceGroup2.sources.size()) {
                        return false;
                    }
                    int size2 = tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        if (!tLRPC$TL_groupCallParticipantVideoSourceGroup2.sources.contains(tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.get(i2))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private int isValidUpdate(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants) {
            int i = this.call.version;
            int i2 = i + 1;
            int i3 = tLRPC$TL_updateGroupCallParticipants.version;
            if (i2 == i3 || i == i3) {
                return 0;
            }
            return i < i3 ? 1 : 2;
        }

        public void lambda$createRtmpStreamParticipant$1() {
            this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        public void lambda$loadGroupCall$10(TLObject tLObject) {
            this.lastGroupCallReloadTime = SystemClock.elapsedRealtime();
            this.loadingGroupCall = false;
            if (tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                TLRPC$GroupCall tLRPC$GroupCall = this.call;
                int i = tLRPC$GroupCall.participants_count;
                int i2 = tLRPC$TL_phone_groupParticipants.count;
                if (i != i2) {
                    tLRPC$GroupCall.participants_count = i2;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("new participants reload count " + this.call.participants_count);
                    }
                    this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
                }
            }
        }

        public void lambda$loadGroupCall$11(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatObject.Call.this.lambda$loadGroupCall$10(tLObject);
                }
            });
        }

        public void lambda$loadMembers$2(boolean z, TLObject tLObject, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants) {
            this.loadingMembers = false;
            if (z) {
                this.reloadingMembers = false;
            }
            if (tLObject != null) {
                TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                onParticipantsLoad(tLRPC$TL_phone_groupParticipants.participants, z, tLRPC$TL_phone_getGroupParticipants.offset, tLRPC$TL_phone_groupParticipants.next_offset, tLRPC$TL_phone_groupParticipants.version, tLRPC$TL_phone_groupParticipants.count);
            }
        }

        public void lambda$loadMembers$3(final boolean z, final TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatObject.Call.this.lambda$loadMembers$2(z, tLObject, tLRPC$TL_phone_getGroupParticipants);
                }
            });
        }

        public void lambda$loadUnknownParticipants$5(int i, TLObject tLObject, OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet) {
            if (this.loadingGuids.remove(Integer.valueOf(i))) {
                if (tLObject != null) {
                    TLRPC$TL_phone_groupParticipants tLRPC$TL_phone_groupParticipants = (TLRPC$TL_phone_groupParticipants) tLObject;
                    this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupParticipants.users, false);
                    this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupParticipants.chats, false);
                    int size = tLRPC$TL_phone_groupParticipants.participants.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) tLRPC$TL_phone_groupParticipants.participants.get(i2);
                        long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = (TLRPC$TL_groupCallParticipant) this.participants.get(peerId);
                        if (tLRPC$TL_groupCallParticipant2 != null) {
                            this.sortedParticipants.remove(tLRPC$TL_groupCallParticipant2);
                            processAllSources(tLRPC$TL_groupCallParticipant2, false);
                        }
                        this.participants.put(peerId, tLRPC$TL_groupCallParticipant);
                        this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                        processAllSources(tLRPC$TL_groupCallParticipant, true);
                        if (this.invitedUsersMap.contains(Long.valueOf(peerId))) {
                            Long valueOf = Long.valueOf(peerId);
                            this.invitedUsersMap.remove(valueOf);
                            this.invitedUsers.remove(valueOf);
                        }
                    }
                    if (this.call.participants_count < this.participants.size()) {
                        this.call.participants_count = this.participants.size();
                    }
                    sortParticipants();
                    this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
                    if (onParticipantsLoad != null) {
                        onParticipantsLoad.onLoad(arrayList);
                    } else {
                        setParticiapantsVolume();
                    }
                }
                hashSet.removeAll(arrayList);
            }
        }

        public void lambda$loadUnknownParticipants$6(final int i, final OnParticipantsLoad onParticipantsLoad, final ArrayList arrayList, final HashSet hashSet, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatObject.Call.this.lambda$loadUnknownParticipants$5(i, tLObject, onParticipantsLoad, arrayList, hashSet);
                }
            });
        }

        public void lambda$new$0() {
            this.typingUpdateRunnableScheduled = false;
            checkOnlineParticipants();
            this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallTypingsUpdated, new Object[0]);
        }

        public static int lambda$processUpdatesQueue$7(TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants, TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants2) {
            return AndroidUtilities.compare(tLRPC$TL_updateGroupCallParticipants.version, tLRPC$TL_updateGroupCallParticipants2.version);
        }

        public void lambda$reloadGroupCall$8(TLObject tLObject) {
            if (tLObject instanceof TLRPC$TL_phone_groupCall) {
                TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall = (TLRPC$TL_phone_groupCall) tLObject;
                this.call = tLRPC$TL_phone_groupCall.call;
                this.currentAccount.getMessagesController().putUsers(tLRPC$TL_phone_groupCall.users, false);
                this.currentAccount.getMessagesController().putChats(tLRPC$TL_phone_groupCall.chats, false);
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = tLRPC$TL_phone_groupCall.participants;
                String str = tLRPC$TL_phone_groupCall.participants_next_offset;
                TLRPC$GroupCall tLRPC$GroupCall = tLRPC$TL_phone_groupCall.call;
                onParticipantsLoad(arrayList, true, "", str, tLRPC$GroupCall.version, tLRPC$GroupCall.participants_count);
            }
        }

        public void lambda$reloadGroupCall$9(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatObject.Call.this.lambda$reloadGroupCall$8(tLObject);
                }
            });
        }

        public void lambda$setTitle$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            }
        }

        public int lambda$sortParticipants$12(long j, boolean z, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2) {
            int i;
            int i2;
            int i3;
            int i4 = tLRPC$TL_groupCallParticipant.videoIndex;
            boolean z2 = i4 > 0;
            int i5 = tLRPC$TL_groupCallParticipant2.videoIndex;
            boolean z3 = i5 > 0;
            if (z2 && z3) {
                return i5 - i4;
            }
            if (z2) {
                return -1;
            }
            if (z3) {
                return 1;
            }
            int i6 = tLRPC$TL_groupCallParticipant.active_date;
            if (i6 != 0 && (i3 = tLRPC$TL_groupCallParticipant2.active_date) != 0) {
                return Integer.compare(i3, i6);
            }
            if (i6 != 0) {
                return -1;
            }
            if (tLRPC$TL_groupCallParticipant2.active_date != 0) {
                return 1;
            }
            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) == j) {
                return -1;
            }
            if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant2.peer) == j) {
                return 1;
            }
            if (z) {
                long j2 = tLRPC$TL_groupCallParticipant.raise_hand_rating;
                if (j2 != 0) {
                    long j3 = tLRPC$TL_groupCallParticipant2.raise_hand_rating;
                    if (j3 != 0) {
                        return Long.compare(j3, j2);
                    }
                }
                if (j2 != 0) {
                    return -1;
                }
                if (tLRPC$TL_groupCallParticipant2.raise_hand_rating != 0) {
                    return 1;
                }
            }
            if (this.call.join_date_asc) {
                i = tLRPC$TL_groupCallParticipant.date;
                i2 = tLRPC$TL_groupCallParticipant2.date;
            } else {
                i = tLRPC$TL_groupCallParticipant2.date;
                i2 = tLRPC$TL_groupCallParticipant.date;
            }
            return Integer.compare(i, i2);
        }

        public void lambda$toggleRecord$13(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                this.currentAccount.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            }
        }

        private void loadGroupCall() {
            if (this.loadingGroupCall || SystemClock.elapsedRealtime() - this.lastGroupCallReloadTime < 30000) {
                return;
            }
            this.loadingGroupCall = true;
            TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
            tLRPC$TL_phone_getGroupParticipants.call = getInputGroupCall();
            tLRPC$TL_phone_getGroupParticipants.offset = "";
            tLRPC$TL_phone_getGroupParticipants.limit = 1;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$loadGroupCall$11(tLObject, tLRPC$TL_error);
                }
            });
        }

        private void loadUnknownParticipants(final ArrayList<Long> arrayList, boolean z, final OnParticipantsLoad onParticipantsLoad) {
            TLRPC$InputPeer tLRPC$TL_inputPeerChannel;
            final HashSet<Long> hashSet = z ? this.loadingUids : this.loadingSsrcs;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                if (hashSet.contains(arrayList.get(i))) {
                    arrayList.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
            if (arrayList.isEmpty()) {
                return;
            }
            final int i2 = this.lastLoadGuid + 1;
            this.lastLoadGuid = i2;
            this.loadingGuids.add(Integer.valueOf(i2));
            hashSet.addAll(arrayList);
            TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
            tLRPC$TL_phone_getGroupParticipants.call = getInputGroupCall();
            int size2 = arrayList.size();
            for (int i3 = 0; i3 < size2; i3++) {
                long longValue = arrayList.get(i3).longValue();
                if (z) {
                    if (longValue > 0) {
                        tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerUser();
                        tLRPC$TL_inputPeerChannel.user_id = longValue;
                    } else {
                        long j = -longValue;
                        TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(j));
                        if (chat == null || ChatObject.isChannel(chat)) {
                            tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                            tLRPC$TL_inputPeerChannel.channel_id = j;
                        } else {
                            tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChat();
                            tLRPC$TL_inputPeerChannel.chat_id = j;
                        }
                    }
                    tLRPC$TL_phone_getGroupParticipants.ids.add(tLRPC$TL_inputPeerChannel);
                } else {
                    tLRPC$TL_phone_getGroupParticipants.sources.add(Integer.valueOf((int) longValue));
                }
            }
            tLRPC$TL_phone_getGroupParticipants.offset = "";
            tLRPC$TL_phone_getGroupParticipants.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$loadUnknownParticipants$6(i2, onParticipantsLoad, arrayList, hashSet, tLObject, tLRPC$TL_error);
                }
            });
        }

        private void onParticipantsLoad(java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r17, boolean r18, java.lang.String r19, java.lang.String r20, int r21, int r22) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.onParticipantsLoad(java.util.ArrayList, boolean, java.lang.String, java.lang.String, int, int):void");
        }

        private void processAllSources(org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11, boolean r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processAllSources(org.telegram.tgnet.TLRPC$TL_groupCallParticipant, boolean):void");
        }

        private void processUpdatesQueue() {
            Collections.sort(this.updatesQueue, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$processUpdatesQueue$7;
                    lambda$processUpdatesQueue$7 = ChatObject.Call.lambda$processUpdatesQueue$7((TLRPC$TL_updateGroupCallParticipants) obj, (TLRPC$TL_updateGroupCallParticipants) obj2);
                    return lambda$processUpdatesQueue$7;
                }
            });
            ArrayList<TLRPC$TL_updateGroupCallParticipants> arrayList = this.updatesQueue;
            if (arrayList != null && !arrayList.isEmpty()) {
                boolean z = false;
                while (this.updatesQueue.size() > 0) {
                    TLRPC$TL_updateGroupCallParticipants tLRPC$TL_updateGroupCallParticipants = this.updatesQueue.get(0);
                    int isValidUpdate = isValidUpdate(tLRPC$TL_updateGroupCallParticipants);
                    if (isValidUpdate == 0) {
                        processParticipantsUpdate(tLRPC$TL_updateGroupCallParticipants, true);
                        this.updatesQueue.remove(0);
                        z = true;
                    } else {
                        if (isValidUpdate == 1) {
                            if (this.updatesStartWaitTime != 0 && (z || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTime) <= 1500)) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("HOLE IN GROUP CALL UPDATES QUEUE - will wait more time");
                                }
                                if (z) {
                                    this.updatesStartWaitTime = System.currentTimeMillis();
                                    return;
                                }
                                return;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("HOLE IN GROUP CALL UPDATES QUEUE - reload participants");
                            }
                            this.updatesStartWaitTime = 0L;
                            this.updatesQueue.clear();
                            this.nextLoadOffset = null;
                            loadMembers(true);
                            return;
                        }
                        this.updatesQueue.remove(0);
                    }
                }
                this.updatesQueue.clear();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("GROUP CALL UPDATES QUEUE PROCEED - OK");
                }
            }
            this.updatesStartWaitTime = 0L;
        }

        private void setParticiapantsVolume() {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance == null || sharedInstance.getAccount() != this.currentAccount.getCurrentAccount() || sharedInstance.getChat() == null || sharedInstance.getChat().id != (-this.chatId)) {
                return;
            }
            sharedInstance.setParticipantsVolume();
        }

        public static boolean videoIsActive(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, Call call) {
            VoIPService sharedInstance;
            VideoParticipant videoParticipant;
            if (tLRPC$TL_groupCallParticipant == null || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                return false;
            }
            if (tLRPC$TL_groupCallParticipant.self) {
                return sharedInstance.getVideoState(z) == 2;
            }
            VideoParticipant videoParticipant2 = call.rtmpStreamParticipant;
            if ((videoParticipant2 == null || videoParticipant2.participant != tLRPC$TL_groupCallParticipant) && (((videoParticipant = call.videoNotAvailableParticipant) == null || videoParticipant.participant != tLRPC$TL_groupCallParticipant) && call.participants.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) == null)) {
                return false;
            }
            return z ? tLRPC$TL_groupCallParticipant.presentation != null : tLRPC$TL_groupCallParticipant.video != null;
        }

        public void addInvitedUser(long j) {
            if (this.participants.get(j) != null || this.invitedUsersMap.contains(Long.valueOf(j))) {
                return;
            }
            this.invitedUsersMap.add(Long.valueOf(j));
            this.invitedUsers.add(Long.valueOf(j));
        }

        public void addSelfDummyParticipant(boolean r9) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.addSelfDummyParticipant(boolean):void");
        }

        public boolean canRecordVideo() {
            if (!this.canStreamVideo) {
                return false;
            }
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            return (sharedInstance != null && sharedInstance.groupCall == this && (sharedInstance.getVideoState(false) == 2 || sharedInstance.getVideoState(true) == 2)) || this.activeVideos < this.call.unmuted_video_limit;
        }

        public void clearVideFramesInfo() {
            for (int i = 0; i < this.sortedParticipants.size(); i++) {
                this.sortedParticipants.get(i).hasCameraFrame = 0;
                this.sortedParticipants.get(i).hasPresentationFrame = 0;
                this.sortedParticipants.get(i).videoIndex = 0;
            }
            sortParticipants();
        }

        public void createNoVideoParticipant() {
            if (this.videoNotAvailableParticipant != null) {
                return;
            }
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
            TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
            tLRPC$TL_groupCallParticipant.peer = tLRPC$TL_peerChannel;
            tLRPC$TL_peerChannel.channel_id = this.chatId;
            tLRPC$TL_groupCallParticipant.muted = true;
            TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = new TLRPC$TL_groupCallParticipantVideo();
            tLRPC$TL_groupCallParticipant.video = tLRPC$TL_groupCallParticipantVideo;
            tLRPC$TL_groupCallParticipantVideo.paused = true;
            tLRPC$TL_groupCallParticipantVideo.endpoint = "";
            this.videoNotAvailableParticipant = new VideoParticipant(tLRPC$TL_groupCallParticipant, false, false);
        }

        public void createRtmpStreamParticipant(List<TLRPC$TL_groupCallStreamChannel> list) {
            if (!this.loadedRtmpStreamParticipant || this.rtmpStreamParticipant == null) {
                VideoParticipant videoParticipant = this.rtmpStreamParticipant;
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = videoParticipant != null ? videoParticipant.participant : new TLRPC$TL_groupCallParticipant();
                TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                tLRPC$TL_groupCallParticipant.peer = tLRPC$TL_peerChat;
                tLRPC$TL_peerChat.channel_id = this.chatId;
                tLRPC$TL_groupCallParticipant.video = new TLRPC$TL_groupCallParticipantVideo();
                TLRPC$TL_groupCallParticipantVideoSourceGroup tLRPC$TL_groupCallParticipantVideoSourceGroup = new TLRPC$TL_groupCallParticipantVideoSourceGroup();
                tLRPC$TL_groupCallParticipantVideoSourceGroup.semantics = "SIM";
                Iterator<TLRPC$TL_groupCallStreamChannel> it = list.iterator();
                while (it.hasNext()) {
                    tLRPC$TL_groupCallParticipantVideoSourceGroup.sources.add(Integer.valueOf(it.next().channel));
                }
                tLRPC$TL_groupCallParticipant.video.source_groups.add(tLRPC$TL_groupCallParticipantVideoSourceGroup);
                tLRPC$TL_groupCallParticipant.video.endpoint = "unified";
                tLRPC$TL_groupCallParticipant.videoEndpoint = "unified";
                this.rtmpStreamParticipant = new VideoParticipant(tLRPC$TL_groupCallParticipant, false, false);
                sortParticipants();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChatObject.Call.this.lambda$createRtmpStreamParticipant$1();
                    }
                });
            }
        }

        public TLRPC$TL_inputGroupCall getInputGroupCall() {
            TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = new TLRPC$TL_inputGroupCall();
            TLRPC$GroupCall tLRPC$GroupCall = this.call;
            tLRPC$TL_inputGroupCall.id = tLRPC$GroupCall.id;
            tLRPC$TL_inputGroupCall.access_hash = tLRPC$GroupCall.access_hash;
            return tLRPC$TL_inputGroupCall;
        }

        public boolean isScheduled() {
            return (this.call.flags & 128) != 0;
        }

        public void loadMembers(final boolean z) {
            if (z) {
                if (this.reloadingMembers) {
                    return;
                }
                this.membersLoadEndReached = false;
                this.nextLoadOffset = null;
            }
            if (this.membersLoadEndReached || this.sortedParticipants.size() > 5000) {
                return;
            }
            if (z) {
                this.reloadingMembers = true;
            }
            this.loadingMembers = true;
            final TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants = new TLRPC$TL_phone_getGroupParticipants();
            tLRPC$TL_phone_getGroupParticipants.call = getInputGroupCall();
            String str = this.nextLoadOffset;
            if (str == null) {
                str = "";
            }
            tLRPC$TL_phone_getGroupParticipants.offset = str;
            tLRPC$TL_phone_getGroupParticipants.limit = 20;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupParticipants, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$loadMembers$3(z, tLRPC$TL_phone_getGroupParticipants, tLObject, tLRPC$TL_error);
                }
            });
        }

        public void migrateToChat(TLRPC$Chat tLRPC$Chat) {
            this.chatId = tLRPC$Chat.id;
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance == null || sharedInstance.getAccount() != this.currentAccount.getCurrentAccount() || sharedInstance.getChat() == null || sharedInstance.getChat().id != (-this.chatId)) {
                return;
            }
            sharedInstance.migrateToChat(tLRPC$Chat);
        }

        public void processGroupCallUpdate(TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
            if (this.call.version < tLRPC$TL_updateGroupCall.call.version) {
                this.nextLoadOffset = null;
                loadMembers(true);
            }
            this.call = tLRPC$TL_updateGroupCall.call;
            this.recording = this.call.record_start_date != 0;
            this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        public void processParticipantsUpdate(org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants r30, boolean r31) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processParticipantsUpdate(org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants, boolean):void");
        }

        public void processTypingsUpdate(AccountInstance accountInstance, ArrayList<Long> arrayList, int i) {
            this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(SystemClock.elapsedRealtime()));
            int size = arrayList.size();
            ArrayList<Long> arrayList2 = null;
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                Long l = arrayList.get(i2);
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) this.participants.get(l.longValue());
                if (tLRPC$TL_groupCallParticipant == null) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList<>();
                    }
                    arrayList2.add(l);
                } else if (i - tLRPC$TL_groupCallParticipant.lastTypingDate > 10) {
                    if (tLRPC$TL_groupCallParticipant.lastVisibleDate != i) {
                        tLRPC$TL_groupCallParticipant.active_date = i;
                    }
                    tLRPC$TL_groupCallParticipant.lastTypingDate = i;
                    z = true;
                }
            }
            if (arrayList2 != null) {
                loadUnknownParticipants(arrayList2, true, null);
            }
            if (z) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
            }
        }

        public void processUnknownVideoParticipants(int[] iArr, OnParticipantsLoad onParticipantsLoad) {
            ArrayList<Long> arrayList = null;
            for (int i = 0; i < iArr.length; i++) {
                if (this.participantsBySources.get(iArr[i]) == null && this.participantsByVideoSources.get(iArr[i]) == null && this.participantsByPresentationSources.get(iArr[i]) == null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(Long.valueOf(iArr[i]));
                }
            }
            if (arrayList != null) {
                loadUnknownParticipants(arrayList, false, onParticipantsLoad);
            } else {
                onParticipantsLoad.onLoad(null);
            }
        }

        public void processVoiceLevelsUpdate(int[] r26, float[] r27, boolean[] r28) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processVoiceLevelsUpdate(int[], float[], boolean[]):void");
        }

        public void reloadGroupCall() {
            TLRPC$TL_phone_getGroupCall tLRPC$TL_phone_getGroupCall = new TLRPC$TL_phone_getGroupCall();
            tLRPC$TL_phone_getGroupCall.call = getInputGroupCall();
            tLRPC$TL_phone_getGroupCall.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_getGroupCall, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$reloadGroupCall$9(tLObject, tLRPC$TL_error);
                }
            });
        }

        public void saveActiveDates() {
            int size = this.sortedParticipants.size();
            for (int i = 0; i < size; i++) {
                this.sortedParticipants.get(i).lastActiveDate = r2.active_date;
            }
        }

        public void setCall(AccountInstance accountInstance, long j, TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall) {
            this.chatId = j;
            this.currentAccount = accountInstance;
            TLRPC$GroupCall tLRPC$GroupCall = tLRPC$TL_phone_groupCall.call;
            this.call = tLRPC$GroupCall;
            this.recording = tLRPC$GroupCall.record_start_date != 0;
            int size = tLRPC$TL_phone_groupCall.participants.size();
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) tLRPC$TL_phone_groupCall.participants.get(i2);
                this.participants.put(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer), tLRPC$TL_groupCallParticipant);
                this.sortedParticipants.add(tLRPC$TL_groupCallParticipant);
                processAllSources(tLRPC$TL_groupCallParticipant, true);
                i = Math.min(i, tLRPC$TL_groupCallParticipant.date);
            }
            sortParticipants();
            this.nextLoadOffset = tLRPC$TL_phone_groupCall.participants_next_offset;
            loadMembers(true);
            createNoVideoParticipant();
            if (this.call.rtmp_stream) {
                createRtmpStreamParticipant(Collections.emptyList());
            }
        }

        public void setSelfPeer(TLRPC$InputPeer tLRPC$InputPeer) {
            if (tLRPC$InputPeer == null) {
                this.selfPeer = null;
                return;
            }
            if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerUser) {
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                this.selfPeer = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = tLRPC$InputPeer.user_id;
            } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
                TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                this.selfPeer = tLRPC$TL_peerChat;
                tLRPC$TL_peerChat.chat_id = tLRPC$InputPeer.chat_id;
            } else {
                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                this.selfPeer = tLRPC$TL_peerChannel;
                tLRPC$TL_peerChannel.channel_id = tLRPC$InputPeer.channel_id;
            }
        }

        public void setTitle(String str) {
            TLRPC$TL_phone_editGroupCallTitle tLRPC$TL_phone_editGroupCallTitle = new TLRPC$TL_phone_editGroupCallTitle();
            tLRPC$TL_phone_editGroupCallTitle.call = getInputGroupCall();
            tLRPC$TL_phone_editGroupCallTitle.title = str;
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallTitle, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$setTitle$4(tLObject, tLRPC$TL_error);
                }
            });
        }

        public boolean shouldShowPanel() {
            TLRPC$GroupCall tLRPC$GroupCall = this.call;
            return tLRPC$GroupCall.participants_count > 0 || tLRPC$GroupCall.rtmp_stream || isScheduled();
        }

        public void sortParticipants() {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            int i;
            VideoParticipant videoParticipant;
            int i2;
            this.visibleVideoParticipants.clear();
            this.visibleParticipants.clear();
            TLRPC$Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
            final boolean canManageCalls = ChatObject.canManageCalls(chat);
            VideoParticipant videoParticipant2 = this.rtmpStreamParticipant;
            if (videoParticipant2 != null) {
                this.visibleVideoParticipants.add(videoParticipant2);
            }
            final long selfId = getSelfId();
            VoIPService.getSharedInstance();
            this.canStreamVideo = true;
            this.activeVideos = 0;
            int size = this.sortedParticipants.size();
            boolean z = false;
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.sortedParticipants.get(i3);
                boolean videoIsActive = videoIsActive(tLRPC$TL_groupCallParticipant2, false, this);
                boolean videoIsActive2 = videoIsActive(tLRPC$TL_groupCallParticipant2, true, this);
                boolean z2 = tLRPC$TL_groupCallParticipant2.self;
                if (!z2 && (videoIsActive || videoIsActive2)) {
                    this.activeVideos++;
                }
                if (!videoIsActive && !videoIsActive2) {
                    if (!z2) {
                        if (this.canStreamVideo) {
                            if (tLRPC$TL_groupCallParticipant2.video == null) {
                                if (tLRPC$TL_groupCallParticipant2.presentation != null) {
                                }
                            }
                        }
                    }
                    tLRPC$TL_groupCallParticipant2.videoIndex = 0;
                } else if (this.canStreamVideo) {
                    if (tLRPC$TL_groupCallParticipant2.videoIndex == 0) {
                        if (z2) {
                            i2 = Integer.MAX_VALUE;
                        } else {
                            i2 = videoPointer + 1;
                            videoPointer = i2;
                        }
                        tLRPC$TL_groupCallParticipant2.videoIndex = i2;
                    }
                    z = true;
                } else {
                    z = true;
                    tLRPC$TL_groupCallParticipant2.videoIndex = 0;
                }
            }
            try {
                Collections.sort(this.sortedParticipants, new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$sortParticipants$12;
                        lambda$sortParticipants$12 = ChatObject.Call.this.lambda$sortParticipants$12(selfId, canManageCalls, (TLRPC$TL_groupCallParticipant) obj, (TLRPC$TL_groupCallParticipant) obj2);
                        return lambda$sortParticipants$12;
                    }
                });
            } catch (Exception unused) {
            }
            if (this.sortedParticipants.isEmpty()) {
                tLRPC$TL_groupCallParticipant = null;
            } else {
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = this.sortedParticipants;
                tLRPC$TL_groupCallParticipant = arrayList.get(arrayList.size() - 1);
            }
            if ((videoIsActive(tLRPC$TL_groupCallParticipant, false, this) || videoIsActive(tLRPC$TL_groupCallParticipant, true, this)) && (i = this.call.unmuted_video_count) > this.activeVideos) {
                this.activeVideos = i;
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance != null && sharedInstance.groupCall == this && (sharedInstance.getVideoState(false) == 2 || sharedInstance.getVideoState(true) == 2)) {
                    this.activeVideos--;
                }
            }
            if (this.sortedParticipants.size() > 5000 && (!ChatObject.canManageCalls(chat) || tLRPC$TL_groupCallParticipant.raise_hand_rating == 0)) {
                int size2 = this.sortedParticipants.size();
                for (int i4 = 5000; i4 < size2; i4++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.sortedParticipants.get(5000);
                    if (tLRPC$TL_groupCallParticipant3.raise_hand_rating == 0) {
                        processAllSources(tLRPC$TL_groupCallParticipant3, false);
                        this.participants.remove(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant3.peer));
                        this.sortedParticipants.remove(5000);
                    }
                }
            }
            checkOnlineParticipants();
            if (!this.canStreamVideo && z && (videoParticipant = this.videoNotAvailableParticipant) != null) {
                this.visibleVideoParticipants.add(videoParticipant);
            }
            int i5 = 0;
            for (int i6 = 0; i6 < this.sortedParticipants.size(); i6++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant4 = this.sortedParticipants.get(i6);
                if (!this.canStreamVideo || tLRPC$TL_groupCallParticipant4.videoIndex == 0) {
                    this.visibleParticipants.add(tLRPC$TL_groupCallParticipant4);
                } else if (!tLRPC$TL_groupCallParticipant4.self && videoIsActive(tLRPC$TL_groupCallParticipant4, true, this) && videoIsActive(tLRPC$TL_groupCallParticipant4, false, this)) {
                    VideoParticipant videoParticipant3 = this.videoParticipantsCache.get(tLRPC$TL_groupCallParticipant4.videoEndpoint);
                    if (videoParticipant3 == null) {
                        videoParticipant3 = new VideoParticipant(tLRPC$TL_groupCallParticipant4, false, true);
                        this.videoParticipantsCache.put(tLRPC$TL_groupCallParticipant4.videoEndpoint, videoParticipant3);
                    } else {
                        videoParticipant3.participant = tLRPC$TL_groupCallParticipant4;
                        videoParticipant3.presentation = false;
                        videoParticipant3.hasSame = true;
                    }
                    VideoParticipant videoParticipant4 = this.videoParticipantsCache.get(tLRPC$TL_groupCallParticipant4.presentationEndpoint);
                    if (videoParticipant4 == null) {
                        videoParticipant4 = new VideoParticipant(tLRPC$TL_groupCallParticipant4, true, true);
                    } else {
                        videoParticipant4.participant = tLRPC$TL_groupCallParticipant4;
                        videoParticipant4.presentation = true;
                        videoParticipant4.hasSame = true;
                    }
                    this.visibleVideoParticipants.add(videoParticipant3);
                    if (videoParticipant3.aspectRatio > 1.0f) {
                        i5 = this.visibleVideoParticipants.size() - 1;
                    }
                    this.visibleVideoParticipants.add(videoParticipant4);
                    if (videoParticipant4.aspectRatio <= 1.0f) {
                    }
                    i5 = this.visibleVideoParticipants.size() - 1;
                } else if (tLRPC$TL_groupCallParticipant4.self) {
                    if (videoIsActive(tLRPC$TL_groupCallParticipant4, true, this)) {
                        this.visibleVideoParticipants.add(new VideoParticipant(tLRPC$TL_groupCallParticipant4, true, false));
                    }
                    if (videoIsActive(tLRPC$TL_groupCallParticipant4, false, this)) {
                        this.visibleVideoParticipants.add(new VideoParticipant(tLRPC$TL_groupCallParticipant4, false, false));
                    }
                } else {
                    boolean videoIsActive3 = videoIsActive(tLRPC$TL_groupCallParticipant4, true, this);
                    VideoParticipant videoParticipant5 = this.videoParticipantsCache.get(videoIsActive3 ? tLRPC$TL_groupCallParticipant4.presentationEndpoint : tLRPC$TL_groupCallParticipant4.videoEndpoint);
                    if (videoParticipant5 == null) {
                        videoParticipant5 = new VideoParticipant(tLRPC$TL_groupCallParticipant4, videoIsActive3, false);
                        this.videoParticipantsCache.put(videoIsActive3 ? tLRPC$TL_groupCallParticipant4.presentationEndpoint : tLRPC$TL_groupCallParticipant4.videoEndpoint, videoParticipant5);
                    } else {
                        videoParticipant5.participant = tLRPC$TL_groupCallParticipant4;
                        videoParticipant5.presentation = videoIsActive3;
                        videoParticipant5.hasSame = false;
                    }
                    this.visibleVideoParticipants.add(videoParticipant5);
                    if (videoParticipant5.aspectRatio <= 1.0f) {
                    }
                    i5 = this.visibleVideoParticipants.size() - 1;
                }
            }
            if (GroupCallActivity.isLandscapeMode || this.visibleVideoParticipants.size() % 2 != 1) {
                return;
            }
            this.visibleVideoParticipants.add(this.visibleVideoParticipants.remove(i5));
        }

        public void toggleRecord(String str, int i) {
            this.recording = !this.recording;
            TLRPC$TL_phone_toggleGroupCallRecord tLRPC$TL_phone_toggleGroupCallRecord = new TLRPC$TL_phone_toggleGroupCallRecord();
            tLRPC$TL_phone_toggleGroupCallRecord.call = getInputGroupCall();
            tLRPC$TL_phone_toggleGroupCallRecord.start = this.recording;
            if (str != null) {
                tLRPC$TL_phone_toggleGroupCallRecord.title = str;
                tLRPC$TL_phone_toggleGroupCallRecord.flags |= 2;
            }
            if (i == 1 || i == 2) {
                tLRPC$TL_phone_toggleGroupCallRecord.flags |= 4;
                tLRPC$TL_phone_toggleGroupCallRecord.video = true;
                tLRPC$TL_phone_toggleGroupCallRecord.video_portrait = i == 1;
            }
            this.currentAccount.getConnectionsManager().sendRequest(tLRPC$TL_phone_toggleGroupCallRecord, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatObject.Call.this.lambda$toggleRecord$13(tLObject, tLRPC$TL_error);
                }
            });
            this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE);
        }

        public void updateVisibleParticipants() {
            sortParticipants();
            this.currentAccount.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), Boolean.FALSE, 0L);
        }
    }

    public static class VideoParticipant {
        public float aspectRatio;
        public int aspectRatioFromHeight;
        public int aspectRatioFromWidth;
        public boolean hasSame;
        public TLRPC$TL_groupCallParticipant participant;
        public boolean presentation;

        public VideoParticipant(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, boolean z2) {
            this.participant = tLRPC$TL_groupCallParticipant;
            this.presentation = z;
            this.hasSame = z2;
        }

        private void setAspectRatio(float f, Call call) {
            if (this.aspectRatio != f) {
                this.aspectRatio = f;
                if (GroupCallActivity.isLandscapeMode || call.visibleVideoParticipants.size() % 2 != 1) {
                    return;
                }
                call.updateVisibleParticipants();
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            VideoParticipant videoParticipant = (VideoParticipant) obj;
            return this.presentation == videoParticipant.presentation && MessageObject.getPeerId(this.participant.peer) == MessageObject.getPeerId(videoParticipant.participant.peer);
        }

        public void setAspectRatio(int i, int i2, Call call) {
            this.aspectRatioFromWidth = i;
            this.aspectRatioFromHeight = i2;
            setAspectRatio(i / i2, call);
        }
    }

    public static boolean canAddAdmins(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 4);
    }

    public static boolean canAddBotsToChat(TLRPC$Chat tLRPC$Chat) {
        if (!isChannel(tLRPC$Chat)) {
            return tLRPC$Chat.migrated_to == null;
        }
        if (!tLRPC$Chat.megagroup) {
            return false;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
        return (tLRPC$TL_chatAdminRights != null && (tLRPC$TL_chatAdminRights.post_messages || tLRPC$TL_chatAdminRights.add_admins)) || tLRPC$Chat.creator;
    }

    public static boolean canAddUsers(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 3);
    }

    public static boolean canBlockUsers(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 2);
    }

    public static boolean canChangeChatInfo(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 1);
    }

    public static boolean canCreateTopic(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 15);
    }

    public static boolean canDeleteTopic(int i, TLRPC$Chat tLRPC$Chat, long j) {
        return (j == 1 || tLRPC$Chat == null || !canDeleteTopic(i, tLRPC$Chat, MessagesController.getInstance(i).getTopicsController().findTopic(tLRPC$Chat.id, j))) ? false : true;
    }

    public static boolean canDeleteTopic(int i, TLRPC$Chat tLRPC$Chat, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        TLRPC$Message tLRPC$Message;
        TLRPC$Message tLRPC$Message2;
        if (tLRPC$TL_forumTopic != null && tLRPC$TL_forumTopic.id == 1) {
            return false;
        }
        if (!canUserDoAction(tLRPC$Chat, 13)) {
            if (!isMyTopic(i, tLRPC$TL_forumTopic) || (tLRPC$Message = tLRPC$TL_forumTopic.topMessage) == null || (tLRPC$Message2 = tLRPC$TL_forumTopic.topicStartMessage) == null) {
                return false;
            }
            int i2 = tLRPC$Message.id - tLRPC$Message2.id;
            ArrayList arrayList = tLRPC$TL_forumTopic.groupedMessages;
            if (i2 > Math.max(1, arrayList == null ? 0 : arrayList.size()) || !MessageObject.peersEqual(tLRPC$TL_forumTopic.from_id, tLRPC$TL_forumTopic.topMessage.from_id)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canManageCalls(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 14);
    }

    public static boolean canManageTopic(int i, TLRPC$Chat tLRPC$Chat, long j) {
        return canManageTopics(tLRPC$Chat) || isMyTopic(i, tLRPC$Chat, j);
    }

    public static boolean canManageTopic(int i, TLRPC$Chat tLRPC$Chat, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        return canManageTopics(tLRPC$Chat) || isMyTopic(i, tLRPC$TL_forumTopic);
    }

    public static boolean canManageTopics(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAdminAction(tLRPC$Chat, 15);
    }

    public static boolean canPinMessages(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        return canUserDoAction(tLRPC$Chat, 0) || (isChannel(tLRPC$Chat) && !tLRPC$Chat.megagroup && (tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights.edit_messages);
    }

    public static boolean canPost(TLRPC$Chat tLRPC$Chat) {
        return canUserDoAction(tLRPC$Chat, 5);
    }

    public static boolean canSendAnyMedia(TLRPC$Chat tLRPC$Chat) {
        return canSendPhoto(tLRPC$Chat) || canSendVideo(tLRPC$Chat) || canSendRoundVideo(tLRPC$Chat) || canSendVoice(tLRPC$Chat) || canSendDocument(tLRPC$Chat) || canSendMusic(tLRPC$Chat) || canSendStickers(tLRPC$Chat);
    }

    public static boolean canSendAsPeers(TLRPC$Chat tLRPC$Chat) {
        return isChannel(tLRPC$Chat) && ((!tLRPC$Chat.megagroup && tLRPC$Chat.signatures && hasAdminRights(tLRPC$Chat) && canWriteToChat(tLRPC$Chat)) || (tLRPC$Chat.megagroup && (isPublic(tLRPC$Chat) || tLRPC$Chat.has_geo || tLRPC$Chat.has_link)));
    }

    public static boolean canSendDocument(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 19);
    }

    public static boolean canSendEmbed(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 9);
    }

    public static boolean canSendMessages(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 6);
    }

    public static boolean canSendMusic(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 18);
    }

    public static boolean canSendPhoto(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 16);
    }

    public static boolean canSendPlain(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 22);
    }

    public static boolean canSendPolls(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 10);
    }

    public static boolean canSendRoundVideo(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 21);
    }

    public static boolean canSendStickers(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 8);
    }

    public static boolean canSendVideo(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 17);
    }

    public static boolean canSendVoice(TLRPC$Chat tLRPC$Chat) {
        if (isIgnoredChatRestrictionsForBoosters(tLRPC$Chat)) {
            return true;
        }
        return canUserDoAction(tLRPC$Chat, 20);
    }

    public static boolean canUserDoAction(TLRPC$Chat tLRPC$Chat, int i) {
        if (tLRPC$Chat == null || canUserDoAdminAction(tLRPC$Chat, i)) {
            return true;
        }
        if (!getBannedRight(tLRPC$Chat.banned_rights, i) && isBannableAction(i)) {
            if (tLRPC$Chat.admin_rights != null && !isAdminAction(i)) {
                return true;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
            if (tLRPC$TL_chatBannedRights == null && ((tLRPC$Chat instanceof TLRPC$TL_chat_layer92) || (tLRPC$Chat instanceof TLRPC$TL_chat_old) || (tLRPC$Chat instanceof TLRPC$TL_chat_old2) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer92) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer77) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer72) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer67) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer48) || (tLRPC$Chat instanceof TLRPC$TL_channel_old))) {
                return true;
            }
            if (tLRPC$TL_chatBannedRights != null && !getBannedRight(tLRPC$TL_chatBannedRights, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(TLRPC$Chat tLRPC$Chat, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, int i) {
        if (tLRPC$Chat == null) {
            return true;
        }
        if (tLRPC$ChannelParticipant == null) {
            return false;
        }
        if (canUserDoAdminAction(tLRPC$ChannelParticipant.admin_rights, i)) {
            return true;
        }
        if (!getBannedRight(tLRPC$ChannelParticipant.banned_rights, i) && isBannableAction(i)) {
            if (tLRPC$ChannelParticipant.admin_rights != null && !isAdminAction(i)) {
                return true;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
            if (tLRPC$TL_chatBannedRights == null && ((tLRPC$Chat instanceof TLRPC$TL_chat_layer92) || (tLRPC$Chat instanceof TLRPC$TL_chat_old) || (tLRPC$Chat instanceof TLRPC$TL_chat_old2) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer92) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer77) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer72) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer67) || (tLRPC$Chat instanceof TLRPC$TL_channel_layer48) || (tLRPC$Chat instanceof TLRPC$TL_channel_old))) {
                return true;
            }
            if (tLRPC$TL_chatBannedRights != null && !getBannedRight(tLRPC$TL_chatBannedRights, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAdminAction(TLRPC$Chat tLRPC$Chat, int i) {
        boolean z;
        if (tLRPC$Chat == null) {
            return false;
        }
        if (tLRPC$Chat.creator) {
            return true;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
        if (tLRPC$TL_chatAdminRights != null) {
            if (i == 0) {
                z = tLRPC$TL_chatAdminRights.pin_messages;
            } else if (i == 1) {
                z = tLRPC$TL_chatAdminRights.change_info;
            } else if (i == 2) {
                z = tLRPC$TL_chatAdminRights.ban_users;
            } else if (i == 3) {
                z = tLRPC$TL_chatAdminRights.invite_users;
            } else if (i == 4) {
                z = tLRPC$TL_chatAdminRights.add_admins;
            } else if (i != 5) {
                switch (i) {
                    case 12:
                        z = tLRPC$TL_chatAdminRights.edit_messages;
                        break;
                    case 13:
                        z = tLRPC$TL_chatAdminRights.delete_messages;
                        break;
                    case 14:
                        z = tLRPC$TL_chatAdminRights.manage_call;
                        break;
                    case 15:
                        z = tLRPC$TL_chatAdminRights.manage_topics;
                        break;
                    default:
                        z = false;
                        break;
                }
            } else {
                z = tLRPC$TL_chatAdminRights.post_messages;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAdminAction(TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, int i) {
        boolean z;
        if (tLRPC$TL_chatAdminRights != null) {
            if (i == 0) {
                z = tLRPC$TL_chatAdminRights.pin_messages;
            } else if (i == 1) {
                z = tLRPC$TL_chatAdminRights.change_info;
            } else if (i == 2) {
                z = tLRPC$TL_chatAdminRights.ban_users;
            } else if (i == 3) {
                z = tLRPC$TL_chatAdminRights.invite_users;
            } else if (i == 4) {
                z = tLRPC$TL_chatAdminRights.add_admins;
            } else if (i != 5) {
                switch (i) {
                    case 12:
                        z = tLRPC$TL_chatAdminRights.edit_messages;
                        break;
                    case 13:
                        z = tLRPC$TL_chatAdminRights.delete_messages;
                        break;
                    case 14:
                        z = tLRPC$TL_chatAdminRights.manage_call;
                        break;
                    case 15:
                        z = tLRPC$TL_chatAdminRights.manage_topics;
                        break;
                    default:
                        z = false;
                        break;
                }
            } else {
                z = tLRPC$TL_chatAdminRights.post_messages;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public static boolean canWriteToChat(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        return !isChannel(tLRPC$Chat) || tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights.post_messages) || (!(tLRPC$Chat.broadcast || tLRPC$Chat.gigagroup) || (tLRPC$Chat.gigagroup && hasAdminRights(tLRPC$Chat)));
    }

    public static String getAllowedSendString(TLRPC$Chat tLRPC$Chat) {
        StringBuilder sb = new StringBuilder();
        if (canSendPhoto(tLRPC$Chat)) {
            sb.append(LocaleController.getString(R.string.SendMediaPermissionPhotos));
        }
        if (canSendVideo(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaPermissionVideos));
        }
        if (canSendStickers(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaPermissionStickersGifs));
        }
        if (canSendMusic(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaPermissionMusic));
        }
        if (canSendDocument(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaPermissionFiles));
        }
        if (canSendVoice(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaPermissionVoice));
        }
        if (canSendRoundVideo(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaPermissionRound));
        }
        if (canSendEmbed(tLRPC$Chat)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString(R.string.SendMediaEmbededLinks));
        }
        return sb.toString();
    }

    private static boolean getBannedRight(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, int i) {
        if (tLRPC$TL_chatBannedRights == null) {
            return false;
        }
        if (i == 0) {
            return tLRPC$TL_chatBannedRights.pin_messages;
        }
        if (i == 1) {
            return tLRPC$TL_chatBannedRights.change_info;
        }
        if (i == 3) {
            return tLRPC$TL_chatBannedRights.invite_users;
        }
        switch (i) {
            case 6:
                return tLRPC$TL_chatBannedRights.send_messages;
            case 7:
                return tLRPC$TL_chatBannedRights.send_media;
            case 8:
                return tLRPC$TL_chatBannedRights.send_stickers;
            case 9:
                return tLRPC$TL_chatBannedRights.embed_links;
            case 10:
                return tLRPC$TL_chatBannedRights.send_polls;
            case 11:
                return tLRPC$TL_chatBannedRights.view_messages;
            default:
                switch (i) {
                    case 15:
                        return tLRPC$TL_chatBannedRights.manage_topics;
                    case 16:
                        return tLRPC$TL_chatBannedRights.send_photos;
                    case 17:
                        return tLRPC$TL_chatBannedRights.send_videos;
                    case 18:
                        return tLRPC$TL_chatBannedRights.send_audios;
                    case 19:
                        return tLRPC$TL_chatBannedRights.send_docs;
                    case 20:
                        return tLRPC$TL_chatBannedRights.send_voices;
                    case 21:
                        return tLRPC$TL_chatBannedRights.send_roundvideos;
                    case 22:
                        return tLRPC$TL_chatBannedRights.send_plain;
                    default:
                        return false;
                }
        }
    }

    public static String getBannedRightsString(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        return (((((((((((((((((((("" + (tLRPC$TL_chatBannedRights.view_messages ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_messages ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_media ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_stickers ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_gifs ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_games ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_inline ? 1 : 0)) + (tLRPC$TL_chatBannedRights.embed_links ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_polls ? 1 : 0)) + (tLRPC$TL_chatBannedRights.invite_users ? 1 : 0)) + (tLRPC$TL_chatBannedRights.change_info ? 1 : 0)) + (tLRPC$TL_chatBannedRights.pin_messages ? 1 : 0)) + (tLRPC$TL_chatBannedRights.manage_topics ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_photos ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_videos ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_roundvideos ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_voices ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_audios ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_docs ? 1 : 0)) + (tLRPC$TL_chatBannedRights.send_plain ? 1 : 0)) + tLRPC$TL_chatBannedRights.until_date;
    }

    public static int getColorId(TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null) {
            return 0;
        }
        TLRPC$TL_peerColor tLRPC$TL_peerColor = tLRPC$Chat.color;
        return (tLRPC$TL_peerColor == null || (tLRPC$TL_peerColor.flags & 1) == 0) ? (int) (tLRPC$Chat.id % 7) : tLRPC$TL_peerColor.color;
    }

    public static long getEmojiId(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_peerColor tLRPC$TL_peerColor;
        if (tLRPC$Chat == null || (tLRPC$TL_peerColor = tLRPC$Chat.color) == null || (tLRPC$TL_peerColor.flags & 2) == 0) {
            return 0L;
        }
        return tLRPC$TL_peerColor.background_emoji_id;
    }

    public static int getParticipantVolume(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        if ((tLRPC$TL_groupCallParticipant.flags & 128) != 0) {
            return tLRPC$TL_groupCallParticipant.volume;
        }
        return 10000;
    }

    public static MessagesController.PeerColor getPeerColorForAvatar(int i, TLRPC$Chat tLRPC$Chat) {
        return null;
    }

    public static TLRPC$ChatPhoto getPhoto(TLRPC$Chat tLRPC$Chat) {
        if (hasPhoto(tLRPC$Chat)) {
            return tLRPC$Chat.photo;
        }
        return null;
    }

    public static int getProfileColorId(TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null) {
            return 0;
        }
        TLRPC$TL_peerColor tLRPC$TL_peerColor = tLRPC$Chat.profile_color;
        if (tLRPC$TL_peerColor == null || (tLRPC$TL_peerColor.flags & 1) == 0) {
            return -1;
        }
        return tLRPC$TL_peerColor.color;
    }

    public static long getProfileEmojiId(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_peerColor tLRPC$TL_peerColor;
        if (tLRPC$Chat == null || (tLRPC$TL_peerColor = tLRPC$Chat.profile_color) == null || (tLRPC$TL_peerColor.flags & 2) == 0) {
            return 0L;
        }
        return tLRPC$TL_peerColor.background_emoji_id;
    }

    public static String getPublicUsername(TLRPC$Chat tLRPC$Chat) {
        return getPublicUsername(tLRPC$Chat, false);
    }

    public static String getPublicUsername(TLRPC$Chat tLRPC$Chat, boolean z) {
        ArrayList arrayList;
        if (tLRPC$Chat == null) {
            return null;
        }
        if (!TextUtils.isEmpty(tLRPC$Chat.username) && !z) {
            return tLRPC$Chat.username;
        }
        if (tLRPC$Chat.usernames != null) {
            for (int i = 0; i < tLRPC$Chat.usernames.size(); i++) {
                TLRPC$TL_username tLRPC$TL_username = (TLRPC$TL_username) tLRPC$Chat.usernames.get(i);
                if (tLRPC$TL_username != null && (((tLRPC$TL_username.active && !z) || tLRPC$TL_username.editable) && !TextUtils.isEmpty(tLRPC$TL_username.username))) {
                    return tLRPC$TL_username.username;
                }
            }
        }
        if (TextUtils.isEmpty(tLRPC$Chat.username) || !z || ((arrayList = tLRPC$Chat.usernames) != null && arrayList.size() > 0)) {
            return null;
        }
        return tLRPC$Chat.username;
    }

    public static String getRestrictedErrorText(TLRPC$Chat tLRPC$Chat, int i) {
        return i == 23 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachGifRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachGifRestrictedForever", R.string.AttachGifRestrictedForever, new Object[0]) : LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 8 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachStickersRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachStickersRestrictedForever", R.string.AttachStickersRestrictedForever, new Object[0]) : LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 16 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachPhotoRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachPhotoRestrictedForever", R.string.AttachPhotoRestrictedForever, new Object[0]) : LocaleController.formatString("AttachPhotoRestricted", R.string.AttachPhotoRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 17 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachVideoRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachVideoRestrictedForever", R.string.AttachVideoRestrictedForever, new Object[0]) : LocaleController.formatString("AttachVideoRestricted", R.string.AttachVideoRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 19 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachDocumentsRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachDocumentsRestrictedForever", R.string.AttachDocumentsRestrictedForever, new Object[0]) : LocaleController.formatString("AttachDocumentsRestricted", R.string.AttachDocumentsRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 7 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachMediaRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachMediaRestrictedForever", R.string.AttachMediaRestrictedForever, new Object[0]) : LocaleController.formatString("AttachMediaRestricted", R.string.AttachMediaRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 18 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachAudioRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachAudioRestrictedForever", R.string.AttachAudioRestrictedForever, new Object[0]) : LocaleController.formatString("AttachAudioRestricted", R.string.AttachAudioRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 22 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachPlainRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachPlainRestrictedForever", R.string.AttachPlainRestrictedForever, new Object[0]) : LocaleController.formatString("AttachPlainRestricted", R.string.AttachPlainRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 21 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachRoundRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachRoundRestrictedForever", R.string.AttachRoundRestrictedForever, new Object[0]) : LocaleController.formatString("AttachRoundRestricted", R.string.AttachRoundRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : i == 20 ? (tLRPC$Chat == null || isActionBannedByDefault(tLRPC$Chat, i)) ? LocaleController.getString(R.string.GlobalAttachVoiceRestricted) : AndroidUtilities.isBannedForever(tLRPC$Chat.banned_rights) ? LocaleController.formatString("AttachVoiceRestrictedForever", R.string.AttachVoiceRestrictedForever, new Object[0]) : LocaleController.formatString("AttachVoiceRestricted", R.string.AttachVoiceRestricted, LocaleController.formatDateForBan(tLRPC$Chat.banned_rights.until_date)) : "";
    }

    public static long getSendAsPeerId(TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull) {
        return getSendAsPeerId(tLRPC$Chat, tLRPC$ChatFull, false);
    }

    public static long getSendAsPeerId(TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        TLRPC$Peer tLRPC$Peer;
        if (tLRPC$Chat != null && tLRPC$ChatFull != null && (tLRPC$Peer = tLRPC$ChatFull.default_send_as) != null) {
            long j = tLRPC$Peer.user_id;
            return j != 0 ? j : z ? -tLRPC$Peer.channel_id : tLRPC$Peer.channel_id;
        }
        if (tLRPC$Chat != null && (tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights.anonymous) {
            long j2 = tLRPC$Chat.id;
            return z ? -j2 : j2;
        }
        if (tLRPC$Chat == null || !isChannelAndNotMegaGroup(tLRPC$Chat) || tLRPC$Chat.signatures) {
            return UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        }
        long j3 = tLRPC$Chat.id;
        return z ? -j3 : j3;
    }

    public static boolean hasAdminRights(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        return tLRPC$Chat != null && (tLRPC$Chat.creator || !((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) == null || tLRPC$TL_chatAdminRights.flags == 0));
    }

    public static boolean hasPhoto(TLRPC$Chat tLRPC$Chat) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        return (tLRPC$Chat == null || (tLRPC$ChatPhoto = tLRPC$Chat.photo) == null || (tLRPC$ChatPhoto instanceof TLRPC$TL_chatPhotoEmpty)) ? false : true;
    }

    public static boolean hasPublicLink(TLRPC$Chat tLRPC$Chat, String str) {
        if (tLRPC$Chat == null) {
            return false;
        }
        if (!TextUtils.isEmpty(tLRPC$Chat.username)) {
            return tLRPC$Chat.username.equalsIgnoreCase(str);
        }
        if (tLRPC$Chat.usernames != null) {
            for (int i = 0; i < tLRPC$Chat.usernames.size(); i++) {
                TLRPC$TL_username tLRPC$TL_username = (TLRPC$TL_username) tLRPC$Chat.usernames.get(i);
                if (tLRPC$TL_username != null && tLRPC$TL_username.active && !TextUtils.isEmpty(tLRPC$TL_username.username) && tLRPC$TL_username.username.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasStories(TLRPC$Chat tLRPC$Chat) {
        return tLRPC$Chat != null && MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(-tLRPC$Chat.id);
    }

    public static boolean isActionBanned(TLRPC$Chat tLRPC$Chat, int i) {
        return tLRPC$Chat != null && (getBannedRight(tLRPC$Chat.banned_rights, i) || getBannedRight(tLRPC$Chat.default_banned_rights, i));
    }

    public static boolean isActionBannedByDefault(TLRPC$Chat tLRPC$Chat, int i) {
        if (tLRPC$Chat == null) {
            return false;
        }
        if (getBannedRight(tLRPC$Chat.banned_rights, i) && getBannedRight(tLRPC$Chat.default_banned_rights, i)) {
            return true;
        }
        return getBannedRight(tLRPC$Chat.default_banned_rights, i);
    }

    private static boolean isAdminAction(int i) {
        return i == 12 || i == 13 || i == 15 || i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    private static boolean isBannableAction(int i) {
        if (i != 0 && i != 1 && i != 3) {
            switch (i) {
                default:
                    switch (i) {
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                            break;
                        default:
                            return false;
                    }
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    return true;
            }
        }
        return true;
    }

    public static boolean isBoostSupported(TLRPC$Chat tLRPC$Chat) {
        return isChannelAndNotMegaGroup(tLRPC$Chat) || isMegagroup(tLRPC$Chat);
    }

    public static boolean isBoosted(TLRPC$ChatFull tLRPC$ChatFull) {
        return tLRPC$ChatFull != null && tLRPC$ChatFull.boosts_applied > 0;
    }

    public static boolean isCanWriteToChannel(long j, int i) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j));
        return canSendMessages(chat) || chat.megagroup;
    }

    public static boolean isChannel(long j, int i) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j));
        return (chat instanceof TLRPC$TL_channel) || (chat instanceof TLRPC$TL_channelForbidden);
    }

    public static boolean isChannel(TLRPC$Chat tLRPC$Chat) {
        return (tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden);
    }

    public static boolean isChannelAndNotMegaGroup(long j, int i) {
        return isChannelAndNotMegaGroup(MessagesController.getInstance(i).getChat(Long.valueOf(j)));
    }

    public static boolean isChannelAndNotMegaGroup(TLRPC$Chat tLRPC$Chat) {
        return isChannel(tLRPC$Chat) && !isMegagroup(tLRPC$Chat);
    }

    public static boolean isChannelOrGiga(TLRPC$Chat tLRPC$Chat) {
        return ((tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden)) && (!tLRPC$Chat.megagroup || tLRPC$Chat.gigagroup);
    }

    public static boolean isForum(int i, long j) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
        if (chat != null) {
            return chat.forum;
        }
        return false;
    }

    public static boolean isForum(TLRPC$Chat tLRPC$Chat) {
        return tLRPC$Chat != null && tLRPC$Chat.forum;
    }

    public static boolean isIgnoredChatRestrictionsForBoosters(TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat != null) {
            return isIgnoredChatRestrictionsForBoosters(MessagesController.getInstance(UserConfig.selectedAccount).getChatFull(tLRPC$Chat.id));
        }
        return false;
    }

    public static boolean isIgnoredChatRestrictionsForBoosters(TLRPC$ChatFull tLRPC$ChatFull) {
        int i;
        return tLRPC$ChatFull != null && (i = tLRPC$ChatFull.boosts_unrestrict) > 0 && tLRPC$ChatFull.boosts_applied - i >= 0;
    }

    public static boolean isInChat(TLRPC$Chat tLRPC$Chat) {
        return (tLRPC$Chat == null || (tLRPC$Chat instanceof TLRPC$TL_chatEmpty) || (tLRPC$Chat instanceof TLRPC$TL_chatForbidden) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden) || tLRPC$Chat.left || tLRPC$Chat.kicked || tLRPC$Chat.deactivated) ? false : true;
    }

    public static boolean isKickedFromChat(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        return tLRPC$Chat == null || (tLRPC$Chat instanceof TLRPC$TL_chatEmpty) || (tLRPC$Chat instanceof TLRPC$TL_chatForbidden) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden) || tLRPC$Chat.kicked || tLRPC$Chat.deactivated || ((tLRPC$TL_chatBannedRights = tLRPC$Chat.banned_rights) != null && tLRPC$TL_chatBannedRights.view_messages);
    }

    public static boolean isLeftFromChat(TLRPC$Chat tLRPC$Chat) {
        return tLRPC$Chat == null || (tLRPC$Chat instanceof TLRPC$TL_chatEmpty) || (tLRPC$Chat instanceof TLRPC$TL_chatForbidden) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden) || tLRPC$Chat.left || tLRPC$Chat.deactivated;
    }

    public static boolean isMegagroup(int i, long j) {
        TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j));
        return isChannel(chat) && chat.megagroup;
    }

    public static boolean isMegagroup(TLRPC$Chat tLRPC$Chat) {
        return ((tLRPC$Chat instanceof TLRPC$TL_channel) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden)) && tLRPC$Chat.megagroup;
    }

    public static boolean isMyTopic(int i, long j, long j2) {
        return isMyTopic(i, MessagesController.getInstance(i).getTopicsController().findTopic(j, j2));
    }

    public static boolean isMyTopic(int i, TLRPC$Chat tLRPC$Chat, long j) {
        return tLRPC$Chat != null && tLRPC$Chat.forum && isMyTopic(i, tLRPC$Chat.id, j);
    }

    public static boolean isMyTopic(int i, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        if (tLRPC$TL_forumTopic != null) {
            if (!tLRPC$TL_forumTopic.my) {
                TLRPC$Peer tLRPC$Peer = tLRPC$TL_forumTopic.from_id;
                if (!(tLRPC$Peer instanceof TLRPC$TL_peerUser) || tLRPC$Peer.user_id != UserConfig.getInstance(i).clientUserId) {
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isNotInChat(TLRPC$Chat tLRPC$Chat) {
        return tLRPC$Chat == null || (tLRPC$Chat instanceof TLRPC$TL_chatEmpty) || (tLRPC$Chat instanceof TLRPC$TL_chatForbidden) || (tLRPC$Chat instanceof TLRPC$TL_channelForbidden) || tLRPC$Chat.left || tLRPC$Chat.kicked || tLRPC$Chat.deactivated;
    }

    public static boolean isPossibleRemoveChatRestrictionsByBoosts(TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat != null) {
            return isPossibleRemoveChatRestrictionsByBoosts(MessagesController.getInstance(UserConfig.selectedAccount).getChatFull(tLRPC$Chat.id));
        }
        return false;
    }

    public static boolean isPossibleRemoveChatRestrictionsByBoosts(TLRPC$ChatFull tLRPC$ChatFull) {
        return tLRPC$ChatFull != null && tLRPC$ChatFull.boosts_unrestrict > 0;
    }

    public static boolean isPublic(TLRPC$Chat tLRPC$Chat) {
        return !TextUtils.isEmpty(getPublicUsername(tLRPC$Chat));
    }

    public static boolean reactionIsAvailable(TLRPC$ChatFull tLRPC$ChatFull, String str) {
        TLRPC$ChatReactions tLRPC$ChatReactions = tLRPC$ChatFull.available_reactions;
        if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsAll) {
            return true;
        }
        if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsSome) {
            TLRPC$TL_chatReactionsSome tLRPC$TL_chatReactionsSome = (TLRPC$TL_chatReactionsSome) tLRPC$ChatReactions;
            for (int i = 0; i < tLRPC$TL_chatReactionsSome.reactions.size(); i++) {
                if ((tLRPC$TL_chatReactionsSome.reactions.get(i) instanceof TLRPC$TL_reactionEmoji) && TextUtils.equals(((TLRPC$TL_reactionEmoji) tLRPC$TL_chatReactionsSome.reactions.get(i)).emoticon, str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean shouldSendAnonymously(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        return (tLRPC$Chat == null || (tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) == null || !tLRPC$TL_chatAdminRights.anonymous) ? false : true;
    }
}
