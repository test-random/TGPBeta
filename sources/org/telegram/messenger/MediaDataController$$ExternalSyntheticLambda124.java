package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$MessageEntity;

public final class MediaDataController$$ExternalSyntheticLambda124 implements GenericProvider {
    public static final MediaDataController$$ExternalSyntheticLambda124 INSTANCE = new MediaDataController$$ExternalSyntheticLambda124();

    private MediaDataController$$ExternalSyntheticLambda124() {
    }

    @Override
    public final Object provide(Object obj) {
        TLRPC$MessageEntity lambda$getEntities$134;
        lambda$getEntities$134 = MediaDataController.lambda$getEntities$134((Void) obj);
        return lambda$getEntities$134;
    }
}
