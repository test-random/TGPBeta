package org.telegram.tgnet;

public class TLRPC$TL_account_getTheme extends TLObject {
    public static int constructor = -1919060949;
    public long document_id;
    public String format;
    public TLRPC$InputTheme theme;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Theme.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.format);
        this.theme.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.document_id);
    }
}
