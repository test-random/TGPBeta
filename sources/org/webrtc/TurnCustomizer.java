package org.webrtc;

public class TurnCustomizer {
    private long nativeTurnCustomizer;

    private static native void nativeFreeTurnCustomizer(long j);

    public TurnCustomizer(long j) {
        this.nativeTurnCustomizer = j;
    }

    public void dispose() {
        checkTurnCustomizerExists();
        nativeFreeTurnCustomizer(this.nativeTurnCustomizer);
        this.nativeTurnCustomizer = 0L;
    }

    @CalledByNative
    long getNativeTurnCustomizer() {
        checkTurnCustomizerExists();
        return this.nativeTurnCustomizer;
    }

    private void checkTurnCustomizerExists() {
        if (this.nativeTurnCustomizer == 0) {
            throw new IllegalStateException("TurnCustomizer has been disposed.");
        }
    }
}
