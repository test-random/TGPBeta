package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_account_savedRingtones extends TLRPC$account_SavedRingtones {
    public static int constructor = -1041683259;
    public long hash;
    public ArrayList<TLRPC$Document> ringtones = new ArrayList<>();

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.hash = abstractSerializedData.readInt64(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 == 481674261) {
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.ringtones.add(TLdeserialize);
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
        }
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.hash);
        abstractSerializedData.writeInt32(481674261);
        int size = this.ringtones.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.ringtones.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
