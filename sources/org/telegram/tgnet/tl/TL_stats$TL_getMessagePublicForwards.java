package org.telegram.tgnet.tl;

import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputChannel;

public class TL_stats$TL_getMessagePublicForwards extends TLObject {
    public TLRPC$InputChannel channel;
    public int limit;
    public int msg_id;
    public String offset;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TL_stats$TL_publicForwards.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(1595212100);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        abstractSerializedData.writeString(this.offset);
        abstractSerializedData.writeInt32(this.limit);
    }
}
