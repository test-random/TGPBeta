package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
public final class MessagesController$$ExternalSyntheticLambda383 implements RequestDelegate {
    public static final MessagesController$$ExternalSyntheticLambda383 INSTANCE = new MessagesController$$ExternalSyntheticLambda383();

    private MessagesController$$ExternalSyntheticLambda383() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$65(tLObject, tLRPC$TL_error);
    }
}
