package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_quickReplies extends TLRPC$messages_quickReplies {
    public ArrayList quick_replies = new ArrayList();
    public ArrayList messages = new ArrayList();
    public ArrayList chats = new ArrayList();
    public ArrayList users = new ArrayList();

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            this.quick_replies.add(TLRPC$TL_quickReply.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
        }
        int readInt323 = abstractSerializedData.readInt32(z);
        if (readInt323 != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
            }
            return;
        }
        int readInt324 = abstractSerializedData.readInt32(z);
        for (int i2 = 0; i2 < readInt324; i2++) {
            this.messages.add(TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
        }
        int readInt325 = abstractSerializedData.readInt32(z);
        if (readInt325 != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
            }
            return;
        }
        int readInt326 = abstractSerializedData.readInt32(z);
        for (int i3 = 0; i3 < readInt326; i3++) {
            this.chats.add(TLRPC$Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
        }
        int readInt327 = abstractSerializedData.readInt32(z);
        if (readInt327 != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt327)));
            }
        } else {
            int readInt328 = abstractSerializedData.readInt32(z);
            for (int i4 = 0; i4 < readInt328; i4++) {
                this.users.add(TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
        }
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(-963811691);
        abstractSerializedData.writeInt32(481674261);
        int size = this.quick_replies.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            ((TLRPC$TL_quickReply) this.quick_replies.get(i)).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(481674261);
        int size2 = this.messages.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            ((TLRPC$Message) this.messages.get(i2)).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(481674261);
        int size3 = this.chats.size();
        abstractSerializedData.writeInt32(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            ((TLRPC$Chat) this.chats.get(i3)).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(481674261);
        int size4 = this.users.size();
        abstractSerializedData.writeInt32(size4);
        for (int i4 = 0; i4 < size4; i4++) {
            ((TLRPC$User) this.users.get(i4)).serializeToStream(abstractSerializedData);
        }
    }
}
