package org.telegram.p009ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C1072R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.p009ui.ActionBar.BaseFragment;
import org.telegram.p009ui.ActionBar.BottomSheet;
import org.telegram.p009ui.PhotoAlbumPickerActivity;

public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private BaseFragment parentFragment;

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap, boolean z);

        void needOpenColorPicker();
    }

    public void cleanup() {
    }

    public WallpaperUpdater(Activity activity, BaseFragment baseFragment, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.parentFragment = baseFragment;
        this.delegate = wallpaperUpdaterDelegate;
    }

    public void showAlert(final boolean z) {
        CharSequence[] charSequenceArr;
        int[] iArr;
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", C1072R.string.ChoosePhoto), true);
        if (z) {
            charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", C1072R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", C1072R.string.SelectFromGallery), LocaleController.getString("SelectColor", C1072R.string.SelectColor), LocaleController.getString("Default", C1072R.string.Default)};
            iArr = null;
        } else {
            charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", C1072R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", C1072R.string.SelectFromGallery)};
            iArr = new int[]{C1072R.C1073drawable.msg_camera, C1072R.C1073drawable.msg_photos};
        }
        builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                WallpaperUpdater.this.lambda$showAlert$0(z, dialogInterface, i);
            }
        });
        builder.show();
    }

    public void lambda$showAlert$0(boolean z, DialogInterface dialogInterface, int i) {
        try {
            if (i != 0) {
                if (i == 1) {
                    openGallery();
                    return;
                } else if (z) {
                    if (i == 2) {
                        this.delegate.needOpenColorPicker();
                        return;
                    } else if (i == 3) {
                        this.delegate.didSelectWallpaper(null, null, false);
                        return;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File generatePicturePath = AndroidUtilities.generatePicturePath();
                if (generatePicturePath != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        Activity activity = this.parentActivity;
                        intent.putExtra("output", FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                        intent.addFlags(2);
                        intent.addFlags(1);
                    } else {
                        intent.putExtra("output", Uri.fromFile(generatePicturePath));
                    }
                    this.currentPicturePath = generatePicturePath.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(intent, 10);
            } catch (Exception e) {
                FileLog.m31e(e);
            }
        } catch (Exception e2) {
            FileLog.m31e(e2);
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (Build.VERSION.SDK_INT >= 23 && baseFragment.getParentActivity() != null && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                return;
            }
            PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER, false, false, null);
            photoAlbumPickerActivity.setAllowSearchImages(false);
            photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                @Override
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                    WallpaperUpdater.this.didSelectPhotos(arrayList);
                }

                @Override
                public void startPhotoSelectActivity() {
                    try {
                        Intent intent = new Intent("android.intent.action.PICK");
                        intent.setType("image/*");
                        WallpaperUpdater.this.parentActivity.startActivityForResult(intent, 11);
                    } catch (Exception e) {
                        FileLog.m31e(e);
                    }
                }
            });
            this.parentFragment.presentFragment(photoAlbumPickerActivity);
            return;
        }
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        this.parentActivity.startActivityForResult(intent, 11);
    }

    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList) {
        try {
            if (arrayList.isEmpty()) {
                return;
            }
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(0);
            if (sendingMediaInfo.path != null) {
                File directory = FileLoader.getDirectory(4);
                this.currentWallpaperPath = new File(directory, Utilities.random.nextInt() + ".jpg");
                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
                loadBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                this.delegate.didSelectWallpaper(this.currentWallpaperPath, loadBitmap, true);
            }
        } catch (Throwable th) {
            FileLog.m31e(th);
        }
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String str) {
        this.currentPicturePath = str;
    }

    public void onActivityResult(int r8, int r9, android.content.Intent r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.WallpaperUpdater.onActivityResult(int, int, android.content.Intent):void");
    }
}
