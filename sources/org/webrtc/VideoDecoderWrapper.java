package org.webrtc;

import org.webrtc.VideoDecoder;

public class VideoDecoderWrapper {
    VideoDecoderWrapper() {
    }

    static VideoDecoder.Callback createDecoderCallback(final long j) {
        return new VideoDecoder.Callback() {
            @Override
            public final void onDecodedFrame(VideoFrame videoFrame, Integer num, Integer num2) {
                VideoDecoderWrapper.nativeOnDecodedFrame(j, videoFrame, num, num2);
            }
        };
    }

    public static native void nativeOnDecodedFrame(long j, VideoFrame videoFrame, Integer num, Integer num2);
}
