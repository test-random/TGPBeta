package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
public final class MessagesController$$ExternalSyntheticLambda381 implements RequestDelegate {
    public static final MessagesController$$ExternalSyntheticLambda381 INSTANCE = new MessagesController$$ExternalSyntheticLambda381();

    private MessagesController$$ExternalSyntheticLambda381() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$installTheme$106(tLObject, tLRPC$TL_error);
    }
}
