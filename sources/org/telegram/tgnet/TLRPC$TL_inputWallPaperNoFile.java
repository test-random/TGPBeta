package org.telegram.tgnet;

public class TLRPC$TL_inputWallPaperNoFile extends TLRPC$InputWallPaper {
    public static int constructor = -1770371538;
    public long f930id;

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.f930id = abstractSerializedData.readInt64(z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.f930id);
    }
}
