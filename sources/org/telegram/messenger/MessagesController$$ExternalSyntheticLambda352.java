package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final class MessagesController$$ExternalSyntheticLambda352 implements RequestDelegate {
    public static final MessagesController$$ExternalSyntheticLambda352 INSTANCE = new MessagesController$$ExternalSyntheticLambda352();

    private MessagesController$$ExternalSyntheticLambda352() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$251(tLObject, tLRPC$TL_error);
    }
}
