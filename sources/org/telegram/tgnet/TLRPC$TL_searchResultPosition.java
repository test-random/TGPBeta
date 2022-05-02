package org.telegram.tgnet;

public class TLRPC$TL_searchResultPosition extends TLObject {
    public static int constructor = 2137295719;
    public int date;
    public int msg_id;
    public int offset;

    public static TLRPC$TL_searchResultPosition TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_searchResultPosition tLRPC$TL_searchResultPosition = new TLRPC$TL_searchResultPosition();
            tLRPC$TL_searchResultPosition.readParams(abstractSerializedData, z);
            return tLRPC$TL_searchResultPosition;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_searchResultPosition", Integer.valueOf(i)));
        }
    }

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.msg_id = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.offset = abstractSerializedData.readInt32(z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.msg_id);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.offset);
    }
}
