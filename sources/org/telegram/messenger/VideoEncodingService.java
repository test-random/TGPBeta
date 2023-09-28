package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
public class VideoEncodingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private static VideoEncodingService instance;
    private NotificationCompat.Builder builder;
    int currentAccount;
    private MediaController.VideoConvertMessage currentMessage;
    String currentPath;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start() {
        if (instance == null) {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class));
        }
    }

    public static void stop() {
        VideoEncodingService videoEncodingService = instance;
        if (videoEncodingService != null) {
            videoEncodingService.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        try {
            stopForeground(true);
        } catch (Throwable unused) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        this.currentMessage = null;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        String str2;
        if (i == NotificationCenter.fileUploadProgressChanged) {
            String str3 = (String) objArr[0];
            if (i2 == this.currentAccount && (str2 = this.currentPath) != null && str2.equals(str3)) {
                float min = Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue()));
                Boolean bool = (Boolean) objArr[3];
                int i3 = (int) (min * 100.0f);
                this.builder.setProgress(100, i3, i3 == 0);
                try {
                    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        } else if (i == NotificationCenter.fileUploaded || i == NotificationCenter.fileUploadFailed) {
            String str4 = (String) objArr[0];
            if (i2 == this.currentAccount && (str = this.currentPath) != null && str.equals(str4)) {
                MediaController.VideoConvertMessage currentForegroundConverMessage = MediaController.getInstance().getCurrentForegroundConverMessage();
                if (currentForegroundConverMessage != null) {
                    setCurrentMessage(currentForegroundConverMessage);
                } else {
                    stopSelf();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        if (isRunning()) {
            return 2;
        }
        instance = this;
        MediaController.VideoConvertMessage currentForegroundConverMessage = MediaController.getInstance().getCurrentForegroundConverMessage();
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder = builder;
            builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", R.string.AppName));
        }
        startForeground(4, this.builder.build());
        setCurrentMessage(currentForegroundConverMessage);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoEncodingService.this.lambda$onStartCommand$0();
            }
        });
        FileLog.d("VideoEncodingService: start foreground");
        return 2;
    }

    public void lambda$onStartCommand$0() {
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
    }

    private void updateBuilderForMessage(MediaController.VideoConvertMessage videoConvertMessage) {
        if (videoConvertMessage == null) {
            return;
        }
        MessageObject messageObject = videoConvertMessage.messageObject;
        if (messageObject != null && MessageObject.isGifMessage(messageObject.messageOwner)) {
            NotificationCompat.Builder builder = this.builder;
            int i = R.string.SendingGif;
            builder.setTicker(LocaleController.getString("SendingGif", i));
            this.builder.setContentText(LocaleController.getString("SendingGif", i));
        } else {
            NotificationCompat.Builder builder2 = this.builder;
            int i2 = R.string.SendingVideo;
            builder2.setTicker(LocaleController.getString("SendingVideo", i2));
            this.builder.setContentText(LocaleController.getString("SendingVideo", i2));
        }
        this.builder.setProgress(100, 0, true);
    }

    private void setCurrentMessage(MediaController.VideoConvertMessage videoConvertMessage) {
        MediaController.VideoConvertMessage videoConvertMessage2 = this.currentMessage;
        if (videoConvertMessage2 == videoConvertMessage) {
            return;
        }
        if (videoConvertMessage2 != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        }
        updateBuilderForMessage(videoConvertMessage);
        int i = videoConvertMessage.currentAccount;
        this.currentAccount = i;
        this.currentPath = videoConvertMessage.messageObject.messageOwner.attachPath;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileUploadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        if (isRunning()) {
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        }
    }

    public static boolean isRunning() {
        return instance != null;
    }
}
