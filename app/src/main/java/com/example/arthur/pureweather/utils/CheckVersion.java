package com.example.arthur.pureweather.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.arthur.pureweather.constant.MyString;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.VersionInfo;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CheckVersion {
    static String localVersion;
    static String latestVersion;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences pref;

    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "获取版本号失败", Toast.LENGTH_SHORT).show();
            versionCode = -1;
        }
        return versionCode;
    }

    public static void manualCheck(Context context) {
        localVersion = String.valueOf(getVersionCode(context));
        NetworkRequest
                .getNewVersion()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable ->
                        Toast.makeText(context, "获取新版本信息失败，请重试！", Toast.LENGTH_SHORT).show())
                .subscribe(versionInfo -> {
                    latestVersion = versionInfo.version;
                    if (latestVersion.compareTo(localVersion) > 0) {
                        showUpdateDialog(context, versionInfo, MyString.MANUAL_CHECK);
                    } else {
                        Toast.makeText(context, "已经是最新版本~", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void autoCheck(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isIgnore = pref.getBoolean(MyString.IGNORE_UPDATE, false);

        if (!isIgnore) {
            localVersion = String.valueOf(getVersionCode(context));
            NetworkRequest
                    .getNewVersion()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable ->
                            Toast.makeText(context, "获取新版本信息失败，请重试！", Toast.LENGTH_SHORT).show())
                    .subscribe(versionInfo -> {
                        latestVersion = versionInfo.version;
                        if (latestVersion.compareTo(localVersion) > 0) {
                            showUpdateDialog(context, versionInfo, MyString.AUTO_CHECK);
                        }
                    });
        }
    }

    public static void showUpdateDialog(final Context context, VersionInfo versionInfo, final String checkType) {

        String negativeText;
        boolean cancelable;
        switch (checkType) {
            case MyString.AUTO_CHECK:
                negativeText = "稍候再说";
                cancelable = false;
                break;
            case MyString.MANUAL_CHECK:
            default:
                negativeText = "取消";
                cancelable = true;
        }
        String title = "有新的版本哦~";
        String body = versionInfo.changelog;
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        new AlertDialog.Builder(context).setTitle(title)
                .setMessage(body)
                .setPositiveButton("下载", (dialog, which) -> {
                    editor.putBoolean(MyString.IGNORE_UPDATE, false);
                    editor.commit();
                    Uri uri = Uri.parse(versionInfo.updateUrl);   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    context.startActivity(intent);        //启动Activity
                })
                .setNegativeButton(negativeText, (dialog, which) -> {
                    if (checkType.equals(MyString.AUTO_CHECK)) {
                        //忽略自动更新
                        editor.putBoolean(MyString.IGNORE_UPDATE, true);
                        editor.commit();
                    }
                })
                .setCancelable(cancelable)
                .show();
    }

}
