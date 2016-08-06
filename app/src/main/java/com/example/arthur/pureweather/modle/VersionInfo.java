package com.example.arthur.pureweather.modle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/6.
 */
public class VersionInfo implements Serializable{
    @SerializedName("name")
    public String name;

    //注意：version对应的是app的versionCode，用于开发者版本的辨识。此处类型为String，在app.gradle中为int，需转换类型再比较
    @SerializedName("version")
    public String version;

    @SerializedName("changelog")
    public String changelog;
    @SerializedName("versionShort")
    public String versionShort;
    @SerializedName("build")
    public String build;
    @SerializedName("installUrl")
    public String installUrl;
    @SerializedName("install_url")
    public String install_url;
    @SerializedName("update_url")
    public String updateUrl;
    @SerializedName("binary")
    public binaryInfo binary;

    public static class binaryInfo{
        @SerializedName("fsize")
        public String fsize;
    }

}
