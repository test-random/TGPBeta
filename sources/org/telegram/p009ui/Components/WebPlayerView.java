package org.telegram.p009ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.p004ui.AspectRatioFrameLayout;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C1072R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.p009ui.Components.VideoPlayer;
import org.telegram.p009ui.Components.WebPlayerView;
import org.webrtc.MediaStreamTrack;

public class WebPlayerView extends ViewGroup implements VideoPlayer.VideoPlayerDelegate, AudioManager.OnAudioFocusChangeListener {
    private static int lastContainerId = 4001;
    private boolean allowInlineAnimation;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private Paint backgroundPaint;
    private TextureView changedTextureView;
    private boolean changingTextureView;
    private ControlsView controlsView;
    private float currentAlpha;
    private Bitmap currentBitmap;
    private AsyncTask currentTask;
    private String currentYoutubeId;
    private WebPlayerViewDelegate delegate;
    private boolean drawImage;
    private boolean firstFrameRendered;
    private ImageView fullscreenButton;
    private boolean hasAudioFocus;
    private boolean inFullscreen;
    private boolean initied;
    private ImageView inlineButton;
    private String interfaceName;
    private boolean isAutoplay;
    private boolean isCompleted;
    private boolean isInline;
    private boolean isStream;
    private long lastUpdateTime;
    private String playAudioType;
    private String playAudioUrl;
    private ImageView playButton;
    private String playVideoType;
    private String playVideoUrl;
    private AnimatorSet progressAnimation;
    private Runnable progressRunnable;
    private RadialProgressView progressView;
    private boolean resumeAudioOnFocusGain;
    private int seekToTime;
    private ImageView shareButton;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Runnable switchToInlineRunnable;
    private boolean switchingInlineMode;
    private ImageView textureImageView;
    private TextureView textureView;
    private ViewGroup textureViewContainer;
    private int videoHeight;
    private VideoPlayer videoPlayer;
    private int videoWidth;
    private int waitingForFirstTextureUpload;
    private WebView webView;
    private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    private static final Pattern vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern twitchClipIdRegex = Pattern.compile("https?://clips\\.twitch\\.tv/(?:[^/]+/)*([^/?#&]+)");
    private static final Pattern twitchStreamIdRegex = Pattern.compile("https?://(?:(?:www\\.)?twitch\\.tv/|player\\.twitch\\.tv/\\?.*?\\bchannel=)([^/#?]+)");
    private static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern twitchClipFilePattern = Pattern.compile("clipInfo\\s*=\\s*(\\{[^']+\\});");
    private static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    private static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    private static final Pattern sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    private static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    private static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    private static final Pattern exprParensPattern = Pattern.compile("[()]");
    private static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|(?:/[a-z]{2}_[A-Z]{2})?/base)?\\.([a-z]+)$");

    public interface CallJavaResultInterface {
        void jsCallFinished(String str);
    }

    public interface WebPlayerViewDelegate {
        boolean checkInlinePermissions();

        ViewGroup getTextureViewContainer();

        void onInitFailed();

        void onInlineSurfaceTextureReady();

        void onPlayStateChanged(WebPlayerView webPlayerView, boolean z);

        void onSharePressed();

        TextureView onSwitchInlineMode(View view, boolean z, int i, int i2, int i3, boolean z2);

        TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2);

        void onVideoSizeChanged(float f, int i);

        void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2);
    }

    @Override
    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
    }

    @Override
    public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
    }

    @Override
    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
    }

    static float access$4724(WebPlayerView webPlayerView, float f) {
        float f2 = webPlayerView.currentAlpha - f;
        webPlayerView.currentAlpha = f2;
        return f2;
    }

    public static class JSExtractor {
        private String jsCode;
        ArrayList<String> codeLines = new ArrayList<>();
        private String[] operators = {"|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*"};
        private String[] assign_operators = {"|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "="};

        public JSExtractor(String str) {
            this.jsCode = str;
        }

        private void interpretExpression(String str, HashMap<String, String> hashMap, int i) throws Exception {
            String trim = str.trim();
            if (TextUtils.isEmpty(trim)) {
                return;
            }
            if (trim.charAt(0) == '(') {
                Matcher matcher = WebPlayerView.exprParensPattern.matcher(trim);
                int i2 = 0;
                while (true) {
                    if (!matcher.find()) {
                        break;
                    } else if (matcher.group(0).indexOf(48) == 40) {
                        i2++;
                    } else {
                        i2--;
                        if (i2 == 0) {
                            interpretExpression(trim.substring(1, matcher.start()), hashMap, i);
                            trim = trim.substring(matcher.end()).trim();
                            if (TextUtils.isEmpty(trim)) {
                                return;
                            }
                        }
                    }
                }
                if (i2 != 0) {
                    throw new Exception(String.format("Premature end of parens in %s", trim));
                }
            }
            int i3 = 0;
            while (true) {
                String[] strArr = this.assign_operators;
                if (i3 < strArr.length) {
                    Matcher matcher2 = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", "[a-zA-Z_$][a-zA-Z_$0-9]*", Pattern.quote(strArr[i3]))).matcher(trim);
                    if (matcher2.find()) {
                        interpretExpression(matcher2.group(3), hashMap, i - 1);
                        String group = matcher2.group(2);
                        if (!TextUtils.isEmpty(group)) {
                            interpretExpression(group, hashMap, i);
                            return;
                        } else {
                            hashMap.put(matcher2.group(1), "");
                            return;
                        }
                    }
                    i3++;
                } else {
                    try {
                        Integer.parseInt(trim);
                        return;
                    } catch (Exception unused) {
                        if (Pattern.compile(String.format(Locale.US, "(?!if|return|true|false)(%s)$", "[a-zA-Z_$][a-zA-Z_$0-9]*")).matcher(trim).find()) {
                            return;
                        }
                        if (trim.charAt(0) == '\"' && trim.charAt(trim.length() - 1) == '\"') {
                            return;
                        }
                        try {
                            new JSONObject(trim).toString();
                            return;
                        } catch (Exception unused2) {
                            Locale locale = Locale.US;
                            Matcher matcher3 = Pattern.compile(String.format(locale, "(%s)\\[(.+)\\]$", "[a-zA-Z_$][a-zA-Z_$0-9]*")).matcher(trim);
                            if (matcher3.find()) {
                                matcher3.group(1);
                                interpretExpression(matcher3.group(2), hashMap, i - 1);
                                return;
                            }
                            Matcher matcher4 = Pattern.compile(String.format(locale, "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$", "[a-zA-Z_$][a-zA-Z_$0-9]*")).matcher(trim);
                            if (matcher4.find()) {
                                String group2 = matcher4.group(1);
                                String group3 = matcher4.group(2);
                                String group4 = matcher4.group(3);
                                if (TextUtils.isEmpty(group3)) {
                                    group3 = group4;
                                }
                                group3.replace("\"", "");
                                String group5 = matcher4.group(4);
                                if (hashMap.get(group2) == null) {
                                    extractObject(group2);
                                }
                                if (group5 == null) {
                                    return;
                                }
                                if (trim.charAt(trim.length() - 1) != ')') {
                                    throw new Exception("last char not ')'");
                                }
                                if (group5.length() != 0) {
                                    for (String str2 : group5.split(",")) {
                                        interpretExpression(str2, hashMap, i);
                                    }
                                    return;
                                }
                                return;
                            }
                            Matcher matcher5 = Pattern.compile(String.format(locale, "(%s)\\[(.+)\\]$", "[a-zA-Z_$][a-zA-Z_$0-9]*")).matcher(trim);
                            if (matcher5.find()) {
                                hashMap.get(matcher5.group(1));
                                interpretExpression(matcher5.group(2), hashMap, i - 1);
                                return;
                            }
                            int i4 = 0;
                            while (true) {
                                String[] strArr2 = this.operators;
                                if (i4 < strArr2.length) {
                                    String str3 = strArr2[i4];
                                    Matcher matcher6 = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", Pattern.quote(str3))).matcher(trim);
                                    if (matcher6.find()) {
                                        boolean[] zArr = new boolean[1];
                                        int i5 = i - 1;
                                        interpretStatement(matcher6.group(1), hashMap, zArr, i5);
                                        if (zArr[0]) {
                                            throw new Exception(String.format("Premature left-side return of %s in %s", str3, trim));
                                        }
                                        interpretStatement(matcher6.group(2), hashMap, zArr, i5);
                                        if (zArr[0]) {
                                            throw new Exception(String.format("Premature right-side return of %s in %s", str3, trim));
                                        }
                                    }
                                    i4++;
                                } else {
                                    Matcher matcher7 = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", "[a-zA-Z_$][a-zA-Z_$0-9]*")).matcher(trim);
                                    if (matcher7.find()) {
                                        extractFunction(matcher7.group(1));
                                    }
                                    throw new Exception(String.format("Unsupported JS expression %s", trim));
                                }
                            }
                        }
                    }
                }
            }
        }

        private void interpretStatement(String str, HashMap<String, String> hashMap, boolean[] zArr, int i) throws Exception {
            if (i < 0) {
                throw new Exception("recursion limit reached");
            }
            zArr[0] = false;
            String trim = str.trim();
            Matcher matcher = WebPlayerView.stmtVarPattern.matcher(trim);
            if (!matcher.find()) {
                Matcher matcher2 = WebPlayerView.stmtReturnPattern.matcher(trim);
                if (matcher2.find()) {
                    trim = trim.substring(matcher2.group(0).length());
                    zArr[0] = true;
                }
            } else {
                trim = trim.substring(matcher.group(0).length());
            }
            interpretExpression(trim, hashMap, i);
        }

        private HashMap<String, Object> extractObject(String str) throws Exception {
            HashMap<String, Object> hashMap = new HashMap<>();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", Pattern.quote(str), "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')")).matcher(this.jsCode);
            String str2 = null;
            while (true) {
                if (!matcher.find()) {
                    break;
                }
                String group = matcher.group();
                String group2 = matcher.group(2);
                if (TextUtils.isEmpty(group2)) {
                    str2 = group2;
                } else {
                    if (!this.codeLines.contains(group)) {
                        this.codeLines.add(matcher.group());
                    }
                    str2 = group2;
                }
            }
            Matcher matcher2 = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')")).matcher(str2);
            while (matcher2.find()) {
                buildFunction(matcher2.group(2).split(","), matcher2.group(3));
            }
            return hashMap;
        }

        private void buildFunction(String[] strArr, String str) throws Exception {
            HashMap<String, String> hashMap = new HashMap<>();
            for (String str2 : strArr) {
                hashMap.put(str2, "");
            }
            String[] split = str.split(";");
            boolean[] zArr = new boolean[1];
            for (String str3 : split) {
                interpretStatement(str3, hashMap, zArr, 100);
                if (zArr[0]) {
                    return;
                }
            }
        }

        public String extractFunction(String str) {
            try {
                String quote = Pattern.quote(str);
                Matcher matcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", quote, quote, quote)).matcher(this.jsCode);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (!this.codeLines.contains(group)) {
                        ArrayList<String> arrayList = this.codeLines;
                        arrayList.add(group + ";");
                    }
                    buildFunction(matcher.group(1).split(","), matcher.group(2));
                }
            } catch (Exception e) {
                this.codeLines.clear();
                FileLog.m31e(e);
            }
            return TextUtils.join("", this.codeLines);
        }
    }

    public static class JavaScriptInterface {
        private final CallJavaResultInterface callJavaResultInterface;

        public JavaScriptInterface(CallJavaResultInterface callJavaResultInterface) {
            this.callJavaResultInterface = callJavaResultInterface;
        }

        @JavascriptInterface
        public void returnResultToJava(String str) {
            this.callJavaResultInterface.jsCallFinished(str);
        }
    }

    protected String downloadUrlContent(AsyncTask asyncTask, String str) {
        return downloadUrlContent(asyncTask, str, null, true);
    }

    protected java.lang.String downloadUrlContent(android.os.AsyncTask r19, java.lang.String r20, java.util.HashMap<java.lang.String, java.lang.String> r21, boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    public class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String str) {
            this.videoId = str;
        }

        @Override
        public java.lang.String[] doInBackground(java.lang.Void... r24) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.WebPlayerView.YoutubeVideoTask.doInBackground(java.lang.Void[]):java.lang.String[]");
        }

        public void lambda$doInBackground$1(String str) {
            if (Build.VERSION.SDK_INT >= 21) {
                WebPlayerView.this.webView.evaluateJavascript(str, new ValueCallback() {
                    @Override
                    public final void onReceiveValue(Object obj) {
                        WebPlayerView.YoutubeVideoTask.this.lambda$doInBackground$0((String) obj);
                    }
                });
                return;
            }
            try {
                String encodeToString = Base64.encodeToString(("<script>" + str + "</script>").getBytes("UTF-8"), 0);
                WebView webView = WebPlayerView.this.webView;
                webView.loadUrl("data:text/html;charset=utf-8;base64," + encodeToString);
            } catch (Exception e) {
                FileLog.m31e(e);
            }
        }

        public void lambda$doInBackground$0(String str) {
            String[] strArr = this.result;
            String str2 = strArr[0];
            String str3 = this.sig;
            strArr[0] = str2.replace(str3, "/signature/" + str.substring(1, str.length() - 1));
            this.countDownLatch.countDown();
        }

        public void onInterfaceResult(String str) {
            String[] strArr = this.result;
            String str2 = strArr[0];
            String str3 = this.sig;
            strArr[0] = str2.replace(str3, "/signature/" + str);
            this.countDownLatch.countDown();
        }

        @Override
        public void onPostExecute(String[] strArr) {
            if (strArr[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m34d("start play youtube video " + strArr[1] + " " + strArr[0]);
                }
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = strArr[0];
                WebPlayerView.this.playVideoType = strArr[1];
                if (WebPlayerView.this.playVideoType.equals("hls")) {
                    WebPlayerView.this.isStream = true;
                }
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled()) {
            } else {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String str) {
            this.videoId = str;
        }

        @Override
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", this.videoId));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(downloadUrlContent).getJSONObject("request").getJSONObject("files");
                if (jSONObject.has("hls")) {
                    JSONObject jSONObject2 = jSONObject.getJSONObject("hls");
                    try {
                        this.results[0] = jSONObject2.getString("url");
                    } catch (Exception unused) {
                        this.results[0] = jSONObject2.getJSONObject("cdns").getJSONObject(jSONObject2.getString("default_cdn")).getString("url");
                    }
                    this.results[1] = "hls";
                } else if (jSONObject.has("progressive")) {
                    this.results[1] = "other";
                    this.results[0] = jSONObject.getJSONArray("progressive").getJSONObject(0).getString("url");
                }
            } catch (Exception e) {
                FileLog.m31e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        @Override
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled()) {
            } else {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String str) {
            this.videoId = str;
        }

        @Override
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", this.videoId));
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher matcher = WebPlayerView.aparatFileListPattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    JSONArray jSONArray = new JSONArray(matcher.group(1));
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                        if (jSONArray2.length() != 0) {
                            JSONObject jSONObject = jSONArray2.getJSONObject(0);
                            if (jSONObject.has("file")) {
                                this.results[0] = jSONObject.getString("file");
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.m31e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        @Override
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled()) {
            } else {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public class TwitchClipVideoTask extends AsyncTask<Void, Void, String> {
        private String currentUrl;
        private String[] results = new String[2];

        public TwitchClipVideoTask(String str, String str2) {
            this.currentUrl = str;
        }

        @Override
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher matcher = WebPlayerView.twitchClipFilePattern.matcher(downloadUrlContent);
                if (matcher.find()) {
                    this.results[0] = new JSONObject(matcher.group(1)).getJSONArray("quality_options").getJSONObject(0).getString("source");
                    this.results[1] = "other";
                }
            } catch (Exception e) {
                FileLog.m31e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        @Override
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled()) {
            } else {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public class TwitchStreamVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[2];
        private String videoId;

        public TwitchStreamVideoTask(String str, String str2) {
            this.videoId = str2;
        }

        @Override
        public String doInBackground(Void... voidArr) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Client-ID", "jzkbprff40iqj646a697cyrvl0zt2m6");
            int indexOf = this.videoId.indexOf(38);
            if (indexOf > 0) {
                this.videoId = this.videoId.substring(0, indexOf);
            }
            WebPlayerView webPlayerView = WebPlayerView.this;
            Locale locale = Locale.US;
            String downloadUrlContent = webPlayerView.downloadUrlContent(this, String.format(locale, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", this.videoId), hashMap, false);
            if (isCancelled()) {
                return null;
            }
            try {
                new JSONObject(downloadUrlContent).getJSONObject("stream");
                JSONObject jSONObject = new JSONObject(WebPlayerView.this.downloadUrlContent(this, String.format(locale, "https://api.twitch.tv/api/channels/%s/access_token", this.videoId), hashMap, false));
                String encode = URLEncoder.encode(jSONObject.getString("sig"), "UTF-8");
                String encode2 = URLEncoder.encode(jSONObject.getString("token"), "UTF-8");
                URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
                String format = String.format(locale, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", this.videoId, "allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=" + ((int) (Math.random() * 1.0E7d)) + "&sig=" + encode + "&token=" + encode2);
                String[] strArr = this.results;
                strArr[0] = format;
                strArr[1] = "hls";
            } catch (Exception e) {
                FileLog.m31e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        @Override
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled()) {
            } else {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String str) {
            this.videoId = str;
        }

        @Override
        public String doInBackground(Void... voidArr) {
            String downloadUrlContent = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", this.videoId));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(downloadUrlContent).getJSONObject("file_versions").getJSONObject("mobile");
                String string = jSONObject.getString(MediaStreamTrack.VIDEO_TRACK_KIND);
                String string2 = jSONObject.getJSONArray(MediaStreamTrack.AUDIO_TRACK_KIND).getString(0);
                if (string != null && string2 != null) {
                    String[] strArr = this.results;
                    strArr[0] = string;
                    strArr[1] = "other";
                    strArr[2] = string2;
                    strArr[3] = "other";
                }
            } catch (Exception e) {
                FileLog.m31e(e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        @Override
        public void onPostExecute(String str) {
            if (str != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = str;
                WebPlayerView.this.playVideoType = this.results[1];
                WebPlayerView.this.playAudioUrl = this.results[2];
                WebPlayerView.this.playAudioType = this.results[3];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (isCancelled()) {
            } else {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    public class ControlsView extends FrameLayout {
        private int bufferedPosition;
        private AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private Runnable hideRunnable;
        private ImageReceiver imageReceiver;
        private boolean isVisible;
        private int lastProgressX;
        private int progress;
        private Paint progressBufferedPaint;
        private Paint progressInnerPaint;
        private StaticLayout progressLayout;
        private Paint progressPaint;
        private boolean progressPressed;
        private TextPaint textPaint;

        public void lambda$new$0() {
            show(false, true);
        }

        public ControlsView(Context context) {
            super(context);
            this.isVisible = true;
            this.hideRunnable = new Runnable() {
                @Override
                public final void run() {
                    WebPlayerView.ControlsView.this.lambda$new$0();
                }
            };
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setColor(-1);
            this.textPaint.setTextSize(AndroidUtilities.m35dp(12.0f));
            Paint paint = new Paint(1);
            this.progressPaint = paint;
            paint.setColor(-15095832);
            Paint paint2 = new Paint();
            this.progressInnerPaint = paint2;
            paint2.setColor(-6975081);
            Paint paint3 = new Paint(1);
            this.progressBufferedPaint = paint3;
            paint3.setColor(-1);
            this.imageReceiver = new ImageReceiver(this);
        }

        public void setDuration(int i) {
            if (this.duration == i || i < 0 || WebPlayerView.this.isStream) {
                return;
            }
            this.duration = i;
            StaticLayout staticLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.duration), this.textPaint, AndroidUtilities.m35dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.durationLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.durationWidth = (int) Math.ceil(this.durationLayout.getLineWidth(0));
            }
            invalidate();
        }

        public void setBufferedProgress(int i) {
            this.bufferedPosition = i;
            invalidate();
        }

        public void setProgress(int i) {
            if (this.progressPressed || i < 0 || WebPlayerView.this.isStream) {
                return;
            }
            this.progress = i;
            this.progressLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.progress), this.textPaint, AndroidUtilities.m35dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            invalidate();
        }

        public void show(boolean z, boolean z2) {
            if (this.isVisible == z) {
                return;
            }
            this.isVisible = z;
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (this.isVisible) {
                if (z2) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f));
                    this.currentAnimation.setDuration(150L);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            ControlsView.this.currentAnimation = null;
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    setAlpha(1.0f);
                }
            } else if (z2) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.currentAnimation = animatorSet3;
                animatorSet3.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f));
                this.currentAnimation.setDuration(150L);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ControlsView.this.currentAnimation = null;
                    }
                });
                this.currentAnimation.start();
            } else {
                setAlpha(0.0f);
            }
            checkNeedHide();
        }

        public void checkNeedHide() {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            if (this.isVisible && WebPlayerView.this.videoPlayer.isPlaying()) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                if (!this.isVisible) {
                    show(true, true);
                    return true;
                }
                onTouchEvent(motionEvent);
                return this.progressPressed;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            checkNeedHide();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            int measuredWidth;
            int measuredHeight;
            int i;
            if (WebPlayerView.this.inFullscreen) {
                i = AndroidUtilities.m35dp(36.0f) + this.durationWidth;
                measuredWidth = (getMeasuredWidth() - AndroidUtilities.m35dp(76.0f)) - this.durationWidth;
                measuredHeight = getMeasuredHeight() - AndroidUtilities.m35dp(28.0f);
            } else {
                measuredWidth = getMeasuredWidth();
                measuredHeight = getMeasuredHeight() - AndroidUtilities.m35dp(12.0f);
                i = 0;
            }
            int i2 = this.duration;
            int i3 = (i2 != 0 ? (int) ((measuredWidth - i) * (this.progress / i2)) : 0) + i;
            if (motionEvent.getAction() == 0) {
                if (this.isVisible && !WebPlayerView.this.isInline && !WebPlayerView.this.isStream) {
                    if (this.duration != 0) {
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        if (x >= i3 - AndroidUtilities.m35dp(10.0f) && x <= AndroidUtilities.m35dp(10.0f) + i3 && y >= measuredHeight - AndroidUtilities.m35dp(10.0f) && y <= measuredHeight + AndroidUtilities.m35dp(10.0f)) {
                            this.progressPressed = true;
                            this.lastProgressX = x;
                            this.currentProgressX = i3;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            invalidate();
                        }
                    }
                } else {
                    show(true, true);
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (WebPlayerView.this.initied && WebPlayerView.this.videoPlayer.isPlaying()) {
                    AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
                }
                if (this.progressPressed) {
                    this.progressPressed = false;
                    if (WebPlayerView.this.initied) {
                        this.progress = (int) (this.duration * ((this.currentProgressX - i) / (measuredWidth - i)));
                        WebPlayerView.this.videoPlayer.seekTo(this.progress * 1000);
                    }
                }
            } else if (motionEvent.getAction() == 2 && this.progressPressed) {
                int x2 = (int) motionEvent.getX();
                int i4 = this.currentProgressX - (this.lastProgressX - x2);
                this.currentProgressX = i4;
                this.lastProgressX = x2;
                if (i4 < i) {
                    this.currentProgressX = i;
                } else if (i4 > measuredWidth) {
                    this.currentProgressX = measuredWidth;
                }
                setProgress((int) (this.duration * 1000 * ((this.currentProgressX - i) / (measuredWidth - i))));
                invalidate();
            }
            super.onTouchEvent(motionEvent);
            return true;
        }

        @Override
        protected void onDraw(android.graphics.Canvas r16) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.WebPlayerView.ControlsView.onDraw(android.graphics.Canvas):void");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public WebPlayerView(Context context, boolean z, boolean z2, WebPlayerViewDelegate webPlayerViewDelegate) {
        super(context);
        lastContainerId++;
        this.allowInlineAnimation = Build.VERSION.SDK_INT >= 21;
        this.backgroundPaint = new Paint();
        this.progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (WebPlayerView.this.videoPlayer == null || !WebPlayerView.this.videoPlayer.isPlaying()) {
                    return;
                }
                WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000));
                AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000L);
            }
        };
        this.surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (WebPlayerView.this.changingTextureView) {
                    if (WebPlayerView.this.switchingInlineMode) {
                        WebPlayerView.this.waitingForFirstTextureUpload = 2;
                    }
                    WebPlayerView.this.textureView.setSurfaceTexture(surfaceTexture);
                    WebPlayerView.this.textureView.setVisibility(0);
                    WebPlayerView.this.changingTextureView = false;
                    return false;
                }
                return true;
            }

            public class ViewTreeObserver$OnPreDrawListenerC30241 implements ViewTreeObserver.OnPreDrawListener {
                ViewTreeObserver$OnPreDrawListenerC30241() {
                }

                @Override
                public boolean onPreDraw() {
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (WebPlayerView.this.textureImageView != null) {
                        WebPlayerView.this.textureImageView.setVisibility(4);
                        WebPlayerView.this.textureImageView.setImageDrawable(null);
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            WebPlayerView.this.currentBitmap = null;
                        }
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            WebPlayerView.TextureView$SurfaceTextureListenerC30232.ViewTreeObserver$OnPreDrawListenerC30241.this.lambda$onPreDraw$0();
                        }
                    });
                    WebPlayerView.this.waitingForFirstTextureUpload = 0;
                    return true;
                }

                public void lambda$onPreDraw$0() {
                    WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                }
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                if (WebPlayerView.this.waitingForFirstTextureUpload == 1) {
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver$OnPreDrawListenerC30241());
                    WebPlayerView.this.changedTextureView.invalidate();
                }
            }
        };
        this.switchToInlineRunnable = new Runnable() {
            @Override
            public void run() {
                WebPlayerView.this.switchingInlineMode = false;
                if (WebPlayerView.this.currentBitmap != null) {
                    WebPlayerView.this.currentBitmap.recycle();
                    WebPlayerView.this.currentBitmap = null;
                }
                WebPlayerView.this.changingTextureView = true;
                if (WebPlayerView.this.textureImageView != null) {
                    try {
                        WebPlayerView webPlayerView = WebPlayerView.this;
                        webPlayerView.currentBitmap = Bitmaps.createBitmap(webPlayerView.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                        WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
                    } catch (Throwable th) {
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            WebPlayerView.this.currentBitmap = null;
                        }
                        FileLog.m31e(th);
                    }
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.textureImageView.setVisibility(0);
                        WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
                    } else {
                        WebPlayerView.this.textureImageView.setImageDrawable(null);
                    }
                }
                WebPlayerView.this.isInline = true;
                WebPlayerView.this.updatePlayButton();
                WebPlayerView.this.updateShareButton();
                WebPlayerView.this.updateFullscreenButton();
                WebPlayerView.this.updateInlineButton();
                ViewGroup viewGroup = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(WebPlayerView.this.controlsView);
                }
                WebPlayerView webPlayerView2 = WebPlayerView.this;
                webPlayerView2.changedTextureView = webPlayerView2.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.videoWidth, WebPlayerView.this.videoHeight, WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation);
                WebPlayerView.this.changedTextureView.setVisibility(4);
                ViewGroup viewGroup2 = (ViewGroup) WebPlayerView.this.textureView.getParent();
                if (viewGroup2 != null) {
                    viewGroup2.removeView(WebPlayerView.this.textureView);
                }
                WebPlayerView.this.controlsView.show(false, false);
            }
        };
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(-16777216);
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(context) {
            @Override
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (WebPlayerView.this.textureViewContainer != null) {
                    ViewGroup.LayoutParams layoutParams = WebPlayerView.this.textureView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                    if (WebPlayerView.this.textureImageView != null) {
                        ViewGroup.LayoutParams layoutParams2 = WebPlayerView.this.textureImageView.getLayoutParams();
                        layoutParams2.width = getMeasuredWidth();
                        layoutParams2.height = getMeasuredHeight();
                    }
                }
            }
        };
        this.aspectRatioFrameLayout = aspectRatioFrameLayout;
        addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        WebView webView = new WebView(context);
        this.webView = webView;
        webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() {
            @Override
            public final void jsCallFinished(String str) {
                WebPlayerView.this.lambda$new$0(str);
            }
        }), this.interfaceName);
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        TextureView textureView = new TextureView(context);
        this.textureView = textureView;
        textureView.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (this.allowInlineAnimation && this.textureViewContainer != null) {
            ImageView imageView = new ImageView(context);
            this.textureImageView = imageView;
            imageView.setBackgroundColor(-65536);
            this.textureImageView.setPivotX(0.0f);
            this.textureImageView.setPivotY(0.0f);
            this.textureImageView.setVisibility(4);
            this.textureViewContainer.addView(this.textureImageView);
        }
        VideoPlayer videoPlayer = new VideoPlayer();
        this.videoPlayer = videoPlayer;
        videoPlayer.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        ControlsView controlsView = new ControlsView(context);
        this.controlsView = controlsView;
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 != null) {
            viewGroup2.addView(controlsView);
        } else {
            addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
        }
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        ImageView imageView2 = new ImageView(context);
        this.fullscreenButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$1(view);
            }
        });
        ImageView imageView3 = new ImageView(context);
        this.playButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$2(view);
            }
        });
        if (z) {
            ImageView imageView4 = new ImageView(context);
            this.inlineButton = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$3(view);
                }
            });
        }
        if (z2) {
            ImageView imageView5 = new ImageView(context);
            this.shareButton = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setImageResource(C1072R.C1073drawable.ic_share_video);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$4(view);
                }
            });
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    public void lambda$new$0(String str) {
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask == null || asyncTask.isCancelled()) {
            return;
        }
        AsyncTask asyncTask2 = this.currentTask;
        if (asyncTask2 instanceof YoutubeVideoTask) {
            ((YoutubeVideoTask) asyncTask2).onInterfaceResult(str);
        }
    }

    public void lambda$new$1(View view) {
        if (!this.initied || this.changingTextureView || this.switchingInlineMode || !this.firstFrameRendered) {
            return;
        }
        this.inFullscreen = !this.inFullscreen;
        updateFullscreenState(true);
    }

    public void lambda$new$2(View view) {
        if (!this.initied || this.playVideoUrl == null) {
            return;
        }
        if (!this.videoPlayer.isPlayerPrepared()) {
            preparePlayer();
        }
        if (this.videoPlayer.isPlaying()) {
            this.videoPlayer.pause();
        } else {
            this.isCompleted = false;
            this.videoPlayer.play();
        }
        updatePlayButton();
    }

    public void lambda$new$3(View view) {
        if (this.textureView == null || !this.delegate.checkInlinePermissions() || this.changingTextureView || this.switchingInlineMode || !this.firstFrameRendered) {
            return;
        }
        this.switchingInlineMode = true;
        if (!this.isInline) {
            this.inFullscreen = false;
            this.delegate.prepareToSwitchInlineMode(true, this.switchToInlineRunnable, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
            return;
        }
        ViewGroup viewGroup = (ViewGroup) this.aspectRatioFrameLayout.getParent();
        if (viewGroup != this) {
            if (viewGroup != null) {
                viewGroup.removeView(this.aspectRatioFrameLayout);
            }
            addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
            this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - AndroidUtilities.m35dp(10.0f), 1073741824));
        }
        Bitmap bitmap = this.currentBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.currentBitmap = null;
        }
        this.changingTextureView = true;
        this.isInline = false;
        updatePlayButton();
        updateShareButton();
        updateFullscreenButton();
        updateInlineButton();
        this.textureView.setVisibility(4);
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 != null) {
            viewGroup2.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView);
        }
        ViewGroup viewGroup3 = (ViewGroup) this.controlsView.getParent();
        if (viewGroup3 != this) {
            if (viewGroup3 != null) {
                viewGroup3.removeView(this.controlsView);
            }
            ViewGroup viewGroup4 = this.textureViewContainer;
            if (viewGroup4 != null) {
                viewGroup4.addView(this.controlsView);
            } else {
                addView(this.controlsView, 1);
            }
        }
        this.controlsView.show(false, false);
        this.delegate.prepareToSwitchInlineMode(false, null, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
    }

    public void lambda$new$4(View view) {
        WebPlayerViewDelegate webPlayerViewDelegate = this.delegate;
        if (webPlayerViewDelegate != null) {
            webPlayerViewDelegate.onSharePressed();
        }
    }

    public void onInitFailed() {
        if (this.controlsView.getParent() != this) {
            this.controlsView.setVisibility(8);
        }
        this.delegate.onInitFailed();
    }

    public void updateTextureImageView() {
        if (this.textureImageView == null) {
            return;
        }
        try {
            Bitmap createBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
            this.currentBitmap = createBitmap;
            this.changedTextureView.getBitmap(createBitmap);
        } catch (Throwable th) {
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            FileLog.m31e(th);
        }
        if (this.currentBitmap != null) {
            this.textureImageView.setVisibility(0);
            this.textureImageView.setImageBitmap(this.currentBitmap);
            return;
        }
        this.textureImageView.setImageDrawable(null);
    }

    public String getYoutubeId() {
        return this.currentYoutubeId;
    }

    @Override
    public void onStateChanged(boolean z, int i) {
        if (i != 2) {
            if (this.videoPlayer.getDuration() != -9223372036854775807L) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
        }
        if (i != 4 && i != 1 && this.videoPlayer.isPlaying()) {
            this.delegate.onPlayStateChanged(this, true);
        } else {
            this.delegate.onPlayStateChanged(this, false);
        }
        if (this.videoPlayer.isPlaying() && i != 4) {
            updatePlayButton();
        } else if (i == 4) {
            this.isCompleted = true;
            this.videoPlayer.pause();
            this.videoPlayer.seekTo(0L);
            updatePlayButton();
            this.controlsView.show(true, true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.m35dp(10.0f), this.backgroundPaint);
    }

    @Override
    public void onError(VideoPlayer videoPlayer, Exception exc) {
        FileLog.m31e(exc);
        onInitFailed();
    }

    @Override
    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            if (i3 == 90 || i3 == 270) {
                i2 = i;
                i = i2;
            }
            float f2 = i * f;
            this.videoWidth = (int) f2;
            this.videoHeight = i2;
            float f3 = i2 == 0 ? 1.0f : f2 / i2;
            aspectRatioFrameLayout.setAspectRatio(f3, i3);
            if (this.inFullscreen) {
                this.delegate.onVideoSizeChanged(f3, i3);
            }
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        this.firstFrameRendered = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.controlsView.invalidate();
    }

    @Override
    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        if (this.changingTextureView) {
            this.changingTextureView = false;
            if (this.inFullscreen || this.isInline) {
                if (this.isInline) {
                    this.waitingForFirstTextureUpload = 1;
                }
                this.changedTextureView.setSurfaceTexture(surfaceTexture);
                this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
                this.changedTextureView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (this.waitingForFirstTextureUpload == 2) {
            ImageView imageView = this.textureImageView;
            if (imageView != null) {
                imageView.setVisibility(4);
                this.textureImageView.setImageDrawable(null);
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
            }
            this.switchingInlineMode = false;
            this.delegate.onSwitchInlineMode(this.controlsView, false, this.videoWidth, this.videoHeight, this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
            this.waitingForFirstTextureUpload = 0;
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int measuredWidth = (i5 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int i6 = i4 - i2;
        int m35dp = ((i6 - AndroidUtilities.m35dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        aspectRatioFrameLayout.layout(measuredWidth, m35dp, aspectRatioFrameLayout.getMeasuredWidth() + measuredWidth, this.aspectRatioFrameLayout.getMeasuredHeight() + m35dp);
        if (this.controlsView.getParent() == this) {
            ControlsView controlsView = this.controlsView;
            controlsView.layout(0, 0, controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        int measuredWidth2 = (i5 - this.progressView.getMeasuredWidth()) / 2;
        int measuredHeight = (i6 - this.progressView.getMeasuredHeight()) / 2;
        RadialProgressView radialProgressView = this.progressView;
        radialProgressView.layout(measuredWidth2, measuredHeight, radialProgressView.getMeasuredWidth() + measuredWidth2, this.progressView.getMeasuredHeight() + measuredHeight);
        this.controlsView.imageReceiver.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.m35dp(10.0f));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2 - AndroidUtilities.m35dp(10.0f), 1073741824));
        if (this.controlsView.getParent() == this) {
            this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
        }
        this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m35dp(44.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.m35dp(44.0f), 1073741824));
        setMeasuredDimension(size, size2);
    }

    public void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (!this.videoPlayer.isPlaying()) {
            if (this.isCompleted) {
                this.playButton.setImageResource(this.isInline ? C1072R.C1073drawable.ic_againinline : C1072R.C1073drawable.ic_again);
                return;
            } else {
                this.playButton.setImageResource(this.isInline ? C1072R.C1073drawable.ic_playinline : C1072R.C1073drawable.ic_play);
                return;
            }
        }
        this.playButton.setImageResource(this.isInline ? C1072R.C1073drawable.ic_pauseinline : C1072R.C1073drawable.ic_pause);
        AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
        checkAudioFocus();
    }

    private void checkAudioFocus() {
        if (this.hasAudioFocus) {
            return;
        }
        this.hasAudioFocus = true;
        ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND)).requestAudioFocus(this, 3, 1);
    }

    @Override
    public void onAudioFocusChange(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WebPlayerView.this.lambda$onAudioFocusChange$5(i);
            }
        });
    }

    public void lambda$onAudioFocusChange$5(int i) {
        if (i == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
        } else if (i == 1) {
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (i != -3 && i == -2 && this.videoPlayer.isPlaying()) {
            this.resumeAudioOnFocusGain = true;
            this.videoPlayer.pause();
            updatePlayButton();
        }
    }

    public void updateFullscreenButton() {
        if (!this.videoPlayer.isPlayerPrepared() || this.isInline) {
            this.fullscreenButton.setVisibility(8);
            return;
        }
        this.fullscreenButton.setVisibility(0);
        if (!this.inFullscreen) {
            this.fullscreenButton.setImageResource(C1072R.C1073drawable.ic_gofullscreen);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
            return;
        }
        this.fullscreenButton.setImageResource(C1072R.C1073drawable.ic_outfullscreen);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
    }

    public void updateShareButton() {
        ImageView imageView = this.shareButton;
        if (imageView == null) {
            return;
        }
        imageView.setVisibility((this.isInline || !this.videoPlayer.isPlayerPrepared()) ? 8 : 0);
    }

    private View getControlView() {
        return this.controlsView;
    }

    private View getProgressView() {
        return this.progressView;
    }

    public void updateInlineButton() {
        ImageView imageView = this.inlineButton;
        if (imageView == null) {
            return;
        }
        imageView.setImageResource(this.isInline ? C1072R.C1073drawable.ic_goinline : C1072R.C1073drawable.ic_outinline);
        this.inlineButton.setVisibility(this.videoPlayer.isPlayerPrepared() ? 0 : 8);
        if (this.isInline) {
            this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
        } else {
            this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
        }
    }

    public void preparePlayer() {
        String str = this.playVideoUrl;
        if (str == null) {
            return;
        }
        if (str != null && this.playAudioUrl != null) {
            this.videoPlayer.preparePlayerLoop(Uri.parse(str), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
        } else {
            this.videoPlayer.preparePlayer(Uri.parse(str), this.playVideoType);
        }
        this.videoPlayer.setPlayWhenReady(this.isAutoplay);
        if (this.videoPlayer.getDuration() != -9223372036854775807L) {
            this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
        } else {
            this.controlsView.setDuration(0);
        }
        updateFullscreenButton();
        updateShareButton();
        updateInlineButton();
        this.controlsView.invalidate();
        int i = this.seekToTime;
        if (i != -1) {
            this.videoPlayer.seekTo(i * 1000);
        }
    }

    public void pause() {
        this.videoPlayer.pause();
        updatePlayButton();
        this.controlsView.show(true, true);
    }

    private void updateFullscreenState(boolean z) {
        ViewGroup viewGroup;
        if (this.textureView == null) {
            return;
        }
        updateFullscreenButton();
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 == null) {
            this.changingTextureView = true;
            if (!this.inFullscreen) {
                if (viewGroup2 != null) {
                    viewGroup2.addView(this.textureView);
                } else {
                    this.aspectRatioFrameLayout.addView(this.textureView);
                }
            }
            if (this.inFullscreen) {
                ViewGroup viewGroup3 = (ViewGroup) this.controlsView.getParent();
                if (viewGroup3 != null) {
                    viewGroup3.removeView(this.controlsView);
                }
            } else {
                ViewGroup viewGroup4 = (ViewGroup) this.controlsView.getParent();
                if (viewGroup4 != this) {
                    if (viewGroup4 != null) {
                        viewGroup4.removeView(this.controlsView);
                    }
                    ViewGroup viewGroup5 = this.textureViewContainer;
                    if (viewGroup5 != null) {
                        viewGroup5.addView(this.controlsView);
                    } else {
                        addView(this.controlsView, 1);
                    }
                }
            }
            TextureView onSwitchToFullscreen = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
            this.changedTextureView = onSwitchToFullscreen;
            onSwitchToFullscreen.setVisibility(4);
            if (this.inFullscreen && this.changedTextureView != null && (viewGroup = (ViewGroup) this.textureView.getParent()) != null) {
                viewGroup.removeView(this.textureView);
            }
            this.controlsView.checkNeedHide();
            return;
        }
        if (this.inFullscreen) {
            ViewGroup viewGroup6 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
            if (viewGroup6 != null) {
                viewGroup6.removeView(this.aspectRatioFrameLayout);
            }
        } else {
            ViewGroup viewGroup7 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
            if (viewGroup7 != this) {
                if (viewGroup7 != null) {
                    viewGroup7.removeView(this.aspectRatioFrameLayout);
                }
                addView(this.aspectRatioFrameLayout, 0);
            }
        }
        this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), z);
    }

    public void exitFullscreen() {
        if (this.inFullscreen) {
            this.inFullscreen = false;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInitied() {
        return this.initied;
    }

    public boolean isInline() {
        return this.isInline || this.switchingInlineMode;
    }

    public void enterFullscreen() {
        if (this.inFullscreen) {
            return;
        }
        this.inFullscreen = true;
        updateInlineButton();
        updateFullscreenState(false);
    }

    public boolean isInFullscreen() {
        return this.inFullscreen;
    }

    public static String getYouTubeVideoId(String str) {
        Matcher matcher = youtubeIdRegex.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean canHandleUrl(String str) {
        if (str != null) {
            if (str.endsWith(".mp4")) {
                return true;
            }
            try {
                Matcher matcher = youtubeIdRegex.matcher(str);
                if ((matcher.find() ? matcher.group(1) : null) != null) {
                    return true;
                }
            } catch (Exception e) {
                FileLog.m31e(e);
            }
            try {
                Matcher matcher2 = vimeoIdRegex.matcher(str);
                if ((matcher2.find() ? matcher2.group(3) : null) != null) {
                    return true;
                }
            } catch (Exception e2) {
                FileLog.m31e(e2);
            }
            try {
                Matcher matcher3 = aparatIdRegex.matcher(str);
                if ((matcher3.find() ? matcher3.group(1) : null) != null) {
                    return true;
                }
            } catch (Exception e3) {
                FileLog.m31e(e3);
            }
            try {
                Matcher matcher4 = twitchClipIdRegex.matcher(str);
                if ((matcher4.find() ? matcher4.group(1) : null) != null) {
                    return true;
                }
            } catch (Exception e4) {
                FileLog.m31e(e4);
            }
            try {
                Matcher matcher5 = twitchStreamIdRegex.matcher(str);
                if ((matcher5.find() ? matcher5.group(1) : null) != null) {
                    return true;
                }
            } catch (Exception e5) {
                FileLog.m31e(e5);
            }
            try {
                Matcher matcher6 = coubIdRegex.matcher(str);
                return (matcher6.find() ? matcher6.group(1) : null) != null;
            } catch (Exception e6) {
                FileLog.m31e(e6);
                return false;
            }
        }
        return false;
    }

    public void willHandle() {
        this.controlsView.setVisibility(4);
        this.controlsView.show(false, false);
        showProgress(true, false);
    }

    public boolean loadVideo(java.lang.String r27, org.telegram.tgnet.TLRPC$Photo r28, java.lang.Object r29, java.lang.String r30, boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.WebPlayerView.loadVideo(java.lang.String, org.telegram.tgnet.TLRPC$Photo, java.lang.Object, java.lang.String, boolean):boolean");
    }

    public String getCoubId(String str) {
        String group;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            Matcher matcher = coubIdRegex.matcher(str);
            group = matcher.find() ? matcher.group(1) : null;
        } catch (Exception e) {
            FileLog.m31e(e);
        }
        if (group != null) {
            return group;
        }
        return null;
    }

    public View getAspectRatioView() {
        return this.aspectRatioFrameLayout;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public ImageView getTextureImageView() {
        return this.textureImageView;
    }

    public View getControlsView() {
        return this.controlsView;
    }

    public void destroy() {
        this.videoPlayer.releasePlayer(false);
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    public void showProgress(boolean z, boolean z2) {
        if (z2) {
            AnimatorSet animatorSet = this.progressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.progressAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, "alpha", fArr);
            animatorSet2.playTogether(animatorArr);
            this.progressAnimation.setDuration(150L);
            this.progressAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    WebPlayerView.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        this.progressView.setAlpha(z ? 1.0f : 0.0f);
    }
}
