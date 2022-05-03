package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$DecryptedMessageMedia extends TLObject {
    public double _long;
    public long access_hash;
    public String address;
    public ArrayList<TLRPC$DocumentAttribute> attributes = new ArrayList<>();
    public String caption;
    public int date;
    public int dc_id;
    public int duration;
    public String file_name;
    public String first_name;
    public int h;
    public long id;
    public byte[] iv;
    public byte[] key;
    public String last_name;
    public double lat;
    public String mime_type;
    public String phone_number;
    public String provider;
    public int size;
    public int thumb_h;
    public int thumb_w;
    public String title;
    public String url;
    public long user_id;
    public String venue_id;
    public int w;

    public static TLRPC$DecryptedMessageMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia;
        switch (i) {
            case -1978796689:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVenue();
                break;
            case -1760785394:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVideo();
                break;
            case -1332395189:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaDocument() {
                    public static int constructor = -1332395189;
                    public byte[] thumb;

                    @Override
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.file_name = abstractSerializedData2.readString(z2);
                        this.mime_type = abstractSerializedData2.readString(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeString(this.file_name);
                        abstractSerializedData2.writeString(this.mime_type);
                        abstractSerializedData2.writeInt32(this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case -452652584:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaWebPage();
                break;
            case -235238024:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaPhoto();
                break;
            case -90853155:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaExternalDocument();
                break;
            case 144661578:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaEmpty();
                break;
            case 846826124:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaPhoto() {
                    public static int constructor = 846826124;
                    public byte[] thumb;

                    @Override
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32(this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 893913689:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaGeoPoint();
                break;
            case 1290694387:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVideo() {
                    public static int constructor = 1290694387;
                    public byte[] thumb;

                    @Override
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.duration = abstractSerializedData2.readInt32(z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeInt32(this.duration);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32(this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 1380598109:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVideo() {
                    public static int constructor = 1380598109;
                    public byte[] thumb;

                    @Override
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.duration = abstractSerializedData2.readInt32(z2);
                        this.mime_type = abstractSerializedData2.readString(z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeInt32(this.duration);
                        abstractSerializedData2.writeString(this.mime_type);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32(this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 1474341323:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaAudio();
                break;
            case 1485441687:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaContact();
                break;
            case 1619031439:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaAudio() {
                    public static int constructor = 1619031439;

                    @Override
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.duration = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.duration);
                        abstractSerializedData2.writeInt32(this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 2063502050:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaDocument();
                break;
            default:
                tLRPC$DecryptedMessageMedia = null;
                break;
        }
        if (tLRPC$DecryptedMessageMedia != null || !z) {
            if (tLRPC$DecryptedMessageMedia != null) {
                tLRPC$DecryptedMessageMedia.readParams(abstractSerializedData, z);
            }
            return tLRPC$DecryptedMessageMedia;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DecryptedMessageMedia", Integer.valueOf(i)));
    }
}
