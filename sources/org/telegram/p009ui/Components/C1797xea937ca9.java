package org.telegram.p009ui.Components;

import org.telegram.p009ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.p009ui.Components.SimpleFloatPropertyCompat;

public final class C1797xea937ca9 implements SimpleFloatPropertyCompat.Setter {
    public static final C1797xea937ca9 INSTANCE = new C1797xea937ca9();

    private C1797xea937ca9() {
    }

    @Override
    public final void set(Object obj, float f) {
        ((ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) obj).setSwipeOffsetY(f);
    }
}
