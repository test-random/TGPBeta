package org.telegram.p009ui;

import org.telegram.p009ui.LaunchActivity;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final class LaunchActivity$10$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final LaunchActivity$10$$ExternalSyntheticLambda0 INSTANCE = new LaunchActivity$10$$ExternalSyntheticLambda0();

    private LaunchActivity$10$$ExternalSyntheticLambda0() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LaunchActivity.C327210.lambda$onEmojiSelected$0(tLObject, tLRPC$TL_error);
    }
}
