package org.telegram.p009ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Cells.BaseCell;
import org.telegram.p009ui.Components.SeekBar;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;

public class PopupAudioView extends BaseCell implements SeekBar.SeekBarDelegate, DownloadController.FileDownloadProgressListener {
    private int TAG;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private int currentAccount;
    protected MessageObject currentMessageObject;
    private String lastTimeString;
    private ProgressView progressView;
    private SeekBar seekBar;
    private int seekBarX;
    private int seekBarY;
    private StaticLayout timeLayout;
    private TextPaint timePaint;
    int timeWidth;
    private int timeX;
    private boolean wasLayout;

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSeekBarContinuousDrag(float f) {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
    }

    @Override
    public void onSeekBarPressed() {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarPressed(this);
    }

    @Override
    public void onSeekBarReleased() {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarReleased(this);
    }

    public PopupAudioView(Context context) {
        super(context);
        this.wasLayout = false;
        this.buttonState = 0;
        this.buttonPressed = 0;
        this.timeWidth = 0;
        this.lastTimeString = null;
        TextPaint textPaint = new TextPaint(1);
        this.timePaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.m36dp(16.0f));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        SeekBar seekBar = new SeekBar(this);
        this.seekBar = seekBar;
        seekBar.setDelegate(this);
        this.progressView = new ProgressView();
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject) {
            this.currentAccount = messageObject.currentAccount;
            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
            this.progressView.setProgressColors(-2497813, -7944712);
            this.currentMessageObject = messageObject;
            this.wasLayout = false;
            requestLayout();
        }
        updateButtonState();
    }

    public final MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.m36dp(56.0f));
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentMessageObject == null) {
            return;
        }
        this.seekBarX = AndroidUtilities.m36dp(54.0f);
        this.buttonX = AndroidUtilities.m36dp(10.0f);
        this.timeX = (getMeasuredWidth() - this.timeWidth) - AndroidUtilities.m36dp(16.0f);
        this.seekBar.setSize((getMeasuredWidth() - AndroidUtilities.m36dp(70.0f)) - this.timeWidth, AndroidUtilities.m36dp(30.0f));
        this.progressView.width = (getMeasuredWidth() - AndroidUtilities.m36dp(94.0f)) - this.timeWidth;
        this.progressView.height = AndroidUtilities.m36dp(30.0f);
        this.seekBarY = AndroidUtilities.m36dp(13.0f);
        this.buttonY = AndroidUtilities.m36dp(10.0f);
        updateProgress();
        if (z || !this.wasLayout) {
            this.wasLayout = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i;
        int i2;
        if (this.currentMessageObject == null) {
            return;
        }
        if (!this.wasLayout) {
            requestLayout();
            return;
        }
        Point point = AndroidUtilities.displaySize;
        int i3 = point.y;
        int i4 = point.x;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            int measuredWidth = view.getMeasuredWidth();
            i2 = view.getMeasuredHeight();
            i = measuredWidth;
        } else {
            i = i4;
            i2 = i3;
        }
        Theme.chat_msgInMediaDrawable.setTop((int) getY(), i, i2, false, false);
        BaseCell.setDrawableBounds(Theme.chat_msgInMediaDrawable, 0, 0, getMeasuredWidth(), getMeasuredHeight());
        Theme.chat_msgInMediaDrawable.draw(canvas);
        if (this.currentMessageObject == null) {
            return;
        }
        canvas.save();
        int i5 = this.buttonState;
        if (i5 == 0 || i5 == 1) {
            canvas.translate(this.seekBarX, this.seekBarY);
            this.seekBar.draw(canvas);
        } else {
            canvas.translate(this.seekBarX + AndroidUtilities.m36dp(12.0f), this.seekBarY);
            this.progressView.draw(canvas);
        }
        canvas.restore();
        this.timePaint.setColor(-6182221);
        Drawable drawable = Theme.chat_fileStatesDrawable[this.buttonState + 5][this.buttonPressed];
        int m36dp = AndroidUtilities.m36dp(36.0f);
        BaseCell.setDrawableBounds(drawable, ((m36dp - drawable.getIntrinsicWidth()) / 2) + this.buttonX, ((m36dp - drawable.getIntrinsicHeight()) / 2) + this.buttonY);
        drawable.draw(canvas);
        canvas.save();
        canvas.translate(this.timeX, AndroidUtilities.m36dp(18.0f));
        this.timeLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.PopupAudioView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void didPressedButton() {
        int i = this.buttonState;
        if (i == 0) {
            boolean playMessage = MediaController.getInstance().playMessage(this.currentMessageObject);
            if (!this.currentMessageObject.isOut() && this.currentMessageObject.isContentUnread() && this.currentMessageObject.messageOwner.peer_id.channel_id == 0) {
                MessagesController.getInstance(this.currentAccount).markMessageContentAsRead(this.currentMessageObject);
                this.currentMessageObject.setContentIsRead();
            }
            if (playMessage) {
                this.buttonState = 1;
                invalidate();
            }
        } else if (i == 1) {
            if (MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                this.buttonState = 0;
                invalidate();
            }
        } else if (i == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            invalidate();
        } else if (i == 3) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            invalidate();
        }
    }

    public void updateProgress() {
        int i;
        if (this.currentMessageObject == null) {
            return;
        }
        if (!this.seekBar.isDragging()) {
            this.seekBar.setProgress(this.currentMessageObject.audioProgress);
        }
        if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            i = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= this.currentMessageObject.getDocument().attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.currentMessageObject.getDocument().attributes.get(i2);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    i = tLRPC$DocumentAttribute.duration;
                    break;
                }
                i2++;
            }
        } else {
            i = this.currentMessageObject.audioProgressSec;
        }
        String formatLongDuration = AndroidUtilities.formatLongDuration(i);
        String str = this.lastTimeString;
        if (str == null || (str != null && !str.equals(formatLongDuration))) {
            this.timeWidth = (int) Math.ceil(this.timePaint.measureText(formatLongDuration));
            this.timeLayout = new StaticLayout(formatLongDuration, this.timePaint, this.timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        invalidate();
    }

    public void downloadAudioIfNeed() {
        if (this.buttonState == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 3;
            invalidate();
        }
    }

    public void updateButtonState() {
        String fileName = this.currentMessageObject.getFileName();
        if (FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner).exists()) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                this.buttonState = 0;
            } else {
                this.buttonState = 1;
            }
            this.progressView.setProgress(0.0f);
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                this.buttonState = 2;
                this.progressView.setProgress(0.0f);
            } else {
                this.buttonState = 3;
                Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress != null) {
                    this.progressView.setProgress(fileProgress.floatValue());
                } else {
                    this.progressView.setProgress(0.0f);
                }
            }
        }
        updateProgress();
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateButtonState();
    }

    @Override
    public void onSuccessDownload(String str) {
        updateButtonState();
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        this.progressView.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)));
        if (this.buttonState != 3) {
            updateButtonState();
        }
        invalidate();
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    @Override
    public void onSeekBarDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
    }
}
