package org.telegram.tgnet;

public class TLRPC$TL_account_deleteAccount extends TLObject {
    public static int constructor = 1099779595;
    public String reason;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.reason);
    }
}
