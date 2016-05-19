package com.loopme.request;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopme.common.Logging;
import com.loopme.common.Utils;
import com.loopme.constants.ConnectionType;

import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AdRequestParametersProvider {

    private static final String LOG_TAG = AdRequestParametersProvider.class.getSimpleName();

    private static AdRequestParametersProvider sProvider;

    private volatile String mAdvertisingId;
    private volatile boolean mDntPresent;

    private String mLoopMeId;

    private String mAppVersion;
    private String mMraid;
    private String mCarrier;
    private boolean mCarrierInited;

    private AdRequestParametersProvider() {
    }

    public static AdRequestParametersProvider getInstance() {
        if (sProvider == null) {
            sProvider = new AdRequestParametersProvider();
        }
        return sProvider;
    }

    public void reset() {
        sProvider = null;
    }

    public void setGoogleAdvertisingId(String advId, boolean isLimited) {
        Logging.out(LOG_TAG, "Advertising Id = " + advId + " Limited: " + isLimited);
        mAdvertisingId = advId;
        mDntPresent = isLimited;
    }

    public String getGoogleAdvertisingId() {
        return mAdvertisingId;
    }

    public int getConnectionType(Context context) {
        if (context == null) {
            return ConnectionType.UNKNOWN;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return ConnectionType.UNKNOWN;
        }

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return ConnectionType.UNKNOWN;
        }

        int type = ni.getType();

        if (type == ConnectivityManager.TYPE_WIFI) {
            return ConnectionType.WIFI;
        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
            return ConnectionType.ETHERNET;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return ConnectionType.UNKNOWN;
            }

            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return ConnectionType.MOBILE_2G;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return ConnectionType.MOBILE_3G;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return ConnectionType.MOBILE_4G;

                default:
                    return ConnectionType.MOBILE_UNKNOWN_GENERATION;
            }
        } else {
            return ConnectionType.UNKNOWN;
        }
    }

    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getAppVersion(Context context) {
        if (mAppVersion == null) {
            initAppVersion(context);
        }
        return mAppVersion;
    }

    private void initAppVersion(Context context) {
        if (context == null) {
            mAppVersion = "0.0";
            return;
        }
        try {
            mAppVersion = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Logging.out(LOG_TAG, "Can't get app version. Exception: " + e.getMessage());
            mAppVersion = "0.0";
        }
    }

    public String getMraidSupport() {
        if (mMraid == null) {
            initMraidSupport();
        }
        return mMraid;
    }

    private void initMraidSupport() {
        mMraid = "1";
        try {
            Class.forName("com.loopme.MraidView");
        } catch (ClassNotFoundException e) {
            mMraid = "0";
        }
    }

    public String getOrientation(Context context) {
        if (context == null) {
            return "";
        }
        int orientation = context.getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            return "l";
        } else {
            return "p";
        }
    }

    public String getViewerToken() {
        String advId = mAdvertisingId;
        if (TextUtils.isEmpty(advId)) {
            if (mLoopMeId == null) {
                String loopmeId = Long.toHexString(Double.doubleToLongBits(Math.random()));
                Logging.out(LOG_TAG, "LoopMe Id = " + loopmeId);
                mLoopMeId = loopmeId;
            }
            return mLoopMeId;
        } else {
            return advId;
        }
    }

    public String getLatitude() {
        Location location = Utils.getLastKnownLocation();
        if (location != null) {
            return String.valueOf(location.getLatitude());
        } else {
            return null;
        }
    }

    public String getLongitude() {
        Location location = Utils.getLastKnownLocation();
        if (location != null) {
            return String.valueOf(location.getLongitude());
        } else {
            return null;
        }
    }

    public String getCarrier(Context context) {
        if (!mCarrierInited) {
            initCarrier(context);
        }
        return mCarrier;
    }

    private void initCarrier(Context context) {
        if (context == null) {
            mCarrierInited = true;
            return;
        }
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null) {
            mCarrierInited = true;
            return;
        }
        mCarrier = telephonyManager.getNetworkOperator();
        if (mCarrier.isEmpty()) {
            mCarrier = null;
        }
        mCarrierInited = true;
    }

    public boolean isDntPresent() {
        return mDntPresent;
    }

    public boolean isWifiInfoAvailable(Context context) {
        String permission = "android.permission.ACCESS_WIFI_STATE";
        int res = context.checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;
    }

    public String getWifiName(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {
                return null;
            }

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String ssid = wifiInfo.getSSID();

            // remove extra quotes if needed
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }


            if (TextUtils.isEmpty(ssid)) {
                return null;
            }

            if (ssid.contains("unknown ssid")) {
                return null;
            }

            if (ssid.equals("0x")) {
                return null;
            }

            return ssid;
        } catch (Exception e) {
            return null;
        }
    }

    public String getDeviceId(Context context) {
        String imei = null;
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                int permissionCheck = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_PHONE_STATE);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    imei = telephonyManager.getDeviceId();
                }
            }
        }
        return imei;
    }

    /**
     * @return result[0] - charge level: 0..1
     * result[1] - plugged type: USB,AC,WL,CHRG,NCHRG
     */
    public String[] getBatteryInfo(final Context context) {

        final Object monitor = new Object();

        final String[] result = new String[2];

        final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                synchronized (monitor) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level / (float) scale;
                    result[0] = String.valueOf(batteryPct);

                    int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    switch (status) {
                        case 0:
                            result[1] = "NCHRG";
                            break;
                        case BatteryManager.BATTERY_PLUGGED_AC:
                            result[1] = "AC";
                            break;
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            result[1] = "USB";
                            break;
                        case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                            result[1] = "WL";
                            break;
                        default:
                            result[1] = "CHRG";
                    }
                    monitor.notifyAll();
                }
            }
        };

        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryReceiver, batteryLevelFilter);

        // on some devices broadcast does not sent sometimes
        // in this case continue by timeout
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    if (result[0] == null) {
                        result[0] = "-1.0";
                        result[1] = "UNKNOWN";
                    }
                    monitor.notifyAll();
                }
            }
        }, 2, TimeUnit.SECONDS);

        synchronized (monitor) {
            try {
                if (result[0] == null) {
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                // do nothing
            }
            try {
                context.unregisterReceiver(batteryReceiver);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        return result;
    }

}
