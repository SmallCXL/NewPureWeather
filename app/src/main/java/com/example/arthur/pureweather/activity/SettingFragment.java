package com.example.arthur.pureweather.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.utils.CheckVersion;
import com.example.arthur.pureweather.utils.PreferenceUtils;

/**
 * Created by Administrator on 2016/8/8.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference checkForUpdate;
    private Preference updateInterval;
//    private Preference chooseTheme;
    private Preference showNotification;
    private Preference hasLabel;
    private PreferenceUtils mPreferenceUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_fragment);
        mPreferenceUtils = PreferenceUtils.getInstance(getActivity());

        checkForUpdate = findPreference(Constants.CHECK_FOR_UPDATE);
        checkForUpdate.setSummary("当前版本：" + CheckVersion.getVersionName(getActivity()));
        checkForUpdate.setOnPreferenceClickListener(this);

        updateInterval = findPreference(Constants.UPDATE_INTERVAL);
        String intervalText = mPreferenceUtils.getIntervalText(Constants.UPDATE_INTERVAL, 0);
        updateInterval.setSummary(intervalText);
        updateInterval.setOnPreferenceClickListener(this);

//        chooseTheme = findPreference(Constants.THEME_COLOR);
//        chooseTheme.setOnPreferenceClickListener(this);

        showNotification = findPreference(Constants.SHOW_NOTIFICATION);
        showNotification.setOnPreferenceClickListener(this);

        hasLabel = findPreference(Constants.HAS_LABEL);
        hasLabel.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case Constants.CHECK_FOR_UPDATE:
                CheckVersion.manualCheck(getActivity());
                checkForUpdate.setSummary("当前版本：" + CheckVersion.getVersionName(getActivity()));
                break;
            case Constants.UPDATE_INTERVAL:
                showProgressDialog();
                break;
//            case Constants.THEME_COLOR:
//                showThemeDialog();
//                break;
        }
        return false;
    }

    public void showProgressDialog(){
        LayoutInflater layoutInflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View dialogLayout = layoutInflater.
                inflate(R.layout.dialog_update_interval, ((ViewGroup) getActivity().findViewById(R.id.update_dialog_root)), false);
        final AlertDialog updateDialog = new AlertDialog.Builder(getActivity()).setView(dialogLayout).create();
        SeekBar seekBar = ((SeekBar) dialogLayout.findViewById(R.id.update_dialog_seek_bar));
        TextView interval = ((TextView) dialogLayout.findViewById(R.id.update_dialog_content));
        Button check = ((Button) dialogLayout.findViewById(R.id.update_dialog_btn));
        seekBar.setProgress(mPreferenceUtils.getInt(Constants.UPDATE_INTERVAL, 0));

        interval.setText(new StringBuilder().append("更新间隔：").append(seekBar.getProgress()).append("小时").toString());
        updateDialog.show();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interval.setText(new StringBuilder().append("更新间隔：").append(progress).append("小时").toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        check.setOnClickListener(v -> {
            mPreferenceUtils.putInt(Constants.UPDATE_INTERVAL, seekBar.getProgress());
            String text = mPreferenceUtils.getIntervalText(Constants.UPDATE_INTERVAL, 0);
            updateInterval.setSummary(text);
            updateDialog.dismiss();
        });
    }
    public void showThemeDialog(){
        LayoutInflater layoutInflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View dialogLayout = layoutInflater
                .inflate(R.layout.dialog_theme, ((ViewGroup) getActivity().findViewById(R.id.theme_dialog_root)),false);
        final AlertDialog themeDialog = new AlertDialog.Builder(getActivity()).setView(dialogLayout).create();
        themeDialog.show();
    }
}
