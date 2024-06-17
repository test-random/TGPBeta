package org.telegram.tgnet;

public class TLRPC$TL_account_checkUsername extends TLObject {
    public String username;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(655677548);
        abstractSerializedData.writeString(this.username);
    }
}
