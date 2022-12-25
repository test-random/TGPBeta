package org.telegram.tgnet;

public class TLRPC$TL_photoSize extends TLRPC$PhotoSize {
    public static int constructor = 1976012384;

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.type = abstractSerializedData.readString(z);
        this.f879w = abstractSerializedData.readInt32(z);
        this.f878h = abstractSerializedData.readInt32(z);
        this.size = abstractSerializedData.readInt32(z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.type);
        abstractSerializedData.writeInt32(this.f879w);
        abstractSerializedData.writeInt32(this.f878h);
        abstractSerializedData.writeInt32(this.size);
    }
}
