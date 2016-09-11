package com.app.infideap.readcontact.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.app.infideap.readcontact.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by InfiDeaP on 24/9/2015.
 */
public class Common {

    static final public String PACKAGE_NAME = "com.app.truevox.diagnosticapp";
    static final public String COPA_RESULT = PACKAGE_NAME.concat(".REQUEST_PROCESSED");
    static final public String COPA_MESSAGE = PACKAGE_NAME.concat(".COPA_MSG");
    static final public String COPA_CODE = PACKAGE_NAME.concat(".COPA_CODE");
    static final public String COPA_CLASS = PACKAGE_NAME.concat(".COPA_CLASS");


    private static final String TAG = Common.class.getSimpleName();
    private static final double IMAGE_SIZE = 720.00;
    private static final String TARGET_FOLDER = "surveyapp/";
    public static final String DATA_ZIP = "data.zip";
    public static final String DB_ZIP = "db.zip";
    private static final String ZIP_PASSWORD = "y=mx+c";
    private static final String ZIP_PASSWORD_FIRST_LAYER = "y=mx+c";
    private static String deviceInfo;

    public static void messageError(final Context context, int code, String message) {
        switch (code) {
            case 700:
                if (context != null)
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                break;
            case 409:
                if (context != null)
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                break;
            case 401:
                if (context != null)
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                break;
            default:
                if (context != null)
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                break;

        }

    }

    public static boolean hasInternetConnection(final Context context) {

        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                return false;
            } else
                return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasInternetConnection(final View view) {

        try {
            final ConnectivityManager connMgr = (ConnectivityManager) view.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo networkInfo = connMgr
                    .getActiveNetworkInfo();


            if (networkInfo != null) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE) {

                    return true;
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        Snackbar.make(view, R.string.noactiveconnection, Snackbar.LENGTH_LONG).show();
        return false;
    }


    public static void gpsAction(Context context, double lat, double lng) {
        Uri sadasd = Uri.parse(String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng));
        showMap(context, sadasd);
    }

    private static void showMap(Context context, Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    public static Spannable parse(TextView textView, String text, int seperator, int color1, int color2) {
        Spannable wordtoSpan = new SpannableStringBuilder(text);
        Context context = textView.getContext();
        wordtoSpan.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(color1)),
                0, seperator, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(color2)),
                seperator, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(wordtoSpan);
        return wordtoSpan;
    }

    public static String getCountryCode(Context context) {
        String countryCodeValue;
        try {

            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            countryCodeValue = tm.getNetworkCountryIso();
        } catch (Exception e) {
            countryCodeValue = context.getResources().getConfiguration().locale.getCountry();
        }

        return countryCodeValue;
    }

    public static String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return getDate(calendar);
    }

    public static String getDate(Calendar calendar) {

        return getDate(calendar.getTime());
    }

    public static String getDate(Date calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.UK);

        return simpleDateFormat.format(calendar);
    }

    public static String getDateForFilename() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return getDateForFilename(calendar);
    }

    public static String getDateForFilename(Calendar calendar) {

        return getDateForFilename(calendar.getTime());
    }

    public static String getDateForFilename(Date calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssS", Locale.UK);

        return simpleDateFormat.format(calendar);
    }

    public static String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return getMonth(calendar);
    }

    public static String getMonth(Calendar calendar) {

        return getMonth(calendar.getTime());
    }

    public static String getMonth(Date calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.UK);

        return simpleDateFormat.format(calendar);
    }


    public static String getDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return getDateString(calendar);
    }

    public static String getDateString(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return getDateString(calendar);
    }


    public static String getDateString(Calendar calendar) {

        return getDateString(calendar.getTime());
    }

    public static String getDateString(Date calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

        return simpleDateFormat.format(calendar);
    }

    public static String getDateStringWithInitTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return getDateStringWithInitTime(calendar);
    }

    public static String getDateStringWithInitTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return getDateStringWithInitTime(calendar);
    }


    public static String getDateStringWithInitTime(Calendar calendar) {

        return getDateStringWithInitTime(calendar.getTime());
    }

    public static String getDateStringWithInitTime(Date calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.UK);

        return simpleDateFormat.format(calendar);
    }

    public static void showKeyBoard(View view) {
        if (view != null) {
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setSelection(editText.length());
            }
            InputMethodManager imm = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideKeyBoard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Spannable hightlightText(Context context, String text, int start, int end) {
        Spannable builder = new SpannableStringBuilder(text);
        builder.setSpan(
                new ForegroundColorSpan(
                        context.getResources().getColor(R.color.colorAccent)
                ),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static String getUniqueID(Context context) {
        return getUniqueID(context, "");
//        return uuid.fromString(String.valueOf(deviceId.hashCode())).toString();
    }


    public static String getUniqueID(Context context, String i) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString() + System.currentTimeMillis() + i;// + '-' + getDate().replace(' ', '-');
//        try {
//            return new String(encrypt(generateKey(), deviceId.getBytes()),"UTF-8");
//        } catch (Exception e) {
//
//            return deviceId;
//        }


        return UUID.nameUUIDFromBytes(deviceId.getBytes()).toString();
//        return uuid.fromString(String.valueOf(deviceId.hashCode())).toString();
    }

    private static byte[] generateKey() throws NoSuchAlgorithmException {
        byte[] keyStart = "ja_wy12sa@ra".getBytes();
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static boolean isEqual(Object obj1, Object obj2) {

        Field[] fields1 = obj1.getClass().getFields();
        Field[] fields2 = obj2.getClass().getFields();
        for (int i = 0; i < fields1.length; i++) {
            try {
                String item1 = fields1[i].get(obj1).toString();
                String item2 = fields2[i].get(obj2).toString();

                if (!item1.equalsIgnoreCase(item2))
                    return false;
            } catch (Exception e) {

            }
        }
        return true;
    }

    public static Date parseDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date parseServerDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.UK);

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date parseUserDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }


    public static String getVersionName(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null)
            return pInfo.versionName;
        else
            return "";
    }


    public static double toMB(long l) {
        return (((double) l) / (double) 1024.0f) / 1024.0f;
    }

    public static void openWebPage(Context context, String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static String getUserDateString(String dateString) {

        Date date = parseDate(dateString);

        return getUserDateString(date);

    }

    public static String getUserDateString() {

        Date date = Calendar.getInstance().getTime();

        return getUserDateString(date);

    }

    public static String getUserDateString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        return format.format(date);
    }

    public static String getUserDateWithTimeString(String dateString) {

        Date date = parseDate(dateString);

        return getUserDateWithTimeString(date);

    }

    public static String getUserDateWithTimeString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyy  hh:mm:ss a", Locale.UK);

        return format.format(date);
    }

//    public static int getNetworkClass(Context context) {
//        TelephonyManager mTelephonyManager = (TelephonyManager)
//                context.getSystemService(Context.TELEPHONY_SERVICE);
//        int networkType = mTelephonyManager.getNetworkType();
//        switch (networkType) {
//            case TelephonyManager.NETWORK_TYPE_GPRS:
//            case TelephonyManager.NETWORK_TYPE_EDGE:
//            case TelephonyManager.NETWORK_TYPE_CDMA:
//            case TelephonyManager.NETWORK_TYPE_1xRTT:
//            case TelephonyManager.NETWORK_TYPE_IDEN:
//                return Constant.NETWORK_2G;
//            case TelephonyManager.NETWORK_TYPE_UMTS:
//            case TelephonyManager.NETWORK_TYPE_EVDO_0:
//            case TelephonyManager.NETWORK_TYPE_EVDO_A:
//            case TelephonyManager.NETWORK_TYPE_HSDPA:
//            case TelephonyManager.NETWORK_TYPE_HSUPA:
//            case TelephonyManager.NETWORK_TYPE_HSPA:
//            case TelephonyManager.NETWORK_TYPE_EVDO_B:
//            case TelephonyManager.NETWORK_TYPE_EHRPD:
//            case TelephonyManager.NETWORK_TYPE_HSPAP:
//                return Constant.NETWORK_3G;
//            case TelephonyManager.NETWORK_TYPE_LTE:
//                return Constant.NETWORK_4G;
//            default:
//                return Constant.NETWORK_UNKNOWN;
//        }
//    }


    private static boolean isFileExists(String filename) {

        File folder1 = new File(filename);
        return folder1.exists();


    }

    private static boolean deleteFile(String filename) {

        File folder1 = new File(filename);
        return folder1.delete();


    }

    public static long download(DownloadManager dm, String request, String path, String name) {
        try {
            //URLEncoder.encode(request, "UTF-8")
            Uri uri = Uri.parse(request);

            DownloadManager.Request r = new DownloadManager.Request(uri);

            // This put the download in the same Download dir the browser uses
            r.setDestinationInExternalPublicDir(path, name);

            // When downloading music and videos they will be listed in the player
            // (Seems to be available since Honeycomb only)
            r.allowScanningByMediaScanner();

            // Notify user when download is completed
            // (Seems to be available since Honeycomb only)
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Start download
            return dm.enqueue(r);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void upgrade(final Context context, String request, final String folder, final OnUpgradeListener listener) {


        final String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath().concat(folder);

        String[] url = request.split("/");
        final String appname = url[url.length - 1];// + ".apk";

        if (!isFileExists(path)) {
            File file = new File(path);
            file.mkdir();
        }

        if (isFileExists(path + appname))
            deleteFile(path + appname);

        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        final long enqueue = download(dm, request, folder, appname);

        if (enqueue == -1)
            return;

        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                // your code
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {
                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            if (listener != null)
                                listener.onSuccess();
                            if (isFileExists(path + appname)) {

                                File file = new File(path + appname);

                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                                context.startActivity(intent2);
                            }
                        } else {
                            listener.onFailed();
                        }
                    }
                }
            }

        };
//        context.unregisterReceiver(onComplete);
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public static void sendResult(Context context, int code, int message, Class<?> aClass) {
        sendResult(context, code, context.getResources().getString(message), aClass);
    }

    public static void sendResult(Context context, int code, String message, Class<?> aClass) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Common.COPA_RESULT);


        intent.putExtra(Common.COPA_MESSAGE, message);
        intent.putExtra(Common.COPA_CODE, code);
        if (aClass != null)
            intent.putExtra(Common.COPA_CLASS, aClass.getName());
        broadcaster.sendBroadcast(intent);
    }

    public static Bitmap readImage(String url, String name) {
        File sdCard = Environment.getExternalStorageDirectory();

        File directory = new File(sdCard.getAbsolutePath() + url);

        File file = new File(directory, name); //or any other format supported

        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the file

        try {
            streamIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap resize(Bitmap bitmap) {
        double scale = IMAGE_SIZE / bitmap.getWidth();

        if (bitmap.getWidth() < bitmap.getHeight())
            scale = IMAGE_SIZE / bitmap.getHeight();

        return Bitmap.createScaledBitmap(bitmap,
                (int) (bitmap.getWidth() * scale),
                (int) (bitmap.getHeight() * scale), true);
    }


    public static String formatIcNumber(String icNumber) {
        String format;
        if (icNumber.length() > 9) {
            format = icNumber.substring(0, 6) + "-" + icNumber.substring(6, 8) + "-" + icNumber.substring(8);
        } else if (icNumber.length() > 7) {
            format = icNumber.substring(0, 6) + "-" + icNumber.substring(6);
        } else
            format = icNumber;

        return format;
    }


    public static int dpValue(Context context, int value) {
        Resources r = context.getResources();
        return (int)
                Math.round(TypedValue.
                        applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics()));
    }

    public static String getDeviceInfo() {
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String product = Build.PRODUCT;
        String model = Build.MODEL;
        String serial = Build.SERIAL;

        return " Manufacturer:" + capitalize(manufacturer)
                + "\n Brand: " + capitalize(brand)
                + "\n Product: " + capitalize(product)
                + "\n Serial: " + capitalize(serial)
                + "\n Model: " + capitalize(model);
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static void setLocale(Context context, String code) {
        Locale locale;


        if (code != null)
            locale = new Locale(code);
        else
            locale = Locale.getDefault();

        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else
            configuration.locale = locale;

        context.getResources().updateConfiguration(
                configuration,
                context.getResources().getDisplayMetrics()
        );
    }

    public static String getDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEEEEEE", Locale.getDefault());

//        return simpleDateFormat.format(date);

        return String.format(
                Locale.getDefault(),
                "%tA", date
        );
    }

    public static String getSimSerialNumber(Context context) {

        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return tMgr.getSimSerialNumber();
    }

    public static String readAsset(Context context, String fileName) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                builder.append(mLine).append("\n");
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return builder.toString();
    }

    public static Object readAssetAsPOJO(Context context, String fileName, Class<?> c) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson=gsonBuilder.create();
        return gson.fromJson(readAsset(context, fileName), c);
    }

    public interface OnUpgradeListener {
        public void onSuccess();

        public void onFailed();
    }


}

