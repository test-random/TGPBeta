package org.telegram.tgnet;

public class TLRPC$TL_documentAttributeHasStickers extends TLRPC$DocumentAttribute {
    public static int constructor = -1744710921;

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
