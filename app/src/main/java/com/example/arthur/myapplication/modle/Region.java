package com.example.arthur.myapplication.modle;

/**
 * Created by Administrator on 2016/7/3.
 */
public class Region {

    private String name;
    private String code;
    private String superCode;

    public String getSuperCode() {
        return superCode;
    }

    public void setSuperCode(String superCode) {
        this.superCode = superCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

}
