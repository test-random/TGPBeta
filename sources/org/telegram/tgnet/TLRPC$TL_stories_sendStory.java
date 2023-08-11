package org.telegram.tgnet;

import java.util.ArrayList;
public class TLRPC$TL_stories_sendStory extends TLObject {
    public static int constructor = -732562196;
    public String caption;
    public int flags;
    public TLRPC$InputMedia media;
    public boolean noforwards;
    public int period;
    public boolean pinned;
    public long random_id;
    public ArrayList<TLRPC$MediaArea> media_areas = new ArrayList<>();
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public ArrayList<TLRPC$InputPrivacyRule> privacy_rules = new ArrayList<>();

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.pinned ? this.flags | 4 : this.flags & (-5);
        this.flags = i;
        int i2 = this.noforwards ? i | 16 : i & (-17);
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.media.serializeToStream(abstractSerializedData);
        if ((this.flags & 32) != 0) {
            abstractSerializedData.writeInt32(481674261);
            int size = this.media_areas.size();
            abstractSerializedData.writeInt32(size);
            for (int i3 = 0; i3 < size; i3++) {
                this.media_areas.get(i3).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.caption);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.entities.size();
            abstractSerializedData.writeInt32(size2);
            for (int i4 = 0; i4 < size2; i4++) {
                this.entities.get(i4).serializeToStream(abstractSerializedData);
            }
        }
        abstractSerializedData.writeInt32(481674261);
        int size3 = this.privacy_rules.size();
        abstractSerializedData.writeInt32(size3);
        for (int i5 = 0; i5 < size3; i5++) {
            this.privacy_rules.get(i5).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt64(this.random_id);
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.period);
        }
    }
}
