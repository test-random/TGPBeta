package org.telegram.tgnet;

public class TLRPC$TL_photos_uploadContactProfilePhoto extends TLObject {
    public static int constructor = -1189444673;
    public TLRPC$InputFile file;
    public int flags;
    public boolean save;
    public boolean suggest;
    public TLRPC$InputUser user_id;
    public TLRPC$InputFile video;
    public double video_start_ts;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_photos_photo.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.suggest ? this.flags | 8 : this.flags & (-9);
        this.flags = i;
        int i2 = this.save ? i | 16 : i & (-17);
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.user_id.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            this.file.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            this.video.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeDouble(this.video_start_ts);
        }
    }
}
