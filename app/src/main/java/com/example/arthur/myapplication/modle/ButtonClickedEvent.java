package com.example.arthur.myapplication.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/7/24.
 */
public class ButtonClickedEvent {
    private List<String> datas;
    private int color;

    public ButtonClickedEvent(List<String> datas, int color) {
        this.datas = datas;
        this.color = color;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
