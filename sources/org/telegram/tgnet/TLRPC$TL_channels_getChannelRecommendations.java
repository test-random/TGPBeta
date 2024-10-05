package org.telegram.tgnet;

public class TLRPC$TL_channels_getChannelRecommendations extends TLObject {
    public TLRPC$InputChannel channel;
    public int flags;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_Chats.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(631707458);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            this.channel.serializeToStream(abstractSerializedData);
        }
    }
}
