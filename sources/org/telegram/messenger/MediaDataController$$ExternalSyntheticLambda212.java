package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final class MediaDataController$$ExternalSyntheticLambda212 implements RequestDelegate {
    public static final MediaDataController$$ExternalSyntheticLambda212 INSTANCE = new MediaDataController$$ExternalSyntheticLambda212();

    private MediaDataController$$ExternalSyntheticLambda212() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersByIdAsRead$55(tLObject, tLRPC$TL_error);
    }
}
