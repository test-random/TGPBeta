package org.telegram.messenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.NotificationCenter;

public class NotificationImageProvider extends ContentProvider implements NotificationCenter.NotificationCenterDelegate {
    public static final String AUTHORITY = "org.telegram.messenger.beta.notification_image_provider";
    private static final UriMatcher matcher;
    private HashSet<String> waitingForFiles = new HashSet<>();
    private final Object sync = new Object();
    private HashMap<String, Long> fileStartTimes = new HashMap<>();

    @Override
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        matcher = uriMatcher;
        uriMatcher.addURI(AUTHORITY, "msg_media_raw/#/*", 1);
    }

    @Override
    public boolean onCreate() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
        }
        return true;
    }

    @Override
    public void shutdown() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoaded);
        }
    }

    @Override
    public String[] getStreamTypes(Uri uri, String str) {
        if (str.startsWith("*/") || str.startsWith("image/")) {
            return new String[]{"image/jpeg", "image/png", "image/webp"};
        }
        return null;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (!"r".equals(str)) {
            throw new SecurityException("Can only open files for read");
        } else if (matcher.match(uri) == 1) {
            List<String> pathSegments = uri.getPathSegments();
            Integer.parseInt(pathSegments.get(1));
            String str2 = pathSegments.get(2);
            String queryParameter = uri.getQueryParameter("final_path");
            String queryParameter2 = uri.getQueryParameter("fallback");
            File file = new File(queryParameter);
            ApplicationLoader.postInitApplication();
            if (!AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                if (!file.exists()) {
                    Long l = this.fileStartTimes.get(str2);
                    long longValue = l != null ? l.longValue() : System.currentTimeMillis();
                    if (l == null) {
                        this.fileStartTimes.put(str2, Long.valueOf(longValue));
                    }
                    while (!file.exists()) {
                        if (System.currentTimeMillis() - longValue >= 3000) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("Waiting for " + str2 + " to download timed out");
                            }
                            if (!TextUtils.isEmpty(queryParameter2)) {
                                File file2 = new File(queryParameter2);
                                if (!AndroidUtilities.isInternalUri(Uri.fromFile(file2))) {
                                    return ParcelFileDescriptor.open(file2, 268435456);
                                }
                                throw new SecurityException("trying to read internal file");
                            }
                            throw new FileNotFoundException("Download timed out");
                        }
                        synchronized (this.sync) {
                            this.waitingForFiles.add(str2);
                            try {
                                this.sync.wait(1000L);
                            } catch (InterruptedException unused) {
                            }
                        }
                    }
                    if (AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                        throw new SecurityException("trying to read internal file");
                    }
                }
                return ParcelFileDescriptor.open(file, 268435456);
            }
            throw new SecurityException("trying to read internal file");
        } else {
            throw new FileNotFoundException("Invalid URI");
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoaded) {
            synchronized (this.sync) {
                String str = (String) objArr[0];
                if (this.waitingForFiles.remove(str)) {
                    this.fileStartTimes.remove(str);
                    this.sync.notifyAll();
                }
            }
        }
    }
}
