package org.telegram.ui.Stories.recorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import j$.util.DesugarTimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_BotResults;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.recorder.Weather;
public class Weather {
    public static int LOCATION_REQUEST = 3332;
    private static String cacheKey;
    private static State cacheValue;
    private static Utilities.Callback<Boolean> latestPermissionCallback;

    public static boolean isDefaultCelsius() {
        String id = TimeZone.getDefault().getID();
        return (id.startsWith("US/") || "America/Nassau".equals(id) || "America/Belize".equals(id) || "America/Cayman".equals(id) || "Pacific/Palau".equals(id)) ? false : true;
    }

    public static class State extends TLObject {
        public String emoji;
        public double lat;
        public double lng;
        public float temperature;

        public String getEmoji() {
            return this.emoji;
        }

        public String getTemperature() {
            return getTemperature(Weather.isDefaultCelsius());
        }

        public String getTemperature(boolean z) {
            if (z) {
                return Math.round(this.temperature) + "°C";
            }
            StringBuilder sb = new StringBuilder();
            double d = this.temperature;
            Double.isNaN(d);
            sb.append((int) Math.round(((d * 9.0d) / 5.0d) + 32.0d));
            sb.append("°F");
            return sb.toString();
        }

        public static State TLdeserialize(AbstractSerializedData abstractSerializedData) {
            State state = new State();
            state.lat = abstractSerializedData.readDouble(false);
            state.lng = abstractSerializedData.readDouble(false);
            state.emoji = abstractSerializedData.readString(false);
            state.temperature = abstractSerializedData.readFloat(false);
            return state;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeDouble(this.lat);
            abstractSerializedData.writeDouble(this.lng);
            abstractSerializedData.writeString(this.emoji);
            abstractSerializedData.writeFloat(this.temperature);
        }
    }

    public static void fetch(final boolean z, final Utilities.Callback<State> callback) {
        if (callback == null) {
            return;
        }
        getUserLocation(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Weather.lambda$fetch$2(Utilities.Callback.this, z, (Location) obj);
            }
        });
    }

    public static void lambda$fetch$2(final Utilities.Callback callback, final boolean z, Location location) {
        if (location == null) {
            callback.run(null);
            return;
        }
        Activity activity = LaunchActivity.instance;
        if (activity == null) {
            activity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
        }
        if (activity == null || activity.isFinishing()) {
            callback.run(null);
            return;
        }
        final AlertDialog alertDialog = z ? new AlertDialog(activity, 3, new DarkThemeResourceProvider()) : null;
        if (z) {
            alertDialog.showDelayed(200L);
        }
        final Runnable fetch = fetch(location.getLatitude(), location.getLongitude(), new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Weather.lambda$fetch$0(z, alertDialog, callback, (Weather.State) obj);
            }
        });
        if (!z || fetch == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                fetch.run();
            }
        });
    }

    public static void lambda$fetch$0(boolean z, AlertDialog alertDialog, Utilities.Callback callback, State state) {
        if (z) {
            alertDialog.dismissUnless(350L);
        }
        callback.run(state);
    }

    public static State getCached() {
        return cacheValue;
    }

    public static Runnable fetch(final double d, final double d2, final Utilities.Callback<State> callback) {
        Calendar calendar;
        if (callback == null) {
            return null;
        }
        Date date = new Date();
        Calendar.getInstance(DesugarTimeZone.getTimeZone("UTC")).setTime(date);
        final String str = Math.round(d * 1000.0d) + ":" + Math.round(1000.0d * d2) + "at" + (((calendar.getTimeInMillis() / 1000) / 60) / 60);
        if (cacheValue != null && TextUtils.equals(cacheKey, str)) {
            callback.run(cacheValue);
            return null;
        }
        final int[] iArr = new int[1];
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        final ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        String str2 = messagesController.weatherSearchUsername;
        final TLRPC$User[] tLRPC$UserArr = {messagesController.getUser(str2)};
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$5(MessagesController.this, tLRPC$UserArr, d, d2, iArr, connectionsManager, callback, str);
            }
        };
        if (tLRPC$UserArr[0] == null) {
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            tLRPC$TL_contacts_resolveUsername.username = str2;
            iArr[0] = connectionsManager.sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    Weather.lambda$fetch$7(iArr, messagesController, tLRPC$UserArr, runnable, callback, tLObject, tLRPC$TL_error);
                }
            });
        } else {
            runnable.run();
        }
        return new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$8(iArr, connectionsManager);
            }
        };
    }

    public static void lambda$fetch$5(MessagesController messagesController, TLRPC$User[] tLRPC$UserArr, final double d, final double d2, final int[] iArr, ConnectionsManager connectionsManager, final Utilities.Callback callback, final String str) {
        TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
        tLRPC$TL_messages_getInlineBotResults.bot = messagesController.getInputUser(tLRPC$UserArr[0]);
        tLRPC$TL_messages_getInlineBotResults.query = "";
        tLRPC$TL_messages_getInlineBotResults.offset = "";
        tLRPC$TL_messages_getInlineBotResults.flags |= 1;
        TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
        tLRPC$TL_messages_getInlineBotResults.geo_point = tLRPC$TL_inputGeoPoint;
        tLRPC$TL_inputGeoPoint.lat = d;
        tLRPC$TL_inputGeoPoint._long = d2;
        tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
        iArr[0] = connectionsManager.sendRequest(tLRPC$TL_messages_getInlineBotResults, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                Weather.lambda$fetch$4(iArr, callback, d, d2, str, tLObject, tLRPC$TL_error);
            }
        });
    }

    public static void lambda$fetch$4(final int[] iArr, final Utilities.Callback callback, final double d, final double d2, final String str, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$3(iArr, tLObject, callback, d, d2, str);
            }
        });
    }

    public static void lambda$fetch$3(int[] iArr, TLObject tLObject, Utilities.Callback callback, double d, double d2, String str) {
        iArr[0] = 0;
        if (tLObject instanceof TLRPC$messages_BotResults) {
            TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
            if (!tLRPC$messages_BotResults.results.isEmpty()) {
                TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(0);
                String str2 = tLRPC$BotInlineResult.title;
                try {
                    float parseFloat = Float.parseFloat(tLRPC$BotInlineResult.description);
                    State state = new State();
                    state.lat = d;
                    state.lng = d2;
                    state.emoji = str2;
                    state.temperature = parseFloat;
                    cacheKey = str;
                    cacheValue = state;
                    callback.run(state);
                    return;
                } catch (Exception unused) {
                    callback.run(null);
                    return;
                }
            }
        }
        callback.run(null);
    }

    public static void lambda$fetch$7(final int[] iArr, final MessagesController messagesController, final TLRPC$User[] tLRPC$UserArr, final Runnable runnable, final Utilities.Callback callback, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$6(iArr, tLObject, messagesController, tLRPC$UserArr, runnable, callback);
            }
        });
    }

    public static void lambda$fetch$6(int[] iArr, TLObject tLObject, MessagesController messagesController, TLRPC$User[] tLRPC$UserArr, Runnable runnable, Utilities.Callback callback) {
        iArr[0] = 0;
        if (tLObject instanceof TLRPC$TL_contacts_resolvedPeer) {
            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
            messagesController.putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
            messagesController.putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
            tLRPC$UserArr[0] = messagesController.getUser(Long.valueOf(DialogObject.getPeerDialogId(tLRPC$TL_contacts_resolvedPeer.peer)));
            if (tLRPC$UserArr[0] != null) {
                runnable.run();
                return;
            }
        }
        callback.run(null);
    }

    public static void lambda$fetch$8(int[] iArr, ConnectionsManager connectionsManager) {
        if (iArr[0] != 0) {
            connectionsManager.cancelRequest(iArr[0], true);
            iArr[0] = 0;
        }
    }

    public static boolean hasLocationPermission() {
        Activity activity = LaunchActivity.instance;
        if (activity == null) {
            activity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
        }
        if (activity == null) {
            return false;
        }
        return Build.VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0;
    }

    public static void ensureLocationPermission(Utilities.Callback<Boolean> callback) {
        if (callback == null) {
            return;
        }
        final Activity activity = LaunchActivity.instance;
        if (activity == null) {
            activity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
        }
        if (activity == null) {
            return;
        }
        latestPermissionCallback = callback;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                callback.run(Boolean.TRUE);
                return;
            } else if (activity.shouldShowRequestPermissionRationale("android.permission.CAMERA")) {
                new AlertDialog.Builder(activity, null).setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PermissionNoLocationStory))).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        Weather.lambda$ensureLocationPermission$9(activity, dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).create().show();
                callback.run(Boolean.FALSE);
                return;
            } else {
                activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, LOCATION_REQUEST);
                return;
            }
        }
        callback.run(Boolean.TRUE);
    }

    public static void lambda$ensureLocationPermission$9(Activity activity, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void receivePermissionIntent(java.lang.String[] r4, int[] r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.Weather.receivePermissionIntent(java.lang.String[], int[]):void");
    }

    @SuppressLint({"MissingPermission"})
    public static void getUserLocation(final Utilities.Callback<Location> callback) {
        if (callback == null) {
            return;
        }
        ensureLocationPermission(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Weather.lambda$getUserLocation$10(Utilities.Callback.this, (Boolean) obj);
            }
        });
    }

    public static void lambda$getUserLocation$10(Utilities.Callback callback, Boolean bool) {
        Location location = null;
        if (!bool.booleanValue()) {
            callback.run(null);
            return;
        }
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = locationManager.getProviders(true);
        for (int size = providers.size() - 1; size >= 0; size--) {
            location = locationManager.getLastKnownLocation(providers.get(size));
            if (location != null) {
                break;
            }
        }
        callback.run(location);
    }
}
