package org.telegram.messenger.voip;

import android.media.AudioTrack;
import java.nio.ByteBuffer;

public class AudioTrackJNI {
    private AudioTrack audioTrack;
    private byte[] buffer = new byte[1920];
    private long nativeInst;
    private boolean needResampling;
    private boolean running;
    private Thread thread;

    private native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long j) {
        this.nativeInst = j;
    }

    private int getBufferSize(int i, int i2) {
        return Math.max(AudioTrack.getMinBufferSize(i2, 4, 2), i);
    }

    public void init(int i, int i2, int i3, int i4) {
        if (this.audioTrack == null) {
            AudioTrack audioTrack = new AudioTrack(0, 48000, i3 == 1 ? 4 : 12, 2, getBufferSize(i4, 48000), 1);
            this.audioTrack = audioTrack;
            if (audioTrack.getState() != 1) {
                VLog.m21w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable unused) {
                }
                int bufferSize = getBufferSize(i4 * 6, 44100);
                VLog.m27d("buffer size: " + bufferSize);
                this.audioTrack = new AudioTrack(0, 44100, i3 == 1 ? 4 : 12, 2, bufferSize, 1);
                this.needResampling = true;
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    public void stop() {
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            try {
                audioTrack.stop();
            } catch (Exception unused) {
            }
        }
    }

    public void release() {
        this.running = false;
        Thread thread = this.thread;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                VLog.m24e(e);
            }
            this.thread = null;
        }
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            audioTrack.release();
            this.audioTrack = null;
        }
    }

    public void start() {
        if (this.thread == null) {
            startThread();
        } else {
            this.audioTrack.play();
        }
    }

    private void startThread() {
        if (this.thread == null) {
            this.running = true;
            Thread thread = new Thread(new Runnable() {
                @Override
                public final void run() {
                    AudioTrackJNI.this.lambda$startThread$0();
                }
            });
            this.thread = thread;
            thread.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }

    public void lambda$startThread$0() {
        try {
            this.audioTrack.play();
            ByteBuffer byteBuffer = null;
            ByteBuffer allocateDirect = this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
            if (this.needResampling) {
                byteBuffer = ByteBuffer.allocateDirect(1764);
            }
            while (this.running) {
                try {
                    if (this.needResampling) {
                        nativeCallback(this.buffer);
                        allocateDirect.rewind();
                        allocateDirect.put(this.buffer);
                        Resampler.convert48to44(allocateDirect, byteBuffer);
                        byteBuffer.rewind();
                        byteBuffer.get(this.buffer, 0, 1764);
                        this.audioTrack.write(this.buffer, 0, 1764);
                    } else {
                        nativeCallback(this.buffer);
                        this.audioTrack.write(this.buffer, 0, 1920);
                    }
                } catch (Exception e) {
                    VLog.m24e(e);
                }
                if (!this.running) {
                    this.audioTrack.stop();
                    break;
                }
                continue;
            }
            VLog.m23i("audiotrack thread exits");
        } catch (Exception e2) {
            VLog.m25e("error starting AudioTrack", e2);
        }
    }
}
