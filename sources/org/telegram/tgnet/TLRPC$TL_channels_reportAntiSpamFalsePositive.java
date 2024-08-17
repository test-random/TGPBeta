package org.telegram.tgnet;

public class TLRPC$TL_channels_reportAntiSpamFalsePositive extends TLObject {
    public TLRPC$InputChannel channel;
    public int msg_id;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(-1471109485);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
    }
}
