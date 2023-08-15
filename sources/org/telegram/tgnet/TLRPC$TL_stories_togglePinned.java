package org.telegram.tgnet;

import java.util.ArrayList;
public class TLRPC$TL_stories_togglePinned extends TLObject {
    public static int constructor = 1365256516;
    public ArrayList<Integer> id = new ArrayList<>();
    public boolean pinned;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Vector tLRPC$Vector = new TLRPC$Vector();
        int readInt32 = abstractSerializedData.readInt32(z);
        for (int i2 = 0; i2 < readInt32; i2++) {
            tLRPC$Vector.objects.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
        }
        return tLRPC$Vector;
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(481674261);
        int size = this.id.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.id.get(i).intValue());
        }
        abstractSerializedData.writeBool(this.pinned);
    }
}